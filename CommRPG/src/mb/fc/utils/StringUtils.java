package mb.fc.utils;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;

import mb.fc.engine.CommRPG;

public class StringUtils {
	public static boolean isEmpty(String str)
	{
		return (str == null || str.trim().length() == 0 || str.trim().equalsIgnoreCase("null"));
	}
	
	public static boolean isNotEmpty(String str)
	{
		return !isEmpty(str);
	}
	
	public static void drawString(String string, int x, int y, Graphics g)
	{
		g.resetTransform();
		g.drawString(string, x * CommRPG.GAME_SCREEN_SCALE + CommRPG.GAME_SCREEN_PADDING, y * CommRPG.GAME_SCREEN_SCALE);
		g.translate(CommRPG.GAME_SCREEN_PADDING, 0);
		g.scale(CommRPG.GAME_SCREEN_SCALE, CommRPG.GAME_SCREEN_SCALE);
	}
	
	public static int getStringWidth(String string, UnicodeFont font)
	{
		return font.getWidth(string) / CommRPG.GAME_SCREEN_SCALE;
	}
}
