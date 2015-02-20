package mb.fc.game.sprite;

import java.io.Serializable;

public class Progression implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int[] usuableWeapons;
	private int[] usuableArmor;
	private int move;
	private int[] attack;
	private int[] defense;
	private int[] speed;
	private int[] hp;
	private int[] mp;
	private int movementType;
	private String className;

	public Progression(int[] usuableWeapons, int[] usuableArmor, int move, int movementType,
			int[] attackGains, int[] defenseGains, int[] speedGains, int[] hpGains,
			int[] mpGains, String className) {
		super();
		this.usuableWeapons = usuableWeapons;
		this.usuableArmor = usuableArmor;
		this.move = move;
		this.movementType = movementType;
		this.attack = attackGains;
		this.defense = defenseGains;
		this.speed = speedGains;
		this.hp = hpGains;
		this.mp = mpGains;
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
	public int[] getAttack() {
		return attack;
	}
	public int[] getDefense() {
		return defense;
	}
	public int[] getSpeed() {
		return speed;
	}
	public int[] getHp() {
		return hp;
	}
	public int[] getMp() {
		return mp;
	}
	public int getMovementType() {
		return movementType;
	}
	public String getClassName() {
		return className;
	}
}