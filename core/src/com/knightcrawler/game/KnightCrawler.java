package com.knightcrawler.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.knightcrawler.game.screens.MenuScreen;
import com.knightcrawler.game.screens.PlayScreen;

import java.util.Iterator;

public class KnightCrawler extends Game {

	public static final int GAME_WIDTH = 160;
	public static final int GAME_HEIGHT = 320;

    public SpriteBatch batch;

	@Override
	public void create() {
	    batch = new SpriteBatch();
	    setScreen(new MenuScreen(this));
	}

	@Override
    public void render() {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
    }

}
