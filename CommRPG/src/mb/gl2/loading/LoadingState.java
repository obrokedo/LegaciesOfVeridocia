package mb.gl2.loading;

import java.util.List;

import mb.fc.resource.FCResourceManager;
import mb.fc.resource.LoadingComp;

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
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException 
	{
		world.setDelta(delta);
		world.process();
		
		if (loadResources)
		{
			if (loadIndex == 0)
			{				
				try 
				{
					if (loadingMap)
						allLines = FCResourceManager.readAllLines("/loader/Default", getClass());
					else
						allLines = FCResourceManager.readAllLines(textName, getClass());
					loadAmount = allLines.size();	
				} 
				catch (Throwable e) 
				{
					System.err.println("Error loading resource list: " + mapName);
					e.printStackTrace();
					System.exit(0);
				}
			}
			else if (!allLines.get(loadIndex - 1).startsWith("//"))
			{
				try 
				{				
					resourceManager.addResource(allLines.get(loadIndex - 1), loadingEntity, loadIndex - 1, loadAmount + 6);
				} 
				catch (Throwable e) 
				{
					System.err.println("Error loading resource: " + allLines.get(loadIndex - 1));
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
		else
			loadAmount = 0;
		
		loadIndex++;
			
		if (loadIndex > loadAmount)
		{
			if (loadingMap)
			{
				try
				{
					resourceManager.addResource("map,map,/map/" + mapName + ".tmx", loadingEntity, loadIndex + 1, loadAmount + 6);
					resourceManager.addResource("text,/text/" + textName, loadingEntity, loadIndex + 2, loadAmount + 6);

					// If we are loading maps and resources then this is the first load before enter the actual game state,
					// in this case initialize definitions
					if (loadResources)
					{
						resourceManager.addResource("herodefs,/definitions/Heroes", loadingEntity, loadIndex + 3, loadAmount + 6);
						resourceManager.addResource("itemdefs,/definitions/Items", loadingEntity, loadIndex + 4, loadAmount + 6);
						resourceManager.addResource("enemydefs,/definitions/Enemies", loadingEntity, loadIndex + 5, loadAmount + 6);
					}
				}
				catch (Throwable e) 
				{
					System.err.println("Error loading map (tmx) and trigger/text file: " + mapName);
					e.printStackTrace();
					System.exit(0);
				}						
			}
			
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
		this.loadIndex = 0;
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
