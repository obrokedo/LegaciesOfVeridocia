package mb.fc.game.menu;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.item.Item;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ItemMenu extends QuadMenu
{
	private Image emptySpot;
	
	public ItemMenu(StateInfo stateInfo) {
		super(Panel.PANEL_ITEM, stateInfo);
		emptySpot = stateInfo.getResourceManager().getSpriteSheets().get("items").getSprite(17, 1);
		
		this.enabled = new boolean[4];
		this.icons = new Image[4];
		this.text = new String[4];
		this.paintSelectionCursor = true;
	}

	@Override
	public void initialize() {		
		selected = Direction.UP;
		
		for (int i = 0; i < 4; i++)
		{
			if (i < stateInfo.getCurrentSprite().getItemsSize())
			{
				Item item = stateInfo.getCurrentSprite().getItem(i);
				enabled[i] = true;
				if (stateInfo.getCurrentSprite().getEquipped().get(i))
					text[i] = item.getName() + " (EQ)";
				else
					text[i] = item.getName();
				
				icons[i] = item.getImage();
			}
			else
			{
				enabled[i] = false;
				icons[i] = emptySpot;
			}
		}
	}

	@Override
	public MenuUpdate onBack() {
		stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuback", 1f, false));
		stateInfo.sendMessage(Message.MESSAGE_SHOW_BATTLEMENU);
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	public MenuUpdate onConfirm() {
		stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
		stateInfo.sendMessage(new IntMessage(Message.MESSAGE_SHOW_ITEM_OPTION_MENU, getSelectedInt()));			
		return MenuUpdate.MENU_CLOSE;
	}
	
	public int getTextboxWidth()
	{
		return CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 120;
	}
}
