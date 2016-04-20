package com.shiping.gametest.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by ACER- on 20/4/2016.
 */
public class LoadingScreen implements Screen {
    private SpriteBatch batch;
    final TechiesWorld game;
    private OrthographicCamera camera;
    private Stage stage; //** stage holds the Button **//
    private BitmapFont font; //** same as that used in Tut 7 **//
    private Texture calibriFontTexture;
    private TextureAtlas loadingBackgroundAtlas;
    private TextButton loadingBackground;
    private Skin loadingBackgroundSkin;

    public LoadingScreen(TechiesWorld game){
        this.game = game; // ** get Game parameter **//
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 512, 512);
        batch = new SpriteBatch();

        stage = new Stage();        //** window is stage **//
        stage.clear();
        Gdx.input.setInputProcessor(stage); //** stage is responsive **//

        //font
        calibriFontTexture = new Texture(Gdx.files.internal("fonts/hudfont.png"));
        font = new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt"), new TextureRegion(calibriFontTexture), false);

        //background
        loadingBackgroundAtlas = new TextureAtlas("EndPage/endScreen.atlas");
        loadingBackgroundSkin = new Skin();
        loadingBackgroundSkin.addRegions(loadingBackgroundAtlas);
        TextButton.TextButtonStyle styleEndScreen = new TextButton.TextButtonStyle();
        styleEndScreen.up = loadingBackgroundSkin.getDrawable("endscreen");
        styleEndScreen.down = loadingBackgroundSkin.getDrawable("endscreen");
        styleEndScreen.font = font;
        loadingBackground = new TextButton("", styleEndScreen);
        loadingBackground.setPosition(0, 0);
        loadingBackground.setHeight(Gdx.graphics.getHeight());
        loadingBackground.setWidth(Gdx.graphics.getWidth());
    }


    public void update(){

    }

    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {

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
