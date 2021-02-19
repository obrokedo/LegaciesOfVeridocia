package lov.engine;

import lov.engine.config.LOVEngineConfigration;
import lov.engine.config.LOVGlobalPythonFactory;
import tactical.engine.TacticalGame;
import tactical.engine.config.EngineConfigurator;

/**
 * Entry point to the LoVgame
 *
 * @author Broked
 *
 */
public class LoVGame extends TacticalGame   {

	public static final String LOV_VERSION = "DEV 1.407 Feb 19, 2021";
	/**
	 * Entry point into the game
	 */
	public static void main(String args[])
	{						
		LoVGame rpg = new LoVGame(true, args);		
		LOVGlobalPythonFactory.intialize();
		rpg.setup();
	}
	
	public LoVGame(boolean devMode, String[] gameArgs) {
		super("Legacies of Veridocia", LOV_VERSION , devMode, gameArgs);
		
	}

	@Override
	public EngineConfigurator getEngineConfigurator() {
		return new LOVEngineConfigration();
	}	
}
