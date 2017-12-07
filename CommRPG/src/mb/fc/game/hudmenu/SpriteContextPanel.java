package mb.fc.game.hudmenu;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import mb.fc.engine.config.HealthPanelRenderer;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;

public class SpriteContextPanel extends Panel
{
	private CombatSprite sprite;
	private HealthPanelRenderer healthPanelRenderer;
	private FCResourceManager fcrm;

	public SpriteContextPanel(PanelType menuType, CombatSprite sprite, 
			HealthPanelRenderer healthPanelRenderer, FCResourceManager fcrm, GameContainer gc) {
		super(menuType);
		this.sprite = sprite;
		this.healthPanelRenderer = healthPanelRenderer;
		this.fcrm = fcrm;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		HealthPanelRenderer.PanelLocation pl = HealthPanelRenderer.PanelLocation.HERO_HEALTH;
		switch (panelType)
		{
			case PANEL_ENEMY_HEALTH_BAR:
				pl = HealthPanelRenderer.PanelLocation.ENEMY_HEALTH;
				break;
			case PANEL_TARGET_HEALTH_BAR:
				pl = HealthPanelRenderer.PanelLocation.TARGET_HEALTH;
				break;
			default:
				break;
		}
		
		healthPanelRenderer.displayHealthPanel(fcrm, sprite, PANEL_FONT, gc, graphics, pl);
	}
}
