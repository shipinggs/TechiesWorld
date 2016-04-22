package com.shiping.gametest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class AndroidLauncher extends AndroidApplication implements PlayServices,
		RealTimeMessageReceivedListener, RoomStatusUpdateListener, RoomUpdateListener
		, GoogleApiClient.ConnectionCallbacks,OnInvitationReceivedListener,GoogleApiClient.OnConnectionFailedListener {

	private final static int requestCode = 1;
	//collect the room id
	String mRoomId = null;

	//Tag of the game
	final static String TAG = "TechiesWorld";
	//flag to show wether can switch to gameScreen
	private boolean ableToStart = false;

	// arbitrary request code for the waiting room UI.
	final static int RC_SELECT_PLAYERS = 10000;
	final static int RC_INVITATION_INBOX = 10001;
	final static int RC_WAITING_ROOM = 10002;

	// Request code used to invoke sign in user interactions.
	private static final int RC_SIGN_IN = 9001;

	// Client used to interact with Google APIs.
	private GoogleApiClient mGoogleApiClient;

	// Are we currently resolving a connection failure?
	private boolean mResolvingConnectionFailure = false;

	// Has the user clicked the sign-in button?
	private boolean mSignInClicked = false;

	private boolean mAutoStartSignInFlow = true;

	// The participants in the currently active game
	//ArrayList<Participant> mParticipants = null;
	ArrayList<Participant> mParticipants = new ArrayList<>();

	Map<Integer, Integer> playerScore=new HashMap<>();

	//variables used to assign playerID (0-3)
	String mMyId = null;
	ArrayList<Integer> playersMyIdHashcode = new ArrayList<>(); //stores hashcode of all player's mMyID

	//find the player's participant object
	Participant me = null;

	//unique id for this player, initialize to -1
	int myId = -1;

	//map stored the position of certain player based on the id
	Map<Integer, int[]> playerPositions = new HashMap<>();

	//map stored the status of certain player based on the id
	Map<Integer, String> playerStatus = new HashMap<>();

	//is there any other player press the start game button so that we need to quite the waiting room?
	boolean mWaitingRoomFinishedFromCode = false;

	//set the minimum player numbers
	final static int MIN_PLAYERS = 2;


	//Variables for coins
	Map<Integer, int[]> unspawnedCoinPositions = new HashMap<>();
	int unspawnedIndex = -128; //lowest value of a byte
	AtomicInteger numOfCoinsToSpawn = new AtomicInteger(0);
	int[] unspawnedGetIndexArray = {-128, -64, 0, 64}; //player0 mines will be from -128 to -65, p1 -64 to -1, p2 0 to 63, p3 64 to 127
	ConcurrentLinkedQueue<Integer> collectedIndex = new ConcurrentLinkedQueue<>();
	AtomicInteger numOfCoinsToRemove = new AtomicInteger(0);

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
		int[] coinInfo = {player_id, n, amount, index};
		synchronized (unspawnedCoinPositions){
			unspawnedCoinPositions.put(index, coinInfo);
		}
	}



	@Override
	public void putOtherPlayerCoinInHashmap(int player_id, int n, int amount, int index) {
		int[] coinInfo = {player_id, n, amount, index};
		synchronized (unspawnedCoinPositions){
			unspawnedCoinPositions.put(index, coinInfo);
			numOfCoinsToSpawn.incrementAndGet();
		}
	}

	public ArrayList<int[]> minePosition=new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * Create the Google Api Client with access to Games
		 */
		mGoogleApiClient=new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Games.API).addScope(Games.SCOPE_GAMES)
				.build();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		/**
		 * Initialize the game
		 */
		initialize(new TechiesWorld(this), config);
	}


	@Override
	protected void onStart() {
		/**
		 * when the game starts, automatically log in first
		 */
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			Log.w(TAG,
					"GameHelper: client was already connected on onStart()");
		} else {
			Log.d(TAG,"Connecting client.");
			mGoogleApiClient.connect();
		}
		super.onStart();
	}

	/**
	 * When the game stop, quite the room and stop the program
	 */
	@Override
	protected void onStop() {
		Log.d(TAG, "**** got onStop");

		// if we're in a room, leave it.
		leaveRoom();
		super.onStop();
	}

	/**
	 * Handle the different reqestCode called back from the program
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**
		 * Handle the waiting room situation
		 */
		if (requestCode == RC_WAITING_ROOM) {
			if (mWaitingRoomFinishedFromCode){
				if (mParticipants != null) {
					myId = mParticipants.indexOf(me);
					ableToStart = true;
				}
				return;
			}
			if (resultCode == Activity.RESULT_OK) {
				byte[] bytes=new byte[1];
				bytes[0]=(byte) 'A';
				broadcastReliableMsg(bytes);
				if (mParticipants != null) {
					myId = mParticipants.indexOf(me);
					ableToStart = true;
				}
			} else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
				leaveRoom();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				leaveRoom();
			}
		}
		/**
		 * Handle the result of the "Select players UI" we launched when the user clicked the
		 * "Invite friends" button. We react by creating a room with those players.
		 */
		if (requestCode == RC_SELECT_PLAYERS) {
			if (resultCode != Activity.RESULT_OK) {
				Log.w(TAG, "*** select players UI cancelled, " + resultCode);
				leaveRoom();
				return;
			}
			// get the invitee list
			Bundle extras = data.getExtras();
			final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
			Log.d(TAG, "Invitee count: " + invitees.size());
			// get the automatch criteria
			Bundle autoMatchCriteria = null;
			int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
			int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
			if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
				autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
						minAutoMatchPlayers, maxAutoMatchPlayers, 0);
				Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
			}
			// create the room
			Log.d(TAG, "Creating room...");
			RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
			rtmConfigBuilder.addPlayersToInvite(invitees);
			rtmConfigBuilder.setMessageReceivedListener(this);
			rtmConfigBuilder.setRoomStatusUpdateListener(this);
			if (autoMatchCriteria != null) {
				rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
			}
			Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
	}
		/**
		 * Handle the result of the invitation inbox UI, where the player can pick an invitation
		 * to accept. We react by accepting the selected invitation, if any.
		 */
		if (requestCode == RC_INVITATION_INBOX) {
			if (resultCode != Activity.RESULT_OK) {
				Log.w(TAG, "*** select players UI cancelled, " + resultCode);
				leaveRoom();
				return;
			}
			Log.d(TAG, "Invitation inbox UI succeeded.");
			Bundle extras = data.getExtras();
			Invitation inv = extras.getParcelable(Multiplayer.EXTRA_INVITATION);
			// accept invitation
			acceptInviteToRoom(inv.getInvitationId());
		}
		/**
		 * Handle the sign in situation
		 */
		if (requestCode==RC_SIGN_IN){
			Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode="
					+ resultCode + ", intent=" + data);
			mSignInClicked = false;
			mResolvingConnectionFailure = false;
			if (resultCode == RESULT_OK) {
				mGoogleApiClient.connect();
			} else {
				BaseGameUtils.showActivityResultError(this,requestCode,resultCode, 0);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 	Accept the given invitation.
	 */
	void acceptInviteToRoom(String invId) {
		// accept the invitation
		Log.d(TAG, "Accepting invitation: " + invId);
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
		roomConfigBuilder.setInvitationIdToAccept(invId)
				.setMessageReceivedListener(this)
				.setRoomStatusUpdateListener(this);

		Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
	}

	/**
	 * Handle the sign in button
	 */
	@Override
	public void signIn() {
		Log.d(TAG, "Sign-in button clicked");
		mSignInClicked = true;
		mGoogleApiClient.connect();
	}

	/**
	 * Handle the sign out button
	 */
	@Override
	public void signOut() {
		// user wants to sign out
		// sign out.
		Log.d(TAG, "Sign-out button clicked");
		mSignInClicked = false;
		Games.signOut(mGoogleApiClient);
		mGoogleApiClient.disconnect();
	}

	/**
	 * Submit score to google leader board, might be used in the future
	 * @param highScore
	 */
	@Override
	public void submitScore(int highScore) {
		if (isSignedIn()) {
			Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_highest), highScore);
		}
	}

	/**
	 * Show the google achievement, might be used in the future
	 */
	@Override
	public void showAchievement() {
		if (isSignedIn()) {
			startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), requestCode);
		} else {
			signIn();
		}
	}

	/**
	 * Show the google leader board, might be used in the future
	 */
	@Override
	public void showScore() {
		if (isSignedIn()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
					getString(R.string.leaderboard_highest)), requestCode);
		} else {
			signIn();
		}
	}

	/**
	 * Check wether is signed in or not
	 * @return
	 */
	@Override
	public boolean isSignedIn() {
		return mGoogleApiClient.isConnected();
	}


	/**
	 * Start a quick game with minimum two players maximum 4 players
	 */
	@Override
	public void startQuickGame() {
		final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 3;
		Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
				MAX_OPPONENTS, 0);
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
		roomConfigBuilder.setMessageReceivedListener(this);
		roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
		RoomConfig roomConfig = roomConfigBuilder.build();

		//create room:
		Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

	}

	/**
	 * Call this method when invitePlayer button is pressed
	 */
	public void invitePlayer() {
		Intent intentInvite = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
		startActivityForResult(intentInvite, RC_SELECT_PLAYERS);
	}

	/**
	 * Call this method when the showInivitationBox button is pressed
	 */
	public void showInvitationBox() {
		Intent intentBox = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
		startActivityForResult(intentBox, RC_INVITATION_INBOX);
	}

	/**
	 * Handle the received message based on different header
	 * @param realTimeMessage
	 */
	@Override
	public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
		byte[] buf = realTimeMessage.getMessageData();
		if (buf[0]=='A'){
			mWaitingRoomFinishedFromCode=true;
			finishActivity(RC_WAITING_ROOM);
		}else if (buf[0] == 'P') {
			/**
			 * Handle player's position
			 */
			int id = buf[1];
			int[] position = new int[4];
			for (int i = 2; i <= 5; i++) {
				position[i - 2] = (int) buf[i];
			}
			playerPositions.put(id, position);
		} else if (buf[0] == 'S') {
			/**
			 * Player's status
			 */
			int id = buf[1];
			playerStatus.put(id, String.valueOf((char) buf[2]));
		}else if (buf[0]=='c'){ //coin collected by other player , format of msg {'c', index}
			collectedIndex.add((int) buf[1]); //index of coin to be removed on this device
			numOfCoinsToRemove.incrementAndGet();

		} else if (buf[0] == 'C') { //coin spawned by other player
			receiveCoinSpawnMsg(buf);
			//Log.wtf(TAG, "recv coin spawn msg from player: "+buf[1]+" n: "+buf[2]);

		} else if (buf[0] == 'M') {
			/**
			 * Mines
			 */
			int[] mineValue = new int[7];
			for (int i = 1; i <= 7; i++) {
				mineValue[i - 1] = (int) buf[i];
			}
			synchronized (minePosition) {
				minePosition.add(mineValue);
			}

		}else if (buf[0]=='t'){ //test
			int val = buf[1];
			Toast.makeText(getApplicationContext(), "" + val, Toast.LENGTH_SHORT).show();
		}else if (buf[0]=='L'){
			/**
			 * Final score
			 */
			int score=(int)(buf[2])*100+(int)(buf[3]);
			playerScore.put((int)buf[1],score);
		}
	}

	/**
	 * Show error message about game being cancelled and return to main screen.
	 */
	void showGameError() {
		BaseGameUtils.makeSimpleDialog(this, "error");
	}

	/**
	 * Called when room has been created
	 * @param statusCode
	 * @param room
	 */
	@Override
	public void onRoomCreated(int statusCode, Room room) {
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			showGameError();
			return;
		}

		// save room ID so we can leave cleanly before the game starts.
		mRoomId = room.getRoomId();

		// show the waiting room UI
		Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);
		startActivityForResult(i, RC_WAITING_ROOM);
	}


	@Override
	public void onJoinedRoom(int statusCode, Room room) {
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			showGameError();
			return;
		}

		// show the waiting room UI
		Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);
		startActivityForResult(i, RC_WAITING_ROOM);
	}

	@Override
	public void onLeftRoom(int i, String s) {

	}

	/**
	 * Called when room is fully connected.
	 * @param statusCode
	 * @param room
	 */
	@Override
	public void onRoomConnected(int statusCode, Room room) {
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			showGameError();
			return;
		}
		synchronized (playersMyIdHashcode) {
			mParticipants = room.getParticipants();
			mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));
			me = room.getParticipant(mMyId);
		}
	}

	/**
	 * Returen ableToStart flag
	 * @return
	 */
	public boolean getAbleToStart() {
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

	/**
	 * Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
	 * is connected yet).
	 * @param room
	 */
	@Override
	public void onConnectedToRoom(Room room) {
		mParticipants = room.getParticipants();
		mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));
		me = room.getParticipant(mMyId);
		if (mRoomId == null) {
			mRoomId = room.getRoomId();
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

	void leaveRoom() {
		if (mRoomId != null) {
			Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
			mRoomId = null;
			ableToStart = false;

		}
	}

	/**
	 * Update room situation
	 * @param room
	 */
	void updateRoom(Room room) {
		if (room != null) {
			mParticipants = room.getParticipants();
		}
	}

	/**
	 * Send unreliable messages
	 * @param mMsgBuf
	 */
	public void broadcastMsg(byte[] mMsgBuf) {
		if (mParticipants != null) {
			for (Participant p : mParticipants) {
				if (!p.getParticipantId().equals(mMyId)) {
					Games.RealTimeMultiplayer.sendUnreliableMessage(mGoogleApiClient, mMsgBuf, mRoomId,
							p.getParticipantId());
				}
			}
		}

	}

	/**
	 * Send reliable messages
	 * @param mMsgBuf
	 */
	public void broadcastReliableMsg(byte[] mMsgBuf) {
		if (mParticipants != null) {
			for (Participant p : mParticipants) {
				if (!p.getParticipantId().equals(mMyId)) {
					Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, mMsgBuf, mRoomId,
							p.getParticipantId());
				}
			}
		}

	}

	public void destroy() {
		this.destroy();
	}

	/**
	 * Reture player's unique ID
	 * @return
	 */
	public int getMyID() {
		return myId;
	}

	/**
	 * Return certain player's position based on the id
	 * @param id
	 * @return
	 */
	public int[] getPlayerPosition(int id) {
		return playerPositions.get(id);
	}

	/**
	 * Return certain player's status based on the id
	 * @param id
	 * @return
	 */
	public String getPlayerStatus(int id) {
		return playerStatus.get(id);
	}


	public void setPlayerCoinUnspawnedIndex() {
		unspawnedIndex = unspawnedIndex + (getMyID() * 64); //player0 mines will be from -128 to -65, p1 -64 to -1, p2 0 to 63, p3 64 to 127
	}

	@Override
	public int[] getSpawnedCoinPosition() {
		synchronized (unspawnedCoinPositions){
			for(int i = 0; i < 4; i++){
				if(unspawnedCoinPositions.containsKey(unspawnedGetIndexArray[i]) && myId!=i) { //need to extract coin index from hashmap
					int[] retVal = unspawnedCoinPositions.get(unspawnedGetIndexArray[i]); //retVal = {playerID, n, amount, index}
					unspawnedGetIndexArray[i]++;
					numOfCoinsToSpawn.decrementAndGet();
					//Log.wtf("TAG", "Show content of retval: " + Arrays.toString(retVal));
					return retVal;
				}
			}
			return null;
		}
	}

	@Override
	public void decrementCoinsToRemove() {
		numOfCoinsToRemove.decrementAndGet();
	}

	@Override
	public AtomicInteger numOfCoinsToRemove() {
		return numOfCoinsToRemove;
	}

	@Override
	public AtomicInteger numOfNewCoinsLeftToSpawn() {
		return numOfCoinsToSpawn;
	}

	@Override
	public int getCoinToRemoveIndex() {
		return collectedIndex.poll(); //Retrieves and removes the head of this queue, or returns null if this queue is empty.
	}

	public void receiveCoinSpawnMsg(byte[] mMsgBuf) {
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
		}
	}

	/**
	 * Return a copy of current mine map and clear the original one
	 * @return
	 */
	public ArrayList<int[]> getMinePositionAndClear() {
		synchronized (minePosition) {
			ArrayList<int[]> tempArray = new ArrayList<>(minePosition);
			minePosition.clear();
			return tempArray;
		}
	}

	/**
	 * Return whether there is new information of mines need to handle or not
	 * @return
	 */
	public boolean mineIsEmpty() {
		synchronized (minePosition) {
			return minePosition.isEmpty();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(TAG, "onConnected() called. Sign in successful!");

		Log.d(TAG, "Sign-in succeeded.");
		Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

		if (connectionHint != null) {
			Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
			Invitation inv = connectionHint
					.getParcelable(Multiplayer.EXTRA_INVITATION);
			if (inv != null && inv.getInvitationId() != null) {
				// retrieve and cache the invitation ID
				// retrieve and cache the invitation ID
				Log.d(TAG, "onConnected: connection hint has a room invite!");
				acceptInviteToRoom(inv.getInvitationId());
			}
		}

	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
		mGoogleApiClient.connect();
	}

	@Override
	public void onInvitationReceived(Invitation invitation) {
		Toast.makeText(AndroidLauncher.this, "Invitation received", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInvitationRemoved(String s) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

		if (mResolvingConnectionFailure) {
			Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
			return;
		}

		if (mSignInClicked || mAutoStartSignInFlow) {
			mAutoStartSignInFlow = false;
			mSignInClicked = false;
			mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
					connectionResult, RC_SIGN_IN, "Sign in error");
		}
	}

	/**
	 * Return the room size
	 * @return
	 */
	public int getRoomSize(){
		return mParticipants.size();
	}

	/**
	 * Return certain player's score based on the id
	 * @param id
	 * @return
	 */
	public int getPlayerScore(int id){
		synchronized (playerScore){
			if (playerScore.containsKey(id)){
				return playerScore.get(id);
			}
		}
		return 0;
	}

	/**
	 * Add in player's own score
	 * @param score
	 */
	public void putPlayerScore(int score){
		synchronized (playerScore){
			playerScore.put(getMyID(),score);
			System.out.println("adding id: "+getMyID()+" score: "+score);
		}
	}

}
