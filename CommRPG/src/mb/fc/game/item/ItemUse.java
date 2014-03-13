package mb.fc.game.item;

import mb.fc.game.battle.condition.BattleEffect;

public class ItemUse 
{
	public static final int DEFAULT_EXP = -1;
	
	private boolean targetsEnemy;
	private int damage;
	private int mpDamage;
	private BattleEffect effects;
	private int range;
	private int area;
	private String battleText;

	public ItemUse(boolean targetsEnemy, int damage, int mpDamage,
			BattleEffect effects, int range, int area, String battleText) {
		super();
		this.targetsEnemy = targetsEnemy;
		this.damage = damage;
		this.mpDamage = mpDamage;
		this.effects = effects;
		this.range = range;
		this.area = area;
		this.battleText = battleText;
	}

	public boolean isTargetsEnemy() {
		return targetsEnemy;
	}
	
	public int getDamage() {
		return damage;
	}

	public int getMpDamage() {
		return mpDamage;
	}

	public BattleEffect getEffects() {
		return effects;
	}

	public int getRange() {
		return range;
	}

	public int getArea() {
		return area;
	}

	public String getBattleText() {
		return battleText;
	}

	public int getExpGained()
	{
		return 1;
	}
	
	public String getBattleText(String targetName){
		return targetName + " " + battleText;
	}
}
