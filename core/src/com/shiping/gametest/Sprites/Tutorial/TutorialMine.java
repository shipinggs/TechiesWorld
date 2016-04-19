package com.shiping.gametest.Sprites.Tutorial;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.shiping.gametest.Screens.TutorialScreen;
import com.shiping.gametest.Sprites.TutorialPlayer;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 19/4/16.
 */
public class TutorialMine extends TutorialItem {
    private int playerID; // put by which player

    public enum State { PLACED, TRANSITION, ARMED }
    private State currentState;
    private State previousState;
    private float stateTime;

    private TextureRegion placedTexture;
    private TextureRegion transitionTexture;
    private TextureRegion armedTexture;

    public TutorialMine(TutorialScreen screen, float x, float y, int playerID) {
        super(screen, x, y);
        this.playerID = playerID;   //input parameter is set to be 0

        // set TextureRegions for different states of mine
        Texture texture = new Texture("PNGPack.png");
        placedTexture = new TextureRegion((texture), 0, 0, 70, 70);
        transitionTexture = new TextureRegion((texture), 70, 0, 70, 70);
        armedTexture = new TextureRegion((texture), 140, 0, 70, 70);

        currentState = previousState = State.PLACED;
        setRegion(placedTexture);

        defineItem();

    }

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
        }
        // update sprite to correspond with position of b2body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

    }

    public State getState() {
        return currentState;
    }



    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(24 / TechiesWorld.PPM, 24/ TechiesWorld.PPM);
        fdef.filter.categoryBits = TechiesWorld.NOTHING_BIT;
        fdef.filter.maskBits = TechiesWorld.MINE_BIT |
                TechiesWorld.WALL_BIT |
                TechiesWorld.PLAYER_BIT ;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void contact(TutorialPlayer player) {
        //do nothing
    }

}
