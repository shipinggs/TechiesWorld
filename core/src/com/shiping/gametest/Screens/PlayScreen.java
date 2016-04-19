package com.shiping.gametest.Screens;

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


import java.util.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private TouchPadControl touchPadControl;

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
    private ArrayList<OtherPlayer> otherPlayers=new ArrayList<OtherPlayer>();

    // Spawning items handlers
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    // Music
    private AssetManager audioManager;
    private Music music;

    //list of positions for coin spawn, format: {{x1,y1},{x2,y2},...,{xn,yn}}
    /*private float[][] coinSpawnPositions0 = {{0.8f,0.6f},{1.0f,0.6f},{1.2f,0.6f}};
    private float[][] coinSpawnPositions1 = {{0.8f,0.8f},{1.0f,0.8f},{1.2f,0.8f}};
    private float[][] coinSpawnPositions2 = {{0.8f,1.0f},{1.0f,1.0f},{1.2f,1.0f}};
    private float[][] coinSpawnPositions3 = {{0.8f,1.2f},{1.0f,1.2f},{1.2f,1.2f}};*/
    private float[][] coinSpawnPositions0 = {{0.3f,0.4f},{0.9f,0.4f},{1.5f,0.4f},{0.3f,0.7f},{0.7f,0.7f},{1.0f,0.7f},{1.5f,0.7f},{0.3f,1.0f},{0.9f,1.0f},{1.5f,1.0f}};
    private float[][] coinSpawnPositions1 = {{0.5f,1.2f},{1.1f,1.2f},{1.7f,1.2f},{0.5f,1.5f},{0.9f,1.5f},{1.2f,1.5f},{1.7f,1.5f},{0.5f,1.8f},{1.1f,1.8f},{1.7f,1.8f}};
    private float[][] coinSpawnPositions2 = {{0.3f,0.6f},{0.9f,0.6f},{1.5f,0.6f},{0.3f,0.9f},{0.7f,0.9f},{1.0f,0.9f},{1.5f,0.9f},{0.3f,1.2f},{0.9f,1.2f},{1.5f,1.2f}};
    private float[][] coinSpawnPositions3 = {{0.5f,1.0f},{1.1f,1.0f},{1.7f,1.0f},{0.5f,1.3f},{0.9f,1.3f},{1.2f,1.3f},{1.7f,1.3f},{0.5f,1.6f},{1.1f,1.6f},{1.7f,1.6f}};
    private float[][][] allCoinSpawnPositions = {coinSpawnPositions0, coinSpawnPositions1, coinSpawnPositions2, coinSpawnPositions3};
    private boolean[] coinSpawnPositionsOccupied ={false, false, false, false, false, false, false, false, false, false};
    //private boolean[] coinSpawnPositionsOccupied ={false, false, false};
    int numOfCoinSpawnPositionsPerPlayer = 10;
    long timeCounter = 0;

    private int spawnTime = 450; //increase value to reduce frequency of coin spawn, 

    private int mineId;
    private Map<Integer,Mine> mineMap;



    public PlayScreen(TechiesWorld game) {
        playerID = TechiesWorld.playServices.getMyID(); //playerID determines coin spawn locations

        this.game = game;
        //create cam used to follow player
        gamecam = new OrthographicCamera();
        //create a FitViewport to main virtual aspect ratio
        gamePort = new FitViewport(TechiesWorld.V_WIDTH / TechiesWorld.PPM, TechiesWorld.V_HEIGHT / TechiesWorld.PPM, gamecam);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("mapSample.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / TechiesWorld.PPM);

        //Set initial gamecam position to be centered correctly
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true); // 0, 0 denotes no x, y acceleration (gravity)

        //Allows for debug lines of our box2d world
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(world, map);

        player = new Player(this);


        while (TechiesWorld.playServices.getMyID()==-1){}
        //TODO change this number when implementing more players
        for (int i=0;i<TechiesWorld.playServices.getRoomSize();i++){
            if (i!=TechiesWorld.playServices.getMyID()){
                otherPlayers.add(new OtherPlayer(this,i));
            }
        }

        //Heads-Up Display
        hud = new Hud(game.batch, player);

        //TouchPad Controller
        touchPadControl = new TouchPadControl(game.batch);


        world.setContactListener(new WorldContactListener());

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

        mineId=TechiesWorld.playServices.getMyID()*5;
        mineMap=new HashMap<Integer, Mine>();

        //Play music
        audioManager = game.getManager();
        music = audioManager.get("audio/music/bgm2.ogg", Music.class);
        music.setLooping(true);
        music.play();

        TechiesWorld.playServices.setPlayerCoinUnspawnedIndex();
    }

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll(); // like a pop for a queue
            if (idef.type == Mine.class) {
                Mine mine=new Mine(this, idef.position.x, idef.position.y, TechiesWorld.playServices.getMyID());
                synchronized (items){
                    items.add(mine);
                }
                // TODO replace playerID parameter with received message
                if (mineId>=(TechiesWorld.playServices.getMyID()+1)*3){
                    mineId=TechiesWorld.playServices.getMyID()*3;
                }
                if (mineMap.get(mineId)!=null){
                    try {
                        TechiesWorld.playServices.broadcastMsg(sendRemoveMineBuffer(mineMap.get(mineId)));
                        int id=items.indexOf(mineMap.get(mineId),true);
                        synchronized (items){
                            if (!items.get(id).isDestroyed()){
                                items.get(id).destroy();
                            }
                        }
                        mineMap.remove(mineId);
                    } catch (Exception e){}
                }

                mineMap.put(mineId,mine);
                TechiesWorld.playServices.broadcastMsg(sendPlantMineBuffer(idef.position.x, idef.position.y));
                mine.setMineId(mineId);
                mineId++;
            } else if (idef.type == Coin.class) {
                //items.add(new Coin(this, idef.position.x, idef.position.y, player.getAmountDropped()));
                int coinAmount = 100;
                items.add(new Coin(this, idef.position.x, idef.position.y, coinAmount, idef.index));


            }
        }
        if (!TechiesWorld.playServices.mineIsEmpty()){
            ArrayList<int[]> arrayList=TechiesWorld.playServices.getMinePositionAndClear();
            for (int[] positions:arrayList){
                if (positions[0]==1){
                    int minesId=positions[1];
                    int playerId=positions[2];
                    float x=(positions[3]*100+positions[4])/TechiesWorld.PPM;
                    float y=(positions[5]*100+positions[6])/TechiesWorld.PPM;
                    Mine mine=new Mine(this, x, y, playerId);
                    mine.setMineId(minesId);
                    synchronized (items){
                        items.add(mine);
                    }
                    mineMap.put(minesId,mine);
                }else if (positions[0]==0){
                    int minesId=positions[1];
                    if (mineMap.get(minesId)!=null){
                        try {
                            int id=items.indexOf(mineMap.get(minesId),true);
                            synchronized (items){
                                if (!items.get(id).isDestroyed()){
                                    ((Mine) items.get(id)).setCurrentState(4); // 4 for State.EXPLODING
                                }
                            }
                            mineMap.remove(minesId);
                        }catch (Exception e){}
                    }
                }

            }
        }
    }



    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if (!player.isPlayerDead()) {
            if (touchPadControl.isMinePressed()) {
                //testing coin spawn
                spawnItem(new ItemDef(new Vector2(player.b2body.getPosition().x, player.b2body.getPosition().y), Mine.class, 999)); //999 is a placeholder for index input which is only required for coins
                audioManager.get("audio/sounds/mine.wav", Sound.class).play();
                Gdx.app.log("X", "" + player.b2body.getPosition().x);
                Gdx.app.log("Y", "" + player.b2body.getPosition().y);

            }
            player.b2body.setLinearVelocity(touchPadControl.getVelocityVector());
        }
    }

    public void update(float dt) {


        //handle player input first
        handleInput(dt);

        //handle any queued items to spawn
        handleSpawningItems();

        world.step(1 / 60f, 6, 2);

        //update player sprite
        player.update(dt);
        for (OtherPlayer otherPlayer: otherPlayers){
            otherPlayer.update(dt);
        }

        //spawn coins generated by other devices


        //Gdx.app.log("Before if statement", "numOfCoinsToSpawn: "+TechiesWorld.playServices.numOfNewCoinsLeftToSpawn());

        if(TechiesWorld.playServices.numOfNewCoinsLeftToSpawn() > 0){ //spawning coins from other devices
            int[] coinInfo = TechiesWorld.playServices.getSpawnedCoinPosition(); //coinInfo = {playerID, n, amount, index}, numOfCoinsToSpawn-- within method
            //this.spawnItem(new ItemDef(new Vector2(coinInfo[0], coinInfo[1]), Coin.class));
            if(coinInfo!=null && coinInfo[0] < 4){ //to spawn coins not generated by player's death by checking coinInfo[0] < 4
                float x = allCoinSpawnPositions[coinInfo[0]][coinInfo[1]][0];
                float y = allCoinSpawnPositions[coinInfo[0]][coinInfo[1]][1];
                int index = coinInfo[3];
                this.spawnItem(new ItemDef(new Vector2(x, y), Coin.class, index));
//      Gdx.app.log("spawning coin from other device"," at x: "+x+" , at y: "+y);
            }else if(coinInfo!=null && coinInfo[0] >= 4){ //when coinInfo[0] >= 4 means the coin is spawned during a player's death
                float x = coinInfo[0]/60f; //divide by 60 since value was multiplied by 60 on other device before sending
                float y = coinInfo[1]/60f;
                int index = coinInfo[3];
                this.spawnItem(new ItemDef(new Vector2(x, y), Coin.class, index));

            }
        }

        //remove coins collected on other devices
        if(TechiesWorld.playServices.numOfCoinsToRemove() > 0){
            int index = TechiesWorld.playServices.getCoinToRemoveIndex();
            for(Item i : items){
                if(i.index == index){
                    i.destroy(); //destroy coin
                    TechiesWorld.playServices.decrementCoinsToRemove();
                }
            }
        }

        // randomly spawn coins every set time interval
        if(timeCounter%spawnTime==0){ //change % value to adjust spawn frequency
            if(spawnTime > 100){
                spawnTime -= 20; //to maintain consistent coin spawn rate throughout the game
            }
            Random rand = new Random();
            int  n = rand.nextInt(numOfCoinSpawnPositionsPerPlayer); //randomly generate a number from 0 to 2 to select coin spawn position
            if(!coinSpawnPositionsOccupied[n] && playerID >=0){ //playerID is initialised to -1 and will take sometime to be assigned bew value of 0-3
                int index = getUnspawnedCoinIndex();
                TechiesWorld.playServices.incrementUnspawnedIndex();
                this.spawnItem(new ItemDef(new Vector2(allCoinSpawnPositions[playerID][n][0], allCoinSpawnPositions[playerID][n][1]), Coin.class, index));
                coinSpawnPositionsOccupied[n] = true;
                byte[] coinSpawnedMsg = new byte[5]; //format: {'C', playerID, n, index, amount}
                coinSpawnedMsg[0] = 'C';
                coinSpawnedMsg[1] = (byte) playerID;
                //Gdx.app.log("coinSpawnPositions X", ""+(coinSpawnPositions1[n][0] * 10));
                coinSpawnedMsg[2] = (byte) n;
               // Gdx.app.log("coinSpawnPositions Y", ""+(coinSpawnPositions1[n][1] * 10));
                coinSpawnedMsg[3] = (byte) index; //index
                coinSpawnedMsg[4] = (byte) 100; //amount
                TechiesWorld.playServices.putMyCoinInHashmap(playerID, n, 100, index); //playerID, n, amount, index

                TechiesWorld.playServices.broadcastMsg(coinSpawnedMsg);

            }

        }
        timeCounter++;

        // TODO will need to update other player sprites here

        // update items (coin, mines) sprites
        for (Item item: items) {
            item.update(dt);
            if (item.isDestroyed()){
                items.removeValue(item,true);
            }
        }

        // update hud numbers (time, score etc.)
        hud.update(dt);

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
        for (Item item: items) {
            item.draw(game.batch);
        }

        // draw player sprites
        player.draw(game.batch);
        for (OtherPlayer otherPlayer: otherPlayers){
            otherPlayer.draw(game.batch);
        }
        // TODO draw other players sprites here

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined); // what is this for?
        hud.stage.draw();
        touchPadControl.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        touchPadControl.resize(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public AssetManager getAudioManager() {
        return audioManager;
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
    }


    public int getUnspawnedCoinIndex(){
        return TechiesWorld.playServices.getUnspawnedIndex();
    }


    public byte[] sendPlantMineBuffer(float x, float y){
        byte[] minePosition=new byte[8];
        minePosition[0]=(byte)'M';
        minePosition[1]=(byte)1;
        minePosition[2]=(byte)mineId;
        minePosition[3]=(byte)TechiesWorld.playServices.getMyID();
        int x1= (int) (x*TechiesWorld.PPM);
        int y1= (int) (y*TechiesWorld.PPM);
        minePosition[4]=(byte) (x1/100);
        minePosition[5]=(byte) (x1%100);
        minePosition[6]=(byte) (y1/100);
        minePosition[7]=(byte) (y1%100);
        return minePosition;
    }


    public byte[] sendRemoveMineBuffer(Mine mine){
        byte[] minePosition=new byte[8];
        minePosition[0]=(byte)'M';
        minePosition[1]=(byte)0;
        minePosition[2]=(byte)mine.mineId;
        minePosition[3]=(byte) 0;
        minePosition[4]=(byte) 0;
        minePosition[5]=(byte) 0;
        minePosition[6]=(byte) 0;
        minePosition[7]=(byte) 0;
        return minePosition;
    }
}
