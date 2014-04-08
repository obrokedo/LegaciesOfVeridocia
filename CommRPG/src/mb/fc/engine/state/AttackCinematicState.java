package mb.fc.engine.state;

import java.awt.Point;
import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.game.battle.BattleResults;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.hudmenu.SpriteContextPanel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.utils.AnimSprite;
import mb.fc.utils.Animation;
import mb.fc.utils.SpriteAnims;
import mb.gl2.loading.LoadableGameState;
import mb.gl2.loading.ResourceManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

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
	private Panel textMenu;
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
	private int stateDelta = 0;	
	private int transitionX = 0;
	private int transitionIndex = 0;
	private boolean deathFade = false;
	private int damageFade = -1;
	private Color damageColor = new Color(255, 0, 0, 200);
	private Color deathColor = new Color(255, 255, 255, 255);
	private boolean dealtDamage = false;
	
	private SpriteAnims spellAnims;
	private Animation spellAnim;
	private int spellAnimIndex = 0;
	
	private float screenScale = 1.88f; // (640 * 1024 / 3)
	
	private Image heroFloorImage;
	
	private FCResourceManager frm;
	private FCInput input;
	
	//private Color[] targetColors = new Color[] {Color.red, Color.blue, Color.green};
	//private Color attackerColor = Color.yellow;
	
	public AttackCinematicState()
	{
		input = new FCInput();
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setColor(Color.black);
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
					g.drawImage(heroFloorImage, xLoc + 230 * screenScale, bgYPos + backgroundImage.getHeight() - 28 * screenScale);
			}
			
			for (AnimSprite as : targetAnim.frames.get(targetAnimIndex).sprites)
			{
				if (deathFade)
				{
					g.drawImage(targets.get(targetIndex).getAnimationImageAtIndex(as.imageIndex), xLoc + as.x * screenScale, yLoc + as.y * screenScale, deathColor);
				}				
				else if (damageFade != -1)
				{					
					g.drawImage(targets.get(targetIndex).getAnimationImageAtIndex(as.imageIndex), 
							xLoc + as.x * screenScale + CommRPG.RANDOM.nextInt(20) - 10, yLoc + as.y * screenScale + CommRPG.RANDOM.nextInt(20) - 10, damageColor);
				}
				else
				{
					g.drawImage(targets.get(targetIndex).getAnimationImageAtIndex(as.imageIndex), 
							xLoc + as.x * screenScale, yLoc + as.y * screenScale);
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
					g.drawImage(heroFloorImage, xLoc + 230 * screenScale, bgYPos + backgroundImage.getHeight() - 28 * screenScale);
			}
			else
			{
				xLoc = cont.getDisplayPaddingX() + (!targetsAreAllies ? 0 :  -transitionX);
			}
			
			for (AnimSprite as : attackerAnim.frames.get(attackerAnimIndex).sprites)
				g.drawImage(attacker.getAnimationImageAtIndex(as.imageIndex), xLoc + as.x * screenScale, yLoc + as.y * screenScale);
		}
			
		if (spellAnimIndex != -1)
		{
			for (AnimSprite as : spellAnim.frames.get(spellAnimIndex).sprites)
				if (as.imageIndex != -1)
				{
					g.drawImage(spellAnims.getImageAtIndex(as.imageIndex), 
							cont.getDisplayPaddingX() + (targets.get(0).isHero() ? 226 * screenScale : 0) + as.x * screenScale, bgYPos + backgroundImage.getHeight() + as.y * screenScale);
				}
		}				
		
		
		if (state == STATE_PRE_ATTACK || state == STATE_ATTACK_CLIMAX)
			textMenu.render(cont, g);
		
		if (state == STATE_POST_ATTACK && textMenu != null)
			textMenu.render(cont, g);			
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
		
		// Check if the amount of time elapsed is greater then the amount of time that
		// this frame should be displayed 		
		if (attackerDelta > attackerAnim.frames.get(attackerAnimIndex).delay)
		{
			// Reset the attackers frame delta
			attackerDelta = 0;
			
			if (spellAnimIndex != -1)
			{
				if (spellAnimIndex + 1 == spellAnim.frames.size())
					spellAnimIndex = 0;
				else
					spellAnimIndex++;
			}
			
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
					
					textMenu = new SpeechMenu(battleResults.text.get(targetIndex), container);
					
					if (spellAnim != null)
						spellAnimIndex = 0;							
					
					performBattleResult();
					if (!targetsAreAllies && dealtDamage)
					{
						damageFade = 5;
					}
				}
			}
		}
	}
	
	private void handleTargetUpdate(GameContainer container, int delta)
	{
		if (damageFade > -1)
			damageFade--;
		
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
						state = STATE_ATTACK_CLIMAX;
					// If we are transitioning and we are already at the last target then that means we are actually transitioning in
					// the attacker, so we need to set the end of battle state
					else if (state == STATE_TRANSITION_ATTACKER_IN)
					{
						if (battleResults.attackOverText != null)
						{
							textMenu = new SpeechMenu(battleResults.attackOverText, container);
							if (battleResults.levelUpResult != null)
								attacker.getHeroProgression().levelUp(attacker, battleResults.levelUpResult, frm);
						}
						else
							textMenu = null;
						
						state = STATE_POST_ATTACK;
						this.attackerAnimIndex = 0;
						attackerAnim = attacker.getAnimation("UnStand");
					}
					else
					{
						deathFade = false;
						targetAnim = targets.get(++targetIndex).getAnimation("UnStand");
						textMenu = new SpeechMenu(battleResults.text.get(targetIndex), container);						
						performBattleResult();
						
					}
				}
			}
			else if (transitionIndex == 20)
			{
				transitionIndex = 0;
				transitionX = 0;
				if (!targetsAreAllies && dealtDamage)
					damageFade = 5;
			}
			else
			{
				transitionX = (20 - transitionIndex) * 40;
			}
		}
	}
	
	private void handleStateUpdate(GameContainer container, StateBasedGame game, int delta)
	{
		stateDelta += delta;
		
		// Handle use input based on the current state
		if (stateDelta > 250 && container.getInput().isKeyDown(KeyMapping.BUTTON_3))
		{
			stateDelta = 0;

			// If the user clicks pre-attack then start the attack sequence
			if (state == STATE_PRE_ATTACK)
			{				
				state = STATE_ATTACKING;
				attackerAnimIndex = 0;
				attackerAnim = attacker.getAnimation("UnAttack");				
				
				
			}
			// If the user clicks after the attack then return to the battle game-state
			else if (state == STATE_POST_ATTACK)
			{
				if (textMenu != null)
				{
					switch (((SpeechMenu) textMenu).handleUserInput(input, null))
					{
						case MENU_CLOSE:
							container.getInput().removeAllKeyListeners();
							game.enterState(CommRPG.STATE_GAME_BATTLE);
							break;
						default:
							break;						
					}
				}
				game.enterState(CommRPG.STATE_GAME_BATTLE);
			}
			else if (state == STATE_ATTACK_CLIMAX)
			{
				System.out.println("CLICKED CLIMAX");
				// This is the final target, switch off multiple targets so the battle can end
				if (targetIndex + 1 == targets.size())
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
							textMenu = new SpeechMenu(battleResults.attackOverText, container);
							if (battleResults.levelUpResult != null)
								attacker.getHeroProgression().levelUp(attacker, battleResults.levelUpResult, frm);
						}
						else
							textMenu = null;
						
						state = STATE_POST_ATTACK;
					}
				}
				else
				{
					transitionIndex = 1;
				}
			}					
		}		
		input.update(delta);
	}
	
	private void performBattleResult()
	{
		// If the target will dodge this attack then display the dodge animation now 
		if (battleResults.dodged)
		{
			targetAnimIndex = 0;
			targetAnim = targets.get(targetIndex).getAnimation("UnDodge");
		}
		
		CombatSprite target = targets.get(targetIndex); 
		
		target.modifyCurrentHP(battleResults.hpDamage.get(targetIndex));
		// Set the dealt damage flag if the action does damage
		if (battleResults.hpDamage.get(targetIndex) < 0)
			dealtDamage = true;
		else
			dealtDamage = false;
		
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
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_BATTLE_ANIM;
	}
	
	public void setBattleInfo(CombatSprite attacker, FCResourceManager frm, Point bgImagePoint,
			BattleResults battleResults, FCGameContainer gc)
	{				
		input.clear();
		gc.getInput().addKeyListener(input);
		
		targetIndex = 0;
		deathFade = false;
		Image bgIm = frm.getSpriteSheets().get("battlebg").getSprite(bgImagePoint.x, bgImagePoint.y);
		backgroundImage = bgIm.getScaledCopy((gc.getWidth() - gc.getDisplayPaddingX() * screenScale) / (float) bgIm.getWidth());
		
		bgXPos = gc.getDisplayPaddingX();
		bgYPos = (int) ((gc.getHeight() - backgroundImage.getHeight()) / screenScale);		
		
		this.attacker = attacker;
		attackerMenu = new SpriteContextPanel((attacker.isHero() ? Panel.PANEL_HEALTH_BAR : Panel.PANEL_ENEMY_HEALTH_BAR), attacker, gc);
		attackerAnim = attacker.getAnimation("UnStand");
		
		if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
		{
			/*
			spellAnims = frm.getSpriteAnimations().get(battleResults.battleCommand.getSpell().getName().toLowerCase());
			spellAnim = spellAnims.getAnimation(battleResults.battleCommand.getSpell().getName().toLowerCase());
			*/
			spellAnims = frm.getSpriteAnimations().get("blaze");
			spellAnim = spellAnims.getAnimation("blaze");
		}
		else
			spellAnim = null;
		
		spellAnimIndex = -1;
		
		
		
		this.battleResults = battleResults;
				
		if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ATTACK)
			textMenu = new SpeechMenu(attacker.getName() + " Attacks!", gc);
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
			textMenu = new SpeechMenu(attacker.getName() + " casts " + battleResults.battleCommand.getSpell().getName() + " " 
					+ (battleResults.battleCommand.getLevel()), gc);
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ITEM)
			textMenu = new SpeechMenu(attacker.getName() + " uses the " + battleResults.battleCommand.getItem().getName(), gc);
		state = 0;
		stateDelta = 0;
		
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
			targetMenus.add(new SpriteContextPanel((targets.get(0).isHero() ? Panel.PANEL_HEALTH_BAR : Panel.PANEL_ENEMY_HEALTH_BAR), cs, gc));			
		}
		targetAnim = targets.get(targetIndex).getAnimation("UnStand");
		
		// Get the land tile image for the current target
		// TODO Change this on a by-target basis
		heroFloorImage = frm.getImages().get("attackplatform");
	}

	@Override
	public void stateLoaded(ResourceManager resourceManager) {
		frm = (FCResourceManager) resourceManager;
	}
}
