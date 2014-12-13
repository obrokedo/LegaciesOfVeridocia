package mb.fc.game.persist;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.HeroProgression;

public class ClientProfile implements Serializable
{
	private static final long serialVersionUID = 1L;

	private HeroProgression leaderProgression;
	private ArrayList<CombatSprite> heroes;
	private transient ArrayList<CombatSprite> networkHeroes;
	private int gold;
	private String name;
	private transient ArrayList<Integer> startingHeroIds = null;

	public ClientProfile(String name)
	{
		heroes = new ArrayList<>();
		networkHeroes = new ArrayList<>();

		startingHeroIds = new ArrayList<Integer>();
		startingHeroIds.add(0);
		startingHeroIds.add(1);
		startingHeroIds.add(2);

		/*
		leaderProgression = new HeroProgression(new int[][] {{KnownSpell.ID_BLAZE, 2, 6, 16, 27}},
				new Progression(new int[] {EquippableItem.STYLE_SWORD}, new int[] {EquippableItem.STYLE_LIGHT,
						EquippableItem.STYLE_MEDIUM, EquippableItem.STYLE_HEAVY}, 5, HeroProgression.STAT_AVERAGE,
						HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE,
						HeroProgression.STAT_AVERAGE),
				new Progression(new int[] {EquippableItem.STYLE_SWORD}, new int[] {EquippableItem.STYLE_LIGHT,
						EquippableItem.STYLE_MEDIUM, EquippableItem.STYLE_HEAVY}, 5, HeroProgression.STAT_AVERAGE,
						HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE,
						HeroProgression.STAT_AVERAGE));

		ArrayList<KnownSpell> spellDs;
		spellDs = new ArrayList<KnownSpell>();
		spellDs.add(new KnownSpell(KnownSpell.ID_HEAL, (byte) 1));
		spellDs.add(new KnownSpell(KnownSpell.ID_AURA, (byte) 1));


		heroes.add(new CombatSprite(true, "Kiwi1", "kiwi", leaderProgression, 12, 50, 12, 7, 8,
				leaderProgression.getUnpromotedProgression().getMove(), CombatSprite.MOVEMENT_ANIMALS_BEASTMEN, 1, 0, 0, spellDs));
		heroes.get(0).addItem(new Item(0, false));
		heroes.get(0).addItem(new Item(1, true));
		heroes.get(0).addItem(new Item(2, false));
		heroes.get(0).addItem(new Item(2, false));
		heroes.add(new CombatSprite(false, "Kiwi2", "kiwi", leaderProgression, 12, 50, 12, 7, 8,
				leaderProgression.getUnpromotedProgression().getMove(), CombatSprite.MOVEMENT_ANIMALS_BEASTMEN, 1, 0, 1, spellDs));
		heroes.add(new CombatSprite(false, "Kiwi3", "kiwi", leaderProgression, 12, 50, 12, 7, 8,
				leaderProgression.getUnpromotedProgression().getMove(), CombatSprite.MOVEMENT_ANIMALS_BEASTMEN, 1, 0, 2, spellDs));
		heroes.add(new CombatSprite(false, "Kiwi4", "kiwi", leaderProgression, 12, 50, 12, 7, 8,
				leaderProgression.getUnpromotedProgression().getMove(), CombatSprite.MOVEMENT_ANIMALS_BEASTMEN, 1, 0, 0, spellDs));
		*/

		gold = 100;
		this.name = name;

	}

	public ArrayList<Integer> getStartingHeroIds() {
		return startingHeroIds;
	}

	public void setStartingHeroIds(ArrayList<Integer> startingHeroIds) {
		this.startingHeroIds = startingHeroIds;
	}

	public HeroProgression getLeaderProgression() {
		return leaderProgression;
	}

	public void addHero(CombatSprite hero)
	{
		this.heroes.add(hero);
	}

	public ArrayList<CombatSprite> getHeroes() {
		ArrayList<CombatSprite> hs = new ArrayList<>();
		hs.addAll(heroes);
		hs.addAll(networkHeroes);
		Collections.sort(hs, new HeroComparator());
		return hs;
	}

	private class HeroComparator implements Comparator<CombatSprite>
	{
		@Override
		public int compare(CombatSprite c1, CombatSprite c2) {
			return c2.getId() - c1.getId();
		}

	}

	public void addNetworkHeroes(ArrayList<CombatSprite> networkHeroes) {
		this.networkHeroes.addAll(networkHeroes);
	}

	public ArrayList<CombatSprite> getLeaderList()
	{
		ArrayList<CombatSprite> css = new ArrayList<CombatSprite>();
		for (CombatSprite cs : this.getHeroes())
			if (cs.isLeader())
				css.add(cs);
		return css;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public void serializeToFile()
	{
		try
		{
			OutputStream file = new FileOutputStream(name + ".profile");
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(this);
			output.flush();
			file.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static ClientProfile deserializeFromFile(String profile)
	{
	    try
	    {
	      InputStream file = new FileInputStream(profile);
	      InputStream buffer = new BufferedInputStream(file);
	      ObjectInput input = new ObjectInputStream (buffer);

	      ClientProfile cp = (ClientProfile) input.readObject();
	      file.close();
	      return cp;
	    }
	    catch (Exception ex)
	    {
	    	ex.printStackTrace();
	    }

	    return null;
	}

	public String getName() {
		return name;
	}
}
