package mb.fc.game.hudmenu;

import java.util.ArrayList;

import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ChatPanel extends Panel 
{	
	private ArrayList<TimedMessage> timedMessages;

	public ChatPanel() {
		super(Panel.PANEL_CHAT);
		this.timedMessages = new ArrayList<TimedMessage>();
	}
	
	public void addMessage(String message)
	{
		this.timedMessages.add(new TimedMessage(message));
		if (timedMessages.size() > 10)
			timedMessages.remove(0);
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) 
	{
		int startY = gc.getHeight() - 50; 
		graphics.setColor(Color.white);
		for (int i = 0; i < timedMessages.size(); i++)
			graphics.drawString(timedMessages.get(i).getName(), 50 + gc.getDisplayPaddingX(), startY - 30 * i);
	}
	
	public void update(int delta)
	{
		for (int i = 0; i < timedMessages.size(); i++)
		{
			if (timedMessages.get(i).update(delta) <= 0)
			{
				timedMessages.remove(i);
				i--;
			}
		}
	}
	
	private class TimedMessage
	{
		private String name;
		private int timeToLive;
		
		public TimedMessage(String name) {
			super();
			this.name = name;
			this.timeToLive = 300;
		}
		int update(int delta)
		{
			timeToLive -= delta;
			return timeToLive;
		}

		public String getName() {
			return name;
		}
		
		
	}
}
