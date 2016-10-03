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

import org.newdawn.slick.util.Log;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.LevelUpResult;
import mb.fc.game.dev.DevParams;
import mb.fc.game.exception.BadResourceException;
import mb.fc.game.resource.HeroResource;
import mb.fc.game.sprite.CombatSprite;
import mb.jython.GlobalPythonFactory;

public class ClientProfile implements Serializable
{
	private static final long serialVersionUID = 1L;

	private ArrayList<CombatSprite> heroes;
	private int gold;
	private String name;
	private transient ArrayList<CombatSprite> networkHeroes;
	private transient DevParams devParams;

	public void setDevParams(DevParams devParams) {
		this.devParams = devParams;
	}

	public ClientProfile(String name)
	{
		heroes = new ArrayList<>();
		networkHeroes = new ArrayList<>();
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
		if (networkHeroes != null)
			hs.addAll(networkHeroes);
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
	
	public void initialize(StateInfo stateInfo)
	{
		// Add starting heroes if they haven't been added yet
		if (getHeroes().size() == 0)
		{
			// Add the heroes specified in the configuration values,
			// these are the heroes that the force will initially contain
			for (String heroName : GlobalPythonFactory.createConfigurationValues().getStartingHeroIds())
				addHero(HeroResource.getHero(heroName));
			
			applyDevParams(stateInfo);
		}
	}
	
	private void applyDevParams(StateInfo stateInfo)
	{
		if (devParams == null)
			return;
		

		// Add any heroes specified in the development params
		if (devParams.getHeroesToAdd() != null)
			for (Integer heroId : devParams.getHeroesToAdd())
			{
				Log.debug("DevParams adding hero with id: " + heroId + " to the party");
				addHero(HeroResource.getHero(heroId));
			}
		
		if (devParams.getHeroNamesToAdd() != null)
			for (String heroName : devParams.getHeroNamesToAdd())
			{
				Log.debug("DevParams adding hero with name: " + heroName + " to the party");
				addHero(HeroResource.getHero(heroName));
			}

		if (getHeroes().size() == 0)
			throw new BadResourceException("No starting heroes have been specified. Update the ConfigurationValues "
					+ "script to indicate the ids of the heroes that should start in the party.");

		if (devParams.getLevel() > 1)
		{
			Log.debug("DevParams setting hero level to: " + devParams.getLevel());
			
			for (CombatSprite cs : getHeroes())
			{
				while (cs.getLevel() < devParams.getLevel())
				{
					LevelUpResult lur = cs.getHeroProgression().getLevelUpResults(cs, stateInfo);
					cs.setExp(100);
					cs.getHeroProgression().levelUp(cs, lur, stateInfo.getResourceManager());
				}
			}
		}
	}

	public String getName() {
		return name;
	}
}
