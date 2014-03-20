package mb.fc.system.loading;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import mb.fc.resource.LoadingComp;

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

	public FCLoadingRenderSystem(GameContainer container) {
		super(Aspect.getAspectForAll(LoadingComp.class));
		graphics = container.getGraphics();
		this.gc = container;
	}

	@Override
	protected void process(Entity e) {
		LoadingComp loading = loadingMapper.get(e);
		graphics.setColor(Color.white);
		graphics.drawString("Chronicles of Veridocia", 15, gc.getHeight() - 30);
		graphics.drawString("LOADING: " + loading.currentIndex + " / " + loading.maxIndex, gc.getWidth() - 185, gc.getHeight() - 30);
	}	
}
