package mb.fc.engine.config.intr;

import java.io.IOException;

import mb.fc.utils.SpriteAnims;

public interface AnimationParser {
	public SpriteAnims parseAnimations(String animsFile) throws IOException;
}
