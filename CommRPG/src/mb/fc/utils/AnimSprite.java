package mb.fc.utils;

import java.io.Serializable;

public class AnimSprite implements Serializable
{
	private static final long serialVersionUID = 1L;

	public int x, y, imageIndex, angle;

	public AnimSprite(int x, int y, int imageIndex, int angle) {
		super();
		this.x = x;
		this.y = y;
		this.imageIndex = imageIndex;
		this.angle = angle;
	}
}
