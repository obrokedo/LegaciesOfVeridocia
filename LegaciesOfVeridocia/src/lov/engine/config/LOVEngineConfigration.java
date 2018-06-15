package lov.engine.config;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;

import lov.engine.config.loading.LOVFirstLoadRenderer;
import lov.engine.config.loading.LOVLogoLoadRenderer;
import tactical.engine.config.BattleEffectFactory;
import tactical.engine.config.BattleFunctionConfiguration;
import tactical.engine.config.CinematicActorConfiguration;
import tactical.engine.config.DefaultEngineConfiguration;
import tactical.engine.config.EngineConfigurationValues;
import tactical.engine.config.LevelProgressionConfiguration;
import tactical.engine.config.MenuConfiguration;
import tactical.engine.config.MusicConfiguration;
import tactical.engine.config.SpellFactory;
import tactical.loading.LoadingScreenRenderer;

public class LOVEngineConfigration extends DefaultEngineConfiguration {
	public LOVEngineConfigration() {
		healthPanelRenderer = new LOVHealthPanelRenderer();
		spellMenuRenderer = new LOVSpellMenuRenderer();
		yesNoMenuRenderer = new LOVYesNoMenuRenderer();
		panelRenderer = new LOVPanelRenderer();
	}
	
	@Override
	public SpellFactory getSpellFactory() {
		return LOVGlobalPythonFactory.getSpellFactory();
	}

	@Override
	public BattleEffectFactory getBattleEffectFactory() {
		return LOVGlobalPythonFactory.getBattleEffectFactory();
	}
	
	@Override
	public CinematicActorConfiguration getCinematicActorConfiguration() {
		return LOVGlobalPythonFactory.createJCinematicActor();
	}

	@Override
	public BattleFunctionConfiguration getBattleFunctionConfiguration() {
		return LOVGlobalPythonFactory.createJBattleFunctions();
	}

	/*
	@Override
	public PanelRenderer getPanelRenderer() {
		return GlobalPythonFactory.createJPanelRender();
	}
	*/

	@Override
	public MusicConfiguration getMusicConfiguration() {
		return LOVGlobalPythonFactory.createJMusicSelector();
	}

	@Override
	public LevelProgressionConfiguration getLevelProgression() {
		return LOVGlobalPythonFactory.createLevelProgression();
	}

	@Override
	public EngineConfigurationValues getConfigurationValues() {
		return LOVGlobalPythonFactory.createConfigurationValues();
	}
	
	@Override
	public MenuConfiguration getMenuConfiguration() {
		return LOVGlobalPythonFactory.createMenuConfig();
	}
	
	@Override
	public LoadingScreenRenderer getLogoLoadScreenRenderer(GameContainer container) {
		return new LOVLogoLoadRenderer(container);
	}

	@Override
	public LoadingScreenRenderer getFirstLoadScreenRenderer(GameContainer container, Music mainMenuMusic) {
		return new LOVFirstLoadRenderer(container, mainMenuMusic);
	}

	@Override
	public LoadingScreenRenderer getLoadScreenRenderer(GameContainer container) {
		return new LoadingScreenRenderer(container);
	}

	@Override
	public void initialize() {
		LOVGlobalPythonFactory.intialize();
	}
}
