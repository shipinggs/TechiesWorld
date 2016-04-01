package com.shiping.gametest.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 1/4/16.
 */
public class Controller {
    Viewport viewport;
    Stage stage;
    boolean upPressed, downPressed, leftPressed, rightPressed, minePressed, mineTouchedDown;
    OrthographicCamera cam;

    public Controller(SpriteBatch sb) {
        cam = new OrthographicCamera();
        viewport = new FitViewport(TechiesWorld.V_WIDTH, TechiesWorld.V_HEIGHT, cam);
        stage = new Stage(viewport, sb);

        mineTouchedDown = false;

        stage.addListener(new InputListener() {

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.UP:
                        upPressed = true;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = true;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = true;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = true;
                        break;
                    case Input.Keys.SPACE:
                        minePressed = true;
                        break;
                }
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.UP:
                        upPressed = false;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = false;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = false;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = false;
                        break;
                }
                return true;
            }
        });

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.left().bottom().padLeft(50).padBottom(50);

        Image upImg = new Image(new TextureRegion(new Texture("Button(128x128).png"), 128*7, 0, 128, 128));
        upImg.setSize(80, 80);
        upImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });

        Image downImg = new Image(new TextureRegion(new Texture("Button(128x128).png"), 128*3, 0, 128, 128));
        downImg.setSize(80, 80);
        downImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = false;
            }
        });

        Image leftImg = new Image(new TextureRegion(new Texture("Button(128x128).png"), 128*5, 0, 128, 128));
        leftImg.setSize(80, 80);
        leftImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });

        Image rightImg = new Image(new TextureRegion(new Texture("Button(128x128).png"), 128*1, 0, 128, 128));
        rightImg.setSize(80, 80);
        rightImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        Image mineImg = new Image(new TextureRegion(new Texture("Button(128x128).png"), 128*9, 0, 128, 128));
        mineImg.setSize(100, 100);
        mineImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                minePressed = true;
                return true;
            }
        });

        // top row - up button
        table.row().height(100);
        table.add();
        table.add(upImg).size(upImg.getWidth(), upImg.getHeight());
        table.add();

        // center row - left right buttons
        table.row().pad(5, 5, 5, 5).height(100);
        table.add(leftImg).size(leftImg.getWidth(), leftImg.getHeight());
        table.add().width(100);
        table.add(rightImg).size(rightImg.getWidth(), rightImg.getHeight());
        table.add().width(640);
        table.add(mineImg).size(mineImg.getWidth(), mineImg.getHeight());

        // bottom row - down button
        table.row().height(100);
        table.add();
        table.add(downImg).size(downImg.getWidth(), downImg.getHeight());
        table.add();

        stage.addActor(table);


    }

    public void draw() {
        stage.draw();
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isUpPressed() {
        return upPressed;
    }


    public boolean isMinePressed() {
        boolean temp = minePressed;
        minePressed = false;
        return temp;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
