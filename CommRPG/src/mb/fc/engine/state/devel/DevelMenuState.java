package mb.fc.engine.state.devel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import mb.fc.engine.CommRPG;
import mb.fc.engine.load.BulkLoader;
import mb.fc.engine.state.MenuState;
import mb.fc.game.dev.DevParams;
import mb.fc.game.exception.BadResourceException;
import mb.fc.game.resource.SpellResource;
import mb.fc.game.ui.Button;
import mb.fc.game.ui.ListUI;
import mb.fc.game.ui.ListUI.ResourceSelectorListener;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.game.ui.ResourceSelector;
import mb.fc.loading.FCLoadingRenderSystem;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.loading.LoadingState;
import mb.fc.loading.MapParser;
import mb.fc.loading.TilesetParser;
import mb.fc.map.Map;
import mb.fc.map.MapObject;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;
import mb.fc.utils.gif.GifFrame;
import mb.fc.utils.planner.PlannerFrame;
import mb.fc.utils.progression.ProgressionFrame;

/**
 * Development menu state that allows loading maps programatically
 *
 * @author Broked
 *
 */
public class DevelMenuState extends MenuState implements ResourceSelectorListener
{
	private ResourceSelector textSelector;
	private ListUI entranceSelector;
	private PlannerFrame plannerFrame = new PlannerFrame(this);
	private GifFrame quickAnimate = new GifFrame(true);
	private ProgressionFrame progressionFrame = new ProgressionFrame();
	public static ParticleSystem ps;
	private String currentMap;
	private Button loadTownButton = new Button(0, 550, 150, 25, "Load Town");
	private Button loadCinButton = new Button(0, 580, 150, 25, "Load Cin");
	private Button loadBattleButton = new Button(0, 610, 150, 25, "Load Battle");
	
	protected int totalResources = 0;
	private FCResourceManager mainGameFCRM = null;
	protected BulkLoader mainGameBulkLoader = null;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.game = game;
		this.gc = container;
		// gameSetup(game, gc);
		// mapSelector = new ResourceSelector("Select Map", 0, true, "map", ".tmx", container);
		// mapSelector.setListener(this);
		textSelector = new ResourceSelector("Select Text", 0, true, "mapdata", "", container);
		textSelector.setListener(this);
		textSelector.setIgnoreClicksInUpdate(true);
		loadTownButton.setEnabled(false);
		loadBattleButton.setEnabled(false);
		loadCinButton.setEnabled(false);
		
		// Checks to see if any mapdata has errors
		/*
		for (File f : new File("mapdata").listFiles()) {
			if (f.isFile()) {
				System.out.println("Checking file: " + f.getName());
				plannerFrame.openFile(f, false);
				if (!plannerFrame.hasPlannerMap())
					System.out.println("Has no planner map assigned");
				PlannerReference.getBadReferences(plannerFrame.getDataInputTabs()).forEach(s -> System.out.println(s));
			}
		}*/
		/*
		for (String spell : GlobalPythonFactory.createJSpell().getSpellList()) {
			JSpell jspell = GlobalPythonFactory.createJSpell().init(spell);
			System.out.println(spell);
			System.out.println(jspell.getId());
			System.out.println(jspell.getMaxLevel());
			System.out.println(jspell.getName());
			for (int i = 0; i < jspell.getMaxLevel(); i++) {
				System.out.println(jspell.getSpellAnimationFile(i));
				System.out.println(jspell.getSpellRainAnimationFile(i));
				System.out.println(jspell.getSpellRainAnimationName(i));
				System.out.println(jspell.getSpellRainFrequency(i));
				System.out.println(jspell.getSpellOverlayColor(i));
				System.out.println(jspell.getArea()[i]);
				System.out.println(jspell.getCosts()[i]);
				System.out.println(jspell.getDamage()[i]);
				System.out.println(jspell.getMpDamage()[i]);
				System.out.println(jspell.getRange()[i]);
			}
			System.out.println(jspell.getSpellIconId());
			System.out.println(jspell.isLoops());
			System.out.println(jspell.isTargetsEnemy());
		}
		*/
		
		
		//ps = new ParticleSystem(new Image("image/RainBig.png"));
		//RainEmitter rainEmitter = new RainEmitter(500, 100, true);
		//ps.addEmitter(rainEmitter);
	}
	
	

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		Log.debug("Entered DevelMenuState");
		gameSetup(game, container);
		SpellResource.initSpells(null);
		this.progressionFrame.init();
		initializeBulkLoader();
	}

	protected void initializeBulkLoader() {
		if (mainGameBulkLoader == null) {
			mainGameFCRM = new FCResourceManager();
			mainGameBulkLoader = new BulkLoader(mainGameFCRM);
			
			try {
				mainGameBulkLoader.start("/loader/Default");
			} catch (IOException e) {
				throw new BadResourceException("No default resource loader could be found at: ./loader/Default");
			}
			
			totalResources = mainGameBulkLoader.getResourceAmount();
		}
	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException
	{
		g.clearClip();
		g.setColor(Color.red);
		g.drawString("DEVELOPMENT MODE", 5, 5);

		g.setColor(Color.white);
		
		
		g.fillRect(container.getWidth() / 2, container.getHeight() - 60, 
				(float) (170 * (totalResources - mainGameBulkLoader.getResourceAmount()) / totalResources), 15) ;

		g.setColor(Color.blue);
		g.drawRect(container.getWidth() / 2, container.getHeight() - 60, 
				170, 15);
		g.setColor(Color.white);
		
		textSelector.render(g);

		if (entranceSelector != null)
			entranceSelector.render(g);

		loadTownButton.render(g);
		loadBattleButton.render(g);
		loadCinButton.render(g);

		g.drawString(version, container.getWidth() / 2, container.getHeight() - 30);
		
		int amount = 7;
		g.drawString("F1 - Toggle Main/Dev Menu", container.getWidth() - 250, container.getHeight() - amount-- * 30);
		g.drawString("F2 - Open Planner", container.getWidth() - 250, container.getHeight() - amount-- * 30);
		g.drawString("F3 - Open Quick Animator", container.getWidth() - 250, container.getHeight() - amount-- * 30);
		g.drawString("F4 - Open Animation Viewer", container.getWidth() - 250, container.getHeight() - amount-- * 30);
		// g.drawString("F5 - Run Test", container.getWidth() - 250, container.getHeight() - amount-- * 30);
		g.drawString("F6 - Open Battle Viewer", container.getWidth() - 250, container.getHeight() - amount-- * 30);
		g.drawString("F8 - Load Saved Game", container.getWidth() - 250, container.getHeight() - amount-- * 30);
		g.drawString("F10 - Open Progression Viewer", container.getWidth() - 250, container.getHeight() - amount-- * 30);
		

		if (initialized && ps != null)
		{
			g.scale(3, 3);
			ps.render();
			g.resetTransform();
		}
		
		if (initialized) {
			
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException
	{
		this.isPaused(container);
		
		
		
		int x = container.getInput().getMouseX();
		int y = container.getInput().getMouseY();

		loadTownButton.handleUserInput(x, y, false);
		loadCinButton.handleUserInput(x, y, false);
		loadBattleButton.handleUserInput(x, y, false);
		
		if (updateDelta > 0)
			updateDelta = Math.max(0, updateDelta - delta);
		
		textSelector.update(container, delta);

		if (entranceSelector != null) {
			entranceSelector.update(container, delta);
			loadTownButton.setEnabled(entranceSelector.getSelectedResource() != null);
			loadBattleButton.setEnabled(entranceSelector.getSelectedResource() != null);
		}

		if (initialized && ps != null)
		{
			ps.update(delta);
		}
		
		if (initialized)
			mainGameBulkLoader.update();
	}
	
	@Override
	public void keyPressed(int key, char c) {
		if (updateDelta <= 0) {
			updateDelta += 50;
			if (key == Input.KEY_F1)
			{
				((LoadingState) game.getState(CommRPG.STATE_GAME_LOADING)).setLoadingInfo("/menu/MainMenu", false, true,
						new FCResourceManager(),
							(LoadableGameState) game.getState(CommRPG.STATE_GAME_MENU),
								new FCLoadingRenderSystem(this.gc));
	
				game.enterState(CommRPG.STATE_GAME_LOADING);
			}
	
			if (key == Input.KEY_F2 || key == Input.KEY_P)
			{
				if (!plannerFrame.isVisible())
					plannerFrame.setVisible(true);
			}
	
			if (key == Input.KEY_F3)
			{
				if (!quickAnimate.isVisible())
					quickAnimate.setVisible(true);
			}
	
			if (key == Input.KEY_F4)
			{
				game.enterState(CommRPG.STATE_GAME_ANIM_VIEW);
			}
	
			/*
			if (container.getInput().isKeyPressed(Input.KEY_F5))
			{
				CommRPG.TEST_MODE_ENABLED = true;
				this.gameSetup(game, container);
				start(LoadTypeEnum.CINEMATIC, "neweriumcastle", null);
			}
			*/
			
			if (key == Input.KEY_F6)
			{
				((LoadingState) game.getState(CommRPG.STATE_GAME_LOADING)).setLoadingInfo("eriumjail", true, true,
						new FCResourceManager(),
							(LoadableGameState) game.getState(CommRPG.STATE_GAME_BATTLE_ANIM_VIEW),
								new FCLoadingRenderSystem(gc));
	
				game.enterState(CommRPG.STATE_GAME_LOADING);
			}
	
			if (key == Input.KEY_F7)
			{
				try {
					((CommRPG) this.game).toggleFullScreen();
				} catch (SlickException e) {
					Log.error("Unable to toggle fullscreen mode: " + e.getMessage());
					JOptionPane.showMessageDialog(null, "Unable to toggle fullscreen mode: " + e.getMessage());
				}
				updateDelta = 200;
				System.out.println("TOGGLE");
			}
			
			if (key == Input.KEY_F8)
			{
				CommRPG.TEST_MODE_ENABLED = false;
				LoadTypeEnum loadType = LoadTypeEnum.TOWN;
				if (persistentStateInfo.getClientProgress().isBattle())
					loadType = LoadTypeEnum.BATTLE;
				start(loadType, persistentStateInfo.getClientProgress().getMapData(), null);
			}
			
			if (key == Input.KEY_F9)
			{
				CommRPG.TEST_MODE_ENABLED = true;
				CommRPG.BATTLE_MODE_OPTIMIZE = true;
				if (textSelector.getSelectedResource() != null && 
						entranceSelector.getSelectedResource() != null)
					start(LoadTypeEnum.BATTLE, textSelector.getSelectedResource(), 
							entranceSelector.getSelectedResource());
			}
			if (key == Input.KEY_F10)
			{
				progressionFrame.setVisible(true);
			}
		}
	}



	@Override
	public void mousePressed(int button, int x, int y) {
		if (updateDelta <= 0) {
			if (button == Input.MOUSE_LEFT_BUTTON)
			{
				this.updateDelta = 200;
				
				if (loadTownButton.handleUserInput(x, y, true)) {
					// This whole line of logic is somewhat terrifying... We set the resource manager
					// of the psi to the one that the bulkloader is using it is NOT set in the
					// state info at this point. It will be set in the state info and PSI (again)
					// once the town/cin/battle state loads. What's more concerning is that we manually
					// set the loading states bulk loader here and it we will use the same bulk loader
					// for the rest of the game after we get past the menu state. Ideally it would be nice
					// to pass the bulkloader along on these load* calls (below), but there currently isn't
					// a use case for that now
					persistentStateInfo.setResourceManager(mainGameFCRM);
					((LoadingState) game.getState(CommRPG.STATE_GAME_LOADING)).setBulkLoader(mainGameBulkLoader);
					
					start(LoadTypeEnum.TOWN, textSelector.getSelectedResource(), entranceSelector.getSelectedResource());
				}
				if (loadCinButton.handleUserInput(x, y, true)) {
					String id = JOptionPane.showInputDialog("Enter the cinematic id (a number) to run");
					try {
						int iId = Integer.parseInt(id);
						
						// This whole line of logic is somewhat terrifying... We set the resource manager
						// of the psi to the one that the bulkloader is using it is NOT set in the
						// state info at this point. It will be set in the state info and PSI (again)
						// once the town/cin/battle state loads. What's more concerning is that we manually
						// set the loading states bulk loader here and it we will use the same bulk loader
						// for the rest of the game after we get past the menu state. Ideally it would be nice
						// to pass the bulkloader along on these load* calls (below), but there currently isn't
						// a use case for that now
						persistentStateInfo.setResourceManager(mainGameFCRM);
						((LoadingState) game.getState(CommRPG.STATE_GAME_LOADING)).setBulkLoader(mainGameBulkLoader);
						
						startCinematic(textSelector.getSelectedResource(), iId);
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(null, "The value must be a number");
					}
					
				}
				if (loadBattleButton.handleUserInput(x, y, true)) {
					if (new File(textSelector.getSelectedResource()).exists()) {
						if (JOptionPane.showConfirmDialog(null, 
								"A battle configuration was found for this battle, would you like to apply it?", 
								"Load Configuration", JOptionPane.YES_NO_OPTION)
								== JOptionPane.YES_OPTION) {
							DevParams devParams = DevParams.parseDevParams(textSelector.getSelectedResource());
							if (devParams != null)
								persistentStateInfo.getClientProfile().setDevParams(devParams);
						}
					}
					
					// This whole line of logic is somewhat terrifying... We set the resource manager
					// of the psi to the one that the bulkloader is using it is NOT set in the
					// state info at this point. It will be set in the state info and PSI (again)
					// once the town/cin/battle state loads. What's more concerning is that we manually
					// set the loading states bulk loader here and it we will use the same bulk loader
					// for the rest of the game after we get past the menu state. Ideally it would be nice
					// to pass the bulkloader along on these load* calls (below), but there currently isn't
					// a use case for that now
					persistentStateInfo.setResourceManager(mainGameFCRM);
					((LoadingState) game.getState(CommRPG.STATE_GAME_LOADING)).setBulkLoader(mainGameBulkLoader);
					start(LoadTypeEnum.BATTLE, textSelector.getSelectedResource(), entranceSelector.getSelectedResource());
				}
				
				this.textSelector.handleInput(x, y, true);
				if (entranceSelector != null)
					entranceSelector.handleInput(x, y, true);
			}
		}
	}


	@Override
	public void stateLoaded(FCResourceManager resourceManager) {
		this.initialized = true;
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_MENU_DEVEL;
	}

	@Override
	public boolean resourceSelected(String selectedItem,
			ListUI parentSelector) {
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File("mapdata/" + selectedItem)));
			String firstLine = br.readLine();
			br.close();
			
			if (firstLine.startsWith("<map")) {
				ArrayList<TagArea> tagArea = XMLParser.process(Collections.singletonList(firstLine));
				currentMap = tagArea.get(0).getAttribute("file");
			} else {
				JOptionPane.showMessageDialog(null, "The selected map data has not had a map associated with it yet.\n Load the mapdata in the planner to assign a map");
				return false;
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "The selected file could not be found");
			return false;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "The selected file could not be read or is improperly formatted: " + e.getMessage());
			return false;
		}
		
		
		Map map = new Map();
		ArrayList<String> entrances = new ArrayList<>();
		try {
			MapParser.parseMap("map/" + currentMap, map, new TilesetParser(), null);


			for (MapObject mo : map.getMapObjects())
				if (mo.getKey().equalsIgnoreCase("start"))
					entrances.add(mo.getParam("exit"));

			entranceSelector = new ListUI("Select Entrance", (((PaddedGameContainer) gc).getPaddedWidth() - 150) / 2, entrances);
			entranceSelector.setIgnoreClicksInUpdate(true);
			loadCinButton.setEnabled(true);
			return true;

		} catch (Throwable t) {
			t.printStackTrace();
			JOptionPane.showMessageDialog(null, "The selected map " + currentMap + " contains errors and may not be loaded: " + t.getMessage());
			entranceSelector = null;
		}
		
		loadCinButton.setEnabled(false);
		return false;
	}
}
