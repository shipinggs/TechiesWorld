package com.shiping.gametest.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 2/3/16.
 */
public class Player extends Sprite {
    private int playerID;

    private enum State { STANDING, WALKING, BEASTMODE, GROWING, DEAD };
    private State currentState;
    private State previousState;
    private World world;
    public Body b2body;

    private TextureRegion playerStand;
    private Animation playerWalk;
    private TextureRegion playerDead;
    private TextureRegion beastStand;
    private Animation beastWalk;
    private Animation growPlayer;


    public Player(World world) {
        this.world = world;
        definePlayer();
    }

    public void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / TechiesWorld.PPM, 32 / TechiesWorld.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / TechiesWorld.PPM);
        fdef.filter.categoryBits = TechiesWorld.PLAYER_BIT;
        fdef.filter.maskBits = TechiesWorld.WALL_BIT | TechiesWorld.MINE_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData("body"); // fixture is within a body
    }
}
