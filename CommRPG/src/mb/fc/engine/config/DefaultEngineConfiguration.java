package mb.fc.engine.config;

import mb.fc.engine.config.intr.AnimationParser;

public class DefaultEngineConfiguration implements EngineConfigurator {
	
	protected DfAnimationParser animationParser = new DfAnimationParser();
	protected HealthPanelRenderer healthPanelRenderer = new DefaultHealthPanelRenderer();
	protected SpellMenuRenderer spellMenuRenderer = new DefaultSpellMenuRenderer();
	
	@Override
	public void getAttackCinematic() {
		// TODO Auto-generated method stub
	}

	@Override
	public AnimationParser getAnimationParser() {
		return animationParser;
	}

	@Override
	public HealthPanelRenderer getHealthPanelRenderer() {
		return healthPanelRenderer;
	}

	@Override
	public SpellMenuRenderer getSpellMenuRenderer() {
		return spellMenuRenderer;
	}
}
