package lov.engine.config;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import tactical.engine.config.PanelRenderer;
import tactical.game.hudmenu.Panel;
import tactical.loading.ResourceManager;

public class LOVPanelRenderer implements PanelRenderer {

	private SpriteSheet menuBorder = null;
	private Color fillColor = new Color(0, 32, 96);
	
	@Override
	public void render(int x, int y, int width, int height, Graphics graphics, Color fillColor) {
		graphics.setFont(Panel.PANEL_FONT);
		if (fillColor == null)
			graphics.setColor(this.fillColor);
		else
			graphics.setColor(fillColor);

		graphics.fillRect(x, y, width, height);
				
		int imageSize = menuBorder.getSprite(0, 0).getWidth();
		
		// Bottom
		menuBorder.getSprite(4, 0).draw(x, y + height - imageSize, width, imageSize);		
		// Top bar
		menuBorder.getSprite(5, 0).draw(x, y, width, imageSize);
		// Left Side
		menuBorder.getSprite(6, 0).draw(x, y + imageSize, imageSize, height - imageSize * 2);
		// Right side
		menuBorder.getSprite(7, 0).draw(x + width - imageSize, y + imageSize, imageSize, height - imageSize * 2);

		menuBorder.getSprite(0, 0).draw(x, y + height - imageSize);
		menuBorder.getSprite(1, 0).draw(x, y);
		
		menuBorder.getSprite(2, 0).draw(x + width - imageSize, y + height - imageSize);
		menuBorder.getSprite(3, 0).draw(x + width - imageSize, y);
	}
	
	@Override
	public void initializeResources(ResourceManager rm) {
		menuBorder = rm.getSpriteSheet("menuborder");		
	}
}
