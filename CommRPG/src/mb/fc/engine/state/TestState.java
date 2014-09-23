package mb.fc.engine.state;

import mb.fc.engine.CommRPG;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.utils.AnimSprite;
import mb.fc.utils.Animation;
import mb.fc.utils.SpriteAnims;
import mb.gl2.loading.LoadableGameState;
import mb.gl2.loading.ResourceManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class TestState extends LoadableGameState
{
	private FCResourceManager frm;
	private Image backgroundImage = null;
	private FCGameContainer gc;
	private int bgXPos;
	private int bgYPos;
	private int rangedAttackCounter = 0;
	private Color trans = new Color(255, 255, 255, 62);
	private int rangedDelta = 0;
	private Animation targetAnim;
	private SpriteAnims sas;
	private float screenScale = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		gc = (FCGameContainer) container;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException
	{
		g.setColor(Color.black);
		g.fillRect(0, 0, gc.getWidth(), gc.getHeight());
		if (backgroundImage != null)
		{
			// Assume that the target is a hero and determine locations for it
			int xLoc = gc.getDisplayPaddingX();
			int yLoc = bgYPos + backgroundImage.getHeight();

			if (rangedAttackCounter < 22)
			{
				backgroundImage.draw(bgXPos, bgYPos, .5f);
				for (AnimSprite as : targetAnim.frames.get(0).sprites)
					sas.getImageAtIndex(as.imageIndex).draw(xLoc + (as.x * screenScale) * .5f, yLoc + (as.y * screenScale) * 1.5f, .5f);
				for (int i = Math.max(0, rangedAttackCounter - 11); i < rangedAttackCounter; i++)
				{
					if (i < 11)
						backgroundImage.draw(bgXPos, bgYPos, Math.min(.5f + .05f * (rangedAttackCounter - i), 1), trans);
					else
						backgroundImage.draw(bgXPos, bgYPos, 1, trans);
				}

				for (int i = Math.max(0, rangedAttackCounter - 11); i < rangedAttackCounter; i++)
				{

					for (AnimSprite as : targetAnim.frames.get(0).sprites)
						sas.getImageAtIndex(as.imageIndex).draw(
								xLoc + (as.x * screenScale)  * Math.min(.5f + .05f * i, 1),
								yLoc + (as.y * screenScale) * Math.max(1.5f - .05f * i, 1),
								Math.min(.5f + .05f * i, 1), trans);
				}
			}
			else if (rangedAttackCounter < 100)
			{
				backgroundImage.draw(bgXPos, bgYPos);
				for (AnimSprite as : targetAnim.frames.get(0).sprites)
					sas.getImageAtIndex(as.imageIndex).draw(xLoc + (as.x * screenScale), yLoc + (as.y * screenScale));
			}
			else if (rangedAttackCounter < 200)
			{
				backgroundImage.draw(bgXPos, bgYPos, .5f);
				for (AnimSprite as : targetAnim.frames.get(0).sprites)
					sas.getImageAtIndex(as.imageIndex).draw(xLoc + (as.x * screenScale) * .5f, yLoc + (as.y * screenScale) * 1.5f, .5f);
			}
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		rangedDelta += delta;
		while (rangedDelta > 30)
		{
			rangedDelta -= 30;
			if (rangedAttackCounter < 11)
				rangedAttackCounter = (rangedAttackCounter + 1) % 200;
			else
				rangedAttackCounter = (rangedAttackCounter + 2) % 200;
		}
	}

	@Override
	public void stateLoaded(ResourceManager resourceManager) {
		frm = (FCResourceManager) resourceManager;
		Image bgIm = frm.getSpriteSheets().get("battlebg").getSprite(0,  0);

		backgroundImage = bgIm.getScaledCopy((gc.getWidth() - gc.getDisplayPaddingX() * 2) / (float) bgIm.getWidth());
		System.out.println("RESULT SCALE " + backgroundImage.getWidth());

		bgXPos = gc.getDisplayPaddingX();
		bgYPos = (gc.getHeight() - backgroundImage.getHeight()) / 2;

		sas = frm.getSpriteAnimations().get("rat");
		targetAnim = sas.getAnimation("UnStand");
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return CommRPG.STATE_GAME_TEST;
	}

}
