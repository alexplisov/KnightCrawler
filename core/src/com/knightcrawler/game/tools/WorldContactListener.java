package com.knightcrawler.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.knightcrawler.game.entities.Demon;
import com.knightcrawler.game.entities.Player;
import com.knightcrawler.game.screens.PlayScreen;

/**
 * Created by alexp on 09.04.2017.
 */
public class WorldContactListener implements ContactListener {

    private Player player;

    public WorldContactListener(Player player) {
        this.player = player;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        if (fixA.getUserData() == "player" || fixB.getUserData() == "player") {
            Fixture player = fixA.getUserData() == "player" ? fixA : fixB;
            Fixture object = player == fixA ? fixB : fixA;
            if (object.getUserData() != null && Demon.class.isAssignableFrom(object.getUserData().getClass())) {
                ((Demon) object.getUserData()).onCollision();
            }
        }
        if (fixA.getUserData() == "sword" || fixB.getUserData() == "sword") {
            Fixture sword = fixA.getUserData() == "sword" ? fixA : fixB;
            Fixture object = sword == fixA ? fixB : fixA;
            if (object.getUserData() != null && Demon.class.isAssignableFrom(object.getUserData().getClass())) {
                ((Demon) object.getUserData()).isReach(true);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        if (fixA.getUserData() == "player" || fixB.getUserData() == "player") {
            Fixture player = fixA.getUserData() == "player" ? fixA : fixB;
            Fixture object = player == fixA ? fixB : fixA;
            if (object.getUserData() != null && Demon.class.isAssignableFrom(object.getUserData().getClass())) {
                ((Demon) object.getUserData()).onRelease();
            }
        }
        if (fixA.getUserData() == "sword" || fixB.getUserData() == "sword") {
            Fixture sword = fixA.getUserData() == "sword" ? fixA : fixB;
            Fixture object = sword == fixA ? fixB : fixA;
            if (object.getUserData() != null && Demon.class.isAssignableFrom(object.getUserData().getClass())) {
                ((Demon) object.getUserData()).isReach(false);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
