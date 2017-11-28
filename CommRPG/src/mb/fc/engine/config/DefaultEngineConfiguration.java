package mb.fc.engine.config;

import mb.fc.engine.config.intr.AnimationParser;

public class DefaultEngineConfiguration implements EngineConfigurator {
	
	private DfAnimationParser animationParser = new DfAnimationParser();
	
	@Override
	public void getAttackCinematic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AnimationParser getAnimationParser() {
		return animationParser;
	}
}
