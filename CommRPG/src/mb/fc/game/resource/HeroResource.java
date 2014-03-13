package mb.fc.game.resource;

import mb.fc.game.battle.spell.SpellDescriptor;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.HeroProgression;
import mb.fc.game.sprite.Progression;

public class HeroResource 
{
	public static CombatSprite getHero(int heroId)
	{
		switch (heroId)
		{
			default:
				HeroProgression progression = new HeroProgression(new int[][] {{SpellDescriptor.ID_BLAZE, 2, 6, 16, 27}}, 
						new Progression(new int[] {EquippableItem.STYLE_SWORD}, new int[] {EquippableItem.STYLE_LIGHT, 
								EquippableItem.STYLE_MEDIUM, EquippableItem.STYLE_HEAVY}, 5, HeroProgression.STAT_AVERAGE,
								HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE,
								HeroProgression.STAT_AVERAGE), 
						new Progression(new int[] {EquippableItem.STYLE_SWORD}, new int[] {EquippableItem.STYLE_LIGHT, 
								EquippableItem.STYLE_MEDIUM, EquippableItem.STYLE_HEAVY}, 5, HeroProgression.STAT_AVERAGE,
								HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE,
								HeroProgression.STAT_AVERAGE));
				
				return new CombatSprite(true, "Chaz", "chaz", progression, 12, 5, 12, 7, 8, 
						progression.getUnpromotedProgression().getMove(), CombatSprite.MOVEMENT_WALKING, 1, 0, 1, null);
		}
	}
}
