package mb.fc.game.menu;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.input.FCInput;
import mb.fc.game.move.MovingSprite;
import mb.fc.game.sprite.AnimatedSprite;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.jython.GlobalPythonFactory;

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
				stateInfo.getPsi().loadBattle(splitText[1], splitText[2], splitText[3], (splitText.length > 3 ? Integer.parseInt(splitText[4]) : 0));
			}
			else if (splitText[0].equalsIgnoreCase("loadcin"))
			{
				if (splitText.length == 2)
					stateInfo.getPsi().loadCinematic(splitText[1], 0);
				else
					stateInfo.getPsi().loadCinematic(splitText[1], Integer.parseInt(splitText[2]));
			}
			else if (splitText[0].equalsIgnoreCase("mute"))
			{
				/*
				if (stateInfo.getPlayingMusic() != null)
					stateInfo.getPlayingMusic().setVolume(0f);
					*/
			}
			else if (splitText[0].equalsIgnoreCase("printmusic"))
			{
				/*
				if (stateInfo.getPlayingMusic() == null)
					stateInfo.sendMessage(new InfoMessage("SYSTEM", "Playing music: NULL"));
				else
					stateInfo.sendMessage(new InfoMessage("SYSTEM", "Playing music: " + stateInfo.getPlayingMusic().toString()));
					*/
			}
			else if (splitText[0].equalsIgnoreCase("heal"))
			{
				for (CombatSprite cs : stateInfo.getHeroes())
				{
					cs.setCurrentHP(cs.getMaxHP());
					cs.setCurrentMP(cs.getMaxMP());
				}
			}
			else if (splitText[0].equalsIgnoreCase("movespeed"))
			{
				MovingSprite.MOVE_SPEED = Integer.parseInt(splitText[1]);
			}
			else if (splitText[0].equalsIgnoreCase("moveanim"))
			{
				MovingSprite.WALK_ANIMATION_SPEED = Integer.parseInt(splitText[1]);
			}
			else if (splitText[0].equalsIgnoreCase("standanim"))
			{
				MovingSprite.STAND_ANIMATION_SPEED = Integer.parseInt(splitText[1]);
			}
			else if (splitText[0].equalsIgnoreCase("loadscripts"))
			{
				GlobalPythonFactory.intialize();
			}
			else if (splitText[0].equalsIgnoreCase("play"))
			{
				stateInfo.sendMessage(new AudioMessage(MessageType.PLAY_MUSIC, splitText[1], 1, true));
			}
			else if (splitText[0].equalsIgnoreCase("gainlevel"))
			{
				for (CombatSprite cs : stateInfo.getHeroes())
					cs.setExp(100);
			}
			else if (splitText[0].equalsIgnoreCase("setone"))
			{
				for (CombatSprite cs : stateInfo.getHeroes())
					cs.setCurrentHP(1);
			}
			else if (splitText[0].equalsIgnoreCase("shadow"))
			{
				AnimatedSprite.SHADOW_OFFSET = Integer.parseInt(splitText[1]);
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
