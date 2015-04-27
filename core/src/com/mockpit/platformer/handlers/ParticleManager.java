package com.mockpit.platformer.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class ParticleManager {

	//Particle Objects
	private ParticleEffectPool yellowfeatherPool;
	private Array<PooledEffect> effects = new Array<PooledEffect>();
	
	public enum EffectType { 
    	Yellow
   }
	
	public ParticleManager() {
		ParticleEffect particleEffect = new ParticleEffect();
		
		particleEffect.load(Gdx.files.internal("effects/chickpop.p"), Gdx.files.internal("effects"));
		yellowfeatherPool = new ParticleEffectPool(particleEffect, 1, 2);
	}
	
	public void addEffect(EffectType type, float x, float y) {
		PooledEffect effect;
		
		switch (type) {
		case Yellow:
			effect = yellowfeatherPool.obtain();
			break;
		default:
			effect = yellowfeatherPool.obtain();
			break;
		}
		
		effect.setPosition(x, y);
		effects.add(effect);
	}
	
	public void render(SpriteBatch sb) {
        sb.begin();
		for (int i = effects.size - 1; i >= 0; i--) {
		    PooledEffect effect = effects.get(i);
		    effect.draw(sb, Gdx.graphics.getDeltaTime());
		    if (effect.isComplete()) {
		        effect.free();
		        effects.removeIndex(i);
		        effect.dispose();
		    }
		}
        sb.end();
	}
}
