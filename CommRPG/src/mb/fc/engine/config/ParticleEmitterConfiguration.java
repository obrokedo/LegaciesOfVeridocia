package mb.fc.engine.config;

import org.newdawn.slick.Image;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;

import mb.fc.loading.FCResourceManager;

public abstract class ParticleEmitterConfiguration implements ParticleEmitter
{
	protected FCResourceManager fcResourceManager;
	
	public abstract void initialize(boolean isHero);

	@Override
	public void update(ParticleSystem system, int delta) {
		
	}

	@Override
	public boolean completed() {
		return false;
	}

	@Override
	public void wrapUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateParticle(Particle particle, int delta) {
		
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean useAdditive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOriented() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean usePoints(ParticleSystem system) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetState() {
		// TODO Auto-generated method stub
	}

	public FCResourceManager getFcResourceManager() {
		return fcResourceManager;
	}

	public void setFcResourceManager(FCResourceManager fcResourceManager) {
		this.fcResourceManager = fcResourceManager;
	}
}