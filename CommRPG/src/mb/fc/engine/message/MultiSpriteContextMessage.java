package mb.fc.engine.message;

import java.util.ArrayList;

import mb.fc.game.sprite.CombatSprite;

public class MultiSpriteContextMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<CombatSprite> sprites;

	public MultiSpriteContextMessage(int messageType, ArrayList<CombatSprite> sprites) 
	{
		super(messageType);
		
		this.sprites = sprites;
	}

	public ArrayList<CombatSprite> getSprites()
	{
		return sprites;
	}
}
