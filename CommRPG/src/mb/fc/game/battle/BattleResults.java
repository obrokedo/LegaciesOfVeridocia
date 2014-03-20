package mb.fc.game.battle;

import java.io.Serializable;
import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.battle.condition.BattleEffect;
import mb.fc.game.battle.spell.Spell;
import mb.fc.game.item.Item;
import mb.fc.game.item.ItemUse;
import mb.fc.game.sprite.CombatSprite;

public class BattleResults implements Serializable
{
	private static final long serialVersionUID = 1L;
	public boolean dodged, countered, critted, doubleAttack;
	public ArrayList<Integer> hpDamage;
	public ArrayList<Integer> mpDamage;
	public ArrayList<String> text;	
	public ArrayList<CombatSprite> targets;
	public ArrayList<BattleEffect> targetEffects;
	public BattleCommand battleCommand;
	public ArrayList<Integer> attackerHPDamage;
	public ArrayList<Integer> attackerMPDamage;
	public boolean death = false;
	public String attackOverText = null;
	public LevelUpResult levelUpResult = null;
	
	// TODO Effects
	
	public static BattleResults determineBattleResults(CombatSprite attacker, 
			ArrayList<CombatSprite> targets, BattleCommand battleCommand, StateInfo stateInfo)
	{
		BattleResults br = new BattleResults();
		br.battleCommand = battleCommand;
		br.targets = targets;
		br.hpDamage = new ArrayList<Integer>();
		br.mpDamage = new ArrayList<Integer>();
		br.text = new ArrayList<String>();
		br.targetEffects = new ArrayList<BattleEffect>();
		br.attackerHPDamage = new ArrayList<Integer>();
		br.attackerMPDamage = new ArrayList<Integer>();
		br.dodged = false;
		br.countered = false;
		br.critted = false;
		br.doubleAttack = false;
		
		int expGained = 0;
		
		int index = 0;
		for (CombatSprite target : targets)
		{
			String text = null;
			
			// If we are doing a simple attack command then we need to get the dodge chance and calculate damage dealt
			if (battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK)
			{				
				// TODO This needs to take into effect other hitting modifiers.
				int dodgeChance = Math.max(5, 5 + (target.getCurrentSpeed() - attacker.getCurrentSpeed()) / 5);
				
				
				// TODO Critting, countering
				// TODO A lot to do here, handle spells
				if (CommRPG.RANDOM.nextInt(100) < dodgeChance)
				{
					br.hpDamage.add(0);
					br.mpDamage.add(0);
					if (target.isDodges())
						text = target.getName() + " quickly dodged the attack!";
					else
						text = target.getName() + " blocked the attack!";
					br.targetEffects.add(null);
					br.attackerHPDamage.add(0);
					br.attackerMPDamage.add(0);
					br.dodged = true;
					expGained = 1;
				}
				else
				{					
					float landEffect = (100 + stateInfo.getResourceManager().getMap().getLandEffectByTile(target.getMovementType(), 
							target.getTileX(), target.getTileY())) / 100.0f;
					
					int critChance = 3;
					if (critChance >= CommRPG.RANDOM.nextInt(100))
						br.critted = true;
					
					System.out.println("Land effect " + landEffect);
					
					// Multiply the attackers attack by .8 - 1.2 and the targets defense by .8 - 1.2 and then the difference
					// between the two values is the damage dealt or 1 if result is less then 1.
					int damage = -1 * Math.max(1, (int)(((
							// A random number between .8 - 1.2
							CommRPG.RANDOM.nextInt(40) + 80) / 100.0 *
							// Attack
							attacker.getCurrentAttack()) - 
							// A random number between .8 - 1.2
							((CommRPG.RANDOM.nextInt(40) + 80) / 100.0 * 
									// Defense modified by land effect
									+ landEffect * target.getCurrentDefense())));
					
					if (br.critted)
					{
						br.hpDamage.add((int) (damage * 1.25));					
						text = attacker.getName() + " inflicted a vicious blow dealing " + (damage * -1) + " damage.";
					}
					else
					{
						br.hpDamage.add(damage);					
						text = attacker.getName() + " dealt " + (damage * -1) + " damage.";
					}
					br.mpDamage.add(0);
					br.targetEffects.add(null);
					br.attackerHPDamage.add(0);
					br.attackerMPDamage.add(0);
					expGained += getExperienceByDamage(damage, attacker.getLevel(), target);
				}								
			}
			else if (battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
			{
				Spell spell = battleCommand.getSpell();
				int spellLevel = battleCommand.getLevel() - 1;
				int damage = 0;
				
				text = spell.getBattleText(target.getName(), spellLevel);
				
				if (spell.getDamage() != null)
				{
					damage = spell.getDamage()[spellLevel];
					br.hpDamage.add(damage);
				}
				else 
					br.hpDamage.add(0);
				
				if (spell.getMpDamage() != null)
					br.mpDamage.add(spell.getMpDamage()[spellLevel]);
				else
					br.mpDamage.add(0);				
				
				if (spell.getEffects() != null)
					br.targetEffects.add(spell.getEffects()[spellLevel]);
				else
					br.targetEffects.add(null);
				
				br.attackerHPDamage.add(0);
				if (index == 0)
					br.attackerMPDamage.add(-1 * spell.getCosts()[spellLevel]);
				else
					br.attackerMPDamage.add(0);
				
				int exp = spell.getExpGained(spellLevel, attacker, target);
				
				if (exp == Spell.DEFAULT_EXP)
					expGained += getExperienceByDamage(damage, attacker.getLevel(), target);
				else
					expGained += exp;
			}
			else if (battleCommand.getCommand() == BattleCommand.COMMAND_ITEM)
			{
				Item item = battleCommand.getItem();
				ItemUse itemUse = item.getItemUse();
				
				text = itemUse.getBattleText(target.getName());
				
				int damage = 0;
				if (itemUse.getDamage() != 0)
				{
					damage = itemUse.getDamage();
					br.hpDamage.add(damage);
				}
				else 
					br.hpDamage.add(0);
				
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
				
				expGained += exp;
				
				if (item.getItemUse().isSingleUse())
					attacker.removeItem(item);
			}
			
			if (target.getCurrentHP() + br.hpDamage.get(index) <= 0)
			{
				br.death = true;
				text = text + " " + target.getName() + " has been defeated...";
			}
			br.text.add(text);
			index++;
		}
		
		// The maximum exp you can ever get is 49
		expGained = Math.min(49, expGained);
		
		if (attacker.isHero())
		{
			attacker.setExp(attacker.getExp() + expGained);
			br.attackOverText = attacker.getName() + " gained " + expGained +  " experience. ";
			if (attacker.getExp() >= 100)
			{
				br.levelUpResult = attacker.getHeroProgression().getLevelUpResults(attacker, stateInfo);
				br.attackOverText += br.levelUpResult.text;
			}
		}
		
		return br;	
	}
	
	public static void main(String args[])
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
	
	private static int getExperienceByDamage(int damage, int attackerLevel, CombatSprite target)
	{
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
	}
}