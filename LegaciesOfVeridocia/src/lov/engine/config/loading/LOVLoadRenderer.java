package lov.engine.config.loading;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import lov.engine.config.LOVHealthPanelRenderer;
import tactical.loading.LoadingScreenRenderer;
import tactical.loading.LoadingStatus;
import tactical.loading.ResourceManager;

public class LOVLoadRenderer extends LoadingScreenRenderer {
	private Image loadingImage;
	private SpriteSheet healthBarSS;

	public LOVLoadRenderer(GameContainer container) {
		super(container);
	}

	@Override
	public void render(LoadingStatus loading) {
		loadingImage.draw((gc.getWidth() - loadingImage.getWidth() * 1.5f) / 2, gc.getHeight() - 80, 1.5f);
		graphics.scale(2, 2);
		LOVHealthPanelRenderer.renderBar((int) (Math.max((loading.currentIndex * 1.0f) / loading.maxIndex, 0f) * 100), 100,
			149, gc.getWidth() / 4 - 87, gc.getHeight() / 2 - 25, graphics, healthBarSS);
		graphics.resetTransform();
	}

	@Override
	public void initialize() throws SlickException {
		loadingImage = new Image("image/Loading.png", ResourceManager.TRANSPARENT);
		healthBarSS = new SpriteSheet(new Image("image/HealthBars.png"), 2, 8);
	}
}
