package com.knightcrawler.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.knightcrawler.game.entities.Demon;
import com.knightcrawler.game.entities.Player;

import java.util.List;

/**
 * Created by alexp on 09.04.2017.
 */
public class WorldContactListener implements ContactListener {

    private List<Demon> demons;
    private Player player;

    public WorldContactListener(List<Demon> demons, Player player) {
        this.demons = demons;
        this.player = player;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // collide demon with player
        if (fixA.getUserData() != null && fixB.getUserData() != null) {
            if (fixA.getUserData().equals("demon") || fixB.getUserData().equals("demon")) {
                Body demon = fixA.getUserData().equals("demon") ? fixA.getBody() : fixB.getBody();
                Fixture player = fixB.getUserData().equals("player") ? fixB : fixA;
                if (player.getUserData() == "player") {
                    for (Demon d : demons) {
                        if (d.getBody() == demon) {
                            d.setStopped(true);
                        }
                    }
                }
            }
        }

        // collide demonSensor with player
        if (fixA.getUserData() != null && fixB.getUserData() != null) {
            if (fixA.getUserData().equals("demonSensor") || fixB.getUserData().equals("demonSensor")) {
                Body demonSensor = fixA.getUserData().equals("demonSensor") ? fixA.getBody() : fixB.getBody();
                Fixture player = fixB.getUserData().equals("player") ? fixB : fixA;
                if (player.getUserData() == "player") {
                    this.player.setReached(true);
                    for (Demon d : demons) {
                        if (d.getBody() == demonSensor) {
                            d.setStopped(true);
                            d.setFighting(true);
                            d.stateTimer = 0;
                        }
                    }
                }
            }
        }

        // collide playerSensor with demon
        if (fixA.getUserData() != null && fixB.getUserData() != null) {
            if (fixA.getUserData().equals("demon") || fixB.getUserData().equals("demon")) {
                Body demon = fixA.getUserData().equals("demon") ? fixA.getBody() : fixB.getBody();
                Fixture player = fixB.getUserData().equals("playerSensor") ? fixB : fixA;
                if (player.getUserData() == "playerSensor") {
                    for ( Demon d : demons) {
                        if (d.getBody() == demon) {
                            d.setReached(true);
                        }
                    }
                }
            }
        }

        // collide demon with demon
        if (fixA.getUserData() != null && fixB.getUserData() != null) {
            if (fixA.getUserData().equals("demon") && fixB.getUserData().equals("demon")) {
                for ( Demon d : demons) {
                    if (d.getBody() == fixA.getBody()) {
                        d.setStopped(true);
                    }
                    if (d.getBody() == fixB.getBody()) {
                        d.setStopped(true);
                    }
                }
            }
        }

    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // collide demon with player
        if (fixA.getUserData() != null && fixB.getUserData() != null) {
            if (fixA.getUserData().equals("demon") || fixB.getUserData().equals("demon")) {
                Body demon = fixA.getUserData().equals("demon") ? fixA.getBody() : fixB.getBody();
                Fixture player = fixB.getUserData().equals("player") ? fixB : fixA;
                if (player.getUserData() == "player") {
                    for (Demon d : demons) {
                        if (d.getBody() == demon) {
                            d.setStopped(false);
                        }
                    }
                }
            }
        }

        // collide demonSensor with player
        if (fixA.getUserData() != null && fixB.getUserData() != null) {
            if (fixA.getUserData().equals("demonSensor") || fixB.getUserData().equals("demonSensor")) {
                Body demonSensor = fixA.getUserData().equals("demonSensor") ? fixA.getBody() : fixB.getBody();
                Fixture player = fixB.getUserData().equals("player") ? fixB : fixA;
                if (player.getUserData() == "player") {
                    this.player.setReached(false);
                    for (Demon d : demons) {
                        if (d.getBody() == demonSensor) {
                            d.setStopped(false);
                            d.setFighting(false);
                        }
                    }
                }
            }
        }

        // collide playerSensor with demon
        if (fixA.getUserData() != null && fixB.getUserData() != null) {
            if (fixA.getUserData().equals("demon") || fixB.getUserData().equals("demon")) {
                Body demon = fixA.getUserData().equals("demon") ? fixA.getBody() : fixB.getBody();
                Fixture player = fixB.getUserData().equals("playerSensor") ? fixB : fixA;
                if (player.getUserData() == "playerSensor") {
                    for ( Demon d : demons) {
                        if (d.getBody() == demon) {
                            d.setReached(false);
                        }
                    }
                }
            }
        }

        // collide demon with demon
        if (fixA.getUserData() != null && fixB.getUserData() != null) {
            if (fixA.getUserData().equals("demon") && fixB.getUserData().equals("demon")) {
                for ( Demon d : demons) {
                    if (d.getBody() == fixA.getBody()) {
                        d.setStopped(false);
                    }
                    if (d.getBody() == fixB.getBody()) {
                        d.setStopped(false);
                    }
                }
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public boolean areColliding(String firstId, String secondId) {
        return false;
    }
}
