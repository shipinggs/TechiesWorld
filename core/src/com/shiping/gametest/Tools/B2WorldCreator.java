package com.shiping.gametest.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.shiping.gametest.TechiesWorld;

/**
 * This class is responsible for drawing the Box2D bodies as walls of the map.
 */
public class B2WorldCreator {
    public B2WorldCreator(World world, TiledMap map) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // create wall bodies/fixtures
        for (MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / TechiesWorld.PPM, (rect.getY() + rect.getHeight() / 2) / TechiesWorld.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / TechiesWorld.PPM, rect.getHeight() / 2 / TechiesWorld.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }
    }
}
