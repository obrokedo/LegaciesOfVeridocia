package mb.fc.game.definition;

import java.util.ArrayList;

import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.constants.AttributeStrength;
import mb.fc.game.exception.BadResourceException;
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

	private String[] bodyProgression, mindProgression;

	private int[] maxFireAffin, maxElecAffin,
		maxColdAffin, maxDarkAffin, maxWaterAffin, maxEarthAffin, maxWindAffin,
		maxLightAffin;
	private AttributeStrength[] maxBody, maxMind, maxCounter, maxEvade,
		maxDouble, maxCrit;

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

		try
		{
			hd.name = tagArea.getAttribute("name");
			hd.id = Integer.parseInt(tagArea.getAttribute("id"));
			hd.promoted = Boolean.parseBoolean(tagArea.getAttribute("promoted"));
			hd.level = Integer.parseInt(tagArea.getAttribute("level"));
			hd.animations = tagArea.getAttribute("animations");

			if (tagArea.getAttribute("leader") != null)
				hd.leader = Boolean.parseBoolean(tagArea.getAttribute("leader"));

			int listSize = 1;

			// Set up unpromoted progression
			if(!hd.promoted)
			{
				listSize = 2;
			}

			// Read base stats
			hd.move = new int[listSize];
			hd.movementType = new String[listSize];
			hd.attackStart = new int[listSize];
			hd.attackEnd = new int[listSize];
			hd.defenseStart = new int[listSize];
			hd.defenseEnd = new int[listSize];
			hd.speedStart = new int[listSize];
			hd.speedEnd = new int[listSize];
			hd.hpStart = new int[listSize];
			hd.hpEnd = new int[listSize];
			hd.mpStart = new int[listSize];
			hd.mpEnd = new int[listSize];
			hd.attackGain = new String[listSize];
			hd.defenseGain = new String[listSize];
			hd.speedGain = new String[listSize];
			hd.hpGain = new String[listSize];
			hd.mpGain = new String[listSize];
			hd.usuableWeapons = new int[listSize][];
			hd.className = new String[listSize];

			// Read non-standard stats
			hd.maxFireAffin = new int[listSize];
			hd.maxElecAffin = new int[listSize];
			hd.maxColdAffin = new int[listSize];
			hd.maxDarkAffin = new int[listSize];
			hd.maxWaterAffin = new int[listSize];
			hd.maxEarthAffin = new int[listSize];
			hd.maxWindAffin = new int[listSize];
			hd.maxLightAffin = new int[listSize];

			hd.maxBody = new AttributeStrength[listSize];
			hd.maxMind = new AttributeStrength[listSize];
			hd.maxCounter = new AttributeStrength[listSize];
			hd.maxEvade = new AttributeStrength[listSize];
			hd.maxDouble = new AttributeStrength[listSize];
			hd.maxCrit = new AttributeStrength[listSize];

			hd.spellsPerLevel = new ArrayList<int[]>();
			hd.spellIds = new ArrayList<String>();
			hd.items = new ArrayList<Integer>();
			hd.itemsEquipped = new ArrayList<Boolean>();

			hd.bodyProgression = new String[listSize];
			hd.mindProgression = new String[listSize];

			for (TagArea childTagArea : tagArea.getChildren())
			{
				if (childTagArea.getTagType().equalsIgnoreCase("spellprogression"))
				{
					String[] splitSpell = childTagArea.getAttribute("gained").split(",");
					int[] splitLevel = new int[splitSpell.length];
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

					// Load affinities
					hd.maxFireAffin[index] = Integer.parseInt(childTagArea.getAttribute("fireAffin"));
					hd.maxElecAffin[index] = Integer.parseInt(childTagArea.getAttribute("elecAffin"));
					hd.maxColdAffin[index] = Integer.parseInt(childTagArea.getAttribute("coldAffin"));
					hd.maxDarkAffin[index] = Integer.parseInt(childTagArea.getAttribute("darkAffin"));
					hd.maxWaterAffin[index] = Integer.parseInt(childTagArea.getAttribute("waterAffin"));
					hd.maxEarthAffin[index] = Integer.parseInt(childTagArea.getAttribute("earthAffin"));
					hd.maxWindAffin[index] = Integer.parseInt(childTagArea.getAttribute("windAffin"));
					hd.maxLightAffin[index] = Integer.parseInt(childTagArea.getAttribute("lightAffin"));

					// Load body/mind
					hd.maxBody[index] = AttributeStrength.valueOf(childTagArea.getAttribute("bodyStrength"));
					hd.maxMind[index] = AttributeStrength.valueOf(childTagArea.getAttribute("mindStrength"));

					// Load body/mind progress
					hd.bodyProgression[index] = childTagArea.getAttribute("bodyProgress");
					hd.mindProgression[index] = childTagArea.getAttribute("mindProgress");

					// Load battle stats
					hd.maxCounter[index] = AttributeStrength.valueOf(childTagArea.getAttribute("counterStrength"));
					hd.maxEvade[index] = AttributeStrength.valueOf(childTagArea.getAttribute("evadeStrength"));
					hd.maxDouble[index] = AttributeStrength.valueOf(childTagArea.getAttribute("doubleStrength"));
					hd.maxCrit[index] = AttributeStrength.valueOf(childTagArea.getAttribute("critStrength"));


					String[] splitItems = childTagArea.getAttribute("usuableitems").split(",");
					int[] splitIds = new int[splitItems.length];
					for (int i = 0; i < splitItems.length; i++)
						splitIds[i] = Integer.parseInt(splitItems[i]);

					hd.usuableWeapons[index] = splitIds;
				}
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			throw new BadResourceException("Unable to load hero statistics. Make sure that the Heroes\n"
					+ "is up to date by exporting heroes from the planner");
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
		int index = 0;
		if(!promoted)
		{
			unpromotedProgression = new Progression(usuableWeapons[index], null, move[index], movementType[index],
					new Object[] {attackGain[index], attackStart[index], attackEnd[index]},
					new Object[] {defenseGain[index], defenseStart[index], defenseEnd[index]},
					new Object[] {speedGain[index], speedStart[index], speedEnd[index]},
					new Object[] {hpGain[index], hpStart[index], hpEnd[index]},
					new Object[] {mpGain[index], mpStart[index], mpEnd[index]},
					maxFireAffin[index], maxElecAffin[index], maxColdAffin[index], maxDarkAffin[index],
					maxWaterAffin[index], maxEarthAffin[index], maxWindAffin[index], maxLightAffin[index],
					maxCounter[index], maxEvade[index], maxDouble[index], maxCrit[index], maxBody[index], maxMind[index],
					bodyProgression[index], mindProgression[index],
					className[index]);
			index = 1;
		}

		// Set up promoted progression
		promotedProgression = new Progression(usuableWeapons[index], null, move[index], movementType[index],
				new Object[] {attackGain[index], attackStart[index], attackEnd[index]},
				new Object[] {defenseGain[index], defenseStart[index], defenseEnd[index]},
				new Object[] {speedGain[index], speedStart[index], speedEnd[index]},
				new Object[] {hpGain[index], hpStart[index], hpEnd[index]},
				new Object[] {mpGain[index], mpStart[index], mpEnd[index]},
				maxFireAffin[index], maxElecAffin[index], maxColdAffin[index], maxDarkAffin[index],
				maxWaterAffin[index], maxEarthAffin[index], maxWindAffin[index], maxLightAffin[index],
				maxCounter[index], maxEvade[index], maxDouble[index], maxCrit[index], maxBody[index], maxMind[index],
				bodyProgression[index], mindProgression[index],
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
				level, 0, promoted, knownSpells, id);

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