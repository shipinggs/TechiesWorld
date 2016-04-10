package com.shiping.gametest.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by Infinity on 1/4/16.
 *
 * This class represents the Controller overlay.
 * It provides the player with a 'joystick' and a button to lay mines.
 */
public class TouchPadControl {
    private Viewport viewport;
    private Stage stage;
    private OrthographicCamera cam;
    private SpriteBatch batch;

    private Touchpad touchpad;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;

    private boolean minePressed, mineTouchedDown;


    public TouchPadControl(SpriteBatch sb) {
        cam = new OrthographicCamera();
        viewport = new FitViewport(TechiesWorld.V_WIDTH, TechiesWorld.V_HEIGHT, cam);
        batch = sb;
        mineTouchedDown = false;

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

        //Create Image for lay mine button
        Image mineImg = new Image(new TextureRegion(new Texture("Button(128x128).png"), 128*9, 0, 128, 128));
        mineImg.setSize(160, 160);
        mineImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                minePressed = true;
                return true;
            }
        });

        //Create Table to place items in an organised manner
        Table table = new Table();
        table.left().bottom().padLeft(100).padBottom(50);

        table.row();
        table.add(touchpad);
        table.add().width(680);
        table.add(mineImg).size(mineImg.getWidth(), mineImg.getHeight());

        stage.addActor(table);
    }

    public boolean isMinePressed() {
        boolean temp = minePressed;
        minePressed = false;
        return temp;
    }

    //Returns a Vector2 indicating the linear velocity to give the Player's b2body
    public Vector2 getVelocityVector() {
        return new Vector2(touchpad.getKnobPercentX(), touchpad.getKnobPercentY());
    }

    public void draw() {
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }


}
