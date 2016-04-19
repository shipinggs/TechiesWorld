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

        //font, reserved for future use
        calibriFontTexture = new Texture(Gdx.files.internal("fonts/hudfont.png"));
        font = new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt"), new TextureRegion(calibriFontTexture), false);
        font.setColor(0, 0, 1, 1); //** Blue text **//

        //buttons
        buttonsAtlas = new TextureAtlas("menuScreen/buttons.pack");
        buttonSkin = new Skin();
        buttonSkin.addRegions(buttonsAtlas);

        TextButton.TextButtonStyle styleLogout = new TextButton.TextButtonStyle(); //** Button properties **//
        styleLogout.up = buttonSkin.getDrawable("button_logout");
        styleLogout.down = buttonSkin.getDrawable("button_logout");
        styleLogout.font = font;
        logoutBtn = new TextButton("",styleLogout); //empty string since text is already on button
        logoutBtn.setPosition(100, 20); //** Button location **//
        logoutBtn.setHeight(200); //** Button Height **//
        logoutBtn.setWidth(400); //** Button Width **//

        TextButton.TextButtonStyle styleStartGame = new TextButton.TextButtonStyle(); //** Button properties **//
        styleStartGame.up = buttonSkin.getDrawable("button_start_game");
        styleStartGame.down = buttonSkin.getDrawable("button_start_game");
        styleStartGame.font = font;
        startGameBtn = new TextButton("",styleStartGame); //empty string since text is already on button
        startGameBtn.setPosition(100, 250); //** Button location **//
        startGameBtn.setHeight(200); //** Button Height **//
        startGameBtn.setWidth(400); //** Button Width **//


        TextButton.TextButtonStyle styleInvitationBox=new TextButton.TextButtonStyle();
        styleInvitationBox.up=buttonSkin.getDrawable("button_logout");
        styleInvitationBox.down=buttonSkin.getDrawable("button_logout");
        styleInvitationBox.font=font;
        invitationBoxBtn=new TextButton("",styleInvitationBox);
        invitationBoxBtn.setPosition(100,500);
        invitationBoxBtn.setHeight(200);
        invitationBoxBtn.setWidth(400);

        TextButton.TextButtonStyle styleTutorial=new TextButton.TextButtonStyle();
        styleTutorial.up=buttonSkin.getDrawable("button_logout");
        styleTutorial.down=buttonSkin.getDrawable("button_logout");
        styleTutorial.font=font;
        tutorialBtn=new TextButton("",styleTutorial);
        tutorialBtn.setPosition(600,20);
        tutorialBtn.setHeight(200);
        tutorialBtn.setWidth(400);

        music = game.getManager().get("audio/music/bgm1.ogg", Music.class);
        music.setLooping(true);
        music.play();


    }


    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        logoutBtn.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                /*
                THINGS TO DO WHEN LOGOUT BUTTON IS PRESSED
                 */
                TechiesWorld.playServices.destroy();
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
                Gdx.app.log("Tutorial", "Pressed");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new TutorialScreen(game));
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Tutorial", "Released");
            }
        });

        stage.addActor(tutorialBtn);

    }

    public void update(){
        if (TechiesWorld.playServices.isSignedIn()){
            if (TechiesWorld.playServices.getAbleToStart()){
                music.stop();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(game));
                dispose();
            }
        }
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
        music.dispose();

    }
}
