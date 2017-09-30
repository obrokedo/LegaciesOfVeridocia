 package mb.fc.engine.state;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

import org.newdawn.slick.util.Log;

import mb.fc.engine.message.BooleanMessage;
import mb.fc.engine.message.LoadMapMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.message.StringMessage;
import mb.fc.game.Camera;
import mb.fc.game.exception.BadResourceException;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.hudmenu.Panel.PanelType;
import mb.fc.game.hudmenu.WaitPanel;
import mb.fc.game.input.FCInput;
import mb.fc.game.listener.KeyboardListener;
import mb.fc.game.listener.MouseListener;
import mb.fc.game.manager.Manager;
import mb.fc.game.menu.Menu;
import mb.fc.game.persist.ClientProfile;
import mb.fc.game.persist.ClientProgress;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.sprite.SpriteZComparator;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.map.Map;
import mb.fc.map.MapObject;

/**
 * Central container to hold all information that is associated with a given state. AKA "The Dumping Ground"
 *
 * @author Broked
 *
 */
public class StateInfo
{
	private static final SpriteZComparator SPRITE_Z_ORDER_COMPARATOR = new SpriteZComparator();

	/*************************************************************/
	/* These values are created specifically for this state info */
	/*************************************************************/
	private ArrayList<Manager> managers;
	private ArrayList<Message> messagesToProcess;
	private ArrayList<Message> newMessages;
	private boolean initialized = false;
	private boolean isCombat = false;
	private boolean isCinematic = false;
	private boolean isWaiting = false;
	private PersistentStateInfo psi;

	// These values need to be reinitialized each time a map is loaded
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
	private ArrayList<CombatSprite> heroes;
	private boolean showAttackCinematic = false;

	public StateInfo(PersistentStateInfo psi, boolean isCombat, boolean isCinematic)
	{
		this.psi = psi;
		this.isCombat = isCombat;
		this.isCinematic = isCinematic;
		sprites = new ArrayList<Sprite>();
		combatSprites = new ArrayList<CombatSprite>();
		panels = new ArrayList<Panel>();
		menus = new ArrayList<Menu>();
		mouseListeners = new ArrayList<MouseListener>();
		keyboardListeners = new Stack<KeyboardListener>();
		this.managers = new ArrayList<Manager>();
		this.messagesToProcess = new ArrayList<Message>();
		this.newMessages = new ArrayList<Message>();
		this.fcInput = new FCInput();
		this.heroes = new ArrayList<>();
	}

	/************************/
	/* State Initialization */
	/************************/
	public void initState()
	{
		Log.debug("Initialize State");

		psi.setCurrentStateInfo(this);

		this.initialized = false;
		setWaiting();
		
		psi.getClientProfile().initialize(getResourceManager());

		initializeSystems();

		if (isCombat)
			this.heroes.addAll(getClientProfile().getHeroesInParty());
		else
			this.heroes.add(getClientProfile().getMainCharacter());
		
		/*
		 * If the first time into the game we start with a battle then the
		 * client progress will say that we are in battle, but we don't actually have
		 * any battle information yet (current turn, sprites...), so check
		 * to make sure we're not in this case which would cause us to avoid
		 * loading.
		 * 
		 * Technically we could use "isCombat" variable as well, but we'd still need
		 * to check the current turn
		 */
		boolean isBattleInitialized = false;
		if (psi.getClientProgress().isBattle() && psi.getClientProgress().getCurrentTurn() != null)
		{
			Log.debug("Initializing battle from load");
			isBattleInitialized = true;
			/*
			 * Set the combat sprites and current sprite for this battle. Although it may be
			 * reasonably assumed that the CombatSprites stored in the client progress would
			 * be the same (in memory) as the CombatSprites in the client profile, this is not
			 * the case. 
			 */
			this.addAllCombatSprites(getClientProgress().getBattleSprites(this));
			for (CombatSprite cs : this.combatSprites)
			{
				if (cs.getId() == psi.getClientProgress().getCurrentTurn())
				{
					this.currentSprite = cs;
					break;
				}
			}
			
			/*
			 * Clear these values here so that when we load a battle
			 * we don't think that we're loading from a mid-battle save
			 */
			getClientProgress().setCurrentTurn(null);
			getClientProgress().setBattleSprites(null);
		}
		else if (isCombat)
		{
			Log.debug("Initializing NEW battle");
		}

		sendMessage(new BooleanMessage(MessageType.INTIIALIZE, isBattleInitialized));

		if (this.getClientProgress().getRetriggerablesByMap() != null)
			for (Integer triggerId : this.getClientProgress().getRetriggerablesByMap())
				getResourceManager().getTriggerEventById(triggerId).perform(this);

		// If this isn't a cinematic check MapLoad conditions
		if (!isCinematic)
		{
			this.getResourceManager().checkTriggerCondtions(null, false, true, this);
		}
		
		psi.getGc().getInput().addKeyListener(fcInput);

		if (psi.isOnline())
			sendMessage(MessageType.WAIT);
		else
		{
			initialized = true;
			if (isCombat)
			{
				// If the battle initialized just restart the current turn
				if (isBattleInitialized)
				{
					sendMessage(new SpriteContextMessage(MessageType.COMBATANT_TURN, currentSprite), true);
				}
				else
				{
					// Start the whole battle
					sendMessage(MessageType.NEXT_TURN);
				}
			}
		}
	}

	private void initializeSystems()
	{
		Log.debug("Initialize Systems");

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
		this.getCurrentMap().initializeObjects(isCombat, this);
	}

	public void registerManager(Manager m)
	{
		managers.add(m);
	}

	public String getEntranceLocation() {
		return psi.getEntranceLocation();
	}

	/************************/
	/* Message Management	*/
	/************************/
	public void sendMessage(Message message)
	{
		if (message.isImmediate()) {
			sendMessageImpl(message);
			// Send the message to our peers, these messages will not be received locally
			psi.sendMessageToPeers(message);
		}
		else
			psi.sendMessage(message);
	}

	public void sendMessage(Message message, boolean ifHost)
	{
		if (psi.isHost())
			psi.sendMessage(message);

		/*
		if (message.isImmediate())
			sendMessageImpl(message);
		else
			newMessages.add(message);
			*/
	}

	/**
	 * Covenience method to send a generic message that requires no parameters aside from the message
	 *
	 * @param messageType The type of the message to be sent
	 */
	public void sendMessage(MessageType messageType)
	{
		sendMessage(new Message(messageType));
	}

	public void sendMessage(MessageType messageType, boolean ifHost)
	{
		Message message = new Message(messageType);
		sendMessage(message, ifHost);
	}

	private void sendMessageImpl(Message message)
	{
		for (Manager m : managers)
			m.recieveMessage(message);
	}

	public void recieveMessage(Message message)
	{
		newMessages.add(message);
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
				case LOAD_MAP:
					sendMessage(MessageType.PAUSE_MUSIC);

					LoadMapMessage lmm = (LoadMapMessage) m;
					psi.loadMap(lmm.getMapData(), lmm.getLocation(), lmm.getTransDir());
					break MESSAGES;
				case START_BATTLE:
					sendMessage(MessageType.PAUSE_MUSIC);

					LoadMapMessage lmb = (LoadMapMessage) m;
					psi.loadBattle(lmb.getMapData(), lmb.getLocation(), lmb.getBattleBG());
					break MESSAGES;
				case LOAD_CINEMATIC:
					sendMessage(MessageType.PAUSE_MUSIC);

					LoadMapMessage lmc = (LoadMapMessage) m;
					psi.loadCinematic(lmc.getMapData(), lmc.getCinematicID());
					break;
				case SAVE:
					getClientProgress().setInTownLocation(new Point((int) currentSprite.getLocX(), (int) currentSprite.getLocY()));
					getClientProfile().serializeToFile();
					getClientProgress().serializeToFile();
					break;
				case SAVE_BATTLE:
					getClientProgress().setBattle(true);
					getClientProgress().setBattleSprites(combatSprites);
					getClientProgress().setCurrentTurn(currentSprite);
					getClientProfile().serializeToFile();
					getClientProgress().serializeToFile();
					System.exit(0);
					break;
				case COMPLETE_QUEST:
					this.setQuestStatus(((StringMessage) m).getString(), true);
					break;
				case UNCOMPLETE_QUEST:
					this.setQuestStatus(((StringMessage) m).getString(), false);
					break;
				case CONTINUE:
					if (!initialized)
					{
						initialized = true;
						if (isCombat)
							// Start the whole battle
							sendMessage(MessageType.NEXT_TURN);
					}
					this.removePanel(PanelType.PANEL_WAIT);
					isWaiting = false;
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
	public void addSingleInstancePanel(Panel panel)
	{
		for (Panel m : panels)
		{
			if (m.getPanelType() == panel.getPanelType())
				return;
		}

		panels.add(panel);
		panel.panelAdded(this);
	}

	public void addPanel(Panel panel)
	{
		panels.add(panel);
		panel.panelAdded(this);
	}

	public void addMenu(Menu menu)
	{
		menus.add(menu);
		menu.panelAdded(this);
	}

	public Menu getTopMenu()
	{
		if (menus.size() > 0)
			return menus.get(menus.size() - 1);
		return null;
	}

	public void removeTopMenu()
	{
		menus.remove(menus.size() - 1).panelRemoved(this);
	}
	
	public void removeMenu(Menu menu) {
		menus.remove(menu);
	}

	public boolean arePanelsDisplayed()
	{
		return panels.size() > 0;
	}

	public boolean isMenuDisplayed(PanelType panelType)
	{
		for (Menu m : menus)
			if (m.getPanelType() == panelType)
				return true;
		return false;
	}

	public boolean areMenusDisplayed()
	{
		return menus.size() > 0;
	}

	public void removePanel(Panel panel)
	{
		if (panels.remove(panel))
			panel.panelRemoved(this);
	}

	public void removePanel(PanelType panelType)
	{
		for (Panel m : panels)
			if (m.getPanelType() == panelType)
			{
				panels.remove(m);
				m.panelRemoved(this);
				break;
			}
	}

	public void addSingleInstanceMenu(Menu menu)
	{
		for (Panel m : menus)
		{
			if (m.getPanelType() == menu.getPanelType())
				return;
		}

		menus.add(menu);
		menu.panelAdded(this);
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
		if (keyboardListeners.size() > 0)
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
	public void checkTriggersMovement(int mapX, int mapY, boolean immediate)
	{
		for (MapObject mo : this.getCurrentMap().getMapObjects())
		{
			if (mo.contains(mapX, mapY))
			{
				this.getResourceManager().checkTriggerCondtions(mo.getName(), immediate, false, this);
			}
		}
		this.getResourceManager().checkTriggerCondtions(null, false, false, this);

		this.getResourceManager().getMap().checkRoofs(mapX, mapY);
	}
	
	public void checkTriggersEnemyDeath(CombatSprite cs)
	{
		getResourceManager().checkTriggerCondtions(null, false, false, this);
	}
	
	public void checkTriggersHeroDeath(CombatSprite cs)
	{
		getResourceManager().checkTriggerCondtions(null, false, false, this);
	}

	public void setQuestStatus(String id, boolean completed)
	{
		psi.setQuestStatus(id, completed);
	}

	public boolean isQuestComplete(String questId)
	{
		return psi.isQuestComplete(questId);
	}

	/*********************/
	/* Convience Methods */
	/*********************/
	public int getTileWidth()
	{
		return psi.getResourceManager().getMap().getTileEffectiveWidth();
		// return psi.getResourceManager().getMap().getTileWidth();
	}

	public int getTileHeight()
	{
		return psi.getResourceManager().getMap().getTileEffectiveHeight();
		// return psi.getResourceManager().getMap().getTileHeight();
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
		{
			if ((targetsHero == null || s.isHero() == targetsHero) && 
					s.getTileX() == tileX && s.getTileY() == tileY && s.getCurrentHP() > 0)
				return s;
		}
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

	public CombatSprite getHeroById(int id)
	{
		for (CombatSprite cs : getAllHeroes())
			if (cs.getId() == id)
				return cs;
		throw new BadResourceException("Attempted to access a hero with ID: " + id + " who does not exist");
	}
	
	public CombatSprite getCombatantById(int id)
	{
		for (CombatSprite cs : combatSprites)
			if (cs.getId() == id)
				return cs;
		return null;
	}
	
	public CombatSprite getEnemyCombatSpriteByUnitId(int unitId) {
		for (CombatSprite cs : combatSprites) {
			if (!cs.isHero() && cs.getUniqueEnemyId() == unitId) {
				return cs;
			}
		}
		return null;
	}
	
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

	public void sortSprites()
	{
		Collections.sort(sprites, SPRITE_Z_ORDER_COMPARATOR);
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
		sprites.remove(cs);
		combatSprites.remove(cs);
	}

	public void addAllCombatSprites(Collection<CombatSprite> ss)
	{
		sprites.addAll(ss);
		combatSprites.addAll(ss);
	}
	
	public void addAllCombatSprites(Iterable<CombatSprite> css)
	{
		for (CombatSprite ss : css)
		{
			sprites.add(ss);
			combatSprites.add(ss);
		}
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
		return psi.getCamera();
	}

	public PaddedGameContainer getPaddedGameContainer() {
		return psi.getGc();
	}

	public boolean isCombat() {
		return isCombat;
	}

	public void setResourceManager(FCResourceManager resourceManager) {
		psi.setResourceManager(resourceManager);
	}
	
	public Iterable<CombatSprite> getAllHeroes() {
		return this.psi.getClientProfile().getHeroes();
	}

	public Iterable<CombatSprite> getHeroesInState() {
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

	public PersistentStateInfo getPersistentStateInfo() {
		return psi;
	}
	
	public boolean isInCinematicState() {
		return isCinematic;
	}

	public boolean isWaiting() {
		return isWaiting;
	}

	public void setWaiting() {
		if (psi.isOnline())
		{
			this.addPanel(new WaitPanel());
			this.isWaiting = true;
		}
	}
}
