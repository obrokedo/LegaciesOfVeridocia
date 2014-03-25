package mb.fc.cinematic;

import java.util.ArrayList;
import java.util.Hashtable;

import mb.fc.cinematic.event.CinematicEvent;
import mb.fc.engine.CommRPG;
import mb.fc.engine.state.PersistentStateInfo;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.constants.Direction;
import mb.fc.game.input.FCInput;
import mb.fc.game.menu.Menu.MenuUpdate;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.sprite.AnimatedSprite;
import mb.fc.game.sprite.NPCSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.map.Map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Cinematic 
{	
	private ArrayList<CinematicEvent> initializeEvents;
	private ArrayList<CinematicEvent> cinematicEvents;
	private Hashtable<String, CinematicActor> actors;
	private SpeechMenu speechMenu;
	private int haltedMovers;
	private int haltedAnims;
	private int waitTime;
	
	/*********************/
	/* Camera parameters */
	/*********************/
	private CinematicActor cameraFollow;
	private int cameraMoveToX = -1, cameraMoveToY = -1;
	private int cameraStartX, cameraStartY;
	private long cameraMoveDelta = 0;
	private float cameraMoveSpeed;
	private static final int CAMERA_UPDATE = 30;
	private boolean cameraShaking = false;
	private int cameraShakeDuration;
	private int cameraShakeSeverity;
	private int lastCameraShake;
	
	public Cinematic(ArrayList<CinematicEvent> initializeEvents, ArrayList<CinematicEvent> cinematicEvents, int cameraX, int cameraY)
	{
		this.initializeEvents = initializeEvents;
		this.cinematicEvents = cinematicEvents;
		actors = new Hashtable<String, CinematicActor>();
		this.haltedMovers = 0;
		this.haltedAnims = 0;
		this.waitTime = 0;						
		this.cameraMoveToX = cameraX;
		this.cameraMoveToY = cameraY;
	}
	
	public void initialize(PersistentStateInfo psi, Camera camera, Map map, StateInfo stateInfo)
	{		
		camera.setLocation(cameraStartX, cameraStartY);
		
		for (CinematicEvent ce : initializeEvents)
			handleEvent(ce, camera, psi.getGc(), psi, map, stateInfo);
	}
	
	public boolean update(int delta, Camera camera, FCInput input, GameContainer gc, PersistentStateInfo psi, Map map, StateInfo stateInfo)
	{
		for (CinematicActor ca : actors.values())
			ca.update(delta, this);
		
		if (waitTime > 0)
			waitTime = Math.max(0, waitTime - delta);
		
		
		if (cameraMoveToX != -1)
		{
			cameraMoveDelta += delta;
			
			if (cameraMoveDelta > CAMERA_UPDATE)
			{
				cameraMoveDelta -= CAMERA_UPDATE;
				float xDel = 0;
				float yDel = 0;
				if (cameraMoveToX > camera.getLocationX())
					xDel = Math.min(cameraMoveSpeed, cameraMoveToX - camera.getLocationX());
				else if (cameraMoveToX < camera.getLocationX())
					xDel = - Math.min(cameraMoveSpeed, camera.getLocationX() - cameraMoveToX);
				
				if (cameraMoveToY > camera.getLocationY())
					yDel = Math.min(cameraMoveSpeed, cameraMoveToY - camera.getLocationY());
				else if (cameraMoveToY < camera.getLocationY())
					yDel = -Math.min(cameraMoveSpeed, camera.getLocationY() - cameraMoveToY);
				
				if (xDel != 0 || yDel != 0)
				{
					camera.setLocation(camera.getLocationX() + xDel, camera.getLocationY() + yDel);
				}
				else
				{
					cameraMoveToX = -1;
					cameraMoveToY = -1;
				}
			}
		}
		
		if (cameraFollow != null)
		{
			camera.centerOnPoint(cameraFollow.getLocX(), cameraFollow.getLocY(), map);	
		}
		
		if (cameraShaking)
		{
			cameraShakeDuration -= delta;
			// Reset the camera to the correct location
			camera.setLocation(camera.getLocationX() - lastCameraShake, camera.getLocationY() - lastCameraShake);
			if (cameraShakeDuration > 0)
			{					
				int newCameraShake = CommRPG.RANDOM.nextInt(cameraShakeSeverity * 2) - cameraShakeSeverity;			
				camera.setLocation(camera.getLocationX() + newCameraShake, camera.getLocationY() + newCameraShake);
				lastCameraShake = newCameraShake;
			}
			else
				cameraShaking = false;
		}
		
		if (speechMenu != null && MenuUpdate.MENU_CLOSE == speechMenu.handleUserInput(input, null))
		{
			speechMenu = null;
		}
		
		// If nothing is currently blocking then continue processing the cinematics
		while (waitTime == 0 && haltedAnims == 0 && 
				haltedMovers == 0 && cameraMoveToX == -1 && speechMenu == null &&
					cinematicEvents.size() > 0)
		{
			CinematicEvent ce = cinematicEvents.remove(0);
			handleEvent(ce, camera, gc, psi, map, stateInfo);
		}
		
		return waitTime == 0 && haltedAnims == 0 && 
				haltedMovers == 0 && cameraMoveToX == -1 && speechMenu == null &&
				cinematicEvents.size() == 0;
	}	
	
	private void handleEvent(CinematicEvent ce, Camera camera, GameContainer gc, 
			PersistentStateInfo psi, Map map, StateInfo stateInfo)
	{
		System.out.println("Handle event: " + ce.getType());
		switch (ce.getType())
		{
			case HALTING_MOVE:
				actors.get(ce.getParam(3)).moveToLocation((int) ce.getParam(0), (int) ce.getParam(1), (int) ce.getParam(2), true);
				haltedMovers++;
				break;
			case MOVE:
				actors.get(ce.getParam(3)).moveToLocation((int) ce.getParam(0), (int) ce.getParam(1), (int) ce.getParam(2), false);
				break;
			case LOOP_MOVE:
				actors.get(ce.getParam(0)).loopMoveToLocation((int) ce.getParam(1), (int) ce.getParam(2), (int) ce.getParam(3));
				break;
			case STOP_LOOP_MOVE:
				actors.get((String) ce.getParam(0)).stopLoopMove();
				break;
			case CAMERA_MOVE:
				cameraMoveToX = (int) ce.getParam(0);
				cameraMoveToY = (int) ce.getParam(1);				
				int distance = Math.abs(camera.getLocationX() - cameraMoveToX);
				distance += Math.abs(camera.getLocationY() - cameraMoveToY);
				System.out.println((int) ce.getParam(2));
				cameraMoveSpeed = distance / ((int) ce.getParam(2) / CAMERA_UPDATE); 
				cameraFollow = null;
				break;
			case CAMERA_CENTER:
				camera.centerOnPoint((int) ce.getParam(0), (int) ce.getParam(1), map);
				cameraFollow = null;
				break;
			case CAMERA_FOLLOW:
				cameraFollow = actors.get(ce.getParam(0));
				camera.centerOnPoint(cameraFollow.getLocX(), cameraFollow.getLocY(), map);					
				break;
			case CAMERA_SHAKE:
				lastCameraShake = 0;
				cameraShakeDuration = (int) ce.getParam(0);
				cameraShakeSeverity = (int) ce.getParam(1);
				cameraShaking = true;				
				break;
			case SPEECH:
				speechMenu = new SpeechMenu((String) ce.getParam(0), gc);
				break;
			case LOAD_MAP:
				psi.loadMap((String) ce.getParam(0), (String) ce.getParam(1));
				break;
			case LOAD_BATTLE:
				psi.loadBattle((String) ce.getParam(0), (String) ce.getParam(1));
				break;
			case HALTING_ANIMATION:	
				actors.get((String) ce.getParam(0)).setAnimation((String) ce.getParam(1), (int) ce.getParam(2), true, false);
				haltedAnims++;
				break;
			case ANIMATION:	
				actors.get((String) ce.getParam(0)).setAnimation((String) ce.getParam(1), (int) ce.getParam(2), false, (boolean) ce.getParam(3));
				break;
			case ADD_ACTOR:
				actors.put((String) ce.getParam(2), new CinematicActor(psi.getResourceManager().getSpriteAnimations().get((String) ce.getParam(3)), 
						(String) ce.getParam(4), (int) ce.getParam(0), (int) ce.getParam(1), (boolean) ce.getParam(5)));
				break;
			case WAIT:
				waitTime = (int) ce.getParam(0);
				break;
			case SPIN:
				actors.get((String) ce.getParam(0)).setSpinning((int) ce.getParam(1), (int) ce.getParam(2));
				break;
			case STOP_SPIN:
				actors.get((String) ce.getParam(0)).stopSpinning();
				break;
			case SHRINK:
				actors.get((String) ce.getParam(0)).shrink((int) ce.getParam(1));
				break;
			case GROW:
				actors.get((String) ce.getParam(0)).grow((int) ce.getParam(1));
				break;
			case QUIVER:
				actors.get((String) ce.getParam(0)).quiver();
				break;
			case LAY_ON_SIDE:
				actors.get((String) ce.getParam(0)).layOnSide();
				break;
			case FALL_ON_FACE:
				actors.get((String) ce.getParam(0)).fallOnFace();
				break;
			case FLASH:
				actors.get((String) ce.getParam(0)).flash((int) ce.getParam(1), (int) ce.getParam(2));
				break;
			case NOD:
				actors.get((String) ce.getParam(0)).nodHead();
				break;
			case HEAD_SHAKE:
				actors.get((String) ce.getParam(0)).shakeHead();
				break;
			case STOP_SE:
				actors.get((String) ce.getParam(0)).stopSpecialEffect();
				break;
			case VISIBLE:
				actors.get((String) ce.getParam(0)).setVisible((boolean) ce.getParam(1));
				break;
			case REMOVE_ACTOR:
				actors.remove((String) ce.getParam(0));
				break;
			case FACING:
				int dir = (int) ce.getParam(1);
				if (dir == 0)
					actors.get((String) ce.getParam(0)).setFacing(Direction.UP);
				else if (dir == 1)
					actors.get((String) ce.getParam(0)).setFacing(Direction.DOWN);
				else if (dir == 2)
					actors.get((String) ce.getParam(0)).setFacing(Direction.LEFT);
				else if (dir == 3)
					actors.get((String) ce.getParam(0)).setFacing(Direction.RIGHT);
				break;
			case ASSOCIATE_NPC_AS_ACTOR:
				for (Sprite s : stateInfo.getSprites())
				{
					if (s.getSpriteType() == Sprite.TYPE_NPC && ((NPCSprite) s).getUniqueNPCId() == (int) ce.getParam(0))
					{
						actors.put((String) ce.getParam(1), new CinematicActor((AnimatedSprite) s));
						break;
					}
				}
				break;
		}				
	}
	
	public void render(Graphics graphics, Camera camera, FCGameContainer cont)
	{
		for (CinematicActor ca : actors.values())
			ca.render(graphics, camera, cont);		
	}
	
	public void renderMenus(FCGameContainer cont, Graphics g)
	{
		if (speechMenu != null)
			speechMenu.render(cont, g);
	}
	
	public void renderPostEffects(Graphics g)
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

	public int getCameraStartX() {
		return cameraStartX;
	}

	public int getCameraStartY() {
		return cameraStartY;
	}
}
