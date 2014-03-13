package mb.fc.game.battle.spell.impl;

import mb.fc.game.battle.spell.Spell;
import mb.fc.game.battle.spell.SpellDescriptor;
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
		this.id = SpellDescriptor.ID_AURA;
	}

	@Override
	public String getBattleText(String targetName, int spellLevel) {
		// TODO Auto-generated method stub
		return "A healing aura washes over " + targetName + ". " + targetName + " is healed by " + damage[spellLevel] + "!";
	}
	
	@Override
	public int getExpGained(int level, CombatSprite attacker,
			CombatSprite target) 
	{		
		double percent = Math.min(1, (target.getMaxHP() - target.getCurrentHP()) * 1.0 / damage[level]);
		return (int) (12 * percent);
	}
}
