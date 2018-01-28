package mb.fc.engine.config;

import mb.fc.engine.config.intr.AnimationParser;

public class DefaultEngineConfiguration implements EngineConfigurator {
	
	protected DfAnimationParser animationParser = new DfAnimationParser();
	protected HealthPanelRenderer healthPanelRenderer = new DefaultHealthPanelRenderer();
	protected SpellMenuRenderer spellMenuRenderer = new DefaultSpellMenuRenderer();
	protected YesNoMenuRenderer yesNoMenuRenderer = new DefaultYesNoRenderer();
	protected CinematicActorConfiguration cinematicActorConfiguration = 
			new DefaultCinematicActorConfiguration();
	
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
	
	@Override
	public YesNoMenuRenderer getYesNoMenuRenderer() {
		// TODO Auto-generated method stub
		return yesNoMenuRenderer;
	}

	@Override
	public SpellFactory getSpellFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BattleEffectFactory getBattleEffectFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CinematicActorConfiguration getCinematicActorConfiguration() {
		// TODO Auto-generated method stub
		return cinematicActorConfiguration;
	}

	@Override
	public BattleFunctionConfiguration getBattleFunctionConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PanelRenderer getPanelRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MusicConfiguration getMusicConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LevelProgressionConfiguration getLevelProgression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EngineConfigurationValues getConfigurationValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuConfiguration getMenuConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}
}
