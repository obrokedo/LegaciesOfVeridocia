package mb.fc.renderer;

import mb.fc.engine.message.Message;
import mb.fc.game.manager.Manager;
import mb.fc.game.sprite.Sprite;

import org.newdawn.slick.Graphics;

public class SpriteRenderer extends Manager
{	
	public void render(Graphics g) 
	{
		for (Sprite s : stateInfo.getSprites())
		{
			if (s.isVisible())
				s.render(stateInfo.getCamera(), g, stateInfo.getGc());
		}				
	}

	@Override
	public void initialize() {}

	@Override
	public void recieveMessage(Message message) {
		// TODO Auto-generated method stub
		
	}
}
