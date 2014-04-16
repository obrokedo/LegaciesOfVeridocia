package mb.fc.game.menu;

import mb.fc.engine.message.ChatMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.input.FCInput;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.gui.TextField;

public class DebugMenu extends Menu
{
	private TextField textField;
	
	public DebugMenu(GameContainer gc) {
		super(Menu.PANEL_DEBUG);
		textField = new TextField(gc, gc.getDefaultFont(), 60, gc.getHeight() - 45, gc.getWidth() - 120, 30);
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		textField.setFocus(true);
		
		if (stateInfo.getGc().getInput().isKeyDown(Input.KEY_ENTER))
		{
			String text = textField.getText();
			String[] splitText = text.split(" ");
			if (splitText[0].equalsIgnoreCase("loadmap"))
			{
				stateInfo.getPsi().loadMap(splitText[1], splitText[2]);
			}
			else if (splitText[0].equalsIgnoreCase("loadbattle"))
			{
				stateInfo.getPsi().loadBattle(splitText[1], splitText[2]);
			}
			else if (splitText[0].equalsIgnoreCase("loadcin"))
			{
				stateInfo.getPsi().loadCinematic(splitText[1]);
			}
			else if (splitText[0].equalsIgnoreCase("mute"))
			{
				if (stateInfo.getPlayingMusic() != null)
					stateInfo.getPlayingMusic().setVolume(0f);
			}
			else if (splitText[0].equalsIgnoreCase("printmusic"))
			{
				if (stateInfo.getPlayingMusic() == null)
					stateInfo.sendMessage(new ChatMessage(Message.MESSAGE_SEND_INTERNAL_MESSAGE, "SYSTEM", "Playing music: NULL"));
				else
					stateInfo.sendMessage(new ChatMessage(Message.MESSAGE_SEND_INTERNAL_MESSAGE, "SYSTEM", "Playing music: " + stateInfo.getPlayingMusic().toString()));
			}
			else if (splitText[0].equalsIgnoreCase("heal"))
			{
				for (CombatSprite cs : stateInfo.getHeroes())
				{
					cs.setCurrentHP(cs.getMaxHP());
					cs.setCurrentMP(cs.getMaxMP());
				}
			}
			
			textField.deactivate();
			return MenuUpdate.MENU_CLOSE;
		}
		
		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) {	
		graphics.setColor(Color.white);
		graphics.fillRect(50, gc.getHeight() - 50, gc.getWidth() - 100, 40);
		textField.render(gc, graphics);
	}

}
