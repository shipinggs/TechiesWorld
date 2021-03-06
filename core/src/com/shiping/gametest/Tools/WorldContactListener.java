package com.shiping.gametest.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.shiping.gametest.Sprites.Items.Coin;
import com.shiping.gametest.Sprites.Items.Mine;
import com.shiping.gametest.Sprites.Player;
import com.shiping.gametest.Sprites.Tutorial.TutorialCoin;
import com.shiping.gametest.Sprites.Tutorial.TutorialMine;
import com.shiping.gametest.Sprites.TutorialPlayer;
import com.shiping.gametest.TechiesWorld;

/**
 * All collision handling can be found here!
 */
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case TechiesWorld.PLAYER_BIT | TechiesWorld.MINE_BIT:
                if (fixA.getFilterData().categoryBits == TechiesWorld.MINE_BIT) {
                    ((Mine) fixA.getUserData()).contact((Player) fixB.getUserData());
                } else {
                    ((Mine) fixB.getUserData()).contact((Player) fixA.getUserData());
                }
                break;

            case TechiesWorld.PLAYER_BIT | TechiesWorld.COIN_BIT:
            case TechiesWorld.RESPAWN_BIT | TechiesWorld.COIN_BIT:
                if (fixA.getFilterData().categoryBits == TechiesWorld.COIN_BIT) {
                    ((Coin) fixA.getUserData()).contact((Player) fixB.getUserData());
                } else {
                    ((Coin) fixB.getUserData()).contact((Player) fixA.getUserData());
                }
                break;

            case TechiesWorld.PLAYER_BIT | TechiesWorld.TUTCOIN_BIT:
            case TechiesWorld.RESPAWN_BIT | TechiesWorld.TUTCOIN_BIT:
                if (fixA.getFilterData().categoryBits == TechiesWorld.TUTCOIN_BIT) {
                    ((TutorialCoin) fixA.getUserData()).contact((TutorialPlayer) fixB.getUserData());
                } else {
                    ((TutorialCoin) fixB.getUserData()).contact((TutorialPlayer) fixA.getUserData());
                }
                break;

            case TechiesWorld.PLAYER_BIT | TechiesWorld.TUTMINE_BIT:
                if (fixA.getFilterData().categoryBits == TechiesWorld.TUTMINE_BIT) {
                    ((TutorialMine) fixA.getUserData()).contact((TutorialPlayer) fixB.getUserData());
                } else {
                    ((TutorialMine) fixB.getUserData()).contact((TutorialPlayer) fixA.getUserData());
                }
                break;
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
