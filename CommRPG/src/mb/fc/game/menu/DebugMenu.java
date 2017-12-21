package mb.fc.game.menu;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.constants.Direction;
import mb.fc.game.input.FCInput;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.trigger.Trigger;
import mb.fc.game.ui.Button;
import mb.fc.game.ui.ListUI;
import mb.fc.game.ui.ListUI.ResourceSelectorListener;
import mb.fc.game.ui.PaddedGameContainer;

public class DebugMenu extends Menu implements ResourceSelectorListener
{
	private enum DebugMenuState {
		CHOOSE_TRIGGER,
		SEE_QUEST,
		CHOOSE_SPRITE,
		SPRITE_OPTIONS,
		PLACE_SPRITE,
		SHOW_QUESTS
	}
	
	private StateInfo stateInfo;
	private ListUI triggerList;
	private ListUI questList;
	private CombatSprite selectedSprite = null;
	private Button moveButton = new Button(270, 25, 140, 20, "Move Combatant");
	private Button killButton = new Button(270, 55, 140, 20, "Kill Combatant");
	private Button levelButton = new Button(270, 85, 140, 20, "Level Up Hero");
	private Button healButton = new Button(270, 115, 140, 20, "Heal");
	private Button setToOneButton = new Button(270, 145, 140, 20, "Set to 1 HP");
	
	private Button chooseSprite = new Button(270, 55, 140, 20, "Choose Sprite");
	private Button showQuests = new Button(270, 25, 200, 20, "Show Completed Quests");
	private int inputTimer = 0;
	private String triggerStatus = null;
	private DebugMenuState state = DebugMenuState.CHOOSE_TRIGGER;

	public DebugMenu(StateInfo stateInfo) {
		super(PanelType.PANEL_DEBUG);
		this.stateInfo = stateInfo;
		
		this.triggerList = new ListUI("Triggers", 5, 
				new ArrayList<String>(stateInfo.getResourceManager().getTriggers().stream().map(t -> t.getName()).collect(Collectors.toList())),
				22);
		this.triggerList.setListener(this);
		

	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		if (stateInfo.getPaddedGameContainer().getInput().isKeyDown(Input.KEY_ENTER))
		{
			stateInfo.getCamera().centerOnSprite(stateInfo.getCurrentSprite(), stateInfo.getCurrentMap());
			return MenuUpdate.MENU_CLOSE;
		}
		/*
		textField.setFocus(true);
		
		if (stateInfo.getFCGameContainer().getInput().isKeyDown(Input.KEY_ENTER))
		{
			String text = textField.getText();
			String[] splitText = text.split(" ");
			if (splitText[0].equalsIgnoreCase("loadmap"))
			{
				stateInfo.getPersistentStateInfo().loadMap(splitText[1], splitText[2]);
			}
			else if (splitText[0].equalsIgnoreCase("loadbattle"))
			{
				stateInfo.getPersistentStateInfo().loadBattle(splitText[1], splitText[2], (splitText.length > 2 ? Integer.parseInt(splitText[3]) : 0));
			}
			else if (splitText[0].equalsIgnoreCase("loadcin"))
			{
				if (splitText.length == 2)
					stateInfo.getPersistentStateInfo().loadCinematic(splitText[1], 0);
				else
					stateInfo.getPersistentStateInfo().loadCinematic(splitText[1], Integer.parseInt(splitText[2]));
			}
			else if (splitText[0].equalsIgnoreCase("printmusic"))
			{
				
			}
			else if (splitText[0].equalsIgnoreCase("heal"))
			{
				for (CombatSprite cs : stateInfo.getHeroesInState())
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
				for (CombatSprite cs : stateInfo.getHeroesInState())
					cs.setExp(100);
			}
			else if (splitText[0].equalsIgnoreCase("setone"))
			{
				for (CombatSprite cs : stateInfo.getHeroesInState())
					cs.setCurrentHP(1);
			}
			else if (splitText[0].equalsIgnoreCase("shadow"))
			{
				AnimatedSprite.SHADOW_OFFSET = Integer.parseInt(splitText[1]);
			}
			else if (splitText[0].equalsIgnoreCase("framerate"))
			{
				if (splitText.length > 1)
				{
					try
					{
						gc.setTargetFrameRate(Integer.parseInt(splitText[1]));
					}
					catch (NumberFormatException nfe)
					{

					}
				}
			}
			else if (splitText[0].equalsIgnoreCase("mute"))
			{
				stateInfo.sendMessage(MessageType.PAUSE_MUSIC);
				CommRPG.MUTE_MUSIC = true;
			}

			textField.deactivate();
			return MenuUpdate.MENU_CLOSE;
		}
		*/

		return MenuUpdate.MENU_NO_ACTION;
	}
	
	@Override
	public MenuUpdate update(long delta, StateInfo stateInfo) {
		if (inputTimer > 0) {
			inputTimer -= delta;
			return MenuUpdate.MENU_NO_ACTION;
		}
		
		int x = stateInfo.getPaddedGameContainer().getInput().getMouseX();
		int y = stateInfo.getPaddedGameContainer().getInput().getMouseY();
		
		int mouseMove = 10;
		int mouseBounds = 20;
		Camera camera = stateInfo.getCamera();
		if (state == DebugMenuState.CHOOSE_SPRITE || state == DebugMenuState.PLACE_SPRITE) {
			if (x > stateInfo.getPaddedGameContainer().getWidth() - mouseBounds) {
				camera.setLocation(camera.getLocationX() + mouseMove, 
						camera.getLocationY(), stateInfo);
			} else if (x < mouseBounds) {
				camera.setLocation(camera.getLocationX() - mouseMove, 
						camera.getLocationY(), stateInfo);
			}
			
			if (y > stateInfo.getPaddedGameContainer().getHeight() - mouseBounds) {
				camera.setLocation(camera.getLocationX(), 
						camera.getLocationY() + mouseMove, stateInfo);
			} else if (y < mouseBounds) {
				camera.setLocation(camera.getLocationX(), 
						camera.getLocationY() - mouseMove, stateInfo);
			}
		}
		
		boolean leftClick = stateInfo.getPaddedGameContainer().getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON);
		boolean rightClick = stateInfo.getPaddedGameContainer().getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON);
		
		if (rightClick) {
			if (state == DebugMenuState.PLACE_SPRITE)
				state = DebugMenuState.SPRITE_OPTIONS;
			else if (state == DebugMenuState.SPRITE_OPTIONS)
				state = DebugMenuState.CHOOSE_TRIGGER;
			else if (state == DebugMenuState.CHOOSE_SPRITE)
				state = DebugMenuState.CHOOSE_TRIGGER;
			else if (state == DebugMenuState.SHOW_QUESTS)
				state = DebugMenuState.CHOOSE_TRIGGER;
			
			timerReset();
		}
		
		if (leftClick && (state == DebugMenuState.CHOOSE_SPRITE || state == DebugMenuState.PLACE_SPRITE)) {
			int clickX =  (int) (x / CommRPG.GAME_SCREEN_SCALE + camera.getLocationX()) ; 
			int clickY =  (int) (y / CommRPG.GAME_SCREEN_SCALE + camera.getLocationY());
			
			int tilePixelX = clickX / stateInfo.getTileWidth() * stateInfo.getTileWidth();
			int tilePixelY = clickY / stateInfo.getTileHeight() * stateInfo.getTileHeight();
			CombatSprite cs = stateInfo.getCombatSpriteAtMapLocation(tilePixelX, 
					tilePixelY, false);
			if (cs == null) {
				cs = stateInfo.getCombatSpriteAtMapLocation(tilePixelX, 
						tilePixelY, true);
			}
			if (state == DebugMenuState.CHOOSE_SPRITE) {
				this.selectedSprite = cs;
				if (selectedSprite != null)
					state = DebugMenuState.SPRITE_OPTIONS;
			} else if (state == DebugMenuState.PLACE_SPRITE && cs == null) {
				selectedSprite.setLocation(tilePixelX, tilePixelY, stateInfo.getTileWidth(), stateInfo.getTileHeight());
				selectedSprite.setFacing(Direction.DOWN);
			}
			timerReset();
		}
		
		if (state == DebugMenuState.SPRITE_OPTIONS) {
			if (moveButton.handleUserInput(x, y, leftClick)) {
				state = DebugMenuState.PLACE_SPRITE;
				timerReset();
			}
			if (killButton.handleUserInput(x, y, leftClick)) {
				selectedSprite.setCurrentHP(0);
				state = DebugMenuState.CHOOSE_TRIGGER;
				selectedSprite = null;
				timerReset();
			}
			if (levelButton.handleUserInput(x, y, leftClick)) {
				if (selectedSprite.isHero()) {
					selectedSprite.levelUpHiddenStatistics();
					selectedSprite.getHeroProgression().levelUp(selectedSprite, selectedSprite.getHeroProgression().
							getLevelUpResults(selectedSprite));
					selectedSprite.setExp(0);
					
				}
				timerReset();
			}
			if (healButton.handleUserInput(x, y, leftClick)) {
				selectedSprite.setCurrentHP(selectedSprite.getMaxHP());
				selectedSprite.setCurrentMP(selectedSprite.getMaxMP());
				timerReset();
			}
			
			if (setToOneButton.handleUserInput(x, y, leftClick)) {
				selectedSprite.setCurrentHP(1);
				timerReset();
			}
		} 
		if (state == DebugMenuState.CHOOSE_TRIGGER) {
			if (stateInfo.isCombat() && chooseSprite.handleUserInput(x, y, leftClick)) {
				state = DebugMenuState.CHOOSE_SPRITE;
				triggerStatus = null;
				timerReset();
			}
			if (showQuests.handleUserInput(x, y, leftClick)) {
				state = DebugMenuState.SHOW_QUESTS;
				triggerStatus = null;
				questList = new ListUI("Completed Quests", 5, 
				new ArrayList<String>(stateInfo.getClientProgress().getQuestsCompleted()),
				22);
				timerReset();
			}
		}
		
		
		// stateInfo.getCamera().setLocation(x, y, stateInfo);
		triggerList.update(stateInfo.getPaddedGameContainer(), (int) delta);
		return super.update(delta, stateInfo);
	}

	private void timerReset() {
		inputTimer = 200;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		
		graphics.setColor(new Color(0, 0, 0, 120));
		graphics.scale(1.0f / CommRPG.GAME_SCREEN_SCALE, 1.0f / CommRPG.GAME_SCREEN_SCALE);
		if (state == DebugMenuState.CHOOSE_TRIGGER || state == DebugMenuState.SPRITE_OPTIONS) {
			graphics.fillRect(0, 0, gc.getWidth() / 2, gc.getHeight());
			triggerList.render(graphics);
		}
		else if (state == DebugMenuState.SHOW_QUESTS) {
			graphics.fillRect(0, 0, gc.getWidth() / 2, gc.getHeight());
			questList.render(graphics);
		}
		
		int mouseBounds = 20;
		
		graphics.setColor(Color.yellow);
		if (state == DebugMenuState.CHOOSE_SPRITE || state == DebugMenuState.PLACE_SPRITE)
			graphics.drawRect(mouseBounds, mouseBounds, gc.getWidth() - mouseBounds * 2, gc.getHeight() - mouseBounds * 2);
		graphics.setColor(Color.white);
		if (state == DebugMenuState.SPRITE_OPTIONS) {
			graphics.drawString("Selected " + selectedSprite.getName() + " " + selectedSprite.getUniqueEnemyId(), 200, 15);
			moveButton.render(graphics);
			killButton.render(graphics);
			levelButton.render(graphics);
			healButton.render(graphics);
			setToOneButton.render(graphics);
		} 
		
		if (state == DebugMenuState.CHOOSE_TRIGGER) {
			if (stateInfo.isCombat())
				chooseSprite.render(graphics);
			showQuests.render(graphics);
		}
		graphics.drawString("Right Click to Go Back", 5, 650);
		
		if (triggerStatus != null) {
			graphics.drawString(triggerStatus, 5, 620);
		}
		
		graphics.scale(CommRPG.GAME_SCREEN_SCALE, CommRPG.GAME_SCREEN_SCALE);
	}

	@Override
	public boolean resourceSelected(String selectedItem, ListUI parentSelector) {
		if (!selectedItem.equalsIgnoreCase(parentSelector.getSelectedResource())) {
			Optional<Trigger> trigger = stateInfo.getResourceManager().getTriggers().stream().filter(t -> t.getName().equalsIgnoreCase(selectedItem)).findFirst();
			if (trigger.isPresent()) {
				switch (trigger.get().perform(stateInfo)) {
				case EXCLUDED_QUEST_DONE:
					triggerStatus = "Trigger Not Run: Excluded Quest Completed";
					break;
				case IS_IMMEDIATE:
					trigger.get().perform(stateInfo, true);
					break;
				case NON_RETRIG:
					triggerStatus = "Trigger Not Run: Can only be run once per game";
					break;
				case REQUIRED_QUEST_NOT_DONE:
					triggerStatus = "Trigger Not Run: Required Quest Incomplete";
					break;
				case TRIGGERED:
					triggerStatus = "Trigger Run (May need to close menu)";
					break;
				case TRIGGER_ONCE:
					triggerStatus = "Trigger Not Run: Can only be run once per map";
					break;
				default:
					break;
				
				}
			}
		}
		return true;
	}
}
