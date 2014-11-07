package mb.fc.game.resource;

import java.util.Hashtable;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.loading.FCResourceManager;
import mb.jython.GlobalPythonFactory;
import mb.jython.JSpell;

public class SpellResource
{
	public static Hashtable<Integer, JSpell> spells;

	public static void initSpells(FCResourceManager frm)
	{
		spells = new Hashtable<Integer, JSpell>();
		addSpell(KnownSpell.ID_BLAZE, frm);
		addSpell(KnownSpell.ID_HEAL, frm);
		addSpell(KnownSpell.ID_AURA, frm);
	}

	private static void addSpell(int spellId, FCResourceManager frm)
	{
		JSpell s =  GlobalPythonFactory.createJSpell();
		s = s.init(spellId);
		s.setSpellIcon(frm.getSpriteSheets().get("spellicons").getSubImage(spellId, 0));
		spells.put(spellId, s);
	}

	public static JSpell getSpell(int spellId, FCResourceManager frm)
	{
		return spells.get(spellId);
	}

	public static JSpell getSpell(int spellId, StateInfo stateInfo)
	{
		return getSpell(spellId, stateInfo.getResourceManager());
	}
}
