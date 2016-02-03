package mb.fc.engine.state;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import mb.fc.engine.CommRPG;
import mb.fc.game.ui.ResourceSelector;
import mb.fc.game.ui.ResourceSelector.ResourceSelectorListener;
import mb.fc.loading.FCResourceManager;
import mb.fc.utils.AnimationWrapper;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class DevelAnimationViewState extends BasicGameState implements ResourceSelectorListener
{
	private ResourceSelector animationFileSelector, animationSelector, weaponSelector;
	private FCResourceManager frm;
	private int updateDelta;
	private AnimationWrapper currentAnimation;
	private Rectangle playRect = new Rectangle(350, 675, 100, 30);
	private Rectangle loopRect = new Rectangle(500, 675, 100, 30);
	private int drawX, drawY;
	private static final Color BG_COLOR = new Color(172, 205, 183);

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		animationFileSelector = new ResourceSelector("Animations", 0, true, FCResourceManager.ANIMATIONS_FOLDER, FCResourceManager.ANIMATIONS_EXTENSION, container);
		animationFileSelector.setListener(this);
		weaponSelector = new ResourceSelector("Weapons", 300, true, FCResourceManager.WEAPONS_FOLDER, FCResourceManager.WEAPONS_EXTENSION, container);
		weaponSelector.setListener(this);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.enter(container, game);
		frm = new FCResourceManager();
		try {
			frm.addResource(FCResourceManager.ANIMATIONS_FOLDER_IDENTIFIER + "," + FCResourceManager.ANIMATIONS_FOLDER, null, 0, 0);
			frm.addResource(FCResourceManager.IMAGES_FOLDER_IDENTIFIER + "," + FCResourceManager.WEAPONS_FOLDER, null, 0, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		if (currentAnimation != null && currentAnimation.getCurrentAnimation() != null)
		{
			g.setColor(Color.black);
			g.drawRect(playRect.getX() - 50, playRect.getY() + 100, playRect.getWidth(), playRect.getHeight());
			g.draw(playRect);
			g.draw(loopRect);
			g.drawString("Play", 380,  680);
			g.drawString("Loop", 530,  680);
			currentAnimation.drawAnimation(drawX, drawY, g);
		}

		g.setColor(Color.black);
		animationFileSelector.render(g);
		g.setColor(Color.black);
		weaponSelector.render(g);

		g.setColor(Color.black);
		if (animationSelector != null)
			animationSelector.render(g);

		g.setColor(Color.black);

		g.drawString("Escape - Dev Menu", container.getWidth() - 250, container.getHeight() - 30);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (currentAnimation != null && currentAnimation.getCurrentAnimation() != null)
		{
			currentAnimation.update(delta);
		}

		if (updateDelta > 0)
			updateDelta = Math.max(0, updateDelta - delta);
		else if (container.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
		{
			this.updateDelta = 200;

			int x = container.getInput().getMouseX();
			int y = container.getInput().getMouseY();

			animationFileSelector.mouseClicked(x, y);
			weaponSelector.mouseClicked(x, y);
			if (animationSelector != null)
				animationSelector.mouseClicked(x, y);

			if (currentAnimation != null && currentAnimation.getCurrentAnimation() != null)
			{
				if (playRect.contains(x, y))
					currentAnimation.resetCurrentAnimation();
				if (loopRect.contains(x, y))
					currentAnimation.setAnimation(animationSelector.getSelectedResource(), true);
			}
		}

		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE))
		{
			game.enterState(CommRPG.STATE_GAME_MENU_DEVEL);
		}
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_ANIM_VIEW;
	}

	@Override
	public boolean resourceSelected(String selectedItem,
			ResourceSelector parentSelector) {
		if (parentSelector == animationFileSelector)
		{
			try {
				frm.addResource(FCResourceManager.ANIMATION_IDENTIFIER + "," + selectedItem + "," + FCResourceManager.ANIMATIONS_FOLDER + "/" + selectedItem,  null, 0, 0);
				currentAnimation = new AnimationWrapper(frm.getSpriteAnimations().get(selectedItem));
				ArrayList<String> animNames = new ArrayList<>(currentAnimation.getAnimations());
				animationSelector = new ResourceSelector("Anim", 600, animNames);
				animationSelector.setListener(this);
				weaponSelector.setSelectedIndex(-1);
			} catch (Throwable t ) {
				t.printStackTrace();
				JOptionPane.showMessageDialog(null, "Unable to load the selected animation: " + t.getMessage(),
						"Unable to load animation", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		else if (parentSelector == animationSelector)
		{
			currentAnimation.setAnimation(selectedItem, false);
			Point p = currentAnimation.getCurrentAnimation().getFirstFramePosition();

			if (Math.abs(p.getX()) < 100 && Math.abs(p.getY()) < 100)
			{
				drawX = 400;
				drawY = 450;
			}
			else
			{
				drawX = 0;
				drawY = 650;
			}
		}
		else if (parentSelector == weaponSelector)
		{
			if (currentAnimation != null)
			{
				currentAnimation.setWeapon(frm.getImages().get(selectedItem.replace(FCResourceManager.WEAPONS_EXTENSION, "")));
			}
		}

		return true;
	}

}
