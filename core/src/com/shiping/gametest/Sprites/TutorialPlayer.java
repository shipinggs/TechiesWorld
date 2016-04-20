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
import com.shiping.gametest.Screens.TutorialScreen;
import com.shiping.gametest.Sprites.Items.Coin;
import com.shiping.gametest.Sprites.Items.ItemDef;
import com.shiping.gametest.Sprites.Tutorial.TutorialCoin;
import com.shiping.gametest.Sprites.Tutorial.TutorialItemDef;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 19/4/16.
 */
public class TutorialPlayer extends Sprite {
    private int playerID;
    private int gold;

    private enum State {ALIVE, DEAD, RESPAWN}

    private State currentState;
    private State previousState;
    private World world;
    private TutorialScreen screen;
    public Body b2body;

    private Animation playerAlive;
    private Animation playerDead;
    private Animation playerRespawn;

    private float stateTimer;
    private boolean runGrowAnimation;
    private boolean playerIsDead;
    private boolean playerIsRespawning;

    private Texture texturePack = new Texture("PNGPack.png");
    private Texture respawnPack = new Texture("respawn.png");

    private boolean pickedCoin;


    public TutorialPlayer(TutorialScreen screen) {
        this.world = screen.getWorld();
        this.screen = screen;
        currentState = State.RESPAWN;
        previousState = State.RESPAWN;
        playerIsRespawning = true;

        playerID = 1;   // player 1 for tutorial
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
            getAmountDropped(); //to deduct score

            screen.spawnItem(new TutorialItemDef(new Vector2(b2body.getPosition().x, b2body.getPosition().y), TutorialCoin.class));

            world.destroyBody(b2body);
            definePlayer();
            screen.getAudioManager().get("audio/sounds/respawn.wav", Sound.class).play(2f);
        } else if (previousState == State.RESPAWN && stateTimer > 1.8) {
            currentState = State.ALIVE;
            playerIsRespawning = false;

            definePlayer();
        }
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

    public void addGold(TutorialCoin coin) {
        pickedCoin = true;
        gold += coin.getAmount();
    }

    public void minusGold(int amount) {
        if (gold - amount >= 0) gold -= amount;
        else gold = 0;
    }

    public int getGoldAmount() {
        return gold;
    }

    public boolean hasPickedCoin() {
        return pickedCoin;
    }

    public void setPlayerDead() {
        playerIsDead = true;
        screen.setHasDied(true);
    }

    public boolean isPlayerDead() {
        return playerIsDead;
    }

    public int getPlayerID() {
        return playerID;
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
                TechiesWorld.TUTMINE_BIT |
                TechiesWorld.TUTCOIN_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this); // fixture is within a body
    }

}
