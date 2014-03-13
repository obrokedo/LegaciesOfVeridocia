 package mb.fc.engine.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

import mb.fc.engine.ForsakenChampions;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.LoadMapMessage;
import mb.fc.engine.message.Message;
import mb.fc.game.Camera;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.listener.KeyboardListener;
import mb.fc.game.listener.MouseListener;
import mb.fc.game.manager.Manager;
import mb.fc.game.menu.Menu;
import mb.fc.game.persist.ClientProfile;
import mb.fc.game.persist.ClientProgress;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.trigger.Trigger;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.map.Map;
import mb.fc.map.MapObject;
import mb.fc.resource.FCResourceManager;
import mb.gl2.loading.LoadableGameState;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class StateInfo
{
	/*************************************************************/
	/* These values are created specifically for this state info */
	/*************************************************************/	
	private ArrayList<Manager> managers;
	private ArrayList<Message> messagesToProcess;
	private ArrayList<Message> newMessages;
	private boolean initialized = false;		
	private boolean isCombat = false;
	private PersistentStateInfo psi;
	
	// These values need to be reinitialized each time a map is loaded
	private ArrayList<Trigger> triggers;
	private ArrayList<Sprite> sprites;
	private ArrayList<CombatSprite> combatSprites;
	private ArrayList<Panel> panels;
	private ArrayList<Menu> menus;
	private ArrayList<MouseListener> mouseListeners;
	private Stack<KeyboardListener> keyboardListeners;
	private FCInput fcInput;
	
	private CombatSprite currentSprite;	
	
	private long inputDelay = 0;
	
	/**************************************************/
	/* These values are retrieved from the persistent */
	/* state info									  */
	/**************************************************/	 
	private Camera camera;	
	private FCGameContainer gc;
	private Graphics graphics;
	private ArrayList<CombatSprite> heroes;
	private boolean showAttackCinematic = false;
	private String entranceLocation = "priest";
	private String currentMap;
	
	public StateInfo(PersistentStateInfo psi, boolean isCombat)
	{
		this.psi = psi;
		this.isCombat = isCombat;
		sprites = new ArrayList<Sprite>();
		combatSprites = new ArrayList<CombatSprite>();
		panels = new ArrayList<Panel>();
		menus = new ArrayList<Menu>();
		mouseListeners = new ArrayList<MouseListener>();
		keyboardListeners = new Stack<KeyboardListener>();
		this.managers = new ArrayList<Manager>();
		this.messagesToProcess = new ArrayList<Message>();		
		this.newMessages = new ArrayList<Message>();
		this.triggers = new ArrayList<Trigger>();
		this.fcInput = new FCInput();
		
		camera = psi.getCamera();
		gc = psi.getGc();
		graphics = psi.getGraphics();
		heroes = psi.getHeroes();
		
		this.entranceLocation = psi.getClientProgress().getLocation();
		this.currentMap = psi.getClientProgress().getMap();
	}
	
	/************************/
	/* State Initialization */
	/************************/
	public void initState()
	{
		this.initialized = false;
		initializeSystems();
		if (isCombat)
			this.heroes.addAll(getClientProfile().getHeroes());
		else
			this.heroes.addAll(getClientProfile().getLeaderList());
		sendMessage(Message.MESSAGE_INTIIALIZE);
		
		if (this.getClientProgress().getRetriggerablesByMap() != null)
			for (Integer triggerId : this.getClientProgress().getRetriggerablesByMap())
				getResourceManager().getTriggerEventById(triggerId).perform(this);
		
		fcInput.clear();
		gc.getInput().addKeyListener(fcInput);
		
		this.initialized = true;
		
		if (isCombat)
			// Start the whole battle
			sendMessage(Message.MESSAGE_NEXT_TURN);
	}
	
	private void initializeSystems()
	{
		triggers.clear();
		sprites.clear();
		combatSprites.clear();
		panels.clear();
		menus.clear();
		heroes.clear();
		mouseListeners.clear();
		keyboardListeners.clear();
		messagesToProcess.clear();
		newMessages.clear();
		
		
		for (Manager m : managers)
			m.initializeSystem(this);
		initializeMapObjects();	
		
		this.currentMap = psi.getClientProgress().getMap();
		
		psi.getResourceManager().getTriggerEventById(0).perform(this);
	}
	
	private void initializeMapObjects()
	{
		for (MapObject mo : getResourceManager().getMap().getMapObjects())
		{
			if (!isCombat && mo.getKey().equalsIgnoreCase("trigger"))
			{
				triggers.add(new Trigger(this, mo));
			}
			else if (isCombat && mo.getKey().equalsIgnoreCase("battletrigger"))
			{
				triggers.add(new Trigger(this, mo));
			}
		}
	}
	
	public void registerManager(Manager m)
	{
		managers.add(m);
	}
	
	/********************/
	/* Map Management	*/
	/********************/	
	private void loadMap(String map)
	{				
		gc.getInput().removeAllKeyListeners();
		
		psi.getClientProgress().setMap(map);		
		
		psi.getGame().setLoadingInfo(map, map,
				(LoadableGameState) psi.getGame().getState(ForsakenChampions.STATE_GAME_TOWN),
					psi.getResourceManager());
		psi.getGame().enterState(ForsakenChampions.STATE_GAME_LOADING, new FadeOutTransition(Color.black, 250), new EmptyTransition());
	}
	
	private void loadBattle(String text, String map)
	{			
		gc.getInput().removeAllKeyListeners();
		
		psi.getGame().setLoadingInfo(text, map, 
				(LoadableGameState) psi.getGame().getState(ForsakenChampions.STATE_GAME_BATTLE),
					psi.getResourceManager());
		psi.getGame().enterState(ForsakenChampions.STATE_GAME_LOADING, new FadeOutTransition(Color.black, 250), new EmptyTransition());
	}	
	
	public String getEntranceLocation() {
		return entranceLocation;
	}
	
	/************************/
	/* Message Management	*/
	/************************/
	public void sendMessage(Message message)
	{		
		if (message.isImmediate())
			sendMessageImpl(message);
		else
			newMessages.add(message);
	}
	
	/**
	 * Covenience method to send a generic message that requires no parameters aside from the message
	 * 
	 * @param messageType The type of the message to be sent
	 */
	public void sendMessage(int messageType)
	{		
		Message message = new Message(messageType);
		if (message.isImmediate())
			sendMessageImpl(message);
		else
			newMessages.add(message);
	}
	
	private void sendMessageImpl(Message message)
	{
		for (Manager m : managers)
			m.recieveMessage(message);
	}
	
	public void processMessages()
	{
		messagesToProcess.addAll(newMessages);
		newMessages.clear();
		MESSAGES: for (int i = 0; i < messagesToProcess.size(); i = 0)
		{
			Message m = messagesToProcess.remove(i);
			switch (m.getMessageType())
			{			
				case Message.MESSAGE_LOAD_MAP:
					LoadMapMessage lmm = (LoadMapMessage) m;
					entranceLocation = lmm.getLocation();
					loadMap(lmm.getMap());
					break MESSAGES;
				case Message.MESSAGE_START_BATTLE:
					LoadMapMessage lmb = (LoadMapMessage) m;
					loadBattle(lmb.getBattle(), lmb.getMap());
					break MESSAGES;
				case Message.MESSAGE_SAVE:
					getClientProfile().serializeToFile();
					getClientProgress().serializeToFile(currentMap, "priest");
					break;
				case Message.MESSAGE_COMPLETE_QUEST:
					this.setQuestComplete(((IntMessage) m).getValue());
					break;
				default:						
					sendMessageImpl(m);
					break;
			}				
		}
	}
	
	/********************************/
	/* Panel and Menu management	*/
	/********************************/
	public void addPanel(Panel panel)
	{
		panels.add(panel);
	}
	
	public void addMenu(Menu menu)
	{
		menus.add(menu);
	}
	
	public Menu getTopMenu()
	{
		return menus.get(menus.size() - 1);
	}
	
	public void removeTopMenu()
	{
		menus.remove(menus.size() - 1);
	}
	
	public boolean arePanelsDisplayed()
	{
		return panels.size() > 0;
	}
	
	public boolean areMenusDisplayed()
	{
		return menus.size() > 0;
	}
	
	public void removePanel(Panel panel)
	{
		panels.remove(panel);
	}
	
	public void removePanel(int panelType)
	{
		for (Panel m : panels)
			if (m.getPanelType() == panelType)
			{
				panels.remove(m);
				break;
			}
	}
	
	public void removeMenu(int menuType)
	{
		for (Menu m : menus)
			if (m.getPanelType() == menuType)
			{
				menus.remove(m);
				break;
			}
	}
	
	public void removeMenu(Menu menu)
	{
		menus.remove(menu);
	}
	
	public void addSingleInstanceMenu(Menu menu)
	{
		for (Panel m : menus)
		{
			if (m.getPanelType() == menu.getPanelType())
				return;
		}
		
		menus.add(menu);
	}
	
	public Iterable<Panel> getPanels()
	{
		return panels;
	}
	
	public Iterable<Menu> getMenus()
	{
		return menus;
	}
	
	/****************************************/
	/* Manage Mouse and Keyboard Listeners	*/ 
	/****************************************/
	public void registerMouseListener(MouseListener ml)
	{
		mouseListeners.add(ml);
		Collections.sort(mouseListeners, new MouseListenerComparator());
	}
	
	public void unregisterMouseListener(MouseListener ml)
	{
		mouseListeners.remove(ml);
	}

	public class MouseListenerComparator implements Comparator<MouseListener>
	{
		@Override
		public int compare(MouseListener m1, MouseListener m2) {
			return m1.getZOrder() - m2.getZOrder();
		}
	}
	
	public int getMouseListenersSize()
	{
		return mouseListeners.size();
	}
	
	public MouseListener getMouseListener(int index)
	{
		return mouseListeners.get(index);
	}
	
	public KeyboardListener getKeyboardListener()
	{
		if (keyboardListeners.empty())
			return null;
		return keyboardListeners.peek();
	}
	
	public void removeKeyboardListener()
	{
		keyboardListeners.pop();
	}
	
	public void removeKeyboardListeners()
	{
		keyboardListeners.clear();
	}
	
	public void addKeyboardListener(KeyboardListener kl)
	{
		keyboardListeners.add(kl);
	}
	
	/****************************/
	/* Plot Progression Methods	*/
	/****************************/
	public void checkTriggers(int mapX, int mapY)
	{
		for (Trigger trigger : triggers)
		{
			if (trigger.contains(mapX, mapY))
			{
				trigger.perform(this);
			}
		}
	}
	
	public void setQuestComplete(int id)
	{
		psi.setQuestComplete(id);
	}
	
	public boolean isQuestComplete(int questId)
	{
		return psi.isQuestCompelte(questId);
	}
	
	/*********************/
	/* Convience Methods */
	/*********************/
	public int getTileWidth()
	{
		return psi.getResourceManager().getMap().getTileWidth();
	}
	
	public int getTileHeight()
	{
		return psi.getResourceManager().getMap().getTileHeight();
	}
	
	public Map getCurrentMap()
	{
		return psi.getResourceManager().getMap();
	}
	
	/**
	 * Gets the CombatSprite that is located at the specified tile location.
	 * 
	 * @param tileX The x location of the specified tile to check
	 * @param tileY The y location of the specified tile to check
	 * @param targetsHero A boolean indicating whether we should only return a hero or enemy. If true only a hero
	 * 			will be returned at this location. If false only an enemy will be returned at this location. If null
	 * 			any CombatSprite will be returned at this location
	 * @return The CombatSprite at this location, null if there is not one at this location
	 */
	public CombatSprite getCombatSpriteAtTile(int tileX, int tileY, Boolean targetsHero)
	{
		for (CombatSprite s : combatSprites)
			if ((targetsHero == null || s.isHero() == targetsHero) && s.getTileX() == tileX && s.getTileY() == tileY)
				return s;		
		return null;
	}
	
	/**
	 * Gets the CombatSprite that is located at the specified map location.
	 * 
	 * @param mapX The x location on the map to check
	 * @param mapY The y location on the map to check
	 * @param targetsHero A boolean indicating whether we should only return a hero or enemy. If true only a hero
	 * 			will be returned at this location. If false only an enemy will be returned at this location. If null
	 * 			any CombatSprite will be returned at this location
	 * @return The CombatSprite at this location, null if there is not one at this location
	 */
	public CombatSprite getCombatSpriteAtMapLocation(int mapX, int mapY, Boolean targetsHero)
	{
		for (CombatSprite s : combatSprites)
			if ((targetsHero == null || s.isHero() == targetsHero) && s.getLocX() == mapX && s.getLocY() == mapY)
				return s;		
		return null;
	}
	
	/****************************/
	/* General Mutator Methods	*/
	/****************************/
	// Special care needs to be taken to ensure that the sprites and combat sprites are in sync with each other.
	// Thus the actual lists should never be directly exposed
		
	public Iterable<Sprite> getSprites() {
		return sprites;
	}
	
	public Iterable<CombatSprite> getCombatSprites() {
		return combatSprites;
	}
	
	/**
	 * Returns an iterator that allows traversal and modification of the sprites lists. NOTE:  When deleting
	 * a sprite via this iterator, if the sprite is a CombatSprite then you must also call the
	 * removeCombatSprite method to ensure that the combat sprite is also removed from that list as well.
	 * 
	 * @return An iterator for the sprites list
	 */
	public Iterator<Sprite> getSpriteIterator()
	{
		return sprites.iterator();
	}

	public void addSprite(Sprite s)
	{
		sprites.add(s);
		if (s.getSpriteType() == Sprite.TYPE_COMBAT)
			combatSprites.add((CombatSprite) s);
	}
	
	// I hate that this has to be here, but I don't have a good way of knowing when a sprite has been removed via an iterator
	// so I need to have it removed programatically
	
	public void removeCombatSprite(CombatSprite cs)
	{
		combatSprites.remove(cs);
	}
	
	public void addAllCombatSprites(Collection<CombatSprite> ss)
	{
		sprites.addAll(ss);
		combatSprites.addAll(ss);
	}
	
	public void addAllSprites(Collection<Sprite> ss)
	{
		sprites.addAll(ss);
		
		for (Sprite s : ss)
			if (s.getSpriteType() == Sprite.TYPE_COMBAT)
				combatSprites.add((CombatSprite) s);
	}

	public FCInput getInput() {
		return fcInput;
	}

	public FCResourceManager getResourceManager() {
		return psi.getResourceManager();
	}

	public ArrayList<Manager> getManagers() {
		return managers;
	}

	public boolean isInitialized() {
		return initialized;
	}
	
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
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
	
	public boolean isCombat() {
		return isCombat;
	}

	public void setResourceManager(FCResourceManager resourceManager) {
		psi.setResourceManager(resourceManager);
	}

	public ArrayList<CombatSprite> getHeroes() {
		return heroes;
	}

	public long getInputDelay() {
		return inputDelay;
	}

	public void setInputDelay(long inputDelay) {
		this.inputDelay = inputDelay;
	}
	
	public ClientProfile getClientProfile() {
		return psi.getClientProfile();
	}
	
	public ClientProgress getClientProgress() {
		return psi.getClientProgress();
	}

	public boolean isShowAttackCinematic() {
		return showAttackCinematic;
	}

	public void setShowAttackCinematic(boolean showAttackCinematic) {
		this.showAttackCinematic = showAttackCinematic;
	}
		
	public CombatSprite getCurrentSprite() {
		return currentSprite;
	}

	public void setCurrentSprite(CombatSprite currentSprite) {
		this.currentSprite = currentSprite;
	}	
}
