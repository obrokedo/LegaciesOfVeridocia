package lov.engine.config.loading;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import tactical.loading.LoadingStatus;

public class LOVLogoLoadRenderer extends LOVLoadRenderer  {
	private Image logo;

	public LOVLogoLoadRenderer(GameContainer container) {
		super(container);
	}

	@Override
	public void render(LoadingStatus loading) {
		graphics.setColor(Color.white);
		graphics.fillRect(0, 200, gc.getWidth(), gc.getHeight() - 400);
		super.render(loading);
		logo.draw((gc.getWidth() - logo.getWidth()) / 2, (gc.getHeight() - logo.getHeight()) / 2);
		
	}

	@Override
	public void initialize() throws SlickException {
		super.initialize();
		logo = new Image("image/engine/EngineLogo.png");
	}

	
}
