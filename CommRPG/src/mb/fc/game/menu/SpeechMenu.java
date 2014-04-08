package mb.fc.game.menu;

import java.util.ArrayList;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class SpeechMenu extends Menu
{	
	private int x;
	private int width;
	private ArrayList<String> panelText;
	private int textIndex = 0;
	private int triggerId = -1;
	private Image portrait;
	
	public SpeechMenu(String text, GameContainer gc) 
	{
		this(text, gc, -1, -1, null);
	}
	
	public SpeechMenu(String text, GameContainer gc, int portraitId, StateInfo stateInfo) 
	{
		this(text, gc, -1, portraitId, stateInfo);
	}
	
	public SpeechMenu(String text, GameContainer gc, int triggerId, int portraitId, StateInfo stateInfo) 
	{
		super(Panel.PANEL_SPEECH);
		width = gc.getWidth() - 100;
		x = 50; 
		
		int maxTextWidth = width - 30;		
		int spaceWidth = SPEECH_FONT.getWidth("_");
		String[] splitText = text.split(" ");
		int currentLineWidth = 0;
		String currentLine = "";
		
		panelText = new ArrayList<String>();
						
		for (int i = 0; i < splitText.length; i++)
		{
			int wordWidth = SPEECH_FONT.getWidth(splitText[i]);
			
			if (wordWidth + currentLineWidth <= maxTextWidth)
			{
				currentLine += " " + splitText[i];
				currentLineWidth += wordWidth + spaceWidth;
			}
			else
			{
				i--;
				currentLineWidth = 0;				
				panelText.add(currentLine);
				currentLine = "";				
			}			
		}
		
		if (currentLineWidth > 0)
			panelText.add(currentLine);
		
		this.triggerId = triggerId;

		if (portraitId != -1)
			portrait = stateInfo.getResourceManager().getSpriteSheets().get("portraits").getSprite(portraitId, 0);		
		else
			portrait = null;
	}

	public void render(FCGameContainer gc, Graphics graphics)
	{
		Panel.drawPanelBox(x, gc.getHeight() - 120, width , 115, graphics);
		graphics.setFont(SPEECH_FONT);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		// graphics.setFont(ufont);
	
		for (int i = textIndex; i < Math.min(textIndex + 3, panelText.size()); i++)
		{
			graphics.drawString(panelText.get(i), x + 15, 
					gc.getHeight() - 110 + (i - textIndex) * 29);
		}
		
		if (portrait != null)
			graphics.drawImage(portrait, 10, 10);
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		if (input.isKeyDown(KeyMapping.BUTTON_3))
		{
			if (textIndex + 4 < panelText.size())
			{
				textIndex += 4;
				return MenuUpdate.MENU_ACTION_LONG;
			}
			else
			{
				System.out.println("SEND TRIGGER " + triggerId);
				if (triggerId != -1)
					stateInfo.getResourceManager().getTriggerEventById(triggerId).perform(stateInfo);
				return MenuUpdate.MENU_CLOSE;
			}
		}
			
		return MenuUpdate.MENU_NO_ACTION;
	}
}
