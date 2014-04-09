package mb.fc.game.manager;

import java.awt.Point;
import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.BattleResultsMessage;
import mb.fc.engine.message.BattleSelectionMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.LocationMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.MultiSpriteContextMessage;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.state.AttackCinematicState;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.BattleResults;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.listener.KeyboardListener;
import mb.fc.game.menu.BattleActionsMenu;
import mb.fc.game.menu.HeroStatMenu;
import mb.fc.game.menu.ItemMenu;
import mb.fc.game.menu.ItemOptionMenu;
import mb.fc.game.menu.LandEffectPanel;
import mb.fc.game.menu.SpellMenu;
import mb.fc.game.move.AttackableSpace;
import mb.fc.game.move.MoveableSpace;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.turnaction.AttackSpriteAction;
import mb.fc.game.turnaction.MoveToTurnAction;
import mb.fc.game.turnaction.TargetSpriteAction;
import mb.fc.game.turnaction.TurnAction;
import mb.fc.game.turnaction.WaitAction;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

public class TurnManager extends Manager implements KeyboardListener
{		
	/**
	 * So if AI is command people it just manually adds entries to the turn actions that determine
	 * what type of battle command should be contained in the AttackSpriteAction. If this is being controlled
	 * by someone then we need to keep track of what kind of BattleCommand has been chosen via the menus
	 */
	private ArrayList<TurnAction> turnActions;
	private MoveableSpace ms;
	private AttackableSpace as;
	private CombatSprite currentSprite;	
	private Point spriteStartPoint;
	private boolean ownsSprite;
	private BattleResults battleResults;
	private BattleCommand battleCommand;	
	private SpellMenu spellMenu;
	private ItemMenu itemMenu;
	private ItemOptionMenu itemOptionMenu;
	private BattleActionsMenu battleActionsMenu;
	private LandEffectPanel landEffectPanel;
	private Rectangle cursor;
	private int updateDelta = 0;
	private static final int UPDATE_TIME = 50;
	
	// This describes the location and size of the moveable tiles array on the world
	private boolean displayMoveable = false;
	
	// Trying to draw attack coordinates
	private boolean displayAttackable = false;
	
	private boolean displayCursor = false;
	
	@Override
	public void initialize() {
		turnActions = new ArrayList<TurnAction>();	
		battleActionsMenu = new BattleActionsMenu(stateInfo);
		spellMenu = new SpellMenu(stateInfo);
		itemMenu = new ItemMenu(stateInfo);
		itemOptionMenu = new ItemOptionMenu(stateInfo);
		landEffectPanel = new LandEffectPanel();
		
		cursor = new Rectangle(0, 0, stateInfo.getTileWidth(), stateInfo.getTileHeight());
	}
	
	public void update(StateBasedGame game, int delta)
	{
		updateDelta += delta;
		if (updateDelta >= UPDATE_TIME)
		{
			updateDelta -= UPDATE_TIME;
			// If there are actions to process then handle those
			if (turnActions.size() > 0)
			{
				processTurnActions(game);
			}
			Input i = stateInfo.getGc().getInput();
		}
	}
	
	public void render(Graphics graphics)
	{
		
		if (displayMoveable)
			ms.renderMoveable(stateInfo.getGc(), stateInfo.getCamera(), graphics);
		
		if (displayAttackable)
			as.render(stateInfo.getGc(), stateInfo.getCamera(), graphics);
		
		if (displayCursor)
		{
			graphics.setColor(Color.white);
			graphics.drawRect(cursor.getX() - stateInfo.getCamera().getLocationX(), 
					cursor.getY() - stateInfo.getCamera().getLocationY(), 
						stateInfo.getTileWidth() - 1, stateInfo.getTileHeight() - 1);
		}
	}
	
	private void processTurnActions(StateBasedGame game)
	{		
		TurnAction a = turnActions.get(0);
		switch (a.action)
		{
			case TurnAction.ACTION_MOVE_CURSOR:
				if (cursor.getX() == currentSprite.getLocX() &&
					cursor.getY() == currentSprite.getLocY())
				{
					if (ownsSprite)	
					{
						stateInfo.addKeyboardListener(ms);
					}
					landEffectPanel.setLandEffect(stateInfo.getCurrentMap().getLandEffectByTile(currentSprite.getMovementType(), 
							currentSprite.getTileX(), currentSprite.getTileY()));
					stateInfo.addPanel(landEffectPanel);					
					displayMoveable = true;
					// The display cursor will toggled via the wait
					turnActions.remove(0);	
					
					stateInfo.removePanel(Panel.PANEL_HEALTH_BAR);
					currentSprite.triggerOverEvent(stateInfo);
					
					if (turnActions.size() == 0)
						displayCursor = false;
				}
				
				if (cursor.getX() < currentSprite.getLocX())
					cursor.setX(cursor.getX() + stateInfo.getTileWidth());
				else if (cursor.getX() > currentSprite.getLocX())
					cursor.setX(cursor.getX() - stateInfo.getTileWidth());
				else if (cursor.getY() < currentSprite.getLocY())
					cursor.setY(cursor.getY() + stateInfo.getTileHeight());
				else if (cursor.getY() > currentSprite.getLocY())
					cursor.setY(cursor.getY() - stateInfo.getTileHeight());
				
				stateInfo.getCamera().centerOnPoint((int) cursor.getX(), (int) cursor.getY(), stateInfo.getCurrentMap());
				break;
			case TurnAction.ACTION_MOVE_TO:
				handleSpriteMovement(a);
				break;
			case TurnAction.ACTION_WAIT:
				WaitAction wait = (WaitAction) a;
				if (wait.waitAmt > 0)
				{
					wait.waitAmt--;
				}
				else
				{
					displayCursor = false;
					turnActions.remove(0);
				}
				break; 
			case TurnAction.ACTION_END_TURN:
				// This moveable space is no longer needed to destroy it
				turnActions.remove(0);
				currentSprite.setFacing(Direction.DOWN);
				if (currentSprite.isHero())
				{
					stateInfo.checkTriggers(currentSprite.getLocX(), currentSprite.getLocY());
				}
				turnActions.clear();
				stateInfo.removePanel(landEffectPanel);
				// stateInfo.removeKeyboardListeners();
				displayAttackable = false;
				displayMoveable = false;
				cursor.setLocation(currentSprite.getLocX(), currentSprite.getLocY());
				stateInfo.sendMessage(Message.MESSAGE_NEXT_TURN);				
				break;
			case TurnAction.ACTION_HIDE_MOVE_AREA:
				displayMoveable = false;
				turnActions.remove(0);
				break;
			case TurnAction.ACTION_TARGET_SPRITE:
				TargetSpriteAction tsa = (TargetSpriteAction) a;
				this.battleCommand = tsa.getBattleCommand();
				this.displayAttackable(false);
				as.setTargetSprite(tsa.getTargetSprite());
				displayAttackable = true;
				turnActions.remove(0);
				break;
			case TurnAction.ACTION_ATTACK_SPRITE:
				stateInfo.sendMessage(new BattleResultsMessage(BattleResults.determineBattleResults(currentSprite, 
					((AttackSpriteAction) a).getTargets(),
					((AttackSpriteAction) a).getBattleCommand(), stateInfo)));
				displayAttackable = false;
				turnActions.remove(0);
				break;
			case TurnAction.ACTION_PERFORM_ATTACK:
				stateInfo.removePanel(Panel.PANEL_HEALTH_BAR);
				stateInfo.setShowAttackCinematic(true);
				AttackCinematicState acs = (AttackCinematicState) game.getState(CommRPG.STATE_GAME_BATTLE_ANIM);
				acs.setBattleInfo(currentSprite, stateInfo.getResourceManager(), 
					new Point(2, 0), battleResults, stateInfo.getGc());
				game.enterState(CommRPG.STATE_GAME_BATTLE_ANIM);
				turnActions.remove(0);				
				break;
			case TurnAction.ACTION_CHECK_DEATH:				
				if (battleResults.death)
					turnActions.add(new WaitAction(30));
				turnActions.add(new TurnAction(TurnAction.ACTION_END_TURN));
				turnActions.remove(0);
				break;
		}
	}
	
	private void handleSpriteMovement(TurnAction turnAction)
	{
		MoveToTurnAction move = (MoveToTurnAction) turnAction;
		int xDelta = 0;
		int yDelta = 0;
		
		if (move.locX > currentSprite.getLocX())
			xDelta = stateInfo.getTileWidth() / 4;					
		else if (move.locX < currentSprite.getLocX())
			xDelta = -stateInfo.getTileWidth() / 4;
		else if (move.locY > currentSprite.getLocY())
			yDelta = stateInfo.getTileWidth() / 4;
		else if (move.locY < currentSprite.getLocY())
			yDelta = -stateInfo.getTileWidth() / 4;
		
		currentSprite.setLocX(currentSprite.getLocX() + xDelta);
		currentSprite.setLocY(currentSprite.getLocY() + yDelta);
					
		// Check to see if we have arrived at our destination, if so
		// then we just remove this action and allow input for the moveablespace
		if (move.locX == currentSprite.getLocX() &&
				move.locY == currentSprite.getLocY())
		{
			turnActions.remove(0);
			if (turnActions.size() == 0)
				ms.setCheckEvents(true);
			landEffectPanel.setLandEffect(stateInfo.getCurrentMap().getLandEffectByTile(currentSprite.getMovementType(), 
					currentSprite.getTileX(), currentSprite.getTileY()));
		}
		
		
		stateInfo.getCamera().centerOnSprite(currentSprite, stateInfo.getCurrentMap());
	}
	
	private void determineMoveableSpaces()
	{
		ms = MoveableSpace.determineMoveableSpace(stateInfo, currentSprite, this.ownsSprite);
	}
	
	private void initializeCombatantTurn(CombatSprite sprite)
	{				
		stateInfo.removeKeyboardListeners();
		currentSprite = sprite;						
		stateInfo.setCurrentSprite(currentSprite);
		
		as = null;
		this.battleResults = null;
		if (ms != null)
			ms.destroy(stateInfo);
		
		spriteStartPoint = new Point(sprite.getTileX(),
				sprite.getTileY());		
		
		ownsSprite = false;
		
		// This is the first combatant to act in the battle, the cursor will
		// not have been set to any location yet, so set it on the current sprite
		if (ms == null)
			cursor.setLocation(currentSprite.getLocX(), currentSprite.getLocY());
		
		if (sprite.getAi() == null)
		{			
			// stateInfo.sendMessage(new Message(Message.MESSAGE_SHOW_BATTLEMENU));
			this.ownsSprite = true;
		}
		
		determineMoveableSpaces();
		
		// If we own this sprite then we add keyboard input listener 
		if (ownsSprite)
			stateInfo.addKeyboardListener(this);
		
		turnActions.add(new TurnAction(TurnAction.ACTION_MOVE_CURSOR));
		turnActions.add(new WaitAction(3));
		
		if (sprite.getAi() != null)
			turnActions.addAll(sprite.getAi().performAI(stateInfo, ms, currentSprite));
		
		displayCursor = true;
		
	}
	
	private void displayAttackable(boolean playerAttacking)
	{	
		displayMoveable = false;		
		
		// Determine how big the range should be
		int[][] range = null;
		int[][] area = null;
		boolean targetsHero = !currentSprite.isHero();
		int declaredRange = 0;
		
		if (battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK)
		{	
			declaredRange = currentSprite.getAttackRange();
			area = AttackableSpace.RANGE_0;
		}
		else if (battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
		{
			declaredRange = battleCommand.getSpell().getRange()[battleCommand.getLevel() - 1];			
			
			switch (battleCommand.getSpell().getArea()[battleCommand.getLevel() - 1])
			{
				case 1:
					area = AttackableSpace.RANGE_0;
					break;
				case 2:
					area = AttackableSpace.RANGE_1;
					break;
				case 3:
					area = AttackableSpace.RANGE_2;
					break;
			}
			
			if (!battleCommand.getSpell().isTargetsEnemy())
				targetsHero = currentSprite.isHero();																			
		}
		else if (battleCommand.getCommand() == BattleCommand.COMMAND_ITEM)
		{
			declaredRange = battleCommand.getItem().getItemUse().getRange();			
			
			switch (battleCommand.getItem().getItemUse().getArea())
			{
				case 1:
					area = AttackableSpace.RANGE_0;
					break;
				case 2:
					area = AttackableSpace.RANGE_1;
					break;
				case 3:
					area = AttackableSpace.RANGE_2;
					break;
			}
			
			if (!battleCommand.getItem().getItemUse().isTargetsEnemy())
				targetsHero = currentSprite.isHero();																	
		}
		
		switch (declaredRange)
		{
			case 1:
				range = AttackableSpace.RANGE_1;
				break;
			case 2:
				range = AttackableSpace.RANGE_2;
				break;
			case 3:
				range = AttackableSpace.RANGE_3;
				break;
			case EquippableItem.RANGE_BOW_2_NO_1:
				range = AttackableSpace.RANGE_2_1;
				break;
		}
		
		as = new AttackableSpace(stateInfo, currentSprite, targetsHero, range, area);
		
		if (playerAttacking)
			stateInfo.addKeyboardListener(as);
		
		displayAttackable = true;
	}

	@Override
	public void recieveMessage(Message message) {		
		switch (message.getMessageType())
		{
			case Message.MESSAGE_SHOW_BATTLEMENU:
				displayMoveable = false;
				displayAttackable = false;		
				battleActionsMenu.initialize(stateInfo);
				stateInfo.addMenu(battleActionsMenu);
				break;
			case Message.MESSAGE_SHOW_MOVEABLE:
				displayMoveable = true;
				break;
			case Message.MESSAGE_SHOW_SPELLMENU:
				spellMenu.initialize(stateInfo);
				stateInfo.addMenu(spellMenu);
				break;
			case Message.MESSAGE_SHOW_ITEM_MENU:
				itemMenu.initialize(stateInfo);
				stateInfo.addMenu(itemMenu);
				break;
			case Message.MESSAGE_SHOW_ITEM_OPTION_MENU:				
				itemOptionMenu.initialize(((IntMessage) message).getValue(), stateInfo);
				stateInfo.addMenu(itemOptionMenu);
				break;
			// THIS IS SENT BY THE OWNER
			case Message.MESSAGE_ATTACK_PRESSED:
				battleCommand = new BattleCommand(BattleCommand.COMMAND_ATTACK);
				displayAttackable(true);				
				break;
			// This message should never be sent by AI
			// THIS IS SENT BY THE OWNER
			case Message.MESSAGE_TARGET_SPRITE:
				displayAttackable = false;
				displayMoveable = true;				
				
				// At this point we know who we intend to target, but we need to inject the BattleCommand.
				// Only the owner will have a value for the battle command so they will have to be
				// the one to send the BattleResults
				turnActions.add(new AttackSpriteAction(TurnAction.ACTION_ATTACK_SPRITE, 
						((MultiSpriteContextMessage) message).getSprites(), battleCommand));
				break;
			// THIS IS SENT BY THE OWNER
			case Message.MESSAGE_SELECT_SPELL:
				BattleSelectionMessage bsm = (BattleSelectionMessage) message;
				battleCommand = new BattleCommand(BattleCommand.COMMAND_SPELL, 
						currentSprite.getSpellsDescriptors().get(bsm.getSelectionIndex()).getSpell(), bsm.getLevel());				
				displayAttackable(true);
				break;
			case Message.MESSAGE_SELECT_ITEM:
				BattleSelectionMessage ibsm = (BattleSelectionMessage) message;
				battleCommand = new BattleCommand(BattleCommand.COMMAND_ITEM, 
						currentSprite.getItem(ibsm.getSelectionIndex()));				
				displayAttackable(true);
				break;
			case Message.MESSAGE_COMBATANT_TURN:
				initializeCombatantTurn((CombatSprite) ((SpriteContextMessage) message).getSprite());
				break;
			case Message.MESSAGE_RESET_SPRITELOC:				
				// This right here is my biggest fear using the keyboard, while holding button 2 in battle
				// it's possible for 
				if (spriteStartPoint.x == currentSprite.getTileX() && 
						spriteStartPoint.y == currentSprite.getTileY())
				{
					// If we are already reset then switch to cursor mode
					displayMoveable = false;
					displayCursor = true;
					stateInfo.removePanel(landEffectPanel);
					stateInfo.removeKeyboardListener();
				}
				else
				{
					ms.setCheckEvents(false);
					ms.addMoveActionsToLocation(spriteStartPoint.x, spriteStartPoint.y, currentSprite, turnActions);
				}
				break;
			case Message.MESSAGE_MOVETO_SPRITELOC:				
				ms.setCheckEvents(false);
				
				MoveToTurnAction mtta = new MoveToTurnAction(((LocationMessage) message).locX, 
						((LocationMessage) message).locY);
				turnActions.add(mtta);
				
				// Grab the final TurnAction, it should be a MoveToTurnAction. Process the first move
				handleSpriteMovement(turnActions.get(turnActions.size() - 1));
				break;
			case Message.MESSAGE_HIDE_ATTACKABLE:
				displayAttackable = false;
				displayMoveable = true;
				
				if (ownsSprite)
					stateInfo.sendMessage(Message.MESSAGE_SHOW_BATTLEMENU);
				break;			
			case Message.MESSAGE_BATTLE_RESULTS:
				battleResults = ((BattleResultsMessage) message).getBattleResults();
				turnActions.add(new AttackSpriteAction(TurnAction.ACTION_PERFORM_ATTACK, battleResults.targets, battleResults.battleCommand));
				turnActions.add(new TurnAction(TurnAction.ACTION_CHECK_DEATH));								
				break;
			case Message.MESSAGE_PLAYER_END_TURN:
				turnActions.add(new TurnAction(TurnAction.ACTION_END_TURN));
				break;
			
		}
	}

	@Override
	public boolean handleKeyboardInput(FCInput input, StateInfo stateInfo) 
	{
		if (turnActions.size() == 0)
		{
			boolean moved = false;
			if (input.isKeyDown(KeyMapping.BUTTON_1))
			{
				
			}
			else if (input.isKeyDown(KeyMapping.BUTTON_2))
			{
				turnActions.add(new TurnAction(TurnAction.ACTION_MOVE_CURSOR));
				return true;
			}
			else if (input.isKeyDown(KeyMapping.BUTTON_3))
			{
				// Get any combat sprite at the cursors location
				CombatSprite cs = stateInfo.getCombatSpriteAtMapLocation((int) cursor.getX(), (int) cursor.getY(), null);
				
				// if there is a combat sprite here display it's health panel
				if (cs != null)
				{
					stateInfo.addMenu(new HeroStatMenu(stateInfo.getGc(), cs, stateInfo));
					return true;
				}
			}
			else if (input.isKeyDown(KeyMapping.BUTTON_UP))
			{
				if (cursor.getY() > 0)
				{
					cursor.setY(cursor.getY() - stateInfo.getTileHeight());
					moved = true;
				}
			}
			else if (input.isKeyDown(KeyMapping.BUTTON_DOWN))
			{
				if (cursor.getY() + stateInfo.getTileHeight() < stateInfo.getCurrentMap().getMapHeightInPixels())
				{
					cursor.setY(cursor.getY() + stateInfo.getTileHeight());
					moved = true;
				}
			}
			else if (input.isKeyDown(KeyMapping.BUTTON_LEFT))
			{
				if (cursor.getX() > 0)
				{
					cursor.setX(cursor.getX() - stateInfo.getTileWidth());
					moved = true;
				}
			}
			else if (input.isKeyDown(KeyMapping.BUTTON_RIGHT))
			{
				if (cursor.getX() + stateInfo.getTileWidth() < stateInfo.getCurrentMap().getMapWidthInPixels())
				{
					cursor.setX(cursor.getX() + stateInfo.getTileWidth());
					moved = true;
				}
			}
			
			if (moved)
			{
				// Remove any health bar panels that may have been displayed from a sprite that we were previously over
				stateInfo.removePanel(Panel.PANEL_HEALTH_BAR);
				stateInfo.getCamera().centerOnPoint((int) cursor.getX(), (int) cursor.getY(), stateInfo.getCurrentMap());
				// Get any combat sprite at the cursors location
				CombatSprite cs = stateInfo.getCombatSpriteAtMapLocation((int) cursor.getX(), (int) cursor.getY(), null);
				
				// if there is a combat sprite here display it's health panel
				if (cs != null)
					cs.triggerOverEvent(stateInfo);
				stateInfo.setInputDelay(System.currentTimeMillis() + 70);
			}
		}
		
		return false;
	}
}
