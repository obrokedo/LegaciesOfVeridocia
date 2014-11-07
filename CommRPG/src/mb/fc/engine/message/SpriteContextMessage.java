package mb.fc.engine.message;

import java.util.ArrayList;

import mb.fc.game.sprite.CombatSprite;

/**
 * A reusuable message that takes a custom message type and has an associated
 * CombatSprite or list of CombatSprites that should be related to the message
 *
 * @author Broked
 *
 */
public class SpriteContextMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private ArrayList<CombatSprite> sprites;

	public SpriteContextMessage(int messageType, CombatSprite sprite)
	{
		super(messageType);
		this.sprites = new ArrayList<CombatSprite>();
		sprites.add(sprite);
	}

	public SpriteContextMessage(int messageType, ArrayList<CombatSprite> sprites)
	{
		super(messageType);
		this.sprites = new ArrayList<CombatSprite>();
		this.sprites.addAll(sprites);
	}

	public CombatSprite getSprite() {
		return sprites.get(0);
	}

	public ArrayList<CombatSprite> getSprites() {
		return sprites;
	}
}
