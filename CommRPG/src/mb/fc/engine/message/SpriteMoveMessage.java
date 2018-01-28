package mb.fc.engine.message;

import mb.fc.game.constants.Direction;
import mb.fc.game.sprite.AnimatedSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.map.Map.Stairs;

public class SpriteMoveMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private int spriteId;
	private Direction direction;
	private Stairs stairs = null;

	public SpriteMoveMessage(AnimatedSprite sprite,
			Direction direction) {
		this(sprite, direction, null);
	}
	
	public SpriteMoveMessage(AnimatedSprite sprite,
			Direction direction, Stairs stairs) {
		super(MessageType.OVERLAND_MOVE_MESSAGE);
		this.spriteId = sprite.getId();
		this.direction = direction;
		this.stairs = stairs;
	}



	public AnimatedSprite getSprite(Iterable<Sprite> sprites) {
		for (Sprite s : sprites)
		{
			if (s.getId() == spriteId && s instanceof AnimatedSprite)
				return (AnimatedSprite) s;
		}
		return null;
	}

	public Direction getDirection() {
		return direction;
	}

	public Stairs getStairs() {
		return stairs;
	}
}
