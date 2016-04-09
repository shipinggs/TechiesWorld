package com.shiping.gametest;

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
}
