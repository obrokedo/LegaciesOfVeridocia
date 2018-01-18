package mb.fc.engine.config;

import mb.jython.GlobalPythonFactory;

public class LOVEngineConfigration extends DefaultEngineConfiguration {
	public LOVEngineConfigration() {
		healthPanelRenderer = new LOVHealthPanelRenderer();
		spellMenuRenderer = new LOVSpellMenuRenderer();
		yesNoMenuRenderer = new LOVYesNoMenuRenderer();
	}
	
	@Override
	public SpellFactory getSpellFactory() {
		return GlobalPythonFactory.getSpellFactory();
	}

	@Override
	public BattleEffectFactory getBattleEffectFactory() {
		return GlobalPythonFactory.getBattleEffectFactory();
	}
	
	@Override
	public CinematicActorConfiguration getCinematicActorConfiguration() {
		return GlobalPythonFactory.createJCinematicActor();
	}

	@Override
	public BattleFunctionConfiguration getBattleFunctionConfiguration() {
		return GlobalPythonFactory.createJBattleFunctions();
	}

	@Override
	public PanelRenderer getPanelRenderer() {
		return GlobalPythonFactory.createJPanelRender();
	}

	@Override
	public MusicConfiguration getMusicConfiguration() {
		return GlobalPythonFactory.createJMusicSelector();
	}

	@Override
	public LevelProgressionConfiguration getLevelProgression() {
		return GlobalPythonFactory.createLevelProgression();
	}

	@Override
	public EngineConfigurationValues getConfigurationValues() {
		return GlobalPythonFactory.createConfigurationValues();
	}
	
	@Override
	public MenuConfiguration getMenuConfiguration() {
		return GlobalPythonFactory.createMenuConfig();
	}

	@Override
	public void initialize() {
		GlobalPythonFactory.intialize();
	}
}
