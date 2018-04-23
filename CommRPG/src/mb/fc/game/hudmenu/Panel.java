package mb.fc.game.hudmenu;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;

import mb.fc.engine.CommRPG;
import mb.fc.engine.config.MusicConfiguration;
import mb.fc.engine.config.PanelRenderer;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.menu.Menu.MenuUpdate;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;

/**
 * A container to display information to the screen that does not generally need to be interacted with.
 */
public abstract class Panel
{
	public enum PanelType
	{
		PANEL_HEALTH_BAR,
		PANEL_LAND_EFFECT,
		PANEL_INITIATIVE,
		PANEL_ENEMY_HEALTH_BAR,
		PANEL_TEXT,
		PANEL_BATTLE,
		PANEL_BATTLE_OPTIONS,
		PANEL_MAPMOVE,
		PANEL_SPELL,
		PANEL_MAPATTACK,
		PANEL_CHAT,
		PANEL_SPEECH,
		PANEL_WAIT,
		PANEL_SYSTEM,
		PANEL_CONNECTIONS,
		PANEL_SHOP,
		PANEL_HEROS_OVERVIEW,
		PANEL_HEROS_STATS,
		PANEL_YES_NO,
		PANEL_PRIEST,
		PANEL_ASSIGN_HERO,
		PANEL_STRING,
		PANEL_BATTLE_MOVE,
		PANEL_ITEM,
		PANEL_ITEM_OPTIONS,
		PANEL_DEBUG,
		PANEL_TARGET_HEALTH_BAR,
		PANEL_MAP_ENTRY,
		PANEL_SHOP_OPTIONS,
		PANEL_MULTI_JOIN_CHOOSE,
		PANEL_MINI_MAP,
		PANEL_PAUSE
	}

	protected PanelType panelType;
	public final static Color COLOR_MOUSE_OVER = new Color(0, 0, 153);
	public final static Color COLOR_FOREFRONT = Color.white;
	protected static MusicConfiguration MUSIC_SELECTOR;

	public static SpriteSheet MENU_BORDER;
	protected static UnicodeFont PANEL_FONT;
	protected static UnicodeFont SPEECH_FONT;
	protected static PanelRenderer renderer;


	public Panel(PanelType menuType) {
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
		MENU_BORDER = frm.getSpriteSheet("menuborder");
		PANEL_FONT = frm.getFontByName("menufont");
		MUSIC_SELECTOR = CommRPG.engineConfiguratior.getMusicConfiguration();

		/*
		Font awtFont = new Font("Times New Roman", Font.ITALIC, 24);
		UnicodeFont ufont = new UnicodeFont(awtFont, 45, false, true);
		ufont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		ufont.addAsciiGlyphs();
		ufont.addGlyphs(400, 600);
		try
		{
			ufont.loadGlyphs();
			SPEECH_FONT = ufont;
		} catch (SlickException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred trying to load font glyphs:" + e.getMessage(),
					"Error loading font glyphs", JOptionPane.ERROR_MESSAGE);
			throw new BadResourceException(e);
		}
		*/
		
		SPEECH_FONT = frm.getFontByName("speechfont");

		// stateInfo.getGc().getGraphics().setFont(PANEL_FONT);

        renderer =  CommRPG.engineConfiguratior.getPanelRenderer();
	}

	public abstract void render(PaddedGameContainer gc, Graphics graphics);

	public MenuUpdate update(int delta)
	{
		return MenuUpdate.MENU_NO_ACTION;
	}

	// public boolean

	public PanelType getPanelType() {
		return panelType;
	}

	public static void drawPanelBox(int x, int y, int width, int height, Graphics graphics)
	{
		// graphics.setColor(Color.lightGray);
		// graphics.fillRoundRect(x, y, width, height, 5);
		// graphics.drawImage(menuBackground.getSubImage(0, 0, width - 10, height - 10), x + 5, y + 5);
		drawPanelBox(x, y, width, height, graphics, new Color(0, 32, 96));

		// renderer.render(MENU_BORDER, x, y, width, height, graphics);
	}

	public static void drawPanelBox(int x, int y, int width, int height, Graphics graphics, Color color)
	{
		// graphics.setColor(Color.lightGray);
		// graphics.fillRoundRect(x, y, width, height, 5);
		// graphics.drawImage(menuBackground.getSubImage(0, 0, width - 10, height - 10), x + 5, y + 5);
		graphics.setFont(PANEL_FONT);
		graphics.setColor(color);

		renderer.render(MENU_BORDER, x, y, width, height, graphics);

		graphics.fillRect(x, y, width, height);
		
		/*
		int imgWidth = MENU_BORDER.getSprite(5, 0).getWidth();
		int imgHeight = MENU_BORDER.getSprite(5, 0).getHeight();
		graphics.drawImage(MENU_BORDER.getSprite(5, 0), 0, 10);
		graphics.drawImage(MENU_BORDER.getSprite(5, 0), x, y + height - 2, x + width, y + height + 2, 0, 0, MENU_BORDER.getSprite(5, 0).getWidth(), MENU_BORDER.getSprite(5, 0).getHeight());
		*/
		
		// graphics.drawImage(MENU_BORDER.getSprite(4, 0), x, y + height - 4, );
		//MENU_BORDER.getSprite(6, 0).draw(x, y + 4, 4, height - 8);
		//MENU_BORDER.getSprite(5, 0).draw(x, y, width, 4);
		
		MENU_BORDER.getSprite(4, 0).draw(x, y + height - 4, width, 4);
		MENU_BORDER.getSprite(5, 0).draw(x, y, width, 4);
		MENU_BORDER.getSprite(6, 0).draw(x, y + 4, 4, height - 8);
		MENU_BORDER.getSprite(7, 0).draw(x + width - 4, y + 4, 4, height - 8);

		MENU_BORDER.getSprite(0, 0).draw(x, y + height - 4);
		MENU_BORDER.getSprite(1, 0).draw(x, y);
		MENU_BORDER.getSprite(2, 0).draw(x + width - 4, y + height - 4);
		MENU_BORDER.getSprite(3, 0).draw(x + width - 4, y);
		
		/*
		MENU_BORDER.getSprite(4, 0).draw(x, y + height - 12, x + width, y + height, 4, 0, 5, 12);
		MENU_BORDER.getSprite(5, 0).draw(x, y, x + width, y + 12, 4, 0, 5, 12);
		MENU_BORDER.getSprite(6, 0).draw(x, y + 12, 12, height - 24);
		MENU_BORDER.getSprite(7, 0).draw(x + width - 12, y + 12, 12, height - 24);

		MENU_BORDER.getSprite(0, 0).draw(x, y + height - 12);
		MENU_BORDER.getSprite(1, 0).draw(x, y);
		MENU_BORDER.getSprite(2, 0).draw(x + width - 12, y + height - 12);
		MENU_BORDER.getSprite(3, 0).draw(x + width - 12, y);
		*/
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

	public void panelRemoved(StateInfo stateInfo)
	{
		if (makeRemoveSounds())
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, MUSIC_SELECTOR.getMenuRemovedSoundEffect(), 1f, false));
	}

	public void panelAdded(StateInfo stateInfo)
	{
		if (makeAddSounds())
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, MUSIC_SELECTOR.getMenuAddedSoundEffect(), 1f, false));
	}

	public boolean makeAddSounds()
	{
		return false;
	}
	
	public boolean makeRemoveSounds()
	{
		return false;
	}
}
