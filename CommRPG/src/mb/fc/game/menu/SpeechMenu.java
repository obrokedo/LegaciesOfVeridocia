package mb.fc.game.menu;

import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class SpeechMenu extends Menu
{	
	private int x;
	private int y = 60 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];
	private int width;
	private ArrayList<String> panelText;
	private int textIndex = 0;
	private int triggerId = -1;
	private Image portrait;
	private boolean initialized = false;	
	
	private boolean textMoving = true;
	private int textMovingIndex = 0;
	private boolean attackCin = false;
	
	private long waitUntil = -1;
	private String waitingOn = null;
	private static final String CHAR_PAUSE = "{";
	private static final String CHAR_SOFT_STOP = "}";
	private static final String CHAR_HARD_STOP = "]";
	private static final String CHAR_LINE_BREAK = "[";
	
	public SpeechMenu(String text, FCGameContainer gc, boolean attackCin) 
	{
		this(text, gc, -1, -1, null);
		this.attackCin = attackCin;		
		if (attackCin)
		{
			y = 0;
			this.textIndex = panelText.size() - 1;
		}
	}
	
	public SpeechMenu(String text, FCGameContainer gc, int portraitId, StateInfo stateInfo) 
	{
		this(text, gc, -1, portraitId, stateInfo);
	}
	
	public SpeechMenu(String text, FCGameContainer gc, int triggerId, 
			int portraitId, StateInfo stateInfo) 
	{
		super(Panel.PANEL_SPEECH);
		width = gc.getWidth() - 100 - gc.getDisplayPaddingX() * 2;
		x = 50 + gc.getDisplayPaddingX();
		
		int maxTextWidth = width;		
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
				boolean lineBreak = false;
				if (splitText[i].contains(CHAR_LINE_BREAK))
					lineBreak = true;
				
				currentLine += " " + splitText[i].replace(CHAR_LINE_BREAK, "");
				currentLineWidth += wordWidth + spaceWidth;
				
				if (lineBreak)
				{
					currentLineWidth = 0;				
					panelText.add(currentLine);
					currentLine = "";		
				}
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
		Panel.drawPanelBox(x, gc.getHeight() - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 60 + y, width , CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 60 - 5, graphics);
		
		if (!initialized)
			return;
		
		graphics.setFont(SPEECH_FONT);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		// graphics.setFont(ufont);
	
		for (int i = Math.max(0, textIndex - 2); i <= textIndex; i++)
		{			
			graphics.drawString((i == textIndex ? panelText.get(i).substring(0, textMovingIndex) : panelText.get(i)), x + 15, 
					gc.getHeight() - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 60 + 10 +  (i - textIndex + (textIndex >= 2 ? 2 : textIndex)) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 15);
		}
		
		if (portrait != null)
		{
			Panel.drawPanelBox(x, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 12, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 62, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 78, graphics, Color.black);
			graphics.drawImage(portrait, x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 12 + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7);
			// graphics.drawImage(portrait, x, 25);
		}
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) 
	{
		if (!initialized)
		{			
			if (y <= 0)
			{
				initialized = true;
			}
			else
				y = Math.max(y - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 8, 0);
			
			return MenuUpdate.MENU_NO_ACTION;
		}
		
		if (textMoving)
		{
			if (textMovingIndex + 1 > panelText.get(textIndex).length())
			{
				textMovingIndex = panelText.get(textIndex).length();
				textMovingIndex = 0;
				if (textIndex + 1 < panelText.size())
					textIndex++;
				else
				{
					System.out.println("SEND TRIGGER " + triggerId);
					if (triggerId != -1)
						stateInfo.getResourceManager().getTriggerEventById(triggerId).perform(stateInfo);
					return MenuUpdate.MENU_CLOSE;
				}
				// textMoving = false;				
			}
			else
			{
				String nextLetter = panelText.get(textIndex).substring(textMovingIndex, textMovingIndex + 1);
				if (nextLetter.equalsIgnoreCase(CHAR_HARD_STOP))
				{
					textMoving = false;
					waitingOn = CHAR_HARD_STOP;
					System.out.println("HARD STOP");
				}
				else if (nextLetter.equalsIgnoreCase(CHAR_SOFT_STOP))
				{
					textMoving = false;					
					
					String[] softSplit = panelText.get(textIndex).substring(textMovingIndex).split(" ");
					
					if (softSplit.length > 1 && softSplit[0].length() > 1 && softSplit[0].replaceFirst("[0-9]", "").length() != softSplit[0].length())
					{
						waitUntil = System.currentTimeMillis() + Integer.parseInt(softSplit[0].substring(1));
						waitingOn = softSplit[0];
						System.out.println("WAITING ON SOFT");
					}
					else
					{
						waitUntil = System.currentTimeMillis() + 2500;
						waitingOn = CHAR_SOFT_STOP;
						System.out.println("WAITING ON SOFT UNSPEC");
					}
				}
				else if (nextLetter.equalsIgnoreCase(CHAR_PAUSE))
				{
					textMoving = false;
					waitUntil = System.currentTimeMillis() + 400;
					waitingOn = CHAR_PAUSE;
				}
				
				if (textMoving)
					textMovingIndex += 1;
				else
					panelText.set(textIndex, panelText.get(textIndex).replaceFirst("\\" + waitingOn, ""));
				// This is a bit of a kludge, when we are in the attack cinematic we want the text to scroll but we don't
				// want the :talking" sound effect. Really this should probably be a different boolean rather then just
				// checking to see if the state info is not null
				if (!attackCin && textMovingIndex % 6 == 0 && stateInfo != null)
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "speechblip", .15f, false));
			}
		}
		
		if (input.isKeyDown(KeyMapping.BUTTON_3))
		{
			if (waitingOn != null)
			{
				waitingOn = null;
				waitUntil = -1;
				textMoving = true;
			}
		}
		
		if (waitUntil != -1 && waitUntil <= System.currentTimeMillis())
		{
			textMoving = true;
			waitUntil = -1;
		}
			
		return MenuUpdate.MENU_NO_ACTION;
	}
}
