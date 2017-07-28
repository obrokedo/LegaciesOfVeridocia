package mb.fc.particle;

import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleSystem;

import mb.fc.utils.AnimationWrapper;

public class AnimatedParticle extends Particle
{
	private AnimationWrapper animWrapper;
	private boolean flipHorz = false;
	
	public AnimatedParticle(AnimationWrapper animWrapper, ParticleSystem engine) {
		super(engine);
		this.animWrapper = animWrapper;
	}
	
	@Override
	public void update(int delta) {
		animWrapper.update(delta);
		super.update(delta);
	}

	@Override
	public void render() {
		animWrapper.drawAnimationDirect((int) x,  (int) y, null, flipHorz);
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