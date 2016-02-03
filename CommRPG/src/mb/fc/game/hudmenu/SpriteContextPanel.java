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
			case Panel.PANEL_TARGET_HEALTH_BAR:
				displayHealth(gc, graphics, 2);
				break;
		}
	}

	private void displayHealth(FCGameContainer gc, Graphics graphics, int position)
	{
		// Determine panel width by max hp of entity
		int width = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 75;
		int healthWidth = (int) (sprite.getMaxHP() * .75 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
		int mpWidth = (int) (sprite.getMaxMP() * .75 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
		width = Math.max(width, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 60 + healthWidth);
		width = Math.max(width, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 60 + mpWidth);
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
		else if (position == 1)
		{
			x = 5;
			y = gc.getHeight() - height - 5;
		}
		else if (position == 2)
		{
			x = 5;
			y = 5;
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
}
