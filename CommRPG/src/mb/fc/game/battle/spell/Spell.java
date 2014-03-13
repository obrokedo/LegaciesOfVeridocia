package mb.fc.game.battle.spell;

import mb.fc.game.battle.condition.BattleEffect;
import mb.fc.game.sprite.CombatSprite;

import org.newdawn.slick.Image;

public abstract class Spell
{
	public static final int DEFAULT_EXP = -1;
	
	protected String name;
	protected int[] costs;
	protected boolean targetsEnemy;
	protected int maxLevel;
	protected int[] damage;
	protected int[] mpDamage;
	protected BattleEffect[] effects;
	protected int[] range;
	protected int[] area;
	protected int id;	
	protected Image spellIcon;
	
	public String getName() {
		return name;
	}
	public int[] getCosts() {
		return costs;
	}
	public boolean isTargetsEnemy() {
		return targetsEnemy;
	}
	public int getMaxLevel() {
		return maxLevel;
	}
	public int[] getDamage() {
		return damage;
	}
	public BattleEffect[] getEffects() {
		return effects;
	}
	public int[] getRange() {
		return range;
	}
	public int[] getArea() {
		return area;
	}
	public int[] getMpDamage() {
		return mpDamage;
	}	
	public int getId() {
		return id;
	}
	
	public Image getSpellIcon() {
		return spellIcon;
	}
	public void setSpellIcon(Image spellIcon) {
		this.spellIcon = spellIcon;
	}
	public int getExpGained(int level, CombatSprite attacker, CombatSprite target)
	{
		return DEFAULT_EXP;
	}
	
	public abstract String getBattleText(String targetName, int spellLevel);
}
