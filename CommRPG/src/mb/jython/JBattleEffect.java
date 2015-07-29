package mb.jython;

import java.io.Serializable;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.loading.FCResourceManager;
import mb.fc.utils.AnimationWrapper;

public abstract class JBattleEffect implements Serializable
{
	private static final long serialVersionUID = 1L;

	protected boolean isNegativeEffect = false;
	protected int duration;
	protected int currentTurn;
	protected int temporaryStat;
	protected String battleEffectId;
	private transient AnimationWrapper effectAnimation;

	public abstract JBattleEffect init(String battleEffectId);

	protected abstract String performEffectImpl(CombatSprite target, int currentTurn);

	public abstract String effectStarted(CombatSprite attacker, CombatSprite target);

	public abstract String effectStartedText(CombatSprite attacker, CombatSprite target);

	public abstract String effectEnded(CombatSprite target);

	public abstract String getAnimationFile();

	public void initializeAnimation(FCResourceManager frm)
	{
		effectAnimation = new AnimationWrapper(frm.getSpriteAnimations().get(getAnimationFile()), "Effect", true);
	}

	public String performEffect(CombatSprite target)
	{
		return performEffectImpl(target, currentTurn++);
	}

	public boolean isNegativeEffect() {
		return isNegativeEffect;
	}

	public boolean isDone()
	{
		return duration >= currentTurn;
	}

	public void setBattleEffectId(String battleEffectId) {
		this.battleEffectId = battleEffectId;
	}

	public String getBattleEffectId() {
		return battleEffectId;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(int currentTurn) {
		this.currentTurn = currentTurn;
	}

	public int getTemporaryStat() {
		return temporaryStat;
	}

	public void setTemporaryStat(int temporaryStat) {
		this.temporaryStat = temporaryStat;
	}

	public void setNegativeEffect(boolean isNegativeEffect) {
		this.isNegativeEffect = isNegativeEffect;
	}

	public AnimationWrapper getEffectAnimation() {
		return effectAnimation;
	}

	public abstract String[] getBattleEffectList();
}
