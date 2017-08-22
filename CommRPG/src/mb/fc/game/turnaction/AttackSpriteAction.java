package mb.fc.game.turnaction;

import java.util.ArrayList;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleResultsMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.BattleResults;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.manager.TurnManager;
import mb.fc.game.sprite.CombatSprite;

public class AttackSpriteAction extends TurnAction
{
	private static final long serialVersionUID = 1L;

	private ArrayList<Integer> targets;
	private BattleCommand battleCommand;

	public AttackSpriteAction(CombatSprite target, BattleCommand battleCommand) {
		super(TurnAction.ACTION_ATTACK_SPRITE);
		this.battleCommand = battleCommand;
		targets = new ArrayList<Integer>();
		targets.add(target.getId());
	}

	public AttackSpriteAction(ArrayList<CombatSprite> targets, BattleCommand battleCommand) {
		super (TurnAction.ACTION_ATTACK_SPRITE);
		this.battleCommand = battleCommand;
		this.targets = new ArrayList<Integer>();
		for (CombatSprite cs : targets)
			this.targets.add(cs.getId());
	}

	@Override
	public boolean perform(int delta, TurnManager turnManager, StateInfo stateInfo) {
		if (!turnManager.getCurrentSprite().isHero())
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuselect", 1f, false));
		ArrayList<CombatSprite> targArray = new ArrayList<>();
		for (CombatSprite cs : stateInfo.getCombatSprites())
		{
			for (Integer targ : targets)
			{
				if (cs.getId() == targ)
				{
					targArray.add(cs);
					break;
				}
			}
		}
		stateInfo.sendMessage(new BattleResultsMessage(BattleResults.determineBattleResults(turnManager.getCurrentSprite(),
			targArray, battleCommand, stateInfo.getResourceManager())), true);
		turnManager.setDisplayAttackable(false);
		return true;
	}

	public BattleCommand getBattleCommand() {
		return battleCommand;
	}
}
