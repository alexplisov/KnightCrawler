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

/**
 * Created by alexp on 05.04.2017.
 */
public class Demon extends Sprite {

    private boolean dying;
    private boolean fighting;
    private boolean isStopped = false;
    private boolean reached;
    private boolean canDamage;
    private boolean removable;
    private float timer = 0;
    private int xSpawn;
    private int ySpawn;

    // States
    public enum State { WALKING, FIGHTING, DYING, STANDING};
    public Demon.State currentState;
    public Demon.State previousState;

    // Graphics
    private Animation demonStand;
    private Animation demonWalk;
    private Animation demonFight;
    private Animation demonDie;
    private Array<TextureRegion> frames;

    public float stateTimer;
    private Player player;
    public World world;
    public Body body;

    public Demon(World world, PlayScreen playScreen, Player player, int x, int y) {
        super(playScreen.getTextureAtlas().findRegion("kc-spritesheet"));
        this.player = player;

        currentState = State.WALKING;
        previousState = State.WALKING;
        stateTimer = 0;

        fighting = false;
        canDamage = true;
        removable = false;

        frames = new Array<TextureRegion>();
        createAnimationsFromFrames();

        // box2d body
        this.world = world;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(8);

        fixtureDef.shape = circleShape;
        body.createFixture(fixtureDef).setUserData("demon");

        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(new Vector2(-8, -14), new Vector2(8, -14));

        fixtureDef.shape = edgeShape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData("demonSensor");


        setBounds(0,0,32,32);
    }

    public void loadFramesFromSpritesheets(int startSprite, int numberOfSprites, int step,
                                           int yOffset, int width, int height) {
        for (int i = startSprite; i < numberOfSprites; i++) {
            frames.add(new TextureRegion(getTexture(), i * step, yOffset, width, height));
        }
    }

    public void createAnimationsFromFrames() {
        // standing animation
        loadFramesFromSpritesheets(0,2,32,160,32,32);
        demonStand = new Animation(0.2f, frames);
        frames.clear();

        // walking animation
        loadFramesFromSpritesheets(0,4,32,192,32,32);
        demonWalk = new Animation(0.1f, frames);
        frames.clear();

        // fighting animation
        loadFramesFromSpritesheets(0,3,32,224,32,32);
        demonFight = new Animation(0.2f, frames);
        frames.clear();

        // dying animation
        loadFramesFromSpritesheets(0,2,32,256,32,32);
        demonDie = new Animation(0.2f, frames);
        frames.clear();
    }

    public void update(float delta) {
        if (!dying && !isStopped) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.J) && !player.isFighting()) {
                body.setLinearVelocity(new Vector2(0, -120));
                body.applyLinearImpulse(new Vector2(0, -1f), body.getWorldCenter(), true);
            } else {
                body.setLinearVelocity(new Vector2(0, -60));
                body.applyLinearImpulse(new Vector2(0, -1f), body.getWorldCenter(), true);
            }
        } else {
            body.setLinearVelocity(new Vector2(0, 0));
        }
        if (fighting && player.isReached() && !player.isFighting() && canDamage) {
            player.setHitted(true);
            canDamage = false;
        }

        // attack timer
        if (!fighting && player.isReached()) {
            timer += delta;
            if (timer >= 1) {
                fighting = true;
                timer = 0;
                stateTimer = 0;
                canDamage = true;
            }
        }
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2 + 8);
        setRegion(getFrame(delta));
    }

    public TextureRegion getFrame(float delta) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case WALKING:
                region = (TextureRegion) demonWalk.getKeyFrame(stateTimer, true);
                break;
            case FIGHTING:
                region = (TextureRegion) demonFight.getKeyFrame(stateTimer, false);
                if (demonFight.isAnimationFinished(stateTimer)) {
                    if (stateTimer != 0) fighting = false;
                }
                break;
            case DYING:
                region = (TextureRegion) demonDie.getKeyFrame(stateTimer, false);
                if (demonDie.isAnimationFinished(stateTimer)) {
                    removable = true;
                }
                break;
            default:
                region = (TextureRegion) demonStand.getKeyFrame(stateTimer, true);
                break;
        }

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;
    }

    private Demon.State getState() {
        if (dying) {
            Array<Fixture> fixtures = body.getFixtureList();
            for (Fixture fixture : fixtures) {
                body.destroyFixture(fixture);
            }
            return State.DYING;
        } else if (fighting && isStopped && !dying) {
            return State.FIGHTING;
        } else if (!fighting && !isStopped && !dying) {
            return State.WALKING;
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

    public Body getBody() {
        return body;
    }

    public void setDying(boolean dying) {
        stateTimer = 0;
        this.dying = dying;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public void setFighting(boolean fighting) {
        this.fighting = fighting;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

}
