package com.shiping.gametest.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.shiping.gametest.TechiesWorld;

/**
 * Created by G751-4314 on 31/3/2016.
 */
public class LoginScreen implements Screen {
    private SpriteBatch batch;
    private TechiesWorld game;
    private OrthographicCamera camera;

    private Stage stage; //** stage holds the Button **//
    private TextButton loginBtn;
    private Skin loginScreenSkin; //skin to access drawables to login screen UI elements
    private TextButton gameTitle;
    private BitmapFont font;
    private Texture calibriFontTexture;
    private TextureAtlas loginScreenAtlas; //** Holds the entire image for login button and game title**//
    private TextureAtlas menuLoginBackgroundAtlas; //holds background image
    private TextButton menuLoginBackground;
    private Skin menuLoginBackgroundSkin;



    public LoginScreen(TechiesWorld game){
        Gdx.app.log("Login Screen", "constructor called");
        this.game = game; // ** get Game parameter **//
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 512, 512);
        batch = new SpriteBatch();

        stage = new Stage();        //** window is stage **//
        stage.clear();
        Gdx.input.setInputProcessor(stage); //** stage is responsive **//

        //font, reserved for future use
        calibriFontTexture = new Texture(Gdx.files.internal("fonts/hudfont.png"));
        font = new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt"), new TextureRegion(calibriFontTexture), false);
        font.setColor(0,0,1,1); //** Blue text **//

        //background
        menuLoginBackgroundAtlas = new TextureAtlas("Background/background.atlas");
        menuLoginBackgroundSkin = new Skin();
        menuLoginBackgroundSkin.addRegions(menuLoginBackgroundAtlas);
        TextButton.TextButtonStyle styleBackground = new TextButton.TextButtonStyle();
        styleBackground.up = menuLoginBackgroundSkin.getDrawable("Background");
        styleBackground.down = menuLoginBackgroundSkin.getDrawable("Background");
        styleBackground.font = font;
        menuLoginBackground = new TextButton("", styleBackground);
        menuLoginBackground.setPosition(0, 0);
        menuLoginBackground.setHeight(Gdx.graphics.getHeight());
        menuLoginBackground.setWidth(Gdx.graphics.getWidth());

        //button
        loginScreenAtlas = new TextureAtlas("LoginPage/loginscreenitems.atlas");
        loginScreenSkin = new Skin();
        loginScreenSkin.addRegions(loginScreenAtlas);

        TextButton.TextButtonStyle styleLogin = new TextButton.TextButtonStyle(); //** Button properties **//
        styleLogin.up = loginScreenSkin.getDrawable("Login");
        styleLogin.down = loginScreenSkin.getDrawable("Login");
        styleLogin.font = font;
        loginBtn = new TextButton("",styleLogin); //empty string since text is already on button
        int buttonX = (int)(Gdx.graphics.getWidth()/1.75) - (int)loginBtn.getWidth()/2;
        int buttonY = Gdx.graphics.getHeight()/8;
        loginBtn.setPosition(buttonX, 2*buttonY); //** Button location **//


        //Game title
        TextButton.TextButtonStyle styleTitle = new TextButton.TextButtonStyle(); //** Button properties **//
        styleTitle.up = loginScreenSkin.getDrawable("Titlesmall");
        styleTitle.down = loginScreenSkin.getDrawable("Titlesmall");
        styleTitle.font = font;
        gameTitle = new TextButton("",styleTitle); //empty string since text is already on button
        gameTitle.setHeight(Gdx.graphics.getHeight() * 3 / 8);
        gameTitle.setWidth(Gdx.graphics.getHeight() * 3 / 4);
        int titleX = Gdx.graphics.getWidth()/2 - (int) gameTitle.getWidth()/2;
        gameTitle.setPosition(titleX, 5 * buttonY); //** Button location **//
    }

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        stage.addActor(menuLoginBackground);
        stage.addActor(gameTitle);

        loginBtn.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                /*
                THINGS TO DO WHEN LOGIN BUTTON IS PRESSED
                 */
                TechiesWorld.playServices.signIn();
                Gdx.app.log("Logout button", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                /*
                THINGS TO DO WHEN LOGIN BUTTON IS RELEASED
                 */
                Gdx.app.log("Logout button", "Released");
                Gdx.app.log("isSignedIn", "" + TechiesWorld.playServices.isSignedIn());

            }
        });

        stage.addActor(loginBtn);

    }

    public void update(float delta){
        if (TechiesWorld.playServices.isSignedIn()){
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game));
        }
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 99, 1); // rgba. clear screen with blue
        Gdx.gl.glClear((GL20.GL_COLOR_BUFFER_BIT));

        stage.act();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        stage.draw();
        batch.end();

    }



    /**
     * @param width
     * @param height
     * @see //ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * @see //ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see //ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        calibriFontTexture.dispose();
        font.dispose();
        loginScreenAtlas.dispose();
        loginScreenSkin.dispose();
        menuLoginBackgroundAtlas.dispose();
        menuLoginBackgroundSkin.dispose();

    }
}

