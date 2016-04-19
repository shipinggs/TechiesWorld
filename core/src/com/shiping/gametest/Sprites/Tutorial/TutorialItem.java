package com.shiping.gametest.Sprites.Tutorial;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.shiping.gametest.Screens.TutorialScreen;
import com.shiping.gametest.Sprites.Player;
import com.shiping.gametest.Sprites.TutorialPlayer;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 19/4/16.
 */
public abstract class TutorialItem extends Sprite {
    protected TutorialScreen screen;
    protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    public boolean destroyed;
    protected Body body;
    public int index;

    public TutorialItem(TutorialScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.index = -999;
        setPosition(x, y);
        setBounds(getX(), getY(), 64 / TechiesWorld.PPM, 64 / TechiesWorld.PPM);
        defineItem();
        toDestroy = false;
        destroyed = false;
    }

    public TutorialItem(TutorialScreen screen, float x, float y, int index) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.index = index;
        setPosition(x, y);
        setBounds(getX(), getY(), 64 / TechiesWorld.PPM, 64 / TechiesWorld.PPM);
        defineItem();
        toDestroy = false;
        destroyed = false;
    }

    public abstract void defineItem();
    public abstract void contact(TutorialPlayer player);

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
