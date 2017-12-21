package mb.fc.game.resource;

import java.util.Hashtable;

import mb.fc.engine.CommRPG;
import mb.fc.game.battle.spell.SpellDefinition;
import mb.fc.game.exception.BadResourceException;
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
			if (frm != null)
				s.setSpellIcon(frm.getSpriteSheet("spellicons").getSubImage(s.getSpellIconId(), 0));
			spells.put(spellId, s);
		}
	}

	public static SpellDefinition getSpell(String spellId)
	{
		SpellDefinition sd = spells.get(spellId);
		if (sd == null)
			throw new BadResourceException("The spell with id: " + spellId + 
					" has not been correctly configured and does not exist. Check to make sure the spell has been implemented");
		return sd;
	}
}
