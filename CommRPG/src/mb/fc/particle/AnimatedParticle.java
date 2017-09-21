package mb.fc.particle;

import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleSystem;

import mb.fc.utils.AnimationWrapper;

public class AnimatedParticle extends Particle
{
	private AnimationWrapper animWrapper;
	private boolean flipHorz = false;
	private float scale;
	
	public AnimatedParticle(AnimationWrapper animWrapper, ParticleSystem engine, float scale) {
		super(engine);
		this.animWrapper = animWrapper;
		this.scale = scale;
	}
	
	@Override
	public void update(int delta) {
		animWrapper.update(delta);
		super.update(delta);
	}

	@Override
	public void render() {
		animWrapper.drawAnimationDirect((int) x,  (int) y, scale, flipHorz);
	}
	
	public void flipHorizontal(boolean flip)
	{
		this.flipHorz = flip;
	}
	
	public void flipVertical()
	{
		
	}
	
	public void rotate(float angle)
	{
		
	}
}