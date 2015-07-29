package mb.fc.game.battle;

import java.io.Serializable;
import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.item.Item;
import mb.fc.game.item.ItemUse;
import mb.fc.game.sprite.CombatSprite;
import mb.jython.GlobalPythonFactory;
import mb.jython.JBattleEffect;
import mb.jython.JBattleFunctions;
import mb.jython.JSpell;

import org.newdawn.slick.util.Log;

public class BattleResults implements Serializable
{
	private static final long serialVersionUID = 1L;
	public boolean countered, doubleAttack;
	public ArrayList<Boolean> dodged;
	public ArrayList<Boolean> critted;
	public ArrayList<Integer> hpDamage;
	public ArrayList<Integer> mpDamage;
	public ArrayList<Integer> remainingHP;
	public ArrayList<String> text;
	public ArrayList<CombatSprite> targets;
	public ArrayList<JBattleEffect> targetEffects;
	public BattleCommand battleCommand;
	public ArrayList<Integer> attackerHPDamage;
	public ArrayList<Integer> attackerMPDamage;
	public boolean death = false;
	public boolean attackerDeath = false;
	public String attackOverText = null;
	public LevelUpResult levelUpResult = null;

	// TODO Effects

	public static BattleResults determineBattleResults(CombatSprite attacker,
			ArrayList<CombatSprite> targets, BattleCommand battleCommand, StateInfo stateInfo)
	{
        JBattleFunctions jBattleFunctions = GlobalPythonFactory.createJBattleFunctions();

		BattleResults br = new BattleResults();
		br.battleCommand = battleCommand;
		br.targets = targets;
		br.hpDamage = new ArrayList<Integer>();
		br.mpDamage = new ArrayList<Integer>();
		br.text = new ArrayList<String>();
		br.targetEffects = new ArrayList<JBattleEffect>();
		br.attackerHPDamage = new ArrayList<Integer>();
		br.attackerMPDamage = new ArrayList<Integer>();
		br.remainingHP = new ArrayList<>();
		br.countered = false;
		br.dodged = new ArrayList<Boolean>();
		br.critted = new ArrayList<Boolean>();
		br.doubleAttack = false;

		int expGained = 0;

		int index = 0;
		for (CombatSprite target : targets)
		{
			String text = null;

			// If we are doing a simple attack command then we need to get the dodge chance and calculate damage dealt
			if (battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK)
			{
				int damage = 0;

				// Normal Attack
				text = addAttack(attacker, target, br, stateInfo, jBattleFunctions, false);
				damage = br.hpDamage.get(0);
				br.remainingHP.add(target.getCurrentHP() + damage);

				if (attacker.isHero())
				{
					if (damage != 0)
						expGained += getExperienceByDamage(damage, attacker.getLevel(), target);
					else
						expGained += 1;
				}

				// Check to see if the target is dead, if so then there is nothing additional to do
				if (br.remainingHP.get(0) > 0)
				{
					int distanceApart = Math.abs(attacker.getTileX() - target.getTileX()) + Math.abs(attacker.getTileY() - target.getTileY());
					// Counter Attack
					if (distanceApart == 1 && target.getAttackRange().isInDistance(1) &&
							jBattleFunctions.getCounterPercent(attacker, target) >= CommRPG.RANDOM.nextInt(100))
					{
						br.text.add(text);
						text = addAttack(target, attacker, br, stateInfo, jBattleFunctions, true);
						damage = br.hpDamage.get(1);

						// Add the attackers remaining HP
						br.remainingHP.add(attacker.getCurrentHP() + damage);
						if (br.remainingHP.get(br.remainingHP.size() - 1) <= 0)
						{
							br.death = true;
							br.attackerDeath = true;
						}

						if (target.isHero())
						{
							if (damage != 0)
								expGained += getExperienceByDamage(damage, attacker.getLevel(), target);
							else
								expGained += 1;
						}
						else if (br.attackerDeath)
						{
							expGained = 0;
						}

						br.countered = true;
					}

					// Check to make sure the attacker is still alive
					if (br.remainingHP.size() == 1 || br.remainingHP.get(1) > 0)
					{
						// Double Attack
						if (jBattleFunctions.getDoublePercent(attacker, target) >= CommRPG.RANDOM.nextInt(100))
						{
							br.text.add(text);
							text = addAttack(attacker, target, br, stateInfo, jBattleFunctions, false);
							damage = br.hpDamage.get(br.hpDamage.size() - 1);

							// Add the targets remaining HP
							br.remainingHP.add(br.remainingHP.get(0) + damage);
							if (br.remainingHP.get(br.remainingHP.size() - 1) <= 0)
								br.death = true;

							if (attacker.isHero())
							{
								if (damage != 0)
								{
									int level = attacker.getLevel();
									if (attacker.isPromoted())
										level += 10;
									expGained += getExperienceByDamage(damage, level, target);
								}
								else
									expGained += 1;
							}
							else if (br.death)
								expGained = 0;

							br.doubleAttack = true;
						}
					}
				}
			}
			else if (battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
			{
				JSpell spell = battleCommand.getSpell();
				int spellLevel = battleCommand.getLevel() - 1;
				int damage = 0;

				if (spell.getDamage() != null)
				{
					damage = spell.getEffectiveDamage(attacker, target, spellLevel);
					br.hpDamage.add(damage);
					br.remainingHP.add(target.getCurrentHP() + damage);
				}
				else
				{
					br.hpDamage.add(0);
					br.remainingHP.add(target.getCurrentHP());
				}

				if (spell.getMpDamage() != null)
					br.mpDamage.add(spell.getMpDamage()[spellLevel]);
				else
					br.mpDamage.add(0);

				if (spell.getEffect(spellLevel) != null && CommRPG.RANDOM.nextInt(100) <= spell.getEffectChance(spellLevel))
				{
					JBattleEffect eff = spell.getEffect(spellLevel);
					text = text + " " + eff.effectStartedText(attacker, target);
					Log.debug("Battle Results: Spell Text: " + text);
					br.targetEffects.add(eff);
				}
				else
					br.targetEffects.add(null);

				br.attackerHPDamage.add(0);
				if (index == 0)
					br.attackerMPDamage.add(-1 * spell.getCosts()[spellLevel]);
				else
					br.attackerMPDamage.add(0);

				text = spell.getBattleText(target, damage, br.mpDamage.get(br.mpDamage.size() - 1),
						br.attackerHPDamage.get(br.attackerHPDamage.size() - 1),
						br.attackerMPDamage.get(br.attackerMPDamage.size() - 1),
						br.targetEffects.get(br.targetEffects.size() - 1));

				int exp = spell.getExpGained(spellLevel, attacker, target);

				if (attacker.isHero())
					expGained += exp;

				br.critted.add(false);
				br.dodged.add(false);
				text = text + "}";
			}
			else if (battleCommand.getCommand() == BattleCommand.COMMAND_ITEM)
			{
				Item item = battleCommand.getItem();
				ItemUse itemUse = item.getItemUse();

				text = itemUse.getBattleText(target.getName()) + "}";

				int damage = 0;
				if (itemUse.getDamage() != 0)
				{
					damage = itemUse.getDamage();
					br.hpDamage.add(damage);
					br.remainingHP.add(target.getCurrentHP() + damage);
				}
				else
				{
					br.hpDamage.add(0);
					br.remainingHP.add(target.getCurrentHP());
				}

				if (itemUse.getMpDamage() != 0)
					br.mpDamage.add(itemUse.getMpDamage());
				else
					br.mpDamage.add(0);

				if (itemUse.getEffects() != null)
					br.targetEffects.add(itemUse.getEffects());
				else
					br.targetEffects.add(null);

				br.attackerHPDamage.add(0);
				br.attackerMPDamage.add(0);

				int exp = itemUse.getExpGained();

				if (attacker.isHero())
					expGained += exp;

				if (item.getItemUse().isSingleUse())
					attacker.removeItem(item);

				br.critted.add(false);
				br.dodged.add(false);
			}

			if (target.getCurrentHP() + br.hpDamage.get(index) <= 0 ||
					(br.battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK && br.death && !br.attackerDeath))
			{
				br.death = true;
				int idx = text.lastIndexOf("}");
				if (idx != -1)
					text = text.substring(0, idx);

				idx = text.lastIndexOf("]");
				if (idx != -1)
					text = text.substring(0, idx);

				text = text.replaceAll("}", "");
				text = text + " " +jBattleFunctions.getCombatantDeathText(attacker, target);
			}
			else if (br.attackerDeath)
			{
				int idx = text.lastIndexOf("}");
				if (idx != -1)
					text = text.substring(0, idx);

				idx = text.lastIndexOf("]");
				if (idx != -1)
					text = text.substring(0, idx);

				text = text.replaceAll("}", "");
				text = text + " " +jBattleFunctions.getCombatantDeathText(target, attacker);
			}
			br.text.add(text);
			index++;
		}

		// The maximum exp you can ever get is 49
		expGained = Math.min(49, expGained);

		if (attacker.isHero())
		{
			if (!br.attackerDeath)
			{
				attacker.setExp(attacker.getExp() + expGained);
				br.attackOverText = attacker.getName() + " gained " + expGained +  " experience.}";
				if (attacker.getExp() >= 100)
				{
					br.attackOverText += " |[ ";
					br.levelUpResult = attacker.getHeroProgression().getLevelUpResults(attacker, stateInfo);
					br.attackOverText += br.levelUpResult.text;
				}
			}
		}
		else if (expGained != 0)
		{
			targets.get(0).setExp(targets.get(0).getExp() + expGained);
			br.attackOverText = targets.get(0).getName() + " gained " + expGained +  " experience.}";
			if (targets.get(0).getExp() >= 100)
			{
				br.attackOverText += " |[ ";
				br.levelUpResult = targets.get(0).getHeroProgression().getLevelUpResults(targets.get(0), stateInfo);
				br.attackOverText += br.levelUpResult.text;
			}
		}

		return br;
	}

	private static String addAttack(CombatSprite attacker, CombatSprite target, BattleResults br,
			StateInfo stateInfo, JBattleFunctions jBattleFunctions, boolean counter)
	{
		String text;

		// TODO This needs to take into effect other hitting modifiers.
		int dodgeChance = jBattleFunctions.getDodgePercent(attacker, target);


		// TODO Critting, countering
		// TODO A lot to do here, handle spells
		if (CommRPG.RANDOM.nextInt(100) < dodgeChance)
		{
			br.hpDamage.add(0);
			br.mpDamage.add(0);
			if (target.isDodges())
				text = jBattleFunctions.getDodgeText(attacker, target);
			else
				text = jBattleFunctions.getBlockText(attacker, target);
			br.targetEffects.add(null);
			br.attackerHPDamage.add(0);
			br.attackerMPDamage.add(0);
			br.dodged.add(true);
			br.critted.add(false);
		}
		else
		{
			br.dodged.add(false);
			float landEffect = (100 + stateInfo.getResourceManager().getMap().getLandEffectByTile(target.getMovementType(),
					target.getTileX(), target.getTileY())) / 100.0f;

			boolean critted = false;
			if (jBattleFunctions.getCritPercent(attacker, target) >= CommRPG.RANDOM.nextInt(100))
				critted = true;

			br.critted.add(critted);

			// Multiply the attackers attack by .8 - 1.2 and the targets defense by .8 - 1.2 and then the difference
			// between the two values is the damage dealt or 1 if result is less then 1.
			int damage = jBattleFunctions.getDamageDealt(attacker, target, landEffect, CommRPG.RANDOM);

			if (counter)
				damage = Math.min(-1, (int) (damage * jBattleFunctions.getCounterDamageModifier(attacker, target)));

			if (critted)
			{
				int critDamage = Math.min(-1, (int) (damage * jBattleFunctions.getCritDamageModifier(attacker, target)));
				br.hpDamage.add(critDamage);
				text = jBattleFunctions.getCriticalAttackText(attacker, target, critDamage * -1);
			}
			else
			{
				br.hpDamage.add(damage);
				text = jBattleFunctions.getNormalAttackText(attacker, target, damage * -1);
			}

			br.mpDamage.add(0);

			if (attacker.getAttackEffectId() != null && attacker.getAttackEffectChance() >= CommRPG.RANDOM.nextInt(100))
			{
				JBattleEffect eff = GlobalPythonFactory.createJBattleEffect(attacker.getAttackEffectId());
				text = text + " " + eff.effectStartedText(attacker, target);
				br.targetEffects.add(eff);
			}
			else
				br.targetEffects.add(null);

			br.attackerHPDamage.add(0);
			br.attackerMPDamage.add(0);
		}

		text = text + "}";

		return text;
	}

	/*
	public static void main(String args[]) throws UnknownFunctionException, UnparsableExpressionException
	{

		System.out.println("% Dam  10 20 30 40 50 60 70 80 90");
		for (int i = 1; i < 9; i++)
		{
			System.out.print("LVL " + i + ": ");
			for (int j = 1; j < 10; j++)
			{
				System.out.print(getExperienceByDamage(j, i, 10, 10, 3) + " ");
			}
			System.out.println();
		}

	}

	private static String getExperienceByDamage(int damage, int attackerLevel, int targetHP, int targetMaxHP, int targetLevel)
	{
		int maxExp = Math.max(1, Math.min(49, (targetLevel - attackerLevel) * 7 + 35));
		// Check to see if we've killed the target
		if (targetHP + damage <= 0)
		{
			return getString(maxExp);
		}
		// Otherwise give experience based on damage dealt
		else
		{
			// Calculate the percent experience gained, this gives full "kill" experience any time you do 75% damage or more
			// and smaller amounts based on the percent of 75% health that you've done. This number can never exceed 1.
			double percentExperienceGained = Math.min(Math.abs(1.0 * damage / targetMaxHP) / .75, 1);
			return getString((int) Math.max(Math.max(1, 5 + targetLevel - attackerLevel), maxExp * percentExperienceGained));
		}
	}


	private static String getString(int i)
	{
		if (i < 10)
			return "0" + i;
		else
			return "" + i;
	}
	*/

	private static int getExperienceByDamage(int damage, int attackerLevel, CombatSprite target)
	{
		return GlobalPythonFactory.createJBattleFunctions().getExperienceGainedByDamage(damage, attackerLevel, target);
		/*
		int maxExp = Math.max(1, Math.min(49, (target.getLevel() - attackerLevel) * 7 + 35));
		// Check to see if we've killed the target
		if (target.getCurrentHP() + damage <= 0)
			return maxExp;
		// Otherwise give experience based on damage dealt
		else
		{
			// Calculate the percent experience gained, this gives full "kill" experience any time you do 75% damage or more
			// and smaller amounts based on the percent of 75% health that you've done. This number can never exceed 1.
			double percentExperienceGained = Math.min(Math.abs(1.0 * damage / target.getMaxHP()) / .75, 1);
			return (int) Math.max(Math.max(1, 5 + target.getLevel() - attackerLevel), maxExp * percentExperienceGained);
		}
		*/
	}

	public void initialize(StateInfo stateInfo)
	{
		battleCommand.initializeSpell(stateInfo);
	}
}