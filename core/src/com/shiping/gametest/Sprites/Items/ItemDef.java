package com.shiping.gametest.Sprites.Items;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by shiping on 27/3/16.
 */
public class ItemDef {
    public Vector2 position;
    public Class<?> type;
    public int index;

    public ItemDef(Vector2 position, Class<?> type, int index) {
        this.position = position;
        this.type = type;
        this.index = index;
    }
}
