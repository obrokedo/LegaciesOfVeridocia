package mb.fc.loading;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.Transition;
import org.newdawn.slick.util.Log;

import mb.fc.engine.CommRPG;
import mb.fc.engine.config.EngineConfigurator;
import mb.fc.game.exception.BadResourceException;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.resource.SpellResource;
import mb.jython.GlobalPythonFactory;

public class LoadingState extends BasicGameState
{
	private String mapName;
	private String textName;
	private LoadableGameState nextState;
	private FCLoadingRenderSystem loadingRenderer;
	private FCResourceManager resourceManager;
	private boolean loadResources;
	private LoadingStatus loadingStatus;
	private int stateId;
	private int loadIndex;
	private List<String> allLines;
	private int loadAmount;
	private boolean loadingMap;
	private String errorMessage = null;
	private Transition enterNextStateTransition;
	private Image intermediateImage = null;
	public static final boolean inJar = false;
	public static Class<?> MY_CLASS;

	public LoadingState(int stateId)
	{
		this.stateId = stateId;
		if (LoadingState.inJar)
			MY_CLASS = this.getClass();
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException
	{

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		if (intermediateImage != null)
			g.drawImage(intermediateImage, 0, 0);
		else 
			loadingRenderer.update(loadingStatus);
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

					// Regardless of whether we are loading other resources, add the text file
					// that was specified to be loaded
					allLines.add("text,/mapdata/" + textName);
					
					if (!loadResources)
					{
						while (allLines.size() > 0)
							loadResourceLine(((CommRPG) game).getEngineConfiguratior());
					}

				}
				// If we are not loading the map then we just want to load the specified resources
				else if (loadResources)
					allLines = FCResourceManager.readAllLines(textName);
				loadAmount = allLines.size();
			}
			catch (Throwable e)
			{
				Log.debug("Error loading resource list: " + mapName);
				errorMessage = "Error loading resource list: " + mapName;
				e.printStackTrace();
				// System.exit(0);
			}
		}
		else if (allLines.size() > 0)
		{
			loadResourceLine(((CommRPG) game).getEngineConfiguratior());
		}

		loadIndex++;

		if (allLines.size() == 0)
		{
			// This is the entry point into the actual game. Initialize static variables here
			if (loadingMap && loadResources)
			{
				GlobalPythonFactory.intialize();
				Panel.intialize(resourceManager);
				SpellResource.initSpells(resourceManager);

				loadIndex = loadAmount;
			}

			// Only alert the loadable state if resources are being loaded. If they are not being loaded
			// then map data will be updated in the current resource manager
			if (loadResources)	
				nextState.stateLoaded(resourceManager);
			nextState.initAfterLoad();
			if (enterNextStateTransition == null)
				game.enterState(nextState.getID(), new EmptyTransition(), new FadeInTransition(Color.black, 250));
			else
				game.enterState(nextState.getID(), new EmptyTransition(), enterNextStateTransition);
		}

	}

	private void loadResourceLine(EngineConfigurator configurator) {
		String line = allLines.remove(0);
		if (!line.startsWith("//"))
		{
			try
			{
				resourceManager.addResource(line, loadingStatus, loadIndex, loadAmount, configurator);
			}
			catch (BadResourceException e)
			{
				Log.debug("Error loading resource: " + line);
				errorMessage = "Error loading resource: " + line + ": " + e.getMessage();
				e.printStackTrace();
				throw e;
			}
			catch (Throwable e)
			{
				Log.debug("Error loading resource: " + line);
				errorMessage = "Error loading resource: " + line;
				e.printStackTrace();
				throw new BadResourceException("Error loading resource: " + line + ".\n" + e.getMessage());
			}
		}
	}
	
	public void setLoadingInfo(String textName, boolean loadMap, boolean loadResources,
			FCResourceManager resourceManager, LoadableGameState nextState,
				FCLoadingRenderSystem loadingRenderer) {
		setLoadingInfo(textName, loadMap, loadResources, resourceManager, nextState, loadingRenderer, null, null);
	}

	public void setLoadingInfo(String textName, boolean loadMap, boolean loadResources,
			FCResourceManager resourceManager, LoadableGameState nextState,
				FCLoadingRenderSystem loadingRenderer, Image intermediateImage, Transition transition)
	{
		this.errorMessage = null;
		this.textName = textName;
		this.loadingMap = loadMap;
		this.nextState = nextState;
		this.loadingRenderer = loadingRenderer;
		this.loadResources = loadResources;
		this.resourceManager = resourceManager;
		this.loadIndex = -1;
		this.intermediateImage = intermediateImage;
		this.enterNextStateTransition = transition;
		allLines = new ArrayList<String>();
		loadingStatus = new LoadingStatus();
	}

	@Override
	public int getID() {
		return stateId;
	}
}
