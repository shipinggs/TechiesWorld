package com.shiping.gametest;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by G751-4314 on 1/4/2016.
 */
public interface PlayServices {
    public void signIn();
    public void signOut();
    public void submitScore(int highScore);
    public void showAchievement();
    public void showScore();
    public boolean isSignedIn();
    public void startQuickGame();
    public void invitePlayer();
    public void showInvitationBox();
    public boolean getAbleToStart();
    public void broadcastMsg(byte[] mMsgBuf);
    public void broadcastReliableMsg(byte[] mMsgBuf);
    public void destroy();
    public int getMyID();
    public int[] getPlayerPosition(int id);
    public String getPlayerStatus(int id);




//    public int getPlayerId();
    //coins
    public int getUnspawnedIndex();
    public void incrementUnspawnedIndex();
    public void putMyCoinInHashmap(int playerID, int n, int amount, int index);
    public void putOtherPlayerCoinInHashmap(int player_id, int n, int amount, int index);

    //Scenario 1: coin spawned on other device
    public int[] getSpawnedCoinPosition();
    public AtomicInteger numOfNewCoinsLeftToSpawn();


    //Scenario 2: coin spawned on my device




    public int getCoinToRemoveIndex();
    public AtomicInteger numOfCoinsToRemove();
    public void decrementCoinsToRemove();


    public ArrayList<int[]> getMinePositionAndClear();
    public boolean mineIsEmpty();

    public int getRoomSize();

    public void setPlayerCoinUnspawnedIndex();

    public int getPlayerScore(int id);
    public void putPlayerScore(int score);

}
