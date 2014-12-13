package mb.fc.game.resource;

import java.util.Hashtable;

import mb.fc.game.definition.HeroDefinition;
import mb.fc.game.sprite.CombatSprite;

public class HeroResource
{
	private static Hashtable<Integer, HeroDefinition> heroDefinitionsById = new Hashtable<Integer, HeroDefinition>();

	public static void initialize(Hashtable<Integer, HeroDefinition> heroDefinitionsById)
	{
		HeroResource.heroDefinitionsById = heroDefinitionsById;
	}

	public static CombatSprite getHero(int heroId)
	{
		return heroDefinitionsById.get(heroId).getHero();
	}
}
