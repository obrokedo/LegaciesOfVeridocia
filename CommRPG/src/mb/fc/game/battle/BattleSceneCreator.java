package mb.fc.game.battle;

import java.util.ArrayList;

import org.newdawn.slick.Image;

import mb.fc.engine.CommRPG;
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
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.utils.AnimationWrapper;
import mb.fc.utils.HeroAnimationWrapper;

public class BattleSceneCreator {
	private static final int WAIT_TIME_FOR_DMG_AFTER_TRANS = 600;
	
	private ArrayList<CombatAnimation> heroCombatAnimations;
	private ArrayList<CombatAnimation> enemyCombatAnimations;
	private ArrayList<String> textToDisplay;
	private CombatSprite attacker;
	private BattleResults battleResults;
	private FCResourceManager frm;
	private int bgXPos, bgYPos;
	private Image backgroundImage;
	
	public static BattleSceneCreator initializeBattleScene(CombatSprite attacker, FCResourceManager frm,
			BattleResults battleResults, PaddedGameContainer gc, boolean targetsAllies,
			int bgXPos, int bgYPos, Image backgroundImage) {
		BattleSceneCreator bsc = new BattleSceneCreator();
		bsc.setBattleInfo(attacker, frm, battleResults, gc, targetsAllies, bgXPos, bgYPos, backgroundImage);
		return bsc;
	}
	
	private void setBattleInfo(CombatSprite attacker, FCResourceManager frm,
			BattleResults battleResults, PaddedGameContainer gc, boolean targetsAllies,
			int bgXPos, int bgYPos, Image backgroundImage)
	{
		heroCombatAnimations = new ArrayList<>();
		enemyCombatAnimations = new ArrayList<>();
		textToDisplay = new ArrayList<>();
		this.battleResults = battleResults;
		this.attacker = attacker;
		this.frm = frm;
		this.bgXPos = bgXPos;
		this.bgYPos = bgYPos;
		this.backgroundImage = backgroundImage;
		
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
					TransCombatAnimation transOut = new TransCombatAnimation(targetStand, true);
					addCombatAnimationWithNoSpeechNoReaction(target.isHero(), transOut);
					

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
			
			if (isSpell && attacker.hasAnimation("SpellWinddown")) {
				AnimationWrapper aw = new HeroAnimationWrapper(attacker, "SpellWinddown");
				addCombatAnimationWithNoSpeechNoReaction(attacker.isHero(), new CombatAnimation(aw, attacker, aw.getAnimationLength()));
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
		aca.setDrawSpell(isSpell);
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
			
			//TODO We probably need winddown even on miss
			//TODO Add block winddown here
			
			if (attacker.hasAnimation("Winddown") || target.hasAnimation("BlockWinddown")) {
				if (attacker.hasAnimation("Winddown"))
				{
					AnimationWrapper aw = new HeroAnimationWrapper(attacker, "Winddown");
					addCombatAnimation(attacker.isHero(), new CombatAnimation(aw, attacker, aw.getAnimationLength()));
				} else {
					addCombatAnimation(attacker.isHero(), null);
				}
				
				if (target.hasAnimation("BlockWinddown"))
				{
					AnimationWrapper aw = new HeroAnimationWrapper(target, "BlockWinddown");
					addCombatAnimation(target.isHero(), new CombatAnimation(aw, target, aw.getAnimationLength()));
					
				} else {
					addCombatAnimation(target.isHero(), null);
				}
				
				textToDisplay.add(null);
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

			if (!isSpell && attacker.hasAnimation("Winddown"))
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
				TransCombatAnimation.TRANSITION_TIME + WAIT_TIME_FOR_DMG_AFTER_TRANS);
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

	public ArrayList<CombatAnimation> getHeroCombatAnimations() {
		return heroCombatAnimations;
	}

	public ArrayList<CombatAnimation> getEnemyCombatAnimations() {
		return enemyCombatAnimations;
	}

	public ArrayList<String> getTextToDisplay() {
		return textToDisplay;
	}
}