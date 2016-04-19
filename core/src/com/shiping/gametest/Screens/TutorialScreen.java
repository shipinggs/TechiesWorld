package com.shiping.gametest.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.shiping.gametest.Scenes.TouchPadControl;
import com.shiping.gametest.Scenes.TutorialHud;
import com.shiping.gametest.Scenes.TutorialInstructions;
import com.shiping.gametest.Scenes.TutorialTouchPad;
import com.shiping.gametest.Sprites.Tutorial.TutorialCoin;
import com.shiping.gametest.Sprites.Tutorial.TutorialItem;
import com.shiping.gametest.Sprites.Tutorial.TutorialItemDef;
import com.shiping.gametest.Sprites.Tutorial.TutorialMine;
import com.shiping.gametest.Sprites.TutorialPlayer;
import com.shiping.gametest.TechiesWorld;
import com.shiping.gametest.Tools.B2WorldCreator;
import com.shiping.gametest.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by shiping on 19/4/16.
 */
public class TutorialScreen implements Screen {
    private TechiesWorld game;

    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private TutorialHud hud;
    private TutorialTouchPad touchPadControl;
    private TutorialInstructions tutorialInstructions;

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
    private TutorialPlayer player;

    // Spawning items handlers
    private Array<TutorialItem> items;
    private LinkedBlockingQueue<TutorialItemDef> itemsToSpawn;

    // Music
    private AssetManager audioManager;
    private Music music;

    // Instruction steps
    private boolean hasMoved;
    private boolean hasPlantedMine;
    private boolean hasPickedCoin;


    public TutorialScreen(TechiesWorld game) {
        this.game = game;
        //create cam used to follow player
        gamecam = new OrthographicCamera();
        //create a FitViewport to main virtual aspect ratio
        gamePort = new FitViewport(TechiesWorld.V_WIDTH / TechiesWorld.PPM, TechiesWorld.V_HEIGHT / TechiesWorld.PPM, gamecam);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / TechiesWorld.PPM);

        //Set initial gamecam position to be centered correctly
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true); // 0, 0 denotes no x, y acceleration (gravity)

        //Allows for debug lines of our box2d world
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(world, map);

        player = new TutorialPlayer(this);

        //Heads-Up Display
        hud = new TutorialHud(game.batch, player);

        //TouchPad Controller
        touchPadControl = new TutorialTouchPad(game.batch);

        //Tutorial Instructions
        tutorialInstructions = new TutorialInstructions(this, game.batch);

        world.setContactListener(new WorldContactListener());

        items = new Array<TutorialItem>();
        itemsToSpawn = new LinkedBlockingQueue<TutorialItemDef>();


        //Get audioManager
        audioManager = game.getManager();

        TechiesWorld.playServices.setPlayerCoinUnspawnedIndex(); //TODO not sure here
    }

    public void spawnItem(TutorialItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            TutorialItemDef idef = itemsToSpawn.poll(); // like a pop for a queue
            if (idef.type == TutorialMine.class) {
                items.add(new TutorialMine(this, idef.position.x, idef.position.y,0));
            } else if (idef.type == TutorialCoin.class) {
                items.add(new TutorialCoin(this, idef.position.x, idef.position.y, 100));
            }
        }
    }

    public void handleInput(float dt) {
        if (!player.isPlayerDead()) {
            if (touchPadControl.isMinePressed()) {
                if (!hasPlantedMine())  {
                    hasPlantedMine = true;
                    spawnItem(new TutorialItemDef(new Vector2(1.05f,0.7f), TutorialCoin.class));
                    audioManager.get("audio/sounds/pickup2.wav", Sound.class).play();

                }
                spawnItem(new TutorialItemDef(new Vector2(player.b2body.getPosition().x, player.b2body.getPosition().y), TutorialMine.class)); //999 is a placeholder for index input which is only required for coins
                audioManager.get("audio/sounds/mine.wav", Sound.class).play();
                Gdx.app.log("X", "" + player.b2body.getPosition().x);
                Gdx.app.log("Y", "" + player.b2body.getPosition().y);

            }
            if (!touchPadControl.getVelocityVector().isZero() && !hasMoved()) {
                hasMoved = true;
                audioManager.get("audio/sounds/pickup2.wav", Sound.class).play();
            }
            player.b2body.setLinearVelocity(touchPadControl.getVelocityVector());
        }
    }

    public void update(float dt) {
        if (touchPadControl.isBackTouched()) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game));
            dispose();
        }

        if (player.hasPickedCoin() && !hasPickedCoin) hasPickedCoin = true;

        handleInput(dt);
        handleSpawningItems();

        world.step(1 / 60f, 6, 2);

        player.update(dt);
        for (TutorialItem item: items) {
            item.update(dt);
        }

        hud.update(dt);
        tutorialInstructions.update(dt);

        // update touchpadcontrol
        touchPadControl.update(dt);


        // update gamecam position to follow player unless player wanders into corners of map
        float posX = player.b2body.getPosition().x;
        float posY = player.b2body.getPosition().y;
        gamecam.position.x = player.b2body.getPosition().x;
        gamecam.position.y = player.b2body.getPosition().y;
        if (posX <= 0.5) gamecam.position.x = 0.5f;
        if (posX >= 1.6) gamecam.position.x = 1.6f;
        if (posY <= 0.6) gamecam.position.y = 0.6f;
        if (posY >= 1.5) gamecam.position.y = 1.5f;


        gamecam.update();
        // let map renderer know what it needs to render
        // only render what the gamecam can see
        renderer.setView(gamecam);
    }


    public World getWorld() {
        return world;
    }

    public AssetManager getAudioManager() {
        return audioManager;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public boolean hasPlantedMine() {
        return hasPlantedMine;
    }

    public boolean hasPickedCoin() {
        return hasPickedCoin;
    }

    @Override
    public void show() {

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


        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();

        // draw items (mines, coins) sprites
        for (TutorialItem item: items) {
            item.draw(game.batch);
        }

        // draw player sprites
        player.draw(game.batch);


        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        touchPadControl.draw();
        tutorialInstructions.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        touchPadControl.resize(width, height);
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
        touchPadControl.dispose();
        tutorialInstructions.dispose();
    }
}
