package mb.fc.game.hudmenu;

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
	
	private void displayHealth(GameContainer gc, Graphics graphics, int position)
	{		
		// Determine panel width by max hp of entity		
		int width = 150;
		int healthWidth = (int) (sprite.getMaxHP() * 1.5);
		int mpWidth = (int) (sprite.getMaxMP() * 1.5);
		width = Math.max(width, 115 + healthWidth);
		width = Math.max(width, 115 + mpWidth);
		int height = 55 + (sprite.getMaxMP() != 0 ? 20 : 0);
		
		if (position == 0)
		{
			Panel.drawPanelBox(gc.getWidth() - width - 5, 5, width, height, graphics);
			graphics.setColor(Panel.COLOR_FOREFRONT);
			graphics.drawString(sprite.getName(), gc.getWidth() - width + 5, -3);
			graphics.drawString("HP", gc.getWidth() - width + 5, 15);		
			graphics.drawString(sprite.getCurrentHP() + "/" + sprite.getMaxHP(), gc.getWidth() - width + 45 + healthWidth, 15);
			if (sprite.getMaxMP() != 0)
			{
				graphics.drawString("MP", gc.getWidth() - width + 5, 31);
				graphics.drawString(sprite.getCurrentMP() + "/" + sprite.getMaxMP(), gc.getWidth() - width + 45 + mpWidth, 31);
			}
			graphics.setColor(Color.red);
			graphics.fillRoundRect(gc.getWidth() - width + 40, 35, healthWidth, 15, 5);
			if (sprite.getMaxMP() != 0)
				graphics.fillRoundRect(gc.getWidth() - width + 40, 51, mpWidth, 15, 5);
			
			graphics.setColor(Color.yellow);
			graphics.fillRoundRect(gc.getWidth() - width + 40, 35, (int) (Math.max(0, sprite.getCurrentHP() * 1.5)), 15, 5);
			if (sprite.getMaxMP() != 0)
				graphics.fillRoundRect(gc.getWidth() - width + 40, 51, (int) (Math.max(0, sprite.getCurrentMP() * 1.5)), 15, 5);
		}
		else
		{
			
			Panel.drawPanelBox(5, gc.getHeight() - height - 5, width, height, graphics);			
			graphics.setColor(Panel.COLOR_FOREFRONT);
			
			graphics.drawString(sprite.getName(), 15, gc.getHeight() - height + -13);
			graphics.drawString("HP", 15, gc.getHeight() - height + 5);		
			graphics.drawString(sprite.getCurrentHP() + "/" + sprite.getMaxHP(), 50 + healthWidth, gc.getHeight() - height + 5);
			if (sprite.getMaxMP() != 0)
			{
				graphics.drawString("MP", 15, gc.getHeight() - height + 21);
				graphics.drawString(sprite.getCurrentMP() + "/" + sprite.getMaxMP(), 50 + mpWidth, gc.getHeight() - height + 21);
			}
			graphics.setColor(Color.red);
			graphics.fillRoundRect(45, gc.getHeight() - height + 25, healthWidth, 15, 5);
			if (sprite.getMaxMP() != 0)
				graphics.fillRoundRect(45, gc.getHeight() - height + 41, mpWidth, 15, 5);
			
			graphics.setColor(Color.yellow);
			graphics.fillRoundRect(45, gc.getHeight() - height + 25, (int) (sprite.getCurrentHP() * 1.5), 15, 5);
			if (sprite.getMaxMP() != 0)
				graphics.fillRoundRect(45, gc.getHeight() - height + 41, (int) (sprite.getCurrentMP() * 1.5), 15, 5);
				
		}
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
