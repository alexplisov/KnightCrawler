package com.knightcrawler.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.knightcrawler.game.screens.PlayScreen;

import java.util.List;

import static com.knightcrawler.game.screens.PlayScreen.hud;

/**
 * Created by alexp on 04.04.2017.
 */
public class Player extends Sprite {

    private boolean hitted;
    private boolean fighting;
    private boolean reached;

    // States
    public enum State { STANDING, WALKING, SIDEWALKING, FIGHTING, HIT};
    public State currentState;
    public State previousState;

    // Graphics
    private TextureRegion knightStand;
    private Animation playerWalk;
    private Animation playerSidewalk;
    private Animation playerStand;
    private Animation playerFight;
    private Animation playerHit;

    // Entities
    public World world;
    public Body body;
    private List<Demon> demons;

    private float stateTimer;
    Array<TextureRegion> frames;

    public Player(World world, PlayScreen playScreen, List<Demon> demons) {
        super(playScreen.getTextureAtlas().findRegion("kc-spritesheet"));
        this.demons = demons;

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;

        hitted = false;
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
                new Vector2(13,23),
                new Vector2(-13,8),
                new Vector2(13,8)
        });
        fixtureDef.shape = swordSensor;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData("playerSensor");

        knightStand = new TextureRegion(getTexture(), 0, 0, 32, 32);
        setBounds(0,0,32,32);
        setRegion(knightStand);
    }

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

        // hit animation
        loadFramesFromSpritesheets(0,2,32,128,32,32);
        playerHit = new Animation(0.1f, frames);
        frames.clear();
    }

    public void update(float delta) {
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
                region = (TextureRegion) playerFight.getKeyFrame(stateTimer, true);
                if (playerFight.isAnimationFinished(stateTimer)) {
                    fighting = false;
                }
                break;
            case HIT:
                region = (TextureRegion) playerHit.getKeyFrame(stateTimer, true);
                if (playerFight.isAnimationFinished(stateTimer)) {
                    hitted = false;
                    if (hud.getHealth() > 0) hud.setHealth(hud.getHealth() - 1);
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

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.J) && !fighting) {
            body.setLinearVelocity(new Vector2(0, 0));
            fighting = true;
            for ( Demon d : demons) {
                if (d.isReached()) {
                    d.setDying(true);
                    hud.setScore(hud.getScore() + 25);
                }
            }
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

    public boolean isFighting() {
        return fighting;
    }

    private State getState() {
        if (hitted) {
            return State.HIT;
        } else if (fighting && !hitted) {
            return State.FIGHTING;
        } else if (body.getLinearVelocity().y != 0 && !hitted) {
            return State.WALKING;
        } else if (body.getLinearVelocity().x != 0 && !hitted) {
            return State.SIDEWALKING;
        } else {
            return State.STANDING;
        }
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }

    public boolean isReached() {
        return reached;
    }

    public boolean isHitted() {
        return hitted;
    }

    public void setHitted(boolean hitted) {
        this.hitted = hitted;
    }
}
