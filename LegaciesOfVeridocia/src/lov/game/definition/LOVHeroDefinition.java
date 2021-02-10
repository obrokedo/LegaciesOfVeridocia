package lov.game.definition;

import lov.game.sprite.LOVCombatSprite;
import lov.game.sprite.progression.LOVProgression;
import tactical.game.constants.AttributeStrength;
import tactical.game.definition.HeroDefinition;
import tactical.game.sprite.CombatSprite;
import tactical.game.sprite.HeroProgression;
import tactical.game.sprite.Progression;
import tactical.utils.XMLParser.TagArea;

public class LOVHeroDefinition extends HeroDefinition {
	private String[] bodyProgression, mindProgression;

	private int[] maxFireAffin, maxElecAffin,
		maxColdAffin, maxDarkAffin, maxWaterAffin, maxEarthAffin, maxWindAffin,
		maxLightAffin;
	private AttributeStrength[] maxBody, maxMind, maxCounter, maxEvade,
		maxDouble, maxCrit;
	
	public LOVHeroDefinition(TagArea tagArea) {
		super(tagArea);
	}

	@Override
	protected void parseCustomHeroDefinition(int maxProgressionAmt) {		
		// Read non-standard stats
		maxFireAffin = new int[maxProgressionAmt];
		maxElecAffin = new int[maxProgressionAmt];
		maxColdAffin = new int[maxProgressionAmt];
		maxDarkAffin = new int[maxProgressionAmt];
		maxWaterAffin = new int[maxProgressionAmt];
		maxEarthAffin = new int[maxProgressionAmt];
		maxWindAffin = new int[maxProgressionAmt];
		maxLightAffin = new int[maxProgressionAmt];

		maxBody = new AttributeStrength[maxProgressionAmt];
		maxMind = new AttributeStrength[maxProgressionAmt];
		maxCounter = new AttributeStrength[maxProgressionAmt];
		maxEvade = new AttributeStrength[maxProgressionAmt];
		maxDouble = new AttributeStrength[maxProgressionAmt];
		maxCrit = new AttributeStrength[maxProgressionAmt];

		bodyProgression = new String[maxProgressionAmt];
		mindProgression = new String[maxProgressionAmt];
	}

	

	@Override
	protected void parseCustomHeroProgression(int index, TagArea childTagArea) {
		// Load affinities
		maxFireAffin[index] = Integer.parseInt(childTagArea.getAttribute("fireAffin"));
		maxElecAffin[index] = Integer.parseInt(childTagArea.getAttribute("elecAffin"));
		maxColdAffin[index] = Integer.parseInt(childTagArea.getAttribute("coldAffin"));
		maxDarkAffin[index] = Integer.parseInt(childTagArea.getAttribute("darkAffin"));
		maxWaterAffin[index] = Integer.parseInt(childTagArea.getAttribute("waterAffin"));
		maxEarthAffin[index] = Integer.parseInt(childTagArea.getAttribute("earthAffin"));
		maxWindAffin[index] = Integer.parseInt(childTagArea.getAttribute("windAffin"));
		maxLightAffin[index] = Integer.parseInt(childTagArea.getAttribute("lightAffin"));

		// Load body/mind
		maxBody[index] = AttributeStrength.valueOf(childTagArea.getAttribute("bodyStrength"));
		maxMind[index] = AttributeStrength.valueOf(childTagArea.getAttribute("mindStrength"));

		// Load body/mind progress
		bodyProgression[index] = childTagArea.getAttribute("bodyProgress");
		mindProgression[index] = childTagArea.getAttribute("mindProgress");

		// Load battle stats
		maxCounter[index] = AttributeStrength.valueOf(childTagArea.getAttribute("counterStrength"));
		maxEvade[index] = AttributeStrength.valueOf(childTagArea.getAttribute("evadeStrength"));
		maxDouble[index] = AttributeStrength.valueOf(childTagArea.getAttribute("doubleStrength"));
		maxCrit[index] = AttributeStrength.valueOf(childTagArea.getAttribute("critStrength"));
	}
	
	@Override
	protected Progression getProgression(int index) {
		return new LOVProgression(usuableWeapons[index], null, move[index], movementType[index],
				new Object[] {attackGain[index], attackStart[index], attackEnd[index]},
				new Object[] {defenseGain[index], defenseStart[index], defenseEnd[index]},
				new Object[] {speedGain[index], speedStart[index], speedEnd[index]},
				new Object[] {hpGain[index], hpStart[index], hpEnd[index]},
				new Object[] {mpGain[index], mpStart[index], mpEnd[index]},
				maxFireAffin[index], maxElecAffin[index], maxColdAffin[index], maxDarkAffin[index],
				maxWaterAffin[index], maxEarthAffin[index], maxWindAffin[index], maxLightAffin[index],
				maxCounter[index], maxEvade[index], maxDouble[index], maxCrit[index], maxBody[index], maxMind[index],
				bodyProgression[index], mindProgression[index], spellIds.get(index), spellsPerLevel.get(index),
				specialPromotionItemId[index], className[index], classDescription[index]);
	}
	
	protected CombatSprite createNewCombatSprite(HeroProgression heroProgression) {
		return new LOVCombatSprite(leader, name, animations, heroProgression,
				level, 0, startsPromoted, id);
	}
}
