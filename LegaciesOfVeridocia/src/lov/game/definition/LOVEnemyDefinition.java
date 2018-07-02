package lov.game.definition;

import java.util.ArrayList;

import lov.game.sprite.LOVCombatSprite;
import tactical.game.battle.spell.KnownSpell;
import tactical.game.definition.EnemyDefinition;
import tactical.game.sprite.CombatSprite;
import tactical.utils.XMLParser.TagArea;

public class LOVEnemyDefinition extends EnemyDefinition {

	private int maxFireAffin, maxElecAffin,
		maxColdAffin, maxDarkAffin, maxWaterAffin, maxEarthAffin, maxWindAffin,
		maxLightAffin, maxBody, maxMind, maxCounter, maxEvade,
		maxDouble, maxCrit;
	
	public LOVEnemyDefinition(TagArea tagArea) {
		super(tagArea);
	}
	
	protected void parseCustomEnemyDefinition(TagArea tagArea) {
		// Load affinities
		maxFireAffin = Integer.parseInt(tagArea.getAttribute("fireAffin"));
		maxElecAffin = Integer.parseInt(tagArea.getAttribute("elecAffin"));
		maxColdAffin = Integer.parseInt(tagArea.getAttribute("coldAffin"));
		maxDarkAffin = Integer.parseInt(tagArea.getAttribute("darkAffin"));
		maxWaterAffin = Integer.parseInt(tagArea.getAttribute("waterAffin"));
		maxEarthAffin = Integer.parseInt(tagArea.getAttribute("earthAffin"));
		maxWindAffin = Integer.parseInt(tagArea.getAttribute("windAffin"));
		maxLightAffin = Integer.parseInt(tagArea.getAttribute("lightAffin"));

		// Load body/mind
		maxBody = Integer.parseInt(tagArea.getAttribute("bodyStrength"));
		maxMind = Integer.parseInt(tagArea.getAttribute("mindStrength"));

		// Load battle stats
		maxCounter = Integer.parseInt(tagArea.getAttribute("counterStrength"));
		maxEvade = Integer.parseInt(tagArea.getAttribute("evadeStrength"));
		maxDouble = Integer.parseInt(tagArea.getAttribute("doubleStrength"));
		maxCrit = Integer.parseInt(tagArea.getAttribute("critStrength"));
	}

	protected CombatSprite createNewCombatSprite(int myId, ArrayList<KnownSpell> knownSpells) {
		return new LOVCombatSprite(leader, name, animations, hp, mp, attack, defense,
				speed, move, movementType, maxFireAffin, maxElecAffin,
				maxColdAffin, maxDarkAffin, maxWaterAffin, maxEarthAffin, maxWindAffin,
				maxLightAffin, maxBody, maxMind, maxCounter, maxEvade,
				maxDouble, maxCrit, level, myId, knownSpells, ENEMY_COUNT--,
				effectId, effectChance, effectLevel);
	}
}
