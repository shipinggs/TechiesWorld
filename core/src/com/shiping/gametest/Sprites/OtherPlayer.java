package com.shiping.gametest.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 8/4/16.
 *
 *  This class is solely for drawing the sprites of the other players.
 *  It does not provide for any game logic on its own.
 */

public class OtherPlayer extends Sprite {
    private int playerNumber;

    private enum State { ALIVE, GROWING, DEAD }
    private State currentState;
    private State previousState;

    private Animation playerAlive;
    private Animation playerDead;
    private Animation beastAlive;
    private Animation growPlayer;

    private float stateTimer;
    private boolean playerIsGrown;
    private boolean runGrowAnimation;

    private boolean playerIsDead;
    private int[] playerPosition;

    private Texture texturePack = new Texture("PNGPack.png");


    public OtherPlayer(int playerNum) {
        this.playerNumber = playerNum;
        currentState = State.ALIVE;
        previousState = State.ALIVE;

        stateTimer = 0;
        playerIsDead = false;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        switch (playerNumber) {
            case 1:
                // get run animation frames and add them to playerAlive Animation
                for (int i = 0; i < 6; i++) {
                    frames.add(new TextureRegion(texturePack, i * 64, 262, 64, 64));
                }
                playerAlive = new Animation(0.2f, frames);

                // clear frames for next animation sequence
                frames.clear();

                // get dying animation frames and add them to playerDead Animation
                for (int i = 0; i < 6; i++) {
                    frames.add(new TextureRegion(texturePack, i * 64, 326, 64, 64));
                }
                playerDead = new Animation (0.1f, frames);

                // clear frames for next animation sequence
                frames.clear();
                break;
        }


        setBounds(0, 0, 64 / TechiesWorld.PPM, 64 / TechiesWorld.PPM);
        setRegion(playerAlive.getKeyFrame(stateTimer, true));
    }

    public void update(float dt) {
        /* TODO change setPosition to be fetched x,y values from AndroidLauncher
         * Can use TechiesWorld.playServices.getPlayerPosition(playerNumber);
         */
        setPosition(140 / TechiesWorld.PPM, 140 / TechiesWorld.PPM);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame (float dt) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = playerDead.getKeyFrame(stateTimer, true);
                break;
            case GROWING:
                region = growPlayer.getKeyFrame(stateTimer);
                if (growPlayer.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case ALIVE:
            default:
                region = playerIsGrown? beastAlive.getKeyFrame(stateTimer, true) : playerAlive.getKeyFrame(stateTimer, true);
                break;
        }

        stateTimer = currentState == previousState? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if (playerIsDead) return State.DEAD;
        else if (runGrowAnimation) return State.GROWING;
        else return State.ALIVE;
    }

    public void setPlayerDead(boolean bool) {
        playerIsDead = bool;
    }


}
