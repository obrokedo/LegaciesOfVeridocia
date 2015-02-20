package mb.fc.game.ai;

import java.awt.Point;
import java.util.ArrayList;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.sprite.CombatSprite;
import mb.jython.JSpell;

public class ClericAI extends CasterAI
{
	public ClericAI(int approachType) {
		super(approachType, true);
	}

	@Override
	protected void handleSpell(JSpell spell, KnownSpell knownSpell, int i, int tileWidth, int tileHeight, CombatSprite currentSprite,
			CombatSprite targetSprite, StateInfo stateInfo, int baseConfidence, int cost, Point attackPoint, int distance)
	{
		// Check to see if this spell does damage, if so then use the damage to determine the confidence
		if (spell.getDamage() != null && spell.getDamage().length >= i && spell.getDamage()[0] < 0)
			handleDamagingSpell(spell, knownSpell, i, tileWidth, tileHeight, currentSprite, targetSprite, stateInfo, baseConfidence, cost, distance);
		else
			handleHealingSpell(spell, knownSpell, i, tileWidth, tileHeight, currentSprite, targetSprite, stateInfo, baseConfidence, cost, attackPoint);
	}

	// TODO Lots to do here, they aren't really smart enough to move and taget them self, kind of need it's own AI for that?
	// Somhow there is never a time when aura can get 2 people in it
	private void handleHealingSpell(JSpell spell, KnownSpell knownSpell, int i, int tileWidth, int tileHeight, CombatSprite currentSprite,
			CombatSprite targetSprite, StateInfo stateInfo, int baseConfidence, int cost, Point attackPoint)
	{
		boolean healSelf = false;
		if (1.0 * currentSprite.getCurrentHP() / currentSprite.getMaxHP() < .65)
			healSelf = true;


		int currentConfidence = 0;
		int area = spell.getArea()[i - 1];
		ArrayList<CombatSprite> targetsInArea;
		if (area > 1)
		{
			boolean healedSelf = false;

			// If there are multiple targets then get the total percent damage done and then divide it by the area amount
			// this will hopefully prevent casters from casting higher level spells then they need to
			Point castPoint = new Point(targetSprite.getTileX(), targetSprite.getTileY());
			targetsInArea = getNearbySprites(stateInfo, (currentSprite.isHero() ? !spell.isTargetsEnemy() : spell.isTargetsEnemy()),
					tileWidth, tileHeight,
					castPoint, spell.getArea()[i - 1] - 1, currentSprite);

			// Check to see if the point that the healer would move to would be in the radius of the spell. Make sure
			// we don't add the hero multiple times though
			if (targetSprite != currentSprite)
			{
				if (Math.abs(castPoint.x - attackPoint.x) + Math.abs(castPoint.y - attackPoint.y) <= spell.getArea()[i - 1] - 1)
				{
					targetsInArea.add(currentSprite);
					healedSelf = true;
				}
			}
			else
				healedSelf = true;

			if (targetsInArea.size() > 1)
				System.out.println("MULTIPLE TARGETS " + targetsInArea.size());

			int incidentalHealed = 0;

			// We only want to consider enemies that will be healed for 75% of the healing (prevents healing for 1)
			// the healing would provide or are at less then 50% health
			for (CombatSprite ts : targetsInArea)
			{
				// Check to see if the character is at less then 50% health or if the spell would use at least 75% of it's healing power
				if (ts.getCurrentHP() * 1.0 / ts.getMaxHP() < .5 || (ts.getMaxHP() - ts.getCurrentHP()) / spell.getDamage()[i - 1] > .75)
				{
					currentConfidence += Math.min(50, (int)(50.0 *
							// Get the percent of the max health that the spell can heal for and the percent of damage that
							// the target is hurt for and the take the smaller of the two numbers. This prevents spells that
							// can technically heal for a higher percent of max health getting a higher value (causing low
							// level heal spells never to be used)
							Math.min(1.0 * (ts.getMaxHP() - ts.getCurrentHP()) / ts.getMaxHP(), 1.0 *spell.getDamage()[i - 1] / ts.getMaxHP())));
				}
				// If this target isn't hurt but will be in the spell area we just add a small amount of confidence,
				// this won't be added unless at least one person in the radius needs healing
				else
					incidentalHealed++;
			}

			// Only add the base confidence if we have found someone to heal
			if (currentConfidence > 0)
			{
				currentConfidence += incidentalHealed * 2;
				// TODO Should this be divided by the area?
				// currentConfidence /= area;
				// If this action will end up healing the cleric as well,
				// then add an additional 20 points
				if (healedSelf && healSelf)
					currentConfidence += 20;
				currentConfidence += baseConfidence;
			}
		}
		else
		{
			if (targetSprite.getCurrentHP() * 1.0 / targetSprite.getMaxHP() < .5 || (targetSprite.getMaxHP() - targetSprite.getCurrentHP()) / spell.getDamage()[i - 1] > .75)
			{
				currentConfidence += Math.min(50, (int)(50.0 *
					// Get the percent of the max health that the spell can heal for and the percent of damage that
					// the target is hurt for and the take the smaller of the two numbers. This prevents spells that
					// can technically heal for a higher percent of max health getting a higher value (causing low
					// level heal spells never to be used)
					Math.min(1.0 * (targetSprite.getMaxHP() - targetSprite.getCurrentHP()) / targetSprite.getMaxHP(),
							1.0 *spell.getDamage()[i - 1] / targetSprite.getMaxHP())));

				if (targetSprite == currentSprite && healSelf)
					currentConfidence += 20;

				// Only add the base confidence if we have found someone to heal
				currentConfidence += baseConfidence;
			}
			else
				currentConfidence = 0;
			targetsInArea = null;
		}

		// Subtract the mp cost of the spell
		currentConfidence -= cost;

		System.out.println("Spell confidence " + currentConfidence + " name " +
		targetSprite.getName() + " " + targetSprite.getUniqueEnemyId() + " spell " + spell.getName() + " level " + i);

		// Check to see if this is the most confident
		mostConfident = checkForMaxConfidence(mostConfident, currentConfidence, spell, knownSpell, i, targetsInArea, false, true);
	}

	private void handleDamagingSpell(JSpell spell, KnownSpell knownSpell, int i, int tileWidth, int tileHeight, CombatSprite currentSprite,
			CombatSprite targetSprite, StateInfo stateInfo, int baseConfidence, int cost, int distance)
	{
		boolean willKill = false;
		int currentConfidence = 0;
		int area = spell.getArea()[i - 1];
		ArrayList<CombatSprite> targetsInArea;
		if (area > 1)
		{
			int killed = 0;

			// If there are multiple targets then get the total percent damage done and then divide it by the area amount
			// this will hopefully prevent wizards from casting higher level spells then they need to
			targetsInArea = getNearbySprites(stateInfo, (currentSprite.isHero() ? !spell.isTargetsEnemy() : spell.isTargetsEnemy()),
					tileWidth, tileHeight,
					new Point(targetSprite.getTileX(), targetSprite.getTileY()), spell.getArea()[i - 1] - 1,
						currentSprite);

			for (CombatSprite ts : targetsInArea)
			{
				if (ts.getCurrentHP() + spell.getDamage()[i - 1] <= 0)
				{
					killed++;
					willKill = true;
				}
				else
				{
					currentConfidence += Math.min(30, (int)(-30.0 * spell.getDamage()[i - 1] / ts.getMaxHP()));
				}

			}

			currentConfidence /= area;

			// Add a confidence equal to the amount killed + 50
			currentConfidence += killed * 50;
		}
		else
		{

			if (targetSprite.getCurrentHP() + spell.getDamage()[i - 1] <= 0)
			{
				currentConfidence += 50;
				willKill = true;
			}
			else
				currentConfidence += Math.min(30, (int)(-30.0 * spell.getDamage()[i - 1] / targetSprite.getMaxHP()));
			targetsInArea = null;
		}

		currentConfidence += baseConfidence;

		// Subtract the mp cost of the spell
		currentConfidence -= cost;

		currentConfidence += distance - 1;

		System.out.println("Spell confidence " + currentConfidence + " name " + targetSprite.getName() + " " + targetSprite.getUniqueEnemyId() + " spell " + spell.getName() + " level " + i);

		// Check to see if this is the most confident
		mostConfident = checkForMaxConfidence(mostConfident, currentConfidence, spell, knownSpell, i, targetsInArea, willKill, false);
	}

	@Override
	protected int determineBaseConfidence(CombatSprite currentSprite,
			CombatSprite targetSprite, int tileWidth, int tileHeight,
			Point attackPoint, StateInfo stateInfo)
	{
		/*
		int damage = 0;
		if (targetSprite.isHero())
			damage = Math.max(1, targetSprite.getCurrentAttack() - currentSprite.getCurrentDefense());
			*/

		// Determine confidence, add 5 because the attacked sprite will probably always be in range
		int currentConfidence = 5 +
				getNearbySpriteAmount(stateInfo, currentSprite.isHero(), tileWidth, tileHeight, attackPoint, 2, currentSprite) * 5 -
				getNearbySpriteAmount(stateInfo, !currentSprite.isHero(), tileWidth, tileHeight, attackPoint, 2, currentSprite) * 5;
				// Adding the attackers damage to this person causes us to flee way to much
		 		// -Math.min(20, (int)(20.0 * damage / currentSprite.getMaxHP()));
		return currentConfidence;
	}
}
