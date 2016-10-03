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
	protected int effectChance;
	protected int effectLevel;
	protected String battleEffectId;
	
	private transient AnimationWrapper effectAnimation;

	public abstract JBattleEffect init(String battleEffectId, int level);

	protected abstract String performEffectImpl(CombatSprite target, int currentTurn);

	public abstract void effectStarted(CombatSprite attacker, CombatSprite target);

	public abstract String effectStartedText(CombatSprite attacker, CombatSprite target);

	public abstract void effectEnded(CombatSprite target);
	
	public abstract String effectEndedText(CombatSprite target);

	public abstract String getAnimationFile();

	public abstract boolean isEffected(CombatSprite target);
	
	public abstract String getName();
	
	public boolean preventsMovement() {
		return false;
	}
	
	public boolean preventsAttack(){
		return false;
	}
	
	public boolean preventsSpells(){
		return false;
	}
	
	public boolean preventsItems(){
		return false;
	}
	
	public boolean preventsTurn(){
		return false;
	}

	public void initializeAnimation(FCResourceManager frm)
	{
		if (getAnimationFile() != null)
			effectAnimation = new AnimationWrapper(frm.getSpriteAnimations().get(getAnimationFile()), "Effect", true);
		else
			effectAnimation = null;
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
		return duration <= currentTurn;
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

	public void incrementTurn() {
		this.currentTurn++;
	}

	public void setNegativeEffect(boolean isNegativeEffect) {
		this.isNegativeEffect = isNegativeEffect;
	}

	public AnimationWrapper getEffectAnimation() {
		return effectAnimation;
	}

	public int getEffectChance() {
		return effectChance;
	}

	public void setEffectChance(int effectChance) {
		this.effectChance = effectChance;
	}
	
	public int getEffectLevel() {
		return effectLevel;
	}

	public void setEffectLevel(int effectLevel) {
		this.effectLevel = effectLevel;
	}

	public abstract String[] getBattleEffectList();
}
