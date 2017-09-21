package mb.fc.particle;

import org.newdawn.slick.Image;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleSystem;

import mb.fc.loading.FCResourceManager;
import mb.fc.utils.AnimationWrapper;
import mb.fc.utils.SpriteAnims;

public class AnimatedParticleSystem extends ParticleSystem
{
	private String animationName;
	private SpriteAnims spriteAnims;
	private float scale;
	
	public AnimatedParticleSystem(String spriteAnimsName, 
			String animationName, FCResourceManager frm, float scale) {
		// Kind of a kludge but we really don't want to load a default image here
		super(new Image() 
		{
			@Override
			public void endUse() {}

			@Override
			public void startUse() {}
		});
		this.scale = scale;
		this.spriteAnims = frm.getSpriteAnimation(spriteAnimsName);
		this.animationName = animationName;
	}

	@Override
	protected Particle createParticle(ParticleSystem system) {
		if (spriteAnims == null) {
			return super.createParticle(system);
		}
		return new AnimatedParticle(new AnimationWrapper(spriteAnims, animationName, true), system, scale);
	}
}