package com.shiping.gametest.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.shiping.gametest.Screens.PlayScreen;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 8/4/16.
 *
 *  This class is solely for drawing the sprites of the other players.
 *  It does not provide for any game logic on its own.
 */

public class OtherPlayer extends Sprite {
    private int playerNumber;
    private PlayScreen screen;

    private enum State { ALIVE, DEAD }
    private State currentState;
    private State previousState;

    private Animation playerAlive;
    private Animation playerDead;
    private Animation playerRespawn;

    private float stateTimer;

    private boolean playerIsDead;

    private Texture texturePack = new Texture("PNGPack.png");
    private Texture respawnPack = new Texture("respawn.png");


    public OtherPlayer(PlayScreen screen, int playerNum) {
        this.screen = screen;
        this.playerNumber = playerNum;
        currentState = State.ALIVE;
        previousState = State.ALIVE;

        stateTimer = 0;
        playerIsDead = false;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        int yOffset = 134 + playerNum * 128;  // to get yOffset in PNGPack based on playerID
        // get run animation frames and add them to playerAlive Animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(texturePack, i * 64, yOffset, 64, 64));
        }
        playerAlive = new Animation(0.2f, frames);
        // clear frames for next animation sequence
        frames.clear();
        // get dying animation frames and add them to playerDead Animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(texturePack, i * 64, yOffset+64, 64, 64));
        }
        playerDead = new Animation (0.1f, frames);
        // clear frames for next animation sequence
        frames.clear();
        // get respawn animation frames and add them to playerRespawn Animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(respawnPack, i * 64, playerNum*64, 64, 64));
        }
        playerRespawn = new Animation (0.1f, frames);
        // clear frames for next animation sequence
        frames.clear();

        setBounds(0, 0, 64 / TechiesWorld.PPM, 64 / TechiesWorld.PPM);
        setRegion(playerAlive.getKeyFrame(stateTimer, true));
    }

    public void update(float dt) {
        /* TODO change setPosition to be fetched x,y values from AndroidLauncher
         * Can use TechiesWorld.playServices.getPlayerPosition(playerNumber);
         */
        if (TechiesWorld.playServices.getPlayerPosition(playerNumber)!=null){
            int[] position=TechiesWorld.playServices.getPlayerPosition(playerNumber);
            float x=position[0]*100+position[1]-30; //30 is some offset due to data transfer
            float y=position[2]*100+position[3]-30;
            setPosition(x / TechiesWorld.PPM, y / TechiesWorld.PPM);
            setRegion(getFrame(dt));
        }
        if (TechiesWorld.playServices.getPlayerStatus(playerNumber)!=null){
            String status=TechiesWorld.playServices.getPlayerStatus(playerNumber);
            if (status.equals("D")){
                setPlayerDead(true);
            }else if (status.equals("A")){
                setPlayerDead(false);
            }
        }
    }

    public TextureRegion getFrame (float dt) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case DEAD:
                if (stateTimer == 0) screen.getAudioManager().get("audio/sounds/explosion.wav", Sound.class).play(0.7f);
                region = playerDead.getKeyFrame(stateTimer, true);
                break;
            case ALIVE:
                if (stateTimer == 0) screen.getAudioManager().get("audio/sounds/respawn.wav", Sound.class).play(0.5f);

                region = stateTimer <= 1.8? playerRespawn.getKeyFrame(stateTimer,true) : playerAlive.getKeyFrame(stateTimer,true);
                break;
            default:
                region = playerAlive.getKeyFrame(stateTimer, true);
                break;
        }

        stateTimer = currentState == previousState? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if (playerIsDead) return State.DEAD;
        else return State.ALIVE;
    }

    public void setPlayerDead(boolean bool) {
        playerIsDead = bool;
    }


}
