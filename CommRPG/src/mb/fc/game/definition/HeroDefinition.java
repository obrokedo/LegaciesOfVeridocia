package mb.fc.game.definition;

import java.util.ArrayList;

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

	private String className[];
	private int move[];
	private String movementType[];

	private String attackGain[];
	private int attackStart[];
	private int attackEnd[];
	private String defenseGain[];
	private int defenseStart[];
	private int defenseEnd[];
	private String speedGain[];
	private int speedStart[];
	private int speedEnd[];
	private String hpGain[];
	private int hpStart[];
	private int hpEnd[];
	private String mpGain[];
	private int mpStart[];
	private int mpEnd[];

	private int[][] usuableWeapons;

	private ArrayList<int[]> spellsPerLevel;
	private ArrayList<String> spellIds;

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

		hd.name = tagArea.getAttribute("name");
		hd.id = Integer.parseInt(tagArea.getAttribute("id"));
		hd.hp = Integer.parseInt(tagArea.getAttribute("hp"));
		hd.mp = Integer.parseInt(tagArea.getAttribute("mp"));
		hd.attack = Integer.parseInt(tagArea.getAttribute("attack"));
		hd.defense = Integer.parseInt(tagArea.getAttribute("defense"));
		hd.speed = Integer.parseInt(tagArea.getAttribute("speed"));
		hd.promoted = Boolean.parseBoolean(tagArea.getAttribute("promoted"));
		hd.level = Integer.parseInt(tagArea.getAttribute("level"));
		hd.animations = tagArea.getAttribute("animations");

		if (tagArea.getAttribute("leader") != null)
			hd.leader = Boolean.parseBoolean(tagArea.getAttribute("leader"));

		// Set up unpromoted progression
		if(!hd.promoted)
		{
			hd.move = new int[2];
			hd.movementType = new String[2];
			hd.attackStart = new int[2];
			hd.attackEnd = new int[2];
			hd.defenseStart = new int[2];
			hd.defenseEnd = new int[2];
			hd.speedStart = new int[2];
			hd.speedEnd = new int[2];
			hd.hpStart = new int[2];
			hd.hpEnd = new int[2];
			hd.mpStart = new int[2];
			hd.mpEnd = new int[2];
			hd.attackGain = new String[2];
			hd.defenseGain = new String[2];
			hd.speedGain = new String[2];
			hd.hpGain = new String[2];
			hd.mpGain = new String[2];
			hd.usuableWeapons = new int[2][];
			hd.className = new String[2];
		}
		// Otherwise
		else
		{
			hd.move = new int[1];
			hd.movementType = new String[1];
			hd.attackStart = new int[1];
			hd.attackEnd = new int[1];
			hd.defenseStart = new int[1];
			hd.defenseEnd = new int[1];
			hd.speedStart = new int[1];
			hd.speedEnd = new int[1];
			hd.hpStart = new int[1];
			hd.hpEnd = new int[1];
			hd.mpStart = new int[1];
			hd.mpEnd = new int[1];
			hd.attackGain = new String[1];
			hd.defenseGain = new String[1];
			hd.speedGain = new String[1];
			hd.hpGain = new String[1];
			hd.mpGain = new String[1];
			hd.usuableWeapons = new int[1][];
			hd.className = new String[1];
		}

		hd.spellsPerLevel = new ArrayList<int[]>();
		hd.spellIds = new ArrayList<String>();
		hd.items = new ArrayList<Integer>();
		hd.itemsEquipped = new ArrayList<Boolean>();

		for (TagArea childTagArea : tagArea.getChildren())
		{
			if (childTagArea.getTagType().equalsIgnoreCase("spellprogression"))
			{
				String[] splitSpell = childTagArea.getAttribute("gained").split(",");
				int[] splitLevel = new int[splitSpell.length + 1];
				hd.spellIds.add(childTagArea.getAttribute("spellid"));
				for (int i = 0; i < splitSpell.length; i++)
					splitLevel[i] = Integer.parseInt(splitSpell[i]);
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
			else if (childTagArea.getTagType().equalsIgnoreCase("progression"))
			{
				int index = 0;
				if (!hd.promoted)
					index = (Boolean.parseBoolean(childTagArea.getAttribute("promoted")) ? 1 : 0);

				hd.move[index] = Integer.parseInt(childTagArea.getAttribute("move"));
				hd.movementType[index] = childTagArea.getAttribute("movementtype");
				hd.attackGain[index] = childTagArea.getAttribute("attack");
				hd.attackStart[index] = Integer.parseInt(childTagArea.getAttribute("attackstart"));
				hd.attackEnd[index] = Integer.parseInt(childTagArea.getAttribute("attackend"));
				hd.defenseGain[index] = childTagArea.getAttribute("defense");
				hd.defenseStart[index] = Integer.parseInt(childTagArea.getAttribute("defensestart"));
				hd.defenseEnd[index] = Integer.parseInt(childTagArea.getAttribute("defenseend"));
				hd.speedGain[index] = childTagArea.getAttribute("speed");
				hd.speedStart[index] = Integer.parseInt(childTagArea.getAttribute("speedstart"));
				hd.speedEnd[index] = Integer.parseInt(childTagArea.getAttribute("speedend"));
				hd.hpGain[index] = childTagArea.getAttribute("hp");
				hd.hpStart[index] = Integer.parseInt(childTagArea.getAttribute("hpstart"));
				hd.hpEnd[index] = Integer.parseInt(childTagArea.getAttribute("hpend"));
				hd.mpGain[index] = childTagArea.getAttribute("mp");
				hd.mpStart[index] = Integer.parseInt(childTagArea.getAttribute("mpstart"));
				hd.mpEnd[index] = Integer.parseInt(childTagArea.getAttribute("mpend"));
				hd.className[index] = childTagArea.getAttribute("class");

				String[] splitItems = childTagArea.getAttribute("usuableitems").split(",");
				int[] splitIds = new int[splitItems.length];
				for (int i = 0; i < splitItems.length; i++)
					splitIds[i] = Integer.parseInt(splitItems[i]);

				hd.usuableWeapons[index] = splitIds;
			}
		}

		return hd;
	}

	public CombatSprite getHero()
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
			unpromotedProgression = new Progression(usuableWeapons[0], null, move[0], movementType[0],
					new Object[] {attackGain[0], attackStart[0], attackEnd[0]},
					new Object[] {defenseGain[0], defenseStart[0], defenseEnd[0]},
					new Object[] {speedGain[0], speedStart[0], speedEnd[0]},
					new Object[] {hpGain[0], hpStart[0], hpEnd[0]},
					new Object[] {mpGain[0], mpStart[0], mpEnd[0]},
					className[0]);

		// Set up promoted progression
		int index = 1;
		if (promoted)
			index = 0;
		promotedProgression = new Progression(usuableWeapons[index], null, move[index], movementType[index],
				new Object[] {attackGain[index], attackStart[index], attackEnd[index]},
				new Object[] {defenseGain[index], defenseStart[index], defenseEnd[index]},
				new Object[] {speedGain[index], speedStart[index], speedEnd[index]},
				new Object[] {hpGain[index], hpStart[index], hpEnd[index]},
				new Object[] {mpGain[index], mpStart[index], mpEnd[index]},
				className[index]);

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
				knownSpells.add(new KnownSpell(spellIds.get(i), (byte) maxLevel));
		}

		// Create hero progression
		HeroProgression heroProgression = new HeroProgression(spellIds, spellProgression, unpromotedProgression, promotedProgression, id);

		// Create a CombatSprite from default stats, hero progression and spells known
		CombatSprite cs = new CombatSprite(leader, name, animations, heroProgression,
				hp, mp, attack, defense, speed, move[0], movementType[0],

				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, null, null, null, null, // TODO READ THESE VALUES!

				level, 0,
				promoted, knownSpells, id);

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
}