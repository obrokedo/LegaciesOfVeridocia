package mb.fc.loading;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import mb.fc.engine.config.LOVHealthPanelRenderer;

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
		LOVHealthPanelRenderer.renderBar((int) ((loading.currentIndex * 1.0f) / loading.maxIndex * 100), 
			149, gc.getWidth() / 4 - 87, gc.getHeight() / 2 - 25, graphics, healthBarSS);
		graphics.resetTransform();
	}

	@Override
	public void initialize() throws SlickException {
		loadingImage = new Image("image/Loading.png", ResourceManager.TRANSPARENT);
		healthBarSS = new SpriteSheet(new Image("image/HealthBars.png"), 2, 8);
	}
}
