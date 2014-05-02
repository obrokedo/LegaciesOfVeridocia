package mb.fc.cinematic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import mb.fc.cinematic.event.CinematicEvent;
import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.Message;
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

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Cinematic {
	private static final CinematicActorComparator CIN_ACT_COMP = new CinematicActorComparator();
	private ArrayList<CinematicEvent> initializeEvents;
	private ArrayList<CinematicEvent> cinematicEvents;
	private Hashtable<String, CinematicActor> actors;
	private ArrayList<CinematicActor> sortedActors;
	private SpeechMenu speechMenu;
	private int haltedMovers;
	private int haltedAnims;
	private int waitTime;

	private Color fadingColor = null;
	private float fadeSpeed;
	private boolean fadeIn;
	private long fadeDelta;

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


	public Cinematic(ArrayList<CinematicEvent> initializeEvents,
			ArrayList<CinematicEvent> cinematicEvents, int cameraX, int cameraY) {
		this.initializeEvents = initializeEvents;
		this.cinematicEvents = cinematicEvents;
		actors = new Hashtable<String, CinematicActor>();
		this.sortedActors = new ArrayList<CinematicActor>();
		this.haltedMovers = 0;
		this.haltedAnims = 0;
		this.waitTime = 0;
		this.cameraMoveToX = cameraX;
		this.cameraMoveToY = cameraY;
	}

	public void initialize(StateInfo stateInfo) {
		stateInfo.getCamera().setLocation(cameraStartX, cameraStartY);

		for (CinematicEvent ce : initializeEvents)
			handleEvent(ce, stateInfo);
	}

	public boolean update(int delta, Camera camera, FCInput input,
			GameContainer gc, Map map, StateInfo stateInfo) {

		if (fadingColor != null)
		{
			fadeDelta += delta;
			if (fadeDelta >= 50)
			{

				fadeDelta -= 50;

				if (fadeIn)
				{
					fadingColor.a = Math.max(0, fadingColor.a - fadeSpeed);
					if (fadingColor.a == 0)
						fadingColor = null;
				}
				else
				{
					fadingColor.a = Math.min(1, fadingColor.a + fadeSpeed);
					if (fadingColor.a == 1)
						fadingColor = null;
				}
			}
		}

		if (waitTime > 0)
			waitTime = Math.max(0, waitTime - delta);

		if (cameraMoveToX != -1) {
			cameraMoveDelta += delta;

			if (cameraMoveDelta > CAMERA_UPDATE) {
				cameraMoveDelta -= CAMERA_UPDATE;
				float xDel = 0;
				float yDel = 0;
				if (cameraMoveToX > camera.getLocationX())
					xDel = Math.min(cameraMoveSpeed,
							cameraMoveToX - camera.getLocationX());
				else if (cameraMoveToX < camera.getLocationX())
					xDel = -Math.min(cameraMoveSpeed, camera.getLocationX()
							- cameraMoveToX);

				if (cameraMoveToY > camera.getLocationY())
					yDel = Math.min(cameraMoveSpeed,
							cameraMoveToY - camera.getLocationY());
				else if (cameraMoveToY < camera.getLocationY())
					yDel = -Math.min(cameraMoveSpeed, camera.getLocationY()
							- cameraMoveToY);

				if (xDel != 0 || yDel != 0) {
					camera.setLocation(camera.getLocationX() + xDel,
							camera.getLocationY() + yDel);
				} else {
					cameraMoveToX = -1;
					cameraMoveToY = -1;
				}
			}
		}

		for (CinematicActor ca : sortedActors)
		{
			ca.update(delta, this);
			if (ca == cameraFollow)
			{
				camera.centerOnPoint(cameraFollow.getLocX(),
						cameraFollow.getLocY(), map);
			}
		}

		Collections.sort(sortedActors, CIN_ACT_COMP);

		if (cameraShaking) {
			cameraShakeDuration -= delta;
			// Reset the camera to the correct location
			camera.setLocation(camera.getLocationX() - lastCameraShake,
					camera.getLocationY() - lastCameraShake);
			if (cameraShakeDuration > 0) {
				int newCameraShake = CommRPG.RANDOM
						.nextInt(cameraShakeSeverity * 2) - cameraShakeSeverity;
				camera.setLocation(camera.getLocationX() + newCameraShake,
						camera.getLocationY() + newCameraShake);
				lastCameraShake = newCameraShake;
			} else
				cameraShaking = false;
		}


		if (speechMenu != null
				&& MenuUpdate.MENU_CLOSE == speechMenu.handleUserInput(input,
						stateInfo)) {
			speechMenu = null;
		}

		// If nothing is currently blocking then continue processing the
		// cinematics
		while (waitTime == 0 && haltedAnims == 0 && haltedMovers == 0
				&& cameraMoveToX == -1 && speechMenu == null
				&& cinematicEvents.size() > 0) {
			CinematicEvent ce = cinematicEvents.remove(0);
			handleEvent(ce, stateInfo);
		}

		return waitTime == 0 && haltedAnims == 0 && haltedMovers == 0
				&& cameraMoveToX == -1 && speechMenu == null
				&& cinematicEvents.size() == 0;
	}

	boolean debug = false;

	private void handleEvent(CinematicEvent ce, StateInfo stateInfo) {
		System.out.println("Handle event: " + ce.getType());
		switch (ce.getType()) {
		case HALTING_MOVE:
			actors.get(ce.getParam(3)).moveToLocation((int) ce.getParam(0),
					(int) ce.getParam(1), (float) ce.getParam(2), true, -1, (boolean) ce.getParam(4), (boolean) ce.getParam(5));
			haltedMovers++;
			break;
		case MOVE:
			if ((int) ce.getParam(0) == 480 &&
					(int) ce.getParam(1) == 96 && (float) ce.getParam(2) == 2)
				debug = true;

			actors.get(ce.getParam(3)).moveToLocation((int) ce.getParam(0),
					(int) ce.getParam(1), (float) ce.getParam(2), false, -1, (boolean) ce.getParam(4), (boolean) ce.getParam(5));
			break;
		case MOVE_ENFORCE_FACING:
			actors.get(ce.getParam(3)).moveToLocation((int) ce.getParam(0),
					(int) ce.getParam(1), (float) ce.getParam(2), false,
					(int) ce.getParam(4), (boolean) ce.getParam(5), (boolean) ce.getParam(6));
			break;
		case LOOP_MOVE:
			actors.get(ce.getParam(0)).loopMoveToLocation((int) ce.getParam(1),
					(int) ce.getParam(2), (float) ce.getParam(3));
			break;
		case STOP_LOOP_MOVE:
			actors.get(ce.getParam(0)).stopLoopMove();
			break;
		case CAMERA_MOVE:
			cameraMoveToX = (int) ce.getParam(0)
					* CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]
					- stateInfo.getCamera().getViewportWidth() / 2;
			cameraMoveToY = (int) ce.getParam(1)
					* CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]
					- stateInfo.getCamera().getViewportHeight() / 2;
			int distance = Math.abs(stateInfo.getCamera().getLocationX()
					- cameraMoveToX);
			distance += Math.abs(stateInfo.getCamera().getLocationY()
					- cameraMoveToY);
			cameraMoveSpeed = distance / ((int) ce.getParam(2) / CAMERA_UPDATE);
			cameraFollow = null;
			break;
		case CAMERA_CENTER:
			stateInfo
					.getCamera()
					.centerOnPoint(
							(int) ce.getParam(0)
									* CommRPG.GLOBAL_WORLD_SCALE[CommRPG
											.getGameInstance()],
							(int) ce.getParam(1)
									* CommRPG.GLOBAL_WORLD_SCALE[CommRPG
											.getGameInstance()],
							stateInfo.getCurrentMap());
			cameraFollow = null;
			break;
		case CAMERA_FOLLOW:
			cameraFollow = actors.get(ce.getParam(0));
			stateInfo.getCamera().centerOnPoint(cameraFollow.getLocX(),
					cameraFollow.getLocY(), stateInfo.getCurrentMap());
			break;
		case CAMERA_SHAKE:
			lastCameraShake = 0;
			cameraShakeDuration = (int) ce.getParam(0);
			cameraShakeSeverity = (int) ce.getParam(1)
					* CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];
			cameraShaking = true;
			break;
		case ADD_ACTOR:
			CinematicActor ca = new CinematicActor(
					stateInfo.getResourceManager()
					.getSpriteAnimations()
					.get(ce.getParam(3)), (String) ce
					.getParam(4), (int) ce.getParam(0),
			(int) ce.getParam(1), (boolean) ce.getParam(5));
			actors.put(
					(String) ce.getParam(2), ca);
			sortedActors.add(ca);
			break;
		case SPEECH:
			speechMenu = new SpeechMenu((String) ce.getParam(0),
					stateInfo.getGc(), -1, (int) ce.getParam(1), stateInfo);
			break;
		case LOAD_MAP:
			stateInfo.getPsi().loadMap((String) ce.getParam(0),
					(String) ce.getParam(1));
			break;
		case LOAD_BATTLE:
			stateInfo.getPsi().loadBattle((String) ce.getParam(0),
					(String) ce.getParam(1));
			break;
		case HALTING_ANIMATION:
			actors.get(ce.getParam(0)).setAnimation(
					(String) ce.getParam(1), (int) ce.getParam(2), true, false);
			haltedAnims++;
			break;
		case ANIMATION:
			actors.get(ce.getParam(0)).setAnimation(
					(String) ce.getParam(1), (int) ce.getParam(2), false,
					(boolean) ce.getParam(3));
			break;
		case WAIT:
			waitTime = (int) ce.getParam(0);
			break;
		case SPIN:
			actors.get(ce.getParam(0)).setSpinning(
					(int) ce.getParam(1), (int) ce.getParam(2));
			break;
		case STOP_SPIN:
			actors.get(ce.getParam(0)).stopSpinning();
			break;
		case SHRINK:
			actors.get(ce.getParam(0)).shrink((int) ce.getParam(1));
			break;
		case GROW:
			actors.get(ce.getParam(0)).grow((int) ce.getParam(1));
			break;
		case QUIVER:
			actors.get(ce.getParam(0)).quiver();
			break;
		case TREMBLE:
			actors.get(ce.getParam(0)).tremble();
			break;
		case LAY_ON_BACK:
			actors.get(ce.getParam(0)).layOnBack(
					getDirectionFromInt((int) ce.getParam(1)));
			break;
		case LAY_ON_SIDE:
			actors.get(ce.getParam(0)).layOnSide(
					getDirectionFromInt((int) ce.getParam(1)));
			break;
		case FALL_ON_FACE:
			actors.get(ce.getParam(0)).fallOnFace(
					getDirectionFromInt((int) ce.getParam(1)));
			break;
		case FLASH:
			actors.get(ce.getParam(0)).flash((int) ce.getParam(1),
					(int) ce.getParam(2));
			break;
		case NOD:
			actors.get(ce.getParam(0)).nodHead();
			break;
		case HEAD_SHAKE:
			actors.get(ce.getParam(0)).shakeHead((int) ce.getParam(1));
			break;
		case STOP_SE:
			actors.get(ce.getParam(0)).stopSpecialEffect();
			break;
		case VISIBLE:
			actors.get(ce.getParam(0)).setVisible(
					(boolean) ce.getParam(1));
			break;
		case REMOVE_ACTOR:
			sortedActors.remove(actors.remove(ce.getParam(0)));
			break;
		case FACING:
			int dir = (int) ce.getParam(1);
			actors.get(ce.getParam(0)).setFacing(dir);
			break;
		case ASSOCIATE_NPC_AS_ACTOR:
			for (Sprite s : stateInfo.getSprites()) {
				if (s.getSpriteType() == Sprite.TYPE_NPC
						&& ((NPCSprite) s).getUniqueNPCId() == (int) ce
								.getParam(0)) {
					actors.put((String) ce.getParam(1), new CinematicActor(
							(AnimatedSprite) s));
					break;
				}
			}
			break;
		case PLAY_MUSIC:
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_PLAY_MUSIC,
					(String) ce.getParam(0), ((int) ce.getParam(1)) / 100.0f,
					true));
			break;
		case PAUSE_MUSIC:
			stateInfo.sendMessage(Message.MESSAGE_PAUSE_MUSIC);
			break;
		case RESUME_MUSIC:
			stateInfo.sendMessage(Message.MESSAGE_RESUME_MUSIC);
			break;
		case FADE_MUSIC:
			stateInfo.sendMessage(new IntMessage(Message.MESSAGE_FADE_MUSIC,
					(int) ce.getParam(0)));
			break;
		case PLAY_SOUND:
			stateInfo.sendMessage(new AudioMessage(
					Message.MESSAGE_SOUND_EFFECT, (String) ce.getParam(0),
					((int) ce.getParam(1)) / 100.0f, true));
			break;
		case STOP_ANIMATION:
			actors.get(ce.getParam(0)).stopAnimation();
			break;
		case FADE_FROM_BLACK:
			fadeIn = true;
			fadingColor = new Color(0f, 0f, 0f, 1f);
			fadeDelta = 0;
			fadeSpeed =  1.0f / ((int) ce.getParam(0) / 50f);
			if ((boolean) ce.getParam(1))
				this.waitTime = (int) ce.getParam(0);
			break;
		case FADE_TO_BLACK:
			fadeIn = false;
			fadingColor = new Color(0f, 0f, 0f, 0f);
			fadeDelta = 0;
			fadeSpeed =  1.0f / ((int) ce.getParam(0) / 50f);
			if ((boolean) ce.getParam(1))
				this.waitTime = (int) ce.getParam(0);
			break;
			default:
				break;
		}

	}

	public void render(Graphics graphics, Camera camera, FCGameContainer cont,
			StateInfo stateInfo) {
		for (CinematicActor ca : sortedActors)
		{
			ca.render(graphics, camera, cont, stateInfo);
		}

	}

	public void renderMenus(FCGameContainer cont, Graphics g) {
		if (speechMenu != null)
			speechMenu.render(cont, g);
	}

	public void renderPostEffects(FCGameContainer cont, Graphics g) {
		if (fadingColor != null)
		{
			g.setColor(fadingColor);
			g.fillRect(0, 0, cont.getWidth(), cont.getHeight());
		}


	}

	public void decreaseMoves() {
		haltedMovers--;
	}

	public void decreaseAnims() {
		haltedAnims--;
	}

	public int getCameraStartX() {
		return cameraStartX;
	}

	public int getCameraStartY() {
		return cameraStartY;
	}

	public Direction getDirectionFromInt(int dir) {
		if (dir == 0)
			return Direction.UP;
		else if (dir == 1)
			return Direction.DOWN;
		else if (dir == 2)
			return Direction.LEFT;
		else if (dir == 3)
			return Direction.RIGHT;
		return null;
	}
}
