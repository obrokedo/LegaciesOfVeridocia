package mb.fc.engine.message;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;

import org.newdawn.slick.util.pathfinding.Path;

public class OverlandMoveMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private Path path;
	private CombatSprite sprite;

	public OverlandMoveMessage(CombatSprite sprite, Path path) 
	{
		super(Message.MESSAGE_OVERLAND_MOVE_MESSAGE);
		this.path = path;
		this.sprite = sprite;
	}
	
	public Sprite getSprite() {
		return sprite;
	}

	public Path getPath() {
		return path;
	}
}
