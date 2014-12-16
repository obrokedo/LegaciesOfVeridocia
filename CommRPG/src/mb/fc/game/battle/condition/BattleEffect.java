package mb.fc.game.battle.condition;

import java.io.Serializable;

import mb.fc.game.sprite.CombatSprite;

public class BattleEffect implements Serializable
{
	private static final long serialVersionUID = 1L;
	private boolean isNegativeEffect = false;

	public void performEffect(CombatSprite attacker, CombatSprite target)
	{

	}

	public boolean isNegativeEffect() {
		return isNegativeEffect;
	}
}
