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

public class ClientProfile implements Serializable
{
	private static final long serialVersionUID = 1L;

	private ArrayList<CombatSprite> heroes;
	private int gold;
	private String name;
	private transient ArrayList<Integer> devHeroIds = null;
	private transient int develLevel = 0;
	private transient ArrayList<CombatSprite> networkHeroes;

	public void setDevelParams(ArrayList<Integer> heroes, int level)
	{
		this.devHeroIds.addAll(heroes);
		develLevel = level;
	}

	public ClientProfile(String name)
	{
		heroes = new ArrayList<>();
		networkHeroes = new ArrayList<>();
		devHeroIds = new ArrayList<Integer>();
		gold = 100;
		this.name = name;
	}

	public void addHero(CombatSprite hero)
	{
		this.heroes.add(hero);
	}

	public ArrayList<CombatSprite> getHeroes() {
		ArrayList<CombatSprite> hs = new ArrayList<>();
		hs.addAll(heroes);
		// hs.addAll(networkHeroes);
		Collections.sort(hs, new HeroComparator());
		return hs;
	}

	private class HeroComparator implements Comparator<CombatSprite>
	{
		@Override
		public int compare(CombatSprite c1, CombatSprite c2) {
			return  c1.getId() - c2.getId();
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

	public int getDevelLevel() {
		return develLevel;
	}

	public ArrayList<Integer> getDevelHeroIds() {
		return devHeroIds;
	}
}
