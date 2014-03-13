package mb.fc.game.ai;

import java.awt.Point;
import java.util.ArrayList;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.move.MoveableSpace;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.turnaction.AttackSpriteAction;
import mb.fc.game.turnaction.TargetSpriteAction;
import mb.fc.game.turnaction.TurnAction;
import mb.fc.game.turnaction.WaitAction;

public abstract class AI 
{
	// BESERKER
	// CASTER
	// HEALER
	// DEFENDER
	// ATTACKER
	// REACTIVE
	// Everyone but Beserker uses team dynamics, maybe just a general reactive class that also
	// doesn't listen, but doesn't actively charge
	public final static int APPROACH_REACTIVE = 0;
	public final static int APPROACH_KAMIKAZEE = 1;
	public final static int APPROACH_HESITANT = 2;
	public final static int APPROACH_FOLLOW = 3;
	public final static int APPROACH_MOVE_TO_POINT = 4;
	
	private int approachType;
	private boolean canHeal;
	private Point targetPoint;
	private CombatSprite targetCS;

	public AI(int approachType, boolean canHeal) {
		super();
		this.approachType = approachType;
		this.canHeal = canHeal;
	}
	
	public ArrayList<TurnAction> performAI(StateInfo stateInfo, MoveableSpace ms, CombatSprite currentSprite)
	{
		return performTheAI(stateInfo, ms, currentSprite);				
	}
	
	/**
	 * 
	 * @param stateInfo The StateInfo for the current game state
	 * @param ms
	 * @param currentSprite
	 * @return
	 */
	private ArrayList<TurnAction> performTheAI(StateInfo stateInfo, MoveableSpace ms, CombatSprite currentSprite)
	{				
		this.initialize();
		ArrayList<TurnAction> turnActions = new ArrayList<TurnAction>();
		turnActions.add(new WaitAction());
		
		boolean attacking = false;
		
		ArrayList<AttackableEntity> attackableSprites = this.getAttackableSprites(stateInfo, currentSprite, ms, getMaxRange(currentSprite));
		AttackableEntity target = null;
		Point targetPoint = null;
		int maxConfidence = 0;
		int tileWidth = ms.getTileWidth();
		int tileHeight = ms.getTileHeight();
		
		System.out.println("------ " + currentSprite.getName());
		
		boolean foundHero = false;		
		
		// People that can heal should have an oppurtunity to heal themselves
		if (canHeal)
		{
			attackableSprites.add(new AttackableEntity(currentSprite, getBestPoint(stateInfo, tileWidth, tileHeight, currentSprite, ms, true, 2), 0));
		}
		
		// Attempt to move to an area that has the least amount of enemies, most amount of allies next to it
		// and the most amount of damage. Want to balance this with the resources used
		if (attackableSprites.size() > 0)
		{
			for (AttackableEntity as : attackableSprites)
			{		
				if (as.getCombatSprite().isHero())
				{				
					foundHero = true;
				}
					
				for (int i = 0; i < as.getAttackablePoints().size(); i++)
				{
					Point attackPoint = as.getAttackablePoints().get(i);
					int distance = as.getDistances().get(i);
					int currentConfidence = getConfidence(currentSprite, as.getCombatSprite(), tileWidth, tileHeight, 
							attackPoint, distance, stateInfo);										
					
					System.out.println("Target " + as.getCombatSprite().getName() + " Confidence: " + currentConfidence);
					
					if (currentConfidence > maxConfidence)
					{
						targetPoint = attackPoint;
						target = as;
						maxConfidence = currentConfidence;
					}
				}										
			}
			
			// If we have no confidence in what we're going to do then we want to move away from the enemies
			// to try and split them up.
			if (foundHero && maxConfidence == 0)
			{
				targetPoint = this.getBestPoint(stateInfo, tileWidth, tileHeight, currentSprite, ms, true, 2);
				ms.addMoveActionsToLocation(targetPoint.x, targetPoint.y, currentSprite, turnActions);
			}
		}
		
		// If no enemy was found and our max confidence is 0 then we just want to approach
		if (!foundHero && maxConfidence == 0)
		{
			switch (approachType)
			{
				case AI.APPROACH_REACTIVE:
					performReactiveApproach(stateInfo, tileWidth, tileHeight, currentSprite, ms, turnActions);
					break;
				case AI.APPROACH_KAMIKAZEE:
					performKamikazeeApproach(stateInfo, tileWidth, tileHeight, currentSprite, ms, turnActions);
					break;
				case AI.APPROACH_HESITANT:
					performHesitantApproach(stateInfo, tileWidth, tileHeight, currentSprite, ms, turnActions);
					break;
				case AI.APPROACH_FOLLOW:
					performFollowApproach(stateInfo, tileWidth, tileHeight, currentSprite, ms, turnActions);
					break;
				case AI.APPROACH_MOVE_TO_POINT:
					performMoveToApproach(stateInfo, tileWidth, tileHeight, currentSprite, ms, turnActions);
					break;
			}
		}
		
		if (target != null)
		{
			ms.addMoveActionsToLocation(targetPoint.x, targetPoint.y, currentSprite, turnActions);
			turnActions.add(new WaitAction());
			AttackSpriteAction attackAction = getPerformedTurnAction(target.getCombatSprite());
			turnActions.add(new TargetSpriteAction(attackAction.getBattleCommand(), target.getCombatSprite()));
			turnActions.add(new WaitAction());
			turnActions.add(attackAction);
			attacking = true;
		}	
		else
		{
			turnActions.add(new TurnAction(TurnAction.ACTION_HIDE_MOVE_AREA));
			turnActions.add(new WaitAction());
		}
		
		if (!attacking)
			turnActions.add(new TurnAction(TurnAction.ACTION_END_TURN));
		
		return turnActions;
	}	
	
	private void performKamikazeeApproach(StateInfo stateInfo, int tileWidth, int tileHeight, CombatSprite currentSprite, MoveableSpace ms, ArrayList<TurnAction> turnActions)
	{
		Point targetPoint = this.getBestPoint(stateInfo, tileWidth, tileHeight, currentSprite, ms, false, currentSprite.getCurrentMove());			
		ms.addMoveActionsToLocation(targetPoint.x, targetPoint.y, currentSprite, turnActions);
	}
	
	private void performHesitantApproach(StateInfo stateInfo, int tileWidth, int tileHeight, CombatSprite currentSprite, MoveableSpace ms, ArrayList<TurnAction> turnActions)
	{
		Point targetPoint = this.getBestPoint(stateInfo, tileWidth, tileHeight, currentSprite, ms, false, 2);			
		ms.addMoveActionsToLocation(targetPoint.x, targetPoint.y, currentSprite, turnActions);
	}
	
	private void performReactiveApproach(StateInfo stateInfo, int tileWidth, int tileHeight, CombatSprite currentSprite, MoveableSpace ms, ArrayList<TurnAction> turnActions)
	{
		
	}
	
	private void performFollowApproach(StateInfo stateInfo, int tileWidth, int tileHeight, CombatSprite currentSprite, MoveableSpace ms, ArrayList<TurnAction> turnActions)
	{
		ms.addMoveActionsAlongPath(targetCS.getLocX(), targetCS.getLocY(), currentSprite, turnActions);
	}
	
	private void performMoveToApproach(StateInfo stateInfo, int tileWidth, int tileHeight, CombatSprite currentSprite, MoveableSpace ms, ArrayList<TurnAction> turnActions)
	{
		ms.addMoveActionsAlongPath(targetPoint.x, targetPoint.y, currentSprite, turnActions);
	}
	
	/**
	 * Gets the safest point to move to in a moveable area. If retreat is true,
	 * then this point will be as close to the enemies (this entities allies) and furthest from
	 * all heroes (this entities enemies). If retreat = false then this point will be closest
	 * to the heroes (this entities enemies) and closest to all enemies (this entities allies).
	 * 
	 * @param stateInfo The StateInfo for the current game state
	 * @param tileWidth The width of a tile on the map
	 * @param tileHeight The height of a tile on the map
	 * @param attacker The entity for which AI is being performed for. (This is just used to omit the distance of this entity from the selected space)
	 * @param ms The space that is entity is able to move in
	 * @param retreat A boolean indicating whether the point should be as far from heroes (this entities enemies) if true,
	 * 			or whether the point should be as close to the heroes (this entities enemies) if false
	 * @return the safest point to move to in a moveable area.
	 */
	private Point getBestPoint(StateInfo stateInfo, int tileWidth, int tileHeight, 
			CombatSprite attacker, MoveableSpace ms, boolean retreat, int searchRange)
	{
		Point bestPoint = null;
		int maxDistance = Integer.MIN_VALUE;
		
		if (!retreat)
			maxDistance = Integer.MAX_VALUE;
		
		int tx = attacker.getTileX();
		int ty = attacker.getTileY();
		
		for (int i = -searchRange; i <= searchRange; i++)
		{
			for (int j = -searchRange; j <= searchRange; j++)
			{
				if ((Math.abs(i) + Math.abs(j)) <= searchRange &&  ms.canEndMoveHere(tx + i, ty + j))
				{
					int heroDistance = getDistanceFromSprites(stateInfo, true, tileWidth, tileHeight, tx + i, ty + j, attacker);
					int enemyDistance = getDistanceFromSprites(stateInfo, false, tileWidth, tileHeight, tx + i, ty + j, attacker);
					
					int distance = 0;
					if (retreat)
					{
						// Try and maximize this distance
						distance = heroDistance - enemyDistance;
						if (distance > maxDistance)
						{
							maxDistance = distance;
							bestPoint = new Point(tx + i, ty + j);
						}
					}
					else
					{
						// Try and minimize this distance
						distance = heroDistance + (int)(enemyDistance * .1);
						if (distance < maxDistance)
						{
							maxDistance = distance;
							bestPoint = new Point(tx + i, ty + j);
						}
					}
				}
			}
		}
		
		System.out.println(attacker.getName() + " Distance " + maxDistance);
		
		return bestPoint;
	}
	
	/**
	 * Gets a number representing the total distance ALL heroes/enemies are from a given location.
	 * If "isHero" is true then this number represents the distance this space is from all heroes,
	 * otherwise this number represents the distance this space is from all enemies. This can be used
	 * to determine which spaces are furthest/closest to heroes/enemies.
	 * 
	 * @param stateInfo
	 * @param isHero If true, the distance returned will represent the distance from all heroes, if false it will be for all heroes
	 * @param tileWidth The width of a tile on the map
	 * @param tileHeight The height of a tile on the map
	 * @param x The x index of the tile to be checked (Not the location that the tile is drawn)
	 * @param y The y index of the tile to be checked (Not the location that the tile is drawn)
	 * @param attacker The entity for which AI is being performed for. (This is just used to omit the distance of this entity from the selected space)
	 * @return The total distance of all heroes/enemies from the specified space. 
	 */
	private int getDistanceFromSprites(StateInfo stateInfo, boolean isHero, 
			int tileWidth, int tileHeight, int x, int y, CombatSprite attacker)
	{
		int distance = 0;
		
		for (CombatSprite target : stateInfo.getCombatSprites())
		{
			if (target == attacker)
				continue;
			
			if (target.isHero() == isHero)
			{
				int tx = target.getTileX();
				int ty = target.getTileY();
				
				distance += Math.abs(x - tx) + Math.abs(y - ty);
			}
		}
		
		return distance;
			
	}
	
	/**
	 * 
	 * 
	 * @param stateInfo
	 * @param isHero
	 * @param tileWidth
	 * @param tileHeight
	 * @param point
	 * @param range
	 * @param attacker 
	 * @return
	 */
	protected int getNearbySpriteAmount(StateInfo stateInfo, boolean isHero, 
			int tileWidth, int tileHeight, Point point, int range, CombatSprite attacker)
	{
		int count = 0;
		
		for (CombatSprite target : stateInfo.getCombatSprites())
		{
			if (target == attacker)
				continue;
			
			if (target.isHero() == isHero)
			{
				int tx = target.getTileX();
				int ty = target.getTileY();
				
				if (Math.abs(point.x - tx) + Math.abs(point.y - ty) <= range)				
					count++;
			}
		}
		
		return count;
	}	
	
	protected ArrayList<CombatSprite> getNearbySprites(StateInfo stateInfo, boolean isHero, 
			int tileWidth, int tileHeight, Point point, int range, CombatSprite attacker)
	{
		ArrayList<CombatSprite> css = new ArrayList<CombatSprite>();
		
		for (CombatSprite target : stateInfo.getCombatSprites())
		{
			if (target == attacker)
				continue;
			
			if (target.isHero() == isHero)
			{
				int tx = target.getTileX();
				int ty = target.getTileY();
				
				if (Math.abs(point.x - tx) + Math.abs(point.y - ty) <= range)				
					css.add((CombatSprite) target);
			}
		}
		
		return css;
	}

	/**
	 * Gets a list of all sprites that are in a targetable range from at least one point in the attackers current moveable-space
	 * 
	 * @param stateInfo The StateInfo for the current game state
	 * @param attacker The combat sprite that AI is being performed for
	 * @param moveableSpace The moveable space of the attacker combat sprite
	 * @param maxAttackRange The maximum range that should be searched for targetable sprites
	 * @return A list of all sprites that are in a targetable range from at least one point in the attackers current moveable-space. The list
	 * 			will be empty if no sprites are in range 
	 */
	private ArrayList<AttackableEntity> getAttackableSprites(StateInfo stateInfo, CombatSprite attacker, 
			MoveableSpace moveableSpace, int maxAttackRange)
	{
		ArrayList<AttackableEntity> combatSprites = new ArrayList<AttackableEntity>();
		
		for (CombatSprite s : stateInfo.getCombatSprites())
		{
			if (s == attacker)
				continue;
			AttackableEntity as = isInAttackRange(attacker, s, maxAttackRange, moveableSpace, stateInfo);
			if (as != null)
				combatSprites.add(as);
		}
		
		return combatSprites;
	}
	
	public int getApproachType() {
		return approachType;
	}

	public void setApproachType(int approachType) {
		this.approachType = approachType;
	}
	
	public void setApproachType(int approachType, Point p) {
		this.approachType = approachType;
		this.targetPoint = p;
	}
	
	public void setApproachType(int approachType, CombatSprite cs) {
		this.approachType = approachType;
		this.targetCS = cs;
	}

	/**
	 * Gets an AttackableSprite containing the locations from which the specified target combat-sprite is in range for the attacking sprite
	 * 
	 * @param attacking The combat sprite that AI is being performed for
	 * @param target The combat sprite that is being checked for attackability 
	 * @param maxAttackRange The maximum range that should be searched for targetable sprites
	 * @param ms The moveable space of the attacker combat sprite
	 * @param stateInfo The StateInfo for the current game state
	 * @return An AttackableEntity containing the locations from which the specified target entity is in range for the attacking entity, null
	 *			if the target is not in range
	 */
	private AttackableEntity isInAttackRange(CombatSprite attacking, Sprite target, int maxAttackRange, MoveableSpace ms, StateInfo stateInfo)
	{
		int tx = target.getTileX();
		int ty = target.getTileY();
		
		// TODO There are dymamic ranges....
		AttackableEntity attackable = null;
		
		for (int i = -maxAttackRange; i < maxAttackRange + 1; i++)
		{
			for (int j = -maxAttackRange; j < maxAttackRange + 1; j++)
			{
				int range = Math.abs(i) + Math.abs(j);
				if (Math.abs(i) + Math.abs(j) <= maxAttackRange && ms.canEndMoveHere(tx + i, ty + j))
				{
					if (attackable != null)
						attackable.addAttackablePoint(new Point(tx + i, ty + j), range);
					else
						attackable = new AttackableEntity((CombatSprite) target, new Point(tx + i, ty + j), range);
				}
			}
		}
		
		return attackable;
	}
	
	/**
	 * Describes a CombatSprite that is in range of the sprite that AI is being performed for.
	 * Includes all points from which the target is in range and the distance the attacker will
	 * be from the taret when at a given point
	 */
	private class AttackableEntity
	{
		private CombatSprite combatEntity;
		private ArrayList<Point> attackablePoints;
		private ArrayList<Integer> distances;
		
		public AttackableEntity(CombatSprite combatSprite, Point attackablePoint, int distance)
		{
			this.combatEntity = combatSprite; 
			this.attackablePoints = new ArrayList<Point>();
			this.attackablePoints.add(attackablePoint);
			this.distances = new ArrayList<Integer>();
			distances.add(distance);
		}
		
		public void addAttackablePoint(Point attackPoint, int distance)
		{
			attackablePoints.add(attackPoint);
			distances.add(distance);
		}

		public CombatSprite getCombatSprite() {
			return combatEntity;
		}

		public ArrayList<Point> getAttackablePoints() {
			return attackablePoints;
		}

		public ArrayList<Integer> getDistances() {
			return distances;
		}
	}
	
	protected abstract AttackSpriteAction getPerformedTurnAction(CombatSprite target);
	
	protected abstract int getMaxRange(CombatSprite currentSprite);
	
	protected abstract int getConfidence(CombatSprite currentSprite, CombatSprite targetSprite, 
			int tileWidth , int tileHeight, Point attackPoint, int distance, StateInfo stateInfo);
	
	protected abstract void initialize();
}
