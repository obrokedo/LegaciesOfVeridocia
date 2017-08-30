package mb.fc.game.menu;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.item.Item;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.utils.StringUtils;

public class ItemMenu extends QuadMenu
{
	private Image emptySpot;

	public ItemMenu(StateInfo stateInfo) {
		super(PanelType.PANEL_ITEM, null, false, stateInfo);
		emptySpot = stateInfo.getResourceManager().getSpriteSheet("items").getSprite(17, 1);

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
	protected void renderTextBox(PaddedGameContainer gc, Graphics graphics)
	{
		String[] split = getText(selected).split(" ", 2);

		Panel.drawPanelBox(195,
			195 - 7 * (split.length == 1 ? 0 : 1),
			getTextboxWidth(),
			15 * (split.length == 1 ? 1 : 2) + 12, graphics);

		graphics.setColor(COLOR_FOREFRONT);

		StringUtils.drawString(split[0], 202,
				190 + 3 - 7 * (split.length == 1 ? 0 : 1), graphics);
		if (split.length > 1)
			StringUtils.drawString(split[1], 202,
				205 + 3 - 7, graphics);
	}

	@Override
	public MenuUpdate onBack() {
		stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuback", 1f, false));
		stateInfo.sendMessage(MessageType.SHOW_BATTLEMENU);
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	public MenuUpdate onConfirm() {
		stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuselect", 1f, false));
		stateInfo.sendMessage(new IntMessage(MessageType.SHOW_ITEM_OPTION_MENU, getSelectedInt()));
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	public int getTextboxWidth()
	{
		return 95;
	}
}
