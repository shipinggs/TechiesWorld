package com.shiping.gametest.Sprites.Items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.shiping.gametest.Screens.PlayScreen;
import com.shiping.gametest.Sprites.Player;
import com.shiping.gametest.TechiesWorld;

/**
 * The Coin class extends the Item class which in turn extends the Sprite class.
 * This is essentially for rendering the coin's Box2D and drawing the coin's sprite on the screen.
 *
 * It provides a method to handle collisions with players.
 */
public class Coin extends Item {
    private int amount;
    private Animation textureAnimation;
    private float stateTimer;
    private boolean setToDestroy;

    public Coin(PlayScreen screen, float x, float y, int amount, int index) {
        super(screen, x, y, index);
        this.amount = amount;
        stateTimer = 0;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // add coin bubbly animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(new Texture("PNGPack.png"), i*64, 70, 64, 64 ));
        }

        textureAnimation = new Animation(0.2f, frames);
        setRegion(textureAnimation.getKeyFrame(stateTimer, true));
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(24 / TechiesWorld.PPM);
        fdef.filter.categoryBits = TechiesWorld.COIN_BIT;
        fdef.filter.maskBits = TechiesWorld.WALL_BIT |
                TechiesWorld.RESPAWN_BIT |
                TechiesWorld.PLAYER_BIT ;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    public int getAmount() {
        return amount;
    }

    /**
     * The contact() method indicates to the class that the sprite is to be destroyed in the next update cycle.
     * It also sends an update message to other players telling them the coin has been picked up and
     * should be destroyed on their respective screens as well.
     *
     * @param player    The player to add gold to.
     */

    @Override
    public void contact(Player player) {
        setToDestroy = true;
        player.addGold(this);
        screen.getAudioManager().get("audio/sounds/pickup2.wav", Sound.class).play();

        //broadcast msg to other players
        byte[] msg = {'c', (byte) this.index}; //coin destroyed/collected msg
        TechiesWorld.playServices.broadcastMsg(msg);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (setToDestroy) {
            destroy();
        }
        stateTimer += dt;
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRegion(textureAnimation.getKeyFrame(stateTimer, true));
    }
}
