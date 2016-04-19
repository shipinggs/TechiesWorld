package com.shiping.gametest.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by Infinity on 1/4/16.
 *
 * This class represents the Controller overlay.
 * It provides the player with a 'joystick' and a button to lay mines.
 */
public class TutorialTouchPad implements Disposable {
    private Viewport viewport;
    private Stage stage;
    private Table table;
    private Table tabletop;
    private OrthographicCamera cam;
    private SpriteBatch batch;

    private Touchpad touchpad;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;

    private boolean minePressed, mineTouchedDown;

    private Texture texturePack = new Texture("Button(128x128).png");
    private Texture texturePack2 = new Texture("PNGPack.png");
    private ImageButton mineImgBtn;
    private Skin buttonSkin;
    private ImageButton.ImageButtonStyle mineBtnStyle;

    private TextButton backBtn;
    private BitmapFont font;

    private boolean backTouched;

    private float stateTimer;
    private Animation mineCoolDown;

    private enum State { READY, COOLDOWN };
    private State currentState;
    private State previousState;

    public TutorialTouchPad(SpriteBatch sb) {
        cam = new OrthographicCamera();
        viewport = new FitViewport(TechiesWorld.V_WIDTH, TechiesWorld.V_HEIGHT, cam);
        batch = sb;
        mineTouchedDown = false;

        stateTimer = 0;
        currentState = previousState = State.COOLDOWN;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        // get run animation frames and add them to playerAlive Animation
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(texturePack2, i * 64, 134, 64, 64));
        }
        mineCoolDown = new Animation(0.2f, frames);

        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture("touchBackground.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("touchKnob.png"));
        //Create TouchPad Style
        touchpadStyle = new Touchpad.TouchpadStyle();
        //Create Drawable's from TouchPad skin
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        //Apply the Drawables to the TouchpadStyle
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);
        //setBounds(x,y,width,height)
        touchpad.setBounds(15, 15, 200, 200);

        //Create a Stage and add TouchPad
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        buttonSkin = new Skin();
        buttonSkin.add("Ready", new TextureRegion(texturePack, 9 * 128, 0, 128, 128));
        buttonSkin.add("Cooldown", new TextureRegion(texturePack, 8 * 128, 0, 128, 128));
        buttonSkin.add("backbtn", new Texture("TutorialPage/tutorialbackbtn.png"));
        mineBtnStyle = new ImageButton.ImageButtonStyle();
        mineBtnStyle.imageUp = buttonSkin.getDrawable("Ready");

        //Create ImageButton for lay mine button
        mineImgBtn = new ImageButton(mineBtnStyle);
        mineImgBtn.setSize(160, 160);
        mineImgBtn.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (currentState == State.READY) {
                    minePressed = true;
                    currentState = State.COOLDOWN;
                }
                return true;
            }
        });

        font = new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt"));
        font.setColor(255, 255, 255, 1); //** Blue text **//

        TextButton.TextButtonStyle backBtnStyle = new TextButton.TextButtonStyle();
        backBtnStyle.up = buttonSkin.getDrawable("backbtn");
        backBtnStyle.font = font;
        backBtn = new TextButton("Back", backBtnStyle);

        backBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                backTouched = true;
                Gdx.app.log("Backbtn", "Pressed");
                return backTouched;
            }
        });


        //Create Table to place items in an organised manner
        tabletop = new Table();
        tabletop.top();
        tabletop.setFillParent(true);
        tabletop.add(backBtn);

        table = new Table();
        table.left().bottom().padLeft(100).padBottom(50);

        table.row();
        table.add(touchpad);
        table.add().width(680);
        table.add(mineImgBtn).size(mineImgBtn.getWidth(), mineImgBtn.getHeight());

        table.row();

        stage.addActor(tabletop);
        stage.addActor(table);
    }

    public void update(float dt) {
        stateTimer = currentState == previousState? stateTimer + dt : 0;
        if (currentState == State.READY) {
            mineBtnStyle.imageUp = buttonSkin.getDrawable("Ready");
        }
        if (currentState == State.COOLDOWN) {
            mineBtnStyle.imageUp = buttonSkin.getDrawable("Cooldown");
            if (stateTimer > 3) {
                currentState = State.READY;
            }
        }
        previousState = currentState;

    }

    public boolean isMinePressed() {
        boolean temp = minePressed;
        minePressed = false;
        return temp;
    }

    public boolean isBackTouched() {
        return backTouched;
    }

    public void draw() {
        stage.draw();
    }

    //Returns a Vector2 indicating the linear velocity to give the Player's b2body
    public Vector2 getVelocityVector() {
        return new Vector2(touchpad.getKnobPercentX()*0.7f, touchpad.getKnobPercentY()*0.7f);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }


    @Override
    public void dispose() {
        stage.dispose();
    }
}
