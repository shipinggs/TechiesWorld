package com.shiping.gametest.Sprites.Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.shiping.gametest.TechiesWorld;
import com.shiping.gametest.Screens.PlayScreen;
import com.shiping.gametest.Sprites.Player;

/**
 * Created by shiping on 2/3/16.
 */
public class Mine extends Item {

    public enum State { PLACED, TRANSITION, ARMED, HIDDEN }
    private State currentState;
    private State previousState;

    public Mine(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(new TextureRegion(new Texture("PNGPack.png")), 140, 0, 70, 70);
//        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
//        velocity = new Vector2(screen.getPlayer().b2body.getLinearVelocity().x * 3, screen.getPlayer().b2body.getLinearVelocity().y * 3);
        
    }

    public State getState() {
        return currentState;
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(32 / TechiesWorld.PPM);
        fdef.filter.categoryBits = TechiesWorld.MINE_BIT;
        fdef.filter.maskBits = TechiesWorld.MINE_BIT |
                TechiesWorld.WALL_BIT |
                TechiesWorld.ENEMY_BIT ;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void contact(Player player) {
        destroy();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        // update sprite to correspond with position of b2body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
//        body.setLinearVelocity(velocity);

    }
}
