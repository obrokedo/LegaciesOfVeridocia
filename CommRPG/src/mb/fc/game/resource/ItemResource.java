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

	public static Item getUninitializedItem(int itemId)
	{
		return itemDefinitionsById.get(itemId).getUnintializedItem();
	}

	public static void initializeItem(Item item, StateInfo stateInfo)
	{
		itemDefinitionsById.get(item.getItemId()).initializeItem(item, stateInfo);
		// Check to see if the name is null then, if so then this item has be unserialized and
		// needs it's transient fields back into it
		if (item.getName() == null) {
			item.copyItem(getItem(item.getItemId(), stateInfo));
		}

		// If there is a spell use defined, initialize it so the
		// spell object can be loaded
		if (item.getSpellUse() != null)
			item.getSpellUse().initialize(stateInfo);
	}
}
