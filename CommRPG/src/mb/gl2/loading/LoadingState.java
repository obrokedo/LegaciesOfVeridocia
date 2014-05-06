package mb.gl2.loading;

import java.util.ArrayList;
import java.util.List;

import mb.fc.game.hudmenu.Panel;
import mb.fc.game.resource.SpellResource;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadingComp;
import mb.jython.GlobalPythonFactory;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;

import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;

public class LoadingState extends BasicGameState
{
	public static boolean loading = false;
	private World world;
	private String mapName;
	private String textName;
	private LoadableGameState nextState;
	private EntitySystem loadingRenderer;
	private ResourceManager resourceManager;
	private boolean loadResources;
	private Entity loadingEntity;
	private int stateId;
	private int loadIndex;
	private List<String> allLines;
	private int loadAmount;
	private boolean loadingMap;
	private String errorMessage = null;

	public static final boolean inJar = true;

	public LoadingState(int stateId)
	{
		this.stateId = stateId;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException
	{

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		loadingRenderer.process();
		if (errorMessage != null)
		{
			g.setColor(Color.white);
			int strWidth = container.getDefaultFont().getWidth(errorMessage);
			g.drawString(errorMessage, (container.getWidth() - strWidth) / 2, container.getHeight() / 2);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException
	{
		world.setDelta(delta);
		world.process();

		if (errorMessage != null)
			return;

		// Check to see if this is the first time through the update loop, if so then
		// intialize the list of resources that need to be loaded
		if (loadIndex == -1)
		{
			try
			{
				if (loadingMap)
				{
					if (loadResources)
					{
						// If we are loading maps and resources then this is the first load before enter the actual game state.
						// In this case initialize the default resources
						allLines = FCResourceManager.readAllLines("/loader/Default");
					}

					// Regardless of whether we are loading other resources, add the map and text files
					// that were specified to be loaded
					allLines.add(0, "map,map,/map/" + mapName + ".tmx");
					allLines.add(0, "text,/text/" + textName);

				}
				// If we are not loading the map then we just want to load the specified resources
				else if (loadResources)
					allLines = FCResourceManager.readAllLines(textName);
				loadAmount = allLines.size();

			}
			catch (Throwable e)
			{
				System.err.println("Error loading resource list: " + mapName);
				errorMessage = "Error loading resource list: " + mapName;
				e.printStackTrace();
				// System.exit(0);
			}
		}
		else if (allLines.size() > 0)
		{
			String line = allLines.remove(0);
			if (!line.startsWith("//"))
			{
				try
				{
					resourceManager.addResource(line, loadingEntity, loadIndex, loadAmount);
				}
				catch (Throwable e)
				{
					System.err.println("Error loading resource: " + line);
					errorMessage = "Error loading resource: " + line;
					e.printStackTrace();
					// System.exit(0);
				}
			}
		}

		loadIndex++;

		if (allLines.size() == 0)
		{
			// This is the entry point into the actual game. Initialize static variables here
			if (loadingMap && loadResources)
			{
				GlobalPythonFactory.intialize();
				Panel.intialize((FCResourceManager) resourceManager);
				SpellResource.initSpells((FCResourceManager) resourceManager);

				loadIndex = loadAmount;
			}

			// Only alert the loadable state if resources are being loaded. If they are not being loaded
			// then map data will be updated in the current resource manager
			if (loadResources)
				nextState.stateLoaded(resourceManager);
			LoadingState.loading = false;
			game.getState(nextState.getID()).init(container, game);
			game.enterState(nextState.getID(), new EmptyTransition(), new FadeInTransition(Color.black, 250));
		}

	}

	public void setLoadingInfo(String textName, String mapName, boolean loadMap, boolean loadResources,
			ResourceManager resourceManager, LoadableGameState nextState,
				EntitySystem loadingRenderer)
	{
		this.textName = textName;
		this.mapName = mapName;
		this.loadingMap = loadMap;
		this.nextState = nextState;
		this.loadingRenderer = loadingRenderer;
		this.loadResources = loadResources;
		this.resourceManager = resourceManager;
		this.loadIndex = -1;
		allLines = new ArrayList<String>();
		world = new World();
		world.setSystem(loadingRenderer);
		loadingEntity = world.createEntity();
		loadingEntity.addComponent(new LoadingComp());
		loadingEntity.addToWorld();
		world.initialize();
	}

	@Override
	public int getID() {
		return stateId;
	}
}
