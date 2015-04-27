package com.mockpit.platformer.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;

import static com.mockpit.platformer.handlers.B2DVars.PPM;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mockpit.platformer.Game;
import com.mockpit.platformer.entities.B2DSprite;
import com.mockpit.platformer.entities.Bullet;
import com.mockpit.platformer.entities.Crystal;
import com.mockpit.platformer.entities.HUD;
import com.mockpit.platformer.entities.Jet;
import com.mockpit.platformer.entities.LandingPad;
import com.mockpit.platformer.entities.Player;
import com.mockpit.platformer.entities.Seaweed;
import com.mockpit.platformer.entities.Spike;
import com.mockpit.platformer.handlers.B2DVars;
import com.mockpit.platformer.handlers.Background;
import com.mockpit.platformer.handlers.BoundedCamera;
import com.mockpit.platformer.handlers.FathomContactListener;
import com.mockpit.platformer.handlers.FathomInput;
import com.mockpit.platformer.handlers.GameStateManager;
import com.badlogic.gdx.utils.Timer;
import com.mockpit.platformer.handlers.ParticleManager;

/**
 * Created by The Godfather on 07/03/2015.
 */
public class Play extends GameState {

    private boolean debug = false;

    private World world;
    private Box2DDebugRenderer b2dRenderer;
    private FathomContactListener cl;
    private BoundedCamera b2dCam, cam;

    private Player player;

    private TiledMap tileMap;
    private int tileMapWidth;
    private int tileMapHeight;
    private int tileSize;
    private OrthogonalTiledMapRenderer tmRenderer;

    private Array<Crystal> crystals;
    private Array<Spike> spikes;
    private Array<Seaweed> weeds;
    private Array<LandingPad> pads;
    private Array<Jet> jets;
    private Array<Bullet> bullets;
    private Array<Bullet> bulletsFired;
    private Array<B2DSprite> mapSprites;
    private Bullet bullet;
    private ParticleManager particleManager;

    private Background[] backgrounds;
    private HUD hud;

    private float accelerometer;
    private int ammoCount = 0;
    private int bulletsFiredIndex = 0;
    private Vector2 bulletDirection;
    private boolean hit = false;

    public static int level;

    private FPSLogger fps;

    public Play(GameStateManager gsm) {

        super(gsm);

        try {
            tileMap = new TmxMapLoader().load("maps/level" + level + ".tmx");
        }
        catch(Exception e) {
            System.out.println("Cannot find file: maps/level" + level + ".tmx");
            Gdx.app.exit();
        }

        // set up the box2d world and contact listener
        world = new World(new Vector2(0, -1f), true);
        cl = new FathomContactListener();
        world.setContactListener(cl);
        b2dRenderer = new Box2DDebugRenderer();
        //Game.res.getMusic("bbsong").play();


        // create player
        createPlayer();

        // create walls
        createWalls();
        cam = gsm.game().getCamera(); //new BoundedCamera();
        cam.setBounds(0, tileMapWidth * tileSize, 0, tileMapHeight * tileSize);

        // create crystals
        createCrystals();
        player.setTotalCrystals(crystals.size);

        // create spikes
        createSpikes();

        // create weeds
        createWeeds();

        // create landing pads
        createLandingPad();

        // create jets
        createLJets();

        particleManager = new ParticleManager();

        // create backgrounds
        Texture bgs = Game.res.getTexture("bgs");
        TextureRegion sky = new TextureRegion(bgs, 0, 0, 320, 240);
        TextureRegion clouds = new TextureRegion(bgs, 0, 240, 320, 240);
        TextureRegion mountains = new TextureRegion(bgs, 0, 480, 320, 240);
        backgrounds = new Background[3];
        backgrounds[0] = new Background(sky, cam, 0f);
        backgrounds[1] = new Background(clouds, cam, 0.1f);
        backgrounds[2] = new Background(mountains, cam, 0.2f);

        // create hud
        hud = new HUD(player);

        // set up box2d cam
        b2dCam = new BoundedCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
        b2dCam.setBounds(0, (tileMapWidth * tileSize) / PPM, 0, (tileMapHeight * tileSize) / PPM);
    }

    /**
     * Creates the player.
     * Sets up the box2d body and sprites.
     */
    private void createPlayer() {

        // create bodydef
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(60 / PPM, 120 / PPM);
        bdef.linearDamping = 0.5f;
        //bdef.fixedRotation = true;
        //bdef.linearVelocity.set(0.2f, 0.2f);

        // create body from bodydef
        Body body = world.createBody(bdef);

        // create box shape for player collision box
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10 / PPM, 10 / PPM, new Vector2(0, -5 / PPM), 0);

        // create fixturedef for player collision box
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 0;
        fdef.friction = 200;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED_BLOCK | B2DVars.BIT_CRYSTAL | B2DVars.BIT_SPIKE | B2DVars.BIT_WEED | B2DVars.BIT_JET;

        // create player collision box fixture
        body.createFixture(fdef);
        shape.dispose();

        // create box shape for player foot
        shape = new PolygonShape();
        shape.setAsBox(10 / PPM, 3 / PPM, new Vector2(0, -13 / PPM), 0);

        // create fixturedef for player foot
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED_BLOCK;

        // create player foot fixture
        body.createFixture(fdef).setUserData("foot");
        shape.dispose();

        // create box shape for player head
        CircleShape cshape = new CircleShape();
        cshape.setRadius(10 / PPM);
        cshape.setPosition(new Vector2(0, 5 / PPM));

        // create fixturedef for player head
        fdef.shape = cshape;
        fdef.density = 0;
        fdef.friction = 200;
        //fdef.isSensor = true;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER_HEAD;
        fdef.filter.maskBits = B2DVars.BIT_RED_BLOCK | B2DVars.BIT_CRYSTAL | B2DVars.BIT_SPIKE | B2DVars.BIT_WEED | B2DVars.BIT_JET;

        // create player foot fixture
        body.createFixture(fdef).setUserData("head");
        cshape.dispose();

        // create new player
        player = new Player(body);
        body.setUserData(player);
        if(tileMap.getProperties().get("BulletCount", String.class) != null) {
            player.setNumBullets(Integer.parseInt(tileMap.getProperties().get("BulletCount", String.class)));
        }

        // create the players bullets
        //bullets = createBullets(player.getNumBullets());
        bulletsFired = new Array<Bullet>();
        //ammoCount = bullets.size;

        // final tweaks, manually set the player body mass to 1 kg
        //MassData md = body.getMassData();
        //md.mass = 1;
        //body.setMassData(md);

        // i need a ratio of 0.005
        // so at 1kg, i need 200 N jump force

    }

    /**
     * Sets up the tile map collidable tiles.
     * Reads in tile map layers and sets up box2d bodies.
     */
    private void createWalls() {

        // load tile map and map renderer

        tileMapWidth = tileMap.getProperties().get("width", Integer.class);
        tileMapHeight = tileMap.getProperties().get("height", Integer.class);
        tileSize = tileMap.getProperties().get("tilewidth", Integer.class);
        tmRenderer = new OrthogonalTiledMapRenderer(tileMap);


        // read each of the "red" "green" and "blue" layers
        TiledMapTileLayer layer;
        layer = (TiledMapTileLayer) tileMap.getLayers().get("red");
        createBlocks(layer, B2DVars.BIT_RED_BLOCK);
        layer = (TiledMapTileLayer) tileMap.getLayers().get("green");
        createBlocks(layer, B2DVars.BIT_GREEN_BLOCK);
        layer = (TiledMapTileLayer) tileMap.getLayers().get("blue");
        createBlocks(layer, B2DVars.BIT_BLUE_BLOCK);

    }

    /**
     * Creates box2d bodies for all non-null tiles
     * in the specified layer and assigns the specified
     * category bits.
     *
     * @param layer the layer being read
     * @param bits category bits assigned to fixtures
     */
    private void createBlocks(TiledMapTileLayer layer, short bits) {

        // tile size
        float ts = layer.getTileWidth();

        // go through all cells in layer
        for(int row = 0; row < layer.getHeight(); row++) {
            for(int col = 0; col < layer.getWidth(); col++) {

                // get cell
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                // check that there is a cell
                if(cell == null) continue;
                if(cell.getTile() == null) continue;

                // create body from cell
                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((col + 0.5f) * ts / PPM, (row + 0.5f) * ts / PPM);
                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[5];
                v[0] = new Vector2(-ts / 2 / PPM, -ts / 2 / PPM);
                v[1] = new Vector2(-ts / 2 / PPM, ts / 2 / PPM);
                v[2] = new Vector2(ts / 2 / PPM, ts / 2 / PPM);
                v[3] = new Vector2(ts / 2 / PPM, -ts / 2 / PPM);
                v[4] = new Vector2(-ts / 2 / PPM, -ts / 2 / PPM);
                cs.createChain(v);
                FixtureDef fd = new FixtureDef();
                fd.friction = 0;
                fd.shape = cs;
                fd.filter.categoryBits = bits;
                fd.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_PLAYER_HEAD | B2DVars.BIT_BULLET;
                world.createBody(bdef).createFixture(fd).setUserData("block");
                cs.dispose();
            }
        }

    }

    /**
     * Set up box2d bodies for crystals in tile map "crystals" layer
     */
    private void createCrystals() {

        // create list of crystals
        crystals = new Array<Crystal>();

        // get all crystals in "crystals" layer,
        // create bodies for each, and add them
        // to the crystals list
        MapLayer ml = tileMap.getLayers().get("crystals");
        if(ml == null) return;

        for(MapObject mo : ml.getObjects()) {
            BodyDef cdef = new BodyDef();
            cdef.type = BodyDef.BodyType.StaticBody;
            float x = mo.getProperties().get("x", Float.class) / PPM;
            float y = mo.getProperties().get("y", Float.class) / PPM;
            cdef.position.set(x, y);
            Body body = world.createBody(cdef);
            FixtureDef cfdef = new FixtureDef();
            CircleShape cshape = new CircleShape();
            cshape.setRadius(8 / PPM);
            cfdef.shape = cshape;
            cfdef.isSensor = true;
            cfdef.filter.categoryBits = B2DVars.BIT_CRYSTAL;
            cfdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_PLAYER_HEAD;
            body.createFixture(cfdef).setUserData("crystal");
            Crystal c = new Crystal(body);
            body.setUserData(c);
            crystals.add(c);
            cshape.dispose();
        }
    }

    /**
     * Set up box2d bodies for spikes in "spikes" layer
     */
    private void createSpikes() {

        spikes = new Array<Spike>();

        MapLayer ml = tileMap.getLayers().get("spikes");
        if(ml == null) return;

        for(MapObject mo : ml.getObjects()) {
            BodyDef cdef = new BodyDef();
            cdef.type = BodyDef.BodyType.StaticBody;
            float x = mo.getProperties().get("x", Float.class) / PPM;
            float y = mo.getProperties().get("y", Float.class) / PPM;
            cdef.position.set(x, y);
            Body body = world.createBody(cdef);
            FixtureDef cfdef = new FixtureDef();
            CircleShape cshape = new CircleShape();
            cshape.setRadius(5 / PPM);
            cfdef.shape = cshape;
            cfdef.isSensor = true;
            cfdef.filter.categoryBits = B2DVars.BIT_SPIKE;
            cfdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_PLAYER_HEAD;
            body.createFixture(cfdef).setUserData("spike");
            Spike s = new Spike(body);
            body.setUserData(s);
            spikes.add(s);
            cshape.dispose();
        }

    }

    /**
     * Creates the Landing Pad.
     * Sets up the box2d body and sprites.
     */
    private void createLandingPad() {

        // create list of pads
        pads = new Array<LandingPad>();

        // get all crystals in "crystals" layer,
        // create bodies for each, and add them
        // to the crystals list
        MapLayer ml = tileMap.getLayers().get("pads");
        if(ml == null) return;

        for(MapObject mo : ml.getObjects()) {
            BodyDef cdef = new BodyDef();
            cdef.type = BodyDef.BodyType.StaticBody;
            float x = mo.getProperties().get("x", Float.class) / PPM;
            float y = mo.getProperties().get("y", Float.class) / PPM;
            cdef.position.set(x, y);
            Body body = world.createBody(cdef);
            FixtureDef cfdef = new FixtureDef();
            // create box shape for player collision box
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(13 / PPM, 5 / PPM);
            cfdef.shape = shape;
            cfdef.isSensor = true;
            cfdef.filter.categoryBits = B2DVars.BIT_LANDING_PAD;
            cfdef.filter.maskBits = B2DVars.BIT_PLAYER;
            body.createFixture(cfdef).setUserData("pad");
            LandingPad p = new LandingPad(body);
            body.setUserData(p);
            pads.add(p);
            shape.dispose();
        }
    }


    /**
     * Creates the Left.
     * Sets up the box2d body and sprites.
     */
    private void createLJets() {

        // create list of pads
        jets = new Array<Jet>();

        // get all crystals in "crystals" layer,
        // create bodies for each, and add them
        // to the crystals list
        MapLayer ml = tileMap.getLayers().get("leftJets");
        if(ml == null) return;

        for(MapObject mo : ml.getObjects()) {
            BodyDef cdef = new BodyDef();
            cdef.type = BodyDef.BodyType.StaticBody;
            float x = mo.getProperties().get("x", Float.class) / PPM;
            float y = mo.getProperties().get("y", Float.class) / PPM;
            cdef.position.set(x, y);
            Body body = world.createBody(cdef);
            FixtureDef cfdef = new FixtureDef();
            // create box shape for player collision box
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(96 / PPM, 64 / PPM);
            cfdef.shape = shape;
            //cfdef.isSensor = true;
            cfdef.filter.categoryBits = B2DVars.BIT_JET;
            cfdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_PLAYER_HEAD;
            body.createFixture(cfdef).setUserData("jet");
            Jet j = new Jet(body);
            body.setUserData(j);
            jets.add(j);
            shape.dispose();
        }
    }


    /**
     * Set up box2d bodies for weeds in "weeds" layer
     */
    private void createWeeds() {
        weeds = new Array<Seaweed>();
        MapLayer ml = tileMap.getLayers().get("seaweed");
        if(ml == null) return;

        for(MapObject mo : ml.getObjects()) {
            BodyDef cdef = new BodyDef();
            cdef.type = BodyDef.BodyType.StaticBody;
            float x = mo.getProperties().get("x", Float.class) / PPM;
            float y = mo.getProperties().get("y", Float.class) / PPM;
            cdef.position.set(x, y);
            Body body = world.createBody(cdef);
            FixtureDef cfdef = new FixtureDef();
            CircleShape cshape = new CircleShape();
            cshape.setRadius(10 / PPM);
            cfdef.shape = cshape;
            cfdef.isSensor = true;
            cfdef.filter.categoryBits = B2DVars.BIT_WEED;
            cfdef.filter.maskBits = B2DVars.BIT_PLAYER;
            body.createFixture(cfdef).setUserData("seaweed");
            Seaweed w = new Seaweed(body);
            body.setUserData(w);
            weeds.add(w);
            cshape.dispose();
        }

    }

    /**
     * Shoot a bullet
     */
    private Bullet createBullets() {
       // bullets = new Array<Bullet>();
        bulletDirection = new Vector2(0, 0);
        BodyDef cdef = new BodyDef();
        cdef.type = BodyDef.BodyType.DynamicBody;
        float x = player.getPosition().x;// + 5f;
        float y = player.getPosition().y;// + player.getHeight();
        cdef.position.set(x, y);
        Body body = world.createBody(cdef);
        FixtureDef cfdef = new FixtureDef();
        CircleShape cshape = new CircleShape();
        cshape.setRadius(2 / PPM);
        cfdef.shape = cshape;
        //cfdef.isSensor = true;
        cfdef.filter.categoryBits = B2DVars.BIT_BULLET;
        cfdef.filter.maskBits = B2DVars.BIT_RED_BLOCK;
        body.createFixture(cfdef).setUserData("bullet");
        body.isBullet();
        bulletDirection.x = (float) Math.cos(player.getBody().getAngle() + 1.57f);
        bulletDirection.y = (float) Math.sin(player.getBody().getAngle() + 1.57f);
        if (bulletDirection.len() > 0) {
            bulletDirection = bulletDirection.nor();
        }
        body.applyForce(bulletDirection.x, bulletDirection.y, 0f, 0f, true);
        body.applyLinearImpulse(bulletDirection.x * 50 / PPM, bulletDirection.y * 50 / PPM, 0f, 0f, true);
        Bullet b = new Bullet(body);
        body.setUserData(b);

        cshape.dispose();
        return b;
    }

    /**
     * Switch player mask bits to next block.
     */
    private void switchBlocks() {

        // get player foot mask bits
        Filter filter = player.getBody().getFixtureList().get(1).getFilterData();
        short bits = filter.maskBits;

        // switch to next block bit
        // red -> green -> blue
        if(bits == B2DVars.BIT_RED_BLOCK) {
            bits = B2DVars.BIT_GREEN_BLOCK;
        }
        else if(bits == B2DVars.BIT_GREEN_BLOCK) {
            bits = B2DVars.BIT_BLUE_BLOCK;
        }
        else if(bits == B2DVars.BIT_BLUE_BLOCK) {
            bits = B2DVars.BIT_RED_BLOCK;
        }

        // set player foot mask bits
        filter.maskBits = bits;
        player.getBody().getFixtureList().get(1).setFilterData(filter);

        // set player mask bits
        bits |= B2DVars.BIT_CRYSTAL | B2DVars.BIT_SPIKE;
        filter.maskBits = bits;
        player.getBody().getFixtureList().get(0).setFilterData(filter);

        // play sound
        Game.res.getSound("changeblock").play();

    }

    private void playerShoot(){
        if(bulletsFiredIndex < Integer.parseInt(tileMap.getProperties().get("BulletCount", String.class))) {
            bulletsFired.add(createBullets());
            System.out.println("Bullet Created");
            bulletsFiredIndex++;
        }
    }

    public void handleInput() {

        // keyboard input
        if(FathomInput.isPressed(FathomInput.SHOOT)) {
            //playerShoot();
        }
        if(FathomInput.isPressed(FathomInput.BUTTON2)) {
            switchBlocks();
        }
        if(FathomInput.isDown(FathomInput.ACCELERATE)){
            player.accelerate();
        }
        if(FathomInput.isDown(FathomInput.TURN_LEFT)){
            player.turnLeft(accelerometer);
        }
        if(FathomInput.isDown(FathomInput.TURN_RIGHT)){
            player.turnRight(accelerometer);
        }

        // mouse/touch input for android
        if(FathomInput.isPressed()) {
            if(FathomInput.x < Gdx.graphics.getWidth() / 2) {
                playerShoot();
            } else {
                //playerShoot();
            }
        }

        if(FathomInput.isDown()){
            if(FathomInput.x > Gdx.graphics.getWidth() / 2){
                player.accelerate();
            }
        }

        accelerometer = Gdx.input.getAccelerometerY();
        if(accelerometer < 0){
            player.turnLeft(accelerometer);
            System.out.println("ACCEL: " + Gdx.input.getAccelerometerY());
        } else if(accelerometer > 0){
            player.turnRight(accelerometer);
            System.out.println("ACCEL: " + Gdx.input.getAccelerometerY());
        }

    }

    public void update(float dt) {

        // check input
        handleInput();

        // update box2d world
        world.step(Game.STEP, 1, 1);

        // check for collected crystals
        Array<Body> bodies = cl.getCollectedCrystals();
        for(int i = 0; i < bodies.size; i++) {
            Body b = bodies.get(i);
            crystals.removeValue((Crystal) b.getUserData(), true);
            world.destroyBody(bodies.get(i));
            player.collectCrystal();
            Game.res.getSound("crystal").play();
        }
        bodies.clear();
/*
        Array<Body> bulletBodies = cl.getDeadBullets();
        for(int i = 0; i < bulletBodies.size; i++) {
            Body b = bulletBodies.get(i);
            bulletsFired.removeValue((Bullet) b.getUserData(), true);
            world.destroyBody(bulletBodies.get(i));
            System.out.println("Bullet destroyed");
        }
        bulletBodies.clear();
*/



        // check player win
        if(player.getBody().getPosition().x * PPM > tileMapWidth * tileSize) {
            Game.res.getSound("levelselect").play();

        }
        /*
         * This part doesn't seem necessary for what we're trying to achieve
        *
        if(player.getBody().getLinearVelocity().x < 0.001f) {
            Game.res.getSound("hit").play();
            gsm.setState(GameStateManager.MENU);
        }
        */
        if(cl.isPlayerDead()) {

            if(hit == false) {
                Game.res.getSound("hit").play();
                particleManager.addEffect(ParticleManager.EffectType.Yellow, player.getPosition().x * PPM, player.getPosition().y * PPM);
                hit = true;
            }
            Game.res.getMusic("bbsong").stop();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    gsm.setState(GameStateManager.MENU);

                }
            }, 3);
        } else if(!cl.isPlayerDead()){
            // update player
            player.setDamage(cl.getDamage());
            player.update(dt);
        }

        if(cl.didPlayerWin()){
            Game.res.getSound("levelselect").play();
            Game.res.getMusic("bbsong").stop();
            gsm.setState(GameStateManager.NEXT_LEVEL);
        }

        // update crystals
        for(int i = 0; i < crystals.size; i++) {
            crystals.get(i).update(dt);
        }

        // update spikes
        for(int i = 0; i < spikes.size; i++) {
            spikes.get(i).update(dt);
        }

        // update weeds
        for(int i = 0; i < weeds.size; i++){
            weeds.get(i).update(dt);
        }

        // update landing pad
        for(int i = 0; i < pads.size; i++){
            pads.get(i).update(dt);
        }

        // update jets
        for(int i = 0; i < jets.size; i++){
            jets.get(i).update(dt);
        }

        //for (int i = 0; i < bullets.size; i++) {
           // bullets.get(i).update(dt);
        //}

        if(bulletsFired != null) {
            for (int i = 0; i < bulletsFired.size; i++) {
                bulletsFired.get(i).update(dt);
            }
        }

        //if(bullet != null) {
          //  bullet.update(dt);
        //}
    }

    public void render() {

        // camera follow player
        if(!cl.isPlayerDead()) {
            cam.setPosition(player.getPosition().x * PPM + Game.V_WIDTH / 8, player.getPosition().y * PPM + Game.V_HEIGHT / 8);
        }
        cam.update();


        // draw bgs
        sb.setProjectionMatrix(hudCam.combined);
        for(int i = 0; i < backgrounds.length; i++) {
            backgrounds[i].render(sb);
        }

        // draw tilemap
        tmRenderer.setView(cam);
        tmRenderer.render();

        // draw player
        sb.setProjectionMatrix(cam.combined);
        if(!cl.isPlayerDead()) {
            player.render(sb);
        }

        particleManager.render(sb);

        // draw crystals
        for(int i = 0; i < crystals.size; i++) {
            crystals.get(i).render(sb);
        }

        // draw spikes
        for(int i = 0; i < spikes.size; i++) {
            spikes.get(i).render(sb);
        }

        // draw weeds
        for(int i = 0; i < weeds.size; i++){
            weeds.get(i).render(sb);
        }

        // draw landing pad
        for(int i = 0; i < pads.size; i++){
            pads.get(i).render(sb);
        }

        // draw jet
        for(int i = 0; i < jets.size; i++){
            jets.get(i).render(sb);
        }

        if(bulletsFired != null) {
            for (int i = 0; i < bulletsFired.size; i++) {
                bulletsFired.get(i).render(sb);
            }
        }

       //if(bullet != null) {
         //   bullet.render(sb);
        //}

        // draw hud
        sb.setProjectionMatrix(hudCam.combined);
        hud.render(sb);

        // debug draw box2d
        if(debug) {
            //b2dCam.setPosition(player.getPosition().x + Game.V_WIDTH / 4 / PPM, Game.V_HEIGHT / 2 / PPM);
            b2dCam.setPosition(player.getPosition().x + Game.V_WIDTH / 8 / PPM, player.getPosition().y + Game.V_HEIGHT / 8 / PPM);
            b2dCam.update();
            b2dRenderer.render(world, b2dCam.combined);
            //fps.log();
        }

    }

    public void dispose() {
        // everything is in the resource manager com.neet.blockbunny.handlers.Content
    }

}