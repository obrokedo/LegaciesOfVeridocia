package mb.fc.game.resource;

import java.util.Hashtable;

import mb.fc.engine.state.StateInfo;
import mb.fc.loading.FCResourceManager;
import mb.jython.GlobalPythonFactory;
import mb.jython.JSpell;

public class SpellResource
{
	public static Hashtable<String, JSpell> spells;

	public static void initSpells(FCResourceManager frm)
	{
		spells = new Hashtable<String, JSpell>();
		for (String spell : GlobalPythonFactory.createJSpell().getSpellList())
			addSpell(spell, frm);
	}

	private static void addSpell(String spellId, FCResourceManager frm)
	{
		JSpell s =  GlobalPythonFactory.createJSpell();
		s = s.init(spellId);
		s.setSpellIcon(frm.getSpriteSheet("spellicons").getSubImage(s.getSpellIconId(), 0));
		spells.put(spellId, s);
	}

	public static JSpell getSpell(String spellId, FCResourceManager frm)
	{
		return spells.get(spellId);
	}

	public static JSpell getSpell(String spellId, StateInfo stateInfo)
	{
		return getSpell(spellId, stateInfo.getResourceManager());
	}
}
