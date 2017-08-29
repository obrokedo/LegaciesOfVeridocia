package mb.fc.game.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import org.newdawn.slick.Image;

import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.listener.MenuListener;
import mb.fc.game.resource.ItemResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Progression;
import mb.fc.game.trigger.Trigger;
import mb.jython.GlobalPythonFactory;
import mb.jython.JBattleEffect;
import mb.jython.JMenuConfiguration;

public class PriestMenu extends QuadMenu implements MenuListener
{	
	private ArrayList<PromotableHero> promotableHeroes = new ArrayList<>();
	private ArrayList<CombatSprite> curableHeroes = new ArrayList<>();
	private ArrayList<CombatSprite> revivableHeroes = new ArrayList<>();
	private JMenuConfiguration menuConfig;
	
	public PriestMenu(StateInfo stateInfo)
	{
		super(PanelType.PANEL_PRIEST, stateInfo);
		
		this.enabled = new boolean[4];
		this.icons = new Image[8];
		this.text = new String[4];
		
		for (int i = 0; i < icons.length; i++)
			icons[i] = stateInfo.getResourceManager().getSpriteSheet("actionicons").getSubImage(16 + (i % 4), i / 4);

		text = new String[] {"Save Game", "Promote", "Cure", "Resurrect"};
		enabled = new boolean[4];
		for (int i = 0; i < enabled.length; i++) 
			enabled[i] = true;
		
		this.paintSelectionCursor = true;
	}

	@Override
	public void initialize() {
		menuConfig = GlobalPythonFactory.createMenuConfig();
	}

	@Override
	protected MenuUpdate onBack() {
		stateInfo.sendMessage(new SpeechMessage("May the power of light be ever at your back.<hardstop>", Trigger.TRIGGER_NONE, Optional.empty()));
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	protected MenuUpdate onConfirm() {
		switch (selected) {
			// Save Game
			case UP:
				saveGame();
				break;
			// Res
			case DOWN:
				establishRevivables();
				break;
			// Promote
			case LEFT:
				establishPromotables();
				break;
			// Cure
			case RIGHT:
				establishCurables();
				break;
			default:
				break;
		}
		
		return MenuUpdate.MENU_ACTION_LONG;
	}

	private void establishPromotables() {
		boolean found = false;
		promotableHeroes.clear();
		int heroPromotionLevels = GlobalPythonFactory.createConfigurationValues().getHeroPromotionLevel();
		for (CombatSprite cs : stateInfo.getAllHeroes()) {
			if (!cs.isPromoted() && cs.getLevel() >= heroPromotionLevels) {
				found = true;
				
				// Check which progressions we are eligible for
				for (Progression prog : cs.getHeroProgression().getSpecialProgressions()) {
					// Iterate through each hero to see if they have a promotion item
					for (CombatSprite hero : stateInfo.getAllHeroes()) {
						for (int itemIdx = 0; itemIdx < hero.getItemsSize(); itemIdx++) {
							if (hero.getItem(itemIdx).getItemId() == prog.getSpecialPromotionItemId()) {
								promotableHeroes.add(new PromotableHero(cs, prog, hero));
							}
								
						}
					}
				}
				
				promotableHeroes.add(new PromotableHero(cs, cs.getHeroProgression().getPromotedProgression()));
			}
		}
		if (!found) 
			stateInfo.sendMessage(new SpeechMessage(
					menuConfig.getPriestNoOneToPromoteText(), Trigger.TRIGGER_NONE, Optional.empty()));
		else
			promptNextCombatSprite();
		
	}
	
	private void establishRevivables() {
		boolean found = false;
		for (CombatSprite cs : stateInfo.getAllHeroes()) {
			if (cs.getCurrentHP() <= 0) {
				revivableHeroes.add(cs);
				found = true;
			}
		}
		
		if (!found)
			stateInfo.sendMessage(new SpeechMessage(
					menuConfig.getPriestNoOneToResurrectText(), Trigger.TRIGGER_NONE, Optional.empty()));
		else
			promptNextCombatSprite();
	}
	
	private void establishCurables() {
		boolean found = false;
		for (CombatSprite cs : stateInfo.getAllHeroes()) {
			if (cs.getCurrentHP() <= 0)
				continue;
			Iterator<JBattleEffect> beItr = cs.getBattleEffects().iterator();
			
			while (beItr.hasNext()) {
				JBattleEffect be = beItr.next();
				if (!be.doesEffectPersistAfterBattle())
					beItr.remove();
			}
			
			if (cs.getBattleEffects().size() > 0)
				curableHeroes.add(cs);
		}
		
		if (!found)
			stateInfo.sendMessage(new SpeechMessage(menuConfig.getPriestNoOneToCureText(), Trigger.TRIGGER_NONE, Optional.empty()));
		else
			promptNextCombatSprite();
	}

	private void saveGame() {
		stateInfo.sendMessage(new SpeechMessage("Your progress will be saved.<hardstop>", Trigger.TRIGGER_NONE, Optional.empty()));
		stateInfo.sendMessage(MessageType.SAVE);
	}
	
	private void promptNextCombatSprite() {
		if (promotableHeroes.size() > 0) {
			PromotableHero ph = promotableHeroes.get(0);
			String itemUsed = null;
			if (ph.spriteHoldingItem != null)
				itemUsed = ItemResource.getUninitializedItem(ph.progressionToPromoteTo.getSpecialPromotionItemId()).getName();
			stateInfo.addMenu(new YesNoMenu(menuConfig.getPriestSelectSomeoneToPromoteText(
					ph.spriteToPromote.getName(), ph.progressionToPromoteTo.getClassName(), itemUsed), Trigger.TRIGGER_NONE, null, stateInfo, this));
		} else if (curableHeroes.size() > 0) {
			CombatSprite cs = curableHeroes.get(0);
			String[] effectNames = new String[cs.getBattleEffects().size()];
			int[] effectLevels = new int[cs.getBattleEffects().size()];
			for (int i = 0; i < cs.getBattleEffects().size(); i++) {
				effectNames[i] = cs.getBattleEffects().get(i).getBattleEffectId();
				effectLevels[i] = cs.getBattleEffects().get(i).getEffectLevel();
			}
			stateInfo.addMenu(new YesNoMenu(menuConfig.getPriestSelectSomeoneToCureText(cs.getName(), effectNames,
					menuConfig.getPriestCureCost(effectNames, effectLevels)), Trigger.TRIGGER_NONE, null, stateInfo, this));
		} else if (revivableHeroes.size() > 0) {
			CombatSprite cs = revivableHeroes.get(0);
			stateInfo.addMenu(new YesNoMenu(menuConfig.getPriestSelectSomeoneToResurrectText(cs.getName(), 
					menuConfig.getPriestResurrectCost(cs.getLevel(), cs.isPromoted())), Trigger.TRIGGER_NONE, null, stateInfo, this));
		}
	}

	@Override
	public MenuUpdate update(long delta, StateInfo stateInfo) {
		promptNextCombatSprite();
		return super.update(delta, stateInfo);
	}

	@Override
	protected int getTextboxWidth() {
		return 77;
	}

	@Override
	public boolean displayWhenNotTop() {
		return false;
	}

	@Override
	public void valueSelected(StateInfo stateInfo, Object value) {
		if (promotableHeroes.size() > 0) {
			PromotableHero ph = promotableHeroes.remove(0);
			if ((boolean) value) {				
				ph.spriteToPromote.getHeroProgression().promote(ph.spriteToPromote, ph.progressionToPromoteTo);

				String itemName = null;
				
				if (ph.spriteHoldingItem != null)
					itemName = ItemResource.getUninitializedItem(ph.progressionToPromoteTo.getSpecialPromotionItemId()).getName();
				stateInfo.sendMessage(new SpeechMessage(
						menuConfig.getPriestTargetHasBeenPromotedText(ph.spriteToPromote.getName(), ph.progressionToPromoteTo.getClassName(), 
								itemName), Trigger.TRIGGER_NONE, Optional.empty()));
				// This is lazy, but it we promote someone just completely re-establish the promotables
				establishPromotables();
			}
		} else if (curableHeroes.size() > 0) {
			CombatSprite cs = revivableHeroes.remove(0);
			String[] effectNames = new String[cs.getBattleEffects().size()];
			int[] effectLevels = new int[cs.getBattleEffects().size()];
			for (int i = 0; i < cs.getBattleEffects().size(); i++) {
				effectNames[i] = cs.getBattleEffects().get(i).getBattleEffectId();
				effectLevels[i] = cs.getBattleEffects().get(i).getEffectLevel();
			}
			restoreCombatSprite(cs, menuConfig.getPriestCureCost(effectNames, effectLevels), 
					menuConfig.getPriestNotEnoughGoldToCureText(), menuConfig.getPriestTargetHasBeenCuredText(cs.getName()),
					stateInfo, value);
		} else if (revivableHeroes.size() > 0) {
			CombatSprite cs = revivableHeroes.remove(0);
			restoreCombatSprite(cs, menuConfig.getPriestResurrectCost(cs.getLevel(), cs.isPromoted()), 
					menuConfig.getPriestNotEnoughGoldToResurrectText(), menuConfig.getPriestTargetHasBeenResurrectedText(cs.getName()),
					stateInfo, value);
		}
	}

	private void restoreCombatSprite(CombatSprite cs, int cost, 
			String notEnoughGoldString, String hasBeenRestoredString, StateInfo stateInfo, Object value) {
		if ((boolean) value) {
			if (stateInfo.getClientProfile().getGold() >= cost) {
				stateInfo.sendMessage(new SpeechMessage(notEnoughGoldString, 
						Trigger.TRIGGER_NONE, Optional.empty()));
			// If we have the gold then update the parties gold amount and "restore" the character
			} else {
				stateInfo.getClientProfile().setGold(stateInfo.getClientProfile().getGold() - cost);
				cs.setCurrentHP(cs.getMaxHP());
				cs.getBattleEffects().stream().forEach(be -> cs.removeBattleEffect(be));
				stateInfo.sendMessage(new SpeechMessage(
						hasBeenRestoredString, Trigger.TRIGGER_NONE, Optional.empty()));
			}
		}
	}

	@Override
	public void menuClosed() {
		
	}
	
	private class PromotableHero
	{	
		public PromotableHero(CombatSprite spriteToPromote, Progression progressionToPromoteTo) {
			this(spriteToPromote, progressionToPromoteTo, null);
		}
		
		public PromotableHero(CombatSprite spriteToPromote, Progression progressionToPromoteTo,
				CombatSprite spriteHoldingItem) {
			super();
			this.spriteToPromote = spriteToPromote;
			this.progressionToPromoteTo = progressionToPromoteTo;
			this.spriteHoldingItem = spriteHoldingItem;
		}
		CombatSprite spriteToPromote;
		Progression progressionToPromoteTo;
		
		CombatSprite spriteHoldingItem;
	}
}
