package mb.fc.engine.state;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;

import mb.fc.engine.CommRPG;
import mb.fc.game.ui.ResourceSelector;
import mb.fc.game.ui.ResourceSelector.ResourceSelectorListener;
import mb.fc.loading.FCLoadingRenderSystem;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.loading.LoadingState;
import mb.fc.loading.MapParser;
import mb.fc.loading.TilesetParser;
import mb.fc.map.Map;
import mb.fc.map.MapObject;
import mb.fc.utils.gif.GifFrame;
import mb.fc.utils.planner.PlannerFrame;

/**
 * Development menu state that allows loading maps programatically
 *
 * @author Broked
 *
 */
public class DevelMenuState extends MenuState implements ResourceSelectorListener
{
	private ResourceSelector mapSelector, textSelector, entranceSelector;
	private PlannerFrame plannerFrame = new PlannerFrame();
	private GifFrame quickAnimate = new GifFrame();
	private ParticleSystem ps;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {

		this.game = game;
		this.gc = container;
		gameSetup(game, gc);
		mapSelector = new ResourceSelector("Select Map", 0, true, "map", ".tmx", container);
		mapSelector.setListener(this);
		textSelector = new ResourceSelector("Select Text", 0, false, "mapdata", "", container);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException
	{
		g.setColor(Color.red);
		g.drawString("DEVELOPMENT MODE", 5, 5);

		g.setColor(Color.white);

		mapSelector.render(g);
		textSelector.render(g);

		if (entranceSelector != null)
			entranceSelector.render(g);

		if (mapSelector.getSelectedResource() != null &&
				textSelector.getSelectedResource() != null)
		{
			if (entranceSelector != null && entranceSelector.getSelectedResource() != null)
			{
				g.setColor(Color.darkGray);
				g.fillRect(0, 550, 150, 25);
				g.fillRect(0, 610, 150, 25);

				g.setColor(Color.white);
				g.drawString("Load Town", 25, 555);
				g.drawString("Load Battle",25, 615);
			}

			g.setColor(Color.darkGray);
			g.fillRect(0, 580, 150, 25);

			g.setColor(Color.white);
			g.drawString("Load Cin", 25, 585);
		}

		g.drawString(version, container.getWidth() / 2, container.getHeight() - 30);
		g.drawString("F1 - Toggle Main/Dev Menu", container.getWidth() - 250, container.getHeight() - 150);
		g.drawString("F2 - Open Planner", container.getWidth() - 250, container.getHeight() - 120);
		g.drawString("F3 - Open Quick Animator", container.getWidth() - 250, container.getHeight() - 90);
		g.drawString("F4 - Open Animation Viewer", container.getWidth() - 250, container.getHeight() - 60);
		g.drawString("F5 - Run Test", container.getWidth() - 250, container.getHeight() - 30);

		if (initialized)
			ps.render();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException
	{
		if (container.getInput().isKeyPressed(Input.KEY_F1))
		{
			((LoadingState) game.getState(CommRPG.STATE_GAME_LOADING)).setLoadingInfo("/menu/MainMenu", null, false, true,
					new FCResourceManager(),
						(LoadableGameState) game.getState(CommRPG.STATE_GAME_MENU),
							new FCLoadingRenderSystem(container));

			game.enterState(CommRPG.STATE_GAME_LOADING);
		}



		if (container.getInput().isKeyPressed(Input.KEY_F2) || container.getInput().isKeyPressed(Input.KEY_P))
		{
			if (!plannerFrame.isVisible())
				plannerFrame.setVisible(true);
		}

		if (container.getInput().isKeyPressed(Input.KEY_F3))
		{
			if (!quickAnimate.isVisible())
				quickAnimate.setVisible(true);
		}

		if (container.getInput().isKeyPressed(Input.KEY_F4))
		{
			game.enterState(CommRPG.STATE_GAME_ANIM_VIEW);
		}

		if (container.getInput().isKeyPressed(Input.KEY_F5))
		{
			CommRPG.TEST_MODE_ENABLED = true;
			this.gameSetup(game, container);
			start(gc, LoadTypeEnum.CINEMATIC, "neweriumcastle", "neweriumcastle", null);
		}

		if (container.getInput().isKeyDown(Input.KEY_F7))
		{
			((CommRPG) game).toggleFullScreen();
			updateDelta = 200;
			System.out.println("TOGGLE");
		}

		if (updateDelta > 0)
			updateDelta = Math.max(0, updateDelta - delta);
		else if (container.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
		{
			this.updateDelta = 200;

			int x = container.getInput().getMouseX();
			int y = container.getInput().getMouseY();

			mapSelector.mouseClicked(x, y);
			textSelector.mouseClicked(x, y);

			if (entranceSelector != null)
				entranceSelector.mouseClicked(x, y);

			if (((y < 15 && x < 15) || (y > 550 && y < 635)) && mapSelector.getSelectedResource() != null &&
				textSelector.getSelectedResource() != null)
			{
				LoadTypeEnum action = LoadTypeEnum.values()[(y - 550) / 30];
				if (y < 15)
					action = LoadTypeEnum.TOWN;

				String entrance = null;

				if (entranceSelector != null)
				{
					entrance = entranceSelector.getSelectedResource();
				}
				start(gc, action, mapSelector.getSelectedResource().replaceFirst(".tmx", ""), textSelector.getSelectedResource(), entrance);
			}


		}

		if (initialized)
			ps.update(delta);
	}

	@Override
	public void stateLoaded(FCResourceManager resourceManager) {
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_MENU_DEVEL;
	}

	@Override
	public boolean resourceSelected(String selectedItem,
			ResourceSelector parentSelector) {
		Map map = new Map();
		ArrayList<String> entrances = new ArrayList<>();
		try {
			MapParser.parseMap("map/" + selectedItem, map, new TilesetParser(), null);


			for (MapObject mo : map.getMapObjects())
				if (mo.getKey().equalsIgnoreCase("start"))
					entrances.add(mo.getParam("exit"));

			entranceSelector = new ResourceSelector("Select Entrance", (gc.getWidth() - 150) / 2, entrances);
			return true;

		} catch (Throwable t) {
			t.printStackTrace();
			JOptionPane.showMessageDialog(null, "The selected map contains errors and may not be loaded: " + t.getMessage());
			entranceSelector = null;
		}
		return false;
	}
}
