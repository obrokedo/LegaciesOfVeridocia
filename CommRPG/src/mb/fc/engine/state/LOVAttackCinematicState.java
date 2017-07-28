package mb.fc.engine.state;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import mb.fc.engine.CommRPG;
import mb.fc.game.battle.BattleResults;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.combat.AttackCombatAnimation;
import mb.fc.game.combat.CombatAnimation;
import mb.fc.game.combat.DamagedCombatAnimation;
import mb.fc.game.combat.DeathCombatAnimation;
import mb.fc.game.combat.DodgeCombatAnimation;
import mb.fc.game.combat.InvisibleCombatAnimation;
import mb.fc.game.combat.StandCombatAnimation;
import mb.fc.game.combat.TransBGCombatAnimation;
import mb.fc.game.combat.TransCombatAnimation;
import mb.fc.game.combat.WaitCombatAnimation;
import mb.fc.game.constants.TextSpecialCharacters;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.hudmenu.Panel.PanelType;
import mb.fc.game.hudmenu.SpriteContextPanel;
import mb.fc.game.input.FCInput;
import mb.fc.game.menu.Menu.MenuUpdate;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.particle.AnimatedParticleSystem;
import mb.fc.utils.AnimationWrapper;
import mb.fc.utils.HeroAnimationWrapper;
import mb.fc.utils.StringUtils;
import mb.jython.GlobalPythonFactory;
import mb.jython.JBattleEffect;
import mb.jython.JMusicSelector;
import mb.jython.JParticleEmitter;

public class LOVAttackCinematicState extends LoadableGameState implements MusicListener
{
	private static final int WAIT_TIME = 200;

	private JMusicSelector musicSelector = null;

	private ArrayList<CombatAnimation> heroCombatAnimations;
	private CombatAnimation heroCombatAnim;
	private Panel heroHealthPanel = null;

	private ArrayList<CombatAnimation> enemyCombatAnimations;
	private CombatAnimation enemyCombatAnim;
	private Panel enemyHealthPanel= null;

	private ArrayList<String> textToDisplay;

	private SpeechMenu textMenu;
	private FCInput input;
	private boolean consumedText = false;
	private PaddedGameContainer gc;
	private int bgXPos, bgYPos, combatAnimationYOffset;
	private Image backgroundImage;
	private BattleResults battleResults;
	private CombatSprite attacker;
	private FCResourceManager frm;

	// Spell stuff
	private boolean spellStarted = false;
	private boolean drawingSpell = false;
	private int drawingSpellCounter = 0;
	private Color spellFlash = null;
	private AnimationWrapper spellAnimation;
	private boolean spellTargetsHeroes;
	private int spellOverlayFadeIn = 0;
	private int SPELL_OVERLAY_MAX_ALPHA = 80;
	private Color spellOverlayColor = null;
	private AnimatedParticleSystem rainParticleSystem;

	// The amount that the background has been scaled to fit the screen,
	// other animations should be scaled up accordingly
	private float backgroundScale;

	private Music music;
	private Music introMusic;
	public static final int SPELL_FLASH_DURATION = 480;
	public static Image FLOOR_IMAGE;

	public void setBattleInfo(CombatSprite attacker, FCResourceManager frm,
			BattleResults battleResults, PaddedGameContainer gc)
	{
		this.gc = gc;
		this.battleResults = battleResults;
		this.attacker = attacker;
		this.frm = frm;

		// Get the land tile image for the current target
		// TODO Change this on a by-target basis
		FLOOR_IMAGE = frm.getImage("attackplatform");

		input = new FCInput();
		gc.getInput().addKeyListener(input);
		heroCombatAnimations = new ArrayList<>();
		enemyCombatAnimations = new ArrayList<>();
		heroCombatAnim = null;
		enemyCombatAnim = null;
		textToDisplay = new ArrayList<>();
		heroHealthPanel = null;
		enemyHealthPanel = null;

		drawingSpell = false;
		spellStarted = false;
		drawingSpellCounter = 0;
		spellOverlayFadeIn = 0;
		spellFlash = null;
		textMenu = null;

		// Initialize battle effects
		for (CombatSprite cs : battleResults.targets)
			cs.initializeBattleEffects(frm);

		for (ArrayList<JBattleEffect> effs : battleResults.targetEffects)
		{
			for (JBattleEffect eff : effs)
				eff.initializeAnimation(frm);
		}

		attacker.initializeBattleEffects(frm);

		/*****************************/
		/** Setup battle animations **/
		/*****************************/
		if (battleResults.battleCommand.getSpell() != null)
		{
			spellAnimation = new AnimationWrapper(frm.getSpriteAnimation(
					battleResults.battleCommand.getSpell().getSpellAnimationFile(battleResults.battleCommand.getLevel())));
			
			// Get the correct animation from the animation set
			String animationString = Integer.toString(battleResults.battleCommand.getLevel());
			boolean loops = battleResults.battleCommand.getSpell().isLoops();
			if (spellAnimation.hasAnimation(animationString))
				spellAnimation.setAnimation(animationString, loops);
			else
				spellAnimation.setAnimation("1", loops);
				
			spellOverlayColor = battleResults.battleCommand.getSpell().getSpellOverlayColor(battleResults.battleCommand.getLevel());
			spellTargetsHeroes = battleResults.targets.get(0).isHero();
			
			String rainFile = battleResults.battleCommand.getSpell().getSpellRainAnimationFile(battleResults.battleCommand.getLevel());
			if (rainFile != null)
			{
				/*rainParticleSystem.addEmitter(new RainEmitter(
						180, 
						battleResults.battleCommand.getSpell().getSpellRainFrequency(battleResults.battleCommand.getLevel()), 
								!battleResults.targets.get(0).isHero()));
								*/
				// Image im = frm.getImage(rainFile);
				String rainAnimation =  battleResults.battleCommand.getSpell().getSpellRainAnimationName(battleResults.battleCommand.getLevel());
				rainParticleSystem = new AnimatedParticleSystem(rainFile, rainAnimation, frm);
				JParticleEmitter emitter = battleResults.battleCommand.getSpell().getEmitter(battleResults.battleCommand.getLevel());
				emitter.initialize(battleResults.targets.get(0).isHero());
				rainParticleSystem.addEmitter(emitter);
			}
			else
				rainParticleSystem = null;
		}
		else {
			spellAnimation = null;
			rainParticleSystem = null;
		}
		
		

		boolean targetsAllies = battleResults.targets.get(0).isHero() == attacker.isHero();

		if (musicSelector == null)
		{
			musicSelector = GlobalPythonFactory.createJMusicSelector();
		}

		String mus = musicSelector.getAttackMusic(attacker, targetsAllies);
		music = frm.getMusicByName(mus);
		// Why the fuck is there a _L postfix?
		if (frm.containsMusic(mus + "_L"))
			introMusic = frm.getMusicByName(mus + "_L");

		if (introMusic == null)
		{
			if (music != null)
				music.loop();
		}
		else
		{
			introMusic.addListener(this);
			introMusic.play();
		}

		SpriteSheet battleBGSS = frm.getSpriteSheet("battlebg");
		Image bgIm = battleBGSS.getSprite(frm.getMap().getBackgroundImageIndex() % battleBGSS.getHorizontalCount(),
				frm.getMap().getBackgroundImageIndex() / battleBGSS.getHorizontalCount());
		backgroundScale = CommRPG.GAME_SCREEN_SIZE.width / (float) bgIm.getWidth();
		backgroundImage = bgIm.getScaledCopy(backgroundScale);

		bgXPos = 0;
		bgYPos = (CommRPG.GAME_SCREEN_SIZE.height - backgroundImage.getHeight()) / 2;
		combatAnimationYOffset = bgYPos + backgroundImage.getHeight();

		//////////////////////////////////////////////////////////////////////////
		// The attacker will be standing in any case
		addCombatAnimation(attacker.isHero(), new StandCombatAnimation(attacker));

		CombatSprite target = battleResults.targets.get(0);

		// If the battle command is not TURN_PREVENTED then determine how the cinematic should
		// be displayed
		if (battleResults.battleCommand.getCommand() != BattleCommand.COMMAND_TURN_PREVENTED)
		{
			//TODO CHECK TO SEE IF THE COMMAND WAS AN ITEM THAT CASTS A SPELL
			boolean isSpell = battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL;
			int distanceApart = 1;

			// This is a spell
			if (isSpell)
				textToDisplay.add(attacker.getName() + " casts " + battleResults.battleCommand.getSpell().getName() + " " +
						battleResults.battleCommand.getLevel() + TextSpecialCharacters.CHAR_HARD_STOP);
			// This is an item
			else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ITEM) {
				// Check to see if the item being used has a spell effect, if so then display the correct
				// text and set the isSpell boolean to true so that spell effects work correctly
				if (battleResults.battleCommand.getItem().getSpellUse() != null) {
					textToDisplay.add(attacker.getName() + " uses the " +
							battleResults.battleCommand.getItem().getName() + "! " + TextSpecialCharacters.CHAR_HARD_STOP);
					isSpell = true;
				}
				// Otherwise just display the generic use item text
				else
					textToDisplay.add(attacker.getName() + " uses the " +
						battleResults.battleCommand.getItem().getName() + "!" + TextSpecialCharacters.CHAR_HARD_STOP);
			}
			// This is an attack
			else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK)
				textToDisplay.add(attacker.getName() + " attacks!" + TextSpecialCharacters.CHAR_HARD_STOP);

			if (targetsAllies)
			{
				int i = 0;

				//TODO SELF IS A TARGET
				addCombatAnimation(!attacker.isHero(), null);

				int selfIndex = battleResults.targets.indexOf(attacker);
				if (selfIndex != -1)
				{
					battleResults.targets.add(0, battleResults.targets.remove(selfIndex));
					battleResults.text.add(0, battleResults.text.remove(selfIndex));
					battleResults.attackerHPDamage.add(0, battleResults.attackerHPDamage.remove(selfIndex));
					// battleResults.attackerMPDamage.add(0, battleResults.attackerMPDamage.remove(selfIndex));
					battleResults.hpDamage.add(0, battleResults.hpDamage.remove(selfIndex));
					battleResults.mpDamage.add(0, battleResults.mpDamage.remove(selfIndex));
					battleResults.targetEffects.add(0, battleResults.targetEffects.remove(selfIndex));

					AttackCombatAnimation aca = new AttackCombatAnimation(attacker, battleResults, true, battleResults.critted.get(0));
					addCombatAnimationWithNoSpeechNoReaction(attacker.isHero(), aca);
					if (battleResults.targets.size() > 1)
						addDamageAndTransitionOut(attacker, aca, isSpell, battleResults.targets.size() == 1, battleResults, 0);
					else
						addDamage(attacker, aca, isSpell, battleResults, 0);


					i = 1;
				}
				else
				{
					addActionAndTransitionOut(attacker, battleResults, isSpell, false);
				}

				for (; i < battleResults.targets.size(); i++)
				{
					addTransitionInAndOut(isSpell, battleResults.targets.get(i), battleResults, i,
							i == battleResults.targets.size() - 1);
				}

				if (selfIndex == -1 || battleResults.targets.size() > 1)
				{
					StandCombatAnimation attackerStand = new StandCombatAnimation(attacker);
					addCombatAnimationWithNoSpeechNoReaction(attacker.isHero(), new TransCombatAnimation(attackerStand, false));
				}
			}
			// The action is against enemies
			else
			{
				distanceApart = Math.abs(attacker.getTileX() - target.getTileX()) + Math.abs(attacker.getTileY() - target.getTileY());
				// If the attack is in melee range or a spell is being cast then the attacker will
				// always stay on the screen
				if (distanceApart == 1 || isSpell)
				{
					int attackCount = 0;

					// If the targets aren't allies then they should stand
					addCombatAnimation(target.isHero(), new StandCombatAnimation(target));
					addAttackAction(attacker, target, battleResults, attackCount++, isSpell, false);

					if (battleResults.countered)
					{
						addCombatAnimation(attacker.isHero(), new StandCombatAnimation(attacker));
						addCombatAnimation(target.isHero(), new StandCombatAnimation(target));
						textToDisplay.add(target.getName() + "'s counter attack!");
						addAttackAction(target, attacker, battleResults, attackCount++, isSpell, false);
					}

					if (battleResults.doubleAttack)
					{
						addCombatAnimation(attacker.isHero(), new StandCombatAnimation(attacker));
						addCombatAnimation(target.isHero(), new StandCombatAnimation(target));
						textToDisplay.add(attacker.getName() + "'s second attack!");
						addAttackAction(attacker, target, battleResults, attackCount++, isSpell, false);
					}
				}
				// This is a ranged attack
				else
				{
					addCombatAnimation(target.isHero(), null);
					addRangedAttack(attacker, target, 0);

					if (battleResults.doubleAttack)
					{
						addCombatAnimation(attacker.isHero(), new StandCombatAnimation(attacker));
						addCombatAnimation(target.isHero(), null);
						textToDisplay.add(attacker.getName() + "'s second attack!"  + TextSpecialCharacters.CHAR_HARD_STOP);
						addRangedAttack(attacker, target, 1);
					}
				}

				// If there is more then one target they need to be transitioned in and out while the attacker stays
				// in their final attack frame (or spell)
				if (battleResults.targets.size() > 1)
				{
					StandCombatAnimation targetStand = new StandCombatAnimation(target);
					addCombatAnimationWithNoSpeechNoReaction(target.isHero(), new TransCombatAnimation(targetStand, true));

					for (int i = 1; i < battleResults.targets.size(); i++)
					{
						addTransitionInAndOut(isSpell, battleResults.targets.get(i), battleResults, i,
								i == battleResults.targets.size() - 1);
					}

					// Remove the last set of actions because we don't want this target to move off the screen
					heroCombatAnimations.remove(heroCombatAnimations.size() - 1);
					enemyCombatAnimations.remove(enemyCombatAnimations.size() - 1);
					textToDisplay.remove(textToDisplay.size() - 1);
				}
			}

			/*
			 * Show the final frame where both parties (assuming they are alive)
			 * are standing next to eachother.
			 */
			// Check to see if the attacker is still alive
			if (!battleResults.attackerDeath)
				addCombatAnimation(attacker.isHero(), new StandCombatAnimation(attacker));
			else
				addCombatAnimation(attacker.isHero(), null);

			if (!targetsAllies && (isSpell || distanceApart == 1))
			{
				int lastIndex = battleResults.targets.size() - 1;

				// Check to see if the target is still alive
				if ((battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK && (!battleResults.death || battleResults.attackerDeath))
						|| (battleResults.battleCommand.getCommand() != BattleCommand.COMMAND_ATTACK && battleResults.remainingHP.get(lastIndex) > 0))
				{
					addCombatAnimation(!attacker.isHero(), new StandCombatAnimation(
						battleResults.targets.get(lastIndex)));
				}
				else
				{
					addCombatAnimation(!attacker.isHero(), null);
				}
			}
			else
			{
				addCombatAnimation(!attacker.isHero(), null);
			}

			// If the item was damaged then we'll use the animations from above
			// and display the damaged item text, we need to add null animations
			// to the lists so that they use the animations that were determined
			// above for the final frame where the attack over text is displayed
			if (battleResults.itemDamaged) {
				textToDisplay.add("The " + battleResults.itemUsed.getName() + " was damaged due to use." + TextSpecialCharacters.CHAR_SOFT_STOP);
				addCombatAnimation(true, null);
				addCombatAnimation(false, null);
			}

			textToDisplay.add(battleResults.attackOverText);
		} else {
			// If we are targeting an enemy then show him standing on the screen
			// if he was within one space
			if (!targetsAllies && Math.abs(attacker.getTileX() - target.getTileX()) +
					Math.abs(attacker.getTileY() - target.getTileY()) == 1)
			{
				addCombatAnimation(target.isHero(), new StandCombatAnimation(target));
			}
		}

		nextAction(null);
	}

	private void addRangedAttack(CombatSprite attacker, CombatSprite target, int index)
	{
		StandCombatAnimation sca = new StandCombatAnimation(target);
		addActionAndTransitionOut(attacker, battleResults, false, true);
		addCombatAnimationWithNoSpeechNoReaction(attacker.isHero(), new TransBGCombatAnimation(backgroundImage, bgXPos, bgYPos,
				CommRPG.GAME_SCREEN_SIZE.width, null, false, attacker.isHero()));
		addCombatAnimationWithNoSpeechNoReaction(attacker.isHero(), new TransBGCombatAnimation(backgroundImage, bgXPos, bgYPos,
				CommRPG.GAME_SCREEN_SIZE.width, sca, true, !attacker.isHero()));

		addAttackAction(attacker, target, battleResults, index, false, true);

		addCombatAnimationWithNoSpeechNoReaction(target.isHero(), new TransBGCombatAnimation(backgroundImage, bgXPos, bgYPos,
				CommRPG.GAME_SCREEN_SIZE.width, sca, false, target.isHero()));
		addCombatAnimationWithNoSpeechNoReaction(target.isHero(), new TransBGCombatAnimation(backgroundImage, bgXPos, bgYPos,
				CommRPG.GAME_SCREEN_SIZE.width, new StandCombatAnimation(attacker), true, !target.isHero()));

		addCombatAnimation(target.isHero(), new InvisibleCombatAnimation());
		addCombatAnimation(attacker.isHero(), new StandCombatAnimation(attacker));
		textToDisplay.add(null);
	}

	private void addAttackAction(CombatSprite attacker, CombatSprite target, BattleResults battleResults,
			int index, boolean isSpell, boolean rangedAttack)
	{
		AttackCombatAnimation aca = null;

		if (!rangedAttack)
			aca = new AttackCombatAnimation(attacker,
				battleResults, false, battleResults.critted.get(index));
		else
		{
			if (attacker.isHero())
				aca = new AttackCombatAnimation(new AnimationWrapper(frm.getSpriteAnimation("Ranged"),
					"Ranged", false, attacker.getCurrentWeaponImage()), attacker);
			else
				aca = new AttackCombatAnimation(new AnimationWrapper(frm.getSpriteAnimation("EnemyRanged"),
						"Ranged", false, attacker.getCurrentWeaponImage()), attacker);
		}
		addCombatAnimation(attacker.isHero(), aca);

		if (battleResults.dodged.get(index))
		{
			DodgeCombatAnimation targetDodge = new DodgeCombatAnimation(target);
			int startDodge = Math.max(0, aca.getAnimationLength() - targetDodge.getAnimationLength());

			if (startDodge == 0)
			{
				addCombatAnimation(target.isHero(), targetDodge);
				textToDisplay.add(battleResults.text.get(index));
			}
			else
			{
				StandCombatAnimation targetStand = new StandCombatAnimation(target,
						startDodge);
				addCombatAnimation(target.isHero(), targetStand);
				textToDisplay.add(null);
				addCombatAnimation(attacker.isHero(), null);
				addCombatAnimation(target.isHero(), targetDodge);
				textToDisplay.add(battleResults.text.get(index));


			}
		}
		else
		{
			StandCombatAnimation targetStand = new StandCombatAnimation(target,
					(isSpell ? aca.getAnimationLength() + 200 : aca.getAnimationLengthMinusLast()));
			addCombatAnimation(target.isHero(), targetStand);
			textToDisplay.add(null);

			DamagedCombatAnimation dca = new DamagedCombatAnimation(targetStand, battleResults.hpDamage.get(index),
					battleResults.mpDamage.get(index), battleResults.targetEffects.get(index), attacker, index);
			addCombatAnimation(attacker.isHero(), null);
			addCombatAnimation(target.isHero(), dca);
			textToDisplay.add(null);

			if (battleResults.remainingHP.get(index) <= 0)
			{
				DeathCombatAnimation death = new DeathCombatAnimation(targetStand);
				addCombatAnimation(target.isHero(), death);
				addCombatAnimation(!target.isHero(), null);
			}
			else
			{
				addCombatAnimation(attacker.isHero(), null);
				addCombatAnimation(target.isHero(), new StandCombatAnimation(target));
			}
			textToDisplay.add(battleResults.text.get(index));

			if (attacker.hasAnimation("Winddown"))
			{
				AnimationWrapper aw = new HeroAnimationWrapper(attacker, "Winddown");
				addCombatAnimation(attacker.isHero(), new CombatAnimation(aw, attacker, aw.getAnimationLength()));
				addCombatAnimation(target.isHero(), null);
				textToDisplay.add(null);
			}
		}
	}

	private void addActionAndTransitionOut(CombatSprite transitioner, BattleResults battleResults, boolean isSpell,
			boolean ranged)
	{
		AttackCombatAnimation aca = new AttackCombatAnimation(transitioner,
				battleResults, true, ranged);
		addCombatAnimationWithNoSpeechNoReaction(transitioner.isHero(), aca);
		TransCombatAnimation tca = new TransCombatAnimation(aca, true);
		tca.setDrawSpell(isSpell);
		addCombatAnimationWithNoSpeechNoReaction(transitioner.isHero(), tca);
	}

	private void addTransitionInAndOut(boolean showSpell, CombatSprite transitioner,
			BattleResults battleResults, int index, boolean lastTransitioner)
	{
		StandCombatAnimation sca = new StandCombatAnimation(transitioner,
				TransCombatAnimation.TRANSITION_TIME + WAIT_TIME);
		TransCombatAnimation tca = new TransCombatAnimation(sca, false);
		sca.setDrawSpell(showSpell);
		tca.setDrawSpell(showSpell);

		addCombatAnimationWithNoSpeechNoReaction(transitioner.isHero(), tca);
		addCombatAnimationWithNoSpeechNoReaction(transitioner.isHero(), sca);

		addDamageAndTransitionOut(transitioner, sca, showSpell, lastTransitioner, battleResults, index);
	}

	private void addDamage(CombatSprite transitioner, CombatAnimation ca,
			boolean showSpell,  BattleResults battleResults, int index)
	{
		DamagedCombatAnimation dca = new DamagedCombatAnimation(ca, battleResults.hpDamage.get(index),
				battleResults.mpDamage.get(index), battleResults.targetEffects.get(index), attacker, index);
		dca.setDrawSpell(showSpell);
		addCombatAnimation(transitioner.isHero(), dca);
		addCombatAnimation(!transitioner.isHero(), null);
		textToDisplay.add(null);

		if (dca.willSpriteDie())
		{
			DeathCombatAnimation death = new DeathCombatAnimation(ca);
			addCombatAnimation(transitioner.isHero(), death);
			addCombatAnimation(!transitioner.isHero(), null);
			textToDisplay.add(null);
		}

		addCombatAnimation(transitioner.isHero(), new WaitCombatAnimation(ca, 100));
		addCombatAnimation(!transitioner.isHero(), null);
		textToDisplay.add(battleResults.text.get(index));
	}

	private void addDamageAndTransitionOut(CombatSprite transitioner, CombatAnimation ca,
			boolean showSpell, boolean lastTransitioner, BattleResults battleResults, int index)
	{
		addDamage(transitioner, ca, showSpell, battleResults, index);

		TransCombatAnimation outTCA = new TransCombatAnimation(ca, true);
		if (lastTransitioner)
			outTCA.setDrawSpell(false);
		else
			outTCA.setDrawSpell(showSpell);
			// ca.setDrawSpell(false);
		addCombatAnimationWithNoSpeechNoReaction(transitioner.isHero(), outTCA);
	}

	private void addCombatAnimationWithNoSpeechNoReaction(boolean isHero, CombatAnimation combatAnimation)
	{
		addCombatAnimation(isHero, combatAnimation);
		addCombatAnimation(!isHero, null);
		textToDisplay.add(null);
	}

	private void addCombatAnimation(boolean isHero, CombatAnimation combatAnimation)
	{
		if (isHero)
			heroCombatAnimations.add(combatAnimation);
		else
			enemyCombatAnimations.add(combatAnimation);
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

	}

	/**
	 * Initializes this state, this only gets called when coming
	 * from a loading state
	 */
	@Override
	public void initAfterLoad() {

	}

	@Override
	public void doRender(PaddedGameContainer container, StateBasedGame game, Graphics g) 
	{
		if (spellFlash != null)
		{
			g.setColor(spellFlash);
			g.fillRect(0, 0, CommRPG.GAME_SCREEN_SIZE.width, CommRPG.GAME_SCREEN_SIZE.height);
		}

		g.drawImage(backgroundImage, bgXPos, bgYPos);
		if (heroCombatAnim != null)
			heroCombatAnim.render(gc, g, combatAnimationYOffset, backgroundScale);
		if (enemyCombatAnim != null)
			enemyCombatAnim.render(gc, g, combatAnimationYOffset, backgroundScale);
		if (drawingSpell && spellFlash == null)
		{
			spellAnimation.drawAnimation((int) ( (spellTargetsHeroes ? 276 : 70)),
					combatAnimationYOffset - 30, g);
		}
		if (textMenu != null)
			textMenu.render(gc, g);

		if (heroHealthPanel != null)
			heroHealthPanel.render(gc, g);
		if (enemyHealthPanel != null)
			enemyHealthPanel.render(gc, g);
		
		if (drawingSpell && spellFlash == null)
		{
			if (rainParticleSystem != null)
				rainParticleSystem.render();
			
		}
		
		// Draw the spell overlay fade in
		if (spellOverlayFadeIn > 0)
		{
			g.setColor(spellOverlayColor);
			g.fillRect(0, 0, CommRPG.GAME_SCREEN_SIZE.width, CommRPG.GAME_SCREEN_SIZE.height);
		}

		// If dev mode is enabled and we're at the end of the cinematic
		// then display the text to indicate the animation can be
		// restarted
		if (CommRPG.DEV_MODE_ENABLED && heroCombatAnimations.size() == 0)
		{
			g.setColor(Color.white);
			StringUtils.drawString("Press R to restart attack cinematic", 20, 90, g);
		}
	}

	@Override
	public void doUpdate(PaddedGameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// If this is test mode then we want to speed
		// up the game
		if (CommRPG.TEST_MODE_ENABLED)
			delta *= CommRPG.getTestMultiplier();

		// Update the input so that released keys are realized
		input.update(delta);

		// Whether the current animation says that
		// a spell should be rendered
		boolean currentAnimDrawSpell = false;

		// Whether the heroes/enemy current animation has finished yet
		// In other words whether the hero/enemy is ready to move to the next step
		// This is set to true initially so that if there is no current hero/enemy
		// animation it won't block
		boolean heroAnimationFinished = true;
		boolean enemyAnimationFinished = true;

		// If there is a hero combat animation check to see if it has
		// completed and whether the current animation should be drawn
		// with a spell
		if (heroCombatAnim != null)
		{
			heroAnimationFinished = heroCombatAnim.update(delta);
			if (heroCombatAnim.isDrawSpell())
			{
				currentAnimDrawSpell = true;
			}
		}
		
		// If there is a enemy combat animation check to see if it has
		// completed and whether the current animation should be drawn
		// with a spell
		if (enemyCombatAnim != null)
		{
			enemyAnimationFinished = enemyCombatAnim.update(delta);

			if (enemyCombatAnim.isDrawSpell())
			{
				currentAnimDrawSpell = true;
			}
		}

		// If the current animation says a spell should be drawn,
		// but the attack cinematic has not started drawing a spell
		// yet then we need to start drawing the "casting" of the spell
		// which is a flash and play the cast sound
		if (currentAnimDrawSpell && !spellStarted)
		{
			drawingSpell = true;
			spellStarted = true;
			spellOverlayFadeIn = 0;

			String sound = musicSelector.getCastSpellSoundEffect(attacker.isHero(), battleResults.battleCommand.getSpell().getName());

			if (sound != null)
			{
				Sound snd = frm.getSoundByName(sound);
				snd.play();
			}
		}
		// If we have started a spell but the current animation says no spell should
		// be displayed then we toggle off the drawing spell flag
		else if (spellStarted && !currentAnimDrawSpell)
		{
			drawingSpell = false;
		}

		// Check for whether we are in development mode, if so then the animation should be able to be re-run
		// but not actually effect anything on subsequent times through
		if (CommRPG.DEV_MODE_ENABLED && heroCombatAnimations.size() == 0 && input.isKeyDown(Input.KEY_R))
		{
				for (int i = 0; i < battleResults.hpDamage.size(); i++)
					battleResults.hpDamage.set(i, 0);
				for (int i = 0; i < battleResults.mpDamage.size(); i++)
					battleResults.mpDamage.set(i, 0);
				for (int i = 0; i < battleResults.attackerHPDamage.size(); i++)
					battleResults.attackerHPDamage.set(i, 0);
				for (int i = 0; i < battleResults.attackerMPDamage.size(); i++)
					battleResults.attackerMPDamage.set(i, 0);
				battleResults.levelUpResult = null;

				// Restart this animation
				setBattleInfo(attacker, frm,
						battleResults, gc);
		}

		// Check to see if there is a text menu that is currently displayed
		// if so we want to handle the user input and potentially
		// drive the next action or level up a hero
		if (textMenu != null)
		{
			textMenu.handleUserInput(input, null);
			MenuUpdate update = textMenu.update(delta, null);

			if (update == MenuUpdate.MENU_CLOSE)
			{
				textMenu = null;
				nextAction(game);
			}
			else if (update == MenuUpdate.MENU_NEXT_ACTION)
			{
				if (battleResults.levelUpResult != null)
				{
					if (attacker.isHero())
						attacker.getHeroProgression().levelUp(attacker, battleResults.levelUpResult, frm);
					else
						battleResults.targets.get(0).getHeroProgression().levelUp(battleResults.targets.get(0), battleResults.levelUpResult, frm);
					String sound = musicSelector.getLevelUpSoundEffect(attacker);
					if (sound != null)
						frm.getSoundByName(sound).play();
				}
			}
		}
		// If there is no text menu then we don't transition to the
		// next animations until both the hero's and enemy's current
		// animation has finished
		else if (heroAnimationFinished && enemyAnimationFinished)
		{
			// Text appears after the animations at the same index
			// have completed. If we haven't yet checked to see if there
			// should be text after this action do that now and
			// potentially display a text box. Once that text box has been
			// viewed consumedText = true and we'll move onto the next action
			// If there was no text box to display then we'll do an additional update
			// and consumedText = true so we'll get the next action
			if (!consumedText)
			{
				consumedText = true;
				String text = textToDisplay.remove(0);
				if (text != null)
				{
					textMenu = new SpeechMenu(text, gc);
				}
			}
			else
			{
				nextAction(game);
			}
		}

		// If we're currently drawing a spell then we want to
		// update the spells animation
		if (drawingSpell)
			spellUpdate(delta);
		else if (spellOverlayFadeIn > 0)
		{
			spellOverlayFadeIn -= 2;
			spellOverlayColor.a = spellOverlayFadeIn / 255.0f;
		}
	}

	/**
	 * Updates spell flashing, spell animations and the spell overlay.
	 * Additionally handles playing the "After spell flash sound effect"
	 *
	 * @param delta the amount of time that has passed since the previous update
	 */
	private void spellUpdate(int delta)
	{
		// Check to see if we are still flashing,
		// if so then toggle the color based on the time
		// elapse
		if (drawingSpellCounter < SPELL_FLASH_DURATION)
		{
			if (drawingSpellCounter % 160 < 80)
				spellFlash = Color.white;
			else
				spellFlash = Color.darkGray;
			drawingSpellCounter += delta;
		}
		// Otherwise we are done flashing and
		// the actual spell should begin rendering
		else
		{
			// If the flashing just finished then play the "After spell flash sound effect"
			// and set the spell flash to null so that we don't play the sound again
			if (spellFlash != null)
			{
				String sound = musicSelector.getAfterSpellFlashSoundEffect(attacker.isHero(), battleResults.battleCommand.getSpell().getName());
				if (sound != null)
					frm.getSoundByName(sound).play();
				spellFlash = null;
			}

			// If the overlay is not yet at full alpha then increment it
			// so that it will fade in
			if (spellOverlayFadeIn < SPELL_OVERLAY_MAX_ALPHA)
			{
				spellOverlayFadeIn += 2;
				spellOverlayColor.a = spellOverlayFadeIn / 255.0f;
			}

			// Update the spell animation as it should
			// be rendering at this point
			spellAnimation.update(delta);
			if (rainParticleSystem != null)
				rainParticleSystem.update(delta);
		}
	}

	private void nextAction(StateBasedGame game)
	{
		consumedText = false;
		DamagedCombatAnimation damagedSprite = null;

		if (heroCombatAnimations.size() > 0)
		{
			CombatAnimation hero = heroCombatAnimations.remove(0);
			if (hero != null)
			{
				if (hero.getParentSprite() == null || hero.getParentSprite().getCurrentHP() <= 0)
					heroHealthPanel = null;
				else
					heroHealthPanel = new SpriteContextPanel(
							PanelType.PANEL_HEALTH_BAR,
							hero.getParentSprite(), gc);
				hero.initialize();
				heroCombatAnim = hero;
				// TODO I'M NOT SURE IF I LIKE THIS HERE MORE OR IN THE DAMAGEDCOMBATANIMATION
				if (heroCombatAnim.isDamaging()) damagedSprite = (DamagedCombatAnimation) heroCombatAnim;
			}

			CombatAnimation enemy = enemyCombatAnimations.remove(0);
			if (enemy != null)
			{
				if (enemy.getParentSprite() == null || enemy.getParentSprite().getCurrentHP() <= 0)
					enemyHealthPanel = null;
				else
					enemyHealthPanel = new SpriteContextPanel(
							PanelType.PANEL_TARGET_HEALTH_BAR,
							enemy.getParentSprite(), gc);
				enemy.initialize();
				enemyCombatAnim = enemy;


				// TODO I'M NOT SURE IF I LIKE THIS HERE MORE OR IN THE DAMAGEDCOMBATANIMATION
				if (enemyCombatAnim.isDamaging()) damagedSprite = (DamagedCombatAnimation) enemyCombatAnim;
			}

			if (damagedSprite != null)
			{
				attacker.modifyCurrentHP(battleResults.attackerHPDamage.get(damagedSprite.getBattleResultIndex()));
				attacker.modifyCurrentMP(battleResults.attackerMPDamage.get(damagedSprite.getBattleResultIndex()));

				// Check to see if this is a counter attack situation and the "attacker"
				// is actually the target
				if (battleResults.countered && damagedSprite.getParentSprite() == attacker)
				{
					playOnHitSound(battleResults.targets.get(0), attacker.isHero() == damagedSprite.getParentSprite().isHero(),
							damagedSprite.getBattleResultIndex());
				}
				else
					playOnHitSound(attacker, attacker.isHero() == damagedSprite.getParentSprite().isHero(),
						damagedSprite.getBattleResultIndex());
			}
		}
		else
		{
			// EXIT
			if (music != null)
				music.stop();
			if (introMusic != null)
				introMusic.stop();
			gc.getInput().removeAllKeyListeners();
			game.enterState(CommRPG.STATE_GAME_BATTLE, new FadeOutTransition(Color.black, 250), new EmptyTransition());
		}
	}

	private void playOnHitSound(CombatSprite actionAttacker, boolean targetsAreAllies, int index)
	{
		String sound = null;
		if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK)
		{
			if (actionAttacker.getEquippedWeapon() != null)
				sound = musicSelector.getAttackHitSoundEffect(actionAttacker.isHero(), battleResults.critted.get(index),
						battleResults.dodged.get(index), actionAttacker.getEquippedWeapon().getItemStyle());
			else
				sound = musicSelector.getAttackHitSoundEffect(actionAttacker.isHero(), battleResults.critted.get(index),
						battleResults.dodged.get(index), -1);
		}
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ITEM)
			sound = musicSelector.getUseItemSoundEffect(actionAttacker.isHero(), targetsAreAllies, battleResults.battleCommand.getItem().getName());
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
			sound = musicSelector.getSpellHitSoundEffect(actionAttacker.isHero(), targetsAreAllies, battleResults.battleCommand.getSpell().getName());

		if (sound != null)
		{
			Sound snd = frm.getSoundByName(sound);
			snd.play();
		}
	}

	@Override
	public void stateLoaded(FCResourceManager resourceManager) {

	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_BATTLE_ANIM;
	}

	@Override
	public void musicEnded(Music music) {
		music.removeListener(this);
		this.music.loop();
	}

	@Override
	public void musicSwapped(Music music, Music newMusic) {

	}
}