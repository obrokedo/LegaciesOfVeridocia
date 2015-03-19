package mb.fc.engine.state;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import mb.fc.engine.CommRPG;
import mb.fc.loading.FCLoadingRenderSystem;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.loading.LoadingState;
import mb.fc.loading.MapParser;
import mb.fc.loading.TilesetParser;
import mb.fc.map.Map;
import mb.fc.map.MapObject;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Development menu state that allows loading maps programatically
 *
 * @author Broked
 *
 */
public class DevelMenuState extends MenuState
{
	private ResourceSelector mapSelector, textSelector, entranceSelector;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {

		this.game = game;
		this.gc = container;
		gameSetup(game, gc);
		mapSelector = new ResourceSelector(true, "map");
		textSelector = new ResourceSelector(false, "text");
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException
	{
		g.setColor(Color.red);
		g.drawString("DEVELOPMENT MODE", 5, 5);

		g.setColor(Color.white);
		g.drawString("Select Map", 5, 35);
		g.drawString("Select Entrance", (gc.getWidth() - 150) / 2, 35);
		g.drawString("Select Text", gc.getWidth() - 150, 35);

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
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException
	{
		if (container.getInput().isKeyDown(Input.KEY_F1))
		{
			((LoadingState) game.getState(CommRPG.STATE_GAME_LOADING)).setLoadingInfo("/menu/MainMenu", null, false, true,
					new FCResourceManager(),
						(LoadableGameState) game.getState(CommRPG.STATE_GAME_MENU),
							new FCLoadingRenderSystem(container));

			game.enterState(CommRPG.STATE_GAME_LOADING);
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
				int action = (y - 550) / 30;
				if (y < 15)
					action = 0;

				String entrance = null;

				if (entranceSelector != null)
				{
					entrance = entranceSelector.getSelectedResource();
				}
				System.out.println("START");
				start(gc, action, mapSelector.getSelectedResource().replaceFirst(".tmx", ""), textSelector.getSelectedResource(), entrance);
			}
		}
	}

	@Override
	public void stateLoaded(FCResourceManager resourceManager) {

	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_MENU_DEVEL;
	}

	private class ResourceSelector
	{
		private ArrayList<String> mapFiles = new ArrayList<String>();
		private int longestNameWidth = 0;
		private int selectedItem = -1;
		private int drawX = 0;
		private int menuIndex = 0;
		private boolean isMap = false;
		private ArrayList<String> entrances;

		public ResourceSelector(boolean onLeft, String rootFolder)
		{
			File dir = new File(rootFolder);
			mapFiles.clear();

			for (String map : dir.list())
			{
				mapFiles.add(map);
				int width = gc.getDefaultFont().getWidth(map);

				if (width > longestNameWidth)
					longestNameWidth = width;
			}
			longestNameWidth += 50;

			if (!onLeft)
			{
				drawX = gc.getWidth() - longestNameWidth;
			}

			isMap = onLeft;

			if (isMap)
			{
				entrances = new ArrayList<String>();
			}
		}

		public ResourceSelector(int drawX, ArrayList<String> values)
		{
			longestNameWidth = 150;
			this.mapFiles = values;
			this.drawX = drawX;
		}


		public void render(Graphics g)
		{
			for (int i = 0; i < Math.min(15, mapFiles.size()); i++)
			{
				g.setColor(Color.blue);
				g.fillRect(drawX + 0, 70 + 30 * i, longestNameWidth, 25);

				if (i + menuIndex != selectedItem)
					g.setColor(Color.white);
				else
					g.setColor(Color.red);
				g.drawString(mapFiles.get(i + this.menuIndex), drawX + 25, 75 + 30 * i);
			}

			if (mapFiles.size() > 15)
			{
				if (menuIndex > 0)
				{
					g.setColor(Color.blue);
					g.fillRect(drawX + longestNameWidth + 15, 70, 25, 25);
					g.setColor(Color.white);
					g.drawString("^", drawX + longestNameWidth + 22, 75);
				}

				if (menuIndex < mapFiles.size() - 15)
				{
					g.setColor(Color.blue);
					g.fillRect(drawX + longestNameWidth + 15, 490, 25, 25);
					g.setColor(Color.white);
					g.drawString("v", drawX + longestNameWidth + 22, 495);
				}
			}
		}

		public void mouseClicked(int x, int y)
		{
			if (x > drawX && x < drawX + longestNameWidth)
			{
				if (y > 70 && y < 555)
				{
					int item = (y - 70) / 30 + this.menuIndex;
					if (item < this.mapFiles.size())
					{
						this.selectedItem = item;

						if (isMap)
						{
							Map map = new Map();
							entrances.clear();
							try {
								MapParser.parseMap("map/" + mapFiles.get(selectedItem), map, new TilesetParser(), null);


								for (MapObject mo : map.getMapObjects())
									if (mo.getKey().equalsIgnoreCase("start"))
										entrances.add(mo.getParam("exit"));

								entranceSelector = new ResourceSelector((gc.getWidth() - 150) / 2, entrances);

							} catch (Throwable t) {
								t.printStackTrace();
								JOptionPane.showMessageDialog(null, "The selected map contains errors and may not be loaded");
								this.selectedItem = -1;
								entranceSelector = null;
							}
						}
					}
				}
			}
			else if (x > longestNameWidth + 15 && x < longestNameWidth + 40)
			{
				if (menuIndex > 0 && y > 70 && y < 95)
				{
					menuIndex--;
				}
				else if (menuIndex < mapFiles.size() - 15 && y > 490 && y < 515)
				{
					menuIndex++;
				}
			}
		}

		public String getSelectedResource()
		{
			if (selectedItem == -1)
				return null;
			return mapFiles.get(selectedItem);
		}
	}
}
