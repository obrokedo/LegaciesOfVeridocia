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

import mb.fc.game.battle.spell.SpellDescriptor;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.HeroProgression;
import mb.fc.game.sprite.Progression;

public class ClientProfile implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private HeroProgression leaderProgression;
	private ArrayList<CombatSprite> heroes;
	private int gold;
	private String name;
	
	public ClientProfile(String name)
	{
		heroes = new ArrayList<CombatSprite>();
		
		leaderProgression = new HeroProgression(new int[][] {{SpellDescriptor.ID_BLAZE, 2, 6, 16, 27}}, 
				new Progression(new int[] {EquippableItem.STYLE_SWORD}, new int[] {EquippableItem.STYLE_LIGHT, 
						EquippableItem.STYLE_MEDIUM, EquippableItem.STYLE_HEAVY}, 5, HeroProgression.STAT_AVERAGE,
						HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE,
						HeroProgression.STAT_AVERAGE), 
				new Progression(new int[] {EquippableItem.STYLE_SWORD}, new int[] {EquippableItem.STYLE_LIGHT, 
						EquippableItem.STYLE_MEDIUM, EquippableItem.STYLE_HEAVY}, 5, HeroProgression.STAT_AVERAGE,
						HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE, HeroProgression.STAT_AVERAGE,
						HeroProgression.STAT_AVERAGE));

		ArrayList<SpellDescriptor> spellDs;
		spellDs = new ArrayList<SpellDescriptor>();
		spellDs.add(new SpellDescriptor(SpellDescriptor.ID_HEAL, (byte) 1));
		spellDs.add(new SpellDescriptor(SpellDescriptor.ID_AURA, (byte) 1));
		
		
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


		// heroes.add(new CombatSprite(192, 96, true, false, "Chaz", "chaz"));
		gold = 10000;
		this.name = name;
		
	}

	public HeroProgression getLeaderProgression() {
		return leaderProgression;
	}

	public ArrayList<CombatSprite> getHeroes() {
		return heroes;
	}
	
	public ArrayList<CombatSprite> getLeaderList()
	{
		ArrayList<CombatSprite> css = new ArrayList<CombatSprite>();
		for (CombatSprite cs : heroes)
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
	
	public void setClientId(int clientId)
	{
		for (CombatSprite cs : heroes)
			cs.setId(clientId);
	}
}
