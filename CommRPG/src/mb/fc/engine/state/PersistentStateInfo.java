package mb.fc.engine.state;

import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.game.Camera;
import mb.fc.game.persist.ClientProfile;
import mb.fc.game.persist.ClientProgress;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

/**
 * Contains information that should be shared across all game states
 *
 * @author Broked
 *
 */
public class PersistentStateInfo
{
	private Camera camera;
	private CommRPG game;
	private FCGameContainer gc;
	private Graphics graphics;
	private ArrayList<CombatSprite> heroes;
	private ClientProfile clientProfile;
	private ClientProgress clientProgress;
	private FCResourceManager resourceManager;
	private String entranceLocation = "priest";
	private int cinematicID = 0;

	public PersistentStateInfo(ClientProfile clientProfile, ClientProgress clientProgress, CommRPG game, Camera camera,
			GameContainer gc, Graphics graphics)
	{
		this.game = game;
		this.camera = camera;
		this.gc = (FCGameContainer) gc;
		this.graphics = graphics;
		this.heroes = new ArrayList<CombatSprite>();
		this.clientProfile = clientProfile;
		this.clientProgress = clientProgress;
		this.heroes.addAll(clientProfile.getHeroes());
		this.entranceLocation = getClientProgress().getLocation();
	}

	/********************/
	/* Map Management	*/
	/********************/
	public void loadMap(String map, String entrance)
	{
		this.entranceLocation = entrance;

		gc.getInput().removeAllKeyListeners();

		getClientProgress().setMap(map, false);

		getGame().setLoadingInfo(map, map,
				(LoadableGameState) getGame().getState(CommRPG.STATE_GAME_TOWN),
					getResourceManager());
		getGame().enterState(CommRPG.STATE_GAME_LOADING, new FadeOutTransition(Color.black, 250), new EmptyTransition());
	}

	public void loadBattle(String text, String map, String entrance, int battleBGIndex)
	{
		this.entranceLocation = entrance;

		((AttackCinematicState) game.getState(CommRPG.STATE_GAME_BATTLE_ANIM)).setBattleBGIndex(battleBGIndex);

		gc.getInput().removeAllKeyListeners();

		getClientProgress().setMap(map, true);

		getGame().setLoadingInfo(text, map,
				(LoadableGameState) getGame().getState(CommRPG.STATE_GAME_BATTLE),
					getResourceManager());
		getGame().enterState(CommRPG.STATE_GAME_LOADING, new FadeOutTransition(Color.black, 250), new EmptyTransition());
	}

	public void loadCinematic(String map, int cinematicID)
	{
		gc.getInput().removeAllKeyListeners();

		this.cinematicID = cinematicID;

		getGame().setLoadingInfo(map, map,
				(LoadableGameState) getGame().getState(CommRPG.STATE_GAME_CINEMATIC),
					getResourceManager());
		getGame().enterState(CommRPG.STATE_GAME_LOADING, new FadeOutTransition(Color.black, 250), new EmptyTransition());
	}

	public Camera getCamera() {
		return camera;
	}

	public FCGameContainer getGc() {
		return gc;
	}

	public Graphics getGraphics() {
		return graphics;
	}

	public ArrayList<CombatSprite> getHeroes() {
		return heroes;
	}

	public void setQuestComplete(int id)
	{
		this.clientProgress.setQuestComplete(id);
	}

	public boolean isQuestCompelte(int questId)
	{
		return this.clientProgress.isQuestComplete(questId);
	}

	public CommRPG getGame() {
		return game;
	}

	public ClientProfile getClientProfile() {
		return clientProfile;
	}

	public ClientProgress getClientProgress() {
		return clientProgress;
	}

	public FCResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(FCResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public String getEntranceLocation() {
		return entranceLocation;
	}

	public int getCinematicID() {
		return cinematicID;
	}

	public void setEntranceLocation(String entranceLocation) {
		this.entranceLocation = entranceLocation;
	}
}
