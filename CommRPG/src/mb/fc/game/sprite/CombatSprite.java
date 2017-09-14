package mb.fc.game.sprite;

import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.Range;
import mb.fc.game.ai.AI;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.constants.Direction;
import mb.fc.game.dev.DevHeroAI;
import mb.fc.game.hudmenu.Panel.PanelType;
import mb.fc.game.hudmenu.SpriteContextPanel;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.resource.ItemResource;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.utils.AnimSprite;
import mb.fc.utils.Animation;
import mb.jython.GlobalPythonFactory;
import mb.jython.JBattleEffect;
import mb.jython.JLevelProgression;

public class CombatSprite extends AnimatedSprite
{
	private static final long serialVersionUID = 1L;
	public static final int MAXIMUM_ITEM_AMOUNT = 4;

	private transient Color fadeColor = new Color(255, 255, 255, 255);

	private int currentHP, maxHP,
				currentMP, maxMP,
				currentInit,
				currentSpeed, maxSpeed,
				currentMove, maxMove,
				currentAttack, maxAttack,
				currentDefense, maxDefense,
				level, exp;

	// TODO In order to make the engine more generic these stats should probably be moved somewhere else
	//Elemental Affinity Stats
	private int currentFireAffin, maxFireAffin,
				currentElecAffin, maxElecAffin,
				currentColdAffin, maxColdAffin,
				currentDarkAffin, maxDarkAffin,
				currentWaterAffin, maxWaterAffin,
				currentEarthAffin, maxEarthAffin,
				currentWindAffin, maxWindAffin,
				currentLightAffin, maxLightAffin;

	// Defense stats
	private int currentBody, maxBody,
				currentMind, maxMind;


	private int baseCounter, maxCounter,
				baseEvade, maxEvade,
				baseDouble, maxDouble,
				baseCrit, maxCrit;

	private AI ai;

	private boolean isHero = false;
	private boolean isLeader = false;
	private boolean isPromoted = false;

	// This value provides a mean of differentiating between multiple enemies of the same name,
	// in addition this value can be user specified for enemies so that they may be the target
	// of triggers
	private int uniqueEnemyId = -1;
	private int clientId = 0;

	private ArrayList<KnownSpell> spells;
	private ArrayList<Item> items;
	private ArrayList<Boolean> equipped;
	private int[] usuableWeapons;
	private int[] usuableArmor;
	private HeroProgression heroProgression;
	// -1 when not promoted, 0 when promoted by generic, > 0 when special promotion where
	// promotionPath - 1 = index of the special promotion
	private int promotionPath = -1;
	private String movementType;
	private int kills;
	private int defeat;
	private ArrayList<JBattleEffect> battleEffects;
	private transient Image currentWeaponImage = null;
	private String attackEffectId;
	private int attackEffectChance;
	private int attackEffectLevel;
	private boolean drawShadow = true;


	/**
	 * A boolean indicating whether the combat sprite dodges or blocks attacks, dodges if true, blocks if false
	 */
	private boolean dodges;

	/**
	 * Constructor to create an enemy CombatSprite
	 */
	public CombatSprite(boolean isLeader,
			String name, String imageName, int hp, int mp, int attack, int defense, int speed, int move,
				String movementType, int maxFireAffin, int maxElecAffin,
				int maxColdAffin, int maxDarkAffin, int maxWaterAffin, int maxEarthAffin, int maxWindAffin,
				int maxLightAffin, int maxBody, int maxMind, int maxCounter, int maxEvade,
				int maxDouble, int maxCrit, int level,
				int enemyId, ArrayList<KnownSpell> spells, int id,
				String attackEffectId, int attackEffectChance, int attackEffectLevel)
	{
		this(isLeader, name, imageName, null, level, 0, false, id);
		this.spells = spells;
		this.uniqueEnemyId = enemyId;
		this.isHero = false;
		this.attackEffectChance = attackEffectChance;
		this.attackEffectId = attackEffectId;
		this.attackEffectLevel = attackEffectLevel;

		// Set base states
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

		// Set non-standard stats
		this.maxFireAffin = this.currentFireAffin = maxFireAffin;
		this.maxElecAffin = this.currentElecAffin = maxElecAffin;
		this.maxColdAffin = this.currentColdAffin = maxColdAffin;
		this.maxDarkAffin = this.currentDarkAffin = maxDarkAffin;
		this.maxWaterAffin = this.currentWaterAffin = maxWaterAffin;
		this.maxEarthAffin = this.currentEarthAffin = maxEarthAffin;
		this.maxWindAffin = this.currentWindAffin = maxWindAffin;
		this.maxLightAffin = this.currentLightAffin = maxLightAffin;
		this.maxBody = this.currentBody = maxBody;
		this.maxMind = this.currentMind = maxMind;
		this.maxCounter = this.baseCounter = maxCounter;
		this.maxEvade = this.baseEvade = maxEvade;
		this.maxDouble = this.baseDouble = maxDouble;
		this.maxCrit = this.baseCrit = maxCrit;
		this.battleEffects = new ArrayList<>();
	}


	/**
	 * Constructor to create a hero CombatSprite
	 */
	public CombatSprite(boolean isLeader,
			String name, String imageName, HeroProgression heroProgression,
			int level, int exp, boolean promoted, int id)
	{
		super(0, 0, imageName, id);

		this.isPromoted = promoted;
		// If a CombatSprite is created as promoted then it must not be on a special promotion path
		if (isPromoted)
			this.promotionPath = 0;
		this.level = level;
		this.exp = 0;


		dodges = true;

		this.heroProgression = heroProgression;

		// Handle attribute strengths for heroes
		if (heroProgression != null)
		{
			// Stats in the progression are set up as [0] = stat progression, [1] = stat start, [2] = stat end
			currentHP = maxHP = (int) this.getCurrentProgression().getHp()[1];
			maxMP = (int) this.getCurrentProgression().getMp()[1];
			maxSpeed = (int) this.getCurrentProgression().getSpeed()[1];
			maxMove = this.getCurrentProgression().getMove();
			movementType = this.getCurrentProgression().getMovementType();
			maxAttack = (int) this.getCurrentProgression().getAttack()[1];
			maxDefense = (int) this.getCurrentProgression().getDefense()[1];

			setNonRandomStats();			
		}

		this.isHero = true;
		this.isLeader = isLeader;
		this.name = name;
		this.imageName = imageName;
		this.items = new ArrayList<Item>();
		this.equipped = new ArrayList<Boolean>();

		if (heroProgression != null)
		{
			if (!isPromoted)
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

		this.battleEffects = new ArrayList<>();
		
		this.spriteType = Sprite.TYPE_COMBAT;
		this.id = id;
		this.attackEffectId = null;

		if (CommRPG.TEST_MODE_ENABLED)
		{
			this.ai = new DevHeroAI(1);
			if (this.isHero && this.isLeader && !CommRPG.BATTLE_MODE_OPTIMIZE)
			{
				this.setMaxHP(99);
				this.setMaxAttack(99);
				this.setMaxDefense(99);
				this.setMaxSpeed(99);
			}
		}
	}


	public void setNonRandomStats() {
		// Load non standard stats
		JLevelProgression levelProgPython = GlobalPythonFactory.createLevelProgression();
		this.maxCounter = levelProgPython.getBaseBattleStat(this.getCurrentProgression().getCounterStrength(), this);
		this.maxEvade = levelProgPython.getBaseBattleStat(this.getCurrentProgression().getEvadeStrength(), this);
		this.maxDouble = levelProgPython.getBaseBattleStat(this.getCurrentProgression().getDoubleStrength(), this);
		this.maxCrit = levelProgPython.getBaseBattleStat(this.getCurrentProgression().getCritStrength(), this);
		this.maxBody = levelProgPython.getBaseBodyMindStat(this.getCurrentProgression().getBodyStrength(), this);
		this.maxMind = levelProgPython.getBaseBodyMindStat(this.getCurrentProgression().getMindStrength(), this);
		// TODO PROMOTED PROGRESSION OF BATTLE ATTRIBUTES
		this.maxFireAffin = this.getCurrentProgression().getFireAffin();
		this.maxElecAffin = this.getCurrentProgression().getElecAffin();
		this.maxColdAffin = this.getCurrentProgression().getColdAffin();
		this.maxDarkAffin = this.getCurrentProgression().getDarkAffin();
		this.maxWaterAffin = this.getCurrentProgression().getWaterAffin();
		this.maxEarthAffin = this.getCurrentProgression().getEarthAffin();
		this.maxWindAffin = this.getCurrentProgression().getWindAffin();
		this.maxLightAffin = this.getCurrentProgression().getLightAffin();
		
		setSpellsKnownByProgression();
	}


	private void setSpellsKnownByProgression() {
		// Set up spells that are currently known
		ArrayList<int[]> spellProgression = this.getCurrentProgression().getSpellLevelLearned();
		ArrayList<KnownSpell> knownSpells = new ArrayList<KnownSpell>();
		for (int i = 0; i < spellProgression.size(); i++)
		{
			// Check what spells are already known
			boolean known = false;
			int maxLevel = 0;
			for (int j = 0; j < spellProgression.get(i).length; j++)
			{
				if (spellProgression.get(i)[j] <= level)
				{
					maxLevel = j + 1;
					known = true;
				}
			}

			if (known)
				knownSpells.add(new KnownSpell(this.getCurrentProgression().getSpellIds().get(i), (byte) maxLevel));
		}
		this.spells = knownSpells;
	}

	//TODO Need to have a way to init a sprite without resetting stats
	@Override
	public void initializeSprite(FCResourceManager fcrm)
	{
		super.initializeSprite(fcrm);

		drawShadow = GlobalPythonFactory.createConfigurationValues().isAffectedByTerrain(this.movementType);

		currentAnim = spriteAnims.getCharacterAnimation("Down", this.isPromoted);

		if (spells != null && spells.size() > 0)
		{
			for (KnownSpell sd : spells)
				sd.initializeFromLoad(fcrm);
		}

		// TODO Does this work?!? We are persisting a jython object
		for (JBattleEffect effect : battleEffects)
			effect.initializeAnimation(fcrm);

		//TODO Remove (all?) battle effects if this isn't an init mid battle

		for (Item item : items)
		{
			ItemResource.initializeItem(item, fcrm);
		}

		if (this.getEquippedWeapon() != null && this.getEquippedWeapon().getWeaponImage() != null)
		{
			currentWeaponImage = fcrm.getImage(this.getEquippedWeapon().getWeaponImage());
		}

		fadeColor = new Color(255, 255, 255, 255);
	}
	
	public void initializeStats()
	{	
		this.visible = true;
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

		this.currentFireAffin = maxFireAffin;
		this.currentElecAffin = maxElecAffin;
		this.currentColdAffin = maxColdAffin;
		this.currentDarkAffin = maxDarkAffin;
		this.currentWaterAffin = maxWaterAffin;
		this.currentEarthAffin = maxEarthAffin;
		this.currentWindAffin = maxWindAffin;
		this.currentLightAffin = maxLightAffin;
		this.currentBody = maxBody;
		this.currentMind = maxMind;
		this.baseCounter = maxCounter;
		this.baseEvade = maxEvade;
		this.baseDouble = maxDouble;
		this.baseCrit = maxCrit;
		
		// Clear out non-persistent battle effects
		Iterator<JBattleEffect> beItr = this.battleEffects.iterator();
		
		while (beItr.hasNext()) {
			JBattleEffect be = beItr.next();
			if (!be.doesEffectPersistAfterBattle())
				beItr.remove();
		}
		
		if (isHero)
		{
			super.setLocX(-1, 0);
			super.setLocY(-1, 0);
		}
	
		// addBattleEffect(GlobalPythonFactory.createJBattleEffect("Burn", 1));
	}

	@Override
	public void update(StateInfo stateInfo)
	{
		super.update(stateInfo);

		if (currentHP <= 0)
		{
			if (this.getFacing() == Direction.DOWN)
				this.setFacing(Direction.RIGHT);
			else if (this.getFacing() == Direction.RIGHT)
				this.setFacing(Direction.UP);
			else if (this.getFacing() == Direction.UP)
				this.setFacing(Direction.LEFT);
			else if (this.getFacing() == Direction.LEFT)
				this.setFacing(Direction.DOWN);
			currentHP -= 25;
			fadeColor.a = (255 + currentHP) / 255.0f;
		}
	}

	@Override
	public void render(Camera camera, Graphics graphics, PaddedGameContainer cont, int tileHeight)
	{
		graphics.setColor(Color.white);
		/*
		if (this.isHero && stateInfo.getPersistentStateInfo().getClientId() != this.clientId) {
			graphics.drawString("x", this.getLocX() - camera.getLocationX(),
					this.getLocY() - camera.getLocationY() - stateInfo.getResourceManager().getMap().getTileEffectiveHeight() / 2);
		}
		*/
		for (AnimSprite as : currentAnim.frames.get(imageIndex).sprites)
		{
			Image im = spriteAnims.getImageAtIndex(as.imageIndex);
			if (as.flipH) {
				im = im.getFlippedCopy(true, false);
			}
			if (as.flipV) {
				im = im.getFlippedCopy(false, true);
			}

			if (drawShadow)
			{
				AnimatedSprite.drawShadow(im, this.getLocX(), this.getLocY(), camera, true, tileHeight);
			}

			graphics.drawImage(im, this.getLocX() - camera.getLocationX(),
					this.getLocY() - camera.getLocationY() - tileHeight / 2, fadeColor);
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
			if (this.isHero)
			{
				this.unequipItem(oldItem);
				/*
				this.currentAttack -= oldItem.getAttack();
				this.currentDefense -= oldItem.getDefense();
				this.currentSpeed -= oldItem.getSpeed();
				this.maxAttack -= oldItem.getAttack();
				this.maxDefense -= oldItem.getDefense();
				this.maxSpeed -= oldItem.getSpeed();
				int index = items.indexOf(oldItem);
				this.equipped.set(index, false);
				*/
			}
		}

		// TODO Do enemies get item bonuses?
		if (this.isHero)
		{
			toggleEquipWeapon(item, true);
		}

		int index = items.lastIndexOf(item);
		this.equipped.set(index, true);

		return oldItem;
	}

	public void unequipItem(EquippableItem item)
	{
		toggleEquipWeapon(item, false);
		int index = items.indexOf(item);
		this.equipped.set(index, false);
	}

	private void toggleEquipWeapon(EquippableItem item, boolean equip)
	{
		// Non extended stats
		this.currentAttack += ((equip ? 1 : -1) * item.getAttack());
		this.currentDefense += ((equip ? 1 : -1) * item.getDefense());
		this.currentSpeed += ((equip ? 1 : -1) * item.getSpeed());
		this.maxAttack += ((equip ? 1 : -1) * item.getAttack());
		this.maxDefense += ((equip ? 1 : -1) * item.getDefense());
		this.maxSpeed += ((equip ? 1 : -1) * item.getSpeed());

		// Extended Stats
		/*
		 * incmindam=7 inccrit=0 inccounter=0 incdouble=0 incevade=0 maxhpreg=0 minhpreg=0 maxmpreg=0 minhpreg=0
	 * effect="" efflvl=-1 effchc=0 csteff=false dmgaff="NORMAL" ohko=0 ohkooc=0
		 */

		this.maxFireAffin += ((equip ? 1 : -1) * item.getFireAffinity());
		this.maxElecAffin += ((equip ? 1 : -1) * item.getElecAffinity());
		this.maxColdAffin += ((equip ? 1 : -1) * item.getColdAffin());
		this.maxDarkAffin += ((equip ? 1 : -1) * item.getDarkAffin());
		this.maxWaterAffin += ((equip ? 1 : -1) * item.getWaterAffin());
		this.maxEarthAffin += ((equip ? 1 : -1) * item.getEarthAffin());
		this.maxWindAffin += ((equip ? 1 : -1) * item.getWindAffin());
		this.maxLightAffin += ((equip ? 1 : -1) * item.getLightAffin());
		this.currentFireAffin += ((equip ? 1 : -1) * item.getFireAffinity());
		this.currentElecAffin += ((equip ? 1 : -1) * item.getElecAffinity());
		this.currentColdAffin += ((equip ? 1 : -1) * item.getColdAffin());
		this.currentDarkAffin += ((equip ? 1 : -1) * item.getDarkAffin());
		this.currentWaterAffin += ((equip ? 1 : -1) * item.getWaterAffin());
		this.currentEarthAffin += ((equip ? 1 : -1) * item.getEarthAffin());
		this.currentWindAffin += ((equip ? 1 : -1) * item.getWindAffin());
		this.currentLightAffin += ((equip ? 1 : -1) * item.getLightAffin());
	}

	public Range getAttackRange()
	{
		EquippableItem equippedWeapon = this.getEquippedWeapon();
		if (equippedWeapon != null)
			return equippedWeapon.getRange();
		return Range.ONE_ONLY;
	}

	/*******************************************/
	/* MUTATOR AND ACCESSOR METHODS START HERE */
	/*******************************************/
	@Override
	public void setFacing(Direction dir)
	{
		switch (dir)
		{
			case UP:
				currentAnim = spriteAnims.getCharacterAnimation("Up", this.isPromoted);
				break;
			case DOWN:
				currentAnim = spriteAnims.getCharacterAnimation("Down", this.isPromoted);
				break;
			case LEFT:
				currentAnim = spriteAnims.getCharacterAnimation("Left", this.isPromoted);
				break;
			case RIGHT:
				currentAnim = spriteAnims.getCharacterAnimation("Right", this.isPromoted);
				break;
		}
		facing = dir;
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

	public String getMovementType() {
		return movementType;
	}

	public void setMovementType(String characterMovementType) {
		this.movementType = characterMovementType;
	}

	public boolean isHero() {
		return isHero;
	}

	public Animation getAnimation(String animation)
	{
		return spriteAnims.getCharacterAnimation(animation, this.isPromoted);
	}

	public boolean hasAnimation(String animation)
	{
		return spriteAnims.hasCharacterAnimation(animation, this.isPromoted);
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

	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
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

	public String levelUpHiddenStatistics()
	{
		JLevelProgression jlp = GlobalPythonFactory.createLevelProgression();
		String text = jlp.levelUpHero(this);

		Log.debug("Leveling up heroes non-displayed stats: " + this.getName());

		int increase = jlp.getLevelUpBattleStat(this.getCurrentProgression().getCounterStrength(), this, level, isPromoted, this.maxCounter);
		maxCounter += increase;
		baseCounter += increase;

		increase = jlp.getLevelUpBattleStat(this.getCurrentProgression().getCritStrength(), this, level, isPromoted, this.maxCrit);
		maxCrit += increase;
		baseCrit += increase;

		increase = jlp.getLevelUpBattleStat(this.getCurrentProgression().getDoubleStrength(), this, level, isPromoted, this.maxDouble);
		maxDouble += increase;
		baseDouble += increase;

		increase = jlp.getLevelUpBattleStat(this.getCurrentProgression().getEvadeStrength(), this, level, isPromoted, this.maxEvade);
		maxEvade += increase;
		baseEvade += increase;

		increase = jlp.getLevelUpBodyMindStat(this.getCurrentProgression().getBodyProgression(), this, level, isPromoted);
		maxBody += increase;
		currentBody += increase;

		increase = jlp.getLevelUpBodyMindStat(this.getCurrentProgression().getMindProgression(), this, level, isPromoted);
		maxMind += increase;
		currentMind += increase;
		return text;
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

	public void setPromoted(boolean isPromoted, int promotionPath) {
		this.isPromoted = isPromoted;
		this.promotionPath = promotionPath;
	}

	public HeroProgression getHeroProgression() {
		return heroProgression;
	}

	public Progression getCurrentProgression() {
		if (isPromoted) {
			if (promotionPath == 0) {
				return heroProgression.getPromotedProgression();
			} else {
				return heroProgression.getSpecialProgressions().get(promotionPath - 1);
			}
		}
		else
			return heroProgression.getUnpromotedProgression();
	}

	public ArrayList<JBattleEffect> getBattleEffects() {
		return battleEffects;
	}

	public void addBattleEffect(JBattleEffect battleEffect)
	{
		
		for (int i = 0; i < this.battleEffects.size(); i++)
		{
			JBattleEffect be = this.battleEffects.get(i);

			if (be.getBattleEffectId().equalsIgnoreCase(battleEffect.getBattleEffectId()))
			{
				be.effectEnded(this);
				battleEffects.remove(i);
				i--;
			}
		}
		this.battleEffects.add(battleEffect);
	}

	public void removeBattleEffect(JBattleEffect battleEffect)
	{
		Log.debug("Removed " + battleEffect.getBattleEffectId() + " from " + this.getName());
		this.battleEffects.remove(battleEffect);
	}

	public Image getCurrentWeaponImage() {
		return currentWeaponImage;
	}

	public JBattleEffect getAttackEffect()
	{
		JBattleEffect eff = null;
		if (attackEffectId != null)
		{
			eff = GlobalPythonFactory.createJBattleEffect(attackEffectId, attackEffectLevel);
			eff.setEffectChance(attackEffectChance);
		}
		return eff;
	}

	public void setAttackEffect(String attackEffectId, int attackEffectChance, int attackEffectLevel)
	{
		this.attackEffectChance = attackEffectChance;
		this.attackEffectId = attackEffectId;
		this.attackEffectLevel = attackEffectLevel;
	}

	public void initializeBattleEffects(FCResourceManager frm)
	{
		for (JBattleEffect be : this.battleEffects)
			be.initializeAnimation(frm);
	}

	public void triggerButton1Event(StateInfo stateInfo)
	{
		stateInfo.sendMessage(new SpriteContextMessage(MessageType.SHOW_HERO, this));
	}

	public void triggerOverEvent(StateInfo stateInfo)
	{
		stateInfo.addPanel(new SpriteContextPanel(PanelType.PANEL_HEALTH_BAR, this, stateInfo.getFCGameContainer()));
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public int getCurrentFireAffin() {
		return currentFireAffin;
	}

	public int getMaxFireAffin() {
		return maxFireAffin;
	}

	public int getCurrentElecAffin() {
		return currentElecAffin;
	}

	public int getMaxElecAffin() {
		return maxElecAffin;
	}

	public int getCurrentColdAffin() {
		return currentColdAffin;
	}

	public int getMaxColdAffin() {
		return maxColdAffin;
	}

	public int getCurrentDarkAffin() {
		return currentDarkAffin;
	}

	public int getMaxDarkAffin() {
		return maxDarkAffin;
	}

	public int getCurrentWaterAffin() {
		return currentWaterAffin;
	}

	public int getMaxWaterAffin() {
		return maxWaterAffin;
	}

	public int getCurrentEarthAffin() {
		return currentEarthAffin;
	}

	public int getMaxEarthAffin() {
		return maxEarthAffin;
	}

	public int getCurrentWindAffin() {
		return currentWindAffin;
	}

	public int getMaxWindAffin() {
		return maxWindAffin;
	}

	public int getCurrentLightAffin() {
		return currentLightAffin;
	}

	public int getMaxLightAffin() {
		return maxLightAffin;
	}

	public int getCurrentBody() {
		return currentBody;
	}

	public int getMaxBody() {
		return maxBody;
	}

	public int getCurrentMind() {
		return currentMind;
	}

	public int getMaxMind() {
		return maxMind;
	}

	/**
	 * Returns a boolean indicating whether this CombatSprite should have a shadow drawn for it
	 * and by extension whether the battle platform should be displayed for it
	 * 
	 * @return a boolean indicating whether this CombatSprite should have a shadow drawn for it
	 * and by extension whether the battle platform should be displayed for it
	 */
	public boolean isDrawShadow() {
		return drawShadow;
	}

	/**
	 * Returns an integer value representing this CombatSprite's base "counter" chance modified by
	 * it's equipped items
	 * 
	 * @return an integer value representing this CombatSprite's base "counter" chance modified by
	 * it's equipped items
	 */
	public int getModifiedCounter()
	{
		int mod = 0;
		for (int i = 0; i < this.equipped.size(); i++)
			if (this.equipped.get(i))
				mod += ((EquippableItem) this.getItem(i)).getIncreasedCounter();
		return getBaseCounter() + mod;
	}

	/**
	 * Returns an integer value representing this CombatSprite's base "counter" chance (not 
	 * modified by equipment)
	 * 
	 * @return an integer value representing this CombatSprite's base "counter" chance (not 
	 * modified by equipment)
	 */
	public int getBaseCounter() {
		return baseCounter;
	}

	/**
	 * Sets this CombatSprite's base counter chance
	 * 
	 * @param currentCounter an integer to be used as the CombatSprites new base counter stat
	 */
	public void setBaseCounter(int currentCounter) {
		this.baseCounter = currentCounter;
	}

	/**
	 * Returns an integer value representing this CombatSprite's base "evade" chance modified by
	 * it's equipped items
	 * 
	 * @return an integer value representing this CombatSprite's base "evade" chance modified by
	 * it's equipped items
	 */
	public int getModifiedEvade()
	{
		int mod = 0;
		for (int i = 0; i < this.equipped.size(); i++)
			if (this.equipped.get(i))
				mod += ((EquippableItem) this.getItem(i)).getIncreasedEvade();
		return getBaseEvade() + mod;
	}

	/**
	 * Returns an integer value representing this CombatSprite's base "evade" chance (not 
	 * modified by equipment)
	 * 
	 * @return an integer value representing this CombatSprite's base "evade" chance (not 
	 * modified by equipment)
	 */
	public int getBaseEvade() {
		return baseEvade;
	}

	/**
	 * Sets this CombatSprite's base evade chance
	 * 
	 * @param currentCounter an integer to be used as the CombatSprites new base evade stat
	 */
	public void setBaseEvade(int currentEvade) {
		this.baseEvade = currentEvade;
	}

	/**
	 * Returns an integer value representing this CombatSprite's base "double" chance modified by
	 * it's equipped items
	 * 
	 * @return an integer value representing this CombatSprite's base "double" chance modified by
	 * it's equipped items
	 */
	public int getModifiedDouble()
	{
		int mod = 0;
		for (int i = 0; i < this.equipped.size(); i++)
			if (this.equipped.get(i))
				mod += ((EquippableItem) this.getItem(i)).getIncreasedDouble();
		return getBaseDouble() + mod;
	}

	/**
	 * Returns an integer value representing this CombatSprite's base "double" chance (not 
	 * modified by equipment)
	 * 
	 * @return an integer value representing this CombatSprite's base "double" chance (not 
	 * modified by equipment)
	 */
	public int getBaseDouble() {
		return baseDouble;
	}

	/**
	 * Sets this CombatSprite's base double chance
	 * 
	 * @param currentCounter an integer to be used as the CombatSprites new base double stat
	 */
	public void setBaseDouble(int currentDouble) {
		this.baseDouble = currentDouble;
	}

	/**
	 * Returns an integer value representing this CombatSprite's base "critical" chance modified by
	 * it's equipped items
	 * 
	 * @return an integer value representing this CombatSprite's base "critical" chance modified by
	 * it's equipped items
	 */
	public int getModifiedCrit()
	{
		int mod = 0;
		for (int i = 0; i < this.equipped.size(); i++)
			if (this.equipped.get(i))
				mod += ((EquippableItem) this.getItem(i)).getIncreasedCrit();
		return getBaseCrit() + mod;
	}

	/**
	 * Returns an integer value representing this CombatSprite's base "critical" chance (not 
	 * modified by equipment)
	 * 
	 * @return an integer value representing this CombatSprite's base "critical" chance (not 
	 * modified by equipment)
	 */
	public int getBaseCrit() {
		return baseCrit;
	}

	/**
	 * Sets this CombatSprite's base critical chance
	 * 
	 * @param currentCounter an integer to be used as the CombatSprites new base critical stat
	 */
	public void setBaseCrit(int currentCrit) {
		this.baseCrit = currentCrit;
	}


	@Override
	public String toString() {
		return "CombatSprite [name=" + name+ " level=" + level + ", isHero=" + isHero + ", isLeader=" + isLeader + ", isPromoted="
				+ isPromoted + ", uniqueEnemyId=" + uniqueEnemyId + ", id=" + id + ", tileX=" + this.getTileX() + ", tileY=" + this.getTileY() + "]";
	}
	
	
}
