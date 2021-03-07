package lov.game.menu;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import lov.game.sprite.LOVCombatSprite;
import tactical.engine.TacticalGame;
import tactical.engine.message.AudioMessage;
import tactical.engine.message.MessageType;
import tactical.engine.state.StateInfo;
import tactical.game.Timer;
import tactical.game.battle.BattleEffect;
import tactical.game.battle.spell.KnownSpell;
import tactical.game.hudmenu.Panel;
import tactical.game.input.KeyMapping;
import tactical.game.input.UserInput;
import tactical.game.menu.AbstractHeroStatMenu;
import tactical.game.menu.Menu;
import tactical.game.menu.Portrait;
import tactical.game.sprite.CombatSprite;
import tactical.game.ui.PaddedGameContainer;
import tactical.utils.StringUtils;

public class LOVHeroStatMenu extends AbstractHeroStatMenu {

	private CombatSprite selectedSprite;
	private int x;
	private int y;
	private String gold;
	private int animCount = 0;
	private Timer timer;
	private Portrait portrait;
	private SpriteSheet spellLevels;
	private SpriteSheet affinities;
	private Image heroStatImage;
	
	private Image bigContainer = null;
	private Image smallContainer = null;
	private Image equip = null;
	private Image separator;
	private Image purpleGem;
	private Image blueGem;
	private Image tealGem;
	private Image redGem;
	
	private ArrayList<BattleEffect> effects = new ArrayList<>();
	private ArrayList<Image> effectImages = new ArrayList<>();
	
	private boolean pageOne = true;
	
	
	public LOVHeroStatMenu(GameContainer gc, CombatSprite selectedSprite, StateInfo stateInfo) {
		super(PanelType.PANEL_HEROS_STATS);
		
		x = (PaddedGameContainer.GAME_SCREEN_SIZE.width - 260) / 2;
		y = (PaddedGameContainer.GAME_SCREEN_SIZE.height - 192) / 2;
		this.selectedSprite = selectedSprite;
		if (selectedSprite.isHero())
			this.gold = stateInfo.getClientProfile().getGold() + "";

		portrait = Portrait.getPortrait(selectedSprite, stateInfo);
		timer = new Timer(500);
		this.spellLevels = stateInfo.getResourceManager().getSpriteSheet("spelllevel");
		this.affinities = stateInfo.getResourceManager().getSpriteSheet("affinities");
		this.heroStatImage = stateInfo.getResourceManager().getImage("herostats");
		bigContainer = heroStatImage.getSubImage(32, 32, 96, 16);
		smallContainer = heroStatImage.getSubImage(32, 16, 64, 16);
		equip = heroStatImage.getSubImage(128, 0, 8, 10);
		separator = heroStatImage.getSubImage(96, 24, 64, 8);
		purpleGem = heroStatImage.getSubImage(45, 48, 35, 16);
		blueGem = heroStatImage.getSubImage(45, 64, 35, 16);
		tealGem = heroStatImage.getSubImage(93, 64, 35, 16);
		redGem = heroStatImage.getSubImage(93, 48, 35, 16);
		
		effects.addAll(selectedSprite.getBattleEffects());
		
		for (BattleEffect eff : effects) {
			effectImages.add(stateInfo.getResourceManager().getImage(eff.getIconName()));
		}
	}

	@Override
	public MenuUpdate handleUserInput(UserInput input, StateInfo stateInfo) {
		if (input.isKeyDown(KeyMapping.BUTTON_2)) {
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuback", 1f, false));
			return MenuUpdate.MENU_CLOSE;
		} else if (input.isKeyDown(KeyMapping.BUTTON_RIGHT) || input.isKeyDown(KeyMapping.BUTTON_LEFT)) {
			this.pageOne = !pageOne;
			return MenuUpdate.MENU_ACTION_LONG;
		}
		return MenuUpdate.MENU_NO_ACTION;
	}
	
	

	@Override
	public MenuUpdate update(long delta, StateInfo stateInfo) {
		timer.update(delta);

		if (portrait != null)
			portrait.update(delta);

		while (timer.perform())
			animCount = (animCount + 1) % 2;

		return MenuUpdate.MENU_NO_ACTION;
	}
	
	private void renderPageOne(Graphics graphics) {
		int y2 = y + 15;
		int x2 = x + 69;
		
		TacticalGame.ENGINE_CONFIGURATIOR.getPanelRenderer().render(x + 62,
				y, 194,
				200, graphics, null);		
		
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.setFont(PANEL_FONT);
		
		StringUtils.drawString(selectedSprite.getName(), x2, y2 - 18, graphics);
		if (selectedSprite.isHero())
		{
			StringUtils.drawString(selectedSprite.getCurrentProgression().getClassName(),
					x2 + StringUtils.getStringWidth(selectedSprite.getName(), PANEL_FONT) + 10, y2 - 18, graphics);
		}
		
		smallContainer.draw(x2, y2);
		smallContainer.draw(x2, y2 + 16);
		bigContainer.draw(x2, y2 + 32);
		bigContainer.draw(x2, y2 + 48);		
		
		smallContainer.draw(x2 + 105, y2);
		smallContainer.draw(x2 + 105, y2 + 16);
		smallContainer.draw(x2 + 105, y2 + 32);
		smallContainer.draw(x2 + 105, y2 + 48);
		
		StringUtils.drawString("LV", x2 + 7, y2 - 5, graphics);		
		drawCenteredString(selectedSprite.getLevel(), x2 + 46, y2 - 5, graphics);
		
		//StringUtils.drawString(s, x2 + 45 - (w / 2), y2 + 27, graphics);
		//StringUtils.drawString(s, x2 + 77 - (w / 2), y2 + 27, graphics);
		//StringUtils.drawString(s, x2 + 151 - (w / 2), y2 + 27, graphics);
		
		StringUtils.drawString("XP", x2 + 7, y2 + 11, graphics);
		drawCenteredString(selectedSprite.getExp(), x2 + 46, y2 + 11, graphics);
		
		StringUtils.drawString("HP", x2 + 7, y2 + 27, graphics);
		drawCenteredString(selectedSprite.getCurrentHP(), x2 + 46, y2 + 27, graphics);
		drawCenteredString(selectedSprite.getMaxHP(), x2 + 78, y2 + 27, graphics);
		
		StringUtils.drawString("MP", x2 + 7, y2 + 43, graphics);
		drawCenteredString(selectedSprite.getCurrentMP(), x2 + 46, y2 + 43, graphics);
		drawCenteredString(selectedSprite.getMaxMP(), x2 + 78, y2 + 43, graphics);
		
		StringUtils.drawString("ATT", x2 + 109, y2 - 5, graphics);
		drawCenteredString(selectedSprite.getCurrentAttack(), x2 + 152, y2 - 5, graphics);
		
		StringUtils.drawString("DEF", x2 + 109, y2 + 11, graphics);
		drawCenteredString(selectedSprite.getCurrentDefense(), x2 + 152, y2 + 11, graphics);
		
		StringUtils.drawString("AGI", x2 + 109, y2 + 27, graphics);
		drawCenteredString(selectedSprite.getCurrentSpeed(), x2 + 152, y2 + 27, graphics);
		
		StringUtils.drawString("MOV", x2 + 109, y2 + 43, graphics);
		drawCenteredString(selectedSprite.getCurrentMove(), x2 + 152, y2 + 43, graphics);
		
		
		StringUtils.drawString("MAGIC", x2 + 7, y2 + 58, graphics);
		StringUtils.drawString("ITEM", x2 + 109, y2 + 58, graphics);
		
		
		// Draw Spells
		if (selectedSprite.getSpellsDescriptors() != null)
		{
			for (int i = 0; i < selectedSprite.getSpellsDescriptors().size(); i++)
			{
				graphics.setColor(Panel.COLOR_FOREFRONT);
				KnownSpell sd = selectedSprite.getSpellsDescriptors().get(i);
				graphics.drawImage(sd.getSpell().getSpellIcon(), x2 + 6, y2 + 79 + i * 25);
				StringUtils.drawString(sd.getSpell().getName(), x2 + 26, y2 + 70 + i * 25, graphics);
				for (int j = 0; j < sd.getMaxLevel(); j++)
				{
					graphics.setColor(Color.yellow);

					graphics.drawImage(spellLevels.getSprite(0, 2), x2 + 26 + j * 14,
							y2 + 92 + i * 25);					
				}
			}
		}		

		// Draw Items
		graphics.setColor(Panel.COLOR_FOREFRONT);
		for (int i = 0; i < selectedSprite.getItemsSize(); i++)
		{
			graphics.setColor(Panel.COLOR_FOREFRONT);
			graphics.drawImage(selectedSprite.getItem(i).getImage(), x2 + 110,
					y2 + 79 + i * 25);
			String[] itemSplit = StringUtils.splitItemString(selectedSprite.getItem(i).getName());
			StringUtils.drawString(itemSplit[0], x2 + 130, y2 + 70 + i * 25, graphics);
			if (itemSplit.length > 1)
				StringUtils.drawString(itemSplit[1], x2 + 130, y2 + 80 + i * 25, graphics);

			if (selectedSprite.getEquipped().get(i))
			{
				equip.draw(x2 + 120, y2 + 94 + i * 25);
			}

		}
		
		/************************/
		/* Draw the gold window */
		/************************/
		if (selectedSprite.isHero())
		{
			TacticalGame.ENGINE_CONFIGURATIOR.getPanelRenderer().render(x, y + 163, 62, 37, graphics, null);
			graphics.setColor(Panel.COLOR_FOREFRONT);
			StringUtils.drawString("Gold", x + 17, y + 162, graphics);
			StringUtils.drawString(gold, x + 29 - StringUtils.getStringWidth(gold, PANEL_FONT) / 2, y + 174, graphics);
		}
		
		/*****************************/
		/* Draw the statistic window */
		/*****************************/		
		TacticalGame.ENGINE_CONFIGURATIOR.getPanelRenderer().render(x, y + 78, 62, 85, graphics, null);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawImage(selectedSprite.getAnimationImageAtIndex(selectedSprite.getAnimation("Down").frames.get(animCount).sprites.get(0).imageIndex),
				x + 19,
				y + 85);
		separator.draw(x + 7, y + 113);
		drawCenteredString("MOVE", x + 30, y + 110, graphics);
		drawCenteredString("TYPE", x + 29, y + 120, graphics);
		separator.draw(x + 7, y + 139);
		
		if (selectedSprite.isHero())
			drawCenteredString(selectedSprite.getCurrentProgression().getMovementType(), x + 30, y + 136, graphics);
		else
			drawCenteredString(selectedSprite.getMovementType(), x + 30, y + 136, graphics);
	}
	
	private void renderPageTwo(Graphics graphics) {
		int y2 = y + 15;
		int x2 = x + 70;
		
		// Name Panel
		TacticalGame.ENGINE_CONFIGURATIOR.getPanelRenderer().render(x + 62,
				y, 194,
				35, graphics, null);		
		
		// Affin Panel
		TacticalGame.ENGINE_CONFIGURATIOR.getPanelRenderer().render(x + 62,
				y + 35, 122,
				96, graphics, null);
		
		// Effect Panel
		TacticalGame.ENGINE_CONFIGURATIOR.getPanelRenderer().render(x + 184,
				y + 35, 72,
				96, graphics, null);
		
		// Bottom
		TacticalGame.ENGINE_CONFIGURATIOR.getPanelRenderer().render(x + 62,
				y + 131, 194,
				69, graphics, null);
		
		// Status
		TacticalGame.ENGINE_CONFIGURATIOR.getPanelRenderer().render(x,
				y + 78, 62,
				122, graphics, null);
		
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.setFont(PANEL_FONT);
		if (selectedSprite.isHero()) {
			String equippable = "";
			int [] equipList = selectedSprite.getCurrentProgression().getUsuableWeapons();
			if (equipList != null && equipList.length > 0) {
				for (int j = 0; j < equipList.length; j++) {
					equippable += TacticalGame.ENGINE_CONFIGURATIOR.getConfigurationValues().getWeaponTypes()[equipList[j]] + (
							(equipList.length - 1) == j ? "" : " / ");
				}
			}
			StringUtils.drawString(equippable, x2 + 10, y2 - 6, graphics);
		}
		equip.draw(x2 - 1,  y2 + 2);
		
		StringUtils.drawString("AFFINITIES", x2 + 19, y2 + 18, graphics);
		
		affinities.getSubImage(2, 0).draw(x2 - 1, y2 + 38);
		affinities.getSubImage(1, 2).draw(x2 - 1, y2 + 56);
		affinities.getSubImage(0, 1).draw(x2 - 1, y2 + 74);
		affinities.getSubImage(3, 1).draw(x2 - 1, y2 + 92);
				
		purpleGem.draw(x2 + 15, y2 + 38);
		purpleGem.draw(x2 + 15, y2 + 56);
		purpleGem.draw(x2 + 15, y2 + 74);
		purpleGem.draw(x2 + 15, y2 + 92);
		
		drawCenteredString(((LOVCombatSprite) selectedSprite).getCurrentFireAffin(), 
				x2 + 33, y2 + 32, graphics);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getCurrentColdAffin(), 
				x2 + 33, y2 + 50, graphics);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getCurrentElecAffin(), 
				x2 + 33, y2 + 68, graphics);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getCurrentDarkAffin(), 
				x2 + 33, y2 + 86, graphics);
		
		affinities.getSubImage(3, 0).draw(x2 + 56, y2 + 38);
		affinities.getSubImage(0, 2).draw(x2 + 56, y2 + 56);
		affinities.getSubImage(1, 1).draw(x2 + 56, y2 + 74);
		affinities.getSubImage(2, 1).draw(x2 + 56, y2 + 92);
		
		blueGem.draw(x2 + 72, y2 + 38);
		blueGem.draw(x2 + 72, y2 + 56);
		blueGem.draw(x2 + 72, y2 + 74);
		blueGem.draw(x2 + 72, y2 + 92);
		
		drawCenteredString(((LOVCombatSprite) selectedSprite).getCurrentWaterAffin(), 
				x2 + 90, y2 + 32, graphics);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getCurrentWindAffin(), 
				x2 + 90, y2 + 50, graphics);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getCurrentEarthAffin(), 
				x2 + 90, y2 + 68, graphics);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getCurrentLightAffin(), 
				x2 + 90, y2 + 86, graphics);
				
		affinities.getSubImage(1, 0).draw(x2 - 1, y2 + 133);
		affinities.getSubImage(0, 0).draw(x2 - 1, y2 + 160);
		tealGem.draw(x2 + 15, y2 + 133);
		tealGem.draw(x2 + 15, y2 + 160);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getCurrentBody(), 
				x2 + 33, y2 + 127, graphics);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getCurrentMind(), 
				x2 + 33, y2 + 154, graphics);
		StringUtils.drawString("BODY", x2, y2 + 114, graphics);
		StringUtils.drawString("MIND", x2, y2 + 142, graphics);
				
		affinities.getSubImage(3, 3).draw(x2 + 56, y2 + 133);
		affinities.getSubImage(1, 3).draw(x2 + 56, y2 + 160);
		redGem.draw(x2 + 72, y2 + 133);
		redGem.draw(x2 + 72, y2 + 160);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getModifiedDouble(), 
				x2 + 90, y2 + 127, graphics);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getModifiedEvade(), 
				x2 + 90, y2 + 154, graphics);
		StringUtils.drawString("DOUBLE", x2 + 57, y2 + 114, graphics);
		StringUtils.drawString("EVADE", x2 + 57, y2 + 142, graphics);
		
		affinities.getSubImage(2, 3).draw(x2 + 113, y2 + 133);
		affinities.getSubImage(0, 3).draw(x2 + 113, y2 + 160);
		redGem.draw(x2 + 129, y2 + 133);
		redGem.draw(x2 + 129, y2 + 160);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getModifiedCrit(), 
				x2 + 147, y2 + 127, graphics);
		drawCenteredString(((LOVCombatSprite) selectedSprite).getModifiedCounter(), 
				x2 + 147, y2 + 154, graphics);
		StringUtils.drawString("CRITICAL", x2 + 114, y2 + 114, graphics);
		StringUtils.drawString("COUNTER", x2 + 114, y2 + 142, graphics);
				
		StringUtils.drawString("EFFECT", x2 + 129, y2 + 18, graphics);
		
		StringUtils.drawString("STATUS", x + 10, y + 76, graphics);
		separator.draw(x + 7, y + 98);
		
		StringUtils.drawString(selectedSprite.getName(), x2, y2 - 18, graphics);
		if (selectedSprite.isHero())
		{
			StringUtils.drawString(selectedSprite.getCurrentProgression().getClassName(),
					x2 + StringUtils.getStringWidth(selectedSprite.getName(), PANEL_FONT) + 10, y2 - 18, graphics);
		}
		
		for (int i = 0; i < effects.size(); i++) {
			Image sub = null;
			switch (effects.get(i).getRemainingTurns())
			{
				case 1:
					sub = heroStatImage.getSubImage(0, 0, 16, 8);
					break;
				case 2:
					sub = heroStatImage.getSubImage(32, 0, 16, 8);
					break;
				case 3:
					sub = heroStatImage.getSubImage(16, 0, 16, 8);
					break;
				default:
					sub = heroStatImage.getSubImage(48, 0, 16, 8);
			}
			
			Image lvl = null;
			switch (effects.get(i).getEffectLevel())
			{
				case 4:
					lvl = heroStatImage.getSubImage(112, 8, 16, 8);					
					break;				
				case 2:
					lvl = heroStatImage.getSubImage(80, 8, 16, 8);
					break;
				case 3:
					lvl = heroStatImage.getSubImage(96, 8, 16, 8);
					break;
				default:
					lvl = heroStatImage.getSubImage(64, 8, 16, 8);
					
			}
			
			
			
			effectImages.get(i).draw(x + 7 + (i % 3) * 16, y + 114 + (i / 3) * 46);
			sub.draw(x + 7 + (i % 3) * 16, y + 107 + (i / 3) * 30);
			lvl.draw(x + 7 + (i % 3) * 16, y + 137 + (i / 3) * 30);			
		}
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		if (selectedSprite.isHero())
			TacticalGame.ENGINE_CONFIGURATIOR.getPanelRenderer().render(x + 6,
				y + 6, 256,
				200, graphics, null);
		else
			TacticalGame.ENGINE_CONFIGURATIOR.getPanelRenderer().render(x + 67,
					y + 6, 195,
					200, graphics, null);

		/****************************/
		/* Draw the portrait window	*/
		/****************************/
		if (portrait != null)
		{
			portrait.render(x, y, graphics);
		}	
		
		if (pageOne)
			renderPageOne(graphics);
		else
			renderPageTwo(graphics);
	}
	
	private void drawCenteredString(int str, int centerX, int y, Graphics g) {
		drawCenteredString(str + "", centerX, y, g);
	}
	
	private void drawCenteredString(String str, int centerX, int y, Graphics g) {
		int w = StringUtils.getStringWidth(str, PANEL_FONT);				
		StringUtils.drawString(str, centerX - (w / 2), y, g);
	}

	@Override
	public CombatSprite getSelectedSprite() {
		return selectedSprite;
	}
	

}
