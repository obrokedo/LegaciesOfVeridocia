package mb.fc.cinematic;

import java.util.ArrayList;

import mb.fc.cinematic.event.CinematicEvent;
import mb.fc.game.Camera;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Graphics;

public class Cinematic 
{	
	private String name;
	private ArrayList<CinematicEvent> cinematicEvents;
	private ArrayList<CinematicActor> actors;
	private SpeechMenu speechMenu;
	private int haltedMovers;
	private int haltedAnims;
	private int waitTime;
	
	public Cinematic()
	{
		actors = new ArrayList<CinematicActor>();
		this.haltedMovers = 0;
		this.haltedAnims = 0;
		this.waitTime = 0;
	}
	
	public void update(int delta, Camera camera)
	{
		for (CinematicActor ca : actors)
			ca.update(delta, this);
		
		if (waitTime > 0)
			waitTime = Math.max(0, waitTime - delta);
		
		// If nothing is currently blocking then continue processing the cinematics
		if (waitTime == 0 && haltedAnims == 0 && haltedMovers == 0)
		{
			
		}
	}	
	
	public void render(Graphics graphics, Camera camera, FCGameContainer cont)
	{
		
	}
	
	public void decreaseMoves()
	{
		haltedMovers--;
	}
	
	public void decreaseAnims()
	{
		haltedAnims--;
	}
}
