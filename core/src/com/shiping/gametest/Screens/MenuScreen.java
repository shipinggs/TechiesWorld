package com.shiping.gametest.Screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.shiping.gametest.TechiesWorld;
import com.badlogic.gdx.Game;

/**
 * Created by ACER- on 31/3/2016.
 */
public class MenuScreen implements Screen {
    private SpriteBatch batch;
    final TechiesWorld game;
    private OrthographicCamera camera;
    private Stage stage; //** stage holds the Button **//
    private TextButton logoutBtn;
    private TextButton startGameBtn;
    private TextButton invitationBoxBtn;
    private TextButton tutorialBtn;
    private Skin buttonSkin; //** images are used as skins of the button **//
    private BitmapFont font; //** same as that used in Tut 7 **//
    private Texture calibriFontTexture;
    private TextureAtlas buttonsAtlas; //** Holds the entire image for all buttons **//
    private TextureAtlas menuLoginBackgroundAtlas;
    private TextButton menuLoginBackground;
    private Skin menuLoginBackgroundSkin;
    private Skin loginScreenSkin; //** images are used as skins of the button **//
    private TextButton gameTitle;
    private TextureAtlas loginScreenAtlas; //** Holds the entire image for login button and game title**//
    private Music music;


    public MenuScreen(TechiesWorld game){
        Gdx.app.log("Menu Screen", "constructor called");
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
        font.setColor(0, 0, 1, 1); //** Blue text **//

        //background
        menuLoginBackgroundAtlas = new TextureAtlas("Background/background.atlas");
        menuLoginBackgroundSkin = new Skin();
        menuLoginBackgroundSkin.addRegions(menuLoginBackgroundAtlas);
        TextButton.TextButtonStyle styleBackground = new TextButton.TextButtonStyle();
        styleBackground.up = menuLoginBackgroundSkin.getDrawable("Background");
        styleBackground.down = menuLoginBackgroundSkin.getDrawable("Background");
        styleBackground.font = font;
        menuLoginBackground = new TextButton("", styleBackground);
        menuLoginBackground.setPosition(0,0);
        menuLoginBackground.setHeight(Gdx.graphics.getHeight());
        menuLoginBackground.setWidth(Gdx.graphics.getWidth());


        //button
        buttonsAtlas = new TextureAtlas("MenuPage/menuscreenbuttons.atlas");
        buttonSkin = new Skin();
        buttonSkin.addRegions(buttonsAtlas);

        TextButton.TextButtonStyle styleLogout = new TextButton.TextButtonStyle(); //** Button properties **//
        styleLogout.up = buttonSkin.getDrawable("Logout");
        styleLogout.down = buttonSkin.getDrawable("Logout");
        styleLogout.font = font;
        logoutBtn = new TextButton("",styleLogout); //empty string since text is already on button
        int buttonX = (int)(Gdx.graphics.getWidth()/1.75) - (int)logoutBtn.getWidth()/2;
        int buttonY = Gdx.graphics.getHeight()/8;
        logoutBtn.setPosition(buttonX, buttonY); //** Button location **//
        //logoutBtn.setHeight(51); //** Button Height **//
        //logoutBtn.setWidth(400); //** Button Width **//

        TextButton.TextButtonStyle styleStartGame = new TextButton.TextButtonStyle(); //** Button properties **//
        styleStartGame.up = buttonSkin.getDrawable("Quickgame");
        styleStartGame.down = buttonSkin.getDrawable("Quickgame");
        styleStartGame.font = font;
        startGameBtn = new TextButton("",styleStartGame); //empty string since text is already on button
        startGameBtn.setPosition(buttonX, 2*buttonY); //** Button location **//
        //startGameBtn.setHeight(200); //** Button Height **//
        //startGameBtn.setWidth(400); //** Button Width **//


        TextButton.TextButtonStyle styleInvitationBox=new TextButton.TextButtonStyle();
        styleInvitationBox.up=buttonSkin.getDrawable("InvitePlayers");
        styleInvitationBox.down=buttonSkin.getDrawable("InvitePlayers");
        styleInvitationBox.font=font;
        invitationBoxBtn=new TextButton("",styleInvitationBox);
        invitationBoxBtn.setPosition(buttonX, 3*buttonY);
        //invitationBoxBtn.setHeight(200);
        //invitationBoxBtn.setWidth(400);

        TextButton.TextButtonStyle styleTutorial = new TextButton.TextButtonStyle();
        styleTutorial.up=buttonSkin.getDrawable("Tutorial");
        styleTutorial.down=buttonSkin.getDrawable("Tutorial");
        styleTutorial.font=font;
        tutorialBtn=new TextButton("",styleTutorial);
        tutorialBtn.setPosition(buttonX, 4*buttonY);

        //Game title
        loginScreenAtlas = new TextureAtlas("LoginPage/loginscreenitems.atlas");
        loginScreenSkin = new Skin();
        loginScreenSkin.addRegions(loginScreenAtlas);

        TextButton.TextButtonStyle styleTitle = new TextButton.TextButtonStyle(); //** Button properties **//
        styleTitle.up = loginScreenSkin.getDrawable("Titlesmall");
        styleTitle.down = loginScreenSkin.getDrawable("Titlesmall");
        styleTitle.font = font;
        gameTitle = new TextButton("",styleTitle); //empty string since text is already on button
        gameTitle.setHeight(Gdx.graphics.getHeight() * 3 / 8);
        gameTitle.setWidth(Gdx.graphics.getHeight() * 3 / 4);
        int titleX = Gdx.graphics.getWidth()/2 - (int) gameTitle.getWidth()/2;
        gameTitle.setPosition(titleX, 5*buttonY); //** Button location **//

        music = game.getManager().get("audio/music/bgm1.ogg", Music.class);
        music.setLooping(true);
        music.play();


    }


    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        stage.addActor(menuLoginBackground);
        stage.addActor(gameTitle);

        logoutBtn.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                /*
                THINGS TO DO WHEN LOGOUT BUTTON IS PRESSED
                 */
                //TechiesWorld.playServices.destroy();
                TechiesWorld.playServices.signOut();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new LoginScreen(game));
                Gdx.app.log("Logout button", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                /*
                THINGS TO DO WHEN LOGOUT BUTTON IS RELEASED
                 */
                Gdx.app.log("Logout button", "Released");
            }
        });

        stage.addActor(logoutBtn);

        startGameBtn.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                /*
                THINGS TO DO WHEN START GAME BUTTON IS PRESSED
                 */
                TechiesWorld.playServices.startQuickGame();
                Gdx.app.log("Start Game button", "Pressed"); //** Usually used to start Game, etc. **//
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                /*
                THINGS TO DO WHEN START GAME BUTTON IS RELEASED
                 */
                Gdx.app.log("Start Game", "Released");
            }
        });

        stage.addActor(startGameBtn);

        invitationBoxBtn.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Invitation box", "Pressed");
                TechiesWorld.playServices.invitePlayer();
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Invitation box", "Released");
            }
        });

        stage.addActor(invitationBoxBtn);

        tutorialBtn.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                Gdx.app.log("tutorial Btn", "Pressed");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new TutorialScreen(game));
                return true;

            }

        });

        stage.addActor(tutorialBtn);
    }

    public void update(){
        if (TechiesWorld.playServices.isSignedIn()){ //use this block as signal for loading screen
            if (TechiesWorld.playServices.getAbleToStart()){
                music.stop();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(game));
                dispose();
            }
        }
        //Gdx.app.log("width", ""+Gdx.graphics.getWidth());
        //Gdx.app.log("height", ""+Gdx.graphics.getHeight());
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        update();
        Gdx.gl.glClearColor(0, 99, 0, 1); // rgba. clear screen with green
        //Gdx.gl.glClear((GL20.GL_COLOR_BUFFER_BIT));
        //Gdx.gl.glClearColor(1, 0, 0, 0); // rgba. clear screen with black
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
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
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
        buttonsAtlas.dispose();
        buttonSkin.dispose();
        menuLoginBackgroundSkin.dispose();
        menuLoginBackgroundAtlas.dispose();
        music.dispose();
        loginScreenAtlas.dispose();
        loginScreenSkin.dispose();

    }
}
