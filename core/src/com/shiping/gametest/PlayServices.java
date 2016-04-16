package com.shiping.gametest;

import java.util.ArrayList;

/**
 * Created by G751-4314 on 1/4/2016.
 */
public interface PlayServices {
    public void signIn();
    public void signOut();
    public void rateGame();
    public void unlockAchievement();
    public void submitScore(int highScore);
    public void showAchievement();
    public void showScore();
    public boolean isSignedIn();
    public void startQuickGame();
    public boolean getAbleToStart();
    public void broadcastMsg(byte[] mMsgBuf);
    public void destroy();
    public int getMyPosition();
    public int[] getPlayerPosition(int id);
    public String getPlayerStatus(int id);




    public int getPlayerId();
    //coins
    public int getUnspawnedIndex();
    public void incrementUnspawnedIndex();
    public void putMyCoinInHashmap(int playerID, int n, int amount, int index);
    public void putOtherPlayerCoinInHashmap(int player_id, int n, int amount, int index);

    //Scenario 1: coin spawned on other device
    public int[] getSpawnedCoinPosition();
    public int numOfNewCoinsLeftToSpawn();


    //Scenario 2: coin spawned on my device




    public int getCoinToRemoveIndex();
    public int numOfCoinsToRemove();
    public void decrementCoinsToRemove();


    public ArrayList<int[]> getMinePositionAndClear();
    public boolean mineIsEmpty();

}
