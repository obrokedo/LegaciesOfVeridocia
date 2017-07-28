package mb.fc.game.menu;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleSelectionMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.utils.StringUtils;

public class SpellMenu extends QuadMenu
{
	private Image emptySpot;
	private int selectedLevel = 0;
	private boolean choseSpell = false;

	public SpellMenu(StateInfo stateInfo) {
		super(PanelType.PANEL_SPELL, false, stateInfo);
		emptySpot = stateInfo.getResourceManager().getSpriteSheet("spellicons").getSubImage(15, 0);

		this.enabled = new boolean[4];
		this.icons = new Image[4];
		this.text = new String[4];
		this.paintSelectionCursor = true;
	}

	@Override
	public void initialize()
	{
		this.selected = Direction.UP;
		choseSpell = false;

		for (int i = 0; i < 4; i++)
		{
			if (i < stateInfo.getCurrentSprite().getSpellsDescriptors().size())
			{
				enabled[i] = true;
				icons[i] = stateInfo.getCurrentSprite().getSpellsDescriptors().get(i).getSpell().getSpellIcon();
				text[i] = stateInfo.getCurrentSprite().getSpellsDescriptors().get(i).getSpell().getName();
			}
			else
			{
				enabled[i] = false;
				icons[i] = emptySpot;
			}
		}
	}

	@Override
	protected MenuUpdate onBack() {
		stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuback", 1f, false));
		if (choseSpell)
		{
			choseSpell = false;
			return MenuUpdate.MENU_ACTION_LONG;
		}
		else
		{
			stateInfo.sendMessage(MessageType.SHOW_BATTLEMENU);
			return MenuUpdate.MENU_CLOSE;
		}
	}

	@Override
	protected MenuUpdate onConfirm() {
		if (choseSpell)
		{
			if (stateInfo.getCurrentSprite().getSpellsDescriptors().get(getSelectedInt()).getSpell().getCosts()[selectedLevel] <= stateInfo.getCurrentSprite().getCurrentMP())
			{
				stateInfo.sendMessage(new BattleSelectionMessage(MessageType.SELECT_SPELL, getSelectedInt(), selectedLevel + 1));
				return MenuUpdate.MENU_CLOSE;
			}
			else
			{
				stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, MUSIC_SELECTOR.getInvalidActionSoundEffect(), 1f, false));
				// stateInfo.sendMessage(new ChatMessage(MessageType.SEND_INTERNAL_MESSAGE, "SYSTEM", "SYSTEM: You do not have enough MP to cast that spell"));
				return MenuUpdate.MENU_ACTION_LONG;
			}
		}
		else
		{
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuselect", 1f, false));
			choseSpell = true;
			selectedLevel = 0;
			return MenuUpdate.MENU_ACTION_LONG;
		}
	}

	@Override
	protected void renderTextBox(PaddedGameContainer gc, Graphics graphics) {
		graphics.setColor(Panel.COLOR_FOREFRONT);

		Panel.drawPanelBox(198,
				200 - 40, 75,
				36 + 18, graphics);

		KnownSpell overSpell = stateInfo.getCurrentSprite().getSpellsDescriptors().get(getSelectedInt());

		graphics.setColor(COLOR_FOREFRONT);
		StringUtils.drawString(text[getSelectedInt()], 205, 160, graphics);
		if (choseSpell)
		{
			graphics.setColor(Color.red);
			graphics.setLineWidth(2);
			graphics.drawRoundRect(204,
					183,
					64, 11, 4);
		}
		else
			selectedLevel = 0;

		for (int i = 0; i < overSpell.getMaxLevel(); i++)
		{
			if (i <= selectedLevel)
			{
				graphics.setColor(Color.yellow);
				graphics.fillRoundRect(206 + i * 15,
						185,
						14, 7, 4);
				graphics.setColor(COLOR_FOREFRONT);
			}
			graphics.drawRoundRect(206 + i * 15,
					185,
					14, 7, 4);
		}
		// graphics.drawString(spellName, 410, 399);
		StringUtils.drawString("Cost:", 205, 185, graphics);

		if (stateInfo.getCurrentSprite().getCurrentMP() < overSpell.getSpell().getCosts()[selectedLevel])
			graphics.setColor(Color.red);
		StringUtils.drawString(overSpell.getSpell().getCosts()[selectedLevel] + "", 245, 185, graphics);
	}

	@Override
	protected MenuUpdate onUp() {
		if (!choseSpell)
			return super.onUp();
		else
			return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	protected MenuUpdate onDown() {
		if (!choseSpell)
			return super.onDown();
		else
			return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	protected MenuUpdate onLeft() {
		if (!choseSpell)
			return super.onLeft();
		else if (selectedLevel > 0)
		{
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menumove", 1f, false));
			selectedLevel--;
		}
		return MenuUpdate.MENU_ACTION_LONG;
	}

	@Override
	protected MenuUpdate onRight() {
		if (!choseSpell)
			return super.onRight();
		else if (selectedLevel + 1 < stateInfo.getCurrentSprite().getSpellsDescriptors().get(getSelectedInt()).getMaxLevel())
		{
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menumove", 1f, false));
			selectedLevel++;
		}
		return MenuUpdate.MENU_ACTION_LONG;
	}
}
