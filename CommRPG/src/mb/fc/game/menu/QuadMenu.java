package mb.fc.game.menu;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.ui.FCGameContainer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public abstract class QuadMenu extends Menu
{
	protected StateInfo stateInfo;
	protected Direction selected = Direction.UP;
	protected static final Color disabledColor = new Color(111, 111, 111);
	protected int blinkDelta = 0;
	protected boolean blink = false;

	protected Image[] icons;
	protected String[] text;
	protected boolean[] enabled;
	protected boolean paintSelectionCursor;
	protected boolean largeFlor = true;
	protected Image flourish;
	protected Image selectSide;
	protected Image selectTop;
	protected int selectCount = 3;
	protected int flashCount = -5;
	protected Color flashColor = new Color(100, 100, 100);

	protected QuadMenu(int menuType, StateInfo stateInfo) {
		this(menuType, true, stateInfo);
	}

	protected QuadMenu(int menuType, boolean largeFlor, StateInfo stateInfo) {
		super(menuType);
		this.largeFlor = largeFlor;
		if (largeFlor)
			this.flourish = stateInfo.getResourceManager().getImages().get("largeflor");
		else
			this.flourish = stateInfo.getResourceManager().getImages().get("smallflor");
		this.selectSide = stateInfo.getResourceManager().getImages().get("selectside");
		this.selectTop = stateInfo.getResourceManager().getImages().get("selecttop");
		this.stateInfo = stateInfo;
		this.paintSelectionCursor = false;
	}

	public abstract void initialize();

	protected Image getIconImage(Direction dir, boolean blink) {
		switch (dir)
		{
			case UP:
				return icons[0 + (blink ? 4 : 0)];
			case LEFT:
				return icons[1 + (blink ? 4 : 0)];
			case RIGHT:
				return icons[2 + (blink ? 4 : 0)];
			case DOWN:
				return icons[3 + (blink ? 4 : 0)];
		}
		return null;
	}

	protected boolean isOptionEnabled(Direction dir) {
		switch (dir)
		{
			case UP:
				return enabled[0];
			case LEFT:
				return enabled[1];
			case RIGHT:
				return enabled[2];
			case DOWN:
				return enabled[3];
		}
		return true;
	}

	protected String getText(Direction dir) {
		switch (dir)
		{
			case UP:
				return text[0];
			case LEFT:
				return text[1];
			case RIGHT:
				return text[2];
			case DOWN:
				return text[3];
		}
		return null;
	}

	protected int getSelectedInt()
	{
		switch (selected)
		{
			case UP:
				return 0;
			case LEFT:
				return 1;
			case RIGHT:
				return 2;
			case DOWN:
				return 3;
		}
		return 0;
	}

	protected void renderTextBox(FCGameContainer gc, Graphics graphics)
	{
		Panel.drawPanelBox(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 205 + gc.getDisplayPaddingX(),
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 195,
				getTextboxWidth(),
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 15 + 12, graphics);

		graphics.setColor(COLOR_FOREFRONT);

		graphics.drawString(getText(selected), CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 212 + gc.getDisplayPaddingX(),
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 190 + 3);
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics)
	{
		renderTextBox(gc, graphics);

		int iconWidth = getIconImage(Direction.UP, false).getWidth();
		int iconHeight = getIconImage(Direction.UP, false).getHeight();

		int x = (gc.getWidth() - iconWidth) / 2;
		int y = gc.getHeight() - iconHeight * 2 - 25;

		if (isOptionEnabled(Direction.UP))
		{
			if (selected == Direction.UP)
				graphics.drawImage(selectTop, (gc.getWidth() - selectTop.getWidth()) / 2, y - selectTop.getHeight() +  CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * selectCount);
			drawImage(getIconImage(Direction.UP, (selected == Direction.UP ? blink : false)), x, y - (selected == Direction.UP ? CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * selectCount : 0), graphics, Direction.UP);
		}
		else
			graphics.drawImage(getIconImage(Direction.UP, false), x, y, disabledColor);

		if (isOptionEnabled(Direction.LEFT))
		{
			if (selected == Direction.LEFT)
				graphics.drawImage(selectSide, x - iconWidth - selectSide.getWidth() + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 3 - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * selectCount, (float) (y + iconHeight - .5 * selectSide.getHeight()));
			drawImage(getIconImage(Direction.LEFT, (selected == Direction.LEFT ? blink : false)), x - iconWidth - (selected == Direction.LEFT ? CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * selectCount : 0), (float) (y + iconHeight * .5), graphics, Direction.LEFT);
		}
		else
			graphics.drawImage(getIconImage(Direction.LEFT, false), x - iconWidth, (float) (y + iconHeight * .5), disabledColor);


		if (isOptionEnabled(Direction.RIGHT))
		{
			if (selected == Direction.RIGHT)
				graphics.drawImage(selectSide.getFlippedCopy(true, false), x + iconWidth * 2 - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 3 + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * selectCount, (float) (y + iconHeight - .5 * selectSide.getHeight()));
			drawImage(getIconImage(Direction.RIGHT, (selected == Direction.RIGHT ? blink : false)), x + iconWidth + (selected == Direction.RIGHT ? CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * selectCount : 0), (float) (y + iconHeight * .5), graphics, Direction.RIGHT);
		}
		else
			graphics.drawImage(getIconImage(Direction.RIGHT, false), x + iconWidth, (float) (y + iconHeight * .5), disabledColor);
		graphics.drawImage(flourish.getFlippedCopy(false, true), x - flourish.getWidth(), y + iconHeight + flourish.getHeight());

		if (isOptionEnabled(Direction.DOWN))
		{
			if (selected == Direction.DOWN)
				graphics.drawImage(selectTop.getFlippedCopy(false, true), (gc.getWidth() - selectTop.getWidth()) / 2, y + iconHeight * 2 -  CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * selectCount);
			drawImage(getIconImage(Direction.DOWN, (selected == Direction.DOWN ? blink : false)), x, y + iconHeight + (selected == Direction.DOWN ? CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * selectCount : 0), graphics, Direction.DOWN);
		}
		else
			graphics.drawImage(getIconImage(Direction.DOWN, false), x, y + iconHeight, disabledColor);

		graphics.drawImage(flourish, x - flourish.getWidth(), (float) (y + iconHeight * .5) - flourish.getHeight());
		graphics.drawImage(flourish.getFlippedCopy(true, false), x + iconWidth, (float) (y + iconHeight * .5) - flourish.getHeight());
		graphics.drawImage(flourish.getFlippedCopy(false, true), x - flourish.getWidth(), y + iconHeight + flourish.getHeight());
		graphics.drawImage(flourish.getFlippedCopy(true, true), x + iconWidth, y + iconHeight + flourish.getHeight());

	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo)
	{
		blinkDelta++;
		flashCount += 1;

		if (flashCount >= 50)
			flashCount = -50;

		flashColor.r = (50 - Math.abs(flashCount)) / 255.0f;
		flashColor.g = (50 - Math.abs(flashCount)) / 255.0f;
		flashColor.b = (50 - Math.abs(flashCount)) / 255.0f;

		if (selectCount < 3)
			selectCount++;


		if (blinkDelta == 20)
		{
			if (!paintSelectionCursor)
				blink = !blink;
			blinkDelta = 0;
		}

		if (input.isKeyDown(KeyMapping.BUTTON_2))
		{
			return onBack();
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_3) || input.isKeyDown(KeyMapping.BUTTON_1))
		{
			return onConfirm();
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_UP))
		{
			return onUp();
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_DOWN))
		{
			return onDown();
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_LEFT))
		{
			return onLeft();
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_RIGHT))
		{
			return onRight();
		}
		return MenuUpdate.MENU_NO_ACTION;
	}

	protected MenuUpdate onUp()
	{
		return onDirection(Direction.UP);
	}

	protected MenuUpdate onDown()
	{
		return onDirection(Direction.DOWN);
	}

	protected MenuUpdate onLeft()
	{
		return onDirection(Direction.LEFT);
	}

	protected MenuUpdate onRight()
	{
		return onDirection(Direction.RIGHT);
	}

	protected abstract MenuUpdate onBack();

	protected abstract MenuUpdate onConfirm();

	protected MenuUpdate onDirection(Direction dir)
	{
		if (selected != dir && isOptionEnabled(dir))
		{
			selectCount = 2;
			flashCount = -5;
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
			selected = dir;
			if (!paintSelectionCursor)
				blink = true;
			blinkDelta = 0;
		}
		return MenuUpdate.MENU_ACTION_SHORT;
	}

	protected int getTextboxWidth()
	{
		return CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 57;
	}

	protected void drawImage(Image image, float x, float y, Graphics g, Direction dir)
	{
		if (paintSelectionCursor && dir == selected)
		{
			Image whiteIm = image;

			// 1. bind the sprite sheet
			whiteIm.bind();

			// 2. change texture environment
			GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
					GL11.GL_TEXTURE_ENV_MODE, GL11.GL_ADD);

			// 3. start rendering the sprite sheet
			whiteIm.startUse();

			// 4. bind any colors, draw any sprites
			flashColor.bind();
			whiteIm.drawEmbedded(x, y, image.getWidth(), image.getHeight());

			// 5. stop rendering the sprite sheet
			whiteIm.endUse();

			// 6. reset the texture environment
			GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
					GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);


			/*
			g.setColor(Color.red);
			g.drawRect(x + 1, y + 1, image.getWidth() - 2, image.getHeight() - 2);
			g.drawRect(x + 2, y + 2, image.getWidth() - 4, image.getHeight() - 4);
			*/
		}
		else
			g.drawImage(image, x, y);

	}
}
