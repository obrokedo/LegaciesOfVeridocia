package mb.fc.game.resource;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.item.ItemUse;

public class ItemResource 
{
	public static Item getItem(int itemId, StateInfo stateInfo)
	{				
		switch (itemId)
		{
			case 0:
				return new EquippableItem("Short Sword", 30, "A small steel sword", null, false, 0, 9, 0, 0, 1, 
						EquippableItem.TYPE_WEAPON, EquippableItem.STYLE_SWORD, stateInfo.getResourceManager().getSpriteSheets().get("items").getSprite(0, 1));
			case 1:
				return new EquippableItem("Long Sword", 50, "A long steel sword", null, false, 1, 15, 0, 0, 1, EquippableItem.TYPE_WEAPON, EquippableItem.STYLE_SWORD,
						stateInfo.getResourceManager().getSpriteSheets().get("items").getSprite(1, 1));
			case 2:
				return new Item("Medical Herb", 10, "Usable: Heals for 10HP", new ItemUse(false, 10, 0, null, 1, 1, "is healed for 10"), true, false, 2,
						stateInfo.getResourceManager().getSpriteSheets().get("items").getSprite(17, 2));
		}
		return null;
	}
}
