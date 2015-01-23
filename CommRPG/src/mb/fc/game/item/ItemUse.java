package mb.fc.game.item;

import mb.jython.JBattleEffect;

public class ItemUse
{
	public static final int DEFAULT_EXP = -1;

	private boolean targetsEnemy;
	private int damage;
	private int mpDamage;
	private JBattleEffect effects;
	private int range;
	private int area;
	private String battleText;
	private boolean singleUse;

	public ItemUse(boolean targetsEnemy, int damage, int mpDamage,
			JBattleEffect effects, int range, int area, String battleText, boolean singleUse) {
		super();
		this.targetsEnemy = targetsEnemy;
		this.damage = damage;
		this.mpDamage = mpDamage;
		this.effects = effects;
		this.range = range;
		this.area = area;
		this.battleText = battleText;
		this.singleUse = singleUse;
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

	public JBattleEffect getEffects() {
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

	public boolean isSingleUse() {
		return singleUse;
	}

	public int getExpGained()
	{
		return 1;
	}

	public String getBattleText(String targetName){
		return targetName + " " + battleText;
	}
}
