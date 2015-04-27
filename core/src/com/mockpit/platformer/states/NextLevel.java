package com.mockpit.platformer.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mockpit.platformer.Game;
import com.mockpit.platformer.handlers.Background;
import com.mockpit.platformer.handlers.GameButton;
import com.mockpit.platformer.handlers.GameStateManager;

/**
 * Created by The Godfather on 16/03/2015.
 */
public class NextLevel extends GameState {

    private TextureRegion reg;

    private TiledMap tileMap;

    private Background bg;

    private GameButton playButton;

    private BitmapFont font;

    private String summary = "";

    public NextLevel(GameStateManager gsm) {

        super(gsm);

        Texture tex = Game.res.getTexture("menu");
        reg = new TextureRegion(Game.res.getTexture("bgs"), 0, 0, 320, 240);
        bg = new Background(new TextureRegion(tex), cam, 1f);
        bg.setVector(-20, 0);
        font = new BitmapFont();

        //TextureRegion buttonReg = new TextureRegion(Game.res.getTexture("hud"), 0, 0, 32, 32);
        //buttons = new GameButton[5][5];
        //for(int row = 0; row < buttons.length; row++) {
        //    for(int col = 0; col < buttons[0].length; col++) {
        //        buttons[row][col] = new GameButton(buttonReg, 80 + col * 40, 200 - row * 40, cam);
        //        buttons[row][col].setText(row * buttons[0].length + col + 1 + "");
        //    }
        //}

        //TextureRegion buttonReg = new TextureRegion(Game.res.getTexture("hud"), 58, 40, 95, 58);
        tex = Game.res.getTexture("hud");
        playButton = new GameButton(new TextureRegion(tex, 58, 40, 29, 30), 160, 100, cam);

        try {
            tileMap = new TmxMapLoader().load("maps/level" + Play.level++ + ".tmx");
        }
        catch(Exception e) {
            System.out.println("Cannot find file: maps/level" + Play.level++ + ".tmx");
            Gdx.app.exit();
        }

        if(tileMap.getProperties().get("Summary") != null) {
            System.out.println("Story:\n\n" + tileMap.getProperties().get("Summary", String.class));
            summary = tileMap.getProperties().get("Summary", String.class);
            StringBuilder strb = new StringBuilder(summary);

            int i = 0;
            while (i + 40 < strb.length() && (i = strb.lastIndexOf(" ", i + 40)) != -1) {
                strb.replace(i, i + 1, "\n");
            }
            summary = strb.toString();

            System.out.println(strb.toString());
        }
        //System.out.println("String length: " + font.getBounds(summary).width);

        cam.setToOrtho(false, Game.V_WIDTH, Game.V_HEIGHT);
        Game.res.getMusic("next_level").play();

    }

    public void handleInput() {

        if(playButton.isClicked()) {
            Game.res.getSound("crystal").play();
            //Play.level++;
            gsm.setState(GameStateManager.PLAY);
        }

    }

    public void update(float dt) {

        handleInput();

        playButton.update(dt);

        bg.update(dt);

    }

    public void render() {

        sb.setProjectionMatrix(cam.combined);
        bg.render(sb);
        playButton.render(sb);
        sb.begin();
        //sb.draw(playButton, 0, 0);
        font.drawWrapped(sb, summary, Game.V_WIDTH / 10f, Game.V_HEIGHT - Game.V_HEIGHT / 10f, Game.V_WIDTH);
        sb.end();


    }

    public void dispose() {
        // everything is in the resource manager com.neet.blockbunny.handlers.Content
    }
}

