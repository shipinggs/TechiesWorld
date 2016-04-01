package com.shiping.gametest.Sprites;

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
import com.shiping.gametest.Sprites.Items.Item;
import com.shiping.gametest.Sprites.Items.ItemDef;
import com.shiping.gametest.Sprites.Items.Mine;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 2/3/16.
 */
public class Player extends Sprite {
    private int playerID;
    private int score;
    private int minesLeft;

    private enum State { ALIVE, GROWING, DEAD }
    private enum Direction { TOP, TOPRIGHT, RIGHT, RIGHTBOTTOM, BOTTOM, BOTTOMLEFT, LEFT, LEFTTOP }
    private State currentState;
    private State previousState;
    private Direction currentDirection;
    private Direction previousDirection;
    private World world;
    private PlayScreen screen;
    public Body b2body;

    private Animation playerAlive;
    private Animation playerDead;
    private Animation beastAlive;
    private Animation growPlayer;

    private float stateTimer;
    private boolean playerIsGrown;
    private boolean runGrowAnimation;
    private boolean timeToDefineBeastPlayer;
    private boolean timeToRedefinePlayer;
    private boolean playerIsDead;

    private Texture texturePack = new Texture("PNGPack.png");


    public Player(PlayScreen screen) {
        this.world = screen.getWorld();
        this.screen = screen;
        currentState = State.ALIVE;
        previousState = State.ALIVE;
        currentDirection = Direction.BOTTOM;
        previousDirection = Direction.BOTTOM;

        score = 500;
        minesLeft = 3;

        stateTimer = 0;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // get run animation frames and add them to playerAlive Animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(texturePack, i * 64, 134, 64, 64));
        }
        playerAlive = new Animation(0.2f, frames);

        // clear frames for next animation sequence
        frames.clear();

        // get dying animation frames and add them to playerDead Animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(texturePack, i * 64, 198, 64, 64));
        }
        playerDead = new Animation (0.1f, frames);

        // clear frames for next animation sequence
        frames.clear();


        definePlayer();

        setBounds(100, 0, 64 / TechiesWorld.PPM, 64 / TechiesWorld.PPM);
        setRegion(playerAlive.getKeyFrame(stateTimer, true));
    }

    public void update(float dt) {
        // update our sprite to correspond with the position of our Box2D body
        if (playerIsGrown) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / TechiesWorld.PPM);
        } else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }

        if (playerIsDead && previousState == State.DEAD && stateTimer > 0.6) {
            playerIsDead = false;
            currentState = State.ALIVE;
            screen.spawnItem(new ItemDef(new Vector2(b2body.getPosition().x, b2body.getPosition().y), Coin.class));
            world.destroyBody(b2body);
            definePlayer();
        }
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame (float dt) {
        currentState = getState();
        System.out.println(currentState);
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
                region = playerIsGrown ?  beastAlive.getKeyFrame(stateTimer, true) : playerAlive.getKeyFrame(stateTimer, true);
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

    public void addScore(Coin coin) {
        score += coin.getAmount();
    }

    public int getScore() {
        return score;
    }

    public void setPlayerDead() {
        playerIsDead = true;
    }

    public boolean isPlayerDead() {
        return playerIsDead;
    }

    public void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(140 / TechiesWorld.PPM, 140 / TechiesWorld.PPM);
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
