package mb.fc.game.move;

import java.util.ArrayList;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.ChatMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.MultiSpriteContextMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.listener.KeyboardListener;
import mb.fc.game.listener.MouseListener;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class AttackableSpace implements KeyboardListener, MouseListener
{
	private boolean targetsHero = false;
	private CombatSprite currentSprite;
	private int[][] range;
	private int[][] area;
	private int rangeOffset;
	private int areaOffset;
	private int selectX, selectY;
	private int selectedTarget = 0;
	private int spriteTileX, spriteTileY;
	private int tileWidth, tileHeight;
	private ArrayList<CombatSprite> targetsInRange = new ArrayList<CombatSprite>();
	private final Color ATTACKABLE_COLOR = new Color(255, 0, 0, 70);
	
	public static final int[][] RANGE_0 = {{1}};
	
	public static final int[][] RANGE_1 = {{-1, 1, -1},
											{1, 1, 1},
											{-1, 1, -1}};
	
	public static final int[][] RANGE_2 = {	{-1, -1, 1, -1, -1},
											{-1,  1, 1,  1, -1},
											{ 1,  1, 1,  1,  1},
											{-1,  1, 1,  1, -1},
											{-1, -1, 1, -1, -1}};
	
	public static final int[][] RANGE_2_1 = {   {-1, -1, 1, -1, -1},
												{-1,  1, -1,  1, -1},
												{ 1,  -1, 1,  -1,  1},
												{-1,  1, -1,  1, -1},
												{-1, -1, 1, -1, -1}};
	
	public static final int[][] RANGE_3 = {	{-1, -1, -1, 1, -1, -1, -1},
											{-1, -1,  1, 1,  1, -1, -1},
											{-1,  1,  1, 1,  1,  1, -1},
											{ 1,  1,  1, 1,  1,  1,  1},
											{-1,  1,  1, 1,  1,  1, -1},
											{-1, -1,  1, 1,  1, -1, -1},
											{-1, -1, -1, 1, -1, -1, -1}};
	
	
	
	public AttackableSpace(StateInfo stateInfo, CombatSprite currentSprite, boolean targetsHero, int[][] range, int[][] area) 
	{
		this.currentSprite = currentSprite;
		this.range = range;
		this.rangeOffset = (range.length - 1) / 2;
		this.area = area;
		this.areaOffset = (area.length - 1) / 2;
		this.targetsHero = targetsHero;
		this.tileWidth = stateInfo.getTileWidth();
		this.tileHeight = stateInfo.getTileHeight();
		spriteTileX = currentSprite.getTileX();
		spriteTileY = currentSprite.getTileY();
		
		for (int i = 0; i < range.length; i++)
		{
			for (int j = 0; j < range[0].length; j++)
			{
				if (range[i][j] == 1)
				{
					CombatSprite targetable = stateInfo.getCombatSpriteAtTile(currentSprite.getTileX() - rangeOffset + i, 
							currentSprite.getTileY() - rangeOffset + j, targetsHero);
					if (targetable != null)
					{
						targetsInRange.add(targetable);
						System.out.println("ADD TARGETABLE " + targetable.getName());
					}
				}
			}
		}
		
		if (targetsInRange.size() > 0)
		{
			selectX = targetsInRange.get(0).getLocX();
			selectY = targetsInRange.get(0).getLocY();
		}
		else
			stateInfo.sendMessage(new ChatMessage(Message.MESSAGE_SEND_INTERNAL_MESSAGE, null, "No targets in range!"));
		
		if (currentSprite.isHero())
			stateInfo.registerMouseListener(this);		
	}
	
	/**
	 * This displays the white "targeting" rectangle during a characters attack phase
	 */
	public void render(FCGameContainer gc, Camera camera, Graphics graphics) 
	{			
		graphics.setColor(ATTACKABLE_COLOR);
		for (int i = 0; i < range.length; i++)
		{
			for (int j = 0; j < range[0].length; j++)
			{
				if (range[j][i] != -1 && (i != rangeOffset || j != rangeOffset || targetsHero == currentSprite.isHero()))
				{
					graphics.fillRect(gc.getDisplayPaddingX() + (spriteTileX - rangeOffset + i) * tileWidth - camera.getLocationX(), 
							(spriteTileY - rangeOffset + j) * tileHeight - camera.getLocationY(), tileWidth, tileHeight);
				}
			}
		}
		
		if (selectX != -1)
		{
			graphics.setColor(Color.white);
			
			for (int i = 0; i < area.length; i++)
			{
				for (int j = 0; j < area[0].length; j++)
				{
					if (area[i][j] == 1)
						graphics.drawRect(selectX + (tileWidth * (i - areaOffset)) - camera.getLocationX() + gc.getDisplayPaddingX(), 
								selectY + (tileHeight * (j - areaOffset)) - camera.getLocationY(), tileWidth, tileHeight);
				}
			}						
		}
	}
	
	@Override
	public boolean mouseUpdate(int frameMX, int frameMY, int mapMX, int mapMY,
			boolean leftClicked, boolean rightClicked, StateInfo stateInfo) 
	{		
		if (rightClicked)
		{
			stateInfo.sendMessage(Message.MESSAGE_HIDE_ATTACKABLE);									
			stateInfo.unregisterMouseListener(this);
			return true;
		}
				
		int tw = stateInfo.getTileWidth();
		int th = stateInfo.getTileHeight();
		int mx = mapMX / tw;
		int my = mapMY / th;	
		
		int rx = spriteTileX - mx + rangeOffset;
		int ry = spriteTileY - my + rangeOffset;
		
		if (0 <= rx && rx < range.length && 0 <= ry && ry < range.length && range[rx][ry] == 1 && 
				(targetsHero || rx != (range.length - 1) / 2 || ry != (range.length - 1) / 2))
		{
			// This extremely confusing code just makes sure that the cursor is directly on a tile
			// as long as the mouse is on that tile, another option would be to subtract the mod from the amount
			selectX = mapMX / tw * tw;
			selectY = mapMY / th * th;
			
			if (leftClicked)
			{
				if (stateInfo.getCombatSpriteAtTile(mx, my, targetsHero) != null)
				{
					ArrayList<CombatSprite> sprites = new ArrayList<CombatSprite>();
					for (int i = 0; i < area.length; i++)
					{						
						for (int j = 0; j < area[0].length; j++)
						{
							if (area[i][j] == 1)
							{
								CombatSprite cs = stateInfo.getCombatSpriteAtTile(mx + i - areaOffset, my + j - areaOffset, targetsHero);
								if (cs != null)
									sprites.add(cs);
							}
						}
					}		
					
					if (sprites.size() > 0)
					{						
						// If the current sprite is a target, move it to the end of the list
						if (sprites.remove(currentSprite))
						{
							sprites.add(currentSprite);
						}
						stateInfo.sendMessage(new MultiSpriteContextMessage(Message.MESSAGE_TARGET_SPRITE, sprites));
						stateInfo.unregisterMouseListener(this);
						
						System.out.println("TARGETS -> " + sprites.size());
						
						return true;
					}
				}
			}
		}
		else
			selectX = selectY = -1;
		
		return false;
	}		
	
	public void setTargetSprite(CombatSprite targetSprite)
	{
		selectX = targetSprite.getLocX();
		selectY = targetSprite.getLocY();
	}

	@Override
	public int getZOrder() {
		return MouseListener.ORDER_ATTACKABLE_SPACE;
	}

	@Override
	public boolean handleKeyboardInput(FCInput input, StateInfo stateInfo) 
	{				
		if (input.isKeyDown(KeyMapping.BUTTON_1))
		{
			
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_2))
		{
			// Remove yourself as the active keyboard listener
			stateInfo.removeKeyboardListener();			
			stateInfo.sendMessage(Message.MESSAGE_SHOW_BATTLEMENU);
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuback", 1f, false));
			return true;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_3))
		{
			if (targetsInRange.size() == 0)
				return false;
			
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "targetselect", 1f, false));
			
			ArrayList<CombatSprite> sprites = new ArrayList<CombatSprite>();
			for (int i = 0; i < area.length; i++)
			{						
				for (int j = 0; j < area[0].length; j++)
				{
					if (area[i][j] == 1)
					{
						CombatSprite cs = stateInfo.getCombatSpriteAtTile(targetsInRange.get(selectedTarget).getTileX() + i - areaOffset, 
								targetsInRange.get(selectedTarget).getTileY() + j - areaOffset, targetsHero);
						if (cs != null)
							sprites.add(cs);
					}
				}
			}		
			
			if (sprites.size() > 0)
			{						
				// If the current sprite is a target, move it to the end of the list
				if (sprites.remove(currentSprite))
				{
					sprites.add(currentSprite);
				}
				stateInfo.sendMessage(new MultiSpriteContextMessage(Message.MESSAGE_TARGET_SPRITE, sprites));
				
				// Once we've targeted a sprite there can not be anymore keyboard input
				stateInfo.removeKeyboardListeners();
				
				System.out.println("TARGETS -> " + sprites.size());
				
				return true;
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_UP) || input.isKeyDown(KeyMapping.BUTTON_LEFT))
		{
			if (targetsInRange.size() == 0)
				return false;
			selectedTarget = (selectedTarget + 1) % targetsInRange.size();
			selectX = targetsInRange.get(selectedTarget).getLocX();
			selectY = targetsInRange.get(selectedTarget).getLocY();
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "targetselect", 1f, false));
			return true;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_DOWN) || input.isKeyDown(KeyMapping.BUTTON_RIGHT))
		{
			if (targetsInRange.size() == 0)
				return false;
			
			if (selectedTarget > 0)
				selectedTarget--;
			else
				selectedTarget = targetsInRange.size() - 1;
			selectX = targetsInRange.get(selectedTarget).getLocX();
			selectY = targetsInRange.get(selectedTarget).getLocY();
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "targetselect", 1f, false));
			return true;
		}
		return false;
	}
}
