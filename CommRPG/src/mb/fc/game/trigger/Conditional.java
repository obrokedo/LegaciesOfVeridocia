package mb.fc.game.trigger;

import mb.fc.engine.state.StateInfo;

public interface Conditional {
	//TODO Realllllly not happy with this, but the alternative would be to put the MovingSprite in the state info and
	// check that way.
	public boolean conditionIsMet(String locationEntered, boolean immediate, StateInfo stateInfo);
}