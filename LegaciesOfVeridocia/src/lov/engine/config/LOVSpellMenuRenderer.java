package lov.engine.config;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import tactical.engine.TacticalGame;
import tactical.engine.config.SpellMenuRenderer;
import tactical.engine.state.StateInfo;
import tactical.game.battle.spell.KnownSpell;
import tactical.game.hudmenu.Panel;
import tactical.game.sprite.CombatSprite;
import tactical.loading.ResourceManager;
import tactical.utils.StringUtils;

public class LOVSpellMenuRenderer implements SpellMenuRenderer {
	
	private int chooserOffset = 0;
	private int chooseMoveDelta = 0;
	private int spellLevel = 0;
	
	public void render(String spellName, CombatSprite spriteCastingSpell, ResourceManager fcrm,
			boolean spellHasBeenSelected, int selectedLevel, KnownSpell selectedSpell, 
			StateInfo stateInfo, Graphics graphics, Color forefrontColor) {
		
		SpriteSheet spellSpriteSheets = fcrm.getSpriteSheet("spelllevel");
		
		graphics.setColor(Panel.COLOR_FOREFRONT);

		TacticalGame.ENGINE_CONFIGURATIOR.getPanelRenderer().render(198,
				160, 68,
				54, graphics, null);

		graphics.setColor(forefrontColor);
		StringUtils.drawString(spellName, 205, 158, graphics);
		if (spellHasBeenSelected)
		{
			graphics.drawImage(spellSpriteSheets.getSprite(0, 0), 204 + chooserOffset, 175);
			graphics.drawImage(spellSpriteSheets.getSprite(0, 3), 204 + chooserOffset, 191);
		}
		else
			selectedLevel = 0;

		for (int i = 0; i < selectedSpell.getMaxLevel(); i++)
		{
			graphics.drawImage(spellSpriteSheets.getSprite(0, 1), 204 + i * 14,
					183);
			
			if (i <= selectedLevel)
			{
				graphics.drawImage(spellSpriteSheets.getSprite(0, 2), 204 + i * 14,
						183);
			}
		}
		// graphics.drawString(spellName, 410, 399);
		StringUtils.drawString("Cost:", 205, 189, graphics);

		if (spriteCastingSpell.getCurrentMP() 
				< selectedSpell.getSpell().getCosts()[selectedLevel])
			graphics.setColor(Color.red);
		StringUtils.drawString(selectedSpell.getSpell().getCosts()[selectedLevel] + "", 245, 189, graphics);
	}

	@Override
	public void spellLevelChanged(int spellLevel) {
		this.spellLevel = spellLevel;
	}

	@Override
	public void update(long delta) {
		int x = spellLevel * 14;
		if (chooserOffset != x) {
			chooseMoveDelta += delta;
			
			while (chooseMoveDelta >= 12) {
				if (x > chooserOffset) {
					chooserOffset += 1;
				} else if (x < chooserOffset) {
					chooserOffset -= 1;
				}
				chooseMoveDelta -= 12;
			}
		}
	}

}
