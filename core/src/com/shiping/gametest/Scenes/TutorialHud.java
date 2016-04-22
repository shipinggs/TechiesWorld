package com.shiping.gametest.Scenes;

import com.badlogic.gdx.Gdx;
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
import com.shiping.gametest.Sprites.TutorialPlayer;
import com.shiping.gametest.TechiesWorld;

/**
 * This is a HEADS-UP DISPLAY specifically for the Tutorial
 * It takes in a TutorialPlayer as input instead of a Player.
 */
public class TutorialHud implements Disposable {
    public Stage stage;
    // We want a separate viewport for our hud as it should stay locked on screen
    private Viewport viewport;
    private OrthographicCamera hudcam;

    private int worldTimer;
    private float timeCount;
    private static int score;

    private TutorialPlayer player;

    private Label countDownLabel;
    private Label scoreLabel;
    private Label timeLabel;
    private Label goldLabel;



    public TutorialHud (SpriteBatch sb, TutorialPlayer player) {
        this.player = player;
        worldTimer = 100;
        timeCount = 0;
        score = player.getGoldAmount();

        hudcam = new OrthographicCamera();
        viewport = new FitViewport(TechiesWorld.V_WIDTH, TechiesWorld.V_HEIGHT, hudcam);
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true); // table is the size of the parent (stage)

        countDownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")), Color.WHITE));
        scoreLabel = new Label(String.format("%03d", score), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")), Color.CYAN));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")), Color.WHITE));
        goldLabel = new Label("GOLD", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")), Color.GOLD));


        table.add(goldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(countDownLabel).expandX();

        stage.addActor(table); // add table to the stage

    }

    public void update (float dt) {
        timeCount += dt;
        score = player.getGoldAmount();  // update score from player
        scoreLabel.setText((String.format("%06d", score)));
        if (timeCount >= 1 && worldTimer > 0) {   // one second
            worldTimer--;
            countDownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public int getWorldTimer() {
        return worldTimer;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
