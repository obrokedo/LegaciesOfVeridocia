package mb.fc.game.menu;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleSelectionMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.item.Item.ItemDurability;

import org.newdawn.slick.Image;

public class ItemOptionMenu extends QuadMenu
{
	private int itemIndex;
	private Item item;

	public ItemOptionMenu(StateInfo stateInfo) {
		super(PanelType.PANEL_ITEM_OPTIONS, stateInfo);

		icons = new Image[8];

		for (int i = 0; i < icons.length; i++)
			icons[i] = stateInfo.getResourceManager().getSpriteSheet("actionicons").getSubImage(i % 4 + 7, i / 4);
		enabled = new boolean[4];
		enabled[1] = true;
		enabled[3] = true;
		text = new String[] {"Use", "Give", "Equip", "Drop"};
	}

	public void initialize(int itemIndex)
	{
		this.itemIndex = itemIndex;
		this.initialize();
	}

	@Override
	public void initialize() {

		this.item = stateInfo.getCurrentSprite().getItem(itemIndex);

		selected = Direction.LEFT;

		if (item.isEquippable() && item.getDurability() != ItemDurability.BROKEN)
		{
			selected = Direction.RIGHT;
			enabled[2] = true;
		}
		else
			enabled[2] = false;

		if (item.isUsuable() && item.getDurability() != ItemDurability.BROKEN)
		{
			selected = Direction.UP;
			enabled[0] = true;
		}
		else
			enabled[0] = false;
	}

	@Override
	protected MenuUpdate onBack() {
		stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuback", 1f, false));
		stateInfo.sendMessage(MessageType.SHOW_ITEM_MENU);
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	protected MenuUpdate onConfirm() {
		stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuselect", 1f, false));
		switch (selected)
		{
			case UP:
				stateInfo.sendMessage(new BattleSelectionMessage(MessageType.SELECT_ITEM, itemIndex));
				return MenuUpdate.MENU_CLOSE;
			case LEFT:
				// stateInfo.sendMessage(MessageType.SHOW_SPELLMENU);
				return MenuUpdate.MENU_CLOSE;
			case RIGHT:
				stateInfo.getCurrentSprite().equipItem((EquippableItem) item);
				stateInfo.sendMessage(MessageType.SHOW_ITEM_MENU);
				stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuselect", 1f, false));
				return MenuUpdate.MENU_CLOSE;
			case DOWN:
				stateInfo.getCurrentSprite().removeItem(item);
				if (item.isDeal())
					stateInfo.getClientProgress().getDealItems().add(item.getItemId());
				if (stateInfo.getCurrentSprite().getItemsSize() > 0)
					stateInfo.sendMessage(MessageType.SHOW_ITEM_MENU);
				else
					stateInfo.sendMessage(MessageType.SHOW_BATTLEMENU);
				stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuselect", 1f, false));
				return MenuUpdate.MENU_CLOSE;
		}
		return MenuUpdate.MENU_NO_ACTION;
	}

}
