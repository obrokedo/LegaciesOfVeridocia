package mb.fc.game.trigger;

import java.util.ArrayList;
import java.util.List;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.map.MapObject;

public class TriggerCondition {
	private int triggerId;
	private String description;
	private List<Conditional> conditions = new ArrayList<>();
	
	public TriggerCondition(int triggerId, String description) {
		super();
		this.triggerId = triggerId;
		this.description = description;
	}

	public void addCondition(Conditional c)
	{
		this.conditions.add(c);
	}
	
	public void executeCondtions(String locationEntered, boolean immediate, StateInfo stateInfo)
	{
		for (Conditional c : conditions)
			if (!c.conditionIsMet(locationEntered, immediate, stateInfo))
				return;
		stateInfo.getResourceManager().getTriggerEventById(triggerId).perform(stateInfo, immediate);
	}
	
	public static class UnitDeath implements Conditional
	{
		private int unitId;
		private boolean enemy = false;
		
		public UnitDeath(int unitId, boolean enemy) {
			super();
			this.unitId = unitId;
			this.enemy = enemy;
		}

		// Check to see if a unit with a unit id equal to the 
		// specified unit id exists anymore, if not then this condition
		// is met
		@Override
		public boolean conditionIsMet(String locationEntered, boolean immediate, StateInfo stateInfo) {
			for (CombatSprite cs : stateInfo.getCombatSprites())
			{
				if (enemy)
				{
					if (cs.getUniqueEnemyId() == unitId)
						return false;
				}
				else
				{
					if (cs.getId() == unitId)
						return false;
				}
			}
			return true;
		}
	}
	
	public static class HeroEntersLocation implements Conditional
	{
		private String location;
		private boolean immediate;
		
		public HeroEntersLocation(String location, boolean immediate) {
			super();
			this.location = location;
			this.immediate = immediate;
		}

		@Override
		public boolean conditionIsMet(String locationEntered, boolean immediate, StateInfo stateInfo) {
			if (locationEntered != null && this.immediate == immediate && location.equalsIgnoreCase(locationEntered))
			{
				return true;
			}
			
			return false;
		}
		
	}
	
	public static class LocationContainsUnits implements Conditional
	{
		private String location;
		private boolean enemy;
		private int amount;
		private String operator;
		
		public LocationContainsUnits(String location, boolean enemy, int amount, String operator) {
			super();
			this.location = location;
			this.enemy = enemy;
			this.amount = amount;
			this.operator = operator;
		}

		@Override
		public boolean conditionIsMet(String locationEntered, boolean immediate, StateInfo stateInfo) {
			int count = 0;
			for (MapObject mo : stateInfo.getCurrentMap().getMapObjects())
			{
				if (mo.getName() != null && mo.getName().equalsIgnoreCase(location))
				{
					for (CombatSprite cs : stateInfo.getCombatSprites())
					{
						if (cs.isHero() && !enemy && mo.contains(cs))
						{
							count++;
						}
					}
					
					if (operator.equalsIgnoreCase("Greater Than"))
					{
						return count > amount;
							
					}
					else if (operator.equalsIgnoreCase("Less Than"))
					{
						return count < amount;
					}
					else if (operator.equalsIgnoreCase("Equals"))
					{
						return count == amount;
					}
					
				}
			}
			return false;
		}
		
	}
	
	public static class HeroInBattle implements Conditional
	{
		private int id;
		
		public HeroInBattle(int id) {
			super();
			this.id = id;
		}

		@Override
		public boolean conditionIsMet(String locationEntered, boolean immediate, StateInfo stateInfo) {
			return stateInfo.getCombatantById(id) != null;
		}
		
	}
}