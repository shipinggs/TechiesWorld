package com.shiping.gametest.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.shiping.gametest.Scenes.Hud;
import com.shiping.gametest.Screens.PlayScreen;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 2/3/16.
 */
public class Player extends Sprite {
    private int playerID;

    private enum State { ALIVE, GROWING, DEAD }
    private enum Direction { TOP, TOPRIGHT, RIGHT, RIGHTBOTTOM, BOTTOM, BOTTOMLEFT, LEFT, LEFTTOP }
    private State currentState;
    private State previousState;
    private Direction currentDirection;
    private Direction previousDirection;
    private World world;
    public Body b2body;

    private Animation playerAlive;
    private TextureRegion playerDead;
    private Animation beastAlive;
    private Animation growPlayer;

    private float stateTimer;
    private boolean playerIsGrown;
    private boolean runGrowAnimation;
    private boolean timeToDefineBeastPlayer;
    private boolean timeToRedefinePlayer;
    private boolean playerIsDead;


    public Player(PlayScreen screen) {
        this.world = screen.getWorld();
        currentState = State.ALIVE;
        previousState = State.ALIVE;
        currentDirection = Direction.BOTTOM;
        previousDirection = Direction.BOTTOM;

        stateTimer = 0;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // get run animation frames and add them to marioRun Animation
        for (int i = 1; i < 6; i++) {
            frames.add(new TextureRegion(new Texture("PNGPack.png"), i * 64, 134, 64, 64));
        }
        playerAlive = new Animation(0.2f, frames);
//        this.setRotation(180);
        frames.clear();

        definePlayer();

        setBounds(100, 0, 64 / TechiesWorld.PPM, 64 / TechiesWorld.PPM);

    }

    public void update(float dt) {
        // update our sprite to correspond with the position of our Box2D body
        if (playerIsGrown) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / TechiesWorld.PPM);
        } else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame (float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = playerDead;
                break;
            case GROWING:
                region = growPlayer.getKeyFrame(stateTimer);
                if (growPlayer.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case ALIVE:
            default:
                System.out.println("alive");
                region = playerIsGrown ?  beastAlive.getKeyFrame(stateTimer, true) : playerAlive.getKeyFrame(stateTimer, true);
                break;
        }

//        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) { // isFlipX() returns true if the region is flipped from its original
//            region.flip(true, false);
//            runningRight = false;
//        }
//        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
//            region.flip(true, false);
//            runningRight = true;
//        }

        stateTimer = currentState == previousState? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState () {
        if (playerIsDead) return State.DEAD;
        else if (runGrowAnimation) return State.GROWING;
        else return State.ALIVE;
    }

    public void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(70 / TechiesWorld.PPM, 70 / TechiesWorld.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(24 / TechiesWorld.PPM);
        fdef.filter.categoryBits = TechiesWorld.PLAYER_BIT;
        fdef.filter.maskBits = TechiesWorld.WALL_BIT |
                TechiesWorld.MINE_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this); // fixture is within a body
    }
}
