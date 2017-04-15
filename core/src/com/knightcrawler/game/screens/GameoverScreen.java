package com.knightcrawler.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.knightcrawler.game.KnightCrawler;

import static com.knightcrawler.game.screens.PlayScreen.hud;

/**
 * Created by alexp on 03.04.2017.
 */
public class GameoverScreen implements Screen {

    private OrthographicCamera orthographicCamera;
    private KnightCrawler game;
    private Viewport viewport;
    private Stage stage;
    private Label gameOver;
    private Label retry;

    public GameoverScreen(KnightCrawler game) {
        this.game = game;
        orthographicCamera = new OrthographicCamera(800, 480);

        viewport = new FitViewport(800, 480, orthographicCamera);
        stage = new Stage(viewport, game.batch);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        gameOver = new Label("SCORE: " + hud.getScore(), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        retry = new Label("Press any key to restart the game", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(gameOver);
        table.add().row();
        table.add(retry);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.batch.begin();
        game.batch.end();

        game.batch.setProjectionMatrix(stage.getCamera().combined);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            game.setScreen(new PlayScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {

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

    }
}
