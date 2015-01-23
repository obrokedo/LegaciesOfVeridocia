package mb.fc.game.definition;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.item.ItemUse;
import mb.fc.utils.XMLParser.TagArea;

public class ItemDefinition
{
	private Item item;
	private int imageX;
	private int imageY;
	private int id;

	private ItemDefinition() {}

	public static ItemDefinition parseItemDefinition(TagArea tagArea)
	{
		int attack = 0, defense = 0, speed = 0, style = 0, type = 0, range = 0;
		boolean equippable = false;
		ItemUse itemUse = null;
		for (TagArea childTagArea : tagArea.getChildren())
		{
			if (childTagArea.getTagType().equalsIgnoreCase("equippable"))
			{
				equippable = true;
				attack = Integer.parseInt(childTagArea.getAttribute("attack"));
				defense = Integer.parseInt(childTagArea.getAttribute("defense"));
				speed = Integer.parseInt(childTagArea.getAttribute("speed"));
				style = Integer.parseInt(childTagArea.getAttribute("style"));
				type = Integer.parseInt(childTagArea.getAttribute("type"));
				range = Integer.parseInt(childTagArea.getAttribute("range"));
			}
			else if (childTagArea.getTagType().equalsIgnoreCase("use"))
			{
				itemUse = new ItemUse(Boolean.parseBoolean(childTagArea.getAttribute("targetsenemy")), Integer.parseInt(childTagArea.getAttribute("damage")),
						Integer.parseInt(childTagArea.getAttribute("mpdamage")), null,  Integer.parseInt(childTagArea.getAttribute("range")),
						Integer.parseInt(childTagArea.getAttribute("area")),  childTagArea.getAttribute("text"), Boolean.parseBoolean(childTagArea.getAttribute("singleuse")));
			}
		}

		ItemDefinition id = new ItemDefinition();

		id.id = Integer.parseInt(tagArea.getAttribute("id"));

		if (equippable)
			id.item = new EquippableItem(tagArea.getAttribute("name"), Integer.parseInt(tagArea.getAttribute("cost")), tagArea.getAttribute("description"),
					itemUse, id.id, attack, defense, speed, range, type, style);
		else
			id.item = new Item(tagArea.getAttribute("name"), Integer.parseInt(tagArea.getAttribute("cost")), tagArea.getAttribute("description"),
						itemUse, false, id.id);

		id.imageX = Integer.parseInt(tagArea.getAttribute("imageindexx"));
		id.imageY = Integer.parseInt(tagArea.getAttribute("imageindexy"));

		return id;
	}

	public Item getItem(StateInfo stateInfo)
	{
		initializeItem(item, stateInfo);
		return item;
	}

	public Item getUnintializedItem()
	{
		return item;
	}

	public void initializeItem(Item i, StateInfo stateInfo)
	{
		i.setImage(stateInfo.getResourceManager().getSpriteSheets().get("items").getSprite(imageX, imageY));
	}

	public int getId() {
		return id;
	}
}
