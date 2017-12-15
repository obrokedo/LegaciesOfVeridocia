package mb.fc.game.menu;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleSelectionMessage;
import mb.fc.engine.message.IntMessage;
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

	public ItemOptionMenu(StateInfo stateInfo) {
		super(PanelType.PANEL_ITEM_OPTIONS, stateInfo);

		icons = new Image[8];

		for (int i = 0; i < icons.length; i++)
			icons[i] = stateInfo.getResourceManager().getSpriteSheet("actionicons").getSubImage(i % 4 + 7, i / 4);
		enabled = new boolean[] {false, true, false, true};
		text = new String[] {"Use", "Give", "Equip", "Drop"};
	}

	@Override
	public void initialize() {

		for (int i = 0; i < stateInfo.getCurrentSprite().getItemsSize(); i++) {
			Item item = stateInfo.getCurrentSprite().getItem(i);
			if (item.isUsuable() && item.getDurability() != ItemDurability.BROKEN) {
				enabled[0] = true;
			} 
			if(item.isEquippable() && 
					stateInfo.getCurrentSprite().isEquippable((EquippableItem) item) &&
					item.getDurability() != ItemDurability.BROKEN) {
				enabled[2] = true;
			}
		}
		
		selected = Direction.UP;
		if (!enabled[0])
			selected = Direction.LEFT;
		

		/*
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
			*/
	}

	@Override
	protected MenuUpdate onBack() {
		stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuback", 1f, false));
		stateInfo.sendMessage(MessageType.SHOW_BATTLEMENU);
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	protected MenuUpdate onConfirm() {
		stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuselect", 1f, false));
		stateInfo.sendMessage(new IntMessage(MessageType.SHOW_ITEM_MENU, getSelectedInt()));
		return MenuUpdate.MENU_CLOSE;
	}

}
