package mb.fc.game.hudmenu;

import java.awt.Font;

import mb.fc.engine.CommRPG;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.loading.FCResourceManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.geom.Rectangle;

/**
 * A container to display information to the screen that does not generally need to be interacted with.
 */
public abstract class Panel 
{
	public static final int PANEL_HEALTH_BAR = 0;	
	public static final int PANEL_LAND_EFFECT = 1;
	public static final int PANEL_STATS = 2;
	public static final int PANEL_INITIATIVE = 3;
	public static final int PANEL_ENEMY_HEALTH_BAR = 4;
	public static final int PANEL_TEXT = 5;
	public static final int PANEL_BATTLE = 6;
	public static final int PANEL_MAPMOVE = 7;
	public static final int PANEL_SPELL = 8;
	public static final int PANEL_MAPATTACK = 9;
	public static final int PANEL_CHAT = 10;
	public static final int PANEL_SPEECH = 11;
	public static final int PANEL_WAIT = 12;
	public static final int PANEL_SYSTEM = 13;
	public static final int PANEL_CONNECTIONS = 14;
	public static final int PANEL_SHOP = 15;
	public static final int PANEL_HEROS_OVERVIEW = 16;
	public static final int PANEL_HEROS_STATS = 17;
	public static final int PANEL_YES_NO = 18;
	public static final int PANEL_PRIEST = 19;
	public static final int PANEL_ASSIGN_HERO = 20;
	public static final int PANEL_STRING = 21;
	public static final int PANEL_BATTLE_MOVE = 22;
	public static final int PANEL_ITEM = 23;
	public static final int PANEL_ITEM_OPTIONS = 24;
	public static final int PANEL_DEBUG = 25;
	
	protected int panelType;	
	public final static Color COLOR_MOUSE_OVER = new Color(0, 0, 153);
	public final static Color COLOR_FOREFRONT = Color.white;
	
	public static SpriteSheet MENU_BORDER;
	protected static UnicodeFont PANEL_FONT;
	protected static UnicodeFont SPEECH_FONT;
	
	
	public Panel(int menuType) {
		super();
		this.panelType = menuType;
		
		switch (menuType)
		{			
			default:
				break;
		}
	}
	
	public static void intialize(FCResourceManager frm)
	{
		MENU_BORDER = frm.getSpriteSheets().get("menuborder");
		PANEL_FONT = frm.getFontByName("menufont");
		
		Font awtFont = new Font("Times New Roman", Font.ITALIC, 24);
		UnicodeFont ufont = new UnicodeFont(awtFont, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 15, false, true);
		ufont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		ufont.addAsciiGlyphs();
		ufont.addGlyphs(400, 600);
		try 
		{
			ufont.loadGlyphs();
			SPEECH_FONT = ufont;
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// stateInfo.getGc().getGraphics().setFont(PANEL_FONT);
	}

	public abstract void render(FCGameContainer gc, Graphics graphics);
	
	// public boolean 

	public int getPanelType() {
		return panelType;
	}
	
	public static void drawPanelBox(int x, int y, int width, int height, Graphics graphics)
	{		
		// graphics.setColor(Color.lightGray);
		// graphics.fillRoundRect(x, y, width, height, 5);		
		// graphics.drawImage(menuBackground.getSubImage(0, 0, width - 10, height - 10), x + 5, y + 5);
		graphics.setFont(PANEL_FONT);
		graphics.setColor(Color.blue);
		graphics.fillRect(x, y, width, height);
		MENU_BORDER.getSprite(4, 0).draw(x, y + height - 12, x + width, y + height, 4, 0, 5, 12);
		MENU_BORDER.getSprite(5, 0).draw(x, y, x + width, y + 12, 4, 0, 5, 12);
		MENU_BORDER.getSprite(6, 0).draw(x, y + 12, 12, height - 24);
		MENU_BORDER.getSprite(7, 0).draw(x + width - 12, y + 12, 12, height - 24);
		
		MENU_BORDER.getSprite(0, 0).draw(x, y + height - 12);
		MENU_BORDER.getSprite(1, 0).draw(x, y);		
		MENU_BORDER.getSprite(2, 0).draw(x + width - 12, y + height - 12);
		MENU_BORDER.getSprite(3, 0).draw(x + width - 12, y);		
	}
	
	public static void drawRect(Rectangle rect, Graphics graphics)
	{
		graphics.drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}
	
	public static void fillRect(Rectangle rect, Graphics graphics)
	{
		graphics.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}
	
	public static boolean contains(int lowX, int highX, int valX, int lowY, int highY, int valY)
	{
		return (between(lowX, highX, valX) && between(lowY, highY, valY));
	}
	
	public static boolean between(int low, int high, int val)
	{
		if (val >= low && val < high)
			return true;
		return false;
	}
}
