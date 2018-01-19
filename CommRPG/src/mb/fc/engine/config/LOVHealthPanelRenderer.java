package mb.fc.engine.config;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;

import mb.fc.engine.CommRPG;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.utils.StringUtils;

public class LOVHealthPanelRenderer implements HealthPanelRenderer {
	public void displayHealthPanel(FCResourceManager fcrm, CombatSprite sprite, UnicodeFont panelFont, 
			PaddedGameContainer gc, Graphics graphics, PanelLocation position)
	{
		// Determine panel width by max hp of entity
		int width = 75;
		int healthWidth = (int) (Math.min(100, sprite.getMaxHP()) * 1.49);
		int mpWidth = (int) (Math.min(100, sprite.getMaxMP()) * 1.49);
		width = Math.max(width, 17 + healthWidth + StringUtils.getStringWidth(sprite.getMaxHP() + " / " + sprite.getMaxHP(), panelFont));
		width = Math.max(width, 17 + mpWidth + StringUtils.getStringWidth(sprite.getMaxMP() + " / " + sprite.getMaxMP(), panelFont));
		if (sprite.isHero())
		{
			width = Math.max(width, StringUtils.getStringWidth(sprite.getName() + " Lv " +
					sprite.getLevel(), panelFont) + 15);
		}
		else
			width = Math.max(width, StringUtils.getStringWidth(sprite.getName(), panelFont) + 15);
		
		int height = 25 + (sprite.getMaxMP() != 0 ? 10 : 0);
		int x = 0;
		int y = 0;

		switch (position) {
			case HERO_HEALTH:
				x = CommRPG.GAME_SCREEN_SIZE.width - width - 4;
				y = 5;
			break;
			case ENEMY_HEALTH:
				x = 5;
				y = CommRPG.GAME_SCREEN_SIZE.height - height - 4;
			break;
			case TARGET_HEALTH:
				x = 5;
				y = 5;
			break;
		}

		Panel.drawPanelBox(x, y, width, height, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		if (sprite.isHero()) {
			StringUtils.drawString(sprite.getName() + " Lv " +
				sprite.getLevel(), x + 7, y - 5, graphics);
		}
		else
			StringUtils.drawString(sprite.getName(), x + 7, y - 5, graphics);
		
		// Draw health bars
		int largestBarWidth = Math.max(healthWidth, mpWidth) + x + 23;
		
		int maxStatDigits = Math.max(("" + sprite.getMaxHP()).length(), ("" + sprite.getMaxMP()).length());
		
		drawStatBar("HP", Math.max(0, sprite.getCurrentHP()), sprite.getMaxHP(), 
				maxStatDigits, healthWidth, largestBarWidth, x + 7, y + 3, graphics, sprite.isHero(), fcrm);
		if (sprite.getMaxMP() != 0)
			drawStatBar("MP", sprite.getCurrentMP(), sprite.getMaxMP(), 
					maxStatDigits, mpWidth, largestBarWidth, x + 7, y + 12, graphics, sprite.isHero(), fcrm);
	}
	
	private void drawStatBar(String statName, int currStat, int maxStat, int maxDigits,int barWidth, 
			int xValueCoord, int xCoord, int yCoord, Graphics graphics, boolean isHero, FCResourceManager fcrm)
	{
		SpriteSheet healthBar = fcrm.getSpriteSheet("healthbar");
		graphics.setColor(Color.white);
		StringUtils.drawString(statName, xCoord, yCoord, graphics);
		String currStatStr = "" + currStat;
		String maxStatStr = "" + maxStat;
		while (currStatStr.length() < maxDigits)
			currStatStr = " " + currStatStr;
		while (maxStatStr.length() < maxDigits)
			maxStatStr = " " + maxStatStr;
		
		if (maxStat > 100 && !isHero)
			maxStatStr = "???";
		if (currStat > 100 && !isHero)
			currStatStr = "???";
		
		StringUtils.drawString(currStatStr + "/" + maxStatStr,
				xValueCoord + 3, yCoord, graphics);
		
		graphics.drawImage(healthBar.getSprite(3, 0).getScaledCopy(barWidth - 2, 8), xCoord + 18, yCoord + 10);
		
		int barIndex = 0;
		do
		{
			graphics.drawImage(healthBar.getSprite(4 + barIndex, 0).getScaledCopy((int) (Math.min(100, Math.max(0, (currStat - barIndex * 100))) * 1.49), 8), 
					xCoord + 18, yCoord + 10);
			barIndex++;
		}
		while (currStat - (barIndex * 100) > 0);
		
		graphics.drawImage(healthBar.getSprite(0, 0), xCoord + 16, yCoord + 10);
		graphics.drawImage(healthBar.getSprite(1, 0).getScaledCopy(barWidth - 2, 8), xCoord + 18, yCoord + 10);
		graphics.drawImage(healthBar.getSprite(2, 0), xCoord + 16 + barWidth, yCoord + 10);
	}
}
