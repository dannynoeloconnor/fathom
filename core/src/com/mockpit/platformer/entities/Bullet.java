package com.mockpit.platformer.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mockpit.platformer.Game;

/**
 * Created by The Godfather on 12/03/2015.
 */
public class Bullet extends B2DSprite {

    public Bullet(Body body) {

        super(body);
        Texture tex = Game.res.getTexture("bullet");
        TextureRegion[] sprites = TextureRegion.split(tex, 3, 3)[0];
        animation.setFrames(sprites, 1);

        width = tex.getWidth();
        height = tex.getHeight();

    }

}
