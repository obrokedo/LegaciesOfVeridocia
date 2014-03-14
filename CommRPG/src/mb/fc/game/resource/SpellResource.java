package mb.fc.game.resource;

import java.util.Hashtable;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.Spell;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.battle.spell.impl.AuraSpell;
import mb.fc.game.battle.spell.impl.BlazeSpell;
import mb.fc.game.battle.spell.impl.HealSpell;
import mb.fc.resource.FCResourceManager;

public class SpellResource 
{
	public static Hashtable<Integer, Spell> spells;
	
	static
	{
		spells = new Hashtable<Integer, Spell>();
		spells.put(KnownSpell.ID_BLAZE, new BlazeSpell());
		spells.put(KnownSpell.ID_HEAL, new HealSpell());
		spells.put(KnownSpell.ID_AURA, new AuraSpell());
	}
	
	public static Spell getSpell(int spellId, FCResourceManager frm)
	{
		Spell spell = spells.get(spellId);
		spell.setSpellIcon(frm.getSpriteSheets().get("spellicons").getSubImage(spellId, 0));
		return spell;
	}
	
	public static Spell getSpell(int spellId, StateInfo stateInfo)
	{
		return getSpell(spellId, stateInfo.getResourceManager());
	}
}
