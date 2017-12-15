package mb.fc.engine.config;

import mb.fc.engine.config.intr.AnimationParser;

public interface EngineConfigurator {
	public void getAttackCinematic();
	
	public AnimationParser getAnimationParser();
	
	public HealthPanelRenderer getHealthPanelRenderer();
	
	public SpellMenuRenderer getSpellMenuRenderer();
	
	public SpellFactory getSpellFactory();
	
	public BattleEffectFactory getBattleEffectFactory();
	
	public CinematicActorConfiguration getCinematicActorConfiguration();
	
	public BattleFunctionConfiguration getBattleFunctionConfiguration();
	
	public PanelRenderer getPanelRenderer();
	
	public MusicConfiguration getMusicConfiguration();

	public LevelProgressionConfiguration getLevelProgression();

	public EngineConfigurationValues getConfigurationValues();

	public MenuConfiguration getMenuConfiguration();
	
	
}
