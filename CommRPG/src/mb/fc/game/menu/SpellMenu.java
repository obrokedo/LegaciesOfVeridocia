package mb.fc.game.menu;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleSelectionMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class SpellMenu extends QuadMenu
{
	private Image emptySpot;
	private int selectedLevel = 0;
	private boolean choseSpell = false;

	public SpellMenu(StateInfo stateInfo) {
		super(PanelType.PANEL_SPELL, false, stateInfo);
		emptySpot = stateInfo.getResourceManager().getSpriteSheets().get("spellicons").getSubImage(15, 0);

		this.enabled = new boolean[4];
		this.icons = new Image[4];
		this.text = new String[4];
		this.paintSelectionCursor = true;
	}

	@Override
	public void initialize()
	{
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
	protected void renderTextBox(FCGameContainer gc, Graphics graphics) {
		graphics.setColor(Panel.COLOR_FOREFRONT);

		Panel.drawPanelBox(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 198 + gc.getDisplayPaddingX(),
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 200 - 40, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 75,
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 36 + 18, graphics);

		KnownSpell overSpell = stateInfo.getCurrentSprite().getSpellsDescriptors().get(getSelectedInt());

		graphics.setColor(COLOR_FOREFRONT);
		graphics.drawString(text[getSelectedInt()], CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 205 + gc.getDisplayPaddingX(), CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 195 - 35);
		if (choseSpell)
		{
			graphics.setColor(Color.red);
			graphics.drawRoundRect(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 200 + 5 + gc.getDisplayPaddingX(),
					CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 215 - 35,
					CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 64, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7 + 10, 4);
		}
		else
			selectedLevel = 0;

		for (int i = 0; i < overSpell.getMaxLevel(); i++)
		{
			if (i <= selectedLevel)
			{
				graphics.setColor(Color.yellow);
				graphics.fillRoundRect(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 200 + 10 + i * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 15 + gc.getDisplayPaddingX(),
						CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 215 - 30,
						CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 14, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7, 4);
				graphics.setColor(COLOR_FOREFRONT);
			}
			graphics.drawRoundRect(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 200 + 10 + i * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 15 + gc.getDisplayPaddingX(),
					CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 215 - 30,
					CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 14, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7, 4);
		}
		// graphics.drawString(spellName, 410, 399);
		graphics.drawString("Cost:", CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 205 + gc.getDisplayPaddingX(), CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 215 - 30);

		if (stateInfo.getCurrentSprite().getCurrentMP() < overSpell.getSpell().getCosts()[selectedLevel])
			graphics.setColor(Color.red);
		graphics.drawString(overSpell.getSpell().getCosts()[selectedLevel] + "", CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 245 + gc.getDisplayPaddingX(), CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 215 - 30);
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
