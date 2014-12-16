package mb.fc.game.sprite;

import java.io.Serializable;

public class Progression implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int[] usuableWeapons;
	private int[] usuableArmor;
	private int move;
	private int attackGains;
	private int defenseGains;
	private int speedGains;
	private int hpGains;
	private int mpGains;
	private int movementType;
	private int portraitIndex;
	private String className;

	public Progression(int[] usuableWeapons, int[] usuableArmor, int move, int movementType,
			int attackGains, int defenseGains, int speedGains, int hpGains,
			int mpGains, int portraitIndex, String className) {
		super();
		this.usuableWeapons = usuableWeapons;
		this.usuableArmor = usuableArmor;
		this.move = move;
		this.movementType = movementType;
		this.attackGains = attackGains;
		this.defenseGains = defenseGains;
		this.speedGains = speedGains;
		this.hpGains = hpGains;
		this.mpGains = mpGains;
		this.portraitIndex = portraitIndex;
		this.className = className;
	}

	public int[] getUsuableWeapons() {
		return usuableWeapons;
	}
	public int[] getUsuableArmor() {
		return usuableArmor;
	}
	public int getMove() {
		return move;
	}
	public int getAttackGains() {
		return attackGains;
	}
	public int getDefenseGains() {
		return defenseGains;
	}
	public int getSpeedGains() {
		return speedGains;
	}
	public int getHpGains() {
		return hpGains;
	}
	public int getMpGains() {
		return mpGains;
	}
	public int getMovementType() {
		return movementType;
	}
	public int getPortraitIndex() {
		return portraitIndex;
	}
	public String getClassName() {
		return className;
	}
}