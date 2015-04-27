package com.mockpit.platformer.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mockpit.platformer.Game;

/**
 * Created by The Godfather on 17/03/2015.
 */
public class Jet extends B2DSprite {

    public Jet(Body body) {

        super(body);
        Texture tex = Game.res.getTexture("jet");
        TextureRegion[] sprites = TextureRegion.split(tex, 96, 64)[0];
        animation.setFrames(sprites, 1);

        width = sprites[0].getRegionWidth();
        height = sprites[0].getRegionHeight();

    }

}
