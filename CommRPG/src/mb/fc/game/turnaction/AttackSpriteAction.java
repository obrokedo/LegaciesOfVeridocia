package mb.fc.game.turnaction;

import java.util.ArrayList;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleResultsMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.BattleResults;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.manager.TurnManager;
import mb.fc.game.sprite.CombatSprite;

public class AttackSpriteAction extends TurnAction
{
	private static final long serialVersionUID = 1L;

	private ArrayList<CombatSprite> targets;
	private BattleCommand battleCommand;

	public AttackSpriteAction(CombatSprite target, BattleCommand battleCommand) {
		super(TurnAction.ACTION_ATTACK_SPRITE);
		this.battleCommand = battleCommand;
		targets = new ArrayList<CombatSprite>();
		targets.add(target);
	}

	public AttackSpriteAction(ArrayList<CombatSprite> targets, BattleCommand battleCommand) {
		super (TurnAction.ACTION_ATTACK_SPRITE);
		this.battleCommand = battleCommand;
		this.targets = targets;
	}

	@Override
	public boolean perform(TurnManager turnManager, StateInfo stateInfo) {
		if (!turnManager.getCurrentSprite().isHero())
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
		stateInfo.sendMessage(new BattleResultsMessage(BattleResults.determineBattleResults(turnManager.getCurrentSprite(),
			targets, battleCommand, stateInfo)));
		turnManager.setDisplayAttackable(false);
		return true;
	}

	public BattleCommand getBattleCommand() {
		return battleCommand;
	}
}
