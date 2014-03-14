package mb.fc.game.item;

import org.newdawn.slick.Image;

public class EquippableItem extends Item
{
	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_WEAPON = 0;
	public static final int TYPE_ARMOR = 1;
	public static final int TYPE_RING = 2;
	
	public static final int STYLE_SPEAR = 0;
	public static final int STYLE_AXE = 1;
	public static final int STYLE_SWORD = 2;
	public static final int STYLE_STAFF = 3;
	public static final int STYLE_BOW = 4;
	public static final int STYLE_HEAVY = 5;
	public static final int STYLE_MEDIUM = 6;
	public static final int STYLE_LIGHT = 7;
	
	public static final int RANGE_BOW_2_NO_1 = 4;
	public static final int RANGE_BOW_3_NO_1 = 5;
	public static final int RANGE_BOW_3_NO_1_OR_2 = 6;
	
	private int attack, defense, speed, itemType, itemStyle, range;
	private boolean equipped;

	public EquippableItem(String name, int cost, String description,
			 ItemUse itemUse, int itemId, int attack, int defense, int speed, 
			 int range, int itemType, int itemStyle) {
		super(name, cost, description, itemUse, true, itemId);
		this.attack = attack;
		this.defense = defense;
		this.speed = speed;
		this.range = range;
		this.itemType = itemType;
		this.itemStyle = itemStyle;
		this.equipped = false;
	}

	public int getRange() {
		return range;
	}

	public int getAttack() {
		return attack;
	}

	public int getDefense() {
		return defense;
	}

	public int getSpeed() {
		return speed;
	}

	public int getItemType() {
		return itemType;
	}

	public int getItemStyle() {
		return itemStyle;
	}

	public boolean isEquipped() {
		return equipped;
	}

	public void setEquipped(boolean equipped) {
		this.equipped = equipped;
	}
}
