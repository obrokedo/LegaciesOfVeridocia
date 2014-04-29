package mb.fc.cinematic;

import java.util.Comparator;

public class CinematicActorComparator implements Comparator<CinematicActor> {
	@Override
	public int compare(CinematicActor o1, CinematicActor o2) {
		return (int) (o1.getLocY() - o2.getLocY());
	}
}
