package mb.fc.game.menu;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.item.Item;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ItemMenu extends QuadMenu
{
	private Image emptySpot;

	public ItemMenu(StateInfo stateInfo) {
		super(Panel.PANEL_ITEM, false, stateInfo);
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
					text[i] = item.getName() + "(EQ)";
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
	protected void renderTextBox(FCGameContainer gc, Graphics graphics)
	{
		String[] split = getText(selected).split(" ", 2);

		Panel.drawPanelBox(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 195 + gc.getDisplayPaddingX(),
			CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 195 - 7 * (split.length == 1 ? 0 : 1),
			getTextboxWidth(),
			CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 15 * (split.length == 1 ? 1 : 2) + 12, graphics);

		graphics.setColor(COLOR_FOREFRONT);

		graphics.drawString(split[0], CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 202 + gc.getDisplayPaddingX(),
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 190 + 3 - 7 * (split.length == 1 ? 0 : 1));
		if (split.length > 1)
			graphics.drawString(split[1], CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 202 + gc.getDisplayPaddingX(),
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 205 + 3 - 7);
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

	@Override
	public int getTextboxWidth()
	{
		return CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 95;
	}
}
