package lov.game.sprite;

import java.util.ArrayList;

import org.newdawn.slick.util.Log;

import lov.game.sprite.progression.LOVProgression;
import tactical.engine.TacticalGame;
import tactical.engine.config.LevelProgressionConfiguration;
import tactical.game.battle.spell.KnownSpell;
import tactical.game.item.EquippableItem;
import tactical.game.sprite.CombatSprite;
import tactical.game.sprite.HeroProgression;

public class LOVCombatSprite extends CombatSprite
{
	private static final long serialVersionUID = 1L;
	//Elemental Affinity Stats
	protected int currentFireAffin, maxFireAffin,
				currentElecAffin, maxElecAffin,
				currentColdAffin, maxColdAffin,
				currentDarkAffin, maxDarkAffin,
				currentWaterAffin, maxWaterAffin,
				currentEarthAffin, maxEarthAffin,
				currentWindAffin, maxWindAffin,
				currentLightAffin, maxLightAffin;

	// Defense stats
	protected int currentBody, maxBody,
				currentMind, maxMind;


	protected int baseCounter, maxCounter,
				baseEvade, maxEvade,
				baseDouble, maxDouble,
				baseCrit, maxCrit;

	


	/**
	 * A boolean indicating whether the combat sprite dodges or blocks attacks, dodges if true, blocks if false
	 */
	private boolean dodges;

	/**
	 * Constructor to create an enemy CombatSprite
	 */
	public LOVCombatSprite(boolean isLeader,
			String name, String imageName, int hp, int mp, int attack, int defense, int speed, int move,
				String movementType, int maxFireAffin, int maxElecAffin,
				int maxColdAffin, int maxDarkAffin, int maxWaterAffin, int maxEarthAffin, int maxWindAffin,
				int maxLightAffin, int maxBody, int maxMind, int maxCounter, int maxEvade,
				int maxDouble, int maxCrit, int level,
				int enemyId, ArrayList<KnownSpell> spells, int id,
				String attackEffectId, int attackEffectChance, int attackEffectLevel)
	{
		super(isLeader, name, imageName, hp, mp, attack, defense, speed, move, movementType, level, 
				enemyId, spells, id, attackEffectId, attackEffectChance, attackEffectLevel);

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
	public LOVCombatSprite(boolean isLeader,
			String name, String imageName, HeroProgression heroProgression,
			int level, int exp, boolean promoted, int id)
	{
		super(isLeader, name, imageName, heroProgression, level, exp, promoted, id);
	}

	private LOVProgression getTypedCurrentProgression() {
		return (LOVProgression) this.getCurrentProgression();
	}

	public void setNonRandomStats() {
		super.setNonRandomStats();
		// Load non standard stats
		LevelProgressionConfiguration levelProgPython = TacticalGame.ENGINE_CONFIGURATIOR.getLevelProgression();
		this.maxCounter = levelProgPython.getBaseBattleStat(this.getTypedCurrentProgression().getCounterStrength(), this);
		this.maxEvade = levelProgPython.getBaseBattleStat(this.getTypedCurrentProgression().getEvadeStrength(), this);
		this.maxDouble = levelProgPython.getBaseBattleStat(this.getTypedCurrentProgression().getDoubleStrength(), this);
		this.maxCrit = levelProgPython.getBaseBattleStat(this.getTypedCurrentProgression().getCritStrength(), this);
		this.maxBody = levelProgPython.getBaseBodyMindStat(this.getTypedCurrentProgression().getBodyStrength(), this);
		this.maxMind = levelProgPython.getBaseBodyMindStat(this.getTypedCurrentProgression().getMindStrength(), this);
		// TODO PROMOTED PROGRESSION OF BATTLE ATTRIBUTES
		this.maxFireAffin = this.getTypedCurrentProgression().getFireAffin();
		this.maxElecAffin = this.getTypedCurrentProgression().getElecAffin();
		this.maxColdAffin = this.getTypedCurrentProgression().getColdAffin();
		this.maxDarkAffin = this.getTypedCurrentProgression().getDarkAffin();
		this.maxWaterAffin = this.getTypedCurrentProgression().getWaterAffin();
		this.maxEarthAffin = this.getTypedCurrentProgression().getEarthAffin();
		this.maxWindAffin = this.getTypedCurrentProgression().getWindAffin();
		this.maxLightAffin = this.getTypedCurrentProgression().getLightAffin();
	}


	

	public void initializeStats()
	{	
		super.initializeStats();
		
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
	}


	/************************/
	/* Handle item stuff	*/
	/************************/
	protected void toggleEquipWeapon(EquippableItem item, boolean equip)
	{
		super.toggleEquipWeapon(item, equip);
		// Extended Stats

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

	/*******************************************/
	/* MUTATOR AND ACCESSOR METHODS START HERE */
	/*******************************************/
	@Override
	public String levelUpCustomStatistics()
	{
		LevelProgressionConfiguration jlp = TacticalGame.ENGINE_CONFIGURATIOR.getLevelProgression();	
		Log.debug("Leveling up heroes non-displayed stats: " + this.getName());

		int increase = jlp.getLevelUpBattleStat(this.getTypedCurrentProgression().getCounterStrength(), this, level, isPromoted, this.maxCounter);
		maxCounter += increase;
		baseCounter += increase;

		increase = jlp.getLevelUpBattleStat(this.getTypedCurrentProgression().getCritStrength(), this, level, isPromoted, this.maxCrit);
		maxCrit += increase;
		baseCrit += increase;

		increase = jlp.getLevelUpBattleStat(this.getTypedCurrentProgression().getDoubleStrength(), this, level, isPromoted, this.maxDouble);
		maxDouble += increase;
		baseDouble += increase;

		increase = jlp.getLevelUpBattleStat(this.getTypedCurrentProgression().getEvadeStrength(), this, level, isPromoted, this.maxEvade);
		maxEvade += increase;
		baseEvade += increase;

		increase = jlp.getLevelUpBodyMindStat(this.getTypedCurrentProgression().getBodyProgression(), this, level, isPromoted);
		maxBody += increase;
		currentBody += increase;

		increase = jlp.getLevelUpBodyMindStat(this.getTypedCurrentProgression().getMindProgression(), this, level, isPromoted);
		maxMind += increase;
		currentMind += increase;
		
		String text = jlp.levelUpHero(this);
		return text;
	}
	
	public int getCurrentFireAffin() {
		return currentFireAffin;
	}

	public void setCurrentFireAffin(int value) {
		this.currentFireAffin = value;
	}

	public int getMaxFireAffin() {
		return maxFireAffin;
	}

	public int getCurrentElecAffin() {
		return currentElecAffin;
	}

	public void setCurrentElecAffin(int value) {
		this.currentElecAffin = value;
	}

	public int getMaxElecAffin() {
		return maxElecAffin;
	}

	public int getCurrentColdAffin() {
		return currentColdAffin;
	}

	public void setCurrentColdAffin(int value) {
		this.currentColdAffin = value;
	}

	public int getMaxColdAffin() {
		return maxColdAffin;
	}

	public int getCurrentDarkAffin() {
		return currentDarkAffin;
	}

	public void setCurrentDarkAffin(int value) {
		this.currentDarkAffin = value;
	}

	public int getMaxDarkAffin() {
		return maxDarkAffin;
	}

	public int getCurrentWaterAffin() {
		return currentWaterAffin;
	}

	public void setCurrentWaterAffin(int value) {
		this.currentWaterAffin = value;
	}

	public int getMaxWaterAffin() {
		return maxWaterAffin;
	}

	public int getCurrentEarthAffin() {
		return currentEarthAffin;
	}

	public void setCurrentEarthAffin(int value) {
		this.currentEarthAffin = value;
	}

	public int getMaxEarthAffin() {
		return maxEarthAffin;
	}

	public int getCurrentWindAffin() {
		return currentWindAffin;
	}

	public void setCurrentWindAffin(int value) {
		this.currentWindAffin = value;
	}

	public int getMaxWindAffin() {
		return maxWindAffin;
	}

	public int getCurrentLightAffin() {
		return currentLightAffin;
	}

	public void setCurrentLightAffin(int value) {
		this.currentLightAffin = value;
	}

	public int getMaxLightAffin() {
		return maxLightAffin;
	}

	public int getCurrentBody() {
		return currentBody;
	}

	public void setCurrentBody(int value) {
		this.currentBody = value;
	}

	public int getMaxBody() {
		return maxBody;
	}

	public int getCurrentMind() {
		return currentMind;
	}

	public void setCurrentMind(int value) {
		this.currentMind = value;
	}

	public int getMaxMind() {
		return maxMind;
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
