package mb.fc.game.trigger;

import java.util.ArrayList;
import java.util.List;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.sprite.CombatSprite;

public class TriggerCondition {
	private int triggerId;
	private String description;
	private List<Condition> conditions = new ArrayList<>();
	
	public TriggerCondition(int triggerId, String description) {
		super();
		this.triggerId = triggerId;
		this.description = description;
	}

	public void addCondition(Condition c)
	{
		this.conditions.add(c);
	}
	
	public void executeCondtions(StateInfo stateInfo)
	{
		for (Condition c : conditions)
			if (!c.conditionIsMet(stateInfo))
				return;
		stateInfo.getResourceManager().getTriggerEventById(triggerId).perform(stateInfo);
	}
	
	public static class EnemyDeath implements Condition
	{
		private int unitId;
		
		public EnemyDeath(int unitId) {
			super();
			this.unitId = unitId;
		}

		// Check to see if a enemy with a unit id equal to the 
		// specified unit id exists anymore, if not then this condition
		// is met
		@Override
		public boolean conditionIsMet(StateInfo stateInfo) {
			for (CombatSprite cs : stateInfo.getCombatSprites())
			{
				if (cs.getUniqueEnemyId() == unitId)
					return false;
			}
			return true;
		}
	}
	
	private interface Condition
	{
		public boolean conditionIsMet(StateInfo stateInfo);
	}
}