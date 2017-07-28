package mb.fc.game.ui;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import mb.fc.game.Timer;

public class ListUI {
	protected ArrayList<Button> resourceFileButtons = new ArrayList<Button>();
	protected int longestNameWidth = 0;
	protected Button selectedItem = null;
	protected int drawX, drawY = 0;
	protected int menuIndex = 0;
	protected boolean minimized = false;
	protected ResourceSelectorListener listener;
	protected String title;
	protected int listLength = 15;
	protected int itemSpacing = 5;
	protected int buttonHeight = 20;
	private Timer clickCooldown = new Timer(200);
	private Button upButton, downButton;
	
	public ListUI(String title)
	{
		this.title = title;
	}

	public ListUI(String title, int drawX, ArrayList<String> values)
	{
		longestNameWidth = 150;
		this.drawX = drawX;
		for (String value : values)
			this.resourceFileButtons.add(new Button(drawX, 0, longestNameWidth, buttonHeight, value));
		this.title = title;
		this.layoutItems();
		this.setupDirectionalButtons();
	}
	
	protected void setupDirectionalButtons() {
		this.upButton = new Button(drawX + longestNameWidth, drawY + 25, buttonHeight, buttonHeight, "^");
		this.downButton = new Button(drawX + longestNameWidth, 
				(listLength - 1) * buttonHeight + drawY + 25 + (listLength - 1) * itemSpacing, 
				buttonHeight, buttonHeight, "v");
		
		upButton.setVisible(menuIndex > 0);
		downButton.setVisible(menuIndex < resourceFileButtons.size() - listLength);
	}


	public void render(Graphics g)
	{
		if (!minimized)
		{
			g.drawString(title, drawX + 5, drawY);
			for (int i = 0; i < Math.min(listLength, resourceFileButtons.size()); i++)
			{
				Button button = resourceFileButtons.get(i + menuIndex);
				button.render(g);
			}

			
			if (resourceFileButtons.size() > listLength)
			{
				upButton.render(g);
				downButton.render(g);
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
	
	public void update(GameContainer container, int delta) {
		int x = container.getInput().getMouseX();
		int y = container.getInput().getMouseY();
		this.clickCooldown.update(delta);
		
		boolean clicked = container.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && clickCooldown.perform();
		
		for (Button button : resourceFileButtons)
		{
			if (button.handleUserInput(x, y, clicked))
			{
				this.selectedItem = button;
				
				if (listener != null)
				{
					if (!listener.resourceSelected(selectedItem.getText(), this))
						this.selectedItem = null;
				}
				
				
					
				break;
			}
			
			if (selectedItem == button)
				button.setForegroundColor(Color.red);
			else
			{
				button.setForegroundColor(Color.white);
			}
		}			
		
		if (upButton.handleUserInput(x, y, clicked))
		{
			menuIndex--;
			
			upButton.setVisible(menuIndex > 0);
			downButton.setVisible(menuIndex < resourceFileButtons.size() - listLength);
			this.layoutItems();
		}
		else if (downButton.handleUserInput(x, y, clicked))
		{
			menuIndex++;
			
			upButton.setVisible(menuIndex > 0);
			downButton.setVisible(menuIndex < resourceFileButtons.size() - listLength);
			this.layoutItems();
		}
	}

	public String getSelectedResource()
	{
		if (selectedItem == null)
			return null;
		return selectedItem.getText();
	}

	public interface ResourceSelectorListener
	{
		public boolean resourceSelected(String selectedItem, ListUI parentSelector);
	}

	public void setListener(ResourceSelectorListener listener) {
		this.listener = listener;
	}

	public void setSelectedIndex(int selectedIndex) {
		if (selectedIndex < 0 || selectedIndex > resourceFileButtons.size())
			this.selectedItem = null;
		else
			this.selectedItem = resourceFileButtons.get(selectedIndex);
	}
	
	protected void layoutItems()
	{
		for (int buttonIndex = 0; buttonIndex < resourceFileButtons.size(); buttonIndex++) {
			Button button = this.resourceFileButtons.get(buttonIndex);
			button.setX(drawX);
			if (buttonIndex < menuIndex)
			{
				button.setVisible(false);
			}
			else if (buttonIndex >= menuIndex && buttonIndex  < menuIndex + listLength)
			{
				button.setY((buttonIndex - menuIndex) * buttonHeight + drawY + 25 + (buttonIndex - menuIndex) * itemSpacing);
				button.setVisible(true);
			}
			else
			{
				button.setVisible(false);
			}
		}
	}
}
