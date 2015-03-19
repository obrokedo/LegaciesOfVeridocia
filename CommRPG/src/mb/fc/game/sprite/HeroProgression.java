package mb.fc.game.sprite;

import java.io.Serializable;
import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.LevelUpResult;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.resource.SpellResource;
import mb.fc.loading.FCResourceManager;
import mb.jython.GlobalPythonFactory;
import mb.jython.JLevelProgression;
import mb.jython.JSpell;

public class HeroProgression implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final int STAT_NONE = 0;
	public static final int STAT_WEAK = 1;
	public static final int STAT_AVERAGE = 2;
	public static final int STAT_STRONG = 3;
	public static final int STAT_VERY_STRONG = 4;


	private int spellLevels[][];
	private Progression unpromotedProgression;
	private Progression promotedProgression;
	private int heroID;

	private float remainderAtk = 0, remainderDef = 0, remainderSpd = 0, remainderHP = 0, remainderMP = 0;

	public HeroProgression(int[][] spellLevels,
			Progression unpromotedProgression, Progression promotedProgression,
			int heroID) {
		super();
		this.spellLevels = spellLevels;
		this.unpromotedProgression = unpromotedProgression;
		this.promotedProgression = promotedProgression;
		this.heroID = heroID;
	}

	public void promote(CombatSprite cs)
	{
		cs.setPromoted(true);
		cs.setMaxMove(promotedProgression.getMove());
	}

	public LevelUpResult getLevelUpResults(CombatSprite cs, StateInfo stateInfo)
	{
		Progression p = (cs.isPromoted() ? promotedProgression : unpromotedProgression);
		String text = cs.getName() + " has reached level " + (cs.getLevel() + 1) + "!}[";
		LevelUpResult level = new LevelUpResult();

		float increase = getStatIncrease(p.getHp(), cs.isPromoted(), cs.getMaxHP(), cs.getLevel() + 1, remainderHP);
		remainderHP = increase - ((int) increase);
		level.hitpointGain = (int) increase;
		if (((int) increase) > 0)
			text += " HP increased by " + level.hitpointGain + ".}[";

		increase = getStatIncrease(p.getMp(), cs.isPromoted(), cs.getMaxMP(), cs.getLevel() + 1, remainderMP);
		remainderMP = increase - ((int) increase);
		level.magicpointGain = (int) increase;
		if (((int) increase) > 0)
			text += " MP increased by " + level.magicpointGain + ".}[";

		increase = getStatIncrease(p.getAttack(), cs.isPromoted(), cs.getMaxAttack(), cs.getLevel() + 1, remainderAtk);
		remainderAtk = increase - ((int) increase);
		level.attackGain = (int) increase;
		if (((int) increase) > 0)
			text += " Attack increased by " + level.attackGain + ".}[";

		increase = getStatIncrease(p.getDefense(), cs.isPromoted(), cs.getMaxDefense(), cs.getLevel() + 1, remainderDef);
		remainderDef = increase - ((int) increase);
		level.defenseGain = (int) increase;
		if (((int) increase) > 0)
			text += " Defense increased by " + level.defenseGain + ".}[";

		increase = getStatIncrease(p.getSpeed(), cs.isPromoted(), cs.getMaxSpeed(), cs.getLevel() + 1, remainderSpd);
		remainderSpd = increase - ((int) increase);
		level.speedGain = (int) increase;
		if (((int) increase) > 0)
			text += " Speed increased by " + level.speedGain + ".}[";

		for (int i = 0; i < spellLevels.length; i++)
		{
			for (int j = 1; j < spellLevels[i].length; j++)
			{
				if (spellLevels[i][j] == (cs.getLevel() + 1))
				{
					JSpell spell = SpellResource.getSpell(spellLevels[i][0], stateInfo);
					text += " " + cs.getName() + " learned " + spell.getName() + " " + j + "}[";
				}
			}
		}

		level.text = text;

		return level;
	}

	public void levelUp(CombatSprite cs, LevelUpResult level, FCResourceManager frm)
	{
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

		for (int i = 0; i < spellLevels.length; i++)
		{
			for (int j = 1; j < spellLevels[i].length; j++)
			{
				if (spellLevels[i][j] == cs.getLevel())
				{
					JSpell spell = SpellResource.getSpell(spellLevels[i][0], frm);

					boolean found = false;
					if (cs.getSpellsDescriptors() != null)
					{
						for (KnownSpell sd : cs.getSpellsDescriptors())
						{
							if (sd.getSpellId() == spell.getId())
							{
								sd.setMaxLevel((byte) j);
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
		float rem = 0;
		int max = 0;

		for (int j = 0; j < 10; j++)
		{
			val = 30;
			for (int i = 2; i < 30; i++)
			{
				// System.out.println("New Level ---- " + i);
				float gain = getStatIncrease(new int[]{4, 30, 90}, true, val, i, rem);
				val += (int) gain;
				System.out.print("NEW " + val +", ");
				rem = gain - ((int) gain);
			}
			System.out.println();
		}

		if (val > max)
		{
			max = val;
			System.out.println("MAX " + max);
		}

	}

	private static float getStatIncrease(int[] stat, boolean isPromoted, int currentStat,
			int newLevel, float statRemainder)
	{
		if (stat[2] == 0)
			return 0;

		JLevelProgression jlp = GlobalPythonFactory.createLevelProgression();
		float[] values = jlp.getProgressArray(stat[0], isPromoted);

		float percentDone = 0;
		for (int i = 1; i < newLevel; i++)
			percentDone += values[i];

		percentDone /= 100;

		int amountToGainTotal = (stat[2] - stat[1]);

		int averageValue = (int) ((amountToGainTotal * percentDone) + // The amount that should have been gained at minimum
				stat[1] + // The base stat
				newLevel / 3); // Assume 33% bonus hp

		// If promoted then assume 1.5 bonus for each of the "big" levels
		if (isPromoted)
			averageValue += (int) ((1 + (newLevel - 1) / 6) * 1.5);

		// System.out.println("AVG " + averageValue);


		float amountToGainNow = (values[newLevel - 1] / 100) * amountToGainTotal + statRemainder;

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

		// Check for pity upgrades
		if (currentStat + amountToGainNow < averageValue)
		{
			amountToGainNow++;
			// System.out.println("Pity @ " + newLevel);
		}


		return amountToGainNow;
	}



	public int[][] getSpellLevels() {
		return spellLevels;
	}

	public Progression getUnpromotedProgression() {
		return unpromotedProgression;
	}

	public Progression getPromotedProgression() {
		return promotedProgression;
	}

	public int getHeroID() {
		return heroID;
	}
}
