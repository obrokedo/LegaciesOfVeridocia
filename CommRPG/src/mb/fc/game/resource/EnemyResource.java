package mb.fc.game.resource;

import java.util.Hashtable;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.definition.EnemyDefinition;
import mb.fc.game.sprite.CombatSprite;

public class EnemyResource
{
	private static int ID = -1;

	private static Hashtable<Integer, EnemyDefinition> enemyDefinitionsById = null;

	public static void initialize(Hashtable<Integer, EnemyDefinition> enemyDefinitionsById)
	{
		EnemyResource.enemyDefinitionsById = enemyDefinitionsById;
	}

	public static CombatSprite getEnemy(int enemyId, StateInfo stateInfo)
	{
		return enemyDefinitionsById.get(enemyId).getEnemy(stateInfo, ID--);
	}

	public static int getPortraitIndex(int enemyId)
	{
		return enemyDefinitionsById.get(enemyId).getPortrait();
	}
}
