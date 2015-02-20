package mb.fc.game.ai;

import java.awt.Point;
import java.util.ArrayList;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.sprite.CombatSprite;
import mb.jython.JSpell;

public class WizardAI extends CasterAI
{
	public WizardAI(int approachType) {
		super(approachType, false);
	}

	@Override
	protected void handleSpell(JSpell spell,  KnownSpell knownSpell, int i, int tileWidth, int tileHeight, CombatSprite currentSprite,
			CombatSprite targetSprite, StateInfo stateInfo, int baseConfidence, int cost, Point attackPoint, int distance)
	{
		// Check to see if this spell does damage, if so then use the damage to determine the confidence
		if (spell.getDamage() != null && spell.getDamage().length >= i && spell.getDamage()[0] < 0)
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
						// TODO WHY ARE WE USING THEIR MAX HEALTH HERE? PERCENTAGE OF CURRENT HEALTH IS SUFFICIENT WITH
						// A MAX PERCENT OF -1. OTHERWISE WE WILL ALMOST ALWAYS USE HIGHER LEVEL SPELLS
						// ALSO, WHY DO WE HAVE CURRENT CONFIDENCE BE MAXED TO -50? IT SHOULD BE MINNED TO 0
						currentConfidence += Math.min(50, (int)(-50.0 * spell.getDamage()[i - 1] / ts.getMaxHP()));
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
					currentConfidence += Math.min(50, (int)(-50.0 * spell.getDamage()[i - 1] / targetSprite.getMaxHP()));
				targetsInArea = null;
			}

			currentConfidence += baseConfidence;
			currentConfidence += distance - 1;

			// Subtract the mp cost of the spell
			currentConfidence -= cost;

			System.out.println("Spell confidence " + currentConfidence + " name " + targetSprite.getName() + " spell " + spell.getName() + " level " + i);

			// Check to see if this is the most confident
			mostConfident = checkForMaxConfidence(mostConfident, currentConfidence, spell, knownSpell, i, targetsInArea, willKill, false);
		}
	}

	@Override
	protected int determineBaseConfidence(CombatSprite currentSprite,
			CombatSprite targetSprite, int tileWidth, int tileHeight,
			Point attackPoint, StateInfo stateInfo)
	{
		// Adding the targets counter attack causes us to flee to much
		// int damage = Math.max(1, targetSprite.getCurrentAttack() - currentSprite.getCurrentDefense());

		// Determine confidence, add 5 because the attacked sprite will probably always be in range
		int currentConfidence = 5 +
				getNearbySpriteAmount(stateInfo, currentSprite.isHero(), tileWidth, tileHeight, attackPoint, 2, currentSprite) * 5 -
				getNearbySpriteAmount(stateInfo, !currentSprite.isHero(), tileWidth, tileHeight, attackPoint, 2, currentSprite) * 5;
				// - Math.min(20, (int)(20.0 * damage / currentSprite.getMaxHP()));
		return currentConfidence;
	}
}
