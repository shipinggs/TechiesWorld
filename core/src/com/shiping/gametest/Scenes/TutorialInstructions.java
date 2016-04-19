package com.shiping.gametest.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
import com.shiping.gametest.Screens.TutorialScreen;
import com.shiping.gametest.TechiesWorld;

/**
 * Created by shiping on 19/4/16.
 */
public class TutorialInstructions implements Disposable {
    private TutorialScreen screen;
    public Stage stage;
    // We want a separate viewport for our hud as it should stay locked on screen
    private Viewport viewport;
    private OrthographicCamera cam;

    private BitmapFont font;
    private Label leftLabel;
    private Label rightLabel;

    private final String joystickInstruction = "Toggle 'joystick'\n here to move";
    private final String layMineInstruction = "Press button here\n to lay a mine";
    private final String pickCoinInstruction = "Go pick the coin to gain gold";
    private final String surpriseInstruction = "Be wary of mines! You lose gold when you die.\nIt's now safe to pick the coins.";
    private final String pickCoinInstruction2 = "Player with most gold wins the game.\nYou're now ready to go!";

    private float timer;

    public TutorialInstructions (TutorialScreen screen, SpriteBatch sb) {
        this.screen = screen;
        cam = new OrthographicCamera();
        viewport = new FitViewport(TechiesWorld.V_WIDTH, TechiesWorld.V_HEIGHT, cam);
        stage = new Stage(viewport, sb);

        font = new BitmapFont(Gdx.files.internal("fonts/hudfont.fnt"));
        font.setColor(255, 255, 255, 1); //** Blue text **//

        leftLabel = new Label(joystickInstruction, new Label.LabelStyle(font, Color.WHITE));
        rightLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));

        leftLabel.setFontScale(0.7f);
        rightLabel.setFontScale(0.7f);

        Table table = new Table();
        table.center().left();
        table.setFillParent(true); // table is the size of the parent (stage)
        table.add(leftLabel).width(300).padLeft(50);

        Table table2 = new Table();
        table2.center().right();
        table2.setFillParent(true);
        table2.add(rightLabel).width(300).padRight(50);

        stage.addActor(table);
        stage.addActor(table2);

    }

    public void update(float dt) {
        timer += dt;
        if (screen.hasMoved() && !screen.hasPlantedMine()
                && !screen.hasPickedCoin() &&!screen.hasDied() && timer >= 3) {
            rightLabel.setText(layMineInstruction);
            leftLabel.setText("");
        } else if (screen.hasPlantedMine() && !screen.hasPickedCoin()
                && !screen.hasDied() && timer >= 6) {
            rightLabel.setText("");
            leftLabel.setText(pickCoinInstruction);
        } else if (screen.hasDied() && !screen.hasPickedCoin()) {
            leftLabel.setText(surpriseInstruction);
        } else if (screen.hasPickedCoin()) {
            leftLabel.setText(pickCoinInstruction2);
        }



    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
