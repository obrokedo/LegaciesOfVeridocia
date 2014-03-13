package mb.fc.engine.state;

import java.util.ArrayList;

import mb.fc.engine.ForsakenChampions;
import mb.fc.game.Camera;
import mb.fc.game.persist.ClientProfile;
import mb.fc.game.persist.ClientProgress;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.resource.FCResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class PersistentStateInfo
{
	private Camera camera;	
	private ForsakenChampions game;
	private FCGameContainer gc;
	private Graphics graphics;
	private ArrayList<CombatSprite> heroes;
	private ClientProfile clientProfile;
	private ClientProgress clientProgress;
	private FCResourceManager resourceManager;
	
	public PersistentStateInfo(ClientProfile clientProfile, ClientProgress clientProgress, ForsakenChampions game, Camera camera, 
			GameContainer gc, Graphics graphics, int clientId)
	{
		this.game = game;
		this.camera = camera;
		this.gc = (FCGameContainer) gc;
		this.graphics = graphics;
		this.heroes = new ArrayList<CombatSprite>();
		this.clientProfile = clientProfile;
		this.clientProgress = clientProgress;
		this.heroes.addAll(clientProfile.getHeroes());
	}
	
	public PersistentStateInfo(ClientProfile clientProfile, ClientProgress clientProgress, ForsakenChampions game, Camera camera, 
			GameContainer gc, Graphics graphics, boolean isHost)
	{
		this.game = game;
		this.camera = camera;
		this.gc = (FCGameContainer) gc;
		this.graphics = graphics;
		this.heroes = new ArrayList<CombatSprite>();
		this.clientProfile = clientProfile;
		this.clientProgress = clientProgress;
		this.heroes.addAll(clientProfile.getHeroes());
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

	public ForsakenChampions getGame() {
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
}
