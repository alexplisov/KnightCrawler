package com.knightcrawler.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.knightcrawler.game.screens.PlayScreen;

/**
 * Created by alexp on 05.04.2017.
 */
public class Demon extends Sprite {

    private Texture texture;
    public World world;
    public Body body;

    private boolean isReach = false;
    private boolean isStopped = false;

    public Demon(World world) {
        this.world = world;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(80, 64);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(8);

        fixtureDef.shape = circleShape;
        body.createFixture(fixtureDef).setUserData(this);

        texture = new Texture("demon-sprite.png");
        setBounds(0,0,32,32);
        setRegion(texture);
    }

    public void onCollision() {
        isStopped = true;
    }

    public void onRelease() {
        isStopped = false;
    }

    public void update(float delta) {
        if (!isStopped) {
            body.setLinearVelocity(new Vector2(0, -60));
            body.applyLinearImpulse(new Vector2(0, -1f), body.getWorldCenter(), true);
        } else {
            body.setLinearVelocity(new Vector2(0, 0));
        }
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2 + 8);
        setRegion(texture);
    }

    public void isReach(boolean bool) {
        isReach = bool ? true : false;
    }
}
