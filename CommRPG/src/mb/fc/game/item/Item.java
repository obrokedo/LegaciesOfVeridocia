package mb.fc.game.item;

import java.io.Serializable;

import org.newdawn.slick.Image;

public class Item implements Serializable
{
	private static final long serialVersionUID = 1L;

	private transient String name = null;
	private transient int cost;
	private transient String description;
	private transient boolean isUsuable;
	private transient boolean isEquippable;
	private transient Image image;
	private transient ItemUse itemUse;
	private transient SpellItemUse spellUse;
	private int itemId;
	private ItemDurability durability = ItemDurability.PERFECT;
	private boolean useDamagesItem = false;

	public Item(String name, int cost, String description, ItemUse itemUse, SpellItemUse spellUse,
			boolean isEquippable, boolean useDamagesItem, int itemId) {
		super();
		this.name = name;
		this.cost = cost;
		this.description = description;
		this.isUsuable = ((itemUse != null) || (spellUse != null));
		this.spellUse = spellUse;
		this.isEquippable = isEquippable;
		this.useDamagesItem = useDamagesItem;
		this.itemUse = itemUse;
		this.itemId = itemId;
		this.durability = ItemDurability.PERFECT;
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

	public SpellItemUse getSpellUse() {
		return spellUse;
	}

	public boolean useDamagesItem() {
		return useDamagesItem;
	}

	public static EquippableDifference getEquippableDifference(EquippableItem oldItem, EquippableItem newItem)
	{
		return new EquippableDifference(oldItem, newItem);
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}

	public ItemDurability getDurability() {
		return durability;
	}

	public void setDurability(ItemDurability durability) {
		this.durability = durability;
	}

	public void damageItem() {
		switch (durability) {
			case PERFECT:
				durability = ItemDurability.DAMAGED;
				break;
			case DAMAGED:
				durability = ItemDurability.BROKEN;
				break;
			case BROKEN:
				break;
		}
	}

	public void repairItem() {
		durability = ItemDurability.PERFECT;
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

	private enum ItemDurability {
		PERFECT,
		DAMAGED,
		BROKEN
	}

	public void copyItem(Item item) {
		this.cost = item.cost;
		this.description = item.description;
		this.isEquippable = item.isEquippable;
		this.isUsuable = item.isUsuable;
		this.itemUse = item.itemUse;
		this.spellUse = item.spellUse;
		this.name = item.name;
	}
}
