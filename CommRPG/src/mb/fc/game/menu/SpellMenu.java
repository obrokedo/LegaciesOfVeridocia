package mb.fc.game.menu;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import mb.fc.engine.CommRPG;
import mb.fc.engine.config.SpellMenuRenderer;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleSelectionMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.ui.PaddedGameContainer;

public class SpellMenu extends QuadMenu
{
	private Image emptySpot;
	private int selectedLevel = 0;
	private boolean choseSpell = false;
	private SpellMenuRenderer spellMenuRenderer;

	public SpellMenu(StateInfo stateInfo) {
		super(PanelType.PANEL_SPELL, null, false, stateInfo);
		emptySpot = stateInfo.getResourceManager().getSpriteSheet("spellicons").getSubImage(15, 0);

		this.enabled = new boolean[4];
		this.icons = new Image[4];
		this.text = new String[4];
		this.paintSelectionCursor = true;
		spellMenuRenderer = CommRPG.engineConfiguratior.getSpellMenuRenderer();
	}

	@Override
	public void initialize()
	{
		this.selected = Direction.UP;
		choseSpell = false;
		spellMenuRenderer.spellLevelChanged(0);

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
		spellMenuRenderer.render(text[getSelectedInt()], stateInfo.getCurrentSprite(), stateInfo.getResourceManager(), 
				choseSpell, selectedLevel, 
				stateInfo.getCurrentSprite().getSpellsDescriptors().get(getSelectedInt()), 
				stateInfo, graphics, Panel.COLOR_FOREFRONT);
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
			spellMenuRenderer.spellLevelChanged(selectedLevel);
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
			spellMenuRenderer.spellLevelChanged(selectedLevel);
		}
		return MenuUpdate.MENU_ACTION_LONG;
	}

	@Override
	public MenuUpdate update(long delta, StateInfo stateInfo) {
		spellMenuRenderer.update(delta);
		return super.update(delta, stateInfo);
	}
}
