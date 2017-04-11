package com.knightcrawler.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.knightcrawler.game.KnightCrawler;
import com.knightcrawler.game.entities.Demon;
import com.knightcrawler.game.entities.Player;
import com.knightcrawler.game.scenes.Hud;
import com.knightcrawler.game.tools.WorldContactListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by alexp on 03.04.2017.
 */
public class PlayScreen implements Screen {

    private boolean debugMode;

    // Graphics & rendering
    private Box2DDebugRenderer box2DDebugRenderer;
    public static Hud hud;
    private OrthographicCamera gameCamera;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TextureAtlas textureAtlas;
    private TmxMapLoader tmxMapLoader;
    private TiledMap tiledMap;
    private Viewport gameViewport;

    // Entities
    private Demon demon;
    private KnightCrawler game;
    private List<Demon> demons;
    private Player player;
    private World world;

    private WorldContactListener worldContactListener;

    // Screen constructor
    public PlayScreen(KnightCrawler game) {
        this.game = game;

        textureAtlas = new TextureAtlas("kc-spritesheet.pack");

        gameCamera = new OrthographicCamera(KnightCrawler.GAME_WIDTH, KnightCrawler.GAME_HEIGHT);
        gameViewport = new FitViewport(KnightCrawler.GAME_WIDTH, KnightCrawler.GAME_HEIGHT, gameCamera);

        tmxMapLoader = new TmxMapLoader();
        tiledMap = tmxMapLoader.load("Level.tmx");
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        gameCamera.position.set(gameViewport.getWorldWidth() / 2, gameViewport.getWorldHeight() / 2, 0);
        hud = new Hud(game.batch);

        world = new World(new Vector2(0,0), true);
        box2DDebugRenderer = new Box2DDebugRenderer();

        BodyDef bodyDef = new BodyDef();
        PolygonShape polygonShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        for(MapObject object : tiledMap.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2);

            body = world.createBody(bodyDef);

            polygonShape.setAsBox(rectangle.getWidth() / 2, rectangle.getHeight() / 2 );
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
        }


        demons = new LinkedList<Demon>();
        player = new Player(world, this, demons);
        demon = new Demon(world, this, player);
        demons.add(demon);

        worldContactListener = new WorldContactListener(demons, player);
        debugMode = false;
    }

    // Controlls for PlayScreen
    public void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DEL))
            debugMode = !debugMode;
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) && hud.getHealth() < 6)
            hud.setHealth(hud.getHealth() + 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.M) && hud.getHealth() > 0)
            hud.setHealth(hud.getHealth() - 1);
    }

    // Logic updates
    public void update(float delta) {
        handleInput(delta);
        world.step(1/60f, 6, 2);
        player.update(delta);
        demon.update(delta);
        hud.update(delta);
        gameCamera.update();
        orthogonalTiledMapRenderer.setView(gameCamera);

        world.setContactListener(worldContactListener);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Logic updates
        update(delta);

        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render game map
        orthogonalTiledMapRenderer.render();



        // Render sprites
        game.batch.setProjectionMatrix(gameCamera.combined);
        game.batch.begin();
        demon.draw(game.batch);
        player.draw(game.batch);
        game.batch.end();

        // Render hud
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // Debug rendering
        if (debugMode)
            box2DDebugRenderer.render(world, gameCamera.combined);
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        game.dispose();
        tiledMap.dispose();
        orthogonalTiledMapRenderer.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
        textureAtlas.dispose();
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }
}
