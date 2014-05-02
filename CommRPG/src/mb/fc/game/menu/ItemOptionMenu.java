package mb.fc.game.menu;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleSelectionMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;

import org.newdawn.slick.Image;

public class ItemOptionMenu extends QuadMenu
{
	private int itemIndex;
	private Item item;

	public ItemOptionMenu(StateInfo stateInfo) {
		super(Panel.PANEL_ITEM_OPTIONS, stateInfo);

		icons = new Image[8];

		for (int i = 0; i < icons.length; i++)
			icons[i] = stateInfo.getResourceManager().getSpriteSheets().get("actionicons").getSubImage(i % 4 + 7, i / 4);
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

		if (item.isEquippable())
		{
			selected = Direction.RIGHT;
			enabled[2] = true;
		}
		else
			enabled[2] = false;

		if (item.isUsuable())
		{
			selected = Direction.UP;
			enabled[0] = true;
		}
		else
			enabled[0] = false;
	}

	@Override
	protected MenuUpdate onBack() {
		stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuback", 1f, false));
		stateInfo.sendMessage(Message.MESSAGE_SHOW_ITEM_MENU);
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	protected MenuUpdate onConfirm() {
		stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
		switch (selected)
		{
			case UP:
				stateInfo.sendMessage(new BattleSelectionMessage(Message.MESSAGE_SELECT_ITEM, itemIndex));
				return MenuUpdate.MENU_CLOSE;
			case LEFT:
				// stateInfo.sendMessage(Message.MESSAGE_SHOW_SPELLMENU);
				return MenuUpdate.MENU_CLOSE;
			case RIGHT:
				stateInfo.getCurrentSprite().equipItem((EquippableItem) item);
				stateInfo.sendMessage(Message.MESSAGE_SHOW_ITEM_MENU);
				stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
				return MenuUpdate.MENU_CLOSE;
			case DOWN:
				stateInfo.getCurrentSprite().removeItem(item);
				if (stateInfo.getCurrentSprite().getItemsSize() > 0)
					stateInfo.sendMessage(Message.MESSAGE_SHOW_ITEM_MENU);
				else
					stateInfo.sendMessage(Message.MESSAGE_SHOW_BATTLEMENU);
				stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
				return MenuUpdate.MENU_CLOSE;
		}
		return MenuUpdate.MENU_NO_ACTION;
	}

}
