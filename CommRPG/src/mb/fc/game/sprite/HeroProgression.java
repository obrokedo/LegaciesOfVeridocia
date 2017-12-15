package mb.fc.game.sprite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.util.Log;

import mb.fc.engine.CommRPG;
import mb.fc.engine.config.LevelProgressionConfiguration;
import mb.fc.game.battle.LevelUpResult;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.battle.spell.SpellDefinition;
import mb.fc.game.exception.BadResourceException;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.resource.SpellResource;
import mb.fc.loading.FCResourceManager;
import mb.jython.GlobalPythonFactory;

public class HeroProgression implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final int STAT_NONE = 0;
	public static final int STAT_WEAK = 1;
	public static final int STAT_AVERAGE = 2;
	public static final int STAT_STRONG = 3;
	public static final int STAT_VERY_STRONG = 4;

	private Progression unpromotedProgression;
	private Progression promotedProgression;
	private List<Progression> specialProgressions;
	private int heroID;

	public HeroProgression(Progression unpromotedProgression, Progression promotedProgression, List<Progression> specialProgressions,
			int heroID) {
		super();
		this.unpromotedProgression = unpromotedProgression;
		this.promotedProgression = promotedProgression;
		this.specialProgressions = specialProgressions;
		this.heroID = heroID;
	}

	public void promote(CombatSprite cs, Progression progression)
	{
		if (progression == promotedProgression)
			cs.setPromoted(true, 0);
		else 
			cs.setPromoted(true, specialProgressions.indexOf(progression) + 1);
		
		// Remove equipped items because we are going to be setting new stats
		// explicitly and don't want to lose the bonuses from the equipped items
		EquippableItem weapon = cs.getEquippedWeapon();
		EquippableItem ring = cs.getEquippedRing();
		
		if (weapon != null)
			cs.unequipItem(weapon);
		
		if (ring != null)
			cs.unequipItem(ring);
		
		// Explicitly set values for the promoted class
		cs.setMaxMove(cs.getCurrentProgression().getMove());
		cs.setMovementType(cs.getCurrentProgression().getMovementType());
		cs.setNonRandomStats();
		
		// Re-equip items
		if (weapon != null && cs.isEquippable(weapon))
			cs.equipItem(weapon);
		if (ring != null && cs.isEquippable(ring))
			cs.equipItem(ring);
	}

	//TODO This needs to make sure it uses all values from the LevelProgression script
	public LevelUpResult getLevelUpResults(CombatSprite cs, FCResourceManager fcrm)
	{
		Progression p = cs.getCurrentProgression();
		String text = cs.getName() + " has reached level " + (cs.getLevel() + 1) + "!}[";
		LevelUpResult level = new LevelUpResult();

		level.hitpointGain = getStatIncrease(p.getHp(), cs.isPromoted(), cs.getMaxHP(), cs.getLevel() + 1);
		if (level.hitpointGain > 0)
			text += " HP increased by " + level.hitpointGain + ".}[";

		level.magicpointGain = getStatIncrease(p.getMp(), cs.isPromoted(), cs.getMaxMP(), cs.getLevel() + 1);
		if (level.magicpointGain > 0)
			text += " MP increased by " + level.magicpointGain + ".}[";

		level.attackGain = getStatIncrease(p.getAttack(), cs.isPromoted(), cs.getMaxAttack(), cs.getLevel() + 1);
		if (level.attackGain > 0)
			text += " Attack increased by " + level.attackGain + ".}[";

		level.defenseGain = getStatIncrease(p.getDefense(), cs.isPromoted(), cs.getMaxDefense(), cs.getLevel() + 1);
		if (level.defenseGain > 0)
			text += " Defense increased by " + level.defenseGain + ".}[";

		level.speedGain = getStatIncrease(p.getSpeed(), cs.isPromoted(), cs.getMaxSpeed(), cs.getLevel() + 1);
		if (level.speedGain > 0)
			text += " Speed increased by " + level.speedGain + ".}[";

		ArrayList<int[]> spellLevels = p.getSpellLevelLearned();
		ArrayList<String> spellIds = p.getSpellIds();
		for (int i = 0; i < spellLevels.size(); i++)
		{
			for (int j = 0; j < spellLevels.get(i).length; j++)
			{
				if (spellLevels.get(i)[j] == (cs.getLevel() + 1))
				{
					SpellDefinition spell = SpellResource.getSpell(spellIds.get(i), fcrm);
					text += " " + cs.getName() + " learned " + spell.getName() + " " + (j + 1) + "}[";
				}
			}
		}

		level.text = text;

		return level;
	}

	public void levelUp(CombatSprite cs, LevelUpResult level, FCResourceManager frm)
	{
		Log.debug("Applying level-up for " + cs.getName() + " Level: " + (cs.getLevel() + 1));
		
		cs.setLevel(cs.getLevel() + 1);
		cs.setExp(cs.getExp() - 100);

		cs.setCurrentAttack(cs.getCurrentAttack() + level.attackGain);
		cs.setMaxAttack(cs.getMaxAttack() + level.attackGain);

		cs.setCurrentDefense(cs.getCurrentDefense() + level.defenseGain);
		cs.setMaxDefense(cs.getMaxDefense() + level.defenseGain);

		cs.setCurrentSpeed(cs.getCurrentSpeed() + level.speedGain);
		cs.setMaxSpeed(cs.getMaxSpeed() + level.speedGain);

		cs.setMaxHP(cs.getMaxHP() + level.hitpointGain);

		cs.setMaxMP(cs.getMaxMP() + level.magicpointGain);

		// Level up non-displayed stats
		cs.levelUpHiddenStatistics();
		
		ArrayList<int[]> spellLevels = cs.getCurrentProgression().getSpellLevelLearned();
		ArrayList<String> spellIds = cs.getCurrentProgression().getSpellIds();
		for (int i = 0; i < spellLevels.size(); i++)
		{
			for (int j = 0; j < spellLevels.get(i).length; j++)
			{
				if (spellLevels.get(i)[j] == cs.getLevel())
				{
					SpellDefinition spell = SpellResource.getSpell(spellIds.get(i), frm);

					boolean found = false;
					if (cs.getSpellsDescriptors() != null)
					{
						for (KnownSpell sd : cs.getSpellsDescriptors())
						{
							if (sd.getSpellId().equalsIgnoreCase(spell.getId()))
							{
								sd.setMaxLevel((byte) (j + 1));
								found = true;
								break;
							}
						}

						if (!found)
						{
							cs.getSpellsDescriptors().add(new KnownSpell(spell.getId(), (byte) j, SpellResource.getSpell(spell.getId(), frm)));
						}
					}
					else
					{
						cs.setSpells(new ArrayList<KnownSpell>());
						cs.getSpellsDescriptors().add(new KnownSpell(spell.getId(), (byte) j, SpellResource.getSpell(spell.getId(), frm)));
					}
				}
			}
		}
	}

	public static void main(String args[])
	{
		GlobalPythonFactory.intialize();

		int val = 30;
		int max = 0;

		for (int j = 0; j < 10; j++)
		{
			val = 30;
			for (int i = 2; i < 30; i++)
			{
				// System.out.println("New Level ---- " + i);
				float gain = getStatIncrease(new Object[]{"4", 30, 90}, true, val, i);
				val += (int) gain;
			}
		}

		if (val > max)
		{
			max = val;
			System.out.println("MAX " + max);
		}

	}

	private static int getStatIncrease(Object[] stat, boolean isPromoted, int currentStat,
			int newLevel)
	{
		LevelProgressionConfiguration jlp = CommRPG.engineConfiguratior.getLevelProgression();
		float[] values = jlp.getProgressArray((String) stat[0], isPromoted);

		if (values == null)
			throw new BadResourceException("No value was found for the progression type " + ((String) stat[0]) +
					" check LevelProgression.py to make sure an array is returned for the given value");

		float percentDone = 0;
		for (int i = 1; i < newLevel; i++)
			percentDone += values[i];

		percentDone /= 100;

		int amountToGainTotal = (((int) stat[2]) - ((int) stat[1]));
		int amountToGainNowInt = 0;
		
		// If the hero is not supposed to ever gain any of the stat then 
		// the amount they should gain now is always 0
		if (amountToGainTotal != 0)
		{
			int averageValue = (int) ((amountToGainTotal * percentDone) + // The amount that should have been gained at minimum
					((int) stat[1]) + // The base stat
					newLevel / 3); // Assume 33% bonus hp
	
			// If promoted then assume 1.5 bonus for each of the "big" levels
			if (isPromoted)
				averageValue += (int) ((1 + (newLevel - 1) / 6) * 1.5);
	
			// System.out.println("AVG " + averageValue);
	
	
			float amountToGainNow = (values[newLevel - 1] / 100) * amountToGainTotal;
	
	
			if (!isPromoted || (newLevel - 1) % 6 != 0)
			{
				// Add a value 0 - 1
				// amountToGainNow += CommRPG.RANDOM.nextInt(2);
				amountToGainNow += CommRPG.RANDOM.nextFloat() * 1.5;
			}
			// BIG LEVEL!
			else
			{
				// Add a value 0 - 3
				amountToGainNow += CommRPG.RANDOM.nextInt(4);
			}
	
			amountToGainNowInt = Math.round(amountToGainNow);
	
			// Only give pity upgrades if you are unpromoted and under level 11 or
			// if you are promoted and under level 25
			if ((!isPromoted && newLevel <= 10) || (isPromoted && newLevel <= 25))
			{
				// Check for pity upgrades
				if (currentStat + amountToGainNowInt < averageValue)
				{
					amountToGainNowInt++;
					// System.out.println("Pity @ " + newLevel);
				}
			}
		}

		return amountToGainNowInt;
	}

	public Progression getUnpromotedProgression() {
		return unpromotedProgression;
	}

	public Progression getPromotedProgression() {
		return promotedProgression;
	}

	public List<Progression> getSpecialProgressions() {
		return specialProgressions;
	}

	public int getHeroID() {
		return heroID;
	}
}
