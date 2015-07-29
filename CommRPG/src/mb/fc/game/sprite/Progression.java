package mb.fc.game.sprite;

import java.io.Serializable;

public class Progression implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int[] usuableWeapons;
	private int[] usuableArmor;
	private int move;
	private Object[] attack;
	private Object[] defense;
	private Object[] speed;
	private Object[] hp;
	private Object[] mp;
	private String movementType;
	private String className;

	public Progression(int[] usuableWeapons, int[] usuableArmor, int move, String movementType,
			Object[] attackGains, Object[] defenseGains, Object[] speedGains, Object[] hpGains,
			Object[] mpGains, String className) {
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
	public Object[] getAttack() {
		return attack;
	}
	public Object[] getDefense() {
		return defense;
	}
	public Object[] getSpeed() {
		return speed;
	}
	public Object[] getHp() {
		return hp;
	}
	public Object[] getMp() {
		return mp;
	}
	public String getMovementType() {
		return movementType;
	}
	public String getClassName() {
		return className;
	}
}