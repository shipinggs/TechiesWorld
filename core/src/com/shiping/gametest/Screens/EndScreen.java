package com.shiping.gametest.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.shiping.gametest.Scenes.EndScreenScores;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by G751-4314 on 19/4/2016.
 */
public class EndScreen implements Screen {
    private SpriteBatch batch;
    private TechiesWorld game;
    private OrthographicCamera camera;

    private Stage stage; //** stage holds the Button **//


    private EndScreenScores endScreenScores;

    private BitmapFont font;
    private Texture calibriFontTexture;
    private TextureAtlas endScreenAtlas; //holds background image
    private TextButton endScreenBackground;
    private Skin endScreenSkin;
    private TextButton backToMenuBtn;

    public EndScreen(TechiesWorld game){
        this.game = game; // ** get Game parameter **//
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 512, 512);
        batch = new SpriteBatch();

        stage = new Stage();        //** window is stage **//
        stage.clear();
        Gdx.input.setInputProcessor(stage); //** stage is responsive **//

        endScreenScores =new EndScreenScores(game.batch);


        //font
        calibriFontTexture = new Texture(Gdx.files.internal("fonts/hudfont.png"));
        font = new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt"), new TextureRegion(calibriFontTexture), false);

        //background
        endScreenAtlas = new TextureAtlas("EndPage/endScreen.atlas");
        endScreenSkin = new Skin();
        endScreenSkin.addRegions(endScreenAtlas);
        TextButton.TextButtonStyle styleEndScreen = new TextButton.TextButtonStyle();
        styleEndScreen.up = endScreenSkin.getDrawable("endscreen");
        styleEndScreen.down = endScreenSkin.getDrawable("endscreen");
        styleEndScreen.font = font;
        endScreenBackground = new TextButton("", styleEndScreen);
        endScreenBackground.setPosition(0, 0);
        endScreenBackground.setHeight(Gdx.graphics.getHeight());
        endScreenBackground.setWidth(Gdx.graphics.getWidth());

        //Back to menu button
        TextButton.TextButtonStyle styleBackToMenu = new TextButton.TextButtonStyle();
        styleBackToMenu.up = endScreenSkin.getDrawable("BackToMenu");
        styleBackToMenu.down = endScreenSkin.getDrawable("BackToMenu");
        styleBackToMenu.font = font;
        backToMenuBtn = new TextButton("", styleBackToMenu);
        backToMenuBtn.setHeight((int) (Gdx.graphics.getWidth()/2/2.6));
        backToMenuBtn.setWidth(Gdx.graphics.getWidth() / 2);
        backToMenuBtn.setPosition(Gdx.graphics.getWidth() - (int)(backToMenuBtn.getWidth()/1.5),(int)((0-backToMenuBtn.getHeight())/2.2));


        game.getManager().get("audio/sounds/win.wav", Sound.class).play();
    }
    @Override
    public void show() {
        stage.addActor(endScreenBackground);

        backToMenuBtn.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                Gdx.app.log("backToMenuBtn", "Pressed");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game));
                return true;

            }

        });

        //stage.addActor(backToMenuBtn);

    }
    public void update(float delta){
        endScreenScores.update(delta);
    }


    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1); // rgba. clear screen with black
        Gdx.gl.glClear((GL20.GL_COLOR_BUFFER_BIT));

        stage.act();

        batch.setProjectionMatrix(camera.combined);
        stage.draw();
        endScreenScores.stage.draw();
        batch.begin();

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
        font.dispose();
        endScreenAtlas.dispose();
        endScreenSkin.dispose();
        calibriFontTexture.dispose();
        stage.dispose();

    }
}
