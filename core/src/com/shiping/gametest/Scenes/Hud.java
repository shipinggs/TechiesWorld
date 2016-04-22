package com.shiping.gametest.Scenes;

import com.badlogic.gdx.Game;
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
import com.shiping.gametest.Screens.EndScreen;
import com.shiping.gametest.Sprites.Player;
import com.shiping.gametest.TechiesWorld;

/**
 *
 * This is the HEADS-UP DISPLAY for players when on the PlayScreen.
 * It shows players their current amount of gold and the amount of time left.
 *
 */
public class Hud implements Disposable {
    public Stage stage;
    // We want a separate viewport for our hud as it should stay locked on screen
    private Viewport viewport;
    private OrthographicCamera hudcam;

    private int worldTimer;
    private float timeCount;
    private static Integer score;

    private Player player;
    private TechiesWorld game;

    Label countDownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label goldLabel;

    /**
     *  Constructor for the HUD.
     *
     *  @param sb       This is meant for drawing the stage.
     *  @param player   The player whose gold amount will be shown on the HUD. This will typically be the player of the device.
     *  @param game     To be able to set the screen to the EndScreen.
     *  */

    public Hud (SpriteBatch sb, Player player, TechiesWorld game) {
        this.player = player;
        this.game = game;
        worldTimer = 140;
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
        if (timeCount >= 1) {   // one second
            worldTimer--;
            if (worldTimer==0){
                TechiesWorld.playServices.putPlayerScore(score);
                TechiesWorld.playServices.broadcastReliableMsg(sendScoreBuffer());
                ((Game) Gdx.app.getApplicationListener()).setScreen(new EndScreen(game));
            }
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

    public byte[] sendScoreBuffer(){
        byte[] myScore=new byte[4];
        myScore[0]=(byte) 'L';
        myScore[1]=(byte) TechiesWorld.playServices.getMyID();
        myScore[2]=(byte) (score/100);
        myScore[3]=(byte) (score%100);
        return myScore;
    }
}
