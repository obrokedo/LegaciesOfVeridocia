package mb.fc.game.menu;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Timer;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.utils.StringUtils;

public class HeroStatMenu extends Menu
{
	private CombatSprite selectedSprite;
	private int x;
	private int y;
	private String gold;
	private int animCount = 0;
	private Timer timer;
	private Portrait portrait;

	public HeroStatMenu(GameContainer gc, CombatSprite selectedSprite, StateInfo stateInfo) {
		super(PanelType.PANEL_HEROS_STATS);
		x = (CommRPG.GAME_SCREEN_SIZE.width - 230) / 2;
		y = (CommRPG.GAME_SCREEN_SIZE.height - 192) / 2;
		this.selectedSprite = selectedSprite;
		if (selectedSprite.isHero())
			this.gold = stateInfo.getClientProfile().getGold() + "";

		portrait = Portrait.getPortrait(selectedSprite);

		timer = new Timer(500);
	}

	@Override
	public MenuUpdate update(long delta, StateInfo stateInfo) {
		super.update(delta, stateInfo);

		timer.update(delta);

		if (portrait != null)
			portrait.update(delta);

		while (timer.perform())
			animCount = (animCount + 1) % 2;

		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		if (input.isKeyDown(KeyMapping.BUTTON_2)) {
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuback", 1f, false));
			return MenuUpdate.MENU_CLOSE;
		}
		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics)
	{
		/*****************************/
		/* Draw the main stat window */
		/*****************************/
		Panel.drawPanelBox(x + 62,
				y, 168,
				192, graphics);
		graphics.setColor(COLOR_FOREFRONT);
		StringUtils.drawString(selectedSprite.getName(), x + 70, y + -3, graphics);

		if (selectedSprite.isHero())
		{
			StringUtils.drawString(selectedSprite.getCurrentProgression().getClassName(),
					x + 70
						+ StringUtils.getStringWidth(selectedSprite.getName(), PANEL_FONT) + 10, y + -3, graphics);
		}

		int statsY = 13;
		if (!selectedSprite.isHero())
			statsY = -2;
		else
		{
			StringUtils.drawString("LV: " + selectedSprite.getLevel(),
					x + 75, y + 13, graphics);
			StringUtils.drawString("XP: " + selectedSprite.getExp(),
					x + 152, y + 13, graphics);
		}
		StringUtils.drawString("HP: " + selectedSprite.getCurrentHP() + "/" + selectedSprite.getMaxHP(),
				x + 75, y + (statsY + 15), graphics);
		StringUtils.drawString("MP: " + selectedSprite.getCurrentMP() + "/" + selectedSprite.getMaxMP(),
				x + 152, y + (statsY + 15), graphics);


		StringUtils.drawString("ATK: " + (selectedSprite.getCurrentAttack() < 10 ? " " : "") + selectedSprite.getCurrentAttack(),
				x + 75, y + (statsY + 30), graphics);
		StringUtils.drawString("DEF: " + (selectedSprite.getCurrentDefense() < 10 ? " " : "") + selectedSprite.getCurrentDefense() ,
				x + 152, y + (statsY + 30), graphics);
		StringUtils.drawString("SPD: " + (selectedSprite.getCurrentSpeed() < 10 ? " " : "") + selectedSprite.getCurrentSpeed(),
				x + 75, y + (statsY + 45), graphics);
		StringUtils.drawString("MOV: " + (selectedSprite.getCurrentMove() < 10 ? " " : "") + selectedSprite.getCurrentMove(),
				x + 152, y + (statsY + 45), graphics);

		// Draw Spells
		StringUtils.drawString("MAGIC", x + 70, y + 73, graphics);
		if (selectedSprite.getSpellsDescriptors() != null)
		{
			for (int i = 0; i < selectedSprite.getSpellsDescriptors().size(); i++)
			{
				graphics.setColor(Panel.COLOR_FOREFRONT);
				KnownSpell sd = selectedSprite.getSpellsDescriptors().get(i);
				graphics.drawImage(sd.getSpell().getSpellIcon(), x + 70, y + 92 + i * 23);
				StringUtils.drawString(sd.getSpell().getName(), x + 87, y + 83 + i * 23, graphics);
				for (int j = 0; j < sd.getMaxLevel(); j++)
				{
					graphics.setColor(Color.yellow);

					graphics.fillRect(x + 87 + j * 10,
							y + 105 + i * 22,
							7,
							7);
				}
			}
		}

		// Draw Items
		graphics.setColor(Panel.COLOR_FOREFRONT);
		StringUtils.drawString("ITEM", x + 135, y + 73, graphics);
		for (int i = 0; i < selectedSprite.getItemsSize(); i++)
		{
			graphics.setColor(Panel.COLOR_FOREFRONT);
			graphics.drawImage(selectedSprite.getItem(i).getImage(), x + 135,
					y + 92 + i * 24);
			String[] itemSplit = selectedSprite.getItem(i).getName().split(" ");
			StringUtils.drawString(itemSplit[0], x + 155, y + 83 + i * 23, graphics);
			if (itemSplit.length > 1)
				StringUtils.drawString(itemSplit[1], x + 155, y + 90 + i * 23, graphics);

			if (selectedSprite.getEquipped().get(i))
			{
				graphics.setColor(Color.pink);
				StringUtils.drawString("EQ.", x + 155, y + 97 + i * 23, graphics);
			}

		}

		/****************************/
		/* Draw the portrait window	*/
		/****************************/
		if (portrait != null)
		{
			portrait.render(x, y, graphics);
		}

		/*****************************/
		/* Draw the statistic window */
		/*****************************/
		Panel.drawPanelBox(x, y + 78, 62, 80, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawImage(selectedSprite.getAnimationImageAtIndex(selectedSprite.getAnimation("Down").frames.get(animCount).sprites.get(0).imageIndex),
				x + 19,
				y + 82);
		StringUtils.drawString("Kills", x + 13, y + 100, graphics);
		StringUtils.drawString("Defeat", x + 10, y + 125, graphics);
		if (selectedSprite.isHero())
		{
			StringUtils.drawString(selectedSprite.getKills() + "", x + 28, y + 110, graphics);
			StringUtils.drawString(selectedSprite.getDefeat() + "", x + 28, y + 135, graphics);
		}
		else
		{
			StringUtils.drawString("?", x + 28, y + 110, graphics);
			StringUtils.drawString("?", x + 28, y + 135, graphics);
		}

		/************************/
		/* Draw the gold window */
		/************************/
		if (selectedSprite.isHero())
		{
			Panel.drawPanelBox(x, y + 158, 62, 34, graphics);
			graphics.setColor(Panel.COLOR_FOREFRONT);
			StringUtils.drawString("Gold", x + 18, y + 155, graphics);
			StringUtils.drawString(gold, x + 30 - StringUtils.getStringWidth(gold, PANEL_FONT) / 2, y + 167, graphics);
		}
	}
	
	@Override
	public boolean makeAddSounds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean makeRemoveSounds() {
		// TODO Auto-generated method stub
		return true;
	}
}
