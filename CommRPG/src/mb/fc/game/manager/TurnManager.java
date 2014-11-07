package mb.fc.game.manager;

import java.awt.Point;
import java.util.ArrayList;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleResultsMessage;
import mb.fc.engine.message.BattleSelectionMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.LocationMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.SpriteContextMessage;
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
import mb.fc.game.move.MovingSprite;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.turnaction.AttackSpriteAction;
import mb.fc.game.turnaction.MoveToTurnAction;
import mb.fc.game.turnaction.PerformAttackAction;
import mb.fc.game.turnaction.TargetSpriteAction;
import mb.fc.game.turnaction.TurnAction;
import mb.fc.game.turnaction.WaitAction;
import mb.jython.GlobalPythonFactory;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
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
	private MovingSprite movingSprite;
	private Point spriteStartPoint;
	private boolean ownsSprite;
	private BattleResults battleResults;
	private BattleCommand battleCommand;
	private SpellMenu spellMenu;
	private ItemMenu itemMenu;
	private ItemOptionMenu itemOptionMenu;
	private BattleActionsMenu battleActionsMenu;
	private LandEffectPanel landEffectPanel;
	private Image cursorImage;
	private Rectangle cursor;
	private int cursorTargetX, cursorTargetY;
	private int updateDelta = 0;
	private boolean resetSpriteLoc = false;
	private boolean turnManagerHasFocus = false;
	private int activeCharFlashDelta = 0;
	private static final int UPDATE_TIME = 20;

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
		movingSprite = null;

		cursorImage = stateInfo.getResourceManager().getImages().get("battlecursor");
		cursor = new Rectangle(0, 0, stateInfo.getTileWidth(), stateInfo.getTileHeight());
	}

	public void update(StateBasedGame game, int delta)
	{
		updateDelta += delta;
		while (updateDelta >= UPDATE_TIME)
		{
			updateDelta -= UPDATE_TIME;
			// If there are actions to process then handle those
			if (turnActions.size() > 0)
			{
				processTurnActions(game);
			}

			if (displayAttackable && !currentSprite.isHero())
				as.update(stateInfo);
		}

		if (turnManagerHasFocus)
		{
			activeCharFlashDelta += delta;

			if (activeCharFlashDelta > 500)
			{
				activeCharFlashDelta -= 500;
				currentSprite.setVisible(!currentSprite.isVisible());
			}
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
			cursorImage.draw(cursor.getX() - stateInfo.getCamera().getLocationX() + stateInfo.getGc().getDisplayPaddingX(),
					cursor.getY() - stateInfo.getCamera().getLocationY());
			/*
			graphics.setColor(Color.white);
			graphics.drawRect(cursor.getX() - stateInfo.getCamera().getLocationX() + stateInfo.getGc().getDisplayPaddingX(),
					cursor.getY() - stateInfo.getCamera().getLocationY(),
						stateInfo.getTileWidth() - 1, stateInfo.getTileHeight() - 1);
						*/
		}
	}

	private void processTurnActions(StateBasedGame game)
	{
		TurnAction a = turnActions.get(0);
		switch (a.action)
		{
			case TurnAction.ACTION_MANUAL_MOVE_CURSOR:

				if (cursor.getX() < cursorTargetX)
					cursor.setX(cursor.getX() + stateInfo.getTileWidth() / 6);
				else if (cursor.getX() > cursorTargetX)
					cursor.setX(cursor.getX() - stateInfo.getTileWidth() / 6);

				if (cursor.getY() < cursorTargetY)
					cursor.setY(cursor.getY() + stateInfo.getTileHeight() / 6);
				else if (cursor.getY() > cursorTargetY)
					cursor.setY(cursor.getY() - stateInfo.getTileHeight() / 6);

				stateInfo.getCamera().centerOnPoint((int) cursor.getX(), (int) cursor.getY(), stateInfo.getCurrentMap());

				if (cursorTargetX == cursor.getX() && cursorTargetY == cursor.getY())
				{
					// Get any combat sprite at the cursors location
					CombatSprite cs = stateInfo.getCombatSpriteAtMapLocation((int) cursor.getX(), (int) cursor.getY(), null);

					// if there is a combat sprite here display it's health panel
					if (cs != null)
					{
						stateInfo.removePanel(Panel.PANEL_HEALTH_BAR);
						cs.triggerOverEvent(stateInfo);
						landEffectPanel.setLandEffect(stateInfo.getCurrentMap().getLandEffectByTile(cs.getMovementType(),
								cs.getTileX(), cs.getTileY()));
						stateInfo.addSingleInstancePanel(landEffectPanel);
					}
					else
					{
						stateInfo.removePanel(landEffectPanel);
						// Remove any health bar panels that may have been displayed from a sprite that we were previously over
						stateInfo.removePanel(Panel.PANEL_HEALTH_BAR);
					}
					turnActions.remove(0);
				}
				break;
			case TurnAction.ACTION_MOVE_CURSOR_TO_ACTOR:
				if (cursor.getX() == currentSprite.getLocX() &&
					cursor.getY() == currentSprite.getLocY())
				{
					if (ownsSprite)
					{
						stateInfo.addKeyboardListener(ms);
					}
					landEffectPanel.setLandEffect(stateInfo.getCurrentMap().getLandEffectByTile(currentSprite.getMovementType(),
							currentSprite.getTileX(), currentSprite.getTileY()));
					stateInfo.addSingleInstancePanel(landEffectPanel);
					displayMoveable = true;
					// The display cursor will toggled via the wait
					turnActions.remove(0);

					stateInfo.removePanel(Panel.PANEL_HEALTH_BAR);
					currentSprite.triggerOverEvent(stateInfo);

					if (turnActions.size() == 0)
						displayCursor = false;
				}

				if (cursor.getX() < currentSprite.getLocX())
					cursor.setX(cursor.getX() + stateInfo.getTileWidth() / 4);
				else if (cursor.getX() > currentSprite.getLocX())
					cursor.setX(cursor.getX() - stateInfo.getTileWidth() / 4);

				if (cursor.getY() < currentSprite.getLocY())
					cursor.setY(cursor.getY() + stateInfo.getTileHeight() / 4);
				else if (cursor.getY() > currentSprite.getLocY())
					cursor.setY(cursor.getY() - stateInfo.getTileHeight() / 4);

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
					stateInfo.checkTriggers(currentSprite.getLocX(), currentSprite.getLocY(), false);
				}
				turnActions.add(new TurnAction(TurnAction.ACTION_DISPLAY_SPEECH));
				break;
			case TurnAction.ACTION_HIDE_MOVE_AREA:
				displayMoveable = false;
				turnActions.remove(0);
				break;
			case TurnAction.ACTION_TARGET_SPRITE:
				TargetSpriteAction tsa = (TargetSpriteAction) a;
				this.battleCommand = tsa.getBattleCommand();
				this.determineAttackbleSpace(false);
				as.setTargetSprite(tsa.getTargetSprite(), stateInfo);
				displayAttackable = true;
				turnActions.remove(0);
				break;
			case TurnAction.ACTION_ATTACK_SPRITE:
				if (a.perform(this, stateInfo))
					turnActions.remove(0);
				break;
			case TurnAction.ACTION_PERFORM_ATTACK:
				a.perform(this, stateInfo);
				turnActions.remove(0);
				break;
			case TurnAction.ACTION_CHECK_DEATH:
				if (battleResults.death)
					turnActions.add(new WaitAction(1500 / UPDATE_TIME));
				turnActions.add(new TurnAction(TurnAction.ACTION_END_TURN));
				turnActions.remove(0);
				break;
			case TurnAction.ACTION_DISPLAY_SPEECH:
				if (!stateInfo.isMenuDisplayed(Panel.PANEL_SPEECH))
				{
					System.out.println("NOT DISPLAYED");
					turnActions.remove(0);
					stateInfo.removePanel(landEffectPanel);
					stateInfo.removePanel(Panel.PANEL_HEALTH_BAR);
					stateInfo.removePanel(Panel.PANEL_ENEMY_HEALTH_BAR);
					// stateInfo.removeKeyboardListeners();
					displayAttackable = false;
					displayMoveable = false;
					cursor.setLocation(currentSprite.getLocX(), currentSprite.getLocY());
					stateInfo.sendMessage(Message.MESSAGE_NEXT_TURN);
				}
				break;
		}
	}

	private void handleSpriteMovement(TurnAction turnAction)
	{
		if (movingSprite == null)
		{
			MoveToTurnAction move = (MoveToTurnAction) turnAction;

			Direction dir = Direction.UP;

			if (move.locX > currentSprite.getLocX())
				dir = Direction.RIGHT;
			else if (move.locX < currentSprite.getLocX())
				dir = Direction.LEFT;
			else if (move.locY > currentSprite.getLocY())
				dir = Direction.DOWN;
			else if (move.locY < currentSprite.getLocY())
				dir = Direction.UP;
			else
			{
				turnActions.remove(0);
				if (turnActions.size() == 0)
				{
					ms.setCheckEvents(true);
					if (resetSpriteLoc)
					{
						resetSpriteLoc = false;
						currentSprite.setFacing(Direction.DOWN);
						// If we are already reset then switch to cursor mode
						setToCursorMode();
					}
				}
				landEffectPanel.setLandEffect(stateInfo.getCurrentMap().getLandEffectByTile(currentSprite.getMovementType(),
						currentSprite.getTileX(), currentSprite.getTileY()));
				movingSprite = null;
				return;
			}

			movingSprite = new MovingSprite(currentSprite, dir, stateInfo);
		}

		// Check to see if we have arrived at our destination, if so
		// then we just remove this action and allow input for the moveablespace
		if (movingSprite.update(resetSpriteLoc))
		{
			turnActions.remove(0);
			if (turnActions.size() == 0)
			{
				ms.setCheckEvents(true);
				if (resetSpriteLoc)
				{
					resetSpriteLoc = false;
					currentSprite.setFacing(Direction.DOWN);
					// If we are already reset then switch to cursor mode
					setToCursorMode();
				}
			}
			landEffectPanel.setLandEffect(stateInfo.getCurrentMap().getLandEffectByTile(currentSprite.getMovementType(),
					currentSprite.getTileX(), currentSprite.getTileY()));
			movingSprite = null;
		}
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

		turnActions.add(new TurnAction(TurnAction.ACTION_MOVE_CURSOR_TO_ACTOR));
		turnActions.add(new WaitAction(150 / UPDATE_TIME));

		if (sprite.getAi() != null)
			turnActions.addAll(sprite.getAi().performAI(stateInfo, ms, currentSprite));

		displayCursor = true;

	}

	private void determineAttackbleSpace(boolean playerAttacking)
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

	private void setToCursorMode()
	{
		// If we are already reset then switch to cursor mode
		displayMoveable = false;
		displayCursor = true;
		stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, GlobalPythonFactory.createJMusicSelector().getMenuAddedSoundEffect(), 1f, false));
		stateInfo.removeKeyboardListener();
		this.turnManagerHasFocus = true;
	}

	@Override
	public void recieveMessage(Message message) {
		switch (message.getMessageType())
		{
			case Message.MESSAGE_SHOW_BATTLEMENU:
				displayMoveable = false;
				displayAttackable = false;
				battleActionsMenu.initialize();
				stateInfo.addMenu(battleActionsMenu);
				break;
			case Message.MESSAGE_SHOW_MOVEABLE:
				displayMoveable = true;
				break;
			case Message.MESSAGE_SHOW_SPELLMENU:
				spellMenu.initialize();
				stateInfo.addMenu(spellMenu);
				break;
			case Message.MESSAGE_SHOW_ITEM_MENU:
				itemMenu.initialize();
				stateInfo.addMenu(itemMenu);
				break;
			case Message.MESSAGE_SHOW_ITEM_OPTION_MENU:
				itemOptionMenu.initialize(((IntMessage) message).getValue());
				stateInfo.addMenu(itemOptionMenu);
				break;
			// THIS IS SENT BY THE OWNER
			case Message.MESSAGE_ATTACK_PRESSED:
				battleCommand = new BattleCommand(BattleCommand.COMMAND_ATTACK);
				determineAttackbleSpace(true);
				break;
			// This message should never be sent by AI
			// THIS IS SENT BY THE OWNER
			case Message.MESSAGE_TARGET_SPRITE:
				displayAttackable = false;
				displayMoveable = true;

				// At this point we know who we intend to target, but we need to inject the BattleCommand.
				// Only the owner will have a value for the battle command so they will have to be
				// the one to send the BattleResults
				turnActions.add(new AttackSpriteAction(((SpriteContextMessage) message).getSprites(), battleCommand));
				break;
			// THIS IS SENT BY THE OWNER
			case Message.MESSAGE_SELECT_SPELL:
				BattleSelectionMessage bsm = (BattleSelectionMessage) message;
				battleCommand = new BattleCommand(BattleCommand.COMMAND_SPELL,
						currentSprite.getSpellsDescriptors().get(bsm.getSelectionIndex()).getSpell(), bsm.getLevel());
				determineAttackbleSpace(true);
				break;
			case Message.MESSAGE_SELECT_ITEM:
				BattleSelectionMessage ibsm = (BattleSelectionMessage) message;
				battleCommand = new BattleCommand(BattleCommand.COMMAND_ITEM,
						currentSprite.getItem(ibsm.getSelectionIndex()));
				determineAttackbleSpace(true);
				break;
			case Message.MESSAGE_COMBATANT_TURN:
				initializeCombatantTurn(((SpriteContextMessage) message).getSprite());
				break;
			case Message.MESSAGE_RESET_SPRITELOC:
				if (spriteStartPoint.x == currentSprite.getTileX() &&
						spriteStartPoint.y == currentSprite.getTileY())
				{
					// If we are already reset then switch to cursor mode
					setToCursorMode();
				}
				else
				{
					ms.setCheckEvents(false);
					ms.addMoveActionsToLocation(spriteStartPoint.x, spriteStartPoint.y, currentSprite, turnActions);
					this.resetSpriteLoc = true;
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
				stateInfo.setPlayingMusicPostion(stateInfo.getPlayingMusic().getPosition());
				stateInfo.getPlayingMusic().fade(250, 0f, true);
				System.out.println("PLAYING POSITION " + stateInfo.getPlayingMusicPostion());
				// turnActions.add(new WaitAction(15));
				turnActions.add(new PerformAttackAction(battleResults));
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
				this.turnManagerHasFocus = false;
				currentSprite.setVisible(true);
				turnActions.add(new TurnAction(TurnAction.ACTION_MOVE_CURSOR_TO_ACTOR));
				stateInfo.removePanel(landEffectPanel);
				stateInfo.removePanel(Panel.PANEL_HEALTH_BAR);
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

			cursorTargetX = (int) cursor.getX();
			cursorTargetY = (int) cursor.getY();

			if (input.isKeyDown(KeyMapping.BUTTON_UP))
			{
				if (cursor.getY() > 0)
				{
					cursorTargetY = (int) (cursor.getY() - stateInfo.getTileHeight());
					moved = true;
				}
			}
			else if (input.isKeyDown(KeyMapping.BUTTON_DOWN))
			{
				if (cursor.getY() + stateInfo.getTileHeight() < stateInfo.getCurrentMap().getMapHeightInPixels())
				{
					cursorTargetY = (int) (cursor.getY() + stateInfo.getTileHeight());
					moved = true;
				}
			}

			if (!stateInfo.getResourceManager().getMap().isInBattleRegion(cursorTargetX, cursorTargetY))
			{
				cursorTargetY = (int) cursor.getY();
				moved = false;
			}

			if (input.isKeyDown(KeyMapping.BUTTON_LEFT))
			{
				if (cursor.getX() > 0)
				{
					cursorTargetX = (int) (cursor.getX() - stateInfo.getTileWidth());
					moved = true;
				}
			}
			else if (input.isKeyDown(KeyMapping.BUTTON_RIGHT))
			{
				if (cursor.getX() + stateInfo.getTileWidth() < stateInfo.getCurrentMap().getMapWidthInPixels())
				{
					cursorTargetX = (int) (cursor.getX() + stateInfo.getTileWidth());
					moved = true;
				}
			}

			if (!stateInfo.getResourceManager().getMap().isInBattleRegion(cursorTargetX, cursorTargetY))
			{
				cursorTargetX = (int) cursor.getX();
				moved = false;
			}

			if (moved)
			{
				turnActions.add(new TurnAction(TurnAction.ACTION_MANUAL_MOVE_CURSOR));
			}
		}

		return false;
	}

	public CombatSprite getCurrentSprite() {
		return currentSprite;
	}

	public void setDisplayAttackable(boolean displayAttackable) {
		this.displayAttackable = displayAttackable;
	}
}
