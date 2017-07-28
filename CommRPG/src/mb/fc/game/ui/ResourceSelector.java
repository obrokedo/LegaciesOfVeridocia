package mb.fc.game.ui;

import java.io.File;

import org.newdawn.slick.GameContainer;

public class ResourceSelector extends ListUI {
	public ResourceSelector(String title, int drawX,
			boolean fromLeft, String rootFolder, 
			String suffix, GameContainer gc)
	{
		super(title);
		File dir = new File(rootFolder);
		resourceFileButtons.clear();

		for (File file : dir.listFiles())
		{
			if (file.isDirectory() || file.getName().startsWith("."))
				continue;

			if (file.getName().endsWith(suffix))
			{
				resourceFileButtons.add(new Button(0, 0, 0, buttonHeight, file.getName()));
				int width = gc.getDefaultFont().getWidth(file.getName());

				if (width > longestNameWidth)
					longestNameWidth = width;
			}
		}
		longestNameWidth += 15;

		for (Button button : resourceFileButtons)
			button.setWidth(longestNameWidth);
		
		if (fromLeft)
			this.drawX = drawX;
		else
			this.drawX = gc.getWidth() - longestNameWidth - drawX;
		this.drawY = 0;

		this.title = title;
		this.layoutItems();
		this.setupDirectionalButtons();
	}
}
