package mb.fc.game.battle.command;

import java.io.Serializable;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.item.Item;
import mb.jython.JSpell;

public class BattleCommand implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final int COMMAND_ATTACK = 0;
	public static final int COMMAND_SPELL = 1;
	public static final int COMMAND_ITEM = 2;
	public static final int COMMAND_TURN_PREVENTED = 3;

	private int command;
	private KnownSpell spell;
	private transient JSpell jSpell;
	private Item item;
	private int level;

	public BattleCommand(int command) {
		super();
		this.command = command;
	}

	public BattleCommand(int command, Item item) {
		super();
		this.command = command;
		this.item = item;
	}

	public BattleCommand(int command, JSpell jSpell, KnownSpell spell, int level) {
		super();
		this.command = command;
		this.spell = spell;
		this.level = level;
		this.jSpell = jSpell;
	}
	
	public int getCommand() {
		return command;
	}

	public JSpell getSpell() {
		return jSpell;
	}

	public void setjSpell(JSpell jSpell) {
		this.jSpell = jSpell;
	}

	public void initializeSpell(StateInfo stateInfo) {
		if (spell != null)
		{
			spell.initializeFromLoad(stateInfo);
			jSpell = spell.getSpell();
		}
	}

	public Item getItem() {
		return item;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
