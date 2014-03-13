package mb.gl2.loading;

import java.io.IOException;

import org.newdawn.slick.SlickException;

import com.artemis.Entity;

public abstract class ResourceManager
{
	public abstract void addResource(String resource, Entity entity, int currentIndex, int maxIndex) throws IOException, SlickException;
	
	public abstract void initializeLoadingComp(Entity entity);
}
