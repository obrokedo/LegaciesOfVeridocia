package mb.fc.game.ai;

import java.awt.Point;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.turnaction.AttackSpriteAction;

public class WarriorAI extends AI
{
	public WarriorAI(int approachType) {
		super(approachType, false);
	}

	@Override
	protected int getConfidence(CombatSprite currentSprite, CombatSprite targetSprite,
			int tileWidth , int tileHeight, Point attackPoint, int distance, StateInfo stateInfo) {
		int damage = Math.max(1, currentSprite.getCurrentAttack() - targetSprite.getCurrentDefense());

		System.out.println(damage + " " +  50 * damage / targetSprite.getMaxHP() + " " +
				getNearbySpriteAmount(stateInfo, false, tileWidth, tileHeight, attackPoint, 2, currentSprite) + " " +
					getNearbySpriteAmount(stateInfo, true, tileWidth, tileHeight, attackPoint, 2, currentSprite));

		if (!targetSprite.isHero())
			return Integer.MIN_VALUE;

		// Check to make sure that if we're using a ranged weapon that has spots that it cannot target in the range that the enemy is not in one of those spaces
		int attackRange = currentSprite.getAttackRange();
		System.out.println("ATTACK RANGE " + attackRange);
		if ((attackRange == EquippableItem.RANGE_BOW_2_NO_1 && distance == 1) ||
				(attackRange == EquippableItem.RANGE_BOW_3_NO_1 && distance == 1) ||
					(attackRange == EquippableItem.RANGE_BOW_3_NO_1_OR_2 && distance == 1) ||
						(attackRange == EquippableItem.RANGE_BOW_3_NO_1_OR_2 && distance == 2))
			return Integer.MIN_VALUE;

		// Determine confidence, add 5 because the attacked sprite will probably always be in range
		int currentConfidence = 5 +
				getNearbySpriteAmount(stateInfo, false, tileWidth, tileHeight, attackPoint, 2, currentSprite) * 5 -
				getNearbySpriteAmount(stateInfo, true, tileWidth, tileHeight, attackPoint, 2, currentSprite) * 5 +
				// Get the percent of damage that will be done to the hero
				Math.min(50, (int)(50.0 * damage / targetSprite.getMaxHP()));

		// If this attack would kill the target then add 50 confidence
		if (targetSprite.getCurrentHP() <= damage)
			currentConfidence += 50;

		return currentConfidence;
	}

	@Override
	protected int getMaxRange(CombatSprite currentSprite) {
		// Check for weapons that have a range, but areas in the range they cannot hit
		int attackRange = currentSprite.getAttackRange();
		if (attackRange == EquippableItem.RANGE_BOW_2_NO_1)
			attackRange = 2;
		else if (attackRange == EquippableItem.RANGE_BOW_3_NO_1 || attackRange == EquippableItem.RANGE_BOW_3_NO_1_OR_2)
			attackRange = 3;
		return attackRange;
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	protected AttackSpriteAction getPerformedTurnAction(CombatSprite target) {
		return new AttackSpriteAction(target,
				new BattleCommand(BattleCommand.COMMAND_ATTACK));
	}
}
