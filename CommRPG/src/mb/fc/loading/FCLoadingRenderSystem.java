package mb.fc.loading;

import mb.fc.engine.CommRPG;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;

public class FCLoadingRenderSystem extends EntityProcessingSystem
{
	private Graphics graphics;
	private GameContainer gc;
	@Mapper ComponentMapper<LoadingComp> loadingMapper;

	@SuppressWarnings("unchecked")
	public FCLoadingRenderSystem(GameContainer container) {
		super(Aspect.getAspectForAll(LoadingComp.class));
		graphics = container.getGraphics();
		this.gc = container;
	}

	@Override
	protected void process(Entity e) {
		LoadingComp loading = loadingMapper.get(e);
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

		graphics.drawString("LOADING: " + loading.currentIndex + " / " + loading.maxIndex, gc.getWidth() - 185, gc.getHeight() - 50);
	}
}
