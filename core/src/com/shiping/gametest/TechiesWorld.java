package com.shiping.gametest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.shiping.gametest.Screens.LoginScreen;

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
	public static final short COIN_BIT = 32;
	public static final short RESPAWN_BIT = 64;
	public static final short TUTCOIN_BIT = 128;
	public static final short TUTMINE_BIT = 256;

	public SpriteBatch batch;
	private AssetManager manager;

	public static PlayServices playServices;

	public TechiesWorld(PlayServices playServices){
		this.playServices=playServices;
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/music/bgm1.ogg", Music.class);
		manager.load("audio/music/bgm2.ogg", Music.class);
		manager.load("audio/sounds/mine.wav", Sound.class);
		manager.load("audio/sounds/pickup2.wav", Sound.class);
		manager.load("audio/sounds/respawn.wav", Sound.class);
		manager.load("audio/sounds/slime2.wav", Sound.class);
		manager.load("audio/sounds/die2.wav", Sound.class);
		manager.load("audio/sounds/explosion.wav", Sound.class);
		manager.load("audio/sounds/explosion2.wav", Sound.class);
		manager.load("audio/sounds/win.wav", Sound.class);
		manager.finishLoading();

		setScreen(new LoginScreen(this));
	}

	public AssetManager getManager() {
		return manager;
	}

	@Override
	public void dispose() {
		super.dispose();
		manager.dispose();
		batch.dispose();
	}

	@Override
	public void render () {
		super.render(); // delegate render method to active screen
	}
}
