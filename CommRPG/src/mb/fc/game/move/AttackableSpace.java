package mb.fc.game.move;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.InfoMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.Range;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel.PanelType;
import mb.fc.game.hudmenu.SpriteContextPanel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.listener.KeyboardListener;
import mb.fc.game.listener.MouseListener;
import mb.fc.game.sprite.AnimatedSprite;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;

public class AttackableSpace implements KeyboardListener, MouseListener
{
	private boolean targetsHero = false;
	private CombatSprite currentSprite;
	private int[][] range;
	private int[][] area;
	private int rangeOffset;
	private int areaOffset;
	private float targetSelectX, targetSelectY;
	private float selectX, selectY;
	private int selectedTarget = 0;
	private int spriteTileX, spriteTileY;
	private int tileWidth, tileHeight;
	private ArrayList<CombatSprite> targetsInRange = new ArrayList<CombatSprite>();
	private Image cursorImage;
	private final Color ATTACKABLE_COLOR = new Color(255, 0, 0, 70);
	private boolean targetsAll = false;

	public static final int[][] AREA_0 = {{1}};

	public static final int[][] AREA_1 = {{-1, 1, -1},
											{1, 1, 1},
											{-1, 1, -1}};

	public static final int[][] AREA_2 = {	{-1, -1, 1, -1, -1},
											{-1,  1, 1,  1, -1},
											{ 1,  1, 1,  1,  1},
											{-1,  1, 1,  1, -1},
											{-1, -1, 1, -1, -1}};

	public static final int[][] AREA_2_NO_1 = {   {-1, -1, 1, -1, -1},
												{-1,  1, -1,  1, -1},
												{ 1,  -1, 1,  -1,  1},
												{-1,  1, -1,  1, -1},
												{-1, -1, 1, -1, -1}};

	public static final int[][] AREA_3 = {	{-1, -1, -1, 1, -1, -1, -1},
											{-1, -1,  1, 1,  1, -1, -1},
											{-1,  1,  1, 1,  1,  1, -1},
											{ 1,  1,  1, 1,  1,  1,  1},
											{-1,  1,  1, 1,  1,  1, -1},
											{-1, -1,  1, 1,  1, -1, -1},
											{-1, -1, -1, 1, -1, -1, -1}};

	public static final int[][] AREA_3_NO_1 =
										{	{-1, -1, -1,  1, -1, -1, -1},
											{-1, -1,  1,  1,  1, -1, -1},
											{-1,  1,  1, -1,  1,  1, -1},
											{ 1,  1, -1,  1, -1,  1,  1},
											{-1,  1,  1, -1,  1,  1, -1},
											{-1, -1,  1,  1,  1, -1, -1},
											{-1, -1, -1,  1, -1, -1, -1}};
	public static final int[][] AREA_3_NO_1_2 =
										{	{-1, -1, -1,  1, -1, -1, -1},
											{-1, -1,  1, -1,  1, -1, -1},
											{-1,  1, -1, -1, -1,  1, -1},
											{ 1, -1, -1,  1, -1, -1,  1},
											{-1,  1, -1, -1, -1,  1, -1},
											{-1, -1,  1, -1,  1, -1, -1},
											{-1, -1, -1,  1, -1, -1, -1}};

	public static final int[][] AREA_ALL = {{}};

	public static final int AREA_ALL_INDICATOR = 0;



	public AttackableSpace(StateInfo stateInfo, CombatSprite currentSprite, boolean targetsHero,
			int[][] range, int[][] area)
	{
		this.currentSprite = currentSprite;
		this.range = range;
		this.area = area;
		this.targetsHero = targetsHero;
		this.tileWidth = stateInfo.getTileWidth();
		this.tileHeight = stateInfo.getTileHeight();
		spriteTileX = currentSprite.getTileX();
		spriteTileY = currentSprite.getTileY();
		Log.debug("Finding attackables for " + currentSprite.getName());
		if (area == AttackableSpace.AREA_ALL) {
			targetsAll = true;
			this.area = AttackableSpace.AREA_0;
			this.range = AttackableSpace.AREA_0;
		}

		this.areaOffset = (this.area.length - 1) / 2;
		this.rangeOffset = (this.range.length - 1) / 2;

		for (int i = 0; i < this.range.length; i++)
		{
			for (int j = 0; j < this.range[0].length; j++)
			{
				if (this.range[i][j] == 1)
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
			Log.debug("Default target " + targetsInRange.get(0).getName());
		}
		else
		{
			Log.debug("\tNo targets found in range");
		}

		if (currentSprite.isHero())
			stateInfo.registerMouseListener(this);

		cursorImage = stateInfo.getResourceManager().getImage("battlecursor");
	}

	/**
	 * This displays the white "targeting" rectangle during a characters attack phase
	 */
	public void render(PaddedGameContainer gc, Camera camera, Graphics graphics)
	{
		graphics.setColor(ATTACKABLE_COLOR);
		for (int i = 0; i < range.length; i++)
		{
			for (int j = 0; j < range[0].length; j++)
			{
				if (range[j][i] != -1 && (i != rangeOffset || j != rangeOffset || targetsHero == currentSprite.isHero()))
				{
					graphics.fillRect((spriteTileX - rangeOffset + i) * tileWidth - camera.getLocationX(),
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
						cursorImage.draw(selectX + (tileWidth * (i - areaOffset)) - camera.getLocationX(),
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

		stateInfo.removePanel(PanelType.PANEL_ENEMY_HEALTH_BAR);
		stateInfo.addPanel(new SpriteContextPanel(PanelType.PANEL_ENEMY_HEALTH_BAR, targetsInRange.get(selectedTarget), stateInfo.getPaddedGameContainer()));

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
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuback", 1f, false));
			return true;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_3))
		{
			if (targetsInRange.size() == 0)
				return false;

			stateInfo.removePanel(PanelType.PANEL_ENEMY_HEALTH_BAR);
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuselect", 1f, false));

			ArrayList<AnimatedSprite> sprites = new ArrayList<>();

			if (!targetsAll) {
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
			} else {
				for (CombatSprite cs : stateInfo.getCombatSprites()) {
					if (cs.isHero() == targetsHero && cs.getCurrentHP() > 0)
						sprites.add(cs);
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
			stateInfo.sendMessage(new SpriteContextMessage(MessageType.SET_SELECTED_SPRITE, targetsInRange.get(selectedTarget)));
			// setTargetSprite(targetsInRange.get(selectedTarget), stateInfo);
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
			stateInfo.sendMessage(new SpriteContextMessage(MessageType.SET_SELECTED_SPRITE, targetsInRange.get(selectedTarget)));
			// setTargetSprite(targetsInRange.get(selectedTarget), stateInfo);
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
				area = AttackableSpace.AREA_1;
				break;
			case TWO_AND_LESS:
				area = AttackableSpace.AREA_2;
				break;
			case THREE_AND_LESS:
				area = AttackableSpace.AREA_3;
				break;
			case TWO_NO_ONE:
				area = AttackableSpace.AREA_2_NO_1;
				break;
			case THREE_NO_ONE_OR_TWO:
				area = AttackableSpace.AREA_3_NO_1_2;
			case SELF_ONLY:
				break;
			case THREE_NO_ONE:
				area = AttackableSpace.AREA_3_NO_1;
				break;
			default:
				break;
		}

		return area;
	}
}
