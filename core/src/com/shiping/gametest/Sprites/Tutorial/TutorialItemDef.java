package com.shiping.gametest.Sprites.Tutorial;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by shiping on 19/4/16.
 */
public class TutorialItemDef {
    public Vector2 position;
    public Class<?> type;
    public int index;

    public TutorialItemDef(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
        this.index = -999; //was 0
    }

    public TutorialItemDef(Vector2 position, Class<?> type, int index) {
        this.position = position;
        this.type = type;
        this.index = index;
    }
}
