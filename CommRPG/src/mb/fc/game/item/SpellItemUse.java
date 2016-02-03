package mb.fc.game.item;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.resource.SpellResource;
import mb.jython.JSpell;

public class SpellItemUse {
	private transient JSpell spell;
	private String spellId;
	private int level;
	private boolean singleUse;

	public SpellItemUse(String spellId, int level, boolean singleUse) {
		super();
		this.spellId = spellId;
		this.level = level;
		this.singleUse = singleUse;
	}

	public JSpell getSpell() {
		return spell;
	}

	public String getSpellId() {
		return spellId;
	}

	public int getLevel() {
		return level;
	}

	public boolean isSingleUse() {
		return singleUse;
	}

	public void initialize(StateInfo stateInfo) {
		spell = SpellResource.getSpell(spellId, stateInfo);
	}
}
