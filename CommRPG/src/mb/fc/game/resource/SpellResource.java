package mb.fc.game.resource;

import java.util.Hashtable;

import mb.fc.engine.CommRPG;
import mb.fc.game.battle.spell.SpellDefinition;
import mb.fc.loading.FCResourceManager;

public class SpellResource
{
	public static Hashtable<String, SpellDefinition> spells;

	public static void initSpells(FCResourceManager frm)
	{
		spells = new Hashtable<String, SpellDefinition>();
		for (String spell : CommRPG.engineConfiguratior.getSpellFactory().getSpellList())
			addSpell(spell, frm);
	}

	private static void addSpell(String spellId, FCResourceManager frm)
	{
		SpellDefinition s = CommRPG.engineConfiguratior.getSpellFactory().createSpell(spellId);
		if (s != null) {
			s.setSpellIcon(frm.getSpriteSheet("spellicons").getSubImage(s.getSpellIconId(), 0));
			spells.put(spellId, s);
		}
	}

	public static SpellDefinition getSpell(String spellId, FCResourceManager frm)
	{
		return spells.get(spellId);
	}
}
