package mb.fc.utils;

import java.awt.Font;

import javax.swing.JOptionPane;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import mb.fc.engine.CommRPG;
import mb.fc.game.exception.BadResourceException;

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
	
	public static UnicodeFont loadFont(String fontName, int size, boolean bold, boolean italic) {
		Font awtFont = new Font(fontName, 0, 0);
		UnicodeFont ufont = new UnicodeFont(awtFont, size, bold, italic);
		ufont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		ufont.addAsciiGlyphs();
		ufont.addGlyphs(400, 600);
		try
		{
			ufont.loadGlyphs();
			return ufont;
		} catch (SlickException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred trying to load font glyphs:" + e.getMessage(),
					"Error loading font glyphs", JOptionPane.ERROR_MESSAGE);
			throw new BadResourceException(e);
		}
	}
}
