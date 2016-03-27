package com.shiping.gametest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.shiping.gametest.Screens.PlayScreen;

public class TechiesWorld extends Game {
	public static final int V_WIDTH = 1200;
	public static final int V_HEIGHT = 624;
	public static final float PPM = 500;

	public static final short NOTHING_BIT = 0;
	public static final short WALL_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short MINE_BIT = 4;
	public static final short EXPLODED_BIT = 8;
	public static final short ENEMY_BIT = 16;

	public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render(); // delegate render method to active screen
	}
}
