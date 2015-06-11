package mb.fc.game.manager;

import java.awt.Point;
import java.util.ArrayList;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleResultsMessage;
import mb.fc.engine.message.BattleSelectionMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.LocationMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.message.TurnActionsMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.BattleResults;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.constants.Direction;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.listener.KeyboardListener;
import mb.fc.game.menu.BattleActionsMenu;
import mb.fc.game.menu.HeroStatMenu;
import mb.fc.game.menu.ItemMenu;
import mb.fc.game.menu.ItemOptionMenu;
import mb.fc.game.menu.LandEffectPanel;
import mb.fc.game.menu.SpeechMenu;
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
import mb.jython.JBattleEffect;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

public class TurnManager extends Manager implements KeyboardListener
{
	private static final int UPDATE_TIME = 20;

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
	private int activeCharFlashDelta = 0;

	private boolean ownsSprite;
	private boolean resetSpriteLoc = false;
	private boolean turnManagerHasFocus = false;
	private boolean displayOverCursor = false;
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
		ms = null;
		as = null;
		currentSprite = null;
		spriteStartPoint = null;
		battleResults = null;
		battleCommand = null;
		displayMoveable = displayAttackable = displayCursor = displayOverCursor =
				turnManagerHasFocus = resetSpriteLoc = ownsSprite = false;

		cursorImage = stateInfo.getResourceManager().getImages().get("battlecursor");
		cursor = new Rectangle(0, 0, stateInfo.getTileWidth(), stateInfo.getTileHeight());
		cursorTargetX = cursorTargetY = updateDelta = activeCharFlashDelta = 0;
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

	public void renderCursor(Graphics graphics)
	{
		if (displayCursor && displayOverCursor)
		{
			cursorImage.draw(cursor.getX() - stateInfo.getCamera().getLocationX() + stateInfo.getGc().getDisplayPaddingX(),
					cursor.getY() - stateInfo.getCamera().getLocationY(), new Color(1f, 1f, 1f, .5f));
		}
	}

	private void processTurnActions(StateBasedGame game)
	{
		if (turnActions.size() == 0)
			return;
		TurnAction a = turnActions.get(0);
		switch (a.action)
		{
			case TurnAction.ACTION_MANUAL_MOVE_CURSOR:

				// Get any combat sprite at the cursors location
				if (stateInfo.getCombatSpriteAtMapLocation(cursorTargetX, cursorTargetY, null) != null)
				{
					displayOverCursor = false;
				}

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
						displayOverCursor = true;
						stateInfo.removePanel(landEffectPanel);
						// Remove any health bar panels that may have been displayed from a sprite that we were previously over
						stateInfo.removePanel(Panel.PANEL_HEALTH_BAR);
					}
					turnActions.remove(0);
				}
				break;
			case TurnAction.ACTION_MOVE_CURSOR_TO_ACTOR:
				this.displayOverCursor = true;

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

				if (currentSprite.getBattleEffects().size() > 0)
				{
					String text = "";
					for (int i = 0; i < currentSprite.getBattleEffects().size(); i++)
					{
						JBattleEffect be = currentSprite.getBattleEffects().get(i);
						text = text + be.performEffect(currentSprite);
						if (i + 1 == currentSprite.getBattleEffects().size())
							text += "]";
						else
							text += "} ";

						if (be.isDone())
						{
							currentSprite.getBattleEffects().remove(i--);
							be.effectEnded(currentSprite);
						}
					}

					stateInfo.addMenu(new SpeechMenu(text, stateInfo));
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
				this.determineAttackableSpace(false);
				as.setTargetSprite(tsa.getTargetSprite(stateInfo.getCombatSprites()), stateInfo);
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
				{
					turnActions.add(new WaitAction(2000 / UPDATE_TIME));
				}
				turnActions.add(new TurnAction(TurnAction.ACTION_END_TURN));
				turnActions.remove(0);
				break;
			case TurnAction.ACTION_DISPLAY_SPEECH:
				if (!stateInfo.isMenuDisplayed(Panel.PANEL_SPEECH))
				{
					turnActions.remove(0);
					stateInfo.removePanel(landEffectPanel);
					stateInfo.removePanel(Panel.PANEL_HEALTH_BAR);
					stateInfo.removePanel(Panel.PANEL_ENEMY_HEALTH_BAR);
					// stateInfo.removeKeyboardListeners();
					displayAttackable = false;
					displayMoveable = false;
					cursor.setLocation(currentSprite.getLocX(), currentSprite.getLocY());
					stateInfo.sendMessage(MessageType.NEXT_TURN, true);
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

		spriteStartPoint = new Point(sprite.getTileX(),
				sprite.getTileY());

		ownsSprite = false;

		// This is the first combatant to act in the battle, the cursor will
		// not have been set to any location yet, so set it on the current sprite
		if (ms == null)
			cursor.setLocation(currentSprite.getLocX(), currentSprite.getLocY());

		if (sprite.isHero() && sprite.getClientId() == stateInfo.getPsi().getClientId())
		{
			// stateInfo.sendMessage(new Message(MessageType.SHOW_BATTLEMENU));
			this.ownsSprite = true;
		}

		determineMoveableSpaces();


		turnActions.add(new TurnAction(TurnAction.ACTION_MOVE_CURSOR_TO_ACTOR));
		turnActions.add(new WaitAction(150 / UPDATE_TIME));

		if (sprite.getAi() != null)
		{
			Log.debug("Perform AI for " + sprite.getName());
			stateInfo.sendMessage(new TurnActionsMessage(false, sprite.getAi().performAI(stateInfo, ms, currentSprite)), true);
		}
		// If we own this sprite then we add keyboard input listener
		else if (ownsSprite)
			stateInfo.addKeyboardListener(this);
		displayCursor = true;
		displayOverCursor = false;
	}

	private void determineAttackableSpace(boolean playerAttacking)
	{
		displayMoveable = false;

		// Determine how big the range should be
		int[][] range = null;
		int[][] area = null;
		boolean targetsHero = !currentSprite.isHero();

		if (battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK)
		{
			range = currentSprite.getAttackRange().getAttackableSpace();
			area = AttackableSpace.RANGE_0;
		}
		else if (battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
		{
			range = battleCommand.getSpell().getRange()[battleCommand.getLevel() - 1].getAttackableSpace();

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
			range = battleCommand.getItem().getItemUse().getRange().getAttackableSpace();

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

		as = new AttackableSpace(stateInfo, currentSprite, targetsHero, range, area);
		if (as.getTargetAmount() == 0)
		{
			stateInfo.sendMessage(MessageType.SHOW_BATTLEMENU);
		}
		else
		{
			if (playerAttacking && currentSprite.getClientId() == stateInfo.getPsi().getClientId())
				stateInfo.addKeyboardListener(as);

			displayAttackable = true;
		}
	}

	private void setToCursorMode()
	{
		if (!ownsSprite)
			return;

		// If we are already reset then switch to cursor mode
		displayMoveable = false;
		displayCursor = true;
		displayOverCursor = false;
		stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, GlobalPythonFactory.createJMusicSelector().getMenuAddedSoundEffect(), 1f, false));
		stateInfo.removeKeyboardListener();
		this.turnManagerHasFocus = true;
	}

	@Override
	public void recieveMessage(Message message) {
		switch (message.getMessageType())
		{
			case SHOW_BATTLEMENU:
				displayMoveable = false;
				displayAttackable = false;
				battleActionsMenu.initialize();
				stateInfo.addMenu(battleActionsMenu);
				break;
			case SHOW_MOVEABLE:
				displayMoveable = true;
				break;
			case SHOW_SPELLMENU:
				spellMenu.initialize();
				stateInfo.addMenu(spellMenu);
				break;
			case SHOW_ITEM_MENU:
				itemMenu.initialize();
				stateInfo.addMenu(itemMenu);
				break;
			case SHOW_ITEM_OPTION_MENU:
				itemOptionMenu.initialize(((IntMessage) message).getValue());
				stateInfo.addMenu(itemOptionMenu);
				break;
			// THIS IS SENT BY THE OWNER
			case ATTACK_PRESSED:
				battleCommand = new BattleCommand(BattleCommand.COMMAND_ATTACK);
				determineAttackableSpace(true);
				break;
			// This message should never be sent by AI
			// THIS IS SENT BY THE OWNER
			case TARGET_SPRITE:
				displayAttackable = false;
				displayMoveable = true;

				// At this point we know who we intend to target, but we need to inject the BattleCommand.
				// Only the owner will have a value for the battle command so they will have to be
				// the one to send the BattleResults
				turnActions.add(new AttackSpriteAction(((SpriteContextMessage) message).getSprites(stateInfo.getSprites()), battleCommand));
				break;
			// THIS IS SENT BY THE OWNER
			case SELECT_SPELL:
				BattleSelectionMessage bsm = (BattleSelectionMessage) message;
				battleCommand = new BattleCommand(BattleCommand.COMMAND_SPELL,
						currentSprite.getSpellsDescriptors().get(bsm.getSelectionIndex()).getSpell(),
						currentSprite.getSpellsDescriptors().get(bsm.getSelectionIndex()), bsm.getLevel());
				determineAttackableSpace(true);
				break;
			case SELECT_ITEM:
				BattleSelectionMessage ibsm = (BattleSelectionMessage) message;
				battleCommand = new BattleCommand(BattleCommand.COMMAND_ITEM,
						currentSprite.getItem(ibsm.getSelectionIndex()));
				determineAttackableSpace(true);
				break;
			case COMBATANT_TURN:
				initializeCombatantTurn(((SpriteContextMessage) message).getSprite(stateInfo.getSprites()));
				break;
			case RESET_SPRITELOC:
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
			case MOVETO_SPRITELOC:
				ms.setCheckEvents(false);

				MoveToTurnAction mtta = new MoveToTurnAction(((LocationMessage) message).locX,
						((LocationMessage) message).locY);

				turnActions.add(mtta);

				// Grab the final TurnAction, it should be a MoveToTurnAction. Process the first move
				handleSpriteMovement(turnActions.get(turnActions.size() - 1));
				break;
			case HIDE_ATTACKABLE:
				displayAttackable = false;
				displayMoveable = true;

				if (ownsSprite)
					stateInfo.sendMessage(MessageType.SHOW_BATTLEMENU);
				break;
			case BATTLE_RESULTS:
				battleResults = ((BattleResultsMessage) message).getBattleResults();
				battleResults.initialize(stateInfo);
				ArrayList<CombatSprite> transposedTargets = new ArrayList<>();
				for (CombatSprite oldTargets : battleResults.targets)
				{
					for (CombatSprite cs : stateInfo.getCombatSprites())
					{
						if (oldTargets.getId() == cs.getId())
						{
							transposedTargets.add(cs);
							break;
						}
					}
				}
				battleResults.targets = transposedTargets;
				stateInfo.sendMessage(MessageType.PAUSE_MUSIC);

				turnActions.add(new PerformAttackAction(battleResults));
				turnActions.add(new TurnAction(TurnAction.ACTION_CHECK_DEATH));
				break;
			case PLAYER_END_TURN:
				turnActions.add(new TurnAction(TurnAction.ACTION_END_TURN));
				break;
			case TURN_ACTIONS:
				TurnActionsMessage tam = (TurnActionsMessage) message;
				turnActions.addAll(tam.getTurnActions());
				break;
			case HIDE_ATTACK_AREA:
				displayAttackable = false;
				break;
			default:
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
