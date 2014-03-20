package mb.fc.game.definition;

import java.util.ArrayList;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.resource.ItemResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.HeroProgression;
import mb.fc.game.sprite.Progression;
import mb.fc.utils.XMLParser.TagArea;

public class HeroDefinition 
{
	private int id;
	private String name;
	private boolean leader;
	private int hp;
	private int mp;
	private int attack;
	private int defense;
	private int speed;
	private boolean promoted;
	private int level;
	private String animations;
	
	private int move[];
	private int movementType[];
	private int attackGain[];
	private int defenseGain[];
	private int speedGain[];
	private int hpGain[];
	private int mpGain[];
	private int portrait[];
	private int[][] usuableWeapons;
	
	private ArrayList<int[]> spellsPerLevel;
	
	private ArrayList<Integer> items;
	private ArrayList<Boolean> itemsEquipped;
	
	private HeroDefinition() {}
	
	public static HeroDefinition parseHeroDefinition(TagArea tagArea)
	{		
		/*
		<hero name="Noah" id=0 leader=true hp=12 mp=0 attack=9 defense=5 speed=4 promoted=false level=1 portrait=0 animations="Noah">
			<progression promoted=false attack=1 defense=1 speed=1 hp=1 mp=1 move=5 movementtype=0 usuableitems=2/>
			<progression promoted=true attack=1 defense=1 speed=1 hp=1 mp=1 move=7 movementtype=0 usuableitems=2/>
			<spellprogression spellid=0 gained=2,6,9/>
			<item itemid=0 equipped=true>
		</hero>
		*/
		
		HeroDefinition hd = new HeroDefinition();
		
		hd.name = tagArea.getParams().get("name");
		hd.id = Integer.parseInt(tagArea.getParams().get("id"));
		hd.hp = Integer.parseInt(tagArea.getParams().get("hp"));
		hd.mp = Integer.parseInt(tagArea.getParams().get("mp"));
		hd.attack = Integer.parseInt(tagArea.getParams().get("attack"));
		hd.defense = Integer.parseInt(tagArea.getParams().get("defense"));
		hd.speed = Integer.parseInt(tagArea.getParams().get("speed"));
		hd.promoted = Boolean.parseBoolean(tagArea.getParams().get("promoted"));
		hd.level = Integer.parseInt(tagArea.getParams().get("level"));		
		hd.animations = tagArea.getParams().get("animations");
		
		if (tagArea.getParams().containsKey("leader"))
			hd.leader = Boolean.parseBoolean(tagArea.getParams().get("leader"));
		
		// Set up unpromoted progression
		if(!hd.promoted)
		{
			hd.move = new int[2];
			hd.movementType = new int[2];
			hd.attackGain = new int[2];
			hd.defenseGain = new int[2];
			hd.speedGain = new int[2];
			hd.hpGain = new int[2];
			hd.mpGain = new int[2];
			hd.usuableWeapons = new int[2][];
			hd.portrait = new int[2];
		}
		// Otherwise 
		else
		{
			hd.move = new int[1];
			hd.movementType = new int[1];
			hd.attackGain = new int[1];
			hd.defenseGain = new int[1];
			hd.speedGain = new int[1];
			hd.hpGain = new int[1];
			hd.mpGain = new int[1];
			hd.usuableWeapons = new int[1][];
			hd.portrait = new int[1];
		}
		
		hd.spellsPerLevel = new ArrayList<int[]>();
		hd.items = new ArrayList<Integer>();
		hd.itemsEquipped = new ArrayList<Boolean>();				
		
		for (TagArea childTagArea : tagArea.getChildren())
		{
			if (childTagArea.getTagType().equalsIgnoreCase("spellprogression"))
			{				
				String[] splitSpell = childTagArea.getParams().get("gained").split(",");
				int[] splitLevel = new int[splitSpell.length + 1];
				splitLevel[0] = Integer.parseInt(childTagArea.getParams().get("spellid"));
				for (int i = 0; i < splitSpell.length; i++)
					splitLevel[i + 1] = Integer.parseInt(splitSpell[i]);
				hd.spellsPerLevel.add(splitLevel);
			}
			else if (childTagArea.getTagType().equalsIgnoreCase("item"))
			{
				hd.items.add(Integer.parseInt(childTagArea.getParams().get("itemid")));
				if (childTagArea.getParams().containsKey("equipped"))
					hd.itemsEquipped.add(Boolean.parseBoolean(childTagArea.getParams().get("equipped")));
				else
					hd.itemsEquipped.add(false);
			}
			else if (childTagArea.getTagType().equalsIgnoreCase("progression"))
			{
				int index = 0;
				if (!hd.promoted)
					index = (Boolean.parseBoolean(childTagArea.getParams().get("promoted")) ? 1 : 0);
				
				hd.move[index] = Integer.parseInt(childTagArea.getParams().get("move"));
				hd.movementType[index] = Integer.parseInt(childTagArea.getParams().get("movementtype"));
				hd.attackGain[index] = Integer.parseInt(childTagArea.getParams().get("attack"));
				hd.defenseGain[index] = Integer.parseInt(childTagArea.getParams().get("defense"));
				hd.speedGain[index] = Integer.parseInt(childTagArea.getParams().get("speed"));
				hd.hpGain[index] = Integer.parseInt(childTagArea.getParams().get("hp"));
				hd.mpGain[index] = Integer.parseInt(childTagArea.getParams().get("mp"));
				hd.portrait[index] = Integer.parseInt(childTagArea.getParams().get("portrait"));
				
				String[] splitItems = childTagArea.getParams().get("usuableitems").split(",");
				int[] splitIds = new int[splitItems.length];
				for (int i = 0; i < splitItems.length; i++)
					splitIds[i] = Integer.parseInt(splitItems[i]);
				
				hd.usuableWeapons[index] = splitIds;								
			}
		}
		
		return hd;
	}
	
	public CombatSprite getHero(StateInfo stateInfo)
	{
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
		*/		
		
		Progression unpromotedProgression = null;
		Progression promotedProgression = null;
		
		// Set up unpromoted progression
		if(!promoted)
			unpromotedProgression = new Progression(usuableWeapons[0], null, move[0], movementType[0], attackGain[0], 
					defenseGain[0], speedGain[0], hpGain[0], mpGain[0], portrait[0]);
	
		// Set up promoted progression
		int index = 1;
		if (promoted)
			index = 0;
		promotedProgression = new Progression(usuableWeapons[index], null, move[index], movementType[index], attackGain[index], 
				defenseGain[index], speedGain[index], hpGain[index], mpGain[index], portrait[index]);
		
		// Set up spell Progression
		int[][] spellProgression = new int[spellsPerLevel.size()][];
		ArrayList<KnownSpell> knownSpells = new ArrayList<KnownSpell>();		
		for (int i = 0; i < spellsPerLevel.size(); i++)
		{
			spellProgression[i] = spellsPerLevel.get(i);
			
			// Check what spells are already known
			boolean known = false;
			int maxLevel = 0;
			for (int j = 1; j < spellsPerLevel.get(i).length; j++)
			{
				if (spellsPerLevel.get(i)[j] <= level)
				{
					maxLevel = j;
					known = true;
				}
			}
			
			if (known)
				knownSpells.add(new KnownSpell(spellsPerLevel.get(i)[0], (byte) maxLevel));
		}
		
		// Get default values for promotion based stats
		Progression currentProgress = null;
		if (unpromotedProgression != null)
			currentProgress = unpromotedProgression;
		else
			currentProgress = promotedProgression;
		
		// Create hero progression
		HeroProgression heroProgression = new HeroProgression(spellProgression, unpromotedProgression, promotedProgression);
						
		// Create a CombatSprite from default stats, hero progression and spells known
		CombatSprite cs = new CombatSprite(leader, name, animations, heroProgression, hp, mp, attack, defense, speed, move[0], movementType[0], level, 0, portrait[0], knownSpells);
		
		// Add items to the combat sprite
		for (int i = 0; i < items.size(); i++)
		{
			Item item = ItemResource.getItem(items.get(i), stateInfo);
			cs.addItem(item);
			if (itemsEquipped.get(i))
				cs.equipItem((EquippableItem) item);
		}
		
		return cs;
	}

	public int getId() {
		return id;
	}
}