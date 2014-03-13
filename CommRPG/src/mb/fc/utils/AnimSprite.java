package mb.fc.utils;

import java.io.Serializable;

public class AnimSprite implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public int x, y, imageIndex;

	public AnimSprite(int x, int y, int imageIndex) {
		super();
		this.x = x;
		this.y = y;
		this.imageIndex = imageIndex;
	}
}
