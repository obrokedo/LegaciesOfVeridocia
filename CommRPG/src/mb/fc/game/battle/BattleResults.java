package mb.fc.game.battle;

import java.io.Serializable;
import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.constants.TextSpecialCharacters;
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
	private static final int ITEM_CHANCE_TO_BREAK = 15;
	public boolean countered, doubleAttack;
	public ArrayList<Boolean> dodged;
	public ArrayList<Boolean> critted;
	public ArrayList<Integer> hpDamage;
	public ArrayList<Integer> mpDamage;
	public ArrayList<Integer> remainingHP;
	public ArrayList<String> text;
	public ArrayList<CombatSprite> targets;
	public ArrayList<ArrayList<JBattleEffect>> targetEffects;
	public BattleCommand battleCommand;
	public ArrayList<Integer> attackerHPDamage;
	public ArrayList<Integer> attackerMPDamage;
	public boolean death = false;
	public boolean attackerDeath = false;
	public String attackOverText = null;
	public LevelUpResult levelUpResult = null;
	public boolean itemDamaged = false;
	public Item itemUsed = null;

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
		br.targetEffects = new ArrayList<ArrayList<JBattleEffect>>();
		br.attackerHPDamage = new ArrayList<Integer>();
		br.attackerMPDamage = new ArrayList<Integer>();
		br.remainingHP = new ArrayList<>();
		br.countered = false;
		br.dodged = new ArrayList<Boolean>();
		br.critted = new ArrayList<Boolean>();
		br.doubleAttack = false;

		JSpell spell = null;
		ItemUse itemUse = null;
		int spellLevel = 0;
		String preventEffectName = null;

		for (JBattleEffect effect : attacker.getBattleEffects()) {
			if ((battleCommand.getCommand() == BattleCommand.COMMAND_ITEM && effect.preventsItems()) ||
					(battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK && effect.preventsAttack()) ||
					(battleCommand.getCommand() == BattleCommand.COMMAND_SPELL && effect.preventsSpells()))
			{
					battleCommand = new BattleCommand(BattleCommand.COMMAND_TURN_PREVENTED);
					preventEffectName = effect.getBattleEffectId();
					break;
			}
		}

		if (battleCommand.getCommand() == BattleCommand.COMMAND_TURN_PREVENTED) {
			br.text.add(attacker.getName() + " was unable to act due to the " + preventEffectName);
			// Remove all but one target as that's the most that will
			// be seen in the animation
			while (br.targets.size() > 1) {
				br.targets.remove(1);
			}
		}

		// Check to see if we're using an item
		if (battleCommand.getCommand() == BattleCommand.COMMAND_ITEM) {
			br.itemUsed = battleCommand.getItem();

			// If the item has a spell use get the spell
			// and check for single use
			if (br.itemUsed.getSpellUse() != null) {
				spell = br.itemUsed.getSpellUse().getSpell();
				spellLevel = br.itemUsed.getSpellUse().getLevel() - 1;
				battleCommand.setjSpell(spell);
				battleCommand.setLevel(spellLevel + 1);
				// Check to see if the item is single use, if so
				// then remove the item from the attacker
				if (br.itemUsed.getSpellUse().isSingleUse())
					attacker.removeItem(br.itemUsed);
			}
			// We're just using the 'item use', retrieve that and see if
			// this item was single use
			else {
				itemUse = br.itemUsed.getItemUse();

				if (itemUse.isSingleUse())
					attacker.removeItem(br.itemUsed);
			}

			// Check durability
			if (br.itemUsed.useDamagesItem() && CommRPG.RANDOM.nextInt(100) <= ITEM_CHANCE_TO_BREAK) {
				br.itemUsed.damageItem();
				br.itemDamaged = true;
			}

		// Check to see if we're using a spell
		} else if (battleCommand.getCommand() == BattleCommand.COMMAND_SPELL) {
			spell = battleCommand.getSpell();
			spellLevel = battleCommand.getLevel() - 1;
		}

		int expGained = 0;

		int index = 0;
		for (int targetIndex = 0; targetIndex < targets.size(); targetIndex++)
		{
			CombatSprite target = targets.get(targetIndex);

			String text = null;

			// If we are doing a simple attack command then we need to get the dodge chance and calculate damage dealt
			if (battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK)
			{
				int damage = 0;
				int sumDamage = 0;

				// Normal Attack
				text = addAttack(attacker, target, br, stateInfo, jBattleFunctions, false);
				damage = br.hpDamage.get(0);
				sumDamage = damage;
				br.remainingHP.add(target.getCurrentHP() + damage);

				if (attacker.isHero())
				{
					if (damage == 0)
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
								expGained += getExperienceByDamage(damage, target, attacker);
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
							sumDamage += damage;

							if (damage == 0)
								expGained += 1;
							
							// Add the targets remaining HP
							br.remainingHP.add(br.remainingHP.get(0) + damage);
							if (br.remainingHP.get(br.remainingHP.size() - 1) <= 0)
								br.death = true;

							br.doubleAttack = true;
						}
					}
				}
				
				if (attacker.isHero() && sumDamage != 0)
				{
					expGained += getExperienceByDamage(sumDamage, attacker, target);
				}
				else if (br.attackerDeath)
					expGained = 0;
			}
			// Check to see if the battle command indicates a spell is being used
			else if (spell != null)
			{
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

				ArrayList<JBattleEffect> appliedEffects = new ArrayList<>();
				
				// This spell will NOT kill the target so effects should still be applied
				if (target.getCurrentHP() + damage > 0)
				{
					// Check to see if a battle effect should be applied via this spell
					JBattleEffect[] effs = null;
					if ((effs = spell.getEffects(spellLevel)) != null)
					{
						for (JBattleEffect eff : effs)
						{
							if (eff.isEffected(target))
							{
								Log.debug(target.getName() + " was affected by " + eff.getBattleEffectId());
								appliedEffects.add(eff);
							}
						}
					}
				}

				br.targetEffects.add(appliedEffects);

				br.attackerHPDamage.add(0);
				if (index == 0)
					br.attackerMPDamage.add(-1 * spell.getCosts()[spellLevel]);
				else
					br.attackerMPDamage.add(0);

				text = spell.getBattleText(target, damage, br.mpDamage.get(br.mpDamage.size() - 1),
						br.attackerHPDamage.get(br.attackerHPDamage.size() - 1),
						br.attackerMPDamage.get(br.attackerMPDamage.size() - 1));

				// br.targetEffects.get(br.targetEffects.size() - 1)

				// If a battle effect was applied then append that to the battle text
				for (JBattleEffect eff : appliedEffects) {
					String effectText = eff.effectStartedText(attacker, target);
					if (effectText != null)
					{
						if (text.length() > 0)
							text = text + "} " + effectText;
						else
							text = text + " " + effectText;
					}

				}
				int exp = spell.getExpGained(spellLevel, attacker, target);

				if (attacker.isHero())
					expGained += exp;
				br.critted.add(false);
				br.dodged.add(false);
				text = text + TextSpecialCharacters.CHAR_SOFT_STOP;
			}
			else if (itemUse != null)
			{
				text = itemUse.getBattleText(target.getName());

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

				ArrayList<JBattleEffect> appliedEffects = new ArrayList<>();

				JBattleEffect eff = null;
				if ((eff = itemUse.getEffects()) != null && eff.isEffected(target))
				{
					appliedEffects.add(eff);
					Log.debug(target.getName() + " was affected by " + eff.getBattleEffectId());
				}

				br.targetEffects.add(appliedEffects);

				for (JBattleEffect effect : appliedEffects)
				{
					String effectText = eff.effectStartedText(attacker, target);
					if (effectText != null)
						text = text + " " + effectText;
				}

				text += TextSpecialCharacters.CHAR_SOFT_STOP;

				br.attackerHPDamage.add(0);
				br.attackerMPDamage.add(0);

				int exp = itemUse.getExpGained();

				if (attacker.isHero())
					expGained += exp;

				br.critted.add(false);
				br.dodged.add(false);
			}

			if (target.getCurrentHP() + br.hpDamage.get(index) <= 0 ||
					(br.battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK && br.death && !br.attackerDeath))
			{
				br.death = true;
				int idx = text.lastIndexOf(TextSpecialCharacters.CHAR_SOFT_STOP);
				if (idx != -1)
					text = text.substring(0, idx);

				idx = text.lastIndexOf(TextSpecialCharacters.CHAR_HARD_STOP);
				if (idx != -1)
					text = text.substring(0, idx);

				text = text.replaceAll(TextSpecialCharacters.CHAR_SOFT_STOP, "");
				text = text + " " +jBattleFunctions.getCombatantDeathText(attacker, target);
			}
			else if (br.attackerDeath)
			{
				int idx = text.lastIndexOf(TextSpecialCharacters.CHAR_SOFT_STOP);
				if (idx != -1)
					text = text.substring(0, idx);

				idx = text.lastIndexOf(TextSpecialCharacters.CHAR_HARD_STOP);
				if (idx != -1)
					text = text.substring(0, idx);

				text = text.replaceAll(TextSpecialCharacters.CHAR_SOFT_STOP, "");
				text = text + " " + jBattleFunctions.getCombatantDeathText(target, attacker);
			}
			br.text.add(text);
			index++;
		}

		// The maximum exp you can ever get is 49
		expGained = Math.min(49, expGained);

		// In optimize mode no exp should be gained
		if (CommRPG.BATTLE_MODE_OPTIMIZE)
			expGained = 0;
		
		if (attacker.isHero())
		{
			if (!br.attackerDeath)
			{
				attacker.setExp(attacker.getExp() + expGained);
				br.attackOverText = attacker.getName() + " gained " + expGained +  " experience.}";
				// If the hero has leveled up then set the level up results and the correct text
				if (attacker.getExp() >= 100)
				{
					br.attackOverText += " " + TextSpecialCharacters.CHAR_NEXT_CIN + TextSpecialCharacters.CHAR_LINE_BREAK + " ";
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
				br.attackOverText += " " + TextSpecialCharacters.CHAR_NEXT_CIN + TextSpecialCharacters.CHAR_LINE_BREAK + " ";
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
			br.targetEffects.add(new ArrayList<JBattleEffect>());
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

			ArrayList<JBattleEffect> appliedEffects = new ArrayList<>();

			// This spell will NOT kill the target so effects should still be applied
			if (target.getCurrentHP() + damage > 0)
			{
				JBattleEffect eff = null;
				if ((eff = attacker.getAttackEffect()) != null && eff.isEffected(target))
				{
					appliedEffects.add(eff);
					Log.debug(target.getName() + " was affected by " + eff.getBattleEffectId());
					String effectText = eff.effectStartedText(attacker, target);
					if (effectText != null)
						text = text + " " + effectText;
				}
			}

			br.targetEffects.add(appliedEffects);

			br.attackerHPDamage.add(0);
			br.attackerMPDamage.add(0);
		}

		text = text + TextSpecialCharacters.CHAR_SOFT_STOP;

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

	private static int getExperienceByDamage(int damage, CombatSprite attacker, CombatSprite target)
	{
		int attackerLevel = attacker.getLevel();
		if (attacker.isPromoted())
		{
			attackerLevel += 10;
		}
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