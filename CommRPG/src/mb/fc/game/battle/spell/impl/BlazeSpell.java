package mb.fc.game.battle.spell.impl;

import mb.fc.game.battle.spell.Spell;
import mb.fc.game.battle.spell.SpellDescriptor;

public class BlazeSpell extends Spell 
{
	public BlazeSpell()
	{
		this.name = "Blaze";
		this.costs = new int[] {2, 5, 8, 12};
		this.targetsEnemy = true;
		this.maxLevel = costs.length;
		this.damage = new int[] {-6, -8, -15, -40};
		this.mpDamage = null;
		this.effects = null;
		this.range = new int[] {2, 2, 2, 2};
		this.area = new int[] {1, 2, 2, 1};
		this.id = SpellDescriptor.ID_BLAZE;
	}

	@Override
	public String getBattleText(String targetName, int spellLevel) 
	{
		return "Flame engulfs " + targetName + "'s body dealing " + -damage[spellLevel] + " damage!";
	}
}
