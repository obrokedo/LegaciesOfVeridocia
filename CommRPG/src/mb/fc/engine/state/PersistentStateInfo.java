package mb.fc.engine.state;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.Message;
import mb.fc.game.Camera;
import mb.fc.game.persist.ClientProfile;
import mb.fc.game.persist.ClientProgress;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.network.TCPClient;
import mb.fc.network.TCPServer;
import mb.tcp.network.Client;
import mb.tcp.network.PacketHandler;

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
public class PersistentStateInfo implements PacketHandler
{
	private Camera camera;
	private CommRPG game;
	private PaddedGameContainer gc;
	private ClientProfile clientProfile;
	private ClientProgress clientProgress;
	private FCResourceManager resourceManager;
	private String entranceLocation = "priest";
	private int cinematicID = 0;
	private int clientId;
	private TCPServer server = null;
	private TCPClient client = null;
	private StateInfo currentStateInfo;

	public PersistentStateInfo(ClientProfile clientProfile, ClientProgress clientProgress, 
			CommRPG game, Camera camera, GameContainer gc)
	{
		this.game = game;
		this.camera = camera;
		this.gc = (PaddedGameContainer) gc;
		this.clientProfile = clientProfile;
		this.clientProgress = clientProgress;
		this.entranceLocation = getClientProgress().getLocation();
	}

	/********************/
	/* Map Management	*/
	/********************/
	public void loadMap(String mapData, String entrance)
	{
		this.entranceLocation = entrance;

		gc.getInput().removeAllKeyListeners();

		getClientProgress().setMapData(mapData, false);

		getGame().setLoadingInfo(mapData,
				(LoadableGameState) getGame().getState(CommRPG.STATE_GAME_TOWN), getResourceManager());
		getGame().enterState(CommRPG.STATE_GAME_LOADING, new FadeOutTransition(Color.black, 250), new EmptyTransition());
	}

	public void loadBattle(String mapData, String entrance, int battleBGIndex)
	{
		this.entranceLocation = entrance;

		gc.getInput().removeAllKeyListeners();

		getClientProgress().setMapData(mapData, true);
		
		getGame().setLoadingInfo(mapData,
				(LoadableGameState) getGame().getState(CommRPG.STATE_GAME_BATTLE), getResourceManager());
		
		
		
		getGame().enterState(CommRPG.STATE_GAME_LOADING, new FadeOutTransition(Color.black, 250), new EmptyTransition());
	}

	public void loadCinematic(String mapData, int cinematicID)
	{
		gc.getInput().removeAllKeyListeners();

		this.cinematicID = cinematicID;
		
		getClientProgress().setMapData(mapData, false);

		getGame().setLoadingInfo(mapData,
				(LoadableGameState) getGame().getState(CommRPG.STATE_GAME_CINEMATIC),
					getResourceManager());
		getGame().enterState(CommRPG.STATE_GAME_LOADING, new FadeOutTransition(Color.black, 250), new EmptyTransition());
	}

	public Camera getCamera() {
		return camera;
	}

	public PaddedGameContainer getGc() {
		return gc;
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

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		for (CombatSprite cs : clientProfile.getHeroes())
			cs.setClientId(clientId);
		this.clientId = clientId;
	}

	public void setServer(TCPServer server) {
		this.server = server;
	}

	public void setClient(TCPClient client) {
		this.client = client;
	}

	public boolean isHost()
	{
		if (!isOnline())
			return true;
		return server != null;
	}

	public boolean isOnline()
	{
		return client != null;
	}

	/**
	 * Sends the specified message to all connected peers if the message
	 * is not an internal message
	 *
	 * @param message the message to be sent
	 */
	public void sendMessageToPeers(Message message)
	{
		if (!message.isInternal() && isOnline())
			client.sendMessage(message);
	}

	public void sendMessage(Message message)
	{
		if (!message.isInternal() && isOnline())
			client.sendMessage(message);
		else
			currentStateInfo.recieveMessage(message);
	}

	@Override
	public void handleIncomingPacket(Client client, Object packet) {
		currentStateInfo.recieveMessage((Message) packet);
	}

	@Override
	public void handlerRegistered(Client client) {

	}

	public void setCurrentStateInfo(StateInfo currentStateInfo) {
		this.currentStateInfo = currentStateInfo;
		if (isOnline())
		{
			client.unregisterPacketHandler(this);
			client.registerPacketHandler(this);
		}
	}
}
