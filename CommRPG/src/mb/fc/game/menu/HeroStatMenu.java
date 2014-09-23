package mb.fc.game.menu;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Timer;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class HeroStatMenu extends Menu
{
	private CombatSprite selectedSprite;
	private int x;
	private int y;
	private String gold;
	private int animCount = 0;
	private Timer timer;

	public HeroStatMenu(GameContainer gc, CombatSprite selectedSprite, StateInfo stateInfo) {
		super(Panel.PANEL_HEROS_STATS);
		x = (gc.getWidth() - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 230) / 2;
		y = (gc.getHeight() - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 192) / 2;
		this.selectedSprite = selectedSprite;
		if (selectedSprite.isHero())
			this.gold = stateInfo.getClientProfile().getGold() + "";

		timer = new Timer(500);
	}

	@Override
	public MenuUpdate update(long delta, StateInfo stateInfo) {
		super.update(delta, stateInfo);

		timer.update(delta);

		while (timer.perform())
			animCount = (animCount + 1) % 2;

		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		if (input.isKeyDown(KeyMapping.BUTTON_2))
			return MenuUpdate.MENU_CLOSE;
		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics)
	{
		/*****************************/
		/* Draw the main stat window */
		/*****************************/
		Panel.drawPanelBox(x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 62,
				y, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 168,
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 192, graphics);
		graphics.setColor(COLOR_FOREFRONT);
		graphics.drawString(selectedSprite.getName(), x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 70, y + -3);

		if (selectedSprite.isHero())
		{
			if (selectedSprite.isPromoted())
				graphics.drawString(selectedSprite.getHeroProgression().getPromotedProgression().getClassName(), x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 70 + graphics.getFont().getWidth(selectedSprite.getName()) + 10 *  CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], y + -3);
			else
				graphics.drawString(selectedSprite.getHeroProgression().getUnpromotedProgression().getClassName(), x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 70 + graphics.getFont().getWidth(selectedSprite.getName()) + 10 *  CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], y + -3);
		}

		int statsY = 13;
		if (!selectedSprite.isHero())
			statsY = -2;
		else
		{
			graphics.drawString("LV: " + selectedSprite.getLevel(),
					x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 75, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 13);
			graphics.drawString("XP: " + selectedSprite.getExp(),
					x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 152, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 13);
		}
		graphics.drawString("HP: " + selectedSprite.getCurrentHP() + "/" + selectedSprite.getMaxHP(),
				x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 75, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (statsY + 15));
		graphics.drawString("MP: " + selectedSprite.getCurrentMP() + "/" + selectedSprite.getMaxMP(),
				x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 152, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (statsY + 15));


		graphics.drawString("ATK: " + (selectedSprite.getCurrentAttack() < 10 ? " " : "") + selectedSprite.getCurrentAttack(),
				x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 75, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (statsY + 30));
		graphics.drawString("DEF: " + (selectedSprite.getCurrentDefense() < 10 ? " " : "") + selectedSprite.getCurrentDefense() ,
				x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 152, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (statsY + 30));
		graphics.drawString("SPD: " + (selectedSprite.getCurrentSpeed() < 10 ? " " : "") + selectedSprite.getCurrentSpeed(),
				x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 75, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (statsY + 45));
		graphics.drawString("MOV: " + (selectedSprite.getCurrentMove() < 10 ? " " : "") + selectedSprite.getCurrentMove(),
				x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 152, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (statsY + 45));

		// Draw Spells
		graphics.drawString("MAGIC", x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 70, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 73);
		if (selectedSprite.getSpellsDescriptors() != null)
		{
			for (int i = 0; i < selectedSprite.getSpellsDescriptors().size(); i++)
			{
				graphics.setColor(Panel.COLOR_FOREFRONT);
				KnownSpell sd = selectedSprite.getSpellsDescriptors().get(i);
				graphics.drawImage(sd.getSpell().getSpellIcon(), x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 70, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 92 + i * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 23);
				graphics.drawString(sd.getSpell().getName(), x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 87, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 83 + i * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 23);
				for (int j = 0; j < sd.getMaxLevel(); j++)
				{
					graphics.setColor(Color.yellow);

					graphics.fillRect(x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 87 + j * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 10,
							y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 105 + i * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 22,
							CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7,
							CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7);
				}
			}
		}

		// Draw Items
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawString("ITEM", x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 135, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 73);
		for (int i = 0; i < selectedSprite.getItemsSize(); i++)
		{
			graphics.setColor(Panel.COLOR_FOREFRONT);
			graphics.drawImage(selectedSprite.getItem(i).getImage(), x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 135,
					y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 92 + i * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 24);
			String[] itemSplit = selectedSprite.getItem(i).getName().split(" ");
			graphics.drawString(itemSplit[0], x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 155, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 83 + i * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 23);
			if (itemSplit.length > 1)
				graphics.drawString(itemSplit[1], x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 155, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 90 + i * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 23);

			if (selectedSprite.getEquipped().get(i))
			{
				graphics.setColor(Color.pink);
				graphics.drawString("EQ.", x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 155, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 97 + i * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 23);
			}

		}

		/****************************/
		/* Draw the portrait window	*/
		/****************************/
		if (selectedSprite.getPortraitImage() != null)
		{
			Panel.drawPanelBox(x, y, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 62, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 78, graphics, Color.black);
			graphics.drawImage(selectedSprite.getPortraitImage(), x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7);
		}

		/*****************************/
		/* Draw the statistic window */
		/*****************************/
		Panel.drawPanelBox(x, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 78, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 62, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 80, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawImage(selectedSprite.getAnimationImageAtIndex(selectedSprite.getAnimation("UnDown").frames.get(animCount).sprites.get(0).imageIndex),
				x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 19,
				y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 82);
		graphics.drawString("Kills", x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 13, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 100);
		graphics.drawString("Defeat", x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 10, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 125);
		if (selectedSprite.isHero())
		{
			graphics.drawString(selectedSprite.getKills() + "", x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 28, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 110);
			graphics.drawString(selectedSprite.getDefeat() + "", x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 28, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 135);
		}
		else
		{
			graphics.drawString("?", x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 28, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 110);
			graphics.drawString("?", x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 28, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 135);
		}

		/************************/
		/* Draw the gold window */
		/************************/
		if (selectedSprite.isHero())
		{
			Panel.drawPanelBox(x, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 158, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 62, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 34, graphics);
			graphics.setColor(Panel.COLOR_FOREFRONT);
			graphics.drawString("Gold", x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 18, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 155);
			graphics.drawString(gold, x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 30 - graphics.getFont().getWidth(gold) / 2, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 167);
		}
	}

	@Override
	public boolean makeAddAndRemoveSounds()
	{
		return true;
	}
}
