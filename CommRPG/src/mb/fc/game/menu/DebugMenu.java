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
	private StateInfo stateInfo;
	private ListUI triggerList;
	private CombatSprite selectedSprite = null;
	private Button moveButton = new Button(200, 50, 140, 20, "Move Combatant");
	private Button killButton = new Button(200, 80, 140, 20, "Kill Combatant");
	private Button levelButton = new Button(200, 110, 140, 20, "Level Up Hero");
	private Button healButton = new Button(200, 140, 140, 20, "Heal");
	private Button setToOneButton = new Button(200, 170, 140, 20, "Set to 1 HP");
	private Button chooseSprite = new Button(200, 50, 140, 20, "Choose Sprite");
	private int inputTimer = 0;
	private String triggerStatus = null;
	
	private boolean choosingSprite = false, moveSprite = false;

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
		if (stateInfo.getFCGameContainer().getInput().isKeyDown(Input.KEY_ENTER))
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
		
		int x = stateInfo.getFCGameContainer().getInput().getMouseX();
		int y = stateInfo.getFCGameContainer().getInput().getMouseY();
		
		int mouseMove = 10;
		int mouseBounds = 20;
		Camera camera = stateInfo.getCamera();
		if (choosingSprite || moveSprite) {
			if (x > stateInfo.getFCGameContainer().getWidth() - mouseBounds) {
				camera.setLocation(camera.getLocationX() + mouseMove, 
						camera.getLocationY(), stateInfo);
			} else if (x < mouseBounds) {
				camera.setLocation(camera.getLocationX() - mouseMove, 
						camera.getLocationY(), stateInfo);
			}
			
			if (y > stateInfo.getFCGameContainer().getHeight() - mouseBounds) {
				camera.setLocation(camera.getLocationX(), 
						camera.getLocationY() + mouseMove, stateInfo);
			} else if (y < mouseBounds) {
				camera.setLocation(camera.getLocationX(), 
						camera.getLocationY() - mouseMove, stateInfo);
			}
		}
		
		boolean leftClick = stateInfo.getFCGameContainer().getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON);
		boolean rightClick = stateInfo.getFCGameContainer().getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON);
		
		if (rightClick) {
			if (moveSprite)
				moveSprite = false;
			else if (selectedSprite != null)
				selectedSprite = null;
			else if (choosingSprite)
				choosingSprite = false;
			
			
			timerReset();
		}
		
		if (leftClick && (choosingSprite || moveSprite)) {
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
			if (choosingSprite) {
				this.selectedSprite = cs;
				if (selectedSprite != null)
					choosingSprite = false;
			} else if (moveSprite && cs == null) {
				selectedSprite.setLocation(tilePixelX, tilePixelY, stateInfo.getTileWidth(), stateInfo.getTileHeight());
				selectedSprite.setFacing(Direction.DOWN);
			}
			timerReset();
		}
		
		if (selectedSprite != null && !moveSprite) {
			if (moveButton.handleUserInput(x, y, leftClick)) {
				moveSprite = true;
				timerReset();
			}
			if (killButton.handleUserInput(x, y, leftClick)) {
				selectedSprite.setCurrentHP(selectedSprite.getMaxHP());
				selectedSprite.setCurrentMP(selectedSprite.getMaxMP());
				selectedSprite = null;
				timerReset();
			}
			if (levelButton.handleUserInput(x, y, leftClick)) {
				if (selectedSprite.isHero()) {
					selectedSprite.levelUpHiddenStatistics();
					selectedSprite.getHeroProgression().levelUp(selectedSprite, selectedSprite.getHeroProgression().
							getLevelUpResults(selectedSprite, stateInfo.getResourceManager()), stateInfo.getResourceManager());
					selectedSprite.setExp(0);
					
				}
				timerReset();
			}
			if (healButton.handleUserInput(x, y, leftClick)) {
				selectedSprite.initializeStats();
				timerReset();
			}
			
			if (setToOneButton.handleUserInput(x, y, leftClick)) {
				selectedSprite.setCurrentHP(1);
				timerReset();
			}
		} 
		if (!choosingSprite && selectedSprite == null && stateInfo.isCombat()) {
			if (chooseSprite.handleUserInput(x, y, leftClick)) {
				choosingSprite = true;
				triggerStatus = null;
				timerReset();
			}
		}
		
		
		// stateInfo.getCamera().setLocation(x, y, stateInfo);
		triggerList.update(stateInfo.getFCGameContainer(), (int) delta);
		return super.update(delta, stateInfo);
	}

	private void timerReset() {
		inputTimer = 200;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		
		graphics.setColor(new Color(0, 0, 0, 120));
		graphics.scale(1.0f / CommRPG.GAME_SCREEN_SCALE, 1.0f / CommRPG.GAME_SCREEN_SCALE);
		if (!choosingSprite && !moveSprite) {
			graphics.fillRect(0, 0, gc.getWidth() / 2, gc.getHeight());
			triggerList.render(graphics);
		}
		
		int mouseBounds = 20;
		
		graphics.setColor(Color.yellow);
		if (choosingSprite || moveSprite)
			graphics.drawRect(mouseBounds, mouseBounds, gc.getWidth() - mouseBounds * 2, gc.getHeight() - mouseBounds * 2);
		graphics.setColor(Color.white);
		if (selectedSprite != null && !moveSprite) {
			graphics.drawString("Selected " + selectedSprite.getName() + " " + selectedSprite.getUniqueEnemyId(), 200, 15);
			moveButton.render(graphics);
			killButton.render(graphics);
			levelButton.render(graphics);
			healButton.render(graphics);
			setToOneButton.render(graphics);
		} 
		
		if (!choosingSprite && selectedSprite == null && stateInfo.isCombat()) {
			chooseSprite.render(graphics);
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
