package mb.fc.game.battle.spell.impl;

import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.battle.spell.Spell;
import mb.fc.game.sprite.CombatSprite;

public class AuraSpell extends Spell
{
	public AuraSpell()
	{
		this.name = "Aura";
		this.costs = new int[] {7, 11, 15, 20};
		this.targetsEnemy = false;
		this.maxLevel = costs.length;
		this.damage = new int[] {15, 15, 30, 120};
		this.mpDamage = null;
		this.effects = null;
		this.range = new int[] {3, 3, 3, 3};
		this.area = new int[] {2, 3, 3, 3};
		this.id = KnownSpell.ID_AURA;
	}

	@Override
	public String getBattleText(CombatSprite target, int spellLevel) {
		return "A healing aura washes over " + target.getName() + ". " + target.getName() + " is healed by " + Math.min(target.getMaxHP() - target.getCurrentHP(), damage[spellLevel]) + "!";
	}

	@Override
	public int getExpGained(int level, CombatSprite attacker,
			CombatSprite target)
	{
		double percent = Math.max(0, (target.getMaxHP() - target.getCurrentHP()) * 1.0 / damage[level]);
		return (int) (12 * percent);
	}
}
