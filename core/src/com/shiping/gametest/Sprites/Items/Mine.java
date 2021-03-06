package com.shiping.gametest.Sprites.Items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.shiping.gametest.TechiesWorld;
import com.shiping.gametest.Screens.PlayScreen;
import com.shiping.gametest.Sprites.Player;

/**
 * The Mine class extends Item class which in turn extends the Sprite class.
 * It creates a Box2D body for the Mine item and draws the appropriate texture for the body,
 * depending on the state the mine is in.
 *
 * It provides the mechanism to allow a player's own mines to be visible and not dangerous,
 * while making other players' mines invisible after arming and exploding upon contact.
 */
public class Mine extends Item {

    private int playerID; // put by which player

    public enum State { PLACED, TRANSITION, ARMED, HIDDEN, EXPLODING }
    private State currentState;
    private State previousState;
    private float stateTime;

    private TextureRegion placedTexture;
    private TextureRegion transitionTexture;
    private TextureRegion armedTexture;
    private TextureRegion nullTexture;
    private Animation explosion;


    public int mineId;

    public Mine(PlayScreen screen, float x, float y, int playerID) {
        super(screen, x, y);
        this.playerID = playerID;

        // set TextureRegions for different states of mine
        Texture texture = new Texture("PNGPack.png");
        placedTexture = new TextureRegion((texture), 0, 0, 70, 70);
        transitionTexture = new TextureRegion((texture), 70, 0, 70, 70);
        armedTexture = new TextureRegion((texture), 140, 0, 70, 70);
        nullTexture = new TextureRegion((texture), 210, 0, 70, 70);

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // get explosion animation frames and add to explosion Animation
        for (int i = 0; i < 5; i++) {
            frames.add(new TextureRegion(new Texture("explode(64x64).png"), i*64, 0, 64, 64));
        }
        explosion = new Animation(0.1f, frames);

        // clear frames
        frames.clear();

        currentState = previousState = State.PLACED;
        setRegion(placedTexture);
    }

    /**
     *
     * @param dt    Necessary for returning the right keyframes of an Animation
     *
     * @return The TextureRegion to be drawn at that update cycle.
     */

    public TextureRegion getFrame(float dt) {
        TextureRegion region;

        switch (currentState) {
            case PLACED:
                region = placedTexture;
                break;
            case TRANSITION:
                region = transitionTexture;
                break;
            case ARMED:
                region = armedTexture;
                break;
            case HIDDEN:
                region = nullTexture;
                break;
            case EXPLODING:
                region = explosion.getKeyFrame(stateTime, true);
                break;
            default:
                region = armedTexture;
                break;
        }

        stateTime = currentState == previousState? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setRegion(getFrame(dt));
        if (currentState == State.PLACED && stateTime > 0.5) {
            currentState = State.TRANSITION;
        } else if (currentState == State.TRANSITION && stateTime > 1) {
            currentState = State.ARMED;
            defineItem();
        } else if (currentState == State.ARMED && stateTime > 0.5 && playerID != TechiesWorld.playServices.getMyID()) {
            currentState = State.HIDDEN;
        } else if (currentState == State.EXPLODING & stateTime > 0.5) {
            destroy();
        }
        // update sprite to correspond with position of b2body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

    }

    public State getState() {
        return currentState;
    }

    public void setCurrentState(int index) {
        currentState = State.values()[index];
    }

    /**
     * This method changes the State to the Exploding state and
     * sends an update message to other players that this mine has been contacted.
     *
     * @param player To set the player to die in the next update cycle.
     */
    @Override
    public void contact(Player player) {
        if (currentState == State.ARMED || currentState == State.HIDDEN) {
            TechiesWorld.playServices.broadcastMsg(sendRemoveMineBuffer(this));
            currentState = State.EXPLODING;
            screen.getAudioManager().get("audio/sounds/explosion2.wav", Sound.class).play();
            screen.getAudioManager().get("audio/sounds/die2.wav", Sound.class).play();
            screen.getAudioManager().get("audio/sounds/slime2.wav", Sound.class).play();
            player.setPlayerDead();
            player.b2body.setLinearVelocity(0, 0);
        }
    }

    /**
     * This method provides the mechanism to allow different collision handlers for own mines and others' mines.
     * It sets the mine to different categoryBits filters for own mines and others' mines.
     *
     * It also provides the mechanism to allow the different collision handling for mines of different States.
     * Only ARMED(others' mines) and HIDDEN mines will be set off upon contact with players.
     */

    @Override
    public void defineItem() {
        Vector2 position;
        if (currentState == State.ARMED) {
            position = body.getPosition();
            world.destroyBody(body);
        } else {
            position = new Vector2(getX(),getY());
        }
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(24 / TechiesWorld.PPM, 24/ TechiesWorld.PPM);
        if (currentState == State.ARMED && playerID != TechiesWorld.playServices.getMyID()) {
            fdef.filter.categoryBits = TechiesWorld.MINE_BIT;
        } else {
            fdef.filter.categoryBits = TechiesWorld.NOTHING_BIT;
        }
        fdef.filter.maskBits = TechiesWorld.MINE_BIT |
                TechiesWorld.WALL_BIT |
                TechiesWorld.PLAYER_BIT ;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }


    public void setMineId(int id){
        mineId=id;
    }

    public byte[] sendRemoveMineBuffer(Mine mine){
        byte[] minePosition=new byte[8];
        minePosition[0]=(byte)'M';
        minePosition[1]=(byte)0;
        minePosition[2]=(byte)mine.mineId;
        minePosition[3]=(byte) 0;
        minePosition[4]=(byte) 0;
        minePosition[5]=(byte) 0;
        minePosition[6]=(byte) 0;
        minePosition[7]=(byte) 0;
        return minePosition;
    }

}
