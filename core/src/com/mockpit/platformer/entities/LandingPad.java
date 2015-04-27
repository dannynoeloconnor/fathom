package com.mockpit.platformer.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mockpit.platformer.Game;

/**
 * Created by The Godfather on 10/03/2015.
 */
public class LandingPad extends B2DSprite {

    public LandingPad(Body body) {

        super(body);
        Texture tex = Game.res.getTexture("landing-pad");
        TextureRegion[] sprites = TextureRegion.split(tex, 40, 10)[0];
        animation.setFrames(sprites, 1);

        width = tex.getWidth();
        height = tex.getHeight();

    }

}
