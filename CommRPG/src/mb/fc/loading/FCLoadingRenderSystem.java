package mb.fc.loading;

import mb.fc.engine.CommRPG;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class FCLoadingRenderSystem
{
	private Graphics graphics;
	private GameContainer gc;

	public FCLoadingRenderSystem(GameContainer container) {
		graphics = container.getGraphics();
		this.gc = container;
	}

	public void update(LoadingStatus loading) {
		graphics.setColor(Color.white);
		graphics.drawString(CommRPG.GAME_TITLE, 15, gc.getHeight() - 30);

		/*
		if (loading.maxIndex > 0)
		{
			graphics.setColor(new Color(1 - 1 * (1.0f * loading.currentIndex / loading.maxIndex), (1.0f * loading.currentIndex / loading.maxIndex), 0));
			System.out.println(255 - 255 * (1.0f * loading.currentIndex / loading.maxIndex) + " " + 255 * (1.0f * loading.currentIndex / loading.maxIndex));
			graphics.fillRect(gc.getWidth() - 190, gc.getHeight() - 52, (float) (160 * (1.0 * loading.currentIndex / loading.maxIndex)), 20);
		}
		graphics.setColor(Color.white);
		graphics.drawRect(gc.getWidth() - 190, gc.getHeight() - 50, 160, 15);
		*/

		graphics.drawString("LOADING: " + loading.currentIndex + " / " + loading.maxIndex, gc.getWidth() - 185, gc.getHeight() - 30);
	}
}
