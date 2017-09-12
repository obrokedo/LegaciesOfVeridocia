package mb.fc.game.resource;

import java.util.Hashtable;

import mb.fc.game.definition.ItemDefinition;
import mb.fc.game.item.Item;
import mb.fc.loading.FCResourceManager;
import mb.fc.utils.StringUtils;

public class ItemResource
{
	private static Hashtable<Integer, ItemDefinition> itemDefinitionsById = null;

	public static void initialize(Hashtable<Integer, ItemDefinition> itemDefinitionsById)
	{
		ItemResource.itemDefinitionsById = itemDefinitionsById;
	}

	public static Item getItem(int itemId, FCResourceManager fcrm)
	{
		return itemDefinitionsById.get(itemId).getItem(fcrm);
	}

	public static Item getUninitializedItem(int itemId)
	{
		return itemDefinitionsById.get(itemId).getUnintializedItem();
	}
	
	public static int getItemIdByName(String itemName) {
		if (StringUtils.isNotEmpty(itemName))
			return itemDefinitionsById.values().stream().filter(
				id -> id.getUnintializedItem().getName().equals(itemName)).findFirst().get().getId();
		return -1;
	}

	public static void initializeItem(Item item, FCResourceManager fcrm)
	{
		itemDefinitionsById.get(item.getItemId()).initializeItem(item, fcrm);
		// Check to see if the name is null then, if so then this item has be unserialized and
		// needs it's transient fields back into it
		if (item.getName() == null) {
			item.copyItem(getItem(item.getItemId(), fcrm));
		}

		// If there is a spell use defined, initialize it so the
		// spell object can be loaded
		if (item.getSpellUse() != null)
			item.getSpellUse().initialize(fcrm);
	}
}
