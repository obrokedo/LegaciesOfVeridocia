package mb.fc.map;

import org.newdawn.slick.geom.Rectangle;

public class Roof 
{
	private Rectangle rectangle;
	private boolean isVisible;
	
	public Roof(Rectangle rectangle) {
		super();
		this.rectangle = rectangle;
		isVisible = true;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}
}
