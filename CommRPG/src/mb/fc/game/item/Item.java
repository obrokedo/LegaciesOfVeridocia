package mb.fc.game.item;

import java.io.Serializable;

import org.newdawn.slick.Image;

public class Item implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private transient String name;
	private transient int cost;
	private transient String description;
	private transient boolean isUsuable;
	private transient boolean isEquippable;
	private int itemId;
	private transient Image image;
	private transient ItemUse itemUse;	
	
	public Item(String name, int cost, String description, ItemUse itemUse,
			boolean isEquippable, int itemId) {
		super();
		this.name = name;
		this.cost = cost;
		this.description = description;
		this.isUsuable = itemUse != null;
		this.isEquippable = isEquippable;
		this.itemUse = itemUse;
		this.itemId = itemId;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public int getCost() {
		return cost;
	}

	public String getDescription() {
		return description;
	}

	public boolean isUsuable() {
		return isUsuable;
	}

	public boolean isEquippable() {
		return isEquippable;
	}

	public int getItemId() {
		return itemId;
	}
	
	public ItemUse getItemUse() {
		return itemUse;
	}

	public static EquippableDifference getEquippableDifference(EquippableItem oldItem, EquippableItem newItem)
	{
		return new EquippableDifference(oldItem, newItem);
	}
	
	public static class EquippableDifference
	{
		public int atk, def, spd;
		
		public EquippableDifference(EquippableItem oldItem, EquippableItem newItem)
		{
			if (oldItem != null)
			{
				atk = newItem.getAttack() - oldItem.getAttack();
				def = newItem.getDefense() - oldItem.getDefense();
				spd = newItem.getSpeed() - oldItem.getSpeed();
			}
			else
			{
				atk = newItem.getAttack();
				def = newItem.getDefense();
				spd = newItem.getSpeed();
			}
		}
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}
}
