package com.shiping.gametest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import com.shiping.gametest.Screens.PlayScreen;


import com.shiping.gametest.Sprites.Items.Mine;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
	//ArrayList<Participant> mParticipants = null;
	ArrayList<Participant> mParticipants = new ArrayList<>();

	//variables used to assign playerID (0-3)
	String mMyId=null;
	int mMyIdHashcode=0;
	ArrayList<Integer> playersMyIdHashcode = new ArrayList<>(); //stores hashcode of all player's mMyID
	int playerId = -1; //will be assigned a value of 0-3 later

	Participant me=null;

	int myPosition=-1;

	Map<Integer,int[]> playerPositions=new HashMap<>();

	Map<Integer, String> playerStatus=new HashMap<>();

	public byte[] receivedPlayer;



	//Variables for coins
	Map<Integer,int[]> unspawnedCoinPositions=new HashMap<>();
	int unspawnedIndex = -128; //lowest value of a byte
	int numOfCoinsToSpawn = 0;
	int[] unspawnedGetIndexArray = {-128, -64, -1, 63}; //player0 mines will be from -128 to -65, p1 -64 to -1, p2 0 to 63, p3 64 to 127
	Object numOfCoinsToSpawnLock = new Object();

	//Map<Integer,int[]> collectedCoinPositions=new HashMap<>();
	int collectedIndex = 0;
	int numOfCoinsToRemove = 0;

	@Override
	public int getUnspawnedIndex() {
		return unspawnedIndex;
	} //for coin

	@Override
	public void incrementUnspawnedIndex() {
		unspawnedIndex++; //may need to change increment
	}

	@Override
	public void putMyCoinInHashmap(int player_id, int n, int amount, int index) {
		synchronized (unspawnedCoinPositions){
			int[] coinInfo = {player_id, n, amount, index};
			unspawnedCoinPositions.put(index, coinInfo);
		}
	}

	@Override
	public void putOtherPlayerCoinInHashmap(int player_id, int n, int amount, int index) {
		synchronized (unspawnedCoinPositions){
			int[] coinInfo = {player_id, n, amount, index};
			unspawnedCoinPositions.put(index, coinInfo);
			synchronized (numOfCoinsToSpawnLock){
				numOfCoinsToSpawn++;
				Log.d(TAG, "numOfCoinsToSpawn incremented to: " + numOfCoinsToSpawn);
			}
		}
	}

	public ArrayList<int[]> minePosition=new ArrayList<>();

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(false);
		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener()
		{
			@Override
			public void onSignInFailed(){
				Toast.makeText(getApplicationContext(), "sign in failed", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSignInSucceeded(){
				Toast.makeText(getApplicationContext(), "sign in succeeded", Toast.LENGTH_SHORT).show();
			}
		};

		gameHelper.setup(gameHelperListener);
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new TechiesWorld(this), config);
	}

	@Override
	protected void onStart() {
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_WAITING_ROOM){
			if (resultCode== Activity.RESULT_OK){
				if (mParticipants!=null){
					myPosition=mParticipants.indexOf(me);
					ableToStart=true;
				}

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
				public void run() {
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
		if (isSignedIn()) {
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
		if (isSignedIn())
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
		if (buf[0]=='P'){
			int id=buf[1];
			int[] position=new int[4];
			for (int i=2;i<=5;i++){
				position[i-2]=(int)buf[i];
			}
			playerPositions.put(id, position);
		}else if (buf[0]=='S'){
			int id=buf[1];
			playerStatus.put(id, String.valueOf((char)buf[2]));

		}else if (buf[0]=='c'){ //coin collected by other player , format of msg {'c', index}
			collectedIndex = buf[1]; //index of coin to be removed on this device
			numOfCoinsToRemove++;

		}else if (buf[0]=='C'){ //coin spawned by other player
			receiveCoinSpawnMsg(buf);
			Log.d(TAG, "received coin spawn msg");

		}else if (buf[0]=='t'){ //test
			int val = buf[1];
			Toast.makeText(getApplicationContext(), ""+val, Toast.LENGTH_SHORT).show();

		}else if (buf[0]=='o'){ //determining order of playerID
			synchronized (playersMyIdHashcode){
				byte[] hashCodeArray = {buf[1],buf[2],buf[3],buf[4]}; //last 4 bytes store hashcode
				playersMyIdHashcode.add(ByteBuffer.wrap(hashCodeArray).getInt()); //get int hashcode from other players
			}
			//at this point we haven't added the mMyIDhashcode of this device to playersMyIdHashcode yet hence the +1
			if(playersMyIdHashcode.size()+1==mParticipants.size()){ //add a barrier so that mParticipants has been initialised else it is null
				setPlayerId();
			}
		}else if(buf[0]=='M'){
			int[] mineValue=new int[7];
			for (int i=1;i<=7;i++){
				mineValue[i-1]=(int)buf[i];
			}
			synchronized (minePosition){
				minePosition.add(mineValue);
			}

		}
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
		synchronized (playersMyIdHashcode){
			mParticipants=room.getParticipants();
			mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));
			me=room.getParticipant(mMyId);
			calcMyIdHashCode();

			byte[] initOrderMsg = toBytesInitMsg(mMyIdHashcode);
			broadcastMsg(initOrderMsg);
		}

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
		mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));
		me=room.getParticipant(mMyId);
		if (mRoomId==null){
			mRoomId=room.getRoomId();
		}

		// print out the list of participants (for debug purposes)
		Log.d(TAG, "Room ID: " + mRoomId);
		Log.d(TAG, "My ID " + mMyId);
		Log.d("mMyId", "My ID " + mMyId);
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
			me=room.getParticipant(mMyId);
		}
	}

	public void broadcastMsg(byte[] mMsgBuf){
		if (mParticipants!=null){
			for (Participant p:mParticipants){
				if (!p.getParticipantId().equals(mMyId)){
					Games.RealTimeMultiplayer.sendUnreliableMessage(gameHelper.getApiClient(), mMsgBuf, mRoomId,
							p.getParticipantId());
				}
			}
		}

	}


	public void destroy(){
		this.destroy();
	}

	public int getMyPosition(){
		return myPosition;
	}

	public int[] getPlayerPosition(int id){
		return playerPositions.get(id);
	}

	public String getPlayerStatus(int id){
		return playerStatus.get(id);
	}


	@Override
	public int getPlayerId() { //synced so that playscreen must wait and cannot be instantiated until playerid is ready
		synchronized (playersMyIdHashcode){
			return playerId;
		}
	}

	public void setPlayerId() { //obtain a playerID (0-3) for this device's player
		//largest hashcode ranked at rank0, playerID = 0
		synchronized (playersMyIdHashcode){
			playersMyIdHashcode.add(mMyIdHashcode);
			Collections.sort(playersMyIdHashcode); // Sort the arraylist, last element is the largest
			for(int i=0; i<playersMyIdHashcode.size(); i++){
				//Toast.makeText(getApplicationContext(), "hashcode: "+playersMyIdHashcode.get(playersMyIdHashcode.size() - (1 + i)), Toast.LENGTH_LONG).show();
				if(mMyIdHashcode==playersMyIdHashcode.get(playersMyIdHashcode.size() - (1 + i))){ //gets the last item in first iteration, largest for an ascending sort
					playerId = i;
					//Toast.makeText(getApplicationContext(), "playerId assigned: "+playerId, Toast.LENGTH_LONG).show();
					//Toast.makeText(getApplicationContext(), "playersMy size: "+playersMyIdHashcode.size(), Toast.LENGTH_SHORT).show();
					unspawnedIndex = unspawnedIndex + (i*64); //player0 mines will be from -128 to -65, p1 -64 to -1, p2 0 to 63, p3 64 to 127

				}

			}

		}
	}

	@Override
	public int[] getSpawnedCoinPosition() {
		synchronized (unspawnedCoinPositions){
			for(int i = 0; i < 4; i++){
				if(unspawnedCoinPositions.containsKey(unspawnedGetIndexArray[i]) && playerId!=i) { //need to extract coin index from hashmap
					int[] retVal = unspawnedCoinPositions.get(unspawnedGetIndexArray[i]); //retVal = {playerID, n, amount, index}
					unspawnedGetIndexArray[i]++;
					synchronized (numOfCoinsToSpawnLock) {
						numOfCoinsToSpawn--;
					}
					return retVal;
				}
			}

			return null;

		}

	}

	@Override
	public void decrementCoinsToRemove() {
		numOfCoinsToRemove--;
	}

	@Override
	public int numOfCoinsToRemove() {
		return numOfCoinsToRemove;
	}

	@Override
	public int numOfNewCoinsLeftToSpawn() {
		synchronized (numOfCoinsToSpawnLock){
			//Log.d(TAG, "return numOfCoinsToSpawn: " + numOfCoinsToSpawn);
			return numOfCoinsToSpawn;
		}

	}

	@Override
	public int getCoinToRemoveIndex() {
		return collectedIndex;
	}

	public void receiveCoinSpawnMsg(byte[] mMsgBuf){
		//need to spawn coins created by other players
		// mMsgBuf format is: second byte - x position, third byte - y position, fourth byte is amount
		synchronized (unspawnedCoinPositions){
			int player_Id = (int) mMsgBuf[1]; //playerID or x coin coordinate if coin is spawned by player's death
			//Log.d(TAG, "playerID: " + coinInfo[0]);
			int n = (int) mMsgBuf[2]; //n or y coin coordinate if coin is spawned by player's death
			//Log.d(TAG, "n: " + coinInfo[1]);
			int index = (int) mMsgBuf[3];
			int amount = (int) mMsgBuf[4]; //amount
			//Log.d(TAG, "amount: " + coinInfo[2]);

			putOtherPlayerCoinInHashmap(player_Id, n, amount, index);

			/*synchronized (numOfCoinsToSpawnLock){
				numOfCoinsToSpawn++;
				Log.d(TAG, "numOfCoinsToSpawn incremented to: " + numOfCoinsToSpawn);
			}*/
		}
	}


	public byte[] toBytesInitMsg(int i) {
		byte[] result = new byte[5];

		result[0] = (byte) 'o';
		result[1] = (byte) (i >> 24);
		result[2] = (byte) (i >> 16);
		result[3] = (byte) (i >> 8);
		result[4] = (byte) (i /*>> 0*/);

		return result;
	}

	public void calcMyIdHashCode(){
		mMyIdHashcode = mMyId.hashCode();
	}

	public ArrayList<int[]> getMinePositionAndClear(){
		synchronized (minePosition){
			ArrayList<int[]> tempArray=new ArrayList<>(minePosition);
			minePosition.clear();
			return tempArray;
		}
	}

	public boolean mineIsEmpty(){
		synchronized (minePosition){
			return minePosition.isEmpty();
		}
	}




}
