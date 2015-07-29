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
	private String movementType;
	private int level;
	private String animations;
	private String effectId = null;
	private int effectChance = -1;

	private ArrayList<Integer> spellsPerLevel;
	private ArrayList<String> spellIds;

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
		hd.movementType = tagArea.getAttribute("movementtype");
		hd.animations = tagArea.getAttribute("animations");

		if (tagArea.getAttribute("leader") != null)
			hd.leader = Boolean.parseBoolean(tagArea.getAttribute("leader"));

		hd.spellsPerLevel = new ArrayList<Integer>();
		hd.spellIds = new ArrayList<String>();
		hd.items = new ArrayList<Integer>();
		hd.itemsEquipped = new ArrayList<Boolean>();
		hd.effectId = null;

		for (TagArea childTagArea : tagArea.getChildren())
		{
			if (childTagArea.getTagType().equalsIgnoreCase("spell"))
			{
				hd.spellIds.add(childTagArea.getAttribute("spellid"));
				hd.spellsPerLevel.add(Integer.parseInt(childTagArea.getAttribute("level")));
			}
			else if (childTagArea.getTagType().equalsIgnoreCase("item"))
			{
				hd.items.add(Integer.parseInt(childTagArea.getAttribute("itemid")));
				if (childTagArea.getAttribute("equipped") != null)
					hd.itemsEquipped.add(Boolean.parseBoolean(childTagArea.getAttribute("equipped")));
				else
					hd.itemsEquipped.add(false);
			}
			else if (childTagArea.getTagType().equalsIgnoreCase("attackeffect"))
			{
				hd.effectId = childTagArea.getAttribute("effectid");
				hd.effectChance = Integer.parseInt(childTagArea.getAttribute("effectchance"));
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
			knownSpells.add(new KnownSpell(spellIds.get(i), (byte) spellsPerLevel.get(i).intValue()));
		}

		// Create a CombatSprite from default stats, hero progression and spells known
		CombatSprite cs = new CombatSprite(leader, name, animations, hp, mp, attack, defense,
				speed, move, movementType,

				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, null, null, null, null, // TODO READ THESE VALUES!

				level, myId, knownSpells, ENEMY_COUNT--,
				effectId, effectChance);

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
