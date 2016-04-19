package com.shiping.gametest.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.shiping.gametest.Scenes.Hud2;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by G751-4314 on 19/4/2016.
 */
public class EndScreen implements Screen {
    private SpriteBatch batch;
    private TechiesWorld game;
    private OrthographicCamera camera;

    private Stage stage; //** stage holds the Button **//


    private Hud2 hud2;

    public EndScreen(TechiesWorld game){
        this.game = game; // ** get Game parameter **//
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 512, 512);
        batch = new SpriteBatch();

        stage = new Stage();        //** window is stage **//
        stage.clear();
        Gdx.input.setInputProcessor(stage); //** stage is responsive **//

        hud2=new Hud2(game.batch);
    }
    @Override
    public void show() {

    }
    public void update(float delta){
        hud2.update(delta);
    }


    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 99, 1); // rgba. clear screen with blue
        Gdx.gl.glClear((GL20.GL_COLOR_BUFFER_BIT));

        stage.act();

        batch.setProjectionMatrix(camera.combined);
        hud2.stage.draw();
        batch.begin();
        stage.draw();
        batch.end();

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
