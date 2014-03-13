package mb.fc.engine.state;

import java.util.ArrayList;

import mb.fc.game.trigger.Trigger;
import mb.fc.map.MapObject;
import mb.gl2.loading.LoadableGameState;
import mb.gl2.loading.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class CinematicState extends LoadableGameState
{
	private PersistentStateInfo psi;
	
	private ArrayList<Trigger> triggers;

	public CinematicState(PersistentStateInfo psi)
	{
		this.psi = psi;
		triggers = new ArrayList<Trigger>();
	}
	
	public void initialize()
	{
		triggers.clear();
	}
	
	private void initializeMapObjects()
	{
		for (MapObject mo : psi.getResourceManager().getMap().getMapObjects())
		{
			if (mo.getKey().equalsIgnoreCase("trigger"))
			{
				
			}
		}
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateLoaded(ResourceManager resourceManager) {
		
	}

	@Override
	public int getID() {
		return 0;
	}
}
