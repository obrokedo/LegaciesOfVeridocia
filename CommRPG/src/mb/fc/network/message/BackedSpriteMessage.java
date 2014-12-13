package mb.fc.network.message;

import java.util.ArrayList;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.MessageType;
import mb.fc.game.sprite.CombatSprite;

public class BackedSpriteMessage extends Message {
	private static final long serialVersionUID = 1L;

	private ArrayList<CombatSprite> sprites = new ArrayList<>();

	public BackedSpriteMessage(ArrayList<CombatSprite> sprites){
		super(MessageType.CLIENT_BROADCAST_HERO);
		this.sprites = sprites;
	}

	public ArrayList<CombatSprite> getSprites() {
		return sprites;
	}
}
