package com.shiping.gametest.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.shiping.gametest.Scenes.Controller;
import com.shiping.gametest.Sprites.Items.Coin;
import com.shiping.gametest.Sprites.OtherPlayer;
import com.shiping.gametest.TechiesWorld;
import com.shiping.gametest.Scenes.Hud;

import com.shiping.gametest.Sprites.Items.Item;
import com.shiping.gametest.Sprites.Items.ItemDef;
import com.shiping.gametest.Sprites.Items.Mine;
import com.shiping.gametest.Sprites.Player;
import com.shiping.gametest.Tools.B2WorldCreator;
import com.shiping.gametest.Tools.WorldContactListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Infinity on 1/3/16.
 *
 * This class represents everything that the Player sees on his/her own screen.
 *
 */
public class PlayScreen implements Screen {

    private int playerID;
    private TechiesWorld game;

    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    private Controller controller;

    // Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer; // renders the map onto the screen

    // Box2D Variables
    private World world;    // Box2d world

    // Renderer to provide graphical representation of fixtures and bodies of box2D world
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    // sprites
    private Player player;
    private OtherPlayer otherPlayer1;


    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;


    public PlayScreen(TechiesWorld game) {
        playerID = 1;

        this.game = game;
        // create cam used to follow player
        gamecam = new OrthographicCamera();
        // create a FitViewport to main virtual aspect ratio
        gamePort = new FitViewport(TechiesWorld.V_WIDTH / TechiesWorld.PPM, TechiesWorld.V_HEIGHT / TechiesWorld.PPM, gamecam);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("mapSample.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / TechiesWorld.PPM);

        // Set initial gamecam position to be centered correctly
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true); // 0, 0 denotes no x, y acceleration (gravity)

        // Allows for debug lines of our box2d world
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(world, map);

        player = new Player(this);
        otherPlayer1 = new OtherPlayer(1);

        // Heads-Up Display
        hud = new Hud(game.batch, player);

        // Controller
        controller = new Controller(game.batch);


        world.setContactListener(new WorldContactListener());

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll(); // like a pop for a queue
            if (idef.type == Mine.class) {
                // TODO replace playerID parameter with received message
                items.add(new Mine(this, idef.position.x, idef.position.y, 0));
            } else if (idef.type == Coin.class) {
                items.add(new Coin(this, idef.position.x, idef.position.y, player.getAmountDropped()));

            }

        }
    }


    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if (!player.isPlayerDead()) {
            if (controller.isMinePressed()) {
                if (player.getMinesLeft() > 0) {
                    spawnItem(new ItemDef(new Vector2(player.b2body.getPosition().x, player.b2body.getPosition().y), Mine.class));
//                    player.decreaseMinesCount();
                }
            }

            if (controller.isUpPressed() && player.b2body.getLinearVelocity().y <= 0.6) {
                player.b2body.applyForce(new Vector2(0, 60f), player.b2body.getWorldCenter(), true);
            }
            if (controller.isDownPressed() && player.b2body.getLinearVelocity().y >= -0.6) {
                player.b2body.applyForce(new Vector2(0, -60f), player.b2body.getWorldCenter(), true);
            }
            if (controller.isLeftPressed() && player.b2body.getLinearVelocity().x >= -0.6) {
                player.b2body.applyForce(new Vector2(-60f, 0), player.b2body.getWorldCenter(), true);
            }
            if (controller.isRightPressed() && player.b2body.getLinearVelocity().x <= 0.6) {
                player.b2body.applyForce(new Vector2(60f, 0), player.b2body.getWorldCenter(), true);
            }
        }

    }

    public void update(float dt) {
        // handle player input first
        handleInput(dt);

        // handle any queued items to spawn
        handleSpawningItems();

        // slow the player down 'naturally'
        player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x / 2, player.b2body.getLinearVelocity().y / 2);

        world.step(1 / 60f, 6, 2);

        // update player sprite
        player.update(dt);
        otherPlayer1.update(dt);

        // TODO will need to update other player sprites here

        // update items (coin, mines) sprites
        for (Item item: items) {
            item.update(dt);
        }

        // update hud numbers (time, score etc.)
        hud.update(dt);

        gamecam.position.x = player.b2body.getPosition().x;
        gamecam.position.y = player.b2body.getPosition().y;

        gamecam.update();
        // let map renderer know what it needs to render
        // only render what the gamecam can see
        renderer.setView(gamecam);


    }

    @Override
    public void render(float delta) {
        update(delta);

        // erase and redraw everything
        // clear screen with black. RGBA settings.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear((GL20.GL_COLOR_BUFFER_BIT));


        // render game map
        renderer.render();

        // render our Box2D Debug Lines
        b2dr.render(world, gamecam.combined); // TODO remove when ready

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();

        // draw items (mines, coins) sprites
        for (Item item: items) {
            item.draw(game.batch);
        }

        // draw player sprites
        player.draw(game.batch);
        otherPlayer1.draw(game.batch);
        // TODO draw other players sprites here

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        controller.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        controller.resize(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPlayerID() {
        return playerID;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }


}
