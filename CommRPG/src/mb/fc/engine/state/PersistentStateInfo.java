package mb.fc.engine.state;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.Transition;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.Message;
import mb.fc.engine.transition.MoveMapTransition;
import mb.fc.game.Camera;
import mb.fc.game.constants.Direction;
import mb.fc.game.persist.ClientProfile;
import mb.fc.game.persist.ClientProgress;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.LoadingScreenRenderer;
import mb.fc.loading.ResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.loading.LoadingState;
import mb.fc.network.TCPClient;
import mb.fc.network.TCPServer;
import mb.tcp.network.Client;
import mb.tcp.network.PacketHandler;

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
	private ResourceManager resourceManager;
	private String entranceLocation = null;
	private int cinematicID = 0;
	private int clientId;
	private TCPServer server = null;
	private TCPClient client = null;
	private StateInfo currentStateInfo;
	private transient boolean isFirstLoad = true;

	public PersistentStateInfo(ClientProfile clientProfile, ClientProgress clientProgress, 
			CommRPG game, Camera camera, GameContainer gc)
	{
		this.game = game;
		this.camera = camera;
		this.gc = (PaddedGameContainer) gc;
		this.clientProfile = clientProfile;
		this.clientProgress = clientProgress;
	}

	/********************/
	/* Map Management	*/
	/********************/
	public void loadMap(String mapData, String entrance)
	{	
		loadMap(mapData, entrance, null);
	}
	
	public void loadMap(String mapData, String entrance, Direction transitionDir)
	{	
		this.entranceLocation = entrance;

		cleanupStateAndLoadNext(mapData, CommRPG.STATE_GAME_TOWN, transitionDir);
	}

	private void cleanupStateAndLoadNext(String mapData, int nextState, Direction transitionDir) {
		gc.getInput().removeAllKeyListeners();

		getClientProgress().setMapData(mapData, false);

		if (transitionDir != null) {
			TownState townState = (TownState) getGame().getCurrentState();
			try {
				Image image = townState.getStateImageScreenshot(true);
				setLoadingInfo(mapData,
					(LoadableGameState) getGame().getState(nextState), getResourceManager(),
					image,
					new MoveMapTransition(townState, transitionDir));
			} catch (SlickException e) {
				e.printStackTrace();
				setLoadingInfo(mapData,
						(LoadableGameState) getGame().getState(nextState), getResourceManager(), null, null);
			}
		} else {
			setLoadingInfo(mapData, (LoadableGameState) getGame().getState(nextState), 
					getResourceManager(), null, null);
		}
		
		// Do not fade out when coming from a cinematic or if the map is going to 'slide' out
		if (getGame().getCurrentStateID() == CommRPG.STATE_GAME_CINEMATIC ||
				transitionDir != null)
			getGame().enterState(CommRPG.STATE_GAME_LOADING);
		else
			getGame().enterState(CommRPG.STATE_GAME_LOADING, 
					// Do not fade out when coming from a cinematic
					new FadeOutTransition(Color.black, 250), new EmptyTransition());
		/*
		getGame().enterState(CommRPG.STATE_GAME_LOADING, 
				// Do not fade out when coming from a cinematic
				(getGame().getCurrentStateID() != CommRPG.STATE_GAME_CINEMATIC) ? new FadeOutTransition(Color.black, 250) :
					new EmptyTransition(), new EmptyTransition());
					*/
	}
	
	/**
	 * Sets the loading state to use existing resources that are already contained in the resource manager
	 * and to just load the specified text and map. It then transitions into the specified next state.
	 *
	 * @param text The text file to load
	 * @param nextState The next state that should be entered once the loading is done
	 * @param fcResourceManager Existing resource manager that contains all resources already loaded
	 * @param intermediateImage An image to show for the loading screen background
	 * @param transition the Transition that should be used to load in to the next state. A value of 
	 * 			null will use the default transition
	 */
	private void setLoadingInfo(String text, LoadableGameState nextState,
			ResourceManager fcResourceManager, Image intermediateImage, Transition transition)
	{
		// If it's not the first load then we don't want to reload the resources
		((LoadingState) game.getState(CommRPG.STATE_GAME_LOADING)).setLoadingInfo(text, true, isFirstLoad,
			(fcResourceManager == null ? new ResourceManager() : fcResourceManager),
				nextState,
					new LoadingScreenRenderer(gc), intermediateImage, transition);
		isFirstLoad = false;
	}

	
	public void loadMapFromSave()
	{
		loadMap(clientProgress.getMapData(), null);
	}

	public void loadBattle(String mapData, String entrance, int battleBGIndex)
	{
		this.entranceLocation = entrance;

		cleanupStateAndLoadNext(mapData, CommRPG.STATE_GAME_BATTLE, null);
	}

	public void loadCinematic(String mapData, int cinematicID)
	{
		this.cinematicID = cinematicID;		
		
		cleanupStateAndLoadNext(mapData, CommRPG.STATE_GAME_CINEMATIC, null);
	}

	public Camera getCamera() {
		return camera;
	}

	public PaddedGameContainer getGc() {
		return gc;
	}

	public void setQuestStatus(String id, boolean completed)
	{
		this.clientProgress.setQuestStatus(id, completed);
	}

	public boolean isQuestComplete(String questId)
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

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
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
