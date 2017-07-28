package mb.fc.game.combat;

import mb.fc.game.ui.PaddedGameContainer;

import org.newdawn.slick.Graphics;

public class InvisibleCombatAnimation extends CombatAnimation {

	@Override
	public boolean update(int delta) {
		return true;
	}

	@Override
	public void render(PaddedGameContainer fcCont, Graphics g, int yDrawPos, float scale) {

	}

}
