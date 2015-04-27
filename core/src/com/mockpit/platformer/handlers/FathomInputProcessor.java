package com.mockpit.platformer.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

/**
 * Created by The Godfather on 08/03/2015.
 */
public class FathomInputProcessor extends InputAdapter {

    public boolean mouseMoved(int x, int y) {
        FathomInput.x = x;
        FathomInput.y = y;
        return true;
    }

    public boolean touchDragged(int x, int y, int pointer) {
        FathomInput.x = x;
        FathomInput.y = y;
        FathomInput.down = true;
        return true;
    }

    public boolean touchDown(int x, int y, int pointer, int button) {
        FathomInput.x = x;
        FathomInput.y = y;
        FathomInput.down = true;
        return true;
    }

    public boolean touchUp(int x, int y, int pointer, int button) {
        FathomInput.x = x;
        FathomInput.y = y;
        FathomInput.down = false;
        return true;
    }

    public boolean keyDown(int k) {
        if(k == Keys.SPACE) FathomInput.setKey(FathomInput.SHOOT, true);
        if(k == Keys.X) FathomInput.setKey(FathomInput.BUTTON2, true);
        if(k == Keys.UP) FathomInput.setKey(FathomInput.ACCELERATE, true);
        if(k == Keys.LEFT) FathomInput.setKey(FathomInput.TURN_LEFT, true);
        if(k == Keys.RIGHT) FathomInput.setKey(FathomInput.TURN_RIGHT, true);
        return true;
    }

    public boolean keyUp(int k) {
        if(k == Keys.SPACE) FathomInput.setKey(FathomInput.SHOOT, false);
        if(k == Keys.X) FathomInput.setKey(FathomInput.BUTTON2, false);
        if(k == Keys.UP) FathomInput.setKey(FathomInput.ACCELERATE, false);
        if(k == Keys.LEFT) FathomInput.setKey(FathomInput.TURN_LEFT, false);
        if(k == Keys.RIGHT) FathomInput.setKey(FathomInput.TURN_RIGHT, false);
        return true;
    }

}
