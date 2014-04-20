package mb.fc.utils.planner;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;

public class PlannerFrame extends JFrame implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = 1L;
	
	private Hashtable<String, PlannerContainerDef> containersByName;
	private ArrayList<ArrayList<String>> listOfLists;
	private JTabbedPane jtp;
	private File triggerFile;
	
	private static final int TAB_TRIGGER = 0;
	private static final int TAB_CIN = 1;
	private static final int TAB_TEXT = 2;
	private static final int TAB_HERO = 3;
	private static final int TAB_ENEMY = 4;
	private static final int TAB_ITEM = 5;
	private static final int TAB_QUEST = 6;
	
	private static String PATH_ENEMIES = "definitions/Enemies";
	private static String PATH_HEROES = "definitions/Heroes";
	private static String PATH_ITEMS = "definitions/Items";
	private static String PATH_ANIMATIONS = "animations/fsa";
	private static String PATH_QUESTS = "Quests";

	public static void main(String args[])
	{
		new PlannerFrame();
		System.out.println("Planner frame started");
	}
	
	private PlannerFrame()
	{
		super("Planner: NO TRIGGERS LOADED");
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem newTriggersItem = new JMenuItem("New Triggers/Speech/Cinematic");
		newTriggersItem.addActionListener(this);
		newTriggersItem.setActionCommand("new");
		fileMenu.add(newTriggersItem);
		JMenuItem openTriggersItem = new JMenuItem("Open Triggers/Speech/Cinematic");
		openTriggersItem.addActionListener(this);
		openTriggersItem.setActionCommand("open");
		fileMenu.add(openTriggersItem);
		/*
		JMenuItem createTriggersItem = new JMenuItem("Create Triggers Based on Map");
		createTriggersItem.addActionListener(this);
		createTriggersItem.setActionCommand("generate");
		fileMenu.add(createTriggersItem);
		*/
		JMenuItem saveTriggers = new JMenuItem("Save Triggers/Speech/Cinematic");
		saveTriggers.addActionListener(this);
		saveTriggers.setActionCommand("save");
		fileMenu.add(saveTriggers);
		JMenuItem savePersistentFiles = new JMenuItem("Save All");
		savePersistentFiles.addActionListener(this);
		savePersistentFiles.setActionCommand("saveall");
		fileMenu.add(savePersistentFiles);
		JMenuItem exportEnemyCheatSheetFiles = new JMenuItem("Export Enemy Cheat Sheet");
		exportEnemyCheatSheetFiles.addActionListener(this);
		exportEnemyCheatSheetFiles.setActionCommand("enemycheat");
		fileMenu.add(exportEnemyCheatSheetFiles);
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(this);
		exitItem.setActionCommand("exit");
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);		
		
		this.setJMenuBar(menuBar);
		
		containersByName =  new Hashtable<String, PlannerContainerDef>();
		
		listOfLists = new ArrayList<ArrayList<String>>();
		
		for (int i = 0; i < 18; i++)
			listOfLists.add(new ArrayList<String>());
			
		// Setup AI Types
		listOfLists.get(PlannerValueDef.REFERS_AI - 1).add("Wait for target");
		listOfLists.get(PlannerValueDef.REFERS_AI - 1).add("Kamikazee");
		listOfLists.get(PlannerValueDef.REFERS_AI - 1).add("Approach slowly");
		listOfLists.get(PlannerValueDef.REFERS_AI - 1).add("Follow");
		listOfLists.get(PlannerValueDef.REFERS_AI - 1).add("Move to point");
		
		// Setup stat gain types
		listOfLists.get(PlannerValueDef.REFERS_STAT_GAINS - 1).add("Weak");
		listOfLists.get(PlannerValueDef.REFERS_STAT_GAINS - 1).add("Average");
		listOfLists.get(PlannerValueDef.REFERS_STAT_GAINS - 1).add("Strong");
		listOfLists.get(PlannerValueDef.REFERS_STAT_GAINS - 1).add("Very Strong");
		
		// Setup usuable itemstyles
		listOfLists.get(PlannerValueDef.REFERS_ITEM_STYLE - 1).add("SPEAR");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_STYLE - 1).add("AXE");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_STYLE - 1).add("SWORD");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_STYLE - 1).add("STAFF");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_STYLE - 1).add("BOW");
		
		// Setup usuable item types
		listOfLists.get(PlannerValueDef.REFERS_ITEM_TYPE - 1).add("Weapon");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_TYPE - 1).add("Ring");
		
		// Setup usuable item ranges
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add("Self only");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add("All within 1");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add("All within 2");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add("All within 3");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add("Only at range 2");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add("Only at range 2 and 3");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_RANGE - 1).add("Only at range 3");
		
		// Setup usuable item areas
		listOfLists.get(PlannerValueDef.REFERS_ITEM_AREA - 1).add("None");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_AREA - 1).add("One square");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_AREA - 1).add("Five squares");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_AREA - 1).add("Thirteen squares");
		listOfLists.get(PlannerValueDef.REFERS_ITEM_AREA - 1).add("Everyone");
		
		// Setup movement types
		listOfLists.get(PlannerValueDef.REFERS_MOVE_TYPE - 1).add("WALKING");
		listOfLists.get(PlannerValueDef.REFERS_MOVE_TYPE - 1).add("CENTAUR");
		listOfLists.get(PlannerValueDef.REFERS_MOVE_TYPE - 1).add("BEASTMEN");
		listOfLists.get(PlannerValueDef.REFERS_MOVE_TYPE - 1).add("MECHANICAL");
		listOfLists.get(PlannerValueDef.REFERS_MOVE_TYPE - 1).add("FLYING");
		listOfLists.get(PlannerValueDef.REFERS_MOVE_TYPE - 1).add("HOVERING");
		listOfLists.get(PlannerValueDef.REFERS_MOVE_TYPE - 1).add("SWIMMING");
		listOfLists.get(PlannerValueDef.REFERS_MOVE_TYPE - 1).add("ELVES");
		
		// Setup spells
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Heal");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Aura");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Detox");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Boost");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Slow");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Strength");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Dispel");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Muddle");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Desoul");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Sleep");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Egress");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Blaze");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Freeze");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Bolt");
		listOfLists.get(PlannerValueDef.REFERS_SPELL - 1).add("Blast");
		
		// Setup Direction
		listOfLists.get(PlannerValueDef.REFERS_DIRECTION - 1).add("Up");
		listOfLists.get(PlannerValueDef.REFERS_DIRECTION - 1).add("Down");
		listOfLists.get(PlannerValueDef.REFERS_DIRECTION - 1).add("Left");
		listOfLists.get(PlannerValueDef.REFERS_DIRECTION - 1).add("Right");
				
		// Animation files
		File animations = new File(PATH_ANIMATIONS);
		for (String f : animations.list())
			if (f.endsWith(".fsa"))
				listOfLists.get(PlannerValueDef.REFERS_ANIMATIONS - 1).add(f.replaceFirst(".fsa", ""));

		
		/*******************/
		/* Set up triggers */
		/*******************/
		setupTriggerDefinition();	
		setupTextDefinitions();
		setupHeroDefinitions();
		setupEnemyDefinitions();
		setupItemDefinitions();
		setupQuestDefinitions();
		setupCinematicDefinitions();
		
		initUI();
		
		getSavedData();
	}
	
	private void exportData(ArrayList<PlannerContainer> containers, String pathToFile, boolean append)
	{
		ArrayList<String> buffer = new ArrayList<String>();
		for (int i = 0; i < containers.size(); i++)
		{
			PlannerContainer pc = containers.get(i);
			
			buffer.add(exportLine(pc.getPcdef().getDefiningLine(), pc.getDefLine(), i));
			
			for (PlannerLine pl : pc.getLines())
				buffer.add(exportLine(pl.getPlDef(), pl, -1));
			
			buffer.add("</" + pc.getPcdef().getDefiningLine().getTag() + ">");
		}				
		
		Path path = Paths.get(pathToFile);
		try {
			if (append)
				Files.write(path, buffer, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
			else
				Files.write(path, buffer, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String exportLine(PlannerLineDef pldef, PlannerLine pl, int id)
	{
		String stringBuffer = "";
		if (pl.isDefining())
			stringBuffer += "<" + pldef.getTag();
		else
			stringBuffer += "\t<" + pldef.getTag();
		
		if (id != -1)
			stringBuffer += " id=" + id;
		
		for (int i = 0; i < pldef.getPlannerValues().size(); i++)
		{
			PlannerValueDef pvd = pldef.getPlannerValues().get(i); 
			stringBuffer += " " + pvd.getTag() + "=";
			if (pvd.getValueType() == PlannerValueDef.TYPE_BOOLEAN)
				stringBuffer += pl.getValues().get(i);
			else if (pvd.getValueType() == PlannerValueDef.TYPE_STRING)
				stringBuffer += "\"" + pl.getValues().get(i) +  "\"";
			else if (pvd.getValueType() == PlannerValueDef.TYPE_INT)
			{
				if (pvd.getRefersTo() == PlannerValueDef.REFERS_NONE)
					stringBuffer += pl.getValues().get(i);
				else
					stringBuffer += (int) pl.getValues().get(i) - 1;
			}
			else if (pvd.getValueType() == PlannerValueDef.TYPE_MULTI_INT)
			{
				String[] oldVals = ((String) pl.getValues().get(i)).split(",");
				String newVals = "";
				
				for (int j = 0; j < oldVals.length; j++)
				{
					newVals = newVals + (Integer.parseInt(oldVals[j]) - 1);
					if (j + 1 <= oldVals.length)
						newVals += ",";
				}
				
				stringBuffer += newVals;
			}
		}
		if (pl.isDefining())
			stringBuffer += ">";
		else
			stringBuffer += "/>";
		return stringBuffer;
	}
	
	private void getSavedData()
	{
		try 
		{
			/*
			jtp.addTab("Triggers", new PlannerTab(containersByName, new String[] {"trigger"}));		
			jtp.addTab("Cinematic", new PlannerTab(containersByName, new String[] {"cinematic"}));		
			jtp.addTab("Speech", new PlannerTab(containersByName, new String[] {"text"}));		
			jtp.addTab("Heroes", new PlannerTab(containersByName, new String[] {"hero"}));
			jtp.addTab("Enemies", new PlannerTab(containersByName, new String[] {"enemy"}));
			jtp.addTab("Items", new PlannerTab(containersByName, new String[] {"item"}));
			jtp.addTab("Quests", new PlannerTab(containersByName, new String[] {"quest"}));
			*/
			
			parseContainer(XMLParser.process(Files.readAllLines(Paths.get(PATH_ENEMIES), StandardCharsets.UTF_8)), TAB_ENEMY, "enemy");
			parseContainer(XMLParser.process(Files.readAllLines(Paths.get(PATH_HEROES), StandardCharsets.UTF_8)), TAB_HERO, "hero");
			parseContainer(XMLParser.process(Files.readAllLines(Paths.get(PATH_ITEMS), StandardCharsets.UTF_8)), TAB_ITEM, "item");
			parseContainer(XMLParser.process(Files.readAllLines(Paths.get(PATH_QUESTS), StandardCharsets.UTF_8)), TAB_QUEST, "quest");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseContainer(ArrayList<TagArea> tas, int tabIndex, String allowableValue)
	{
		for (TagArea ta : tas)
		{			
			if (!ta.getTagType().equalsIgnoreCase(allowableValue))
				continue;
			System.out.println(ta.getTagType());
			PlannerContainerDef pcd = containersByName.get(ta.getTagType());
			PlannerContainer plannerContainer = new PlannerContainer(pcd);
			PlannerLine plannerLine = plannerContainer.getDefLine();
			parseLine(plannerLine, pcd.getDefiningLine(), ta);
			pcd.getDataLines().add(plannerContainer.getDescription());
			((PlannerTab) this.jtp.getComponent(tabIndex)).addPlannerContainer(plannerContainer);
			
			for (TagArea taChild : ta.getChildren())
			{
				boolean found = false;
				for (PlannerLineDef allowable : pcd.getAllowableLines())
				{
					if (taChild.getTagType().equalsIgnoreCase(allowable.getTag()))
					{
						found = true;
						PlannerLine childLine = new PlannerLine(allowable, false);
						parseLine(childLine, allowable, taChild);
						plannerContainer.addLine(childLine);
					}
				}
				
				if (!found)
					System.out.println("Unable to find tag definition for " + taChild.getTagType());
			}
		}
	}
	
	private void parseLine(PlannerLine plannerLine, PlannerLineDef pld, TagArea ta)
	{
		System.out.println("PARENT: " + pld.getTag());
		for (PlannerValueDef pvd : pld.getPlannerValues())			
		{
			if (pvd.getValueType() == PlannerValueDef.TYPE_STRING)
				plannerLine.getValues().add(ta.getParams().get(pvd.getTag()));
			else if (pvd.getValueType() == PlannerValueDef.TYPE_INT)
			{
				if (pvd.getRefersTo() == PlannerValueDef.REFERS_NONE)
				{
					System.out.println("TAG: " + pvd.getTag() + " " + ta.getParams().get(pvd.getTag()));
					int value = 0;
					try
					{
						value = Integer.parseInt(ta.getParams().get(pvd.getTag()));
					}
					catch (NumberFormatException ex) {}
					
					plannerLine.getValues().add(value);
				}
				else
					plannerLine.getValues().add(Integer.parseInt(ta.getParams().get(pvd.getTag())) + 1);
			}
			else if (pvd.getValueType() == PlannerValueDef.TYPE_MULTI_INT)
			{								
				String newVals = "";
				
				if (ta.getParams().get(pvd.getTag()) !=  null)
				{
					String[] values = ta.getParams().get(pvd.getTag()).split(",");				
					for (int j = 0; j < values.length; j++)
					{
						newVals = newVals + (Integer.parseInt(values[j]) + 1);
						if (j + 1 != values.length)
							newVals = newVals + ",";
					}
				}
				else
					newVals = "0";
				
				plannerLine.getValues().add(newVals);
			}
			else if (pvd.getValueType() == PlannerValueDef.TYPE_BOOLEAN)
				plannerLine.getValues().add(Boolean.parseBoolean(ta.getParams().get(pvd.getTag())));
			
		}
	}
	
	private void setupCinematicDefinitions()
	{
		PlannerContainerDef cinematicContainer;
		
		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "description", false, 
				"Description", "A description of the object that will be presented to the players"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "camerax", false, 
				"The initial X location of the camera", "The x position (in pixels) of the items image"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "cameray", false, 
				"The initial Y location of the camera", "The y position (in pixels) of the items image"));
		PlannerLineDef definingLine = new PlannerLineDef("cinematic", "Cinematic", "", definingValues);
		
		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();
		
		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();
				
		// Add actor
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "x", false, 
				"Start Location X", "The x coordinate (in pixels) to start the actor in"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "y", false, 
				"Start Location Y", "The y coordinate (in pixels) to start the actor in"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor to be created. This will be used to reference the actor in the cinematic"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ANIMATIONS, PlannerValueDef.TYPE_STRING, "anim", false, 
				"Animation file", "The name of the animation file to be used for this actor"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "startanim", false, 
				"Starting Animation", "The name of the animation that this actor should start in"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "visible", false, 
				"Starts Visible", "Whether this actor should start visible"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "init", false, 
				"Initialize before Cinematic", "Indicates that this action should be taken before the scene is rendered. Blocking actions should NEVER be initialized before the scene"));

		allowableLines.add(new PlannerLineDef("addactor", "Add Actor", "Adds an actor to the cinematic, this actor can be accessed in the future by its' name", definingValues));
		
		// Camera follow
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that the camera should follow. This actor should already have been added to the cinematic"));

		allowableLines.add(new PlannerLineDef("camerafollow", "Set Camera follows actor", "Causes the camera to always be centered on the current actor. You can cancel this function by calling another camera location command", definingValues));
		
		// Halting Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the action"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "x", false, 
				"X Coordinate", "The x coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "y", false, 
				"Y Coordinate", "The y coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "speed", false, 
				"Move Speed", "The amount of pixels that the actor will move every 30ms towards their destination"));

		allowableLines.add(new PlannerLineDef("haltingmove", "Halting Move", "Orders the specified actor to move to the specified coordinate. This action is 'halting' which means no further actions will be issued until this action is complete", definingValues));
		
		// Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the action"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "x", false, 
				"X Coordinate", "The x coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "y", false, 
				"Y Coordinate", "The y coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "speed", false, 
				"Move Speed", "The amount of pixels that the actor will move every 30ms towards their destination"));

		allowableLines.add(new PlannerLineDef("move", "Move", "Orders the specified actor to move to the specified coordinate.", definingValues));
		
		// Halting Anim
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the action"));		
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "time", false, 
				"Time", "The amount of time in milliseconds that this animation should be performed over. All frames will be shown for an equal time."));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "anim", false, 
				"Animation to Show", "The name of the animation that the actor should take"));

		allowableLines.add(new PlannerLineDef("haltinganim", "Halting Animation", "Causes the specified actor to perform the specified animation, This action is 'halting' which means no further actions will be issued until this action is complete.", definingValues));
		
		// Anim
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the action"));		
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "time", false, 
				"Time", "The amount of time in milliseconds that this animation should be performed over. All frames will be shown for an equal time."));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "anim", false, 
				"Animation to Show", "The name of the animation that the actor should take"));

		allowableLines.add(new PlannerLineDef("anim", "Animation", "Causes the specified actor to perform the specified animation.", definingValues));
		
		// Camera Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "x", false, 
				"X Coordinate", "The x coordinate (in pixels) that the camera should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "y", false, 
				"Y Coordinate", "The y coordinate (in pixels) that the camera should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "time", false, 
				"Moving Time", "The amount of time in ms that the camera should be moved over"));

		allowableLines.add(new PlannerLineDef("cameramove", "Camera Pan", "Pans the camera to the specified location over time. If the time is 0 then the camera immediate will move to the location", definingValues));
		
		// Text Box
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "text", false, 
				"Text", "The text that should be displayed"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "portrait", false, 
				"Portrait Index", "The index of the portrait that should be used from the Image/Portraits file starting from 0. A value of -1 means no portrait should be displayed"));

		allowableLines.add(new PlannerLineDef("speech", "Show Speech Box", "Displays the specified text in a text box. This action is 'halting', which means subsequent actions will not be performed until the text box is dismissed via user input.", definingValues));
		
		// Load map
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "map", false, 
				"Map", "The name of the map file to be loaded, the associated text file should have the same name"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "enter", false, 
				"Map Entrance", "The entrance area definied in the map file that the hero should start in once the map loads. The area should be marked with a 'start' name and an 'exit' value"));

		allowableLines.add(new PlannerLineDef("loadmap", "Load Map", "Loads the specified map and places the hero at the specified entrance.", definingValues));
		
		// Wait
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "time", false, 
				"Wait Time", "The amount of time in ms that the cinematic should wait before more actions are processed"));

		allowableLines.add(new PlannerLineDef("wait", "Wait", "Halts new actions from being processed for the specified time. This is a halting action", definingValues));
		
		// Spin
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should spin"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "speed", false, 
				"Spin Speed", "The amount of time in ms that should pass in between changing facing direction"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "time", false, 
				"Spin Duration", "The amount of time that this actor should spin for. A value of -1 indicates that this actor should spin for an indefinite amount of time. If an indefinite amount of time is specified then the actor will stop spinning when the a STOP SPIN action is issued"));

		allowableLines.add(new PlannerLineDef("spin", "Spin Actor", "Causes the specified actor to begin spinning, this will cause other animations to stop for the duration of the spin. This can be used in conjunction with the grow and shrink special effect", definingValues));
		
		// Stop Spin
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should stop spinning"));
		
		allowableLines.add(new PlannerLineDef("stopspin", "Stop Actor Spinning", "Stops the specified actor from spinning. If the actor is not spinning then no action will be taken", definingValues));
		
		// Facing
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should change their facing"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_DIRECTION, PlannerValueDef.TYPE_INT, "dir", false, 
				"Facing Direction", "The direction that the actor should face"));

		allowableLines.add(new PlannerLineDef("facing", "Set Actor Facing", "Causes the specified actor to face the specified direction", definingValues));
		
		// Shrink
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the shrink special effect"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "time", false, 
				"Shrink Time", "The amount of time that this actor should shrink over"));

		allowableLines.add(new PlannerLineDef("shrink", "Shrink Actor", "Causes the specified actor to shrink over time, once the time is up the actor will immediately return to normal size, so if the intention is to remove the actor then it should be removed immediately before the end of this action. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.", definingValues));
		
		// Grow
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the grow special effect"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "time", false, 
				"Grow Time", "The amount of time that this actor should grow over"));

		allowableLines.add(new PlannerLineDef("grow", "Grow Actor", "Causes the specified actor to grow over time from 1% height to 100% height, once the time is up the actor will immediately return to normal size. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations", definingValues));
		
		// Stop Special Effect
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should stop performing special effects"));

		allowableLines.add(new PlannerLineDef("stopse", "Stop Actor Special Effect", "Causes the specified actor to stop peforming any special effects that are currently active. This should be used to stop special effects of indefinite duration.", definingValues));
		
		// Visible
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor whose visibility should be changed"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "isvis", false, 
				"Is Visible", "If true, the actor should become visible, otherwise they should become invisible"));

		allowableLines.add(new PlannerLineDef("visible", "Set Actor Visiblity", "Sets the specified actors visiblity", definingValues));
		
		// Remove Actor
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should be removed from the cinematic"));

		allowableLines.add(new PlannerLineDef("removeactor", "Remove Actor", "Removes the specified actor from the cinematic. This actor will no longer be able to be the target of actions.", definingValues));
		
		// Quiver
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the quiver special effect"));

		allowableLines.add(new PlannerLineDef("quiver", "Start Actor Quivering", "Causes the specified actor to begin quivering. This effect will continue until a STOP SPECIAL EFFECT is issued for the actor. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.", definingValues));
		
		// Fall on Face
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the fall-on-face special effect"));

		allowableLines.add(new PlannerLineDef("fallonface", "Actor Fall on Face", "Causes the specified actor to fall on their face. This effect will continue until a STOP SPECIAL EFFECT is issued for the actor. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.", definingValues));
				
		// Lay on Side
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the lay-on-side special effect"));

		allowableLines.add(new PlannerLineDef("layonside", "Actor Lay on Side", "Causes the specified actor to lay on their side. This effect will continue until a STOP SPECIAL EFFECT is issued for the actor. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.", definingValues));
		
		// Flash
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the flash special effect"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "time", false, 
				"Flash Duration", "The amount of time in ms that this actor should flash for"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "speed", false, 
				"Flash Speed", "The amount of time that a single flash should take"));

		allowableLines.add(new PlannerLineDef("flash", "Actor Flash", "Causes the specified actor to flash white. If the duration is marked as indefinite then this effect will continue until a STOP SPECIAL EFFECT is issued for the actor. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.", definingValues));
		
		// Nod
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the nod effect"));

		allowableLines.add(new PlannerLineDef("nod", "Actor Nod", "Causes the specified actor to nod. This effect lasts 500ms. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.", definingValues));
		
		// Shake head
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the shake head effect"));

		allowableLines.add(new PlannerLineDef("shakehead", "Actor Shake Head", "Causes the specified actor to nod. This effect lasts 750ms. This is a 'special effect'. Only one special effect can be active on a given actor at any time. This will stop any current animations.", definingValues));
			
		// Loop Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the looping move"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "x", false, 
				"X Coordinate", "The x coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "y", false, 
				"Y Coordinate", "The y coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "speed", false, 
				"Move Speed", "The amount of pixels that the actor will move every 30ms towards their destination"));

		allowableLines.add(new PlannerLineDef("loopmove", "Move Actor in Loop", "Causes the specified actor to move to the specified location, once the actor gets to that location they will teleport back to where they started when this action was first called. This action will continue until a STOP LOOP MOVE action is called on this actor or another move command is issued for this actor", definingValues));
				
		// Stop Loop Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should stop their looping move"));

		allowableLines.add(new PlannerLineDef("stoploopmove", "Stop Actor Looping Move", "Causes the specified actor to stop looping their move, they will still walk to their target location but will not teleport", definingValues));
		
		// Camera Shake
		definingValues = new ArrayList<PlannerValueDef>();	
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "time", false, 
				"Shake Time", "The amount of time that the camera should shake for"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "severity", false, 
				"Severity", "The amount of pixels that the camera can be offset to during the shake"));

		allowableLines.add(new PlannerLineDef("camerashake", "Shake Camera", "Shakes the camera to simulate an earthquake effect. After the camera is done shaking it will return to it's original location", definingValues));
		
		// Forced Facing Move
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Actor Name", "The name of the actor that should perform the action"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "x", false, 
				"X Coordinate", "The x coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "y", false, 
				"Y Coordinate", "The y coordinate (in pixels) that the actor should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "speed", false, 
				"Move Speed", "The amount of pixels that the actor will move every 30ms towards their destination"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_DIRECTION, PlannerValueDef.TYPE_INT, "facing", false, 
				"Facing", "The direction that the sprite should face for the duration of the move"));

		allowableLines.add(new PlannerLineDef("forcedmove", "Move Forced Facing", "Orders the specified actor to move to the specified coordinate. This actor will keep facing the same direction for the duration of the move", definingValues));
		
		// Play Music
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "music", false, 
				"Music Title", "The name of the music that should be played"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "volume", false, 
				"Volume", "The percent volume that the music should be played at. This should be a value between 1-100"));

		allowableLines.add(new PlannerLineDef("playmusic", "Play Music", "Loops music at the specified volume. Only one song can be playing at a time, starting music while one is already playing will end the other one.", definingValues));
		
		// Pause Music
		definingValues = new ArrayList<PlannerValueDef>();
		allowableLines.add(new PlannerLineDef("pausemusic", "Pause Music", "Pauses the currently playing music", definingValues));
		
		// Resume Music
		definingValues = new ArrayList<PlannerValueDef>();
		allowableLines.add(new PlannerLineDef("resumemusic", "Resume Music", "Resumes any paused music", definingValues));
		
		// Fade Music
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "duration", false, 
				"Fade Duration", "The amount of time in ms that the music should fade out over"));
		allowableLines.add(new PlannerLineDef("fademusic", "Fade Out Music", "Fades out any playing music to no volume over the specified period of time. The music stops playing after the fade", definingValues));
		
		
		// Play Sound
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "sound", false, 
				"Sound Title", "The name of the sound that should be played"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "volume", false, 
				"Volume", "The percent volume that the sound should be played at. This should be a value between 1-100"));

		allowableLines.add(new PlannerLineDef("playsound", "Play Sound", "Plays sound at the specified volume.", definingValues));
		
		cinematicContainer = new PlannerContainerDef(definingLine, allowableContainers, allowableLines, listOfLists, PlannerValueDef.REFERS_CINEMATIC - 1);
		containersByName.put("cinematic", cinematicContainer);
	}
	
	private void setupItemDefinitions()
	{
		PlannerContainerDef itemContainer;
		
		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Name", "The name of the item"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "description", false, 
				"Description", "A description of the object that will be presented to the players"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "cost", false, 
				"Cost", "The amount this item costs to purchase"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "imageindexx", false, 
				"X Index", "The x index of the items image"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "imageindexy", false, 
				"Y Index", "The y index of the items image"));
		PlannerLineDef definingLine = new PlannerLineDef("item", "Item", "", definingValues);
		
		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();
		
		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();
				
		// Equippable
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "attack", false, 
				"Attack Modifier", "The amount equipping this item will modify attack"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "defense", false, 
				"Defense Modifier", "The amount equipping this item will modify defense"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "speed", false, 
				"Speed Modifier", "The amount equipping this item will modify speed"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM_TYPE, PlannerValueDef.TYPE_INT, "type", false, 
				"Item Type", "Whether this item is a weapon or ring"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM_STYLE, PlannerValueDef.TYPE_INT, "style", false, 
				"Item Style", "What type of weapon this, use any value for rings"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM_RANGE, PlannerValueDef.TYPE_INT, "range", false, 
				"Item Range", "The range this weapon can attack from, use any value for rings"));
		allowableLines.add(new PlannerLineDef("equippable", "Equippable Item", "Marks this item as equippable and defines stats for it", definingValues));		
		
		// Use
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "targetsenemy", false, 
				"Targets Enemy", "Whether this item can be used on enemies, otherwise it is used on allies"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "damage", false, 
				"Damage Dealt", "The amount of damage this item will deal on use (positive values will heal)"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "mpdamage", false, 
				"MP Damage Dealt", "The amount of damage this item will deal to the targets MP on use (positive values will heal)"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM_RANGE, PlannerValueDef.TYPE_INT, "range", false, 				
				"Item Range", "The range this can be used from"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM_AREA, PlannerValueDef.TYPE_INT, "area", false, 				
				"Item Area of Effect", "The area that this item can effect"));		
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "text", false, 				
				"Item Use Text", "The text that will be appended after the targets name in the attack cinematic. An example value would be 'is healed for 10'. "
						+ "This would cause the battle text to become 'Noah is healed for 10'"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "singleuse", false, 				
				"Single Use Item", "If true, the item will be removed after it has been used. "));
		
		allowableLines.add(new PlannerLineDef("use", "Usuable Item", "Marks this item as usuable and defines its' use", definingValues));	
		
		itemContainer = new PlannerContainerDef(definingLine, allowableContainers, allowableLines, listOfLists, PlannerValueDef.REFERS_ITEM - 1);
		containersByName.put("item", itemContainer);
	}
	
	private void setupQuestDefinitions()
	{
		PlannerContainerDef textContainer;
		
		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "description", false, 
				"Description", "Description"));
		// definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "triggerid", false, 
			//	"Unique Trigger Id", "Unique id that can be used to identify a given trigger"));
		PlannerLineDef definingLine = new PlannerLineDef("quest", "Quest", "", definingValues);
		
		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();
		
		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();						
		
		textContainer = new PlannerContainerDef(definingLine, allowableContainers, allowableLines, listOfLists, PlannerValueDef.REFERS_QUEST - 1);
		containersByName.put("quest", textContainer);
	}
	
	private void setupTextDefinitions()
	{
		PlannerContainerDef textContainer;
		
		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "description", false, 
				"Description", "Description"));
		// definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "triggerid", false, 
			//	"Unique Trigger Id", "Unique id that can be used to identify a given trigger"));
		PlannerLineDef definingLine = new PlannerLineDef("text", "Text", "", definingValues);
		
		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();
		
		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();
				
		// Text
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "message", false, 
				"Message Text", "The text that should be displayed. Using the { character will cause a short pause, the } character will do a soft stop and the } chararacter will do a hard stop."));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_QUEST, PlannerValueDef.TYPE_MULTI_INT, "require", false, 
				"Required Quest", "The ID of the quest that must be complete for this to be shown"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_QUEST, PlannerValueDef.TYPE_MULTI_INT, "exclude", false, 
				"Exclude Quest", "The ID of the quest that CAN NOT be complete for this to be shown"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_TRIGGER, PlannerValueDef.TYPE_INT, "trigger", false, 
				"Trigger ID", "The ID of the trigger that should be run after this message is complete."));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "portrait", false, 
				"Portrait Index", "The index of the portrait in the Image/Portraits file starting from index 0 that should be shown during this text. A value of -1 means no portrait should be shown"));
		allowableLines.add(new PlannerLineDef("string", "Message Text", "A message that should be displayed", definingValues));		
		
		textContainer = new PlannerContainerDef(definingLine, allowableContainers, allowableLines, listOfLists, PlannerValueDef.REFERS_TEXT - 1);
		containersByName.put("text", textContainer);
	}
	
	private void setupEnemyDefinitions()
	{
		PlannerContainerDef enemyContainer;
		
		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Name", "The name of the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "hp", false, 
				"HP", "Starting HP for the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "mp", false, 
				"MP", "Starting MP for the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "attack", false, 
				"Attack", "Starting Attack for the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "defense", false, 
				"Defense", "Starting Defense for the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "speed", false, 
				"Speed", "Starting Speed for the enemy"));		
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "level", false, 
				"Level", "Starting Level for the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "move", false, 
				"Move", "Starting Move for the enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_MOVE_TYPE, PlannerValueDef.TYPE_INT, "movementtype", false, 
				"Movement Type", "The enemies movement type as it relates to land effect and barriers"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "portrait", false, 
				"Portrait Index", "The index of the portrait in the portraits image. If this enemy has no portrait then use -1"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ANIMATIONS, PlannerValueDef.TYPE_STRING, "animations", false, 
				"Animation File", "The name of the animation file that should be used for this enemy"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "leader", false, 
				"Is Leader", "Whether this enemy is the leader of the force"));
		
		// definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "triggerid", false, 
			//	"Unique Trigger Id", "Unique id that can be used to identify a given trigger"));
		PlannerLineDef definingLine = new PlannerLineDef("enemy", "Enemy", "", definingValues);
		
		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();
		
		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();						
		
		// Spell Progression
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_SPELL, PlannerValueDef.TYPE_INT, "spellid", false, 
				"Spell ID", "The ID of the spell that this enemy knows"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "level", false, 
				"Max Level", "The max level known of the specified spell"));
		allowableLines.add(new PlannerLineDef("spell", "Spell", "A spell that this enemy knows", definingValues));		
		
		// Items Equipped
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM, PlannerValueDef.TYPE_INT, "itemid", false, 
				"Item ID", "The ID of the item that this enemy should start with"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "equipped", false, 
				"Item Equipped", "If true, the item will start as equipped."));
		allowableLines.add(new PlannerLineDef("item", "Starting Item", "An item that this hero should start with", definingValues));
		
		enemyContainer = new PlannerContainerDef(definingLine, allowableContainers, allowableLines, listOfLists, PlannerValueDef.REFERS_ENEMY - 1);
		containersByName.put("enemy", enemyContainer);
	}
	
	private void setupHeroDefinitions()
	{
		PlannerContainerDef heroContainer;
		
		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Name", "The name of the hero"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "hp", false, 
				"HP", "Starting HP for the hero"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "mp", false, 
				"MP", "Starting MP for the hero"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "attack", false, 
				"Attack", "Starting Attack for the hero"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "defense", false, 
				"Defense", "Starting Defense for the hero"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "speed", false, 
				"Speed", "Starting Speed for the hero"));		
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "promoted", false, 
				"Promoted", "If true, this hero is promoted"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "level", false, 
				"Level", "Starting Level for the hero"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ANIMATIONS, PlannerValueDef.TYPE_STRING, "animations", false, 
				"Animation File", "The name of the animation file that should be used for this hero"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "leader", true, 
				"Is Leader", "Whether this hero is the leader of the force"));
		
		// definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "triggerid", false, 
			//	"Unique Trigger Id", "Unique id that can be used to identify a given trigger"));
		PlannerLineDef definingLine = new PlannerLineDef("hero", "Hero", "", definingValues);
		
		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();
		
		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();
				
		// Progression
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "promoted", false, 
				"Promoted Progression", "Whether this progression represents this heroes promoted or unpromoted progression"));		
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "move", false, 
				"Starting Move", "The heroes base move while in this progression"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_MOVE_TYPE, PlannerValueDef.TYPE_INT, "movementtype", false, 
				"Movement Type", "The movement type of this hero"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_STAT_GAINS, PlannerValueDef.TYPE_INT, "attack", false, 
				"Attack Gain", "The amount of attack the hero should gain per level"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_STAT_GAINS, PlannerValueDef.TYPE_INT, "defense", false, 
				"Defense Gain", "The amount of defense the hero should gain per level"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_STAT_GAINS, PlannerValueDef.TYPE_INT, "speed", false, 
				"Speed Gain", "The amount of speed the hero should gain per level"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_STAT_GAINS, PlannerValueDef.TYPE_INT, "hp", false, 
				"HP Gain", "The amount of HP the hero should gain per level"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_STAT_GAINS, PlannerValueDef.TYPE_INT, "mp", false, 
				"MP Gain", "The amount of MP the hero should gain per level"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "portrait", false, 
				"Portrait Index", "The index of the portrait in the portraits image."));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM_STYLE, PlannerValueDef.TYPE_MULTI_INT, "usuableitems", false, 
				"Usuable Items", "The type of weapons that this hero can use"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "class", false, 
				"Class Name", "The name of this characters class."));
		allowableLines.add(new PlannerLineDef("progression", "Hero Progression", "This heroes statistic progression", definingValues));
		
		// Spell Progression
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_SPELL, PlannerValueDef.TYPE_INT, "spellid", false, 
				"Spell ID", "The ID of the spell that this hero knows"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "gained", false, 
				"Levels Gained", "A comma seperated list of the levels that the spell levels will be gained at."));
		allowableLines.add(new PlannerLineDef("spellprogression", "Spell Progression", "This heroes spell progression", definingValues));		
		
		// Items Equipped
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM, PlannerValueDef.TYPE_INT, "itemid", false, 
				"Item ID", "The ID of the item that this hero should start with"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "equipped", false, 
				"Item Equipped", "If true, the item will start as equipped."));
		allowableLines.add(new PlannerLineDef("item", "Starting Item", "An item that this hero should start with", definingValues));
		
		heroContainer = new PlannerContainerDef(definingLine, allowableContainers, allowableLines, listOfLists, PlannerValueDef.REFERS_HERO - 1);
		containersByName.put("hero", heroContainer);
	}
	
	private void setupTriggerDefinition()
	{
		PlannerContainerDef triggerContainer;
		
		// Setup defining line
		ArrayList<PlannerValueDef> definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "description", false, 
				"Description", "Description"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_QUEST, PlannerValueDef.TYPE_MULTI_INT, "require", false, 
				"Required Quest", "The ID of the quest that must be complete for this to be shown"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_QUEST, PlannerValueDef.TYPE_MULTI_INT, "exclude", false, 
				"Exclude Quest", "The ID of the quest that CAN NOT be complete for this to be shown"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "nonretrig", false, 
				"Non Retriggerable", "If true, indicates that this trigger can only be executed once per game"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_BOOLEAN, "retrigonenter", false, 
				"Retrigger Each Enter", "If true, indicates that each time the map has been entered that this trigger should be reactivated"));
		PlannerLineDef definingLine = new PlannerLineDef("trigger", "Trigger", "", definingValues);
		
		// Setup allowable containers
		ArrayList<PlannerContainerDef> allowableContainers = new ArrayList<PlannerContainerDef>();
		
		// Setup available types
		ArrayList<PlannerLineDef> allowableLines = new ArrayList<PlannerLineDef>();						
		
		// Complete Quest
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_QUEST, PlannerValueDef.TYPE_INT, "questid", false, 
				"Quest ID", "The ID of the equest that should be marked as complete"));
				allowableLines.add(new PlannerLineDef("completequest", "Complete Quest", "Marks a given quest as completed", definingValues));
				
		// Start Battle
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "battletriggers", false, 
				"Battle Trigger File", "The name of the battle trigger file that should be loaded for this battle"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "battlemap", false, 
				"Battle Map", "The name of the battle map file that should be loaded for this battle"));
		allowableLines.add(new PlannerLineDef("startbattle", "Start Battle", "Starts the battle with the given triggers and map", definingValues));
		
		// Load map
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "map", false, 
				"Map Name", "The name of the map that should be loaded"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "enter", false, 
				"Entrance location", "The name of the map location that the hero will be placed at when the map loads"));
		allowableLines.add(new PlannerLineDef("loadmap", "Load Map", "Loads the given map and places the hero at the given location", definingValues));		
		
		// Show priest
		definingValues = new ArrayList<PlannerValueDef>();
		allowableLines.add(new PlannerLineDef("showpriest", "Show Priest", "Displays the priests menu", definingValues));
		
		// Play Music
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "music", false, 
				"Music File", "The name of the music that should be played"));
		allowableLines.add(new PlannerLineDef("playmusic", "Play Music", "Plays the specified music", definingValues));
		
		// Play Sound
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "sound", false, 
				"Sound File", "The name of the sound that should be played"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "volume", false, 
				"Volume", "A number between 1-100 that represents the percent volume the sound should be played at"));
		allowableLines.add(new PlannerLineDef("playsound", "Play Sound", "Plays the specified sound effect", definingValues));
		
		// Change AI
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_AI, PlannerValueDef.TYPE_INT, "aitype", false, 
				"AI Type", "The type of AI that the specified enemy should employ"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "id", false, 
				"Enemy ID", "The ID of the enemy whose AI should be changed"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "targetid", true, 
				"Target ID", "The ID of the target (enemy) that this enemy should follow"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "x", true, 
				"X Location", "The x coordinate in tiles that this enemy should move to"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "y", true, 
				"Y Location", "The y coordinate in tiles that this enemy should move to"));
		allowableLines.add(new PlannerLineDef("changeai", "Change AI", "Changes the specified enemies approach AI", definingValues));
		
		// Show Text
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_TEXT, PlannerValueDef.TYPE_INT, "textid", false, 
				"Text ID", "The ID of the text that should be displayed"));
		allowableLines.add(new PlannerLineDef("showtext", "Show Text", "Shows the text with the specified ID", definingValues));
		
		// Show Cinematic
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_CINEMATIC, PlannerValueDef.TYPE_INT, "cinid", false, 
				"Cinematic ID", "The ID of the cinematic that should be shown"));
		allowableLines.add(new PlannerLineDef("showcin", "Show Cinematic", "Shows the specified cinematic", definingValues));
		
		// Show Shop
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "shop", false, 
				"Shop Attributes", "UNUSUABLE CURRENTLY"));
		allowableLines.add(new PlannerLineDef("showshop", "Show Shop", "Shows the shop menu with the specified items", definingValues));
		
		// Add hero
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_HERO, PlannerValueDef.TYPE_INT, "heroid", false, 
				"Hero ID", "The ID of the hero that should be added to the force"));
		allowableLines.add(new PlannerLineDef("addhero", "Add Hero", "Adds a new hero to the force", definingValues));
		
		// Hide Roof
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "roofid", false, 
				"Roof ID", "The ID of the roof that should no longer be visible"));
		allowableLines.add(new PlannerLineDef("hideroof", "Hide Roof", "Hides the roof with designated ID. The roof will remain hidden until a show roof command is issued for that roof.", definingValues));
		
		// Show Roof
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_INT, "roofid", false, 
				"Roof ID", "The ID of the roof that should be visible"));
		allowableLines.add(new PlannerLineDef("showroof", "Show Roof", "Shows the roof with designated ID.", definingValues));
		
		// Remove Sprite
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Sprite Name", "The name of the sprite to be removed"));
		allowableLines.add(new PlannerLineDef("removesprite", "Remove Sprite", "Removes the sprite from the map with the specified name.", definingValues));
		
		// Change Sprite Image
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "name", false, 
				"Sprite Name", "The name of the sprite to be changed"));
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_NONE, PlannerValueDef.TYPE_STRING, "image", false, 
				"New Sprite Image", "The new image for this sprite"));
		allowableLines.add(new PlannerLineDef("changesprite", "Change Sprite Image", "Changes an existing sprites image to the specified image.", definingValues));
		
		// Add Item
		definingValues = new ArrayList<PlannerValueDef>();
		definingValues.add(new PlannerValueDef(PlannerValueDef.REFERS_ITEM, PlannerValueDef.TYPE_INT, "itemid", false, 
				"Item ID", "The item that should be given to the group"));
		allowableLines.add(new PlannerLineDef("additem", "Add Item to Group", "Gives the specified item to the first person with room in the group.", definingValues));
		
		// TODO SHOW SHOP
		// TODO ADD HERO
		
		triggerContainer = new PlannerContainerDef(definingLine, allowableContainers, allowableLines, listOfLists, PlannerValueDef.REFERS_TRIGGER - 1);
		containersByName.put("trigger", triggerContainer);
	}
	
	private void initUI()
	{
		jtp = new JTabbedPane();
		jtp.addTab("Triggers", new PlannerTab(containersByName, new String[] {"trigger"}, PlannerValueDef.REFERS_TRIGGER, this));		
		jtp.addTab("Cinematic", new PlannerTab(containersByName, new String[] {"cinematic"}, PlannerValueDef.REFERS_CINEMATIC, this));		
		jtp.addTab("Speech", new PlannerTab(containersByName, new String[] {"text"}, PlannerValueDef.REFERS_TEXT, this));		
		jtp.addTab("Heroes", new PlannerTab(containersByName, new String[] {"hero"}, PlannerValueDef.REFERS_HERO, this));
		jtp.addTab("Enemies", new PlannerTab(containersByName, new String[] {"enemy"}, PlannerValueDef.REFERS_ENEMY, this));
		jtp.addTab("Items", new PlannerTab(containersByName, new String[] {"item"}, PlannerValueDef.REFERS_ITEM, this));
		jtp.addTab("Quests", new PlannerTab(containersByName, new String[] {"quest"}, PlannerValueDef.REFERS_QUEST, this));
		jtp.addTab("Battle Functions", new PlannerFunctionPanel());
		jtp.addChangeListener(this);
		jtp.setEnabledAt(TAB_TRIGGER, false);
		jtp.setEnabledAt(TAB_CIN, false);
		jtp.setEnabledAt(TAB_TEXT, false);
		jtp.setSelectedIndex(TAB_HERO);
		this.setContentPane(jtp);
		
		this.setPreferredSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
	public static JButton createActionButton(String text, String action, ActionListener listener)
	{
		JButton button = new JButton(text);
		button.setActionCommand(action);
		button.addActionListener(listener);
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equalsIgnoreCase("new"))
		{
			JFileChooser fc = new JFileChooser(new File("."));
			int returnVal = fc.showSaveDialog(this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				if (triggerFile != null)
				{
					listOfLists.get(PlannerValueDef.REFERS_TRIGGER - 1).clear();
					listOfLists.get(PlannerValueDef.REFERS_TEXT - 1).clear();
					listOfLists.get(PlannerValueDef.REFERS_CINEMATIC - 1).clear();
					
					((PlannerTab) jtp.getComponent(TAB_TRIGGER)).clearValues(listOfLists);
					((PlannerTab) jtp.getComponent(TAB_CIN)).clearValues(listOfLists);
					((PlannerTab) jtp.getComponent(TAB_TEXT)).clearValues(listOfLists);
				}								
				
				triggerFile = fc.getSelectedFile();
				
				this.setTitle("Planner: " + triggerFile.getName());				
				
				try {
					triggerFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				jtp.setEnabledAt(TAB_TRIGGER, true);
				jtp.setEnabledAt(TAB_CIN, true);
				jtp.setEnabledAt(TAB_TEXT, true);
			}
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("open"))
		{
			JFileChooser fc = new JFileChooser(new File("."));
			int returnVal = fc.showOpenDialog(this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				triggerFile = fc.getSelectedFile();						
				
				if (triggerFile != null)
				{
					listOfLists.get(PlannerValueDef.REFERS_TRIGGER - 1).clear();
					listOfLists.get(PlannerValueDef.REFERS_TEXT - 1).clear();
					listOfLists.get(PlannerValueDef.REFERS_CINEMATIC - 1).clear();
					
					((PlannerTab) jtp.getComponent(TAB_TRIGGER)).clearValues(listOfLists);
					((PlannerTab) jtp.getComponent(TAB_CIN)).clearValues(listOfLists);
					((PlannerTab) jtp.getComponent(TAB_TEXT)).clearValues(listOfLists);
				}						
				
				this.setTitle("Planner: " + triggerFile.getName());
				
				try {
					parseContainer(XMLParser.process(Files.readAllLines(Paths.get(triggerFile.getAbsolutePath()), StandardCharsets.UTF_8)), TAB_TRIGGER, "trigger");
					parseContainer(XMLParser.process(Files.readAllLines(Paths.get(triggerFile.getAbsolutePath()), StandardCharsets.UTF_8)), TAB_TEXT, "text");
					parseContainer(XMLParser.process(Files.readAllLines(Paths.get(triggerFile.getAbsolutePath()), StandardCharsets.UTF_8)), TAB_CIN, "cinematic");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
				
				jtp.setEnabledAt(TAB_TRIGGER, true);
				jtp.setEnabledAt(TAB_CIN, true);
				jtp.setEnabledAt(TAB_TEXT, true);
			}
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("save"))
		{			
			for (int i = 0; i < jtp.getTabCount() - 1; i++)					
				((PlannerTab) jtp.getComponent(i)).setNewValues();
			
			saveTriggers();
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("saveall"))
		{			
			((PlannerTab) jtp.getComponent(TAB_ENEMY)).setNewValues();
			exportData(((PlannerTab) jtp.getComponent(TAB_ENEMY)).getListPC(), PATH_ENEMIES, false);
			
			((PlannerTab) jtp.getComponent(TAB_HERO)).setNewValues();
			exportData(((PlannerTab) jtp.getComponent(TAB_HERO)).getListPC(), PATH_HEROES, false);
			
			((PlannerTab) jtp.getComponent(TAB_ITEM)).setNewValues();
			exportData(((PlannerTab) jtp.getComponent(TAB_ITEM)).getListPC(), PATH_ITEMS, false);
			
			((PlannerTab) jtp.getComponent(TAB_QUEST)).setNewValues();
			exportData(((PlannerTab) jtp.getComponent(TAB_QUEST)).getListPC(), PATH_QUESTS, false);
			
			saveTriggers();
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("exit"))
		{
			System.exit(0);
		}
	}
	
	private void saveTriggers()
	{
		if (triggerFile != null)
		{
			System.out.println("SAVE");
			((PlannerTab) jtp.getComponent(TAB_TRIGGER)).setNewValues();
			exportData(((PlannerTab) jtp.getComponent(TAB_TRIGGER)).getListPC(), triggerFile.getAbsolutePath(), false);			
			
			((PlannerTab) jtp.getComponent(TAB_TEXT)).setNewValues();
			exportData(((PlannerTab) jtp.getComponent(TAB_TEXT)).getListPC(), triggerFile.getAbsolutePath(), true);
			
			((PlannerTab) jtp.getComponent(TAB_CIN)).setNewValues();
			exportData(((PlannerTab) jtp.getComponent(TAB_CIN)).getListPC(), triggerFile.getAbsolutePath(), true);
		}
	}
	
	public void removeReferences(int referenceType, int referenceIndex)
	{
		for (int i = 0; i < jtp.getTabCount() - 1; i++)		
		{
			for (PlannerContainer pcs : ((PlannerTab) jtp.getComponent(i)).getListPC())
			{
				for (PlannerLine pl : pcs.getLines())
				{
					for (int j = 0; j < pl.getPlDef().getPlannerValues().size(); j++)
					{
						PlannerValueDef pvd = pl.getPlDef().getPlannerValues().get(j);
						
						if (pvd.getRefersTo() == referenceType && pvd.getValueType() == PlannerValueDef.TYPE_INT)
						{
							if ((int) pl.getValues().get(j) == referenceIndex + 1)
								pl.getValues().set(j, 0);
							else if ((int) pl.getValues().get(j) > referenceIndex + 1)
							{
								pl.getValues().set(j, (int) pl.getValues().get(j) - 1);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		for (int i = 0; i < jtp.getTabCount() - 1; i++)					
			((PlannerTab) jtp.getComponent(i)).setNewValues();
	}
}
