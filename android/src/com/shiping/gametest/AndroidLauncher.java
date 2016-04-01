package com.shiping.gametest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.example.games.basegameutils.GameHelper;

import java.util.ArrayList;
import java.util.List;

public class AndroidLauncher extends AndroidApplication implements PlayServices,
		RealTimeMessageReceivedListener, RoomStatusUpdateListener, RoomUpdateListener{

	private GameHelper gameHelper;
	private final static int requestCode=1;
	String mRoomId=null;

	final static String TAG="TechiesWorld";
	private boolean ableToStart=false;

	// arbitrary request code for the waiting room UI.
	// This can be any integer that's unique in your Activity.
	final static int RC_WAITING_ROOM = 10002;

	// The participants in the currently active game
	ArrayList<Participant> mParticipants = null;

	String mMyId=null;

	int positionY=0;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(false);
		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener()
		{
			@Override
			public void onSignInFailed(){}

			@Override
			public void onSignInSucceeded(){}
		};

		gameHelper.setup(gameHelperListener);
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new TechiesWorld(this), config);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_WAITING_ROOM){
			if (resultCode== Activity.RESULT_OK){
				ableToStart=true;
			}else if (resultCode== GamesActivityResultCodes.RESULT_LEFT_ROOM){
				leaveRoom();
			}else if (resultCode==Activity.RESULT_CANCELED){
				leaveRoom();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void signIn()
	{
		try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void signOut()
	{
		try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.signOut();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void rateGame()
	{
		String str = "Your PlayStore Link";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
	}

	@Override
	public void unlockAchievement()
	{
		Games.Achievements.unlock(gameHelper.getApiClient(),
				getString(R.string.achievement_dum_dum));
	}

	@Override
	public void submitScore(int highScore) {
		if (isSignedIn() == true)
		{
			Games.Leaderboards.submitScore(gameHelper.getApiClient(),
					getString(R.string.leaderboard_highest), highScore);
		}
	}

	@Override
	public void showAchievement()
	{
		if (isSignedIn() == true)
		{
			startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
		}
		else
		{
			signIn();
		}
	}

	@Override
	public void showScore()
	{
		if (isSignedIn() == true)
		{
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
					getString(R.string.leaderboard_highest)), requestCode);
		}
		else
		{
			signIn();
		}
	}

	@Override
	public boolean isSignedIn()
	{
		return gameHelper.isSignedIn();
	}


	@Override
	public void startQuickGame(){
		final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
		Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
				MAX_OPPONENTS, 0);
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
		roomConfigBuilder.setMessageReceivedListener(this);
		roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
		RoomConfig roomConfig= roomConfigBuilder.build();

		//create room:
		Games.RealTimeMultiplayer.create(gameHelper.getApiClient(), roomConfig);
	}

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
		byte[] buf=realTimeMessage.getMessageData();
		String sender=realTimeMessage.getSenderParticipantId();
		positionY=(int)buf[0]*100+(int)buf[1];
	}

	public int getPositionY(){
		return positionY;
	}
	// Show error message about game being cancelled and return to main screen.
	void showGameError() {
		BaseGameUtils.makeSimpleDialog(this, "error");
	}

	@Override
	public void onRoomCreated(int statusCode, Room room) {
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			showGameError();
			return;
		}

		// save room ID so we can leave cleanly before the game starts.
		mRoomId = room.getRoomId();

		// show the waiting room UI
		Intent i= Games.RealTimeMultiplayer.getWaitingRoomIntent(gameHelper.getApiClient(), room, Integer.MAX_VALUE);
		startActivityForResult(i, RC_WAITING_ROOM);
	}



	@Override
	public void onJoinedRoom(int statusCode, Room room) {
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			showGameError();
			return;
		}

		// show the waiting room UI
		Intent i= Games.RealTimeMultiplayer.getWaitingRoomIntent(gameHelper.getApiClient(), room, Integer.MAX_VALUE);
		startActivityForResult(i, RC_WAITING_ROOM);
	}

	@Override
	public void onLeftRoom(int i, String s) {

	}

	@Override
	public void onRoomConnected(int statusCode, Room room) {
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			showGameError();
			return;
		}
		mParticipants=room.getParticipants();
		mMyId=room.getParticipantId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));
	}

	public boolean getAbleToStart(){
		return ableToStart;
	}

	@Override
	public void onRoomConnecting(Room room) {
		updateRoom(room);
	}

	@Override
	public void onRoomAutoMatching(Room room) {
		updateRoom(room);
	}

	@Override
	public void onPeerInvitedToRoom(Room room, List<String> list) {
		updateRoom(room);
	}

	@Override
	public void onPeerDeclined(Room room, List<String> list) {
		updateRoom(room);
	}

	@Override
	public void onPeerJoined(Room room, List<String> list) {
		updateRoom(room);
	}

	@Override
	public void onPeerLeft(Room room, List<String> list) {
		updateRoom(room);
	}

	@Override
	public void onConnectedToRoom(Room room) {
		mParticipants=room.getParticipants();
		mMyId=room.getParticipantId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));

		if (mRoomId==null){
			mRoomId=room.getRoomId();
		}

		// print out the list of participants (for debug purposes)
		Log.d(TAG, "Room ID: " + mRoomId);
		Log.d(TAG, "My ID " + mMyId);
		Log.d(TAG, "<< CONNECTED TO ROOM>>");
	}

	@Override
	public void onDisconnectedFromRoom(Room room) {
		updateRoom(room);
	}

	@Override
	public void onPeersConnected(Room room, List<String> list) {
		updateRoom(room);
	}

	@Override
	public void onPeersDisconnected(Room room, List<String> list) {
		updateRoom(room);
	}

	@Override
	public void onP2PConnected(String s) {

	}

	@Override
	public void onP2PDisconnected(String s) {

	}

	void leaveRoom(){
		if (mRoomId!=null){
			Games.RealTimeMultiplayer.leave(gameHelper.getApiClient(),this,mRoomId);
			mRoomId=null;
			ableToStart=false;
		}
	}

	void updateRoom(Room room){
		if (room!=null){
			mParticipants=room.getParticipants();
		}
	}

	public void broadcastMsg(byte[] mMsgBuf){
		if (mParticipants!=null){
			for (Participant p:mParticipants){
				if (!p.getParticipantId().equals(mMyId)){
					Games.RealTimeMultiplayer.sendUnreliableMessage(gameHelper.getApiClient(),mMsgBuf,mRoomId,
							p.getParticipantId());
				}
			}
		}

	}

}
