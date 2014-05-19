package mb.fc.game.hudmenu;

import mb.fc.engine.CommRPG;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class SpriteContextPanel extends Panel
{
	private CombatSprite sprite;

	public SpriteContextPanel(int menuType, CombatSprite sprite, GameContainer gc) {
		super(menuType);
		this.sprite = sprite;

		switch (menuType)
		{
			case Panel.PANEL_STATS:
				// this.menuActions.add(new MenuAction(MenuAction.MA_CLOSE_WINDOW, new Rectangle((gc.getWidth() - 500) / 2 + 475,
					//	(gc.getHeight() - 400) / 2 + 2, 10, 10), true));
				break;
		}
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) {
		switch (panelType)
		{
			case Panel.PANEL_HEALTH_BAR:
				displayHealth(gc, graphics, 0);
				break;
			case Panel.PANEL_ENEMY_HEALTH_BAR:
				displayHealth(gc, graphics, 1);
				break;
			case Panel.PANEL_STATS:
				displayStatPanel(gc, graphics);
				break;
		}
	}

	private void displayHealth(FCGameContainer gc, Graphics graphics, int position)
	{
		// Determine panel width by max hp of entity
		int width = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 75;
		int healthWidth = (int) (sprite.getMaxHP() * .75 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
		int mpWidth = (int) (sprite.getMaxMP() * .75 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
		width = Math.max(width, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 57 + healthWidth);
		width = Math.max(width, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 57 + mpWidth);
		if (sprite.isHero())
		{
			width = Math.max(width, PANEL_FONT.getWidth(sprite.getName() + " Lv " +
					sprite.getLevel()) + 10 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
		}
		else
			width = Math.max(width, PANEL_FONT.getWidth(sprite.getName()) + 10 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
		int height = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 27 + (sprite.getMaxMP() != 0 ? CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 10 : 0);
		int x = 0;
		int y = 0;

		if (position == 0)
		{
			x = gc.getWidth() - gc.getDisplayPaddingX() - width - 5;
			y = 5;
		}
		else
		{
			x = 5;
			y = gc.getHeight() - height - 5;
		}

		Panel.drawPanelBox(x, y, width, height, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		if (sprite.isHero())
			graphics.drawString(sprite.getName() + " Lv " +
					sprite.getLevel(), x + 10, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * -7 + 2);
		else
			graphics.drawString(sprite.getName(), x + 10, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * -7 + 2);
		graphics.drawString("HP", x + 10, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 4 - 1);
		graphics.drawString(sprite.getCurrentHP() + "/" + sprite.getMaxHP(),
				x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 22 + healthWidth,
				 y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 4 - 1);
		if (sprite.getMaxMP() != 0)
		{
			graphics.drawString("MP", x + 10, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 14 - 2);
			graphics.drawString(sprite.getCurrentMP() + "/" + sprite.getMaxMP(),
					x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 22 + mpWidth,
					y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 14 - 2);
		}
		graphics.setColor(Color.red);
		graphics.fillRoundRect(x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 20,
				y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 14 - 2,
				healthWidth,
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 8,
				5);
		if (sprite.getMaxMP() != 0)
			graphics.fillRoundRect(x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 20,
					y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 22 + 1,
					mpWidth,
					CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 8,
					5);

		graphics.setColor(Color.yellow);
		graphics.fillRoundRect(x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 20,
				y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 14 - 2,
				(int) (Math.max(0, sprite.getCurrentHP() * .75 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()])),
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 8,
				5);
		if (sprite.getMaxMP() != 0)
			graphics.fillRoundRect(x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 20,
					y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 22 + 1,
					(int) (Math.max(0, sprite.getCurrentMP() * .75 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()])),
					CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 8,
					5);
	}

	private void displayStatPanel(GameContainer gc, Graphics graphics)
	{
		// TODO This should use the Menu draw method for its' background
		graphics.setColor(Color.darkGray);
		int startX = (gc.getWidth() - 500) / 2;
		int startY = (gc.getHeight() - 400) / 2;
		graphics.fillRoundRect(startX, startY, 500, 400, 15);
		//graphics.fillRect(30, 30, 15, 15);
		//graphics.fillRect(515, 415, 15, 15);
		graphics.setColor(Color.blue);
		graphics.fillRect(startX + 5, startY + 15, 490, 370);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.fillRect(startX + 475, startY + 2, 10, 10);
	}
}
