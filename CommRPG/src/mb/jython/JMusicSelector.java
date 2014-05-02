package mb.jython;

import mb.fc.game.sprite.CombatSprite;

public interface JMusicSelector {
	public String getAttackMusic(CombatSprite attacker, boolean targetsAllies);
}
