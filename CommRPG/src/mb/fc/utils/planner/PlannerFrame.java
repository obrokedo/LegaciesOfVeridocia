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
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mb.fc.engine.CommRPG;
import mb.fc.engine.log.LoggingUtils;
import mb.fc.loading.MapParser;
import mb.fc.loading.PlannerMap;
import mb.fc.loading.PlannerTilesetParser;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;
import mb.fc.utils.planner.cinematic.CinematicCreatorPanel;
import mb.fc.utils.planner.mapedit.MapEditorPanel;

import org.newdawn.slick.SlickException;

import com.googlecode.jfilechooserbookmarks.DefaultBookmarksPanel;

public class PlannerFrame extends JFrame implements ActionListener,
		ChangeListener {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggingUtils.createLogger(PlannerFrame.class);

	private Hashtable<String, PlannerContainerDef> containersByName;
	private ArrayList<ArrayList<String>> listOfLists;
	private JTabbedPane jtp;
	private File triggerFile;
	private ArrayList<PlannerTab> plannerTabs = new ArrayList<PlannerTab>();
	private static String version = CommRPG.VERSION;
	private CinematicCreatorPanel cinematicMapPanel;
	private MapEditorPanel mapEditorPanel;
	private PlannerMap plannerMap;
	private JMenuItem exportMapItem;
	public static boolean SHOW_CIN = false;
	public static boolean SHOW_CIN_LOCATION = true;

	public static final int TAB_TRIGGER = 0;
	public static final int TAB_CIN = 1;
	public static final int TAB_TEXT = 2;
	public static final int TAB_CONDITIONS = 3;
	public static final int TAB_HERO = 4;
	public static final int TAB_ENEMY = 5;
	public static final int TAB_ITEM = 6;
	public static final int TAB_QUEST = 7;
	public static final int TAB_CIN_MAP = 8;
	public static final int TAB_EDIT_MAP = 9;

	private static String PATH_ENEMIES = "definitions/Enemies";
	private static String PATH_HEROES = "definitions/Heroes";
	private static String PATH_ITEMS = "definitions/Items";
	private static String PATH_QUESTS = "Quests";
	public static String PATH_MAPS = "map";

	public static void main(String args[]) {
		PlannerFrame pf = new PlannerFrame();
		pf.setVisible(true);
		pf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public PlannerFrame() {
		super("Planner: NO TRIGGERS LOADED " + version);

		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}


		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem newTriggersItem = new JMenuItem(
				"New Triggers/Speech/Cinematic");
		newTriggersItem.addActionListener(this);
		newTriggersItem.setActionCommand("new");
		fileMenu.add(newTriggersItem);
		JMenuItem openTriggersItem = new JMenuItem(
				"Open Triggers/Speech/Cinematic");
		openTriggersItem.addActionListener(this);
		openTriggersItem.setActionCommand("open");
		fileMenu.add(openTriggersItem);
		JMenuItem openMapItem = new JMenuItem(
				"Open Map");
		openMapItem.addActionListener(this);
		openMapItem.setActionCommand("openmap");
		fileMenu.add(openMapItem);
		exportMapItem = new JMenuItem(
				"Export Map");
		exportMapItem.addActionListener(this);
		exportMapItem.setActionCommand("exportmap");
		fileMenu.add(exportMapItem);
		exportMapItem.setEnabled(false);

		JMenuItem reloadTriggersItem = new JMenuItem(
				"Reload Triggers/Speech/Cinematic");
		reloadTriggersItem.addActionListener(this);
		reloadTriggersItem.setActionCommand("reload");
		fileMenu.add(reloadTriggersItem);

		JMenu optionsMenu = new JMenu("Options");
		JMenuItem showCinItem = new JMenuItem(
				"Show Cinematic Timeline");
		showCinItem.addActionListener(this);
		showCinItem.setActionCommand("showcin");
		optionsMenu.add(showCinItem);
		JMenuItem hideCinItem = new JMenuItem(
				"Hide Cinematic Timeline");
		hideCinItem.addActionListener(this);
		hideCinItem.setActionCommand("hidecin");
		optionsMenu.add(hideCinItem);
		JCheckBoxMenuItem showLocationItem = new JCheckBoxMenuItem( "Show Map Locations", true);
		showLocationItem.addActionListener(this);
		showLocationItem.setActionCommand("showloc");
		optionsMenu.add(showLocationItem);

		/*
		 * JMenuItem createTriggersItem = new
		 * JMenuItem("Create Triggers Based on Map");
		 * createTriggersItem.addActionListener(this);
		 * createTriggersItem.setActionCommand("generate");
		 * fileMenu.add(createTriggersItem);
		 */
		JMenuItem saveTriggers = new JMenuItem("Save Triggers/Speech/Cinematic");
		saveTriggers.addActionListener(this);
		saveTriggers.setActionCommand("save");
		fileMenu.add(saveTriggers);
		JMenuItem savePersistentFiles = new JMenuItem("Save All");
		savePersistentFiles.addActionListener(this);
		savePersistentFiles.setActionCommand("saveall");
		fileMenu.add(savePersistentFiles);
		JMenuItem exportEnemyCheatSheetFiles = new JMenuItem(
				"Export Enemy Cheat Sheet");
		exportEnemyCheatSheetFiles.addActionListener(this);
		exportEnemyCheatSheetFiles.setActionCommand("enemycheat");
		fileMenu.add(exportEnemyCheatSheetFiles);
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(this);
		exitItem.setActionCommand("exit");
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);
		menuBar.add(optionsMenu);

		this.setJMenuBar(menuBar);

		containersByName = new Hashtable<String, PlannerContainerDef>();

		listOfLists = new ArrayList<ArrayList<String>>();

		/********************/
		/* Set up referrers */
		/********************/
		PlannerDefinitions.setupRefererList(listOfLists);

		/*******************/
		/* Set up triggers */
		/*******************/
		PlannerDefinitions.setupDefintions(listOfLists, containersByName);

		initUI();

		getSavedData();

		stateChanged(null);
	}

	private boolean exportDataToFile(ArrayList<PlannerContainer> containers,
			String pathToFile, boolean append) {
		ArrayList<String> buffer = export(containers);

		Path path = Paths.get(pathToFile);
		try {
			if (append)
				Files.write(path, buffer, StandardCharsets.UTF_8,
						StandardOpenOption.APPEND);
			else
				Files.write(path, buffer, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while trying to save the data:"
					+ e.getMessage(), "Error saving data", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	public static ArrayList<String> export(ArrayList<PlannerContainer> containers)
	{
		ArrayList<String> buffer = new ArrayList<String>();
		for (int i = 0; i < containers.size(); i++) {
			PlannerContainer pc = containers.get(i);

			buffer.add(exportLine(pc.getPcdef().getDefiningLine(),
					pc.getDefLine(), i));

			for (PlannerLine pl : pc.getLines())
				buffer.add(exportLine(pl.getPlDef(), pl, -1));

			buffer.add("</" + pc.getPcdef().getDefiningLine().getTag() + ">");
		}
		return buffer;
	}

	private static String exportLine(PlannerLineDef pldef, PlannerLine pl, int id) {
		String stringBuffer = "";
		if (pl.isDefining())
			stringBuffer += "<" + pldef.getTag();
		else
			stringBuffer += "\t<" + pldef.getTag();

		if (id != -1)
			stringBuffer += " id=" + id;

		for (int i = 0; i < pldef.getPlannerValues().size(); i++) {
			PlannerValueDef pvd = pldef.getPlannerValues().get(i);
			stringBuffer += " " + pvd.getTag() + "=";
			if (pvd.getValueType() == PlannerValueDef.TYPE_BOOLEAN)
				stringBuffer += pl.getValues().get(i);
			else if (pvd.getValueType() == PlannerValueDef.TYPE_STRING ||
					pvd.getValueType() == PlannerValueDef.TYPE_LONG_STRING)
				stringBuffer += "\"" + pl.getValues().get(i) + "\"";
			else if (pvd.getValueType() == PlannerValueDef.TYPE_INT
					|| pvd.getValueType() == PlannerValueDef.TYPE_UNBOUNDED_INT) {
				if (pvd.getRefersTo() == PlannerValueDef.REFERS_NONE)
					stringBuffer += pl.getValues().get(i);
				else
					stringBuffer += (int) pl.getValues().get(i) - 1;
			} else if (pvd.getValueType() == PlannerValueDef.TYPE_MULTI_INT) {
				String[] oldVals = ((String) pl.getValues().get(i)).split(",");
				String newVals = "";

				for (int j = 0; j < oldVals.length; j++) {
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

	private void getSavedData() {
		try {
			/*
			 * jtp.addTab("Triggers", new PlannerTab(containersByName, new
			 * String[] {"trigger"})); jtp.addTab("Cinematic", new
			 * PlannerTab(containersByName, new String[] {"cinematic"}));
			 * jtp.addTab("Speech", new PlannerTab(containersByName, new
			 * String[] {"text"})); jtp.addTab("Heroes", new
			 * PlannerTab(containersByName, new String[] {"hero"}));
			 * jtp.addTab("Enemies", new PlannerTab(containersByName, new
			 * String[] {"enemy"})); jtp.addTab("Items", new
			 * PlannerTab(containersByName, new String[] {"item"}));
			 * jtp.addTab("Quests", new PlannerTab(containersByName, new
			 * String[] {"quest"}));
			 */

			parseContainer(
					XMLParser.process(Files.readAllLines(
							Paths.get(PATH_ENEMIES), StandardCharsets.UTF_8)),
					TAB_ENEMY, "enemy");
			parseContainer(XMLParser.process(Files.readAllLines(
					Paths.get(PATH_HEROES), StandardCharsets.UTF_8)), TAB_HERO,
					"hero");
			parseContainer(XMLParser.process(Files.readAllLines(
					Paths.get(PATH_ITEMS), StandardCharsets.UTF_8)), TAB_ITEM,
					"item");
			parseContainer(XMLParser.process(Files.readAllLines(
					Paths.get(PATH_QUESTS), StandardCharsets.UTF_8)),
					TAB_QUEST, "quest");
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred parsing the saved data, undo any changes made manually and try again:"
					+ e.getMessage(), "Error parsing data", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void parseContainer(ArrayList<TagArea> tas, int tabIndex,
			String allowableValue) {
		for (TagArea ta : tas) {
			if (!ta.getTagType().equalsIgnoreCase(allowableValue))
				continue;
			LOGGER.fine(ta.getTagType());
			PlannerContainerDef pcd = containersByName.get(ta.getTagType());
			PlannerContainer plannerContainer = new PlannerContainer(pcd, plannerTabs.get(tabIndex));
			PlannerLine plannerLine = plannerContainer.getDefLine();
			parseLine(plannerLine, pcd.getDefiningLine(), ta);
			pcd.getDataLines().add(plannerContainer.getDescription());

			plannerTabs.get(tabIndex).addPlannerContainer(plannerContainer);

			for (TagArea taChild : ta.getChildren()) {
				boolean found = false;
				for (PlannerLineDef allowable : pcd.getAllowableLines()) {
					if (taChild.getTagType().equalsIgnoreCase(
							allowable.getTag())) {
						found = true;
						PlannerLine childLine = new PlannerLine(allowable,
								false);
						parseLine(childLine, allowable, taChild);
						plannerContainer.addLine(childLine);
					}
				}

				if (!found)
					LOGGER.warning("Unable to find tag definition for "
							+ taChild.getTagType());
			}
		}

		plannerTabs.get(tabIndex).updateAttributeList(-1);
	}

	private void parseLine(PlannerLine plannerLine, PlannerLineDef pld,
			TagArea ta) {
		LOGGER.fine("PARENT: " + pld.getTag());
		for (PlannerValueDef pvd : pld.getPlannerValues()) {
			if (pvd.getValueType() == PlannerValueDef.TYPE_STRING || pvd.getValueType() == PlannerValueDef.TYPE_LONG_STRING)
				plannerLine.getValues().add(ta.getAttribute(pvd.getTag()));
			else if (pvd.getValueType() == PlannerValueDef.TYPE_INT
					|| pvd.getValueType() == PlannerValueDef.TYPE_UNBOUNDED_INT) {
				if (pvd.getRefersTo() == PlannerValueDef.REFERS_NONE) {
					LOGGER.fine("TAG: " + pvd.getTag() + " "
							+ ta.getAttribute(pvd.getTag()));
					int value = 0;
					try {
						value = Integer.parseInt(ta.getAttribute(
								pvd.getTag()));
					} catch (NumberFormatException ex) {
					}

					plannerLine.getValues().add(value);
				}
				else
				{
					if (ta.getAttribute(pvd.getTag()) != null)
						plannerLine.getValues().add(Integer.parseInt(ta.getAttribute(pvd.getTag())) + 1);
					else
						plannerLine.getValues().add(0);
				}
			} else if (pvd.getValueType() == PlannerValueDef.TYPE_MULTI_INT) {
				String newVals = "";

				if (ta.getAttribute(pvd.getTag()) != null) {
					String[] values = ta.getAttribute(pvd.getTag())
							.split(",");
					for (int j = 0; j < values.length; j++) {
						newVals = newVals + (Integer.parseInt(values[j]) + 1);
						if (j + 1 != values.length)
							newVals = newVals + ",";
					}
				} else
					newVals = "0";

				plannerLine.getValues().add(newVals);
			} else if (pvd.getValueType() == PlannerValueDef.TYPE_BOOLEAN)
				plannerLine.getValues().add(
						Boolean.parseBoolean(ta.getAttribute(pvd.getTag())));

		}
	}

	private void initUI() {
		jtp = new JTabbedPane();

		// Add triggers
		PlannerTab tempPlannerTab = new PlannerTab("Triggers", containersByName,
				new String[] { "trigger" }, PlannerValueDef.REFERS_TRIGGER, this);
		plannerTabs.add(tempPlannerTab);
		jtp.addTab("Trigger Actions", tempPlannerTab.getUiAspect());

		// Add cinematics
		tempPlannerTab = new PlannerTab("Cinematics", containersByName,
				new String[] { "cinematic" }, PlannerValueDef.REFERS_CINEMATIC,
				this);
		plannerTabs.add(tempPlannerTab);
		jtp.addTab("Cinematic", tempPlannerTab.getUiAspect());

		// Add speech
		tempPlannerTab = new PlannerTab("Speeches", containersByName,
				new String[] { "text" }, PlannerValueDef.REFERS_TEXT, this);
		plannerTabs.add(tempPlannerTab);
		jtp.addTab("Speech", tempPlannerTab.getUiAspect());

		// Add trigger events
		tempPlannerTab = new PlannerTab("Conditions", containersByName,
				new String[] { "condition" }, PlannerValueDef.REFERS_CONDITIONS, this);
		plannerTabs.add(tempPlannerTab);
		jtp.addTab("Condition", tempPlannerTab.getUiAspect());
		
		// Add heroes
		tempPlannerTab = new PlannerTab("Heroes", containersByName,
				new String[] { "hero" }, PlannerValueDef.REFERS_HERO, this);
		plannerTabs.add(tempPlannerTab);
		jtp.addTab("Heroes", tempPlannerTab.getUiAspect());

		// Add enemies
		tempPlannerTab = new PlannerTab("Enemies", containersByName,
				new String[] { "enemy" }, PlannerValueDef.REFERS_ENEMY, this);
		plannerTabs.add(tempPlannerTab);
		jtp.addTab("Enemies", tempPlannerTab.getUiAspect());

		// Add items
		tempPlannerTab = new PlannerTab("Items", containersByName,
				new String[] { "item" }, PlannerValueDef.REFERS_ITEM, this);
		plannerTabs.add(tempPlannerTab);
		jtp.addTab("Items", tempPlannerTab.getUiAspect());

		// Add quests
		tempPlannerTab = new PlannerTab("Quests", containersByName,
				new String[] { "quest" }, PlannerValueDef.REFERS_QUEST, this);
		plannerTabs.add(tempPlannerTab);
		jtp.addTab("Quests", tempPlannerTab.getUiAspect());

		// Add maps
		cinematicMapPanel = new CinematicCreatorPanel(this);
		jtp.addTab("Cinematic Creator", cinematicMapPanel.getUiAspect());
		mapEditorPanel = new MapEditorPanel(this, listOfLists);
		jtp.addTab("Map Editor", mapEditorPanel.getUIAspect());
		// jtp.addTab("Battle Functions", new PlannerFunctionPanel());
		// jtp.addTab("Map Triggers", new MapTriggerPanel(containersByName));
		jtp.addChangeListener(this);
		jtp.setEnabledAt(TAB_TRIGGER, false);
		jtp.setEnabledAt(TAB_CIN, false);
		jtp.setEnabledAt(TAB_TEXT, false);
		jtp.setEnabledAt(TAB_CONDITIONS, false);
		jtp.setEnabledAt(TAB_CIN_MAP, false);
		jtp.setEnabledAt(TAB_EDIT_MAP, false);
		jtp.setSelectedIndex(TAB_HERO);
		this.setContentPane(jtp);

		this.setPreferredSize(new Dimension(900, 600));
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.pack();
		this.setVisible(false);
	}

	public static JButton createActionButton(String text, String action,
			ActionListener listener) {
		JButton button = new JButton(text);
		button.setActionCommand(action);
		button.addActionListener(listener);
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equalsIgnoreCase("new")) {
			JFileChooser fc = createFileChooser();
			int returnVal = fc.showSaveDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if (triggerFile != null) {
					listOfLists.get(PlannerValueDef.REFERS_TRIGGER - 1).clear();
					listOfLists.get(PlannerValueDef.REFERS_TEXT - 1).clear();
					listOfLists.get(PlannerValueDef.REFERS_CINEMATIC - 1)
							.clear();

					plannerTabs.get(TAB_TRIGGER).clearValues();
					plannerTabs.get(TAB_CIN).clearValues();
					plannerTabs.get(TAB_TEXT).clearValues();
					plannerTabs.get(TAB_CONDITIONS).clearValues();
				}

				triggerFile = fc.getSelectedFile();

				this.setTitle("Planner: " + triggerFile.getName() + " " + version);

				try {
					triggerFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "An error occurred while trying to save the trigger file:"
							+ e.getMessage(), "Error saving trigger file", JOptionPane.ERROR_MESSAGE);
					return;
				}

				jtp.setEnabledAt(TAB_TRIGGER, true);
				jtp.setEnabledAt(TAB_CIN, true);
				jtp.setEnabledAt(TAB_TEXT, true);
				jtp.setEnabledAt(TAB_CONDITIONS, true);
			}
		} else if (arg0.getActionCommand().equalsIgnoreCase("open")) {
			JFileChooser fc = createFileChooser();
			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				triggerFile = fc.getSelectedFile();

				openFile(triggerFile);
			}
		} else if (arg0.getActionCommand().equalsIgnoreCase("openmap")) {
			JFileChooser fc = createFileChooser();
			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if (triggerFile == null)
					JOptionPane.showMessageDialog(this, "You have opened a map file without first opening a trigger file.\n"
						+ "Keep in mind that map locations of the type 'trigger' and 'battletrigger' require\n"
						+ "the correct trigger file open so that they may be viewed correctly. As such triggers\n"
						+ "and battletriggers in the map view may not work correctly until the appropriate text file is loaded.");

				plannerMap = new PlannerMap();

				try {
					MapParser.parseMap(fc.getSelectedFile().getAbsolutePath(), plannerMap, new PlannerTilesetParser(), null);
				} catch (IOException | SlickException e) {
					JOptionPane.showMessageDialog(this, "An error occurred while loading the selected map: " + e.getMessage());
					e.printStackTrace();
					return;
				}

				exportMapItem.setEnabled(true);

				mapEditorPanel.loadMap(plannerMap, fc.getSelectedFile().getName());
				cinematicMapPanel.loadMap(plannerMap);
				jtp.setEnabledAt(TAB_CIN_MAP, true);
				jtp.setEnabledAt(TAB_EDIT_MAP, true);
				jtp.setSelectedIndex(TAB_EDIT_MAP);
			}
		} else if (arg0.getActionCommand().equalsIgnoreCase("reload")) {
			if (triggerFile != null)
				openFile(triggerFile);
		} else if (arg0.getActionCommand().equalsIgnoreCase("save")) {
			for (int i = 0; i < jtp.getTabCount() - 2; i++)
				plannerTabs.get(i).setNewValues();

			saveTriggers(true);
		} else if (arg0.getActionCommand().equalsIgnoreCase("saveall")) {
			plannerTabs.get(TAB_ENEMY).setNewValues();
			boolean success = true;
			if (!exportDataToFile(plannerTabs.get(TAB_ENEMY).getListPC(),
					PATH_ENEMIES, false))
				success = false;

			plannerTabs.get(TAB_HERO).setNewValues();
			if (!exportDataToFile(plannerTabs.get(TAB_HERO).getListPC(),
					PATH_HEROES, false))
				success = false;

			plannerTabs.get(TAB_ITEM).setNewValues();
			if (!exportDataToFile(plannerTabs.get(TAB_ITEM).getListPC(),
					PATH_ITEMS, false))
				success = false;

			plannerTabs.get(TAB_QUEST).setNewValues();
			if (!exportDataToFile(plannerTabs.get(TAB_QUEST).getListPC(),
					PATH_QUESTS, false))
				success = false;

			saveTriggers(success);
		} else if (arg0.getActionCommand().equalsIgnoreCase("exit")) {
			System.exit(0);
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("showcin")) {
			SHOW_CIN = true;
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("hidecin")) {
			SHOW_CIN = false;
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("showloc")) {
			SHOW_CIN_LOCATION = !SHOW_CIN_LOCATION;
			((JCheckBoxMenuItem) arg0.getSource()).setSelected(SHOW_CIN_LOCATION);
			this.repaint();
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("exportmap"))
		{
			JFileChooser fc = createFileChooser();
			int returnVal = fc.showSaveDialog(this);
			File newMapFile = null;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				newMapFile = fc.getSelectedFile();

				Path path = Paths.get(newMapFile.getAbsolutePath());
				try {
					Files.write(path, plannerMap.outputNewMap().getBytes());
					JOptionPane.showMessageDialog(this, "The map was exported successfully");
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "An error occurred while trying to save the map:"
							+ e.getMessage(), "Error saving map", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		}
	}

	private void openFile(File triggerFile) {
		listOfLists.get(PlannerValueDef.REFERS_TRIGGER - 1).clear();
		listOfLists.get(PlannerValueDef.REFERS_TEXT - 1).clear();
		listOfLists.get(PlannerValueDef.REFERS_CINEMATIC - 1).clear();

		plannerTabs.get(TAB_TRIGGER).clearValues();
		plannerTabs.get(TAB_CIN).clearValues();
		plannerTabs.get(TAB_TEXT).clearValues();
		plannerTabs.get(TAB_CONDITIONS).clearValues();
		
		plannerTabs.get(TAB_TRIGGER).updateAttributeList(-1);
		plannerTabs.get(TAB_CIN).updateAttributeList(-1);
		plannerTabs.get(TAB_TEXT).updateAttributeList(-1);
		plannerTabs.get(TAB_CONDITIONS).updateAttributeList(-1);

		this.setTitle("Planner: " + triggerFile.getName() + " " + version);

		try {
			parseContainer(XMLParser.process(Files.readAllLines(
					Paths.get(triggerFile.getAbsolutePath()),
					StandardCharsets.UTF_8)), TAB_TRIGGER, "trigger");
			parseContainer(XMLParser.process(Files.readAllLines(
					Paths.get(triggerFile.getAbsolutePath()),
					StandardCharsets.UTF_8)), TAB_TEXT, "text");
			parseContainer(XMLParser.process(Files.readAllLines(
					Paths.get(triggerFile.getAbsolutePath()),
					StandardCharsets.UTF_8)), TAB_CIN, "cinematic");
			parseContainer(XMLParser.process(Files.readAllLines(
					Paths.get(triggerFile.getAbsolutePath()),
					StandardCharsets.UTF_8)), TAB_CONDITIONS, "condition");
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while trying to open the file:"
					+ e.getMessage(), "Error opening file", JOptionPane.ERROR_MESSAGE);
			return;
		}


		jtp.setEnabledAt(TAB_TRIGGER, true);
		jtp.setEnabledAt(TAB_CIN, true);
		jtp.setEnabledAt(TAB_TEXT, true);
		jtp.setEnabledAt(TAB_CONDITIONS, true);
	}

	private void saveTriggers(boolean success) {
		if (triggerFile != null) {
			LOGGER.fine("SAVE");
			plannerTabs.get(TAB_TRIGGER).setNewValues();
			if (!exportDataToFile(plannerTabs.get(TAB_TRIGGER).getListPC(),
					triggerFile.getAbsolutePath(), false))
				success = false;

			plannerTabs.get(TAB_TEXT).setNewValues();
			if (!exportDataToFile(plannerTabs.get(TAB_TEXT).getListPC(),
					triggerFile.getAbsolutePath(), true))
				success = false;

			plannerTabs.get(TAB_CIN).setNewValues();
			if (!exportDataToFile(plannerTabs.get(TAB_CIN).getListPC(),
					triggerFile.getAbsolutePath(), true))
				success = false;
			
			plannerTabs.get(TAB_CONDITIONS).setNewValues();
			if (!exportDataToFile(plannerTabs.get(TAB_CONDITIONS).getListPC(),
					triggerFile.getAbsolutePath(), true))
				success = false;
		}

		if (success) {
			JOptionPane.showMessageDialog(this, "The file was saved successfully");
		}
	}

	public void removeReferences(int referenceType, int referenceIndex) {
		for (int i = 0; i < jtp.getTabCount() - 2; i++) {
			for (PlannerContainer pcs : plannerTabs.get(i)
					.getListPC()) {
				for (PlannerLine pl : pcs.getLines()) {
					for (int j = 0; j < pl.getPlDef().getPlannerValues().size(); j++) {
						PlannerValueDef pvd = pl.getPlDef().getPlannerValues()
								.get(j);

						if (pvd.getRefersTo() == referenceType
								&& pvd.getValueType() == PlannerValueDef.TYPE_INT ||
									pvd.getValueType() == PlannerValueDef.TYPE_UNBOUNDED_INT) {
							if ((int) pl.getValues().get(j) == referenceIndex + 1)
								pl.getValues().set(j, 0);
							else if ((int) pl.getValues().get(j) > referenceIndex + 1) {
								pl.getValues().set(j,
										(int) pl.getValues().get(j) - 1);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		for (int i = 0; i < jtp.getTabCount() - 2; i++)
			plannerTabs.get(i).commitChanges();

		if (jtp.getSelectedIndex() == TAB_CIN_MAP)
		{
			cinematicMapPanel.reloadCinematicItem();
		}
	}

	public PlannerTab getPlannerTabAtIndex(int index)
	{
		return plannerTabs.get(index);
	}

	public void setSelectedTabIndex(int index)
	{
		jtp.setSelectedIndex(index);
	}

	public PlannerContainerDef getContainerDefByName(String name)
	{
		return this.containersByName.get(name);
	}

	public static JFileChooser createFileChooser()
	{
		JFileChooser jfc = new JFileChooser();
		DefaultBookmarksPanel panel = new DefaultBookmarksPanel();
		panel.setOwner(jfc);
		jfc.setAccessory(panel);
		jfc.setPreferredSize(new Dimension(800, 600));
		return jfc;
	}
}
