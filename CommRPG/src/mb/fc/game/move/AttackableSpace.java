package mb.fc.game.move;

import java.util.ArrayList;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.InfoMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.Range;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.hudmenu.SpriteContextPanel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.listener.KeyboardListener;
import mb.fc.game.listener.MouseListener;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;

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
	private Image cursorImage;
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

	public static final int[][] RANGE_2_NO_1 = {   {-1, -1, 1, -1, -1},
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

	public static final int[][] RANGE_3_NO_1 =
										{	{-1, -1, -1,  1, -1, -1, -1},
											{-1, -1,  1,  1,  1, -1, -1},
											{-1,  1,  1, -1,  1,  1, -1},
											{ 1,  1, -1,  1, -1,  1,  1},
											{-1,  1,  1, -1,  1,  1, -1},
											{-1, -1,  1,  1,  1, -1, -1},
											{-1, -1, -1,  1, -1, -1, -1}};
	public static final int[][] RANGE_3_NO_1_2 =
										{	{-1, -1, -1,  1, -1, -1, -1},
											{-1, -1,  1, -1,  1, -1, -1},
											{-1,  1, -1, -1, -1,  1, -1},
											{ 1, -1, -1,  1, -1, -1,  1},
											{-1,  1, -1, -1, -1,  1, -1},
											{-1, -1,  1, -1,  1, -1, -1},
											{-1, -1, -1,  1, -1, -1, -1}};



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
		Log.debug("Finding attackables for " + currentSprite.getName());
		for (int i = 0; i < range.length; i++)
		{
			for (int j = 0; j < range[0].length; j++)
			{
				if (range[i][j] == 1)
				{
					Log.debug("\tChecking space for targetables " + (currentSprite.getTileX() - rangeOffset + i) + ", " +
							(currentSprite.getTileY() - rangeOffset + j));
					CombatSprite targetable = stateInfo.getCombatSpriteAtTile(currentSprite.getTileX() - rangeOffset + i,
							currentSprite.getTileY() - rangeOffset + j, targetsHero);
					if (targetable != null)
					{
						targetsInRange.add(targetable);

						Log.debug("\tAttackable Space: Add Targetable " + targetable.getName());
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
		{
			Log.debug("\tNo targets found in range");
			stateInfo.sendMessage(new InfoMessage(null, "No targets in range!"));
		}

		if (currentSprite.isHero())
			stateInfo.registerMouseListener(this);

		cursorImage = stateInfo.getResourceManager().getImages().get("battlecursor");
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
						cursorImage.draw(selectX + (tileWidth * (i - areaOffset)) - camera.getLocationX() + gc.getDisplayPaddingX(),
								selectY + (tileHeight * (j - areaOffset)) - camera.getLocationY());
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

		stateInfo.removePanel(Panel.PANEL_ENEMY_HEALTH_BAR);
		stateInfo.addPanel(new SpriteContextPanel(Panel.PANEL_ENEMY_HEALTH_BAR, targetsInRange.get(selectedTarget), stateInfo.getGc()));

		stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menumove", 1f, false));
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
			stateInfo.sendMessage(MessageType.SHOW_BATTLEMENU);
			stateInfo.sendMessage(MessageType.HIDE_ATTACK_AREA);
			stateInfo.removePanel(Panel.PANEL_ENEMY_HEALTH_BAR);
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuback", 1f, false));
			return true;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_3))
		{
			if (targetsInRange.size() == 0)
				return false;

			stateInfo.removePanel(Panel.PANEL_ENEMY_HEALTH_BAR);
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuselect", 1f, false));

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
				stateInfo.sendMessage(new SpriteContextMessage(MessageType.TARGET_SPRITE, sprites));

				// Once we've targeted a sprite there can not be anymore keyboard input
				stateInfo.removeKeyboardListeners();

				Log.debug("Target Amount -> " + sprites.size());

				return true;
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_UP) || input.isKeyDown(KeyMapping.BUTTON_LEFT))
		{
			if (targetsInRange.size() <= 1 ||
					selectX != targetSelectX || selectY != targetSelectY)
				return false;

			selectedTarget = (selectedTarget + 1) % targetsInRange.size();
			setTargetSprite(targetsInRange.get(selectedTarget), stateInfo);
			return true;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_DOWN) || input.isKeyDown(KeyMapping.BUTTON_RIGHT))
		{
			if (targetsInRange.size() <= 1 ||
					selectX != targetSelectX || selectY != targetSelectY)
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

	public int getTargetAmount()
	{
		return targetsInRange.size();
	}

	public static int[][] getAttackableArea(Range range)
	{
		int area[][] = null;

		switch (range)
		{
			case ONE_ONLY:
				area = AttackableSpace.RANGE_1;
				break;
			case TWO_AND_LESS:
				area = AttackableSpace.RANGE_2;
				break;
			case THREE_AND_LESS:
				area = AttackableSpace.RANGE_3;
				break;
			case TWO_NO_ONE:
				area = AttackableSpace.RANGE_2_NO_1;
				break;
			case THREE_NO_ONE_OR_TWO:
				area = AttackableSpace.RANGE_3_NO_1_2;
			case SELF_ONLY:
				break;
			case THREE_NO_ONE:
				area = AttackableSpace.RANGE_3_NO_1;
				break;
			default:
				break;
		}

		return area;
	}
}
