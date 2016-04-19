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
import com.shiping.gametest.TechiesWorld;

/**
 * Created by G751-4314 on 19/4/2016.
 */
public class Hud2 implements Disposable {
    public Stage stage;
    // We want a separate viewport for our hud as it should stay locked on screen
    private Viewport viewport;
    private OrthographicCamera hudcam;

    Label player0;
    Label player1;
    Label player2;
    Label player3;

    Label player0Score;
    Label player1Score;
    Label player2Score;
    Label player3Score;

    Label showPlayerId;

    int score0;
    int score1;
    int score2;
    int score3;

    public Hud2(SpriteBatch spriteBatch){
        hudcam = new OrthographicCamera();
        viewport = new FitViewport(TechiesWorld.V_WIDTH, TechiesWorld.V_HEIGHT, hudcam);
        stage = new Stage(viewport, spriteBatch);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

//        countDownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")), Color.WHITE));
        player0=new Label("Player1 Score",new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")), Color.WHITE));
        player1=new Label("Player2 Score",new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")),Color.WHITE));
        player2=new Label("Player3 Score",new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")),Color.WHITE));
        player3=new Label("Player4 Score",new Label.LabelStyle(new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")),Color.WHITE));

        player0Score=new Label(String.format(String.valueOf(score0)), new Label.LabelStyle(
                new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")), Color.WHITE));
        player1Score=new Label(String.format(String.valueOf(score1)), new Label.LabelStyle(
                new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")), Color.WHITE));
        player2Score=new Label(String.format(String.valueOf(score2)), new Label.LabelStyle(
                new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")), Color.WHITE));
        player3Score=new Label(String.format(String.valueOf(score3)), new Label.LabelStyle(
                new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")), Color.WHITE));

        showPlayerId=new Label(String.format("You are player "+String.valueOf(TechiesWorld.playServices.getMyID()+1)), new Label.LabelStyle(
                new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt")), Color.WHITE));

        table.add(showPlayerId).expandX();
        table.row();
        table.add(player0).expandX().padTop(10);
        table.add(player0Score).expandX().padTop(10);
        table.row();
        table.add(player1).expandX().padTop(10);
        table.add(player1Score).expandX().padTop(10);
        table.row();
        table.add(player2).expandX().padTop(10);
        table.add(player2Score).expandX().padTop(10);
        table.row();
        table.add(player3).expandX().padTop(10);
        table.add(player3Score).expandX().padTop(10);

        stage.addActor(table); // add table to the stage

    }

    public void update(float dt){
        score0=TechiesWorld.playServices.getPlayerScore(0);
        player0Score.setText(String.format(String.valueOf(score0)));
        score1=TechiesWorld.playServices.getPlayerScore(1);
        player1Score.setText(String.format(String.valueOf(score1)));
        score2=TechiesWorld.playServices.getPlayerScore(2);
        player2Score.setText(String.format(String.valueOf(score2)));
        score3=TechiesWorld.playServices.getPlayerScore(3);
        player3Score.setText(String.format(String.valueOf(score3)));
    }
    @Override
    public void dispose() {
        stage.dispose();
    }
}
