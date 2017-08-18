package mb.fc.engine.message;

import java.util.ArrayList;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;

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

	private ArrayList<Integer> sprites;

	public SpriteContextMessage(MessageType messageType, CombatSprite sprite)
	{
		super(messageType);
		this.sprites = new ArrayList<Integer>();
		sprites.add(sprite.getId());
	}

	public SpriteContextMessage(MessageType messageType, ArrayList<CombatSprite> sprites)
	{
		super(messageType);
		this.sprites = new ArrayList<Integer>();
		for (CombatSprite cs : sprites)
			this.sprites.add(cs.getId());
	}

	public CombatSprite getSprite(Iterable<Sprite> sprites) {
		for (Sprite s : sprites)
		{
			if (s.getId() == this.sprites.get(0))
				return (CombatSprite) s;
		}
		return null;
	}

	public CombatSprite getCombatSprite(Iterable<CombatSprite> sprites) {
		for (Sprite s : sprites)
		{
			if (s.getId() == this.sprites.get(0))
				return (CombatSprite) s;
		}
		return null;
	}

	public ArrayList<CombatSprite> getSprites(Iterable<Sprite> sprites) {
		ArrayList<CombatSprite> cSprites = new ArrayList<CombatSprite>();
		for (Sprite s : sprites)
		{
			for (Integer id : this.sprites)
			{
				if (id == s.getId())
				{
					cSprites.add((CombatSprite) s);
					break;
				}
			}
		}
		return cSprites;
	}

	public ArrayList<Integer> getSpriteIds() {
		return sprites;
	}
}
