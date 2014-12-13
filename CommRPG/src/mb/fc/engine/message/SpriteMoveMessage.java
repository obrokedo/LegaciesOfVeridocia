package mb.fc.engine.message;

import mb.fc.game.constants.Direction;
import mb.fc.game.sprite.AnimatedSprite;
import mb.fc.game.sprite.Sprite;

public class SpriteMoveMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private int spriteId;
	private Direction direction;

	public SpriteMoveMessage(AnimatedSprite sprite,
			Direction direction) {
		super(MessageType.OVERLAND_MOVE_MESSAGE);
		this.spriteId = sprite.getId();
		this.direction = direction;
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
}
