package com.mockpit.platformer.handlers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.mockpit.platformer.Game;

/**
 * Created by The Godfather on 08/03/2015.
 */
public class FathomContactListener implements ContactListener {

    private int numFootContacts;
    private Array<Body> crystalsToRemove;
    private Array<Body> bulletsToRemove;
    private boolean playerDead;
    private boolean playerWins;
    private float bang;

    public FathomContactListener() {
        super();
        crystalsToRemove = new Array<Body>();
        bulletsToRemove = new Array<Body>();
    }

    public void beginContact(Contact contact) {

        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa == null || fb == null) return;

        if(fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts++;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")) {
            numFootContacts++;
        }

        if(fa.getUserData() != null && fa.getUserData().equals("crystal")) {
            crystalsToRemove.add(fa.getBody());
        }
        if(fb.getUserData() != null && fb.getUserData().equals("crystal")) {
            crystalsToRemove.add(fb.getBody());
        }

        if(fa.getUserData() != null && fa.getUserData().equals("spike")) {
            playerDead = true;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("spike")) {
            playerDead = true;
        }
        if(fa.getUserData() != null && fa.getUserData().equals("pad")) {
            if(fb.getBody().getLinearVelocity().y > -0.3) {
                playerWins = true;
            } else {
                playerDead = true;
            }

        }
        if(fb.getUserData() != null && fb.getUserData().equals("pad")) {
            if(fa.getBody().getLinearVelocity().y > -0.3) {
                playerWins = true;
            } else {
                playerDead = true;
            }
        }
        if(fa.getUserData() != null && fa.getUserData().equals("bullet")) {
            System.out.println("bullet contact");
            bulletsToRemove.add(fa.getBody());
        }
        if(fb.getUserData() != null && fb.getUserData().equals("bullet")) {
            System.out.println("bullet contact");
            bulletsToRemove.add(fb.getBody());
        }
        if(fa.getUserData() != null && fa.getUserData().equals("block")) {
            System.out.println("Wall bang" + bang);
            bang += (fb.getBody().getLinearVelocity().x <= 0.0F) ? 0.0F - fb.getBody().getLinearVelocity().x : fb.getBody().getLinearVelocity().x;
            bang += (fb.getBody().getLinearVelocity().y <= 0.0F) ? 0.0F - fb.getBody().getLinearVelocity().y : fb.getBody().getLinearVelocity().y;
            if(bang<50f)Game.res.getSound("bump").play();
            else if(bang>50f){ playerDead = true; }
        }
        if(fb.getUserData() != null && fb.getUserData().equals("block")) {
            System.out.println("Wall bang" + bang);
            bang += (fa.getBody().getLinearVelocity().x <= 0.0F) ? 0.0F - fa.getBody().getLinearVelocity().x : fa.getBody().getLinearVelocity().x;
            bang += (fa.getBody().getLinearVelocity().y <= 0.0F) ? 0.0F - fa.getBody().getLinearVelocity().y : fa.getBody().getLinearVelocity().y;
            if(bang<50f)Game.res.getSound("bump").play();
            else if(bang>50f){ playerDead = true; }
        }
        if(fa.getUserData() != null && fa.getUserData().equals("jet")) {
            // I have to figure out a way to apply a force to the ship while it's in contact with the jet
            //fb.getBody().applyForceToCenter(fb.getBody().getLinearVelocity().x - 0.05f, 0f, false);
            System.out.println("Pushing to left");
        }
        if(fb.getUserData() != null && fb.getUserData().equals("jet")) {
            // I have to figure out a way to apply a force to the ship while it's in contact with the jet
            //fa.getBody().applyForceToCenter(fa.getBody().getLinearVelocity().x - 0.5f, 0, false);
            System.out.println("Pushing to left");
        }

    }

    public void endContact(Contact contact) {

        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa == null || fb == null) return;

        if(fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts--;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")) {
            numFootContacts--;
        }

    }

    public boolean playerCanJump() { return numFootContacts > 0; }
    public Array<Body> getCollectedCrystals() { return crystalsToRemove; }
    public Array<Body> getDeadBullets() { return bulletsToRemove; }
    public boolean isPlayerDead() { return playerDead; }
    public boolean didPlayerWin() { return playerWins; }
    public float getDamage(){ return bang; }

    public void preSolve(Contact c, Manifold m) {  }

    public void postSolve(Contact c, ContactImpulse ci) {}

}