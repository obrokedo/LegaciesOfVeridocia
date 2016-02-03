package mb.fc.utils.planner;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import mb.fc.game.constants.AttributeStrength;
import mb.jython.GlobalPythonFactory;
import mb.jython.JConfigurationValues;

public class PlannerDefinitions {
	private static String PATH_ANIMATIONS = "animations/animationsheets";
	private static String PATH_SPRITE_IMAGE = "sprite";

	public static void setupDefintions(ArrayList<ArrayList<String>> listOfLists,
			Hashtable<String, PlannerContainerDef> containersByName)
	{
		setupTriggerDefinition(listOfLists, containersByName);
		setupTextDefinitions(listOfLists, containersByName);
		setupHeroDefinitions(listOfLists, containersByName);
		setupEnemyDefinitions(listOfLists, containersByName);
		setupItemDefinitions(listOfLists, containersByName);
		setupQuestDefinitions(listOfLists, containersByName);
		setupCinematicDefinitions(listOfLists, containersByName);
		setupMapDefinitions(listOfLists, containersByName);
		setupMapEditorDefinitions(listOfLists, containersByName);
	}

	public static void setupRefererList(ArrayList<ArrayList<String>> listOfLists)
	{
		for (int i = 0; i < 24; i++)
			listOfLists.add(new ArrayList<String>());

		// Setup AI Types
		listOfLists.get(PlannerValueDef.REFERS_AI_APPROACH - 1).add("wait");
		listOfLists.get(PlannerValueDef.REFERS_AI_APPROACH - 1).add("fast");
		listOfLists.get(PlannerValueDef.REFERS_AI_APPROACH - 1).add("slow");
		listOfLists.get(PlannerValueDef.REFERS_AI_APPROACH - 1).add("Follow");
		listOfLists.get(PlannerValueDef.REFERS_AI_APPROACH - 1).add("Move to point");

		listOfLists.get(PlannerValueDef.REFERS_AI - 1).add("wizard");
		listOfLists.get(PlannerValueDef.REFERS_AI - 1).add("cleric");
		listOfLists.get(PlannerValueDef.REFERS_AI - 1).add("fighter");

		// Setup stat gain types
		GlobalPythonFactory.intialize();

		// Setup progression type
		for (String progressionName : GlobalPythonFactory.createLevelProgression().getStandardStatProgressionTypeList())
			listOfLists.get(PlannerValueDef.REFERS_STAT_GAINS - 1).add(progressionName);

		// Setup usuable itemstyles
		for (String weaponName : GlobalPythonFactory.createConfigurationValues().getWeaponTypes())
			listOfLists.get(PlannerValueDef.REFERS_ITEM_STYLE - 1).add(weaponName);

		// Setup usuable item types
		listOfLists.get(PlannerValueDef.REFERS_ITEM_TYPE - 1).add("Weapon");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_TYPE - 1).add("Ring");

		// Setup usuable item ranges
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add("Self only");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add(
				"All within 1");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add(
				"All within 2");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add(
				"All within 3");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add(
				"Only at range 2");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add(
				"Only at range 2 and 3");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add(
				"Only at range 3");

		// Setup usuable item areas
		listOfLists.get(PlannerValueDef.REFERS_ITEM_AREA - 1).add("None");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_AREA - 1).add("One square");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_AREA - 1).add("Five squares");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_AREA - 1).add("Thirteen squares");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_AREA - 1).add("Everyone");

		// Setup movement types
		for (String movementName : GlobalPythonFactory.createConfigurationValues().getMovementTypes())
			listOfLists.get(PlannerValueDef.REFERS_MOVE_TYPE - 1).add(movementName);

		// Setup spells
		for (String spellName : GlobalPythonFactory.createJSpell().getSpellList())
			listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add(spellName);

		// Setup Direction
		listOfLists.get(PlannerValueDef.REFERS_DIRECTION - 1).add("Up");
		listOfLists.get(PlannerValueDef.REFERS_DIRECTION - 1).add("Down");
		listOfLists.get(PlannerValueDef.REFERS_DIRECTION - 1).add("Left");
		listOfLists.get(PlannerValueDef.REFERS_DIRECTION - 1).add("Right");

		// Animation files
		File animations = new File(PATH_ANIMATIONS);
		for (String f : animations.list())
			if (f.endsWith(".anim"))
				listOfLists.get(PlannerValueDef.REFERS_ANIMATIONS - 1).add(
						f.replaceFirst(".anim", ""));

		// Sprite image files
		File spriteImages = new File(PATH_SPRITE_IMAGE);
		for (String f : spriteImages.list())
			if (f.endsWith(".png"))
				listOfLists.get(PlannerValueDef.REFERS_SPRITE_IMAGE - 1).add(
						f.replaceFirst(".png", ""));

		// Setup Battle Effects
		for (String effectName : GlobalPythonFactory.createJBattleEffect("NOTHING").getBattleEffectList())
			listOfLists.get(PlannerValueDef.REFERS_EFFECT - 1).add(effectName);

		// Setup Attribute Strength
		listOfLists.get(PlannerValueDef.REFERS_ATTRIBUTE_STRENGTH - 1).add(AttributeStrength.WEAK.name());
		listOfLists.get(PlannerValueDef.REFERS_ATTRIBUTE_STRENGTH - 1).add(AttributeStrength.MEDIUM.name());
		listOfLists.get(PlannerValueDef.REFERS_ATTRIBUTE_STRENGTH - 1).add(AttributeStrength.STRONG.name());

		// Setup Body/Mind progression
		for (String progressionName : GlobalPythonFactory.createLevelProgression().getBodyMindProgressionTypeList())
			listOfLists.get(PlannerValueDef.REFERS_BODYMIND_GAIN - 1).add(progressionName);

		// Setup Terrain
		JConfigurationValues jConfigValues = GlobalPythonFactory.createConfigurationValues();
		for (String terrainType : jConfigValues.getTerrainTypes())
			listOfLists.get(PlannerValueDef.REFERS_TERRAIN - 1).add(terrainType);
	}

	public static void setupCinematicDefinitions(ArrayList<ArrayList<String>> listOfLists,
			Hashtable<String, PlannerContainerDef> containersByName) {
		PlannerContainerDef cinematicContainer;

		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "description", false,
						"Description",
						"A description of the object that will be presented to the players"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "camerax", false,
				"The initial X location of the camera",
				"The x position (in pixels) of the items image"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "cameray", false,
				"The initial Y location of the camera",
				"The y position (in pixels) of the items image"));
		PlannerLineDef definingLine = new PlannerLineDef("cinematic",
				"Cinematic", "", definingValues);

		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();

		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();

		// Wait
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT,
						"time",
						false,
						"Wait Time",
						"The amount of time in ms that the cinematic should wait before more actions are processed"));

		allowableLines
				.add(new PlannerLineDef(
						"wait",
						"Wait",
						"Halts new actions from being processed for the specified time. This is a halting action",
						definingValues));

		// Add actor
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "x", false, "Start Location X",
				"The x coordinate (in pixels) to start the actor in"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "y", false, "Start Location Y",
				"The y coordinate (in pixels) to start the actor in"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING,
						"name",
						false,
						"Actor Name",
						"The name of the actor to be created. This will be used to reference the actor in the cinematic"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ANIMATIONS, PlannerValueDef.TYPE_STRING,
				"anim", false, "Animation file",
				"The name of the animation file to be used for this actor"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "startanim", false,
				"Starting Animation",
				"The name of the animation that this actor should start in"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "visible", false,
				"Starts Visible", "Whether this actor should start visible"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_BOOLEAN,
						"init",
						false,
						"Initialize before Cinematic",
						"Indicates that this action should be taken before the scene is rendered. Blocking actions should NEVER be initialized before the scene"));

		definingValues
		.add(new PlannerValueDef(
				PlannerValueDef.REFERS_HERO,
				PlannerValueDef.TYPE_INT,
				"associatedhero",
				false,
				"Associated Hero",
				"The hero that should be used to determine if the unpromoted or promoted version of animations should be used."));

		allowableLines
				.add(new PlannerLineDef(
						"addactor",
						"Add Actor",
						"Adds an actor to the cinematic, this actor can be accessed in the future by its' name",
						definingValues));

		// Associate Actor With Sprite
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING,
				"name", false, "Actor Name",
				"The name that will be used to reference the actor in the cinematic"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "npcid", true, "NPC ID",
				"The ID of the NPC that should become a cinematic actor"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "hero", true, "Associate Hero Leader",
				"If true then the players hero will be established as a cinematic actor. This should not be used in battles"));
		allowableLines.add(new PlannerLineDef("assactor", "Establish Sprite as Actor",
						"Establishes a Sprite (NPC, Enemy, Hero) as an actor.", definingValues));

		// Remove Actor
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "name", false,
						"Actor Name",
						"The name of the actor that should be removed from the cinematic"));

		allowableLines
				.add(new PlannerLineDef(
						"removeactor",
						"Remove Actor",
						"Removes the specified actor from the cinematic. This actor will no longer be able to be the target of actions.",
						definingValues));

		// Add Static Sprite
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "x", false, "Start Location X",
				"The x coordinate (in pixels) to place the sprite at"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "y", false, "Start Location Y",
				"The y coordinate (in pixels) to place the sprite at"));


		definingValues
		.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING,
				"spriteid",
				false,
				"Sprite Identifier",
				""));

		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_SPRITE_IMAGE,
						PlannerValueDef.TYPE_STRING,
						"spriteim",
						false,
						"Sprite image",
						""));

		allowableLines
				.add(new PlannerLineDef(
						"addstatic",
						"Add Static Sprite",
						"Adds a static sprite at the given location",
						definingValues));

		// Remove Static Sprite
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING,
						"spriteid",
						false,
						"Sprite Identifier",
						""));

		allowableLines
				.add(new PlannerLineDef(
						"remstatic",
						"Remove Static Sprite",
						"Removes the static sprite with the given identifier",
						definingValues));

		// Halting Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should perform the action"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "x", false, "X Coordinate",
				"The x coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "y", false, "Y Coordinate",
				"The y coordinate (in pixels) that the actor should move to"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING,
						"speed",
						false,
						"Move Speed",
						"The amount of pixels that the actor will move every 20ms towards their destination. Normal movement is 2.4"));

		definingValues
		.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN,
				"movehor",
				false,
				"Move Horizontal Before Vertical",
				"The sprite will move horizontal before it takes vertical moves if this is checked. Otherwise it will move vertical first"));

		// Diagonal movement
		definingValues
		.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN,
				"movediag",
				false,
				"Allow Diagonal Movement",
				"If checked then this sprite can move diagonally"));

		allowableLines
				.add(new PlannerLineDef(
						"haltingmove",
						"Halting Move",
						"Orders the specified actor to move to the specified coordinate. This action is 'halting' which means no further actions will be issued until this action is complete",
						definingValues));

		// Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should perform the action"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "x", false, "X Coordinate",
				"The x coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "y", false, "Y Coordinate",
				"The y coordinate (in pixels) that the actor should move to"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING,
						"speed",
						false,
						"Move Speed",
						"The amount of pixels that the actor will move every 20ms towards their destination. Normal movement is 2.4"));

		definingValues
		.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN,
				"movehor",
				false,
				"Move Horizontal Before Vertical",
				"The sprite will move horizontal before it takes vertical moves if this is checked. Otherwise it will move vertical first"));

		// Diagonal movement
		definingValues
		.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN,
				"movediag",
				false,
				"Allow Diagonal Movement",
				"If checked then this sprite can move diagonally"));

		allowableLines
				.add(new PlannerLineDef(
						"move",
						"Move",
						"Orders the specified actor to move to the specified coordinate.",
						definingValues));

		// Forced Facing Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should perform the action"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "x", false, "X Coordinate",
				"The x coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "y", false, "Y Coordinate",
				"The y coordinate (in pixels) that the actor should move to"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT,
						"speed",
						false,
						"Move Speed",
						"The amount of pixels that the actor will move every 30ms towards their destination"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_DIRECTION,
						PlannerValueDef.TYPE_INT, "facing", false, "Facing",
						"The direction that the sprite should face for the duration of the move"));

		definingValues
		.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN,
				"movehor",
				false,
				"Move Horizontal Before Vertical",
				"The sprite will move horizontal before it takes vertical moves if this is checked. Otherwise it will move vertical first"));

		// Diagonal movement
		definingValues
		.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN,
				"movediag",
				false,
				"Allow Diagonal Movement",
				"If checked then this sprite can move diagonally"));

		allowableLines
				.add(new PlannerLineDef(
						"forcedmove",
						"Move Forced Facing",
						"Orders the specified actor to move to the specified coordinate. This actor will keep facing the same direction for the duration of the move",
						definingValues));

		// Loop Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should perform the looping move"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "x", false, "X Coordinate",
				"The x coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "y", false, "Y Coordinate",
				"The y coordinate (in pixels) that the actor should move to"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING,
						"speed",
						false,
						"Move Speed",
						"The amount of pixels that the actor will move every 20ms towards their destination. Normal movement is 2.4"));

		allowableLines
				.add(new PlannerLineDef(
						"loopmove",
						"Move Actor in Loop",
						"Causes the specified actor to move to the specified location, once the actor gets to that location they will teleport back to where they started when this action was first called. This action will continue until a STOP LOOP MOVE action is called on this actor or another move command is issued for this actor",
						definingValues));

		// Stop Loop Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should stop their looping move"));

		allowableLines
				.add(new PlannerLineDef(
						"stoploopmove",
						"Stop Actor Looping Move",
						"Causes the specified actor to stop looping their move, they will still walk to their target location but will not teleport",
						definingValues));

		// Halting Anim
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should perform the action"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT,
						"time",
						false,
						"Time",
						"The amount of time in milliseconds that this animation should be performed over. All frames will be shown for an equal time. General stand speed is 1000ms"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "anim", false,
				"Animation to Show",
				"The name of the animation that the actor should take"));

		allowableLines
				.add(new PlannerLineDef(
						"haltinganim",
						"Halting Animation",
						"Causes the specified actor to perform the specified animation, This action is 'halting' which means no further actions will be issued until this action is complete.",
						definingValues));

		// Anim
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should perform the action"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT,
						"time",
						false,
						"Time",
						"The amount of time in milliseconds that this animation should be performed over. All frames will be shown for an equal time. General stand speed is 1000ms"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "anim", false,
				"Animation to Show",
				"The name of the animation that the actor should take"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_BOOLEAN, "loops", false,
						"Loop Animation",
						"Whether this animation should loop after it has finished playing."));

		allowableLines
				.add(new PlannerLineDef(
						"anim",
						"Animation",
						"Causes the specified actor to perform the specified animation.",
						definingValues));

		// Stop Anim
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should perform the action"));

		allowableLines.add(new PlannerLineDef("stopanim", "Stop Animation",
				"Causes the specified actor to stop its' current animation.",
				definingValues));

		// Spin
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should spin"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT,
						"speed",
						false,
						"Spin Speed",
						"The amount of time in ms that should pass in between changing facing direction"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT,
						"time",
						false,
						"Spin Duration",
						"The amount of time that this actor should spin for. A value of -1 indicates that this actor should spin for an indefinite amount of time. If an indefinite amount of time is specified then the actor will stop spinning when the a STOP SPIN action is issued"));

		allowableLines
				.add(new PlannerLineDef(
						"spin",
						"Spin Actor",
						"Causes the specified actor to begin spinning, this will cause other animations to stop for the duration of the spin. This can be used in conjunction with the grow and shrink special effect",
						definingValues));

		// Stop Spin
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should stop spinning"));

		allowableLines
				.add(new PlannerLineDef(
						"stopspin",
						"Stop Actor Spinning",
						"Stops the specified actor from spinning. If the actor is not spinning then no action will be taken",
						definingValues));

		// Facing
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should change their facing"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_DIRECTION, PlannerValueDef.TYPE_INT,
				"dir", false, "Facing Direction",
				"The direction that the actor should face"));

		allowableLines.add(new PlannerLineDef("facing", "Set Actor Facing",
				"Causes the specified actor to face the specified direction",
				definingValues));

		// Shrink
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "name", false,
						"Actor Name",
						"The name of the actor that should perform the shrink special effect"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "time", false, "Shrink Time",
				"The amount of time that this actor should shrink over"));

		allowableLines
				.add(new PlannerLineDef(
						"shrink",
						"Shrink Actor",
						"Causes the specified actor to shrink over time, once the time is up the actor will immediately return to normal size, so if the intention is to remove the actor then it should be removed immediately before the end of this action. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.",
						definingValues));

		// Grow
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "name", false,
						"Actor Name",
						"The name of the actor that should perform the grow special effect"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "time", false, "Grow Time",
				"The amount of time that this actor should grow over"));

		allowableLines
				.add(new PlannerLineDef(
						"grow",
						"Grow Actor",
						"Causes the specified actor to grow over time from 1% height to 100% height, once the time is up the actor will immediately return to normal size. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations",
						definingValues));

		// Quiver
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "name", false,
						"Actor Name",
						"The name of the actor that should perform the tremble special effect"));

		allowableLines
				.add(new PlannerLineDef(
						"tremble",
						"Start Actor Trembling",
						"Causes the specified actor to begin trembling. This effect will continue until a STOP SPECIAL EFFECT is issued for the actor. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.",
						definingValues));

		// Agitate
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "name", false,
						"Actor Name",
						"The name of the actor that should perform the agitate special effect"));

		allowableLines
				.add(new PlannerLineDef(
						"quiver",
						"Start Actor Agitate",
						"Causes the specified actor to be 'Agitated'. This effect will continue until a STOP SPECIAL EFFECT is issued for the actor. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.",
						definingValues));

		// Fall on Face
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "name", false,
						"Actor Name",
						"The name of the actor that should perform the fall-on-face special effect"));
		definingValues
		.add(new PlannerValueDef(PlannerValueDef.REFERS_DIRECTION,
				PlannerValueDef.TYPE_INT, "dir", false,
				"Head Direction",
				"The direction the sprites head should be facing"));

		allowableLines
				.add(new PlannerLineDef(
						"fallonface",
						"Actor Fall on Face",
						"Causes the specified actor to fall on their face. This effect will continue until a STOP SPECIAL EFFECT is issued for the actor. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.",
						definingValues));

		// Lay on Side
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "name", false,
						"Actor Name",
						"The name of the actor that should perform the lay-on-side special effect"));
		definingValues
		.add(new PlannerValueDef(PlannerValueDef.REFERS_DIRECTION,
				PlannerValueDef.TYPE_INT, "dir", false,
				"Head Direction",
				"The direction the sprites head should be facing"));

		allowableLines
				.add(new PlannerLineDef(
						"layonsideright",
						"Actor Lay on Side Right",
						"Causes the specified actor to lay on their side. This effect will continue until a STOP SPECIAL EFFECT is issued for the actor. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.",
						definingValues));

		// Lay on side left
		allowableLines
		.add(new PlannerLineDef(
				"layonsideleft",
				"Actor Lay on Side Left",
				"Causes the specified actor to lay on their side. This effect will continue until a STOP SPECIAL EFFECT is issued for the actor. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.",
				definingValues));

		// Lay on Back
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "name", false,
						"Actor Name",
						"The name of the actor that should perform the lay-on-back special effect"));
		definingValues
		.add(new PlannerValueDef(PlannerValueDef.REFERS_DIRECTION,
				PlannerValueDef.TYPE_INT, "dir", false,
				"Head Direction",
				"The direction the sprites head should be facing"));

		allowableLines
				.add(new PlannerLineDef(
						"layonback",
						"Actor Lay on Back",
						"Causes the specified actor to lay on their back. This effect will continue until a STOP SPECIAL EFFECT is issued for the actor. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.",
						definingValues));

		// Flash
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "name", false,
						"Actor Name",
						"The name of the actor that should perform the flash special effect"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "time", false, "Flash Duration",
				"The amount of time in ms that this actor should flash for"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "speed", false, "Flash Speed",
				"The amount of time that a single flash should take"));

		allowableLines
				.add(new PlannerLineDef(
						"flash",
						"Actor Flash",
						"Causes the specified actor to flash white. If the duration is marked as indefinite then this effect will continue until a STOP SPECIAL EFFECT is issued for the actor. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.",
						definingValues));

		// Nod
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should perform the nod effect"));

		allowableLines
				.add(new PlannerLineDef(
						"nod",
						"Actor Nod",
						"Causes the specified actor to nod. This effect lasts 1500ms. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.",
						definingValues));

		// Shake head
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "name", false,
						"Actor Name",
						"The name of the actor that should perform the shake head effect"));
		definingValues
		.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "time", false,
				"Shake Duration", "The amount of time in ms that this head shake should take to perform"));

		allowableLines
				.add(new PlannerLineDef(
						"shakehead",
						"Actor Shake Head",
						"Causes the specified actor to nod. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.",
						definingValues));

		// Stop Special Effect
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "name", false,
						"Actor Name",
						"The name of the actor that should stop performing special effects"));

		allowableLines
				.add(new PlannerLineDef(
						"stopse",
						"Stop Actor Special Effect",
						"Causes the specified actor to stop peforming any special effects that are currently active. This should be used to stop special effects of indefinite duration.",
						definingValues));

		// Visible
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor whose visibility should be changed"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_BOOLEAN,
						"isvis",
						false,
						"Is Visible",
						"If true, the actor should become visible, otherwise they should become invisible"));

		allowableLines.add(new PlannerLineDef("visible", "Set Actor Visiblity",
				"Sets the specified actors visiblity", definingValues));

		// Move char to forefront
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should be displayed on top of all terrain layers"));

		allowableLines.add(new PlannerLineDef("rendertop", "Render on Top",
				"Causes the selected actor to be rendered on top of all of the terrain layers. This will continue until a 'Render on Normal' command is called for the actor.", definingValues));

		// Remove char from forefront
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Actor Name",
				"The name of the actor that should stop their looping move"));

		allowableLines.add(new PlannerLineDef("rendernormal", "Render on Normal",
				"Causes the selected actor to be rendered in normal layer postion. This should be used to end the 'Render on Top' action.", definingValues));


		// Play Music
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "music", false, "Music Title",
				"The name of the music that should be played"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT,
						"volume",
						false,
						"Volume",
						"The percent volume that the music should be played at. This should be a value between 1-100"));

		allowableLines
				.add(new PlannerLineDef(
						"playmusic",
						"Play Music",
						"Loops music at the specified volume. Only one song can be playing at a time, starting music while one is already playing will end the other one.",
						definingValues));

		// Pause Music
		definingValues = new ArrayList<PlannerValueDef>();
		allowableLines.add(new PlannerLineDef("pausemusic", "Pause Music",
				"Pauses the currently playing music", definingValues));

		// Resume Music
		definingValues = new ArrayList<PlannerValueDef>();
		allowableLines.add(new PlannerLineDef("resumemusic", "Resume Music",
				"Resumes any paused music", definingValues));

		// Fade Music
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT, "duration", false,
						"Fade Duration",
						"The amount of time in ms that the music should fade out over"));
		allowableLines
				.add(new PlannerLineDef(
						"fademusic",
						"Fade Out Music",
						"Fades out any playing music to no volume over the specified period of time. The music stops playing after the fade",
						definingValues));

		// Play Sound
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "sound", false, "Sound Title",
				"The name of the sound that should be played"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT,
						"volume",
						false,
						"Volume",
						"The percent volume that the sound should be played at. This should be a value between 1-100"));

		allowableLines.add(new PlannerLineDef("playsound", "Play Sound",
				"Plays sound at the specified volume.", definingValues));

		// Fade From Black
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "time", false, "Fade Time",
				"The amount of time that the screen should be faded over"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "halting", false, "Wait to Finish",
				"If true, this will be a halting action"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "init", false, "Intialize before cinematic",
				"If true, this action will be intialized before the cinematic starts. If you intend to fade in to the scene then you should check this"));

		allowableLines.add(new PlannerLineDef("fadein", "Fade in from black",
				"Fades the screen in from black.", definingValues));

		// Fade To Black
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "time", false, "Fade Time",
				"The amount of time that the screen should be faded over"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "halting", false, "Wait to Finish",
				"If true, this will be a halting action"));

		allowableLines.add(new PlannerLineDef("fadeout", "Fade to black",
				"Fades the screen in to black.", definingValues));

		// Flash Screen
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "time", false, "Flash Time",
				"The amount of time that the screen should flash over"));

		allowableLines.add(new PlannerLineDef("flashscreen", "Flash Screen",
				"Flashes the screen white.", definingValues));

		// Camera follow
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING,
						"name",
						false,
						"Actor Name",
						"The name of the actor that the camera should follow. This actor should already have been added to the cinematic"));

		allowableLines
				.add(new PlannerLineDef(
						"camerafollow",
						"Set Camera follows actor",
						"Causes the camera to always be centered on the current actor. You can cancel this function by calling another camera location command",
						definingValues));

		// Camera Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "x", false, "X Coordinate",
				"The x coordinate (in pixels) that the camera should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "y", false, "Y Coordinate",
				"The y coordinate (in pixels) that the camera should move to"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT, "time", false, "Moving Time",
						"The amount of time in ms that the camera should be moved over"));

		allowableLines
				.add(new PlannerLineDef(
						"cameramove",
						"Camera Pan",
						"Pans the camera to the specified location over time. If the time is 0 then the camera immediate will move to the location",
						definingValues));

		// Camera Shake
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "time", false, "Shake Time",
				"The amount of time that the camera should shake for"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT, "severity", false,
						"Severity",
						"The amount of pixels that the camera can be offset to during the shake"));

		allowableLines
				.add(new PlannerLineDef(
						"camerashake",
						"Shake Camera",
						"Shakes the camera to simulate an earthquake effect. After the camera is done shaking it will return to it's original location",
						definingValues));

		// Text Box
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_LONG_STRING, "text", false, "Text",
				"The text that should be displayed. Using the { character will cause a short pause, the } character will do a soft stop and the } chararacter will do a hard stop. Using a | will cause the action after this one to be ran"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_HERO, PlannerValueDef.TYPE_INT,
				"heroportrait", false, "Hero Portrait",
				"The hero whose portrait should be shown for this text. This will only be respected when 'Portrait Index' is -1"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ENEMY, PlannerValueDef.TYPE_INT,
				"enemyportrait", false, "Enemy Portrait",
				"The enemy whose portrait should be shown for this text. This will only be respected when 'Portrait Index' is -1"));

		allowableLines
				.add(new PlannerLineDef(
						"speech",
						"Show Speech Box",
						"Displays the specified text in a text box. This action is 'halting', which means subsequent actions will not be performed until the text box is dismissed via user input.",
						definingValues));

		// Load map
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING,
						"map",
						false,
						"Map",
						"The name of the map file to be loaded, the associated text file should have the same name"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING,
						"enter",
						false,
						"Map Entrance",
						"The entrance area definied in the map file that the hero should start in once the map loads. The area should be marked with a 'start' name and an 'exit' value"));

		allowableLines
				.add(new PlannerLineDef(
						"loadmap",
						"Load Map",
						"Loads the specified map and places the hero at the specified entrance.",
						definingValues));

		// Load Battle
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
		.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "textfile", false,
				"Battle Trigger File",
				"The name of the battle trigger file that should be loaded for this battle"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "map", false,
						"Battle Map",
						"The name of the battle map file that should be loaded for this battle"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "entrance", false,
						"Entrance location",
						"The name of the map location that the force will be placed at when the battle loads"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "battbg", false,
				"Battle Background Index",
				"The index of the battle background that should be used for the battle"));
		allowableLines.add(new PlannerLineDef("loadbattle", "Start Battle",
				"Starts the battle with the given triggers and map",
				definingValues));

		// Load Cinematic
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "map", false, "Map Name",
				"The name of the map that should be loaded"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"cinid", false, "Cinematic ID",
				"The ID of the cinematic that should be shown"));
		allowableLines.add(new PlannerLineDef("loadcin", "Load Cinematic",
				"Loads the specified map and text file with the same name and then runs the specified cinematic.", definingValues));

		// Exit Game
		definingValues = new ArrayList<PlannerValueDef>();
		allowableLines.add(new PlannerLineDef("exit", "Exit Game",
				"Causes the game to exit",
				definingValues));

		// Add hero
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_HERO,
				PlannerValueDef.TYPE_INT, "heroid", false, "Hero ID",
				"The ID of the hero that should be added to the force"));
		allowableLines.add(new PlannerLineDef("addhero", "Add Hero",
				"Adds a new hero to the force", definingValues));
		definingValues
		.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN,
				"init",
				false,
				"Initialize before Cinematic",
				"Indicates that this action should be taken before the scene is rendered. Blocking actions should NEVER be initialized before the scene"));



		cinematicContainer = new PlannerContainerDef(definingLine,
				allowableContainers, allowableLines, listOfLists,
				PlannerValueDef.REFERS_CINEMATIC - 1);
		containersByName.put("cinematic", cinematicContainer);
	}

	public static void setupItemDefinitions(ArrayList<ArrayList<String>> listOfLists,
			Hashtable<String, PlannerContainerDef> containersByName) {
		PlannerContainerDef itemContainer;

		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Name",
				"The name of the item"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "description", false,
						"Description",
						"A description of the object that will be presented to the players"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "cost", false, "Cost",
				"The amount this item costs to purchase"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "imageindexx", false, "X Index",
				"The x index of the items image"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "imageindexy", false, "Y Index",
				"The y index of the items image"));
		PlannerLineDef definingLine = new PlannerLineDef("item", "Item", "",
				definingValues);

		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();

		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();

		// Equippable
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "attack", false, "Attack Modifier",
				"The amount equipping this item will modify attack"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "defense", false, "Defense Modifier",
				"The amount equipping this item will modify defense"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "speed", false, "Speed Modifier",
				"The amount equipping this item will modify speed"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ITEM_TYPE, PlannerValueDef.TYPE_INT,
				"type", false, "Item Type",
				"Whether this item is a weapon or ring"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ITEM_STYLE, PlannerValueDef.TYPE_INT,
				"style", false, "Item Style",
				"What type of weapon this, use any value for rings"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM_RANGE,
						PlannerValueDef.TYPE_INT, "range", false, "Item Range",
						"The range this weapon can attack from, use any value for rings"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "weaponimage", false, "Weapon Attack Image",
					"The name of the weapon image that should be used for this weapon (should exist in the images/weapons folder). Use any value for rings"));
		allowableLines.add(new PlannerLineDef("equippable", "Equippable Item",
				"Marks this item as equippable and defines stats for it",
				definingValues));

		// Use Custom
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_BOOLEAN, "targetsenemy", false,
						"Targets Enemy",
						"Whether this item can be used on enemies, otherwise it is used on allies"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT, "damage", false,
						"Damage Dealt",
						"The amount of damage this item will deal on use (positive values will heal)"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT,
						"mpdamage",
						false,
						"MP Damage Dealt",
						"The amount of damage this item will deal to the targets MP on use (positive values will heal)"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM_RANGE,
						PlannerValueDef.TYPE_INT, "range", false, "Item Range",
						"The range this can be used from"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ITEM_AREA, PlannerValueDef.TYPE_INT,
				"area", false, "Item Area of Effect",
				"The area that this item can effect"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING,
						"text",
						false,
						"Item Use Text",
						"The text that will be appended after the targets name in the attack cinematic. An example value would be 'is healed for 10'. "
								+ "This would cause the battle text to become 'Noah is healed for 10'"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "singleuse", false,
				"Single Use Item",
				"If true, the item will be removed after it has been used. "));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "damageitem", false,
				"Damages Item",
				"If true, the item has a chance of being damaged on use"));

		allowableLines.add(new PlannerLineDef("use", "Custom Usuable Item",
				"Marks this item as usuable and defines its' use",
				definingValues));

		// Use Spell
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_SPELL,
						PlannerValueDef.TYPE_STRING, "spellid", false,
						"Spell Cast",
						"The spell that will be cast when this item is used"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT, "level", false,
						"Spell Level",
						"The level of the spell that will be cast upon use"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "singleuse", false,
				"Single Use Item",
				"If true, the item will be removed after it has been used. "));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "damageitem", false,
				"Damages Item",
				"If true, the item has a chance of being damaged on use"));

		allowableLines.add(new PlannerLineDef("usespell", "Usuable Item Spell",
				"Marks this item as usuable and defines the spell it casts on use",
				definingValues));

		itemContainer = new PlannerContainerDef(definingLine,
				allowableContainers, allowableLines, listOfLists,
				PlannerValueDef.REFERS_ITEM - 1);
		containersByName.put("item", itemContainer);
	}

	public static void setupQuestDefinitions(ArrayList<ArrayList<String>> listOfLists,
			Hashtable<String, PlannerContainerDef> containersByName) {
		PlannerContainerDef textContainer;

		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "description", false,
				"Description", "Description"));
		// definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
		// PlannerValueDef.TYPE_INT, "triggerid", false,
		// "Unique Trigger Id",
		// "Unique id that can be used to identify a given trigger"));
		PlannerLineDef definingLine = new PlannerLineDef("quest", "Quest", "",
				definingValues);

		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();

		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();

		textContainer = new PlannerContainerDef(definingLine,
				allowableContainers, allowableLines, listOfLists,
				PlannerValueDef.REFERS_QUEST - 1);
		containersByName.put("quest", textContainer);
	}

	public static void setupTextDefinitions(ArrayList<ArrayList<String>> listOfLists,
			Hashtable<String, PlannerContainerDef> containersByName) {
		PlannerContainerDef textContainer;

		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "description", false,
				"Description", "Description"));
		// definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
		// PlannerValueDef.TYPE_INT, "triggerid", false,
		// "Unique Trigger Id",
		// "Unique id that can be used to identify a given trigger"));
		PlannerLineDef definingLine = new PlannerLineDef("text", "Text", "",
				definingValues);

		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();

		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();

		// Text
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_LONG_STRING,
						"message",
						false,
						"Message Text",
						"The text that should be displayed. Using the { character will cause a short pause, the } character will do a soft stop and the } chararacter will do a hard stop."));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_QUEST,
						PlannerValueDef.TYPE_MULTI_INT, "require", false,
						"Required Quest",
						"The ID of the quest that must be complete for this to be shown"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_QUEST,
						PlannerValueDef.TYPE_MULTI_INT, "exclude", false,
						"Exclude Quest",
						"The ID of the quest that CAN NOT be complete for this to be shown"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_TRIGGER,
						PlannerValueDef.TYPE_INT, "trigger", false,
						"Trigger ID",
						"The ID of the trigger that should be run after this message is complete."));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_HERO, PlannerValueDef.TYPE_INT,
				"heroportrait", false, "Hero Portrait",
				"The hero whose portrait should be shown for this text. This will only be respected when 'Portrait Index' is -1"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ENEMY, PlannerValueDef.TYPE_INT,
				"enemyportrait", false, "Enemy Portrait",
				"The enemy whose portrait should be shown for this text. This will only be respected when 'Portrait Index' is -1"));
		allowableLines.add(new PlannerLineDef("string", "Message Text",
				"A message that should be displayed", definingValues));

		textContainer = new PlannerContainerDef(definingLine,
				allowableContainers, allowableLines, listOfLists,
				PlannerValueDef.REFERS_TEXT - 1);
		containersByName.put("text", textContainer);
	}

	public static void setupEnemyDefinitions(ArrayList<ArrayList<String>> listOfLists,
			Hashtable<String, PlannerContainerDef> containersByName) {
		PlannerContainerDef enemyContainer;

		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Name",
				"The name of the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "hp", false, "HP",
				"Starting HP for the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "mp", false, "MP",
				"Starting MP for the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "attack", false, "Attack",
				"Starting Attack for the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "defense", false, "Defense",
				"Starting Defense for the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "speed", false, "Speed",
				"Starting Speed for the enemy"));
		// Fire Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"fireAffin", false, "Fire Affinitiy",
				"The heroes base fire affinity. Items can modify this value."));

		// Elec Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"elecAffin", false, "Electricity Affinitiy",
				"The heroes base electricity affinity. Items can modify this value."));

		// Cold Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"coldAffin", false, "Cold Affinitiy",
				"The heroes base cold affinity. Items can modify this value."));

		// Dark Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"darkAffin", false, "Dark Affinitiy",
				"The heroes base dark affinity. Items can modify this value."));

		// Water Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"waterAffin", false, "Water Affinitiy",
				"The heroes base water affinity. Items can modify this value."));

		// Earth Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"earthAffin", false, "Earth Affinitiy",
				"The heroes base earth affinity. Items can modify this value."));

		// Wind Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"windAffin", false, "Wind Affinitiy",
				"The heroes base wind affinity. Items can modify this value."));

		// Light Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"lightAffin", false, "Light Affinitiy",
				"The heroes base light affinity. Items can modify this value."));

		// Body Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"bodyStrength", false, "Body Strength",
				"Determines the base body value for the hero."));

		// Mind Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"mindStrength", false, "Mind Strength",
				"Determines the base mind value for the hero."));

		// Counter Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"counterStrength", false, "Counter Chance Strength",
				"Determines the base counter value for the hero."));

		// Evade Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"evadeStrength", false, "Evade Chance Strength",
				"Determines the base evade value for the hero."));

		// Double Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"doubleStrength", false, "Double Chance Strength",
				"Determines the base body value for the hero."));

		// Crit Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"critStrength", false, "Critical Strength",
				"Determines the base critical value for the hero."));

		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "level", false, "Level",
				"Starting Level for the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "move", false, "Move",
				"Starting Move for the enemy"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_MOVE_TYPE,
						PlannerValueDef.TYPE_STRING, "movementtype", false,
						"Movement Type",
						"The enemies movement type as it relates to land effect and barriers"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_ANIMATIONS,
						PlannerValueDef.TYPE_STRING, "animations", false,
						"Animation File",
						"The name of the animation file that should be used for this enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "leader", false, "Is Leader",
				"Whether this enemy is the leader of the force"));

		// definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
		// PlannerValueDef.TYPE_INT, "triggerid", false,
		// "Unique Trigger Id",
		// "Unique id that can be used to identify a given trigger"));
		PlannerLineDef definingLine = new PlannerLineDef("enemy", "Enemy", "",
				definingValues);

		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();

		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();

		// Spell Progression
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_SPELL,
				PlannerValueDef.TYPE_STRING, "spellid", false, "Spell ID",
				"The ID of the spell that this enemy knows"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "level", false, "Max Level",
				"The max level known of the specified spell"));
		allowableLines.add(new PlannerLineDef("spell", "Spell",
				"A spell that this enemy knows", definingValues));

		// Items Equipped
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM,
				PlannerValueDef.TYPE_INT, "itemid", false, "Item ID",
				"The ID of the item that this enemy should start with"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "equipped", false,
				"Item Equipped", "If true, the item will start as equipped."));
		allowableLines.add(new PlannerLineDef("item", "Starting Item",
				"An item that this hero should start with", definingValues));

		// Attack Special Effect
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_EFFECT,
				PlannerValueDef.TYPE_STRING, "effectid", false, "Effect ID",
				"The ID of the effect that the enemies attack may cause"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "effectchance", false,
				"Effect Chance", "The percent chance that the effect will occur"));
		allowableLines.add(new PlannerLineDef("attackeffect", "Attack Effect",
				"An effect that may occur on the enemy attack", definingValues));

		enemyContainer = new PlannerContainerDef(definingLine,
				allowableContainers, allowableLines, listOfLists,
				PlannerValueDef.REFERS_ENEMY - 1);
		containersByName.put("enemy", enemyContainer);
	}

	public static void setupHeroDefinitions(ArrayList<ArrayList<String>> listOfLists,
			Hashtable<String, PlannerContainerDef> containersByName) {
		PlannerContainerDef heroContainer;

		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Name",
				"The name of the hero"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "promoted", false, "Promoted",
				"If true, this hero is promoted"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "level", false, "Level",
				"Starting Level for the hero"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_ANIMATIONS,
						PlannerValueDef.TYPE_STRING, "animations", false,
						"Animation File",
						"The name of the animation file that should be used for this hero"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "leader", true, "Is Leader",
				"Whether this hero is the leader of the force"));

		// definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
		// PlannerValueDef.TYPE_INT, "portrait", false,
		// "Portrait Index",
		// "Unique id that can be used to identify a given trigger"));
		PlannerLineDef definingLine = new PlannerLineDef("hero", "Hero", "",
				definingValues);

		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();

		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();

		// Progression
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_BOOLEAN,
						"promoted",
						false,
						"Promoted Progression",
						"Whether this progression represents this heroes promoted or unpromoted progression"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "move", false, "Starting Move",
				"The heroes base move while in this progression"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_MOVE_TYPE, PlannerValueDef.TYPE_STRING,
				"movementtype", false, "Movement Type",
				"The movement type of this hero"));

		// ATTACK
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_STAT_GAINS, PlannerValueDef.TYPE_STRING,
				"attack", false, "Attack Gain",
				"The amount of attack the hero should gain per level"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"attackstart", false, "Attack Start",
				"The amount of attack the hero should start with"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"attackend", false, "Attack End",
				"The minimum amount of attack the hero should end with"));

		// DEFENSE
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_STAT_GAINS, PlannerValueDef.TYPE_STRING,
				"defense", false, "Defense Gain",
				"The amount of defense the hero should gain per level"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"defensestart", false, "Defense Start",
				"The amount of defense the hero should start with"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"defenseend", false, "Defense End",
				"The minimum amount of defense the hero should end with"));

		// SPEED
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_STAT_GAINS, PlannerValueDef.TYPE_STRING,
				"speed", false, "Speed Gain",
				"The amount of speed the hero should gain per level"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"speedstart", false, "Speed Start",
				"The amount of speed the hero should start with"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"speedend", false, "Speed End",
				"The minimum amount of speed the hero should end with"));

		// HP
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_STAT_GAINS, PlannerValueDef.TYPE_STRING,
				"hp", false, "HP Gain",
				"The amount of HP the hero should gain per level"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"hpstart", false, "HP Start",
				"The amount of HP the hero should start with"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"hpend", false, "HP End",
				"The minimum amount of HP the hero should end with"));

		// MP
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_STAT_GAINS, PlannerValueDef.TYPE_STRING,
				"mp", false, "MP Gain",
				"The amount of MP the hero should gain per level"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"mpstart", false, "MP Start",
				"The amount of MP the hero should start with"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"mpend", false, "MP End",
				"The minimum amount of MP the hero should end with"));

		// Fire Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"fireAffin", false, "Fire Affinitiy",
				"The heroes base fire affinity. Items can modify this value."));

		// Elec Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"elecAffin", false, "Electricity Affinitiy",
				"The heroes base electricity affinity. Items can modify this value."));

		// Cold Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"coldAffin", false, "Cold Affinitiy",
				"The heroes base cold affinity. Items can modify this value."));

		// Dark Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"darkAffin", false, "Dark Affinitiy",
				"The heroes base dark affinity. Items can modify this value."));

		// Water Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"waterAffin", false, "Water Affinitiy",
				"The heroes base water affinity. Items can modify this value."));

		// Earth Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"earthAffin", false, "Earth Affinitiy",
				"The heroes base earth affinity. Items can modify this value."));

		// Wind Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"windAffin", false, "Wind Affinitiy",
				"The heroes base wind affinity. Items can modify this value."));

		// Light Affin
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"lightAffin", false, "Light Affinitiy",
				"The heroes base light affinity. Items can modify this value."));

		// Body Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ATTRIBUTE_STRENGTH, PlannerValueDef.TYPE_STRING,
				"bodyStrength", false, "Body Strength",
				"Determines the base body value for the hero."));

		// Body Progression
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_BODYMIND_GAIN, PlannerValueDef.TYPE_STRING,
				"bodyProgress", false, "Body Progression Type",
				"The name of the progression type that dicates how this heroes body stat will increase."));

		// Mind Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ATTRIBUTE_STRENGTH, PlannerValueDef.TYPE_STRING,
				"mindStrength", false, "Mind Strength",
				"Determines the base mind value for the hero."));

		// Mind Progression
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_BODYMIND_GAIN, PlannerValueDef.TYPE_STRING,
				"mindProgress", false, "Mind Progression Type",
				"The name of the progression type that dicates how this heroes mind stat will increase."));

		// Counter Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ATTRIBUTE_STRENGTH, PlannerValueDef.TYPE_STRING,
				"counterStrength", false, "Counter Chance Strength",
				"Determines the base counter chance value for the hero."));

		// Evade Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ATTRIBUTE_STRENGTH, PlannerValueDef.TYPE_STRING,
				"evadeStrength", false, "Evade Chance Strength",
				"Determines the base evade chance value for the hero."));

		// Double Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ATTRIBUTE_STRENGTH, PlannerValueDef.TYPE_STRING,
				"doubleStrength", false, "Double Chance Strength",
				"Determines the base double attack chance value for the hero."));

		// Crit Strength
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ATTRIBUTE_STRENGTH, PlannerValueDef.TYPE_STRING,
				"critStrength", false, "Critical Strength",
				"Determines the base critical attack chance value for the hero."));

		// Usuable Items
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ITEM_STYLE,
				PlannerValueDef.TYPE_MULTI_INT, "usuableitems", false,
				"Usuable Items", "The type of weapons that this hero can use"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "class", false, "Class Name",
				"The name of this characters class."));
		allowableLines.add(new PlannerLineDef("progression",
				"Hero Progression", "This heroes statistic progression",
				definingValues));

		// Spell Progression
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_SPELL,
				PlannerValueDef.TYPE_STRING, "spellid", false, "Spell ID",
				"The ID of the spell that this hero knows"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "gained", false,
						"Levels Gained",
						"A comma seperated list of the levels that the spell levels will be gained at."));
		allowableLines.add(new PlannerLineDef("spellprogression",
				"Spell Progression", "This heroes spell progression",
				definingValues));

		// Items Equipped
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM,
				PlannerValueDef.TYPE_INT, "itemid", false, "Item ID",
				"The ID of the item that this hero should start with"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "equipped", false,
				"Item Equipped", "If true, the item will start as equipped."));
		allowableLines.add(new PlannerLineDef("item", "Starting Item",
				"An item that this hero should start with", definingValues));

		heroContainer = new PlannerContainerDef(definingLine,
				allowableContainers, allowableLines, listOfLists,
				PlannerValueDef.REFERS_HERO - 1);
		containersByName.put("hero", heroContainer);
	}

	public static void setupTriggerDefinition(ArrayList<ArrayList<String>> listOfLists,
			Hashtable<String, PlannerContainerDef> containersByName) {
		PlannerContainerDef triggerContainer;

		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "description", false,
				"Description", "Description"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_QUEST,
						PlannerValueDef.TYPE_MULTI_INT, "require", false,
						"Required Quest",
						"The ID of the quest that must be complete for this to be shown"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_QUEST,
						PlannerValueDef.TYPE_MULTI_INT, "exclude", false,
						"Exclude Quest",
						"The ID of the quest that CAN NOT be complete for this to be shown"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_BOOLEAN, "nonretrig", false,
						"Non Retriggerable",
						"If true, indicates that this trigger can only be executed once per game"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_BOOLEAN,
						"retrigonenter",
						false,
						"Retrigger Each Enter",
						"If true, indicates that each time the map has been entered that this trigger should be reactivated"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "triggeronce", false,
				"Trigger Once Per Map",
				"If true, indicates that this trigger can only be executed once per map"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "triggerimmediately", false,
				"Trigger Immediately",
				"If true, indicates that this trigger should be executed as soon as a unit begins moving onto this space. "
				+ "If unchecked, the tirgger will only take effect once the unit has stopped moving. Checking this will have "
				+ "no effect during battle"));
		PlannerLineDef definingLine = new PlannerLineDef("trigger", "Trigger",
				"", definingValues);

		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();

		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();

		// Complete Quest
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_QUEST,
				PlannerValueDef.TYPE_INT, "questid", false, "Quest ID",
				"The ID of the equest that should be marked as complete"));
		allowableLines.add(new PlannerLineDef("completequest",
				"Complete Quest", "Marks a given quest as completed",
				definingValues));

		// Start Battle
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "battletriggers", false,
						"Battle Trigger File",
						"The name of the battle trigger file that should be loaded for this battle"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "battlemap", false,
						"Battle Map",
						"The name of the battle map file that should be loaded for this battle"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING, "entrance", false,
						"Entrance location",
						"The name of the map location that the force will be placed at when the map loads"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT, "battbg", false,
						"Battle Background Index",
						"The index of the battle background that should be used for the battle"));
		allowableLines.add(new PlannerLineDef("startbattle", "Load Battle",
				"Starts the battle with the given triggers and map",
				definingValues));

		// Set Battle Conditions
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "allleaders", false,
				"Defeat when all leaders killed ",
				"If true, then battle will only end in defeat if all of the leaders have been killed"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_HERO,
				PlannerValueDef.TYPE_MULTI_INT, "templeader", true,
				"Temporary Leaders. If killed battle ends ",
				"The ID of the quest that must be complete for this to be shown"));
		allowableLines.add(new PlannerLineDef("setbattlecond", "Set Battle Conditions",
				"Set conditions for the battle, should only be called during battle initialization",
				definingValues));

		// Load map
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "map", false, "Map Name",
				"The name of the map that should be loaded"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING,
						"enter",
						false,
						"Entrance location",
						"The name of the map location that the hero will be placed at when the map loads"));
		allowableLines
				.add(new PlannerLineDef(
						"loadmap",
						"Load Map",
						"Loads the given map and places the hero at the given location",
						definingValues));

		// Show Cinematic
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "map", false, "Map Name",
				"The name of the map that should be loaded"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT,
				"cinid", false, "Cinematic ID",
				"The ID of the cinematic that should be shown"));
		allowableLines.add(new PlannerLineDef("loadcin", "Load Cinematic",
				"Loads the specified map and text file with the same name and then runs the specified cinematic.", definingValues));

		// Show priest
		definingValues = new ArrayList<PlannerValueDef>();
		allowableLines.add(new PlannerLineDef("showpriest", "Show Priest",
				"Displays the priests menu", definingValues));

		// Play Music
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "music", false, "Music File",
				"The name of the music that should be played"));
		allowableLines.add(new PlannerLineDef("playmusic", "Play Music",
				"Plays the specified music", definingValues));

		// Play Sound
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "sound", false, "Sound File",
				"The name of the sound that should be played"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT,
						"volume",
						false,
						"Volume",
						"A number between 1-100 that represents the percent volume the sound should be played at"));
		allowableLines.add(new PlannerLineDef("playsound", "Play Sound",
				"Plays the specified sound effect", definingValues));

		// Change AI
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_AI_APPROACH,
				PlannerValueDef.TYPE_INT, "aitype", false, "AI Type",
				"The type of AI that the specified enemy should employ"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "id", false, "Unit",
				"The unit whose AI should be changed"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "heroid", true, "Hero",
				"The index of the hero that should be targeted"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "targetid", true, "Target Unit",
				"The target unit (enemy) that this enemy should follow"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "x", true, "X Location",
				"The x coordinate in tiles that this enemy should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "y", true, "Y Location",
				"The y coordinate in tiles that this enemy should move to"));
		allowableLines.add(new PlannerLineDef("changeai", "Change AI",
				"Changes the specified enemies approach AI", definingValues));

		// Show Text
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_TEXT,
				PlannerValueDef.TYPE_INT, "textid", false, "Text ID",
				"The ID of the text that should be displayed"));
		allowableLines.add(new PlannerLineDef("showtext", "Show Text",
				"Shows the text with the specified ID", definingValues));

		// Show Cinematic
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_CINEMATIC, PlannerValueDef.TYPE_INT,
				"cinid", false, "Cinematic ID",
				"The ID of the cinematic that should be shown"));
		allowableLines.add(new PlannerLineDef("showcin", "Show Map Event",
				"Shows the specified cinematic on the current map", definingValues));

		// Show Shop
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "shop", false, "Shop Attributes",
				"UNUSUABLE CURRENTLY"));
		allowableLines
				.add(new PlannerLineDef("showshop", "Show Shop",
						"Shows the shop menu with the specified items",
						definingValues));

		// Add hero
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_HERO,
				PlannerValueDef.TYPE_INT, "heroid", false, "Hero ID",
				"The ID of the hero that should be added to the force"));
		allowableLines.add(new PlannerLineDef("addhero", "Add Hero",
				"Adds a new hero to the force", definingValues));

		// Hide Roof
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "roofid", false, "Roof ID",
				"The ID of the roof that should no longer be visible"));
		allowableLines
				.add(new PlannerLineDef(
						"hideroof",
						"Hide Roof",
						"Hides the roof with designated ID. The roof will remain hidden until a show roof command is issued for that roof.",
						definingValues));

		// Show Roof
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "roofid", false, "Roof ID",
				"The ID of the roof that should be visible"));
		allowableLines.add(new PlannerLineDef("showroof", "Show Roof",
				"Shows the roof with designated ID.", definingValues));

		// Remove Sprite
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Sprite Name",
				"The name of the sprite to be removed"));
		allowableLines.add(new PlannerLineDef("removesprite", "Remove Sprite",
				"Removes the sprite from the map with the specified name.",
				definingValues));

		// Change Sprite Image
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "name", false, "Sprite Name",
				"The name of the sprite to be changed"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "image", false,
				"New Sprite Image", "The new image for this sprite"));
		allowableLines.add(new PlannerLineDef("changesprite",
				"Change Sprite Image",
				"Changes an existing sprites image to the specified image.",
				definingValues));

		// Add Item
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM,
				PlannerValueDef.TYPE_INT, "itemid", false, "Item ID",
				"The item that should be given to the group"));
		allowableLines
				.add(new PlannerLineDef(
						"additem",
						"Add Item to Group",
						"Gives the specified item to the first person with room in the group.",
						definingValues));

		// Exit Game
		definingValues = new ArrayList<PlannerValueDef>();
		allowableLines
				.add(new PlannerLineDef(
						"exit",
						"Exit Game",
						"Causes the game to exit.",
						definingValues));

		// TODO SHOW SHOP
		// TODO ADD HERO

		triggerContainer = new PlannerContainerDef(definingLine,
				allowableContainers, allowableLines, listOfLists,
				PlannerValueDef.REFERS_TRIGGER - 1);
		containersByName.put("trigger", triggerContainer);
	}

	public static void setupMapDefinitions(ArrayList<ArrayList<String>> listOfLists,
			Hashtable<String, PlannerContainerDef> containersByName) {
		PlannerContainerDef itemContainer;

		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		PlannerLineDef definingLine = new PlannerLineDef("start", "Item", "",
				definingValues);

		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();

		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();

		// Map Start Location
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "entrance", false, "Map Entrance Name",
				"The name of the entrance to the map."));
		allowableLines.add(new PlannerLineDef("Map Entrance", "Map Entrance",
				"This is used to determine where the hero starts when it enters the map. This is used in conjunction with the 'Load Map' trigger action",
				definingValues));

		// Use
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_BOOLEAN, "targetsenemy", false,
						"Targets Enemy",
						"Whether this item can be used on enemies, otherwise it is used on allies"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT, "damage", false,
						"Damage Dealt",
						"The amount of damage this item will deal on use (positive values will heal)"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_INT,
						"mpdamage",
						false,
						"MP Damage Dealt",
						"The amount of damage this item will deal to the targets MP on use (positive values will heal)"));
		definingValues
				.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM_RANGE,
						PlannerValueDef.TYPE_INT, "range", false, "Item Range",
						"The range this can be used from"));
		definingValues.add(new PlannerValueDef(
				PlannerValueDef.REFERS_ITEM_AREA, PlannerValueDef.TYPE_INT,
				"area", false, "Item Area of Effect",
				"The area that this item can effect"));
		definingValues
				.add(new PlannerValueDef(
						PlannerValueDef.REFERS_NONE,
						PlannerValueDef.TYPE_STRING,
						"text",
						false,
						"Item Use Text",
						"The text that will be appended after the targets name in the attack cinematic. An example value would be 'is healed for 10'. "
								+ "This would cause the battle text to become 'Noah is healed for 10'"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_BOOLEAN, "singleuse", false,
				"Single Use Item",
				"If true, the item will be removed after it has been used. "));

		allowableLines.add(new PlannerLineDef("use", "Usuable Item",
				"Marks this item as usuable and defines its' use",
				definingValues));

		itemContainer = new PlannerContainerDef(definingLine,
				allowableContainers, allowableLines, listOfLists,
				PlannerValueDef.REFERS_MAP - 1);
		containersByName.put("map", itemContainer);
	}

	public static void setupMapEditorDefinitions(ArrayList<ArrayList<String>> listOfLists,
			Hashtable<String, PlannerContainerDef> containersByName) {
		PlannerContainerDef plannerContainer;

		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		PlannerLineDef definingLine = new PlannerLineDef("mapedit", "MapEdit", "",
				definingValues);

		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();

		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();

		// start
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_STRING, "exit", false, "Start Name",
				"The name that this start location should be referenced by for triggers that must specify an 'Entrance Location'"));
		allowableLines.add(new PlannerLineDef("start", "start",
				"Marks this as a location that heroes can enter maps from or start battle at",
				definingValues));

		// npc

		// enemy
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ENEMY,
				PlannerValueDef.TYPE_INT, "enemyid", false, "Enemy ID",
				"The type of enemy that should be at this location"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_AI,
				PlannerValueDef.TYPE_STRING, "ai", false, "Enemy AI",
				"The type of AI this enemy should use once it is close to the heroes"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_AI_APPROACH,
				PlannerValueDef.TYPE_STRING, "aiapproach", false, "Enemy Approach Speed",
				"The type of AI this enemy should use to approach the heroes. Do not use follow or move to location here set it via a trigger"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE,
				PlannerValueDef.TYPE_INT, "unit", false, "Unit ID",
				"Use this ID to specifiy which enemy should be the target of triggers (Change AI)"));
		allowableLines.add(new PlannerLineDef("enemy", "enemy",
				"Creates an enemy at this location at the start of battle",
				definingValues));

		// trigger
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_TRIGGER,
				PlannerValueDef.TYPE_INT, "triggerid", false, "Trigger ID",
				"The ID of the trigger to execute"));
		allowableLines.add(new PlannerLineDef("trigger", "trigger",
				"The specified trigger will be executed whenever the hero moves on to this location (OUT OF BATTLE ONLY)",
				definingValues));

		// battletrigger
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_TRIGGER,
				PlannerValueDef.TYPE_INT, "triggerid", false, "Trigger ID",
				"The ID of the trigger to execute"));
		allowableLines.add(new PlannerLineDef("battletrigger", "battletrigger",
				"The specified trigger will be executed whenever a hero ends their turn on this location (BATTLE ONLY)",
				definingValues));

		// terrain
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_TERRAIN,
				PlannerValueDef.TYPE_STRING, "type", false, "Land Type",
				"Terrain type for combatants that end there turn on this location"));
		allowableLines.add(new PlannerLineDef("terrain", "terrain",
				"Defines terrain type for combatants that end there turn on this location",
				definingValues));

		// battleregion
		definingValues = new ArrayList<PlannerValueDef>();
		allowableLines.add(new PlannerLineDef("battleregion", "battleregion",
				"Defines the area that any hero/enemy and the battle cursor can be moved to in a battle on this map",
				definingValues));


		plannerContainer = new PlannerContainerDef(definingLine,
				allowableContainers, allowableLines, listOfLists,
				PlannerValueDef.REFERS_ITEM - 1);
		containersByName.put("mapedit", plannerContainer);
	}
}
