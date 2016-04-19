package com.shiping.gametest.Sprites.Tutorial;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by shiping on 19/4/16.
 */
public class TutorialItemDef {
    public Vector2 position;
    public Class<?> type;

    public TutorialItemDef(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
    }
}
