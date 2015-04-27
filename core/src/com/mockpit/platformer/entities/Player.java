package com.mockpit.platformer.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mockpit.platformer.Game;
import com.mockpit.platformer.handlers.B2DVars;

/**
 * Created by The Godfather on 08/03/2015.
 */
public class Player extends B2DSprite {

    private int numBullets;
    private int numCrystals;
    private int totalCrystals;
    private float rotation;
    private float rotationSpeed;
    private float damage;
    private Vector2 direction;


    public Player(Body body) {

        super(body);

        Texture tex = Game.res.getTexture("ship");
        TextureRegion[] sprites = new TextureRegion[4];
        for(int i = 0; i < sprites.length; i++) {
            sprites[i] = new TextureRegion(tex, i * 32, 0, 32, 32);
        }

        animation.setFrames(sprites, 1 / 12f);

        width = sprites[0].getRegionWidth();
        height = sprites[0].getRegionHeight();
        direction = new Vector2(0, 0);
        damage = 0;

        rotationSpeed = 0.02f;
        numBullets = 0; // 10 bullets by default
    }

    /**
     * Accelerate the ship. Adding 1.57 radians - 90 degrees
     */
    public void accelerate(){
        direction.x = (float) Math.cos(body.getAngle() + 1.57f);
        direction.y = (float) Math.sin(body.getAngle() + 1.57f);
        if(direction.len() > 0){
            this.direction = direction.nor();
        }
        body.applyLinearImpulse(direction.x / 10, direction.y / 10, 0f, 0f, true);
    }

    public void turnLeft(float rotationSpeed){
        if(rotationSpeed == 0) {
            rotation += this.rotationSpeed;

        } else {
            rotation += this.rotationSpeed * -rotationSpeed;
        }
        body.setTransform(this.getPosition(), rotation);
    }

    public void turnRight(float rotationSpeed){
        if(rotationSpeed == 0) {
            rotation -= this.rotationSpeed;

        } else {
            rotation -= this.rotationSpeed * rotationSpeed;
        }
        body.setTransform(this.getPosition(), rotation );
    }

    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(animation.getFrame(), (body.getPosition().x * B2DVars.PPM - width / 2), (int) (body.getPosition().y * B2DVars.PPM - height / 2), this.width/2, this.height/2, this.width, this.height, 1, 1, rotation * MathUtils.radiansToDegrees);
        sb.end();
    }

    public void collectCrystal() {
        numCrystals++;
    }

    public int getNumCrystals() {
        return numCrystals;
    }

    public void setNumBullets(int b) {
        if(b != 0) {
            numBullets = b;
        }
    }

    public int getNumBullets() { return numBullets; }
    public Vector2 getDirection() { return this.direction; }
    public float getRotation() { return this.rotation; }
    public void setDamage(float d){ damage = d; }
    public float getDamage(){ return damage; }
    public void setTotalCrystals(int i) { totalCrystals = i; }
    public int getTotalCrystals() { return totalCrystals; }

}
