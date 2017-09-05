package mb.fc.game.menu;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.listener.MenuListener;
import mb.fc.game.trigger.Trigger;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.game.ui.RectUI;
import mb.fc.game.ui.SelectRectUI;
import mb.fc.game.ui.TextUI;

public class YesNoMenu extends SpeechMenu
{
	private RectUI yesPanel, noPanel;
	private TextUI yesText, noText;
	private SelectRectUI selectRect;
	private boolean yesSelected = true;
	private Integer yesTrigger = null;
	private Integer noTrigger = null;

	public YesNoMenu(String text, int yesTrigger, int noTrigger, StateInfo stateInfo) {
		this(text, Trigger.TRIGGER_NONE, null, stateInfo, null);
		this.yesTrigger = yesTrigger;
		this.noTrigger = noTrigger;
	}
	
	public YesNoMenu(String text, StateInfo stateInfo, MenuListener listener) {
		this(text, Trigger.TRIGGER_NONE, null, stateInfo, listener);
	}

	public YesNoMenu(String text, int triggerId,
			Portrait portrait, StateInfo stateInfo, MenuListener listener) {
		super(text, stateInfo.getFCGameContainer(),triggerId, portrait, listener);
		yesPanel = new RectUI(120, 146, 32, 32);
		noPanel = new RectUI(170, 146, 32, 32);
		yesText = new TextUI("Yes", 125, 148);
		noText = new TextUI("No", 179, 148);
		selectRect = new SelectRectUI(120, 146, 32, 32);
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		if (input.isKeyDown(KeyMapping.BUTTON_1) || input.isKeyDown(KeyMapping.BUTTON_3))
		{
			// Handle unlistened to selections
			if (this.getMenuListener() == null && yesTrigger != null && noTrigger != null) {
				if (yesSelected) {
					if (yesTrigger != Trigger.TRIGGER_NONE)
						stateInfo.getResourceManager().getTriggerEventById(yesTrigger).perform(stateInfo);
				}
				else {
					if (noTrigger != Trigger.TRIGGER_NONE)
						stateInfo.getResourceManager().getTriggerEventById(noTrigger).perform(stateInfo);
				}
			}
			return MenuUpdate.MENU_CLOSE;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_LEFT))
		{
			selectRect.setX(120);
			yesSelected = true;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_RIGHT))
		{
			selectRect.setX(170);
			yesSelected = false;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_2))
		{
			yesSelected = false;
			return MenuUpdate.MENU_CLOSE;
		}
		return MenuUpdate.MENU_NO_ACTION;
	}
	
	

	@Override
	public MenuUpdate update(long delta, StateInfo stateInfo) {
		super.update(delta, stateInfo);
		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics)
	{
		super.render(gc, graphics);
		if (menuIsMovedIn)
		{
			// Draw background
			yesPanel.drawPanel(graphics);
			noPanel.drawPanel(graphics);
			// Draw temporary YES - NO
			graphics.setColor(Color.white);
			yesText.drawText(graphics);
			noText.drawText(graphics);

			// Draw selection square
			selectRect.draw(graphics, Color.red);
		}
	}

	@Override
	public Object getExitValue() {
		return yesSelected;
	}
}
