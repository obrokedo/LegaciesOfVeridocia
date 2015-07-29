package mb.fc.game.ai;

import java.awt.Point;
import java.util.ArrayList;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.Range;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.turnaction.AttackSpriteAction;
import mb.jython.JSpell;

import org.newdawn.slick.util.Log;

public abstract class CasterAI extends AI
{
	protected boolean willKill;
	protected boolean willHeal;
	protected int mostConfident = 0;
	protected JSpell bestSpell;
	protected KnownSpell bestKnownSpell;
	protected int spellLevel;
	protected ArrayList<CombatSprite> targets;

	public CasterAI(int approachType, boolean canHeal) {
		super(approachType, canHeal);
	}

	@Override
	public void initialize()
	{
		mostConfident = Integer.MIN_VALUE;
		bestSpell = null;
		spellLevel = 0;
		targets = null;
		willKill = false;
		willHeal = false;
	}

	@Override
	protected AIConfidence getConfidence(CombatSprite currentSprite,
			CombatSprite targetSprite, int tileWidth, int tileHeight,
			Point attackPoint, int distance, StateInfo stateInfo)
	{
		boolean couldAttackTarget = false;
		int baseConfidence = determineBaseConfidence(currentSprite, targetSprite, tileWidth, tileHeight, attackPoint, stateInfo);
		Log.debug("Base Caster confidence " + baseConfidence + " name " + targetSprite.getName());
		int currentConfidence = baseConfidence;

		Range attackRange = currentSprite.getAttackRange();

		// Check to make sure that if we're using a ranged weapon that has spots that it cannot target in the range that the enemy is not in one of those spaces
		// Get the wizards basic attack confidence, but make sure that the current sprite is in basic attack range
		if (attackRange.isInDistance(distance) && targetSprite.isHero() != currentSprite.isHero())
		{
			couldAttackTarget = true;
			int damage = Math.max(1, currentSprite.getCurrentAttack() - targetSprite.getCurrentDefense());
			currentConfidence += Math.min(30, (int)(30.0 * damage / targetSprite.getMaxHP()));

			// If this attack would kill the target then add 50 confidence
			if (targetSprite.getCurrentHP() <= damage)
			{
				currentConfidence += 50;
				willKill = true;
			}

			Log.debug("Caster Attack confidence " + currentConfidence + " name " + targetSprite.getName());

			mostConfident = checkForMaxConfidence(mostConfident, currentConfidence, null, null, 0, null, willKill, false);
		}


		this.checkSpells(currentSprite, targetSprite, tileWidth, tileHeight, attackPoint, distance, stateInfo, baseConfidence);
		AIConfidence aiC = new AIConfidence(mostConfident);
		aiC.willKill = willKill;
		aiC.willHeal = willHeal;

		// If we're not healing (good effecting) the target and it's the same
		// as you hero/enemy-wise then just return negative
		if (!willHeal && targetSprite.isHero() == currentSprite.isHero())
			return new AIConfidence(Integer.MIN_VALUE);

		// If we're not casting a spell and couldn't reach the target then
		// we can't actually attack this person. In this case just
		// return the minimum value
		if (this.bestSpell == null && !couldAttackTarget)
			return new AIConfidence(Integer.MIN_VALUE);

		return aiC;
	}

	protected void checkSpells(CombatSprite currentSprite,
			CombatSprite targetSprite, int tileWidth, int tileHeight,
			Point attackPoint, int distance, StateInfo stateInfo, int baseConfidence)
	{
		/**********************************************************/
		/* Check each of the spells to see if they should be cast */
		/**********************************************************/
		if (currentSprite.getSpellsDescriptors() != null)
		{
			for (KnownSpell sd : currentSprite.getSpellsDescriptors())
			{
				JSpell spell = sd.getSpell();

				for (int i = 1; i <= sd.getMaxLevel(); i++)
				{
					int cost = spell.getCosts()[i - 1];

					// If we don't have enough MP to cast the spell then don't consider this spell
					if (cost > currentSprite.getCurrentMP())
						continue;

					// Make sure the target is the correct type for this spell
					if ((targetSprite.isHero() == currentSprite.isHero()) == spell.isTargetsEnemy())
						continue;

					// Check to see if the target is in range of this spell
					if (!spell.getRange()[i - 1].isInDistance(distance))
						continue;


					handleSpell(spell, sd, i, tileWidth, tileHeight, currentSprite,
							targetSprite, stateInfo, baseConfidence, cost, attackPoint, distance);
				}
			}
		}
	}

	protected int checkForMaxConfidence(int mostConfident, int confidence, JSpell currentSpell, KnownSpell currentKnownSpell,
			int level, ArrayList<CombatSprite> targets, boolean willKill, boolean willHeal)
	{
		if (confidence > mostConfident)
		{
			bestSpell = currentSpell;
			bestKnownSpell = currentKnownSpell;
			this.spellLevel = level;
			this.targets = targets;
			this.willKill = willKill;
			this.willHeal = willHeal;
			return confidence;
		}
		return mostConfident;
	}

	@Override
	protected int getMaxRange(CombatSprite currentSprite) {
		// Check for weapons that have a range, but areas in the range they cannot hit
		int range = currentSprite.getAttackRange().getMaxRange();
		if (range == EquippableItem.RANGE_BOW_2_NO_1)
			range = 2;
		else if (range == EquippableItem.RANGE_BOW_3_NO_1 || range == EquippableItem.RANGE_BOW_3_NO_1_OR_2)
			range = 3;

		// Get the largest spell range
		if (currentSprite.getSpellsDescriptors() != null)
		{
			for (KnownSpell sd : currentSprite.getSpellsDescriptors())
			{
				JSpell spell = sd.getSpell();

				for (int i = 1; i <= sd.getMaxLevel(); i++)
				{
					range = Math.max(spell.getRange()[i - 1].getMaxRange(), range);
				}
			}
		}

		return range;
	}

	@Override
	protected AttackSpriteAction getPerformedTurnAction(CombatSprite target) {
		if (bestSpell == null)
		{
			Log.debug("Attack is the preferred action");
			return new AttackSpriteAction(target,
				new BattleCommand(BattleCommand.COMMAND_ATTACK));
		}
		else
		{
			Log.debug("Cast spell " + bestSpell.getName() + " " + spellLevel + " is the preferred action");
			if (targets != null)
				return new AttackSpriteAction(targets, new BattleCommand(BattleCommand.COMMAND_SPELL, bestSpell, bestKnownSpell, spellLevel));
			else
				return new AttackSpriteAction(target, new BattleCommand(BattleCommand.COMMAND_SPELL, bestSpell, bestKnownSpell, spellLevel));
		}
	}

	protected abstract void handleSpell(JSpell spell,  KnownSpell knownSpell, int i, int tileWidth, int tileHeight, CombatSprite currentSprite,
			CombatSprite targetSprite, StateInfo stateInfo, int baseConfidence, int cost, Point attackPoint, int distance);

	protected abstract int determineBaseConfidence(CombatSprite currentSprite,
			CombatSprite targetSprite, int tileWidth, int tileHeight,
			Point attackPoint, StateInfo stateInfo);

	@Override
	protected int getLandEffectWeight(int landEffect) {
		return landEffect / 6;
	}


}
