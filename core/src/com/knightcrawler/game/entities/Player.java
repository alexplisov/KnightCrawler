package com.knightcrawler.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.knightcrawler.game.KnightCrawler;
import com.knightcrawler.game.screens.PlayScreen;

/**
 * Created by alexp on 04.04.2017.
 */
public class Player extends Sprite {

    // States
    public enum State { STANDING, WALKING, SIDEWALKING, FIGHTING};
    public State currentState;
    public State previousState;

    // Graphics
    private TextureRegion knightStand;
    private Animation playerWalk;
    private Animation playerSidewalk;
    private Animation playerStand;
    private Animation playerFight;

    // Entities
    public World world;
    public Body body;

    private float stateTimer;
    private boolean fighting;
    private boolean isStopped;
    Array<TextureRegion> frames;

    public void loadFramesFromSpritesheets(int startSprite, int numberOfSprites, int step,
                                           int yOffset, int width, int height) {
        for (int i = startSprite; i < numberOfSprites; i++) {
            frames.add(new TextureRegion(getTexture(), i * step, yOffset, width, height));
        }
    }

    public void createAnimationsFromFrames() {
        // standing animation
        loadFramesFromSpritesheets(0,2,32,0,32,32);
        playerStand = new Animation(0.4f, frames);
        frames.clear();

        // walking animation
        loadFramesFromSpritesheets(0,4,32,32,32,32);
        playerWalk = new Animation(0.1f, frames);
        frames.clear();

        // sidewalking animation
        loadFramesFromSpritesheets(0,2,32,64,32,32);
        playerSidewalk = new Animation(0.1f, frames);
        frames.clear();

        // fighting animation
        loadFramesFromSpritesheets(0,3,64,96,64,32);
        playerFight = new Animation(0.1f, frames);
        frames.clear();
    }

    public boolean isFighting() {
        return fighting;
    }

    public Player(World world, PlayScreen playScreen) {
        super(playScreen.getTextureAtlas().findRegion("kc-spritesheet"));

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;

        fighting = false;

        frames = new Array<TextureRegion>();
        createAnimationsFromFrames();

        // Creating main box2d
        this.world = world;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(80, 16);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(8);

        fixtureDef.shape = circleShape;
        body.createFixture(fixtureDef).setUserData("player");

        PolygonShape swordSensor = new PolygonShape();
        swordSensor.set(new Vector2[] {
                new Vector2(-13,23),
                new Vector2(19,23),
                new Vector2(-13,8),
                new Vector2(19,8)
        });
        fixtureDef.shape = swordSensor;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData("sword");

        knightStand = new TextureRegion(getTexture(), 0, 0, 32, 32);
        setBounds(0,0,32,32);
        setRegion(knightStand);
    }

    public void update(float delta) {
        if (isStopped)
            body.setLinearVelocity(new Vector2(0, 0));
        if (!fighting)
            setBounds(0,0,32,32);
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2 + 8);

        handleInput();
        setRegion(getFrame(delta));
    }

    public TextureRegion getFrame(float delta) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case WALKING:
                region = (TextureRegion) playerWalk.getKeyFrame(stateTimer, true);
                break;
            case SIDEWALKING:
                region = (TextureRegion) playerSidewalk.getKeyFrame(stateTimer, true);
                break;
            case FIGHTING:
                setBounds(0,0,64,32);
                setPosition(body.getPosition().x - getWidth() / 4, body.getPosition().y - getHeight() / 2 + 8);
                region = (TextureRegion) playerFight.getKeyFrame(stateTimer);
                if (playerFight.isAnimationFinished(stateTimer)) {
                    fighting = false;
                }
                break;
            default:
                region = (TextureRegion) playerStand.getKeyFrame(stateTimer, true);
                break;
        }

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;
    }

    private State getState() {
        if (fighting) {
            return State.FIGHTING;
        } else if (body.getLinearVelocity().y != 0) {
            return State.WALKING;
        } else if (body.getLinearVelocity().x != 0) {
            return State.SIDEWALKING;
        } else {
            return State.STANDING;
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.J) && !fighting) {
            body.setLinearVelocity(new Vector2(0, 0));
            fighting = true;
            stateTimer = 0;
        } else if (!fighting && (Gdx.input.isKeyPressed(Input.Keys.W) ||
                Gdx.input.isKeyPressed(Input.Keys.S) ||
                Gdx.input.isKeyPressed(Input.Keys.A) ||
                Gdx.input.isKeyPressed(Input.Keys.D))) {
            // movement
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                body.setLinearVelocity(new Vector2(0, 100));
                body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                body.setLinearVelocity(new Vector2(0, -100));
                body.applyLinearImpulse(new Vector2(0, -4f), body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                body.setLinearVelocity(new Vector2(-100, 0));
                body.applyLinearImpulse(new Vector2(-4f, 0), body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                body.setLinearVelocity(new Vector2(100, 0));
                body.applyLinearImpulse(new Vector2(4f, 0), body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W) && Gdx.input.isKeyPressed(Input.Keys.A)) {
                body.setLinearVelocity(new Vector2(-100, 70));
                body.applyLinearImpulse(new Vector2(-4f, 4f), body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W) && Gdx.input.isKeyPressed(Input.Keys.D)) {
                body.setLinearVelocity(new Vector2(100, 70));
                body.applyLinearImpulse(new Vector2(4f, 4f), body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) && Gdx.input.isKeyPressed(Input.Keys.A)) {
                body.setLinearVelocity(new Vector2(-100, -70));
                body.applyLinearImpulse(new Vector2(-4f, -4f), body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) && Gdx.input.isKeyPressed(Input.Keys.D)) {
                body.setLinearVelocity(new Vector2(100, -70));
                body.applyLinearImpulse(new Vector2(4f, -4f), body.getWorldCenter(), true);
            }
        } else {
            body.setLinearVelocity(new Vector2(0, 0));
        }
    }

    public void onCollision() {
        isStopped = true;
    }

    public void onRelease() {
        isStopped = false;
    }

}
