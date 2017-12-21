package mb.fc.engine.config;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.Timer;
import mb.fc.game.ui.PaddedGameContainer;

public class LOVYesNoMenuRenderer implements YesNoMenuRenderer {

	private Timer timer = new Timer(320);
	protected Image[] icons;
	protected Image cursor;
	private boolean toggle = false;
	private boolean yesSelected = true;
	private Image flourish;
	private SpriteSheet selectorSS;
	private int selectionState = 0;
	private Timer moveTimer = new Timer(40);
	
	@Override
	public void initialize(StateInfo stateInfo) {
		SpriteSheet ss = stateInfo.getResourceManager().getSpriteSheet("yesnoicons");
		icons = new Image[4];
		for (int i = 0; i < icons.length; i++)
			icons[i] = ss.getSubImage(i % 2, i / 2);
	
		cursor = stateInfo.getResourceManager().getImage("battlecursor");
		flourish = stateInfo.getResourceManager().getImage("yesnoflor");
		selectorSS = stateInfo.getResourceManager().getSpriteSheet("yesnoselect");
		yesSelected = true;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		int relX = 135;
		int relY = 130;
		
		graphics.drawImage(flourish, relX + 5, relY);
		
		graphics.drawImage(icons[(yesSelected && toggle ? 2 :0)], relX + 6 - 
				(selectionState > 1 ? selectionState - 1: 0), relY + 12);
		graphics.drawImage(icons[(!yesSelected && toggle ? 3 :1)], relX + 30 - 
				(selectionState < -1 ? selectionState + 1: 0), relY + 12);
		
		if (selectionState > 0) {
			graphics.drawImage(selectorSS.getSprite(selectionState - 1, 0), relX, relY + 7);
		} else if (selectionState < 0) {
			graphics.drawImage(selectorSS.getSprite(Math.abs(selectionState) - 1, 0).getFlippedCopy(true, false), 
					relX + 54, relY + 7);
		}
		
		graphics.drawImage(flourish.getFlippedCopy(false, true), relX + 5, relY + 36);
	}

	@Override
	public void update(long delta, StateInfo stateInfo) {
		timer.update(delta);
		moveTimer.update(delta);
		while (timer.perform()) {
			toggle = !toggle;
		}
		
		while (moveTimer.perform()) {
			if (yesSelected && selectionState < 3) { 
				selectionState++;
			} else if (!yesSelected && selectionState > -3) {
				selectionState--;
			}
		}
	}

	@Override
	public void yesPressed() {
		toggle = false;
		yesSelected = true;
	}

	@Override
	public void noPressed() {
		toggle = false;
		yesSelected = false;
	}

}
