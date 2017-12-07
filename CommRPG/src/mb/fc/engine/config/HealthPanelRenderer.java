package mb.fc.engine.config;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;

public interface HealthPanelRenderer {
	enum PanelLocation {
		HERO_HEALTH,
		ENEMY_HEALTH,
		TARGET_HEALTH
	}
	public void displayHealthPanel(FCResourceManager fcrm, CombatSprite sprite, UnicodeFont panelFont, 
			PaddedGameContainer gc, Graphics graphics, PanelLocation panelLocation);
}
