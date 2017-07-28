package mb.fc.game.hudmenu;

import mb.fc.engine.CommRPG;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.utils.StringUtils;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class SpriteContextPanel extends Panel
{
	private CombatSprite sprite;

	public SpriteContextPanel(PanelType menuType, CombatSprite sprite, GameContainer gc) {
		super(menuType);
		this.sprite = sprite;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		switch (panelType)
		{
			case PANEL_HEALTH_BAR:
				displayHealth(gc, graphics, 0);
				break;
			case PANEL_ENEMY_HEALTH_BAR:
				displayHealth(gc, graphics, 1);
				break;
			case PANEL_TARGET_HEALTH_BAR:
				displayHealth(gc, graphics, 2);
				break;
			default:
				break;
		}
	}

	private void displayHealth(PaddedGameContainer gc, Graphics graphics, int position)
	{
		// Determine panel width by max hp of entity
		int width = 75;
		int healthWidth = (int) (Math.min(100, sprite.getMaxHP()) * 1.48);
		int mpWidth = (int) (Math.min(100, sprite.getMaxMP()) * 1.48);
		width = Math.max(width, 17 + healthWidth + StringUtils.getStringWidth(sprite.getMaxHP() + " / " + sprite.getMaxHP(), PANEL_FONT));
		width = Math.max(width, 17 + mpWidth + StringUtils.getStringWidth(sprite.getMaxMP() + " / " + sprite.getMaxMP(), PANEL_FONT));
		if (sprite.isHero())
		{
			width = Math.max(width, StringUtils.getStringWidth(sprite.getName() + " Lv " +
					sprite.getLevel(), PANEL_FONT) + 15);
		}
		else
			width = Math.max(width, StringUtils.getStringWidth(sprite.getName(), PANEL_FONT) + 15);
		
		int height = 25 + (sprite.getMaxMP() != 0 ? 10 : 0);
		int x = 0;
		int y = 0;

		if (position == 0)
		{
			x = CommRPG.GAME_SCREEN_SIZE.width - width - 4;
			y = 5;
		}
		else if (position == 1)
		{
			x = 5;
			y = CommRPG.GAME_SCREEN_SIZE.height - height - 4;
		}
		else if (position == 2)
		{
			x = 5;
			y = 5;
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
		
		statBar("HP", sprite.getCurrentHP(), sprite.getMaxHP(), maxStatDigits, healthWidth, largestBarWidth, x + 7, y + 3, graphics, sprite.isHero());
		if (sprite.getMaxMP() != 0)
			statBar("MP", sprite.getCurrentMP(), sprite.getMaxMP(), maxStatDigits, mpWidth, largestBarWidth, x + 7, y + 12, graphics, sprite.isHero());
		
		/*
		StringUtils.drawString("HP", x + 7, y + 3, graphics);

		String currentHP = "" + sprite.getCurrentHP();
		String maxHP = "" + sprite.getMaxHP();
		
		if (sprite.getCurrentHP() < 0)
			currentHP = "0";
		
		
		while (currentHP.length() < maxHP.length())
			currentHP = " " + currentHP;
		
		StringUtils.drawString(currentHP + "/" + sprite.getMaxHP(),
				largestBarWidth ,
				 y + 3, graphics);
		if (sprite.getMaxMP() != 0)
		{
			StringUtils.drawString("MP", x + 7, y + 12, graphics);
			String currentMP = "" + sprite.getCurrentMP();
			String maxMP = "" + sprite.getMaxMP();
			
			StringUtils.drawString(currentMP + "/" + sprite.getMaxMP(),
					largestBarWidth,
					y + 12, graphics);
		}
		graphics.setColor(Color.red);
		graphics.fillRoundRect(x + 22,
				y + 13,
				healthWidth, 7, 2);
		if (sprite.getMaxMP() != 0)
			graphics.fillRoundRect(x + 22,
					y + 22,
					mpWidth, 7, 2);

		graphics.setColor(Color.yellow);
		graphics.fillRoundRect(x + 22,
				y + 13,
				(int) (Math.max(0, sprite.getCurrentHP() * 1.48)),
				7,
				2);
		if (sprite.getMaxMP() != 0)
			graphics.fillRoundRect(x + 22,
					y + 22,
					(int) (Math.max(0, sprite.getCurrentMP() * 1.48)),
					7,
					2);
					*/
	}
	
	private void statBar(String statName, int currStat, int maxStat, int maxDigits,int barWidth, 
			int xValueCoord, int xCoord, int yCoord, Graphics graphics, boolean isHero)
	{
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
				xValueCoord + 1, yCoord, graphics);
		
		graphics.setColor(Color.red);
		graphics.fillRoundRect(xCoord + 15, yCoord + 10, barWidth, 7, 2);
		Color[] colors = new Color[] {Color.yellow, Color.blue, Color.green, Color.black};
		int barIndex = 0;
		do
		{
			graphics.setColor(colors[Math.min(3, barIndex)]);
			graphics.fillRoundRect(xCoord + 15, yCoord + 10, (int) (Math.min(100, Math.max(0, (currStat - barIndex * 100))) * 1.48), 7, 2);
			barIndex++;
		}
		while (currStat - (barIndex * 100) > 0);
	}
}
