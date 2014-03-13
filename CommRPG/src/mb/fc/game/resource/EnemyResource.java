package mb.fc.game.resource;

import java.util.ArrayList;

import mb.fc.game.battle.spell.SpellDescriptor;
import mb.fc.game.sprite.CombatSprite;

public class EnemyResource 
{
	private static int ID = -1;
	
	public static CombatSprite getEnemy(int enemyId)
	{
		ArrayList<SpellDescriptor> spellDs;
		switch (enemyId)
		{		
			case 2:
				spellDs = new ArrayList<SpellDescriptor>();
				spellDs.add(new SpellDescriptor(SpellDescriptor.ID_HEAL, (byte) 1));
				spellDs.add(new SpellDescriptor(SpellDescriptor.ID_AURA, (byte) 1));
				return new CombatSprite(false, "Rat" + Math.abs(ID), "rat", 10, 12, 7, 5, 10, 4, 
						CombatSprite.MOVEMENT_ANIMALS_BEASTMEN, 1, ID--, -1, spellDs);
			case 1:
				 spellDs = new ArrayList<SpellDescriptor>();
				spellDs.add(new SpellDescriptor(SpellDescriptor.ID_BLAZE, (byte) 2));
				return new CombatSprite(false, "Rat" + Math.abs(ID), "rat", 8, 12, 8, 5, 10, 4, 
						CombatSprite.MOVEMENT_ANIMALS_BEASTMEN, 1, ID--, -1, spellDs);
			default:
				return new CombatSprite(false, "Rat" + Math.abs(ID), "rat", 12, 0, 11, 5, 10, 4, 
						CombatSprite.MOVEMENT_ANIMALS_BEASTMEN, 1, ID--, -1, null);
		}
	}
}
