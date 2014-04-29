package mb.fc.game.turnaction;

import java.util.ArrayList;

import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.sprite.CombatSprite;

public class AttackSpriteAction extends TurnAction 
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<CombatSprite> targets;
	private BattleCommand battleCommand;
	
	public AttackSpriteAction(int action, CombatSprite target, BattleCommand battleCommand) {
		super(action);
		this.battleCommand = battleCommand;
		targets = new ArrayList<CombatSprite>();
		targets.add(target);
	}
	
	public AttackSpriteAction(int action, ArrayList<CombatSprite> targets, BattleCommand battleCommand) {
		super (action);
		this.battleCommand = battleCommand;
		this.targets = targets;
	}
	
	public ArrayList<CombatSprite> getTargets()
	{
		return targets;
	}

	public BattleCommand getBattleCommand() {
		return battleCommand;
	}
}
