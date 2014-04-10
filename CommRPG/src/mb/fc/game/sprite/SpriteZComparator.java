package mb.fc.game.sprite;

import java.util.Comparator;

public class SpriteZComparator implements Comparator<Sprite> {

	@Override
	public int compare(Sprite o1, Sprite o2) {
		return o1.getLocY() - o2.getLocY();
	}
}
