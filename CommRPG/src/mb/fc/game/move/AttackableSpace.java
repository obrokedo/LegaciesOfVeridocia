package mb.fc.game.move;

import java.util.ArrayList;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.ChatMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.MultiSpriteContextMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.constants.Direction;
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
	private int targetSelectX, targetSelectY;
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
		
		selectX = currentSprite.getLocX();
		selectY = currentSprite.getLocY();
		
		if (targetsInRange.size() > 0)
		{
			targetSelectX = targetsInRange.get(0).getLocX();
			targetSelectY = targetsInRange.get(0).getLocY();
			this.setTargetSprite(targetsInRange.get(0), stateInfo);
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
		
		if (targetSelectX != -1)
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
		return false;
	}		
	
	public void setTargetSprite(CombatSprite targetSprite, StateInfo stateInfo)
	{
		targetSelectX = targetSprite.getLocX();
		targetSelectY = targetSprite.getLocY();
		
		if (targetSelectX > currentSprite.getLocX())
			currentSprite.setFacing(Direction.RIGHT);
		else if (targetSelectX < currentSprite.getLocX())
			currentSprite.setFacing(Direction.LEFT);
		else if (targetSelectY > currentSprite.getLocY())
			currentSprite.setFacing(Direction.DOWN);
		else if (targetSelectY < currentSprite.getLocY())
			currentSprite.setFacing(Direction.UP);
		
		stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "targetselect", 1f, false));
	}

	@Override
	public int getZOrder() {
		return MouseListener.ORDER_ATTACKABLE_SPACE;
	}
	
	public void update(StateInfo stateInfo)
	{
		if (selectX > targetSelectX)
			selectX -= stateInfo.getTileWidth() / 4;
		else if (selectX < targetSelectX)
			selectX += stateInfo.getTileWidth() / 4;
		
		if (selectY > targetSelectY)
			selectY -= stateInfo.getTileHeight() / 4;
		else if (selectY < targetSelectY)
			selectY += stateInfo.getTileHeight() / 4;
	}

	@Override
	public boolean handleKeyboardInput(FCInput input, StateInfo stateInfo) 
	{				
		update(stateInfo);
		
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
			setTargetSprite(targetsInRange.get(selectedTarget), stateInfo);
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
			setTargetSprite(targetsInRange.get(selectedTarget), stateInfo);			
			return true;
		}
		return false;
	}
}
