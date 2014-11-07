package mb.fc.game.battle.command;

import java.io.Serializable;

import mb.fc.game.item.Item;
import mb.jython.JSpell;

public class BattleCommand implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final int COMMAND_ATTACK = 0;
	public static final int COMMAND_SPELL = 1;
	public static final int COMMAND_ITEM = 2;
	
	private int command;
	private JSpell spell;
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
	
	public BattleCommand(int command, JSpell spell, int level) {
		super();
		this.command = command;
		this.spell = spell;
		this.level = level;
	}

	public int getCommand() {
		return command;
	}

	public JSpell getSpell() {
		return spell;
	}

	public Item getItem() {
		return item;
	}

	public int getLevel() {
		return level;
	}
}
