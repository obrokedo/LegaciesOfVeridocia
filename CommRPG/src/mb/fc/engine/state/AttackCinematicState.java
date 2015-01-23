package mb.fc.engine.state;

import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.game.Timer;
import mb.fc.game.battle.BattleResults;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.hudmenu.SpriteContextPanel;
import mb.fc.game.input.FCInput;
import mb.fc.game.menu.Menu.MenuUpdate;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.utils.AnimSprite;
import mb.fc.utils.Animation;
import mb.fc.utils.SpriteAnims;
import mb.jython.GlobalPythonFactory;
import mb.jython.JMusicSelector;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

/**
 * A warning to anyone who wants to figure out how the battle cinematics work,
 * this code is EXTREMELY difficult to follow.
 *
 * PRE ATTACK STATE
 * The attacking cinematic starts in the pre attack state where the attacker will be
 * displayed. The (first) target will be displayed if it is NOT an ally. The text describing the
 * attackers action will also be displayed. Clicking will move to the ATTACKING state
 *
 * ATTACKING STATE
 * The attacking state will display the attacker and the target if it is not an ally. No
 * text will be displayed. One frame before the final frame in the attack animation damage will
 * be dealt to the first target and the first target will turn red and shake if it takes damage from the attack.
 * On the final attack frame if the targets are enemies then the state will transition to the climax state, if the
 * targets are enemies then we change to the transition attacker out state
 *
 *  TRANSITION OUT STATE
 *  This state will only happen if the targets are allies. The attacker will move off of the screen,
 *  once they are off the screen then we will transition to the climax state
 *
 *  CLIMAX STATE
 *  This state will display the attacker and target if they are enemies or just the target if they are allies.
 *  It will also display the battle result for this target.
 *  The first target will have already taken damage at this point (or whatever action). This state waits for a
 *  click to progress. Once a click happens, if there is another target then the current one is moved off the screen
 *  and the new one is moved on. Once the new target starts coming on to the screen damage (or whatever action) is dealt
 *  to the target. Once the target is completely on the screen the target will shake if it is dealt damage. This process continues
 *  until all targets are processed. Once all targets are processed if the targets are enemies OR if the attacker is one of the targets
 *  in the spell then he is currently on the screen and then the battle will switch to the post attack state. If the target
 *  is an ally and is not the attacker himself then the state changes to transtion attacker in state.
 *
 *  TRANSITION IN STATE
 *  In this state the final target is moved off the screen and the attacker is moved onto the screen. After this we progress
 *  in to the post attack state
 *
 *  POST ATTACK STATE
 *  In the post attack state the attacker is displayed and the target if he is an enemy. In addition the experience gained
 *  will be displayed. Once this state is clicked we exit the AttackCinematicState
 */
public class AttackCinematicState extends LoadableGameState
{
	private final byte STATE_PRE_ATTACK = 0;
	private final byte STATE_ATTACKING = 1;
	private final byte STATE_TRANSITION_ATTACKER_OUT = 2;
	private final byte STATE_ATTACK_CLIMAX = 3;
	private final byte STATE_TRANSITION_ATTACKER_IN = 4;
	private final byte STATE_POST_ATTACK = 5;
	private final byte STATE_CASTING_SPELL = 6;

	/**
	 * The background environment image
	 */
	private Image backgroundImage;

	/**
	 * The x location that the background should be drawn at
	 */
	private int bgXPos;

	/**
	 * The y location that the background should be drawn at
	 */
	private int bgYPos;

	/**
	 * Attackers health bar
	 */
	private Panel attackerMenu;

	/**
	 * Targets health bar
	 */
	private ArrayList<Panel> targetMenus;

	/**
	 *
	 */
	private SpeechMenu textMenu;
	private CombatSprite attacker;
	private ArrayList<CombatSprite> targets;
	private int attackerAnimIndex = 0, attackerDelta = 0;
	private int targetAnimIndex = 0, targetDelta = 0;
	private Animation attackerAnim, targetAnim;
	private BattleResults battleResults;
	private int targetIndex = 0;
	private boolean targetsAreAllies = false;
	private boolean targetSelf = false;
	private byte state = 0;
	private int transitionX = 0;
	private int transitionIndex = 0;
	private boolean deathFade = false;
	private int damageFade = -1;
	private Color damageColor = new Color(204, 102, 0, 200);
	private Color deathColor = new Color(255, 255, 255, 255);
	private JMusicSelector musicSelector;
	private Timer damageTimer;
	private Timer spellTimer;
	private SpriteAnims spellAnims;
	private Animation spellAnim;
	private int spellAnimIndex = 0;
	private long spellDelta = 0;
	private int castingDelta = 0;
	private int battleBGIndex = 0;

	private float screenScale = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];// 1.88f; // (640 * 1024 / 3)

	private Image heroFloorImage;

	private FCResourceManager frm;
	private FCInput input;
	private Music music;

	//private Color[] targetColors = new Color[] {Color.red, Color.blue, Color.green};
	//private Color attackerColor = Color.yellow;

	public AttackCinematicState()
	{
		input = new FCInput();
	}

	/**
	 * Initializes this state, this only gets called when coming
	 * from a loading state
	 */
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		if (castingDelta % 6 < 3)
			g.setColor(Color.black);
		else
			g.setColor(Color.white);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());

		g.drawImage(backgroundImage, bgXPos, bgYPos);

		FCGameContainer cont = (FCGameContainer) container;

		// This is a little misleading, this will paint the health-bar for both
		// an ally or enemy target. The reason it paints during the climax is because
		// the ally will be on the screen during the climax
		if (!targetsAreAllies || state == STATE_ATTACK_CLIMAX)
		{
			targetMenus.get(targetIndex).render(cont, g);
		}

		// If the targets are not allies then the attacker will be on the screen for the
		// whole time, unless it's the climax in which case an ally will be on the screen
		if (!targetsAreAllies || state != STATE_ATTACK_CLIMAX)
			attackerMenu.render(cont, g);

		// Assume that the target is a hero and determine locations for it
		int xLoc = cont.getDisplayPaddingX() + transitionX;
		int yLoc = bgYPos + backgroundImage.getHeight();

		// Don't paint the "target" if it is an ally to the attacker unless we are in the climax state
		if (!targetsAreAllies || state == STATE_ATTACK_CLIMAX || state == STATE_TRANSITION_ATTACKER_IN)
		{
			// If the target is an enemy then update the x location
			if (!targets.get(targetIndex).isHero())
				xLoc = cont.getDisplayPaddingX() - transitionX;
			// Otherwise, this is a hero so we need to draw the land tile below them
			else
			{
				// Draw the floor represented by the land underneath the hero unless it is a flying hero
				if (targets.get(targetIndex).getMovementType() != CombatSprite.MOVEMENT_FLYING)
					g.drawImage(heroFloorImage, xLoc + 220 * screenScale, bgYPos + backgroundImage.getHeight() - 15 * screenScale);
			}

			for (AnimSprite as : targetAnim.frames.get(targetAnimIndex).sprites)
			{
				Image im = getRotatedImageIfNeeded(targets.get(targetIndex).getAnimationImageAtIndex(as.imageIndex), as);
				if (deathFade)
				{
					g.drawImage(im, xLoc + as.x * screenScale, yLoc + as.y * screenScale, deathColor);
				}
				else if (damageFade != -1)
				{
					g.drawImage(im, xLoc + as.x * screenScale + (CommRPG.RANDOM.nextInt(20) - 10) * screenScale, yLoc + as.y * screenScale + (CommRPG.RANDOM.nextInt(20) - 10) * screenScale , damageColor);
				}
				else
				{
					g.drawImage(im, xLoc + as.x * screenScale, yLoc + as.y * screenScale);
				}
			}
		}

		if (!targetsAreAllies || (state != STATE_ATTACK_CLIMAX && state != STATE_TRANSITION_ATTACKER_IN))
		{
			if (attacker.isHero())
			{
				xLoc = cont.getDisplayPaddingX() + (!targetsAreAllies ? 0 : transitionX);

				// Draw the floor represented by the land underneath the hero unless it is a flying hero
				if (targets.get(targetIndex).getMovementType() != CombatSprite.MOVEMENT_FLYING)
					g.drawImage(heroFloorImage, xLoc + 220 * screenScale, bgYPos + backgroundImage.getHeight() - 15 * screenScale);
			}
			else
			{
				xLoc = cont.getDisplayPaddingX() + (!targetsAreAllies ? 0 :  -transitionX);
			}

			for (AnimSprite as : attackerAnim.frames.get(attackerAnimIndex).sprites)
			{
				if (as.imageIndex > -1)
					g.drawImage(getRotatedImageIfNeeded(attacker.getAnimationImageAtIndex(as.imageIndex), as), xLoc + as.x * screenScale, yLoc + as.y * screenScale);
			}
		}

		if (spellAnimIndex != -1)
		{
			for (AnimSprite as : spellAnim.frames.get(spellAnimIndex).sprites)
			{
				if (as.imageIndex != -1)
				{
					g.drawImage(getRotatedImageIfNeeded(spellAnims.getImageAtIndex(as.imageIndex), as),
							cont.getDisplayPaddingX() + (targets.get(0).isHero() ? 276 * screenScale : 50 * screenScale) + as.x * screenScale, bgYPos + backgroundImage.getHeight() + as.y * screenScale);
				}
			}
		}


		/*
		if (state == STATE_PRE_ATTACK || state == STATE_ATTACK_CLIMAX)
			textMenu.render(cont, g);

		if (state == STATE_POST_ATTACK && textMenu != null)
			textMenu.render(cont, g);
			*/
		if (textMenu != null)
			textMenu.render(cont, g);
	}

	private Image getRotatedImageIfNeeded(Image image, AnimSprite as)
	{
		Image im = image;
		if (as.angle != 0)
		{
			im = image.copy();
			im.rotate(as.angle);
		}
		return im;
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		handleAttackerUpdate(container, delta);

		handleTargetUpdate(container, delta);

		handleTransitionUpdate(container, delta);

		handleStateUpdate(container, game, delta);
	}

	private void handleAttackerUpdate(GameContainer container, int delta)
	{
		// Keep track of how much time has elapsed on the attackers currently displayed frame
		attackerDelta += delta;

		// Keep track of how much time has elapsed for spells so we know when to update the frames
		if (spellAnimIndex != -1)
		{
			spellDelta += delta;

			// If the spellDelta is longer then the delay then update the animation
			if (spellDelta > spellAnim.frames.get(spellAnimIndex).delay)
			{
				spellDelta -= spellAnim.frames.get(spellAnimIndex).delay;
				if (spellAnimIndex + 1 == spellAnim.frames.size())
					spellAnimIndex = 0;
				else
					spellAnimIndex++;
			}
		}


		// Check if the amount of time elapsed is greater then the amount of time that
		// this frame should be displayed
		if (attackerDelta > attackerAnim.frames.get(attackerAnimIndex).delay)
		{
			// Reset the attackers frame delta
			attackerDelta -= attackerAnim.frames.get(attackerAnimIndex).delay;

			// Check if this is the final frame in this animation
			if (attackerAnimIndex + 1 == attackerAnim.frames.size())
			{
				// If this is the ATTACKING state then this means the attack has ended and we
				// should reset both animations to standing and change the state to POST_ATTACk
				if (state == STATE_ATTACKING)
				{
					// If the action targets allies and the attacker is not the only person in the list then we need
					// to transition out the attacker
					if (targetsAreAllies && (targets.size() > 1 || targets.get(0) != attacker))
					{
						state = STATE_TRANSITION_ATTACKER_OUT;
						transitionIndex = 1;
					}
					// Otherwise this is the climax, and the menu is already being displayed for this action
					else
					{
						state = STATE_ATTACK_CLIMAX;
					}
				}
				else if (state == STATE_PRE_ATTACK || state == STATE_POST_ATTACK)
					// Set the current animation index to the first frame in this animation
					attackerAnimIndex = 0;
			}
			// Otherwise we need to progress the current animation
			else
			{
				attackerAnimIndex++;

				// If this is the ATTACKING state then we may need to update the targets animation or stats as a result of this hit
				if (state == STATE_ATTACKING && attackerAnimIndex == attackerAnim.frames.size() - 1)
				{

					if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
					{
						state = STATE_CASTING_SPELL;
						castingDelta = 0;
						String sound = musicSelector.getCastSpellSoundEffect(attacker.isHero(), battleResults.battleCommand.getSpell().getName());

						if (sound != null)
						{
							Sound snd = frm.getSoundByName(sound);
							snd.play();
						}

					}
					else
					{
						textMenu = new SpeechMenu(battleResults.text.get(targetIndex), (FCGameContainer) container);
						performBattleResult();
					}
				}
			}
		}
	}

	private void handleTargetUpdate(GameContainer container, int delta)
	{

		if (damageFade > -1)
		{
			damageTimer.update(delta);
			while (damageFade > -1 && damageTimer.perform())
				damageFade--;
		}

		targetDelta += delta;
		if (targetDelta > targetAnim.frames.get(targetAnimIndex).delay)
		{
			if (deathFade)
				deathColor.a -= .2;


			targetDelta = 0;
			if (targetAnimIndex + 1 == targetAnim.frames.size())
				targetAnimIndex = 0;
			else
				targetAnimIndex++;
		}
	}

	private void handleTransitionUpdate(GameContainer container, int delta)
	{
		if (transitionIndex > 0)
		{
			transitionIndex++;

			if (transitionIndex <= 10)
			{
				transitionX = transitionIndex * 40;

				if (transitionIndex == 10)
				{
					// If we are transtioning out then all we need to do is change the state to climax
					// and everything will show as needed
					if (state == STATE_TRANSITION_ATTACKER_OUT)
					{
						state = STATE_ATTACK_CLIMAX;
						System.out.println("SET STATE CLIMAX 1");
					}
					// If we are transitioning and we are already at the last target then that means we are actually transitioning in
					// the attacker, so we need to set the end of battle state
					else if (state == STATE_TRANSITION_ATTACKER_IN)
					{
						if (battleResults.attackOverText != null)
						{
							textMenu = new SpeechMenu(battleResults.attackOverText, (FCGameContainer) container);
						}
						else
							textMenu = null;

						System.out.println("SET STATE POST ATTACK");

						state = STATE_POST_ATTACK;
						this.attackerAnimIndex = 0;
						attackerAnim = attacker.getAnimation("UnStand");
					}
					else
					{
						System.out.println("TRANSITION AT 10, NO STATE");
						deathFade = false;
						targetAnim = targets.get(++targetIndex).getAnimation("UnStand");
					}
				}
			}
			else if (transitionIndex == 20)
			{
				// Check to see if we are casting a spell on an ally, in this case we want to start displaying the spell now
				if (spellAnim != null && (targetsAreAllies || targetIndex > 0) && state == STATE_ATTACK_CLIMAX)
				{
					spellAnimIndex = 0;
					textMenu = new SpeechMenu(battleResults.text.get(targetIndex), (FCGameContainer) container);
					performBattleResult();
				}
				transitionIndex = 0;
				transitionX = 0;
			}
			else
			{
				transitionX = (20 - transitionIndex) * 40;
			}
		}
	}

	private void handleStateUpdate(GameContainer container, StateBasedGame game, int delta)
	{
		MenuUpdate menuUpdate = MenuUpdate.MENU_NO_ACTION;
		if (textMenu != null)
			menuUpdate = textMenu.update(delta, null);

		if (textMenu != null)
			textMenu.handleUserInput(input, null);
		// If the enemy was the attacker then there will be no menu to check, in this case just look directly for the key down
		else if (state == STATE_POST_ATTACK)
		{
			container.getInput().removeAllKeyListeners();
			// music.fade(250, 0f, true);
			music.stop();
			game.enterState(CommRPG.STATE_GAME_BATTLE, new FadeOutTransition(Color.black, 250), new EmptyTransition());
		}

		if (menuUpdate == MenuUpdate.MENU_NEXT_ACTION)
		{
			if (battleResults.levelUpResult != null)
			{
				attacker.getHeroProgression().levelUp(attacker, battleResults.levelUpResult, frm);
				String sound = musicSelector.getLevelUpSoundEffect(attacker);
				if (sound != null)
					frm.getSoundByName(sound).play();
			}
		}

		if (menuUpdate == MenuUpdate.MENU_CLOSE)
		{
			// If the user clicks pre-attack then start the attack sequence
			if (state == STATE_PRE_ATTACK)
			{
				state = STATE_ATTACKING;
				attackerAnimIndex = 0;
				if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK)
					attackerAnim = attacker.getAnimation("UnAttack");
				else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
				{
					if (attacker.getAnimation("UnSpell") != null)
						attackerAnim = attacker.getAnimation("UnSpell");
					else
						attackerAnim = attacker.getAnimation("UnAttack");
				}
				else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ITEM)
				{
					if (attacker.getAnimation("UnItem") != null)
						attackerAnim = attacker.getAnimation("UnItem");
					else
						attackerAnim = attacker.getAnimation("UnAttack");
				}
			}
			// If the user clicks after the attack then return to the battle game-state
			else if (state == STATE_POST_ATTACK)
			{
				container.getInput().removeAllKeyListeners();
				// music.fade(250, 0f, true);
				music.stop();
				game.enterState(CommRPG.STATE_GAME_BATTLE, new FadeOutTransition(Color.black, 250), new EmptyTransition());
			}
			else if (state == STATE_ATTACK_CLIMAX)
			{
				// This is the final target, switch off multiple targets so the battle can end
				if (targetIndex + 1 == targets.size())
				{
					if (transitionIndex == 0)
					{
						spellAnimIndex = -1;
						// If this action is targeting allies and the attacker is not a target then we need to transition them back in.
						if (targetsAreAllies && !targetSelf)
						{
							state = STATE_TRANSITION_ATTACKER_IN;
							transitionIndex = 1;
						}
						else
						{
							attackerAnim = attacker.getAnimation("UnStand");
							targetAnim = targets.get(targetIndex).getAnimation("UnStand");
							attackerAnimIndex = 0;

							if (battleResults.attackOverText != null)
							{
								textMenu = new SpeechMenu(battleResults.attackOverText, (FCGameContainer) container);
							}
							else
								textMenu = null;

							state = STATE_POST_ATTACK;
						}
					}
				}
				//TODO What the fuck is going on here that I need to do this, some
				// how the transition index is fucked up
				else if (transitionIndex == 0 || transitionIndex > 20)
				{
					transitionIndex = 1;
				}
			}
			// Check to see if we are in the casting spell state, in this case we want to flash the screen.
			// Once the screen flash is done then need to potentially show the spell and perform the results.
			// Finally move back to the attacking state so we can get back to the climax state
			else if (state == STATE_CASTING_SPELL)
			{
				spellTimer.update(delta);

				while (spellTimer.perform())
					castingDelta++;

				if (castingDelta >= 24)
				{
					castingDelta = 0;

					// Check to see if we are casting a spell on someone besides an ally, in this case case display the spell
					if (!targetsAreAllies || (targetSelf && targets.size() == 1))
					{
						spellAnimIndex = 0;
						textMenu = new SpeechMenu(battleResults.text.get(targetIndex), (FCGameContainer) container);
						performBattleResult();
					}

					state = STATE_ATTACKING;
				}
			}
		}
		input.update(delta);
	}

	private void performBattleResult()
	{
		/*
		// If the target will dodge this attack then display the dodge animation now
		if (battleResults.dodged)
		{
			targetAnimIndex = 0;
			targetAnim = targets.get(targetIndex).getAnimation("UnDodge");
		}

		String sound = null;
		if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK)
		{
			if (attacker.getEquippedWeapon() != null)
				sound = musicSelector.getAttackHitSoundEffect(attacker.isHero(), battleResults.critted, battleResults.dodged, attacker.getEquippedWeapon().getItemStyle());
			else
				sound = musicSelector.getAttackHitSoundEffect(attacker.isHero(), battleResults.critted, battleResults.dodged, -1);
		}
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ITEM)
			sound = musicSelector.getUseItemSoundEffect(attacker.isHero(), targetsAreAllies, battleResults.battleCommand.getItem().getName());
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
			sound = musicSelector.getSpellHitSoundEffect(attacker.isHero(), targetsAreAllies, battleResults.battleCommand.getSpell().getName());

		if (sound != null)
		{
			Sound snd = frm.getSoundByName(sound);
			snd.play();
		}

		CombatSprite target = targets.get(targetIndex);

		target.modifyCurrentHP(battleResults.hpDamage.get(targetIndex));
		// Set the dealt damage flag if the action does damage
		boolean dealtDamage = false;

		if (battleResults.hpDamage.get(targetIndex) < 0)
			dealtDamage = true;

		if (!targetsAreAllies && dealtDamage)
			damageFade = 8;

		target.modifyCurrentMP(battleResults.mpDamage.get(targetIndex));
		attacker.modifyCurrentHP(battleResults.attackerHPDamage.get(targetIndex));
		attacker.modifyCurrentMP(battleResults.attackerMPDamage.get(targetIndex));

		if (battleResults.targetEffects.get(targetIndex) != null)
			battleResults.targetEffects.get(targetIndex).performEffect(attacker, target);

		if (target.getCurrentHP() <= 0)
		{
			deathColor.a = 1;
			deathFade = true;
		}
		*/
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_BATTLE_ANIM;
	}

	public void setBattleInfo(CombatSprite attacker, FCResourceManager frm,
			BattleResults battleResults, FCGameContainer gc)
	{
		this.frm = frm;
		input.clear();
		gc.getInput().addKeyListener(input);

		screenScale = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];

		targetIndex = 0;
		transitionIndex = 0;
		deathFade = false;
		SpriteSheet battleBGSS = frm.getSpriteSheets().get("battlebg");
		Image bgIm = battleBGSS.getSprite(battleBGIndex % battleBGSS.getHorizontalCount(), battleBGIndex / battleBGSS.getHorizontalCount());
		System.out.println("ORIG " + bgIm.getWidth());
		System.out.println("SCALE AMT " + (gc.getWidth() - gc.getDisplayPaddingX() * 2) / (float) bgIm.getWidth());
		backgroundImage = bgIm.getScaledCopy((gc.getWidth() - gc.getDisplayPaddingX() * 2) / (float) bgIm.getWidth());
		System.out.println("RESULT SCALE " + backgroundImage.getWidth());

		bgXPos = gc.getDisplayPaddingX();
		bgYPos = (gc.getHeight() - backgroundImage.getHeight()) / 2;

		this.attacker = attacker;
		attackerMenu = new SpriteContextPanel((attacker.isHero() ? Panel.PANEL_HEALTH_BAR : Panel.PANEL_TARGET_HEALTH_BAR), attacker, gc);
		attackerAnim = attacker.getAnimation("UnStand");

		if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
		{

			spellAnims = frm.getSpriteAnimations().get(battleResults.battleCommand.getSpell().getName());

			spellAnim = spellAnims.getAnimation(1 + "");
			// spellAnim = spellAnims.getAnimation(battleResults.battleCommand.getLevel() + "");

			// spellAnims = frm.getSpriteAnimations().get("blaze");
			// spellAnim = spellAnims.getAnimation("blaze");
		}
		else
			spellAnim = null;

		spellAnimIndex = -1;



		this.battleResults = battleResults;

		if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK)
			textMenu = new SpeechMenu(attacker.getName() + " Attacks!}", gc);
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
			textMenu = new SpeechMenu(attacker.getName() + " casts " + battleResults.battleCommand.getSpell().getName() + " "
					+ (battleResults.battleCommand.getLevel() + "}"), gc);
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ITEM)
			textMenu = new SpeechMenu(attacker.getName() + " uses the " + battleResults.battleCommand.getItem().getName() + "}", gc);
		state = 0;
		/********************/
		/* Handle targets	*/
		/********************/
		this.targets = battleResults.targets;
		// Check to see if the targets are allies of the "attacker"
		this.targetsAreAllies = (targets.get(0).isHero() == attacker.isHero());

		// Check to see if the attackers is one of targets is the attacker
		// If so then move them to the start of the list of targets
		if (targetsAreAllies)
		{
			this.targetSelf = targets.contains(attacker);
		}
		else
			this.targetSelf = false;

		this.targetMenus = new ArrayList<Panel>();
		for (CombatSprite cs : targets)
		{
			targetMenus.add(new SpriteContextPanel((targets.get(0).isHero() ? Panel.PANEL_HEALTH_BAR : Panel.PANEL_TARGET_HEALTH_BAR), cs, gc));
		}
		targetAnim = targets.get(targetIndex).getAnimation("UnStand");

		musicSelector = GlobalPythonFactory.createJMusicSelector();
		music = frm.getMusicByName(musicSelector.getAttackMusic(attacker, targetsAreAllies));
		music.loop();

		// Get the land tile image for the current target
		// TODO Change this on a by-target basis
		heroFloorImage = frm.getImages().get("attackplatform");

		damageTimer = new Timer(16);
		spellTimer = new Timer(25);
	}

	@Override
	public void stateLoaded(FCResourceManager resourceManager) {
		frm = resourceManager;
	}

	public void setBattleBGIndex(int battleBGIndex) {
		this.battleBGIndex = battleBGIndex;
	}
}
