package lov.game.sprite.progression;

import java.util.ArrayList;

import tactical.game.constants.AttributeStrength;
import tactical.game.sprite.Progression;

public class LOVProgression extends Progression {
	// Battle Action stats
	private AttributeStrength 	counterStrength, evadeStrength,
								doubleStrength, critStrength,
								bodyStrength, mindStrength;

	// Elemental Affinities
	private int 				fireAffin, elecAffin, coldAffin,
								darkAffin, waterAffin, earthAffin,
								windAffin, lightAffin;
	private String bodyProgression, mindProgression;
	
	

	public LOVProgression(int[] usuableWeapons, int[] usuableArmor, int move, String movementType,
			Object[] attackGains, Object[] defenseGains, Object[] speedGains, Object[] hpGains,
			Object[] mpGains, int fireAffin, int elecAffin,
			int coldAffin, int darkAffin, int waterAffin, int earthAffin,
			int windAffin, int lightAffin, AttributeStrength counterStrength, AttributeStrength evadeStrength,
			AttributeStrength doubleStrength, AttributeStrength critStrength, AttributeStrength bodyStrength,
			AttributeStrength mindStrength, String bodyProgression, String mindProgression, ArrayList<String> spellIds,
			 ArrayList<int[]> spellLevelLearned, int specialPromotionItemId, String className) {
		super(usuableWeapons, usuableArmor, move, movementType, attackGains, defenseGains, speedGains, hpGains, mpGains,
				spellIds, spellLevelLearned, specialPromotionItemId, className);
		this.counterStrength = counterStrength;
		this.evadeStrength = evadeStrength;
		this.doubleStrength = doubleStrength;
		this.critStrength = critStrength;
		this.bodyStrength = bodyStrength;
		this.mindStrength = mindStrength;
		this.fireAffin = fireAffin;
		this.elecAffin = elecAffin;
		this.coldAffin = coldAffin;
		this.darkAffin = darkAffin;
		this.waterAffin = waterAffin;
		this.earthAffin = earthAffin;
		this.windAffin = windAffin;
		this.lightAffin = lightAffin;
		this.bodyProgression = bodyProgression;
		this.mindProgression = mindProgression;
	}

	public AttributeStrength getCounterStrength() {
		return counterStrength;
	}

	public AttributeStrength getEvadeStrength() {
		return evadeStrength;
	}

	public AttributeStrength getDoubleStrength() {
		return doubleStrength;
	}

	public AttributeStrength getCritStrength() {
		return critStrength;
	}

	public AttributeStrength getBodyStrength() {
		return bodyStrength;
	}

	public AttributeStrength getMindStrength() {
		return mindStrength;
	}

	public int getFireAffin() {
		return fireAffin;
	}

	public int getElecAffin() {
		return elecAffin;
	}

	public int getColdAffin() {
		return coldAffin;
	}

	public int getDarkAffin() {
		return darkAffin;
	}

	public int getWaterAffin() {
		return waterAffin;
	}

	public int getEarthAffin() {
		return earthAffin;
	}

	public int getWindAffin() {
		return windAffin;
	}

	public int getLightAffin() {
		return lightAffin;
	}

	public String getBodyProgression() {
		return bodyProgression;
	}
	public String getMindProgression() {
		return mindProgression;
	}
}
