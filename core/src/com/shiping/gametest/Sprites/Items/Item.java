package com.shiping.gametest.Sprites.Items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.shiping.gametest.TechiesWorld;
import com.shiping.gametest.Screens.PlayScreen;
import com.shiping.gametest.Sprites.Player;

/**
 * This is an abstract class which provides the abstract methods to be implemented in the
 * Mine and Coin classes.
 */
public abstract class Item extends Sprite {
    protected PlayScreen screen;
    protected World world;
    protected boolean toDestroy;
    public boolean destroyed;
    protected Body body;
    public int index;

    public Item(PlayScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.index = -999;
        setPosition(x, y);
        setBounds(getX(), getY(), 64 / TechiesWorld.PPM, 64 / TechiesWorld.PPM);
        defineItem();
        toDestroy = false;
        destroyed = false;
    }

    public Item(PlayScreen screen, float x, float y, int index) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.index = index;
        setPosition(x, y);
        setBounds(getX(), getY(), 64 / TechiesWorld.PPM, 64 / TechiesWorld.PPM);
        defineItem();
        toDestroy = false;
        destroyed = false;
    }

    public abstract void defineItem();              // all items must have a Box2D body
    public abstract void contact(Player player);    // all items must handle collisions with players

    public void update(float dt) {
        if (toDestroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
        }
    }

    public void draw(Batch batch) {
        if (!destroyed) {
            super.draw(batch);
        }
    }

    public void destroy() {
        toDestroy = true;
    }

    public boolean isDestroyed(){
        return destroyed;
    }

    public boolean isToDestroy(){
        return toDestroy;
    }
}
