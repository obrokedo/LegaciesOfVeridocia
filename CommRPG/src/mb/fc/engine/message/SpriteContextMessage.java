package mb.fc.engine.message;

import mb.fc.game.sprite.CombatSprite;

public class SpriteContextMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private CombatSprite sprite;

	public SpriteContextMessage(int messageType, CombatSprite sprite) 
	{
		super(messageType);
		this.sprite = sprite;
	}

	public CombatSprite getSprite() {
		return sprite;
	}
}
