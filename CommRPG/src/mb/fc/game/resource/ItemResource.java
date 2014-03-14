package mb.fc.game.resource;

import java.util.Hashtable;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.definition.ItemDefinition;
import mb.fc.game.item.Item;

public class ItemResource 
{
	private static Hashtable<Integer, ItemDefinition> itemDefinitionsById = null;
	
	public static void initialize(Hashtable<Integer, ItemDefinition> itemDefinitionsById)
	{
		ItemResource.itemDefinitionsById = itemDefinitionsById;
	}
	
	public static Item getItem(int itemId, StateInfo stateInfo)
	{						
		return itemDefinitionsById.get(itemId).getItem(stateInfo);
	}
}
