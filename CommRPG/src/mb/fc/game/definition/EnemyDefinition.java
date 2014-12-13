package mb.fc.game.definition;

import java.util.ArrayList;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.resource.ItemResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.utils.XMLParser.TagArea;

public class EnemyDefinition
{
	private static int ENEMY_COUNT = -1;

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

		hd.name = tagArea.getAttribute("name");
		hd.id = Integer.parseInt(tagArea.getAttribute("id"));
		hd.hp = Integer.parseInt(tagArea.getAttribute("hp"));
		hd.mp = Integer.parseInt(tagArea.getAttribute("mp"));
		hd.attack = Integer.parseInt(tagArea.getAttribute("attack"));
		hd.defense = Integer.parseInt(tagArea.getAttribute("defense"));
		hd.speed = Integer.parseInt(tagArea.getAttribute("speed"));
		hd.level = Integer.parseInt(tagArea.getAttribute("level"));
		hd.move = Integer.parseInt(tagArea.getAttribute("move"));
		hd.movementType = Integer.parseInt(tagArea.getAttribute("movementtype"));
		if (tagArea.getAttribute("portrait") != null)
			hd.portrait = Integer.parseInt(tagArea.getAttribute("portrait"));
		else
			hd.portrait = -1;
		hd.animations = tagArea.getAttribute("animations");

		if (tagArea.getAttribute("leader") != null)
			hd.leader = Boolean.parseBoolean(tagArea.getAttribute("leader"));

		hd.spellsPerLevel = new ArrayList<int[]>();
		hd.items = new ArrayList<Integer>();
		hd.itemsEquipped = new ArrayList<Boolean>();

		for (TagArea childTagArea : tagArea.getChildren())
		{
			if (childTagArea.getTagType().equalsIgnoreCase("spell"))
			{
				int[] splitLevel = new int[2];
				splitLevel[0] = Integer.parseInt(childTagArea.getAttribute("spellid"));
				splitLevel[1] = Integer.parseInt(childTagArea.getAttribute("level"));
				hd.spellsPerLevel.add(splitLevel);
			}
			else if (childTagArea.getTagType().equalsIgnoreCase("item"))
			{
				hd.items.add(Integer.parseInt(childTagArea.getAttribute("itemid")));
				if (childTagArea.getAttribute("equipped") != null)
					hd.itemsEquipped.add(Boolean.parseBoolean(childTagArea.getAttribute("equipped")));
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
		CombatSprite cs = new CombatSprite(leader, name, animations, hp, mp, attack, defense,
				speed, move, movementType, level, myId, portrait, knownSpells, ENEMY_COUNT--);

		// Add items to the combat sprite
		for (int i = 0; i < items.size(); i++)
		{
			Item item = ItemResource.getUninitializedItem(items.get(i));
			cs.addItem(item);
			if (itemsEquipped.get(i))
				cs.equipItem((EquippableItem) item);
		}

		return cs;
	}

	public int getId() {
		return id;
	}

	public String getAnimation() {
		return animations;
	}

	public static void resetEnemyIds()
	{
		ENEMY_COUNT = -1;
	}
}
