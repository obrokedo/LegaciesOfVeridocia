package mb.fc.game.ui;

import java.io.File;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class ResourceSelector {
	private ArrayList<String> resourceFiles = new ArrayList<String>();
	private int longestNameWidth = 0;
	private int selectedItem = -1;
	private int drawX = 0;
	private int menuIndex = 0;
	private boolean minimized = false;
	private ResourceSelectorListener listener;
	private String title;

	public ResourceSelector(String title, int drawX,
			boolean fromLeft, String rootFolder, String suffix, GameContainer gc)
	{
		File dir = new File(rootFolder);
		resourceFiles.clear();

		for (File file : dir.listFiles())
		{
			if (file.isDirectory() || file.getName().startsWith("."))
				continue;

			if (file.getName().endsWith(suffix))
			{
				resourceFiles.add(file.getName());
				int width = gc.getDefaultFont().getWidth(file.getName());

				if (width > longestNameWidth)
					longestNameWidth = width;
			}
		}
		longestNameWidth += 50;

		if (fromLeft)
			this.drawX = drawX;
		else
			this.drawX = gc.getWidth() - longestNameWidth - drawX;

		this.title = title;
	}

	public ResourceSelector(String title, int drawX, ArrayList<String> values)
	{
		longestNameWidth = 150;
		this.resourceFiles = values;
		this.drawX = drawX;
		this.title = title;
	}


	public void render(Graphics g)
	{
		if (!minimized)
		{
			g.drawString(title, drawX + 5, 35);
			for (int i = 0; i < Math.min(15, resourceFiles.size()); i++)
			{
				g.setColor(Color.blue);
				g.fillRect(drawX + 0, 60 + 30 * i, longestNameWidth, 25);

				if (i + menuIndex != selectedItem)
					g.setColor(Color.white);
				else
					g.setColor(Color.red);
				g.drawString(resourceFiles.get(i + this.menuIndex), drawX + 25, 65 + 30 * i);
			}

			if (resourceFiles.size() > 15)
			{
				if (menuIndex > 0)
				{
					g.setColor(Color.blue);
					g.fillRect(drawX + longestNameWidth + 15, 60, 25, 25);
					g.setColor(Color.white);
					g.drawString("^", drawX + longestNameWidth + 22, 65);
				}

				if (menuIndex < resourceFiles.size() - 15)
				{
					g.setColor(Color.blue);
					g.fillRect(drawX + longestNameWidth + 15, 480, 25, 25);
					g.setColor(Color.white);
					g.drawString("v", drawX + longestNameWidth + 22, 485);
				}
			}
		}
		else
		{
			g.setColor(Color.blue);
			g.fillRect(drawX + 0, 30, longestNameWidth, 25);

			g.setColor(Color.white);

			g.drawString(title, drawX + 5, 35);
		}

		g.setColor(Color.white);
	}

	public void mouseClicked(int x, int y)
	{
		if (x > drawX && x < drawX + longestNameWidth)
		{
			if (y > 60 && y < 545)
			{
				int item = (y - 60) / 30 + this.menuIndex;

				if (!minimized)
				{
					if (item < this.resourceFiles.size())
					{
						this.selectedItem = item;

						if (listener != null)
						{
							if (!listener.resourceSelected(resourceFiles.get(selectedItem), this))
								this.selectedItem = -1;
						}
					}
				}
			}
			else
			{
				if (y < 60)
				{
					minimized = !minimized;
				}
			}
		}
		else if (x > longestNameWidth + 15 && x < longestNameWidth + 40)
		{
			if (menuIndex > 0 && y > 70 && y < 95)
			{
				menuIndex--;
			}
			else if (menuIndex < resourceFiles.size() - 15 && y > 490 && y < 515)
			{
				menuIndex++;
			}
		}
	}

	public String getSelectedResource()
	{
		if (selectedItem == -1)
			return null;
		return resourceFiles.get(selectedItem);
	}

	public interface ResourceSelectorListener
	{
		public boolean resourceSelected(String selectedItem, ResourceSelector parentSelector);
	}

	public void setListener(ResourceSelectorListener listener) {
		this.listener = listener;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedItem = selectedIndex;
	}
}
