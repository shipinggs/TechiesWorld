package com.shiping.gametest.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 1/3/16.
 */
public class Hud implements Disposable {
    public Stage stage;
    // We want a separate viewport for our hud as it should stay locked on screen
    private Viewport viewport;
    private OrthographicCamera hudcam;

    private Integer worldTimer;
    private float timeCount;
    private static Integer score;
    private Integer minesLeft;

    Label countDownLabel;
    Label minesLeftLabel;
    static Label scoreLabel;
    Label timeLabel;
    Label minesLabel;
    Label playerLabel;

    public Hud (SpriteBatch sb) {
        worldTimer = 180;
        timeCount = 0;
        score = 0;
        minesLeft = 3;

        hudcam = new OrthographicCamera();
        viewport = new FitViewport(TechiesWorld.V_WIDTH, TechiesWorld.V_HEIGHT, hudcam);
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true); // table is the size of the parent (stage)

        countDownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        minesLeftLabel = new Label(String.format("%1d", minesLeft), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%03d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        minesLabel = new Label("MINES", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        playerLabel = new Label("PLAYER", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        countDownLabel.setFontScale(3f);
        minesLeftLabel.setFontScale(3f);
        scoreLabel.setFontScale(3f);
        timeLabel.setFontScale(3f);
        minesLabel.setFontScale(3f);
        playerLabel.setFontScale(3f);

        table.add(playerLabel).expandX().padTop(10);
        table.add(minesLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(minesLeftLabel).expandX();
        table.add(countDownLabel).expandX();

        stage.addActor(table); // add table to the stage



    }

    public void update (float dt) {
        timeCount += dt;
        if (timeCount >= 1) {   // one second
            worldTimer--;
            countDownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value) {
        score += value;
        scoreLabel.setText((String.format("%06d", score)));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
