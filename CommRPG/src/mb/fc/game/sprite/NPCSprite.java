package mb.fc.game.sprite;

import java.util.ArrayList;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.listener.MouseListener;
import mb.fc.game.text.Speech;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Graphics;

public class NPCSprite extends AnimatedSprite implements MouseListener
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Speech> speeches;
	
	public NPCSprite(String imageName, 
			ArrayList<Speech> speeches) 
	{
		super(0, 0, imageName);
		this.speeches = speeches;
	}

	@Override
	public void initializeSprite(StateInfo stateInfo) {
		super.initializeSprite(stateInfo);
		this.spriteSheet = stateInfo.getResourceManager().getSpriteSheets().get(imageName);
		this.image = spriteSheet.getSprite(0, 0);
		this.imageIndex = 0;
		if (speeches != null)
		{
			stateInfo.registerMouseListener(this);
		}
	}
	
	@Override
	public void render(Camera camera, Graphics graphics, FCGameContainer cont) 
	{
		graphics.drawImage(image, this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(), 
			this.getLocY() - camera.getLocationY());
	
	}	

	@Override
	public boolean mouseUpdate(int frameMX, int frameMY, int mapMX,
			int mapMY, boolean leftClicked, boolean rightClicked,
			StateInfo stateInfo) 
	{
		// TODO Stupid tile width...
		if (leftClicked && Panel.contains(getLocX(), getLocX() + stateInfo.getTileWidth(), 
				mapMX, getLocY(), getLocY() + stateInfo.getTileHeight(), mapMY))
		{
			if ((Math.abs(stateInfo.getCurrentSprite().getLocX() - getLocX()) + 
					Math.abs(stateInfo.getCurrentSprite().getLocY() - getLocY())) <= 64)
			{
				triggerButton1Event(stateInfo);
			}
			return true;
		}
		return false;
	}
	
	public void triggerButton1Event(StateInfo stateInfo)
	{
		SPEECHLOOP: for (Speech s : speeches)
		{
			// Check to see if this mesage meets all required quests
			if (s.getRequires() != null && s.getRequires().length > 0)
			{
				for (int i : s.getRequires())
				{
					if (!stateInfo.isQuestComplete(i))
						continue SPEECHLOOP;
				}
			}
			
			// Check to see if the excludes quests have been completed, if so
			// then we can't use this message
			if (s.getExcludes() != null && s.getExcludes().length > 0)
			{
				for (int i : s.getExcludes())
				{						
					if (stateInfo.isQuestComplete(i))
						continue SPEECHLOOP;
				}
			}
			
			stateInfo.sendMessage(new SpeechMessage(Message.MESSAGE_SPEECH, s.getMessage(), s.getTriggerId()));
			break;
		}
	}

	@Override
	public int getZOrder() {
		return MouseListener.ORDER_NPC;
	}
	
	
}
