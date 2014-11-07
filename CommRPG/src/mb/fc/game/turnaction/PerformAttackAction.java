package mb.fc.game.turnaction;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.AttackCinematicState;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.BattleResults;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.manager.TurnManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class PerformAttackAction extends TurnAction
{
	private static final long serialVersionUID = 1L;

	private BattleResults battleResults;

	public PerformAttackAction(BattleResults battleResults) {
		super (TurnAction.ACTION_PERFORM_ATTACK);
		this.battleResults = battleResults;
	}

	@Override
	public boolean perform(TurnManager turnManager, StateInfo stateInfo) {
		stateInfo.removePanel(Panel.PANEL_HEALTH_BAR);
		stateInfo.removePanel(Panel.PANEL_ENEMY_HEALTH_BAR);
		stateInfo.setShowAttackCinematic(true);
		AttackCinematicState acs = (AttackCinematicState) stateInfo.getPsi().getGame().getState(CommRPG.STATE_GAME_BATTLE_ANIM);
		acs.setBattleInfo(turnManager.getCurrentSprite(), stateInfo.getResourceManager(), battleResults, stateInfo.getGc());
		stateInfo.getPsi().getGame().enterState(CommRPG.STATE_GAME_BATTLE_ANIM, new FadeOutTransition(Color.black, 250), new EmptyTransition());
		return true;
	}

}
