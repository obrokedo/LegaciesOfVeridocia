package mb.fc.engine.state;

import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.game.battle.BattleResults;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.combat.AttackCombatAnimation;
import mb.fc.game.combat.CombatAnimation;
import mb.fc.game.combat.DamagedCombatAnimation;
import mb.fc.game.combat.DeathCombatAnimation;
import mb.fc.game.combat.DodgeCombatAnimation;
import mb.fc.game.combat.StandCombatAnimation;
import mb.fc.game.combat.TransCombatAnimation;
import mb.fc.game.combat.WaitCombatAnimation;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.hudmenu.SpriteContextPanel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.utils.AnimationWrapper;
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

public class AttackCinematicState2 extends LoadableGameState
{
	private static float SCREEN_SCALE = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];

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
	private FCGameContainer gc;
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
	private Music music;
	public static final int SPELL_FLASH_DURATION = 480;
	public static Image FLOOR_IMAGE;

	public void setBattleInfo(CombatSprite attacker, FCResourceManager frm,
			BattleResults battleResults, FCGameContainer gc)
	{
		this.gc = gc;
		this.battleResults = battleResults;
		this.attacker = attacker;
		this.frm = frm;

		// Get the land tile image for the current target
		// TODO Change this on a by-target basis
		FLOOR_IMAGE = frm.getImages().get("attackplatform");

		input = new FCInput();
		gc.getInput().addKeyListener(input);
		heroCombatAnimations = new ArrayList<>();
		enemyCombatAnimations = new ArrayList<>();
		textToDisplay = new ArrayList<>();
		heroHealthPanel = null;
		enemyHealthPanel = null;

		drawingSpell = false;
		spellStarted = false;
		drawingSpellCounter = 0;
		spellFlash = null;

		if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
		{
			spellAnimation = new AnimationWrapper(frm.getSpriteAnimations().get(battleResults.battleCommand.getSpell().getName()),
					battleResults.battleCommand.getLevel() + "", true);

			spellTargetsHeroes = battleResults.targets.get(0).isHero();
		}
		else
			spellAnimation = null;

		boolean targetsAllies = battleResults.targets.get(0).isHero() == attacker.isHero();

		if (musicSelector == null)
		{
			musicSelector = GlobalPythonFactory.createJMusicSelector();
		}

		music = frm.getMusicByName(musicSelector.getAttackMusic(attacker, targetsAllies));
		music.loop();

		SpriteSheet battleBGSS = frm.getSpriteSheets().get("battlebg");
		// TODO CUSTOM IMAGE
		Image bgIm = battleBGSS.getSprite(0 % battleBGSS.getHorizontalCount(), 0 / battleBGSS.getHorizontalCount());
		backgroundImage = bgIm.getScaledCopy((gc.getWidth() - gc.getDisplayPaddingX() * 2) / (float) bgIm.getWidth());
		bgXPos = gc.getDisplayPaddingX();
		bgYPos = (gc.getHeight() - backgroundImage.getHeight()) / 2;
		combatAnimationYOffset = bgYPos + backgroundImage.getHeight();

		//////////////////////////////////////////////////////////////////////////
		textToDisplay.add(attacker.getName() + " attacks!");
		CombatSprite target = battleResults.targets.get(0);

		// The attacker will be standing in any case
		addCombatAnimation(attacker.isHero(), new StandCombatAnimation(attacker));
		if (!targetsAllies)
		{
			// If the targets aren't allies then they should stand
			addCombatAnimation(target.isHero(), new StandCombatAnimation(target));
		}

		boolean isSpell = battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL;
		boolean finalTargetDied = false;

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
				battleResults.attackerMPDamage.add(0, battleResults.attackerMPDamage.remove(selfIndex));
				battleResults.hpDamage.add(0, battleResults.hpDamage.remove(selfIndex));
				battleResults.mpDamage.add(0, battleResults.mpDamage.remove(selfIndex));
				battleResults.targetEffects.add(0, battleResults.targetEffects.remove(selfIndex));

				AttackCombatAnimation aca = new AttackCombatAnimation(attacker, battleResults, true);
				addCombatAnimationWithNoSpeechNoReaction(attacker.isHero(), aca);
				if (battleResults.targets.size() > 1)
					addDamageAndTransitionOut(attacker, aca, isSpell, battleResults.targets.size() == 1, battleResults, 0);
				else
					addDamage(attacker, aca, isSpell, battleResults, 0);


				i = 1;
			}
			else
			{
				addActionAndTransitionOut(attacker, battleResults, isSpell);
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
		else
		{
			addAttackAction(attacker, target, battleResults, 0, isSpell);

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

		//////////////////////////////////////////////////////////////////////////

		addCombatAnimation(attacker.isHero(), new StandCombatAnimation(attacker));
		if (!targetsAllies)
		{
			int lastIndex = battleResults.targets.size() - 1;
			if (battleResults.targets.get(lastIndex).getCurrentHP() + battleResults.hpDamage.get(lastIndex) > 0)
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

		textToDisplay.add(battleResults.attackOverText);

		nextAction(null);
	}

	private void addAttackAction(CombatSprite attacker, CombatSprite target, BattleResults battleResults,
			int index, boolean isSpell)
	{
		AttackCombatAnimation aca = new AttackCombatAnimation(attacker,
				battleResults, false);
		addCombatAnimation(attacker.isHero(), aca);

		if (battleResults.dodged)
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
					battleResults.mpDamage.get(index), battleResults.targetEffects.get(index), attacker);
			addCombatAnimation(attacker.isHero(), null);
			addCombatAnimation(target.isHero(), dca);
			textToDisplay.add(null);

			if (dca.willSpriteDie())
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
		}
	}

	private void addActionAndTransitionOut(CombatSprite transitioner, BattleResults battleResults, boolean isSpell)
	{
		AttackCombatAnimation aca = new AttackCombatAnimation(transitioner,
				battleResults, true);
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
				battleResults.mpDamage.get(index), battleResults.targetEffects.get(index), attacker);
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

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		if (spellFlash != null)
		{
			g.setColor(spellFlash);
			g.fillRect(0, 0, container.getWidth(), container.getHeight());
		}

		g.drawImage(backgroundImage, bgXPos, bgYPos);
		if (heroCombatAnim != null)
			heroCombatAnim.render(gc, g, combatAnimationYOffset);
		if (enemyCombatAnim != null)
			enemyCombatAnim.render(gc, g, combatAnimationYOffset);
		if (drawingSpell && spellFlash == null)
		{
			spellAnimation.drawAnimation((int) (gc.getDisplayPaddingX() + (spellTargetsHeroes ? 276 * SCREEN_SCALE : 50 * SCREEN_SCALE)),
					bgYPos + backgroundImage.getHeight(), g);
		}
		if (textMenu != null)
			textMenu.render(gc, g);

		if (heroHealthPanel != null)
			heroHealthPanel.render(gc, g);
		if (enemyHealthPanel != null)
			enemyHealthPanel.render(gc, g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		input.update(delta);

		boolean startSpell = false;
		boolean heroUpdate = true;

		if (heroCombatAnim != null)
		{
			heroUpdate = heroCombatAnim.update(delta);
			if (heroCombatAnim.isDrawSpell())
				startSpell = true;
		}

		boolean enemyUpdate = true;
		if (enemyCombatAnim != null)
		{
			enemyUpdate = enemyCombatAnim.update(delta);

			if (enemyCombatAnim.isDrawSpell())
				startSpell = true;
		}

		if (startSpell && !spellStarted)
		{
			drawingSpell = true;
			spellStarted = true;

			String sound = musicSelector.getCastSpellSoundEffect(attacker.isHero(), battleResults.battleCommand.getSpell().getName());

			if (sound != null)
			{
				Sound snd = frm.getSoundByName(sound);
				snd.play();
			}
		}
		else if (spellStarted && !startSpell)
		{
			drawingSpell = false;
		}


		if (textMenu != null)
		{
			textMenu.update(delta, null);
			if (input.isKeyDown(KeyMapping.BUTTON_3))
			{
				textMenu = null;
				nextAction(game);
			}
		}
		else if (heroUpdate && enemyUpdate)
		{
			if (!consumedText)
			{
				consumedText = true;
				String text = textToDisplay.remove(0);
				if (text != null)
				{
					textMenu = new SpeechMenu(text, gc, true, true);
				}
			}
			else
			{
				nextAction(game);
			}
		}

		if (drawingSpell)
			spellUpdate(delta);
	}

	private void spellUpdate(int delta)
	{
		if (drawingSpellCounter < SPELL_FLASH_DURATION)
		{
			if (drawingSpellCounter % 160 < 80)
				spellFlash = Color.white;
			else
				spellFlash = Color.darkGray;
			drawingSpellCounter += delta;
		}
		else
		{
			spellFlash = null;
			spellAnimation.udpate(delta);
		}
	}

	private void nextAction(StateBasedGame game)
	{
		consumedText = false;
		CombatSprite damagedSprite = null;

		if (heroCombatAnimations.size() > 0)
		{
			CombatAnimation hero = heroCombatAnimations.remove(0);
			if (hero != null)
			{
				if (hero.getParentSprite().getCurrentHP() <= 0)
					heroHealthPanel = null;
				hero.initialize();
				heroCombatAnim = hero;
				heroHealthPanel = new SpriteContextPanel(
						Panel.PANEL_HEALTH_BAR,
						heroCombatAnim.getParentSprite(), gc);
				// TODO I'M NOT SURE IF I LIKE THIS HERE MORE OR IN THE DAMAGEDCOMBATANIMATION
				if (heroCombatAnim.isDamaging()) damagedSprite = heroCombatAnim.getParentSprite();
			}

			CombatAnimation enemy = enemyCombatAnimations.remove(0);
			if (enemy != null)
			{
				if (enemy.getParentSprite().getCurrentHP() <= 0)
					enemyHealthPanel = null;
				enemy.initialize();
				enemyCombatAnim = enemy;
				enemyHealthPanel = new SpriteContextPanel(
						Panel.PANEL_TARGET_HEALTH_BAR,
						enemyCombatAnim.getParentSprite(), gc);

				// TODO I'M NOT SURE IF I LIKE THIS HERE MORE OR IN THE DAMAGEDCOMBATANIMATION
				if (enemyCombatAnim.isDamaging()) damagedSprite = enemyCombatAnim.getParentSprite();

			}

			if (damagedSprite != null)
			{
				int indexOfDamagedSprite = battleResults.targets.indexOf(damagedSprite);
				attacker.modifyCurrentHP(battleResults.attackerHPDamage.get(indexOfDamagedSprite));
				attacker.modifyCurrentMP(battleResults.attackerMPDamage.get(indexOfDamagedSprite));

				playOnHitSound(attacker.isHero() == damagedSprite.isHero());
			}
		}
		else
		{
			// EXIT
			music.stop();
			gc.getInput().removeAllKeyListeners();
			game.enterState(CommRPG.STATE_GAME_BATTLE, new FadeOutTransition(Color.black, 250), new EmptyTransition());
		}
	}

	private void playOnHitSound(boolean targetsAreAllies)
	{
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
	}

	@Override
	public void stateLoaded(FCResourceManager resourceManager) {

	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_BATTLE_ANIM;
	}

}