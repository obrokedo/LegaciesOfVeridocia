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
	private transient boolean shouldEquip;
	private boolean singleUse;
	
	/**
	 * A constructor to create an item before resoures have been loaded. This should only
	 * be used for the starting heroes
	 * 
	 * @param itemId The item id
	 * @param shouldEquip A boolean indicating whether this item should be equipped once the sprite is initialized
	 */
	public Item(int itemId, boolean shouldEquip)
	{
		this.itemId = itemId;
		this.shouldEquip = shouldEquip;
	}
	
	public Item(String name, int cost, String description, ItemUse itemUse, boolean singleUse,
			boolean isEquippable, int itemId, Image image) {
		super();
		this.name = name;
		this.cost = cost;
		this.description = description;
		this.isUsuable = itemUse != null;
		this.isEquippable = isEquippable;
		this.itemUse = itemUse;
		this.itemId = itemId;
		this.image = image;
		this.singleUse = singleUse;
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

	public boolean isShouldEquip() {
		return shouldEquip;
	}

	public boolean isSingleUse() {
		return singleUse;
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

	public Image getImage() {
		return image;
	}
}
