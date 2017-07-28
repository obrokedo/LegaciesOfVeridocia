package mb.fc.game.turnaction;

import org.newdawn.slick.Color;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.LOVAttackCinematicState;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.BattleResults;
import mb.fc.game.hudmenu.Panel.PanelType;
import mb.fc.game.manager.TurnManager;
import mb.fc.game.sprite.CombatSprite;
import mb.jython.JBattleEffect;

public class PerformAttackAction extends TurnAction
{
	private static final long serialVersionUID = 1L;

	private BattleResults battleResults;

	public PerformAttackAction(BattleResults battleResults) {
		super (TurnAction.ACTION_PERFORM_ATTACK);
		this.battleResults = battleResults;
	}

	@Override
	public boolean perform(int delta, TurnManager turnManager, StateInfo stateInfo) {
		stateInfo.removePanel(PanelType.PANEL_HEALTH_BAR);
		stateInfo.removePanel(PanelType.PANEL_ENEMY_HEALTH_BAR);
		if (CommRPG.BATTLE_MODE_OPTIMIZE)
		{
			for (int i = 0; i < battleResults.targets.size(); i++)
			{
				CombatSprite t = battleResults.targets.get(i);
				t.modifyCurrentHP(battleResults.hpDamage.get(i));
				t.modifyCurrentMP(battleResults.mpDamage.get(i));
				turnManager.getCurrentSprite().modifyCurrentHP(battleResults.attackerHPDamage.get(i));
				turnManager.getCurrentSprite().modifyCurrentMP(battleResults.attackerMPDamage.get(i));
				if (battleResults.targetEffects.get(i) != null)
					for (JBattleEffect be : battleResults.targetEffects.get(i))
					{
						be.effectStarted(turnManager.getCurrentSprite(), t);
					}
			}
			
			return true;
		}
		
		stateInfo.setShowAttackCinematic(true);
		LOVAttackCinematicState acs = (LOVAttackCinematicState) stateInfo.getPersistentStateInfo().getGame().getState(CommRPG.STATE_GAME_BATTLE_ANIM);
		acs.setBattleInfo(turnManager.getCurrentSprite(), stateInfo.getResourceManager(), battleResults, stateInfo.getFCGameContainer());
		stateInfo.getPersistentStateInfo().getGame().enterState(CommRPG.STATE_GAME_BATTLE_ANIM, new FadeOutTransition(Color.black, 250), new EmptyTransition());
		return true;
	}

}
