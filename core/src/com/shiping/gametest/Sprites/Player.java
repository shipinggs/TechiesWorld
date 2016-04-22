package com.shiping.gametest.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.shiping.gametest.Screens.PlayScreen;
import com.shiping.gametest.Sprites.Items.Coin;
import com.shiping.gametest.Sprites.Items.ItemDef;
import com.shiping.gametest.TechiesWorld;

/**
 * The Player class maintains the state of the player and determines which texture is to be drawn on the screen for that state.
 * It also maintains the amount of gold that the player has.
 *
 * This class provides methods and procedures to send update messages at each update cycle to other players, telling them
 * of this player's position on the screen.
 */

public class Player extends Sprite {
    private int playerID;
    private int gold;

    private enum State {ALIVE, DEAD, RESPAWN}

    private State currentState;
    private State previousState;
    private World world;
    private PlayScreen screen;
    public Body b2body;

    private Animation playerAlive;
    private Animation playerDead;
    private Animation playerRespawn;

    private float stateTimer;
    private boolean playerIsDead;
    private boolean playerIsRespawning;

    private Texture texturePack = new Texture("PNGPack.png");
    private Texture respawnPack = new Texture("respawn.png");


    public Player(PlayScreen screen) {
        this.world = screen.getWorld();
        this.screen = screen;
        currentState = State.RESPAWN;
        previousState = State.RESPAWN;
        playerIsRespawning = true;

        playerID = TechiesWorld.playServices.getMyID();
        gold = 500;    // starting score/gold

        stateTimer = 0;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        int yOffset = 134 + playerID * 128;
        // get run animation frames and add them to playerAlive Animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(texturePack, i * 64, yOffset, 64, 64));
        }
        playerAlive = new Animation(0.2f, frames);
        // clear frames for next animation sequence
        frames.clear();
        // get dying animation frames and add them to playerDead Animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(texturePack, i * 64, yOffset + 64, 64, 64));
        }
        playerDead = new Animation(0.1f, frames);
        // clear frames for next animation sequence
        frames.clear();

        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(respawnPack, i * 64, playerID * 64, 64, 64));
        }
        playerRespawn = new Animation(0.1f, frames);
        // clear frames for next animation sequence
        frames.clear();

        definePlayer();

        setBounds(0, 0, 64 / TechiesWorld.PPM, 64 / TechiesWorld.PPM);
        setRegion(playerRespawn.getKeyFrame(stateTimer, true));
    }

    public void update(float dt) {
        // update our sprite to correspond with the position of our Box2D body
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        if (playerIsDead && previousState == State.DEAD && stateTimer > 0.6) {
            playerIsDead = false;
            playerIsRespawning = true;
            currentState = State.RESPAWN;

            //sending message to other devices about coin spawned due to player's death
            byte[] coinSpawnedMsg = new byte[5]; //format: {'C', x, y, index, amount}
            coinSpawnedMsg[0] = 'C';
            int x = Math.round(b2body.getPosition().x * 60); //multiplied by 60 to capture more resolution
            coinSpawnedMsg[1] = (byte) x; //x position of coin
            //Gdx.app.log("coinSpawnPositions X", ""+(coinSpawnPositions1[n][0] * 10));
            int y = Math.round(b2body.getPosition().y * 60); //multiplied by 60 to capture more resolution
            coinSpawnedMsg[2] = (byte) y; //y position of coin
            // Gdx.app.log("coinSpawnPositions Y", ""+(coinSpawnPositions1[n][1] * 10));
            int index = TechiesWorld.playServices.getUnspawnedIndex();

            coinSpawnedMsg[3] = (byte) index; //index
            int amount = getAmountDropped();
            coinSpawnedMsg[4] = (byte) amount; //amount
            int playerID = TechiesWorld.playServices.getMyID();
            TechiesWorld.playServices.putMyCoinInHashmap(playerID, 999, amount, index); //playerID, n, amount, index (999 are just place holder values since they will not be used

            TechiesWorld.playServices.broadcastMsg(coinSpawnedMsg);
            screen.spawnItem(new ItemDef(new Vector2(b2body.getPosition().x, b2body.getPosition().y), Coin.class, index));
            TechiesWorld.playServices.incrementUnspawnedIndex();

            world.destroyBody(b2body);
            definePlayer();
            screen.getAudioManager().get("audio/sounds/respawn.wav", Sound.class).play();
        } else if (previousState == State.RESPAWN && stateTimer > 1.8) {
            currentState = State.ALIVE;
            playerIsRespawning = false;

            definePlayer();
        }

        TechiesWorld.playServices.broadcastMsg(sendPositionBuffer());
        TechiesWorld.playServices.broadcastMsg(sendStatusBuffer());
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = playerDead.getKeyFrame(stateTimer, true);
                break;
            case RESPAWN:
                region = playerRespawn.getKeyFrame(stateTimer, true);
                break;
            case ALIVE:
            default:
                region = playerAlive.getKeyFrame(stateTimer, true);
                break;
        }
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }


    public State getState() {
        if (playerIsDead) return State.DEAD;
        else if (playerIsRespawning) return State.RESPAWN;
        else return State.ALIVE;
    }

    public int getAmountDropped() {
        int amount = gold / 4 < 150 ? 150 : gold / 4;
        gold -= amount;
        if (gold<=0) {
            gold = 0;
        }
        return amount;
    }

    public void addGold(Coin coin) {
        gold += coin.getAmount();
    }

    public int getGoldAmount() {
        return gold;
    }

    public void setPlayerDead() {
        playerIsDead = true;
    }

    public boolean isPlayerDead() {
        return playerIsDead;
    }


    public void definePlayer() {
        BodyDef bdef = new BodyDef();
        if (currentState == State.RESPAWN) {
            if (playerID == 0) {
                bdef.position.set(140 / TechiesWorld.PPM, 140 / TechiesWorld.PPM);
            } else if (playerID == 1) {
                bdef.position.set(880 / TechiesWorld.PPM, 140 / TechiesWorld.PPM);
            } else if (TechiesWorld.playServices.getMyID() == 2) {
                bdef.position.set(140 / TechiesWorld.PPM, 900 / TechiesWorld.PPM);
            } else if (TechiesWorld.playServices.getMyID() == 3) {
                bdef.position.set(900 / TechiesWorld.PPM, 900 / TechiesWorld.PPM);
            }
        }else if (currentState == State.ALIVE) {
                Vector2 currentPosition = b2body.getPosition();
                world.destroyBody(b2body);
                bdef.position.set(currentPosition);
            }

            bdef.type = BodyDef.BodyType.DynamicBody;
            b2body = world.createBody(bdef);

            FixtureDef fdef = new FixtureDef();
            CircleShape shape = new CircleShape();
            shape.setRadius(24 / TechiesWorld.PPM);
            if (currentState == State.RESPAWN) {
                fdef.filter.categoryBits = TechiesWorld.RESPAWN_BIT;
            } else if (currentState == State.ALIVE) {
                fdef.filter.categoryBits = TechiesWorld.PLAYER_BIT;
            }
            fdef.filter.maskBits = TechiesWorld.WALL_BIT |
                    TechiesWorld.MINE_BIT |
                    TechiesWorld.COIN_BIT;

            fdef.shape = shape;
            b2body.createFixture(fdef).setUserData(this); // fixture is within a body
        }


        public byte[] sendPositionBuffer () { //sending player position to other device
            byte[] position = new byte[6];
            position[0] = (byte) 'P';
            position[1] = (byte) TechiesWorld.playServices.getMyID(); //id of player (0-3)
            int x = (int) (b2body.getPosition().x * TechiesWorld.PPM); //multiply with ppm value to get int in hundreds range
            int y = (int) (b2body.getPosition().y * TechiesWorld.PPM);
            position[2] = (byte) (x / 100);
            position[3] = (byte) (x % 100);
            position[4] = (byte) (y / 100);
            position[5] = (byte) (y % 100);
            return position;
        }

        public byte[] sendStatusBuffer () {
            byte[] status = new byte[3];
            status[0] = (byte) 'S';
            status[1] = (byte) TechiesWorld.playServices.getMyID();
            if (playerIsDead) {
                status[2] = (byte) 'D';
            } else {
                status[2] = (byte) 'A';
            }
            return status;
        }
    }
