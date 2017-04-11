package com.knightcrawler.game.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.knightcrawler.game.KnightCrawler;
import com.knightcrawler.game.screens.PlayScreen;

/**
 * Created by alexp on 10.04.2017.
 */
public class Hud {

    private int health;
    private int score;

    public Stage stage;
    private Viewport viewport;
    private Image heartImage1, heartImage2, heartImage3;
    private TextureRegion heartImage, halfHeartImage, heartShadowImage;
    private Label scores;


    public Hud(SpriteBatch spriteBatch) {
        health = 6;
        score = 0;

        viewport = new FitViewport(KnightCrawler.GAME_WIDTH, KnightCrawler.GAME_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        heartImage = new TextureRegion(new Texture("kc-spritesheet.png"), 0, 288, 32, 32);
        halfHeartImage = new TextureRegion(new Texture("kc-spritesheet.png"), 32, 288, 32, 32);
        heartShadowImage = new TextureRegion(new Texture("kc-spritesheet.png"), 64, 288, 32, 32);
        scores = new Label(String.format("%03d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        heartImage1 = new Image(heartImage);
        heartImage2 = new Image(heartImage);
        heartImage3 = new Image(heartImage);

        table.add(heartImage1);
        table.add(heartImage2);
        table.add(heartImage3);
        table.add(scores);

        stage.addActor(table);
    }

    public void update(float delta) {
        scores.setText(String.format("%03d", score));
        switch(health) {
            case 5:
                heartImage1.setDrawable(new TextureRegionDrawable(heartImage));
                heartImage2.setDrawable(new TextureRegionDrawable(heartImage));
                heartImage3.setDrawable(new TextureRegionDrawable(halfHeartImage));
                break;
            case 4:
                heartImage1.setDrawable(new TextureRegionDrawable(heartImage));
                heartImage2.setDrawable(new TextureRegionDrawable(heartImage));
                heartImage3.setDrawable(new TextureRegionDrawable(heartShadowImage));
                break;
            case 3:
                heartImage1.setDrawable(new TextureRegionDrawable(heartImage));
                heartImage2.setDrawable(new TextureRegionDrawable(halfHeartImage));
                heartImage3.setDrawable(new TextureRegionDrawable(heartShadowImage));
                break;
            case 2:
                heartImage1.setDrawable(new TextureRegionDrawable(heartImage));
                heartImage2.setDrawable(new TextureRegionDrawable(heartShadowImage));
                heartImage3.setDrawable(new TextureRegionDrawable(heartShadowImage));
                break;
            case 1:
                heartImage1.setDrawable(new TextureRegionDrawable(halfHeartImage));
                heartImage2.setDrawable(new TextureRegionDrawable(heartShadowImage));
                heartImage3.setDrawable(new TextureRegionDrawable(heartShadowImage));
                break;
            case 0:
                heartImage1.setDrawable(new TextureRegionDrawable(heartShadowImage));
                heartImage2.setDrawable(new TextureRegionDrawable(heartShadowImage));
                heartImage3.setDrawable(new TextureRegionDrawable(heartShadowImage));
                break;
            default:
                heartImage1.setDrawable(new TextureRegionDrawable(heartImage));
                heartImage2.setDrawable(new TextureRegionDrawable(heartImage));
                heartImage3.setDrawable(new TextureRegionDrawable(heartImage));
                break;
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
