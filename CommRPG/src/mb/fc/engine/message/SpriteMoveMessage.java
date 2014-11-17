package mb.fc.engine.message;

import mb.fc.game.constants.Direction;
import mb.fc.game.sprite.AnimatedSprite;

public class SpriteMoveMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private AnimatedSprite sprite;
	private Direction direction;

	public SpriteMoveMessage(AnimatedSprite sprite,
			Direction direction) {
		super(MESSAGE_OVERLAND_MOVE_MESSAGE);
		this.sprite = sprite;
		this.direction = direction;
	}

	public AnimatedSprite getSprite() {
		return sprite;
	}

	public Direction getDirection() {
		return direction;
	}
}
