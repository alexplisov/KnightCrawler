package com.knightcrawler.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.knightcrawler.game.KnightCrawler;
import com.knightcrawler.game.entities.Demon;
import com.knightcrawler.game.entities.Player;
import com.knightcrawler.game.tools.WorldContactListener;

/**
 * Created by alexp on 03.04.2017.
 */
public class PlayScreen implements Screen {

    // Graphics & rendering
    private OrthographicCamera gameCamera;
    private Viewport gameViewport;
    private TextureAtlas textureAtlas;
    private TmxMapLoader tmxMapLoader;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private Box2DDebugRenderer box2DDebugRenderer;

    // Entities
    private KnightCrawler game;
    private World world;
    private Player player;
    private Demon demon;

    // Screen constructor
    public PlayScreen(KnightCrawler game) {
        this.game = game;

        textureAtlas = new TextureAtlas("Knight.pack");

        gameCamera = new OrthographicCamera(KnightCrawler.GAME_WIDTH, KnightCrawler.GAME_HEIGHT);
        gameViewport = new FitViewport(KnightCrawler.GAME_WIDTH, KnightCrawler.GAME_HEIGHT, gameCamera);

        tmxMapLoader = new TmxMapLoader();
        tiledMap = tmxMapLoader.load("Level.tmx");
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        gameCamera.position.set(gameViewport.getWorldWidth() / 2, gameViewport.getWorldHeight() / 2, 0);

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

        player = new Player(world, this);
        demon = new Demon(world);

    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    // Controlls for PlayScreen
    public void handleInput(float delta) {
        //TODO: place input logic here
    }

    // Logic updates
    public void update(float delta) {
        handleInput(delta);
        world.step(1/60f, 6, 2);
        player.update(delta);
        demon.update(delta);
        gameCamera.update();
        orthogonalTiledMapRenderer.setView(gameCamera);

        world.setContactListener(new WorldContactListener(player));
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

        // Debug rendering
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
}
