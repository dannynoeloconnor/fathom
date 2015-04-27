package com.mockpit.platformer.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mockpit.platformer.Game;

/**
 * Created by The Godfather on 10/03/2015.
 */
public class Seaweed extends B2DSprite {

    public Seaweed(Body body) {

        super(body);
        Texture tex = Game.res.getTexture("seaweed");
        TextureRegion[] sprites = TextureRegion.split(tex, 48, 39)[0];
        animation.setFrames(sprites, 1 / 13f);

        width = sprites[0].getRegionWidth();
        height = sprites[0].getRegionHeight();

    }

}
