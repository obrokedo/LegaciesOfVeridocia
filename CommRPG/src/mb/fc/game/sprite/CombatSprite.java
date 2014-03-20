package mb.fc.game.sprite;

import java.util.ArrayList;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.ai.AI;
import mb.fc.game.ai.ClericAI;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.hudmenu.SpriteContextPanel;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.resource.ItemResource;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.utils.AnimSprite;
import mb.fc.utils.Animation;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class CombatSprite extends AnimatedSprite
{
	private static final long serialVersionUID = 1L;
	
	public static final int MOVEMENT_WALKING = 0;
	public static final int MOVEMENT_HORSES_CENTAURS = 1;
	public static final int MOVEMENT_ANIMALS_BEASTMEN = 2;
	public static final int MOVEMENT_MECHANICAL = 3;
	public static final int MOVEMENT_FLYING = 4;
	public static final int MOVEMENT_HOVERING = 5;
	public static final int MOVEMENT_SWIMMING = 6;
	public static final int MOVEMENT_ELVES = 7;	
	
	private transient Color fadeColor = new Color(255, 255, 255, 255);
	private final transient static Color SHADOW_COLOR = new Color(0, 0, 0, 100);
	
	private int currentHP, maxHP, 
				currentMP, maxMP, 
				currentInit, 
				currentSpeed, maxSpeed, 
				currentMove, maxMove, 
				currentAttack, maxAttack,
				currentDefense, maxDefense,
				level, exp;
	
	private transient AI ai;	
	private transient Image portraitImage;
	
	private boolean isHero = false;
	private boolean isLeader = false;
	private boolean isPromoted = false;
	
	// This value provides a mean of differentiating between multiple enemies of the same name,
	// in addition this value can be user speified for enemies so that they may be the target
	// of triggers
	private int uniqueEnemyId = -1;
	private ArrayList<KnownSpell> spells;
	private ArrayList<Item> items;
	private ArrayList<Boolean> equipped;
	private int[] usuableWeapons;
	private int[] usuableArmor;	
	private HeroProgression heroProgression;
	private int movementType;	
	private int portraitIndex;
	private int kills;
	private int defeat;
	
	/**
	 * A boolean indicating whether the combat sprite dodges or blocks attacks, dodges if true, blocks if false
	 */
	private boolean dodges;
	
	public CombatSprite(boolean isLeader,
			String name, String imageName, int hp, int mp, int attack, int defense, int speed, int move, 
				int movementType, int level, int id, int portraitIndex, ArrayList<KnownSpell> spells) 
	{
		this(isLeader, name, imageName, null, hp, mp, attack, defense, speed, move, movementType, level, 0, portraitIndex, spells);
		this.uniqueEnemyId = id;
		this.ai = new ClericAI(AI.APPROACH_REACTIVE);	
		this.isHero = false;
	}
	
	public CombatSprite(boolean isLeader,
			String name, String imageName, HeroProgression heroProgression, int hp, int mp, int attack,
			int defense, int speed, int move, int movementType, int level, int exp, int portraitIndex, ArrayList<KnownSpell> spells) 
	{
		super(0, 0, imageName);
		
		currentHP = hp;
		maxHP = hp;
		currentMP = mp;
		maxMP = mp;
		maxSpeed = speed;
		currentSpeed = speed;
		currentMove = move;
		maxMove = move;
		this.movementType = movementType; 
		currentAttack = attack;
		maxAttack = attack;
		currentDefense = defense;
		maxDefense = defense;
		dodges = true;
		
		this.level = level;
		this.exp = exp;
		
		this.isHero = true;
		this.isLeader = isLeader;
		this.name = name;
		this.imageName = imageName;
		this.items = new ArrayList<Item>();
		this.equipped = new ArrayList<Boolean>();
		this.heroProgression = heroProgression;
		this.portraitIndex = portraitIndex;
		if (heroProgression != null)
		{
			if (isPromoted)
			{
				this.usuableWeapons = heroProgression.getUnpromotedProgression().getUsuableWeapons();
				this.usuableArmor = heroProgression.getUnpromotedProgression().getUsuableArmor();
			}
			else
			{
				this.usuableWeapons = heroProgression.getPromotedProgression().getUsuableWeapons();
				this.usuableArmor = heroProgression.getPromotedProgression().getUsuableArmor();
			}
		}
		this.spells = spells;
		
		this.spriteType = Sprite.TYPE_COMBAT;
	}
	
	public void initializeSprite(StateInfo stateInfo)
	{	
		super.initializeSprite(stateInfo);
		
		if (isHero)
		{
			super.setLocX(-1);
			super.setLocY(-1);
		}		
		
		if (portraitIndex != -1)
			portraitImage = stateInfo.getResourceManager().getSpriteSheets().get("portraits").getSprite(portraitIndex, 0);			
		
		if (spells != null && spells.size() > 0)
		{
			for (KnownSpell sd : spells)
				sd.initializeFromLoad(stateInfo);
		}
		
		int itemsSize = items.size();
		for (int i = 0; i < itemsSize; i++)
		{
			Item item = ItemResource.getItem(items.get(0).getItemId(), stateInfo);			
			items.add(item);			
			items.remove(0);
		}
		
		this.currentAttack = this.maxAttack;
		this.currentDefense = this.maxDefense;
		this.currentSpeed = this.maxSpeed;
		if (currentHP > 0)
		{
			this.currentHP = this.maxHP;
			this.currentMP = this.maxMP;
		}
		else
		{
			currentHP = 0;
			currentMP = 0;
		}
		
		this.currentMove = this.maxMove;
		
		fadeColor = new Color(255, 255, 255, 255);
	}

	@Override
	public void update() 
	{
		super.update();
		
		if (currentHP <= 0)
		{
			currentHP -= 25;
			fadeColor.a = (255 + currentHP) / 255.0f;
		}
	}
	
	@Override
	public void render(Camera camera, Graphics graphics, FCGameContainer cont) 
	{
		for (AnimSprite as : currentAnim.frames.get(imageIndex).sprites)
		{
			
			Image i = (spriteAnims.getImageAtIndex(as.imageIndex)).getFlippedCopy(false, true); 
			i.drawSheared(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(), 
					this.getLocY() - camera.getLocationY() + i.getHeight() - 5, -10, 0, SHADOW_COLOR);
					
			
			
			graphics.drawImage(spriteAnims.getImageAtIndex(as.imageIndex), this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(), 
					this.getLocY() - camera.getLocationY(), fadeColor);
					
			/*
			graphics.drawImage(spriteAnims.getImageAtIndex(as.imageIndex), this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(), 
					this.getLocY() - camera.getLocationY(), fadeColor);
			*/
		}
	}
	
	/************************/
	/* Handle item stuff	*/
	/************************/
	public Item getItem(int i) {
		return items.get(i);
	}
	
	public int getItemsSize() {
		return items.size();
	}
	
	public void removeItem(Item item)
	{
		int indexOf = items.indexOf(item);
		items.remove(indexOf);
		equipped.remove(indexOf);
	}
	
	public void addItem(Item item)
	{
		items.add(item);
		equipped.add(false);
	}
	
	public EquippableItem getEquippedWeapon()
	{
		for (int i = 0; i < items.size(); i++)
		{
			if (items.get(i) instanceof EquippableItem && ((EquippableItem) items.get(i)).getItemType() == EquippableItem.TYPE_WEAPON && equipped.get(i))
			{
				return (EquippableItem) items.get(i);
			}
		}
		return null;
	}
	
	public EquippableItem getEquippedArmor()
	{
		for (int i = 0; i < items.size(); i++)
		{
			if (items.get(i) instanceof EquippableItem && ((EquippableItem) items.get(i)).getItemType() == EquippableItem.TYPE_ARMOR && equipped.get(i))
			{
				return (EquippableItem) items.get(i);
			}
		}
		return null;
	}
	
	public EquippableItem getEquippedRing()
	{
		for (int i = 0; i < items.size(); i++)
		{
			if (items.get(i) instanceof EquippableItem && ((EquippableItem) items.get(i)).getItemType() == EquippableItem.TYPE_RING && equipped.get(i))
			{
				return (EquippableItem) items.get(i);
			}
		}
		return null;
	}
	
	public boolean isEquippable(EquippableItem item)
	{
		if (EquippableItem.TYPE_WEAPON == item.getItemType())
		{
			for (int i = 0; i < usuableWeapons.length; i++)
				if (usuableWeapons[i] == item.getItemStyle())
					return true;
			return false;
		}
		else if (EquippableItem.TYPE_ARMOR == item.getItemType())
		{
			for (int i = 0; i < usuableArmor.length; i++)
				if (usuableArmor[i] == item.getItemStyle())
					return true;
			return false;
		}
		return true;
	}

	public ArrayList<Boolean> getEquipped() {
		return equipped;
	}
	
	public EquippableItem equipItem(EquippableItem item)
	{
		EquippableItem oldItem = null;
		switch (item.getItemType())
		{
			case EquippableItem.TYPE_ARMOR:
				oldItem = getEquippedArmor();
				break;
			case EquippableItem.TYPE_RING:
				oldItem = getEquippedRing();
				break;
			case EquippableItem.TYPE_WEAPON:
				oldItem = getEquippedWeapon();
				break;
		}
		
		if (oldItem != null)
		{
			this.currentAttack -= oldItem.getAttack();
			this.currentDefense -= oldItem.getDefense();
			this.currentSpeed -= oldItem.getSpeed();
			this.maxAttack -= oldItem.getAttack();
			this.maxDefense -= oldItem.getDefense();
			this.maxSpeed -= oldItem.getSpeed();
			int index = items.indexOf(oldItem);
			this.equipped.set(index, false);
			
		}
		
		this.currentAttack += item.getAttack();
		this.currentDefense += item.getDefense();
		this.currentSpeed += item.getSpeed();
		this.maxAttack += item.getAttack();
		this.maxDefense += item.getDefense();
		this.maxSpeed += item.getSpeed();
		int index = items.indexOf(item);
		this.equipped.set(index, true);
		
		return oldItem;
	}
	
	public void unequipItem(EquippableItem item)
	{
		this.currentAttack -= item.getAttack();
		this.currentDefense -= item.getDefense();
		this.currentSpeed -= item.getSpeed();
		this.maxAttack -= item.getAttack();
		this.maxDefense -= item.getDefense();
		this.maxSpeed -= item.getSpeed();
		int index = items.indexOf(item);
		this.equipped.set(index, false);		
	}
	
	public int getAttackRange()
	{
		EquippableItem equippedWeapon = this.getEquippedWeapon();
		if (equippedWeapon != null)
			return equippedWeapon.getRange();
		return 1;
	}
	
	/*******************************************/
	/* MUTATOR AND ACCESSOR METHODS START HERE */
	/*******************************************/
	/*
	 * Gets the image portrait of this combat sprite. This will return NULL if the combat sprite does not
	 * have a portrait.
	 * 
	 * @return the image portrait of this combat sprite. This will return NULL if the combat sprite does not
	 * have a portrait.
	 */
	public Image getPortraitImage() {
		return portraitImage;
	}

	public int getCurrentHP() {
		return currentHP;
	}

	public void setCurrentHP(int currentHP) {
		if (currentHP > 0)
			fadeColor.a = 1;
		this.currentHP = currentHP;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}

	public int getCurrentMP() {
		return currentMP;
	}

	public void setCurrentMP(int currentMP) {
		this.currentMP = currentMP;
	}
	
	public void modifyCurrentHP(int amount)
	{
		currentHP = Math.min(maxHP, Math.max(0, currentHP + amount));
	}
	
	public void modifyCurrentMP(int amount)
	{
		currentMP = Math.min(maxMP, Math.max(0, currentMP + amount));
	}

	public int getMaxMP() {
		return maxMP;
	}

	public void setMaxMP(int maxMP) {
		this.maxMP = maxMP;
	}

	public int getCurrentInit() {
		return currentInit;
	}

	public void setCurrentInit(int currentInit) {
		this.currentInit = currentInit;
	}

	public int getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(int currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	public int getMaxAttack() {
		return maxAttack;
	}

	public void setMaxAttack(int maxAttack) {
		this.maxAttack = maxAttack;
	}

	public int getMaxDefense() {
		return maxDefense;
	}

	public void setMaxDefense(int maxDefense) {
		this.maxDefense = maxDefense;
	}

	public int getCurrentAttack() {
		return currentAttack;
	}

	public void setCurrentAttack(int currentAttack) {
		this.currentAttack = currentAttack;
	}

	public int getCurrentDefense() {
		return currentDefense;
	}

	public void setCurrentDefense(int currentDefense) {
		this.currentDefense = currentDefense;
	}

	public boolean isDodges() {
		return dodges;
	}

	public void setDodges(boolean dodges) {
		this.dodges = dodges;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDefeat() {
		return defeat;
	}

	public void setDefeat(int defeat) {
		this.defeat = defeat;
	}

	public AI getAi() {
		return ai;
	}
	
	public void setAi(AI ai)
	{
		this.ai = ai;
	}

	public int getCurrentMove() {
		return currentMove;
	}

	public void setCurrentMove(int currentMove) {
		this.currentMove = currentMove;
	}

	public int getMaxMove() {
		return maxMove;
	}

	public void setMaxMove(int maxMove) {
		this.maxMove = maxMove;
	}

	public int getMovementType() {
		return movementType;
	}

	public void setMovementType(int characterMovementType) {
		this.movementType = characterMovementType;
	}

	public boolean isHero() {
		return isHero;
	}	

	public Animation getAnimation(String animation)
	{
		return spriteAnims.getAnimation(animation);
	}
	
	public Image getAnimationImageAtIndex(int index)
	{
		return spriteAnims.getImageAtIndex(index);
	}
	
	public int getUniqueEnemyId()
	{
		return this.uniqueEnemyId;
	}
	
	public void setUniqueEnemyId(int id)
	{
		this.uniqueEnemyId = id;
	}
	
	public boolean isLeader() {
		return isLeader;
	}	

	/************************/
	/* Handle Progression	*/
	/************************/
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public ArrayList<KnownSpell> getSpellsDescriptors() {
		return spells;
	}	
	
	public void setSpells(ArrayList<KnownSpell> spells) {
		this.spells = spells;
	}

	public boolean isPromoted() {
		return isPromoted;
	}

	public void setPromoted(boolean isPromoted) {
		this.isPromoted = isPromoted;
	}

	public HeroProgression getHeroProgression() {
		return heroProgression;
	}
	
	
	public void triggerButton1Event(StateInfo stateInfo)
	{
		stateInfo.sendMessage(new SpriteContextMessage(Message.MESSAGE_SHOW_HERO, this));
	}
	
	public void triggerOverEvent(StateInfo stateInfo)
	{
		stateInfo.addPanel(new SpriteContextPanel(Panel.PANEL_HEALTH_BAR, this, stateInfo.getGc()));
	}
}
