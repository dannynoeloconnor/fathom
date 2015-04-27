package com.mockpit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mockpit.platformer.handlers.BoundedCamera;
import com.mockpit.platformer.handlers.Content;
import com.mockpit.platformer.handlers.FathomInput;
import com.mockpit.platformer.handlers.FathomInputProcessor;
import com.mockpit.platformer.handlers.GameStateManager;

public class Game extends ApplicationAdapter {

    public static final String TITLE = "Fathom";
    public static final int V_WIDTH = 320;
    public static final int V_HEIGHT = 240;
    public static final int SCALE = 2;
    public static final float STEP = 1 / 60f;

    private SpriteBatch sb;
    private BoundedCamera cam;
    private OrthographicCamera hudCam;

    private GameStateManager gsm;

    public static Content res;

    public void create() {

       // Texture.setEnforcePotImages(false);
        Gdx.input.setInputProcessor(new FathomInputProcessor());

        res = new Content();
        res.loadTexture("images/menu.png");
        res.loadTexture("images/bgs.png");
        res.loadTexture("images/hud.png");
        res.loadTexture("images/ship.png");
        res.loadTexture("images/crystal.png");
        res.loadTexture("images/spikes.png");
        res.loadTexture("images/seaweed.png");
        res.loadTexture("images/landing-pad.png");
        res.loadTexture("images/bullet.png");
        res.loadTexture("images/jet.png");

        res.loadSound("sfx/jump.wav");
        res.loadSound("sfx/crystal.wav");
        res.loadSound("sfx/levelselect.wav");
        res.loadSound("sfx/hit.wav");
        res.loadSound("sfx/changeblock.wav");
        res.loadSound("sfx/bump.wav");
        res.loadSound("sfx/enemy_explode.wav");
        res.loadSound("sfx/shot.wav");

        res.loadMusic("music/bbsong.ogg");
        res.getMusic("bbsong").setLooping(true);
        res.getMusic("bbsong").setVolume(0.5f);

        res.loadMusic("music/next_level.ogg");
        res.getMusic("next_level").setVolume(0.5f);

        //res.getMusic("bbsong").play();

        cam = new BoundedCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        hudCam = new OrthographicCamera();
        hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);

        sb = new SpriteBatch();

        gsm = new GameStateManager(this);

    }

    public void render() {

        Gdx.graphics.setTitle(TITLE + " -- FPS: " + Gdx.graphics.getFramesPerSecond());

        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render();
        FathomInput.update();

    }

    public void dispose() {
        res.removeAll();
    }

    public void resize(int w, int h) {}

    public void pause() {}

    public void resume() {}

    public SpriteBatch getSpriteBatch() { return sb; }
    public BoundedCamera getCamera() { return cam; }
    public OrthographicCamera getHUDCamera() { return hudCam; }
}
