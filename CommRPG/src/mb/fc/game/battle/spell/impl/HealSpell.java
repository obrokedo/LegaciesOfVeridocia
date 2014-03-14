package mb.fc.game.battle.spell.impl;

import mb.fc.game.battle.spell.Spell;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.sprite.CombatSprite;

public class HealSpell extends Spell
{
	public HealSpell()
	{
		this.name = "Heal";
		this.costs = new int[] {3, 6, 9, 12};
		this.targetsEnemy = false;
		this.maxLevel = costs.length;
		this.damage = new int[] {15, 22, 36, 120};
		this.mpDamage = null;
		this.effects = null;
		this.range = new int[] {1, 2, 3, 1};
		this.area = new int[] {1, 1, 1, 1};
		this.id = KnownSpell.ID_HEAL;
	}

	@Override
	public String getBattleText(String targetName, int spellLevel) {
		return targetName + "'s wounds close and scars fade. " + targetName + " is healed by " + damage[spellLevel] + "!";
	}

	@Override
	public int getExpGained(int level, CombatSprite attacker,
			CombatSprite target) 
	{		
		double percent = Math.min(1, (target.getMaxHP() - target.getCurrentHP()) * 1.0 / damage[level]);
		return (int) (12 * percent);
	}
}
