package mb.fc.renderer;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.Message;
import mb.fc.game.dev.BattleOptimizer;
import mb.fc.game.manager.Manager;
import mb.fc.game.sprite.Sprite;

import org.newdawn.slick.Graphics;

public class SpriteRenderer extends Manager
{
	public void render(Graphics g)
	{
		if (CommRPG.BATTLE_MODE_OPTIMIZE)
			BattleOptimizer.render(g);
		for (Sprite s : stateInfo.getSprites())
		{
			if (s.isVisible())
			{
				s.render(stateInfo.getCamera(), g, stateInfo.getPaddedGameContainer(), stateInfo.getTileHeight());
				/*
				switch (s.getSpriteType())
				{
					case Sprite.TYPE_STATIC_SPRITE:
						break;
					case Sprite.TYPE_COMBAT:
						break;
					case Sprite.TYPE_NPC:
						break;
				}
				*/
			}
		}
	}

	@Override
	public void initialize() {}

	@Override
	public void recieveMessage(Message message) {

	}
}
