package mb.fc.game.combat;

import mb.fc.engine.CommRPG;
import mb.fc.game.battle.condition.BattleEffect;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class DamagedCombatAnimation extends CombatAnimation
{
	private static final Color DAMAGE_COLOR = new Color(204, 102, 0, 200);

	private CombatAnimation childAnimation;
	private int hpDamage;
	private int mpDamage;
	private BattleEffect battleEffect;
	private CombatSprite attacker;
	private boolean isNegativeEffect = false;

	public DamagedCombatAnimation(CombatAnimation childAnimation, int hpDamage, int mpDamage,
			BattleEffect battleEffect, CombatSprite attacker)
	{
		super();
		this.minimumTimePassed = 200;
		this.childAnimation = childAnimation;
		this.hpDamage = hpDamage;
		this.mpDamage = mpDamage;
		this.battleEffect = battleEffect;
		this.attacker = attacker;
		if (hpDamage < 0 || mpDamage < 0 || (battleEffect != null && battleEffect.isNegativeEffect()))
			isNegativeEffect =  true;
	}

	@Override
	public boolean update(int delta)
	{
		this.totalTimePassed += delta;
		childAnimation.update(delta);
		if (isNegativeEffect)
		{
			childAnimation.xOffset = (int) ((CommRPG.RANDOM.nextInt(20) - 10) * SCREEN_SCALE);
			childAnimation.yOffset = (int) ((CommRPG.RANDOM.nextInt(20) - 10) * SCREEN_SCALE);
		}
		return totalTimePassed >= minimumTimePassed;
	}

	@Override
	public void render(FCGameContainer fcCont, Graphics g, int yDrawPos) {
		childAnimation.render(fcCont, g, yDrawPos);
	}

	@Override
	public void initialize() {
		if (isNegativeEffect)
			childAnimation.renderColor = DAMAGE_COLOR;

		childAnimation.parentSprite.modifyCurrentHP(hpDamage);
		childAnimation.parentSprite.modifyCurrentMP(mpDamage);
		if (battleEffect != null)
			battleEffect.performEffect(null, childAnimation.parentSprite);
	}

	@Override
	public CombatSprite getParentSprite() {
		return childAnimation.getParentSprite();
	}

	@Override
	public boolean isDrawSpell() {
		return childAnimation.isDrawSpell();
	}

	@Override
	public boolean isDamaging() {
		return true;
	}

	public boolean willSpriteDie()
	{
		return childAnimation.parentSprite.getCurrentHP() + hpDamage <= 0;
	}
}
