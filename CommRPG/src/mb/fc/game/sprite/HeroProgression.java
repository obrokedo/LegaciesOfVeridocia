package mb.fc.game.sprite;

import java.io.Serializable;
import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.LevelUpResult;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.resource.SpellResource;
import mb.fc.loading.FCResourceManager;
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

	public HeroProgression(int[][] spellLevels,
			Progression unpromotedProgression, Progression promotedProgression) {
		super();
		this.spellLevels = spellLevels;
		this.unpromotedProgression = unpromotedProgression;
		this.promotedProgression = promotedProgression;
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

		int increase = getStatIncrease(p.getHpGains());
		level.hitpointGain = increase;
		if (increase > 0)
			text += " HP increased by " + increase + ".}[";

		increase = getStatIncrease(p.getMpGains());
		level.magicpointGain = increase;
		if (increase > 0)
			text += " MP increased by " + increase + ".}[";

		increase = getStatIncrease(p.getAttackGains());
		level.attackGain = increase;
		if (increase > 0)
			text += " Attack increased by " + increase + ".}[";

		increase = getStatIncrease(p.getDefenseGains());
		level.defenseGain = increase;
		if (increase > 0)
			text += " Defense increased by " + increase + ".}[";

		increase = getStatIncrease(p.getSpeedGains());
		level.speedGain = increase;
		if (increase > 0)
			text += " Speed increased by " + increase + ".}[";

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

	private int getStatIncrease(int type)
	{
		switch (type)
		{
			case STAT_WEAK:
				return CommRPG.RANDOM.nextInt(2);
			case STAT_AVERAGE:
				return CommRPG.RANDOM.nextInt(3);
			case STAT_STRONG:
				return CommRPG.RANDOM.nextInt(3) + 1;
			case STAT_VERY_STRONG:
				return CommRPG.RANDOM.nextInt(4) + 1;
			case STAT_NONE:
				return 0;
		}

		return 0;
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
}
