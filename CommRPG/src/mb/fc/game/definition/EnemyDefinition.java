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

public class EnemyDefinition 
{
	private int id;
	private String name;
	private boolean leader = false;
	private int hp;
	private int mp;
	private int attack;
	private int defense;
	private int speed;
	private int move;
	private int movementType;
	private int level;
	private int portrait = -1;
	private String animations;
	
	private ArrayList<int[]> spellsPerLevel;
	
	private ArrayList<Integer> items;
	private ArrayList<Boolean> itemsEquipped;
	
	private EnemyDefinition() {}
	
	public static EnemyDefinition parseEnemyDefinition(TagArea tagArea)
	{		
		EnemyDefinition hd = new EnemyDefinition();
		
		hd.name = tagArea.getParams().get("name");
		hd.id = Integer.parseInt(tagArea.getParams().get("id"));
		hd.hp = Integer.parseInt(tagArea.getParams().get("hp"));
		hd.mp = Integer.parseInt(tagArea.getParams().get("mp"));
		hd.attack = Integer.parseInt(tagArea.getParams().get("attack"));
		hd.defense = Integer.parseInt(tagArea.getParams().get("defense"));
		hd.speed = Integer.parseInt(tagArea.getParams().get("speed"));
		hd.level = Integer.parseInt(tagArea.getParams().get("level"));
		hd.move = Integer.parseInt(tagArea.getParams().get("move"));
		hd.movementType = Integer.parseInt(tagArea.getParams().get("movementtype"));
		if (tagArea.getParams().containsKey("portrait"))
			hd.portrait = Integer.parseInt(tagArea.getParams().get("portrait"));
		else
			hd.portrait = -1;
		hd.animations = tagArea.getParams().get("animations");
		
		if (tagArea.getParams().containsKey("leader"))
			hd.leader = Boolean.parseBoolean(tagArea.getParams().get("leader"));		
		
		hd.spellsPerLevel = new ArrayList<int[]>();
		hd.items = new ArrayList<Integer>();
		hd.itemsEquipped = new ArrayList<Boolean>();				
		
		for (TagArea childTagArea : tagArea.getChildren())
		{
			if (childTagArea.getTagType().equalsIgnoreCase("spell"))
			{				
				int[] splitLevel = new int[2];
				splitLevel[0] = Integer.parseInt(childTagArea.getParams().get("spellid"));
				splitLevel[1] = Integer.parseInt(childTagArea.getParams().get("level"));
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
		}
		
		return hd;
	}
	
	public CombatSprite getEnemy(StateInfo stateInfo, int myId)
	{						
		// Set up known spells
		ArrayList<KnownSpell> knownSpells = new ArrayList<KnownSpell>();		
		for (int i = 0; i < spellsPerLevel.size(); i++)
		{
			knownSpells.add(new KnownSpell(spellsPerLevel.get(i)[0], (byte) spellsPerLevel.get(i)[1]));			
		}
						
		// Create a CombatSprite from default stats, hero progression and spells known
		CombatSprite cs = new CombatSprite(leader, name, animations, hp, mp, attack, defense, speed, move, movementType, level, myId, portrait, knownSpells);
		
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
