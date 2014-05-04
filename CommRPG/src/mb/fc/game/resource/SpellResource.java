package mb.fc.game.resource;

import java.util.Hashtable;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.battle.spell.Spell;
import mb.fc.loading.FCResourceManager;
import mb.jython.GlobalPythonFactory;

public class SpellResource
{
	public static Hashtable<Integer, Spell> spells;

	public static void initSpells(FCResourceManager frm)
	{
		spells = new Hashtable<Integer, Spell>();
		addSpell(KnownSpell.ID_BLAZE, frm);
		addSpell(KnownSpell.ID_HEAL, frm);
		addSpell(KnownSpell.ID_AURA, frm);
	}

	private static void addSpell(int spellId, FCResourceManager frm)
	{
		Spell s =  GlobalPythonFactory.createSpell();
		s.init(spellId);
		s.setSpellIcon(frm.getSpriteSheets().get("spellicons").getSubImage(spellId, 0));
		spells.put(spellId, s);
	}

	public static Spell getSpell(int spellId, FCResourceManager frm)
	{
		return spells.get(spellId);
	}

	public static Spell getSpell(int spellId, StateInfo stateInfo)
	{
		return getSpell(spellId, stateInfo.getResourceManager());
	}
}
