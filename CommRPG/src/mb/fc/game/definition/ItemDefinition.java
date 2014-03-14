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
		int attack = 0, defense = 0, speed = 0, style = 0, type=0, range = 0;
		boolean equippable = false;
		boolean singleUse = false;
		ItemUse itemUse = null;
		for (TagArea childTagArea : tagArea.getChildren())
		{
			if (childTagArea.getTagType().equalsIgnoreCase("equippable"))
			{
				equippable = true;
				attack = Integer.parseInt(childTagArea.getParams().get("attack"));
				defense = Integer.parseInt(childTagArea.getParams().get("defense"));
				speed = Integer.parseInt(childTagArea.getParams().get("speed"));
				style = Integer.parseInt(childTagArea.getParams().get("style"));
				type = Integer.parseInt(childTagArea.getParams().get("type"));
				range = Integer.parseInt(childTagArea.getParams().get("range"));
			}
			else if (childTagArea.getTagType().equalsIgnoreCase("use"))
			{
				itemUse = new ItemUse(Boolean.parseBoolean(childTagArea.getParams().get("targetsenemy")), Integer.parseInt(childTagArea.getParams().get("damage")), 
						Integer.parseInt(childTagArea.getParams().get("mpdamage")), null,  Integer.parseInt(childTagArea.getParams().get("range")), 
						Integer.parseInt(childTagArea.getParams().get("area")),  childTagArea.getParams().get("text"), Boolean.parseBoolean(childTagArea.getParams().get("singleuse")));
			}
		}
		
		ItemDefinition id = new ItemDefinition();
		
		id.id = Integer.parseInt(tagArea.getParams().get("id"));
		
		if (equippable)
			id.item = new EquippableItem(tagArea.getParams().get("name"), Integer.parseInt(tagArea.getParams().get("cost")), tagArea.getParams().get("description"), 
					itemUse, id.id, attack, defense, speed, range, type, style);
		else
			id.item = new Item(tagArea.getParams().get("name"), Integer.parseInt(tagArea.getParams().get("cost")), tagArea.getParams().get("description"), 
						itemUse, false, Integer.parseInt(tagArea.getParams().get("id")));
		
		id.imageX = Integer.parseInt(tagArea.getParams().get("imageindexx"));
		id.imageY = Integer.parseInt(tagArea.getParams().get("imageindexy"));
		
		return id;
	}
	
	public Item getItem(StateInfo stateInfo)
	{
		item.setImage(stateInfo.getResourceManager().getSpriteSheets().get("items").getSprite(imageX, imageY));
		return item;
	}

	public int getId() {
		return id;
	}
}
