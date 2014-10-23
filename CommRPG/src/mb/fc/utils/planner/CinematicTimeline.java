package mb.fc.utils.planner;

import java.util.ArrayList;
import java.util.Hashtable;

import mb.fc.utils.planner.PlannerTimeBarViewer.ActorBar;
import mb.fc.utils.planner.PlannerTimeBarViewer.CameraLocation;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;

public class CinematicTimeline
{
	public DefaultTimeBarRowModel systemRow;
	public DefaultTimeBarRowModel soundRow;
	public DefaultTimeBarRowModel cameraRow;
	public Hashtable<String, ActorBar> rowsByName;
	public ArrayList<TimeBarMarkerImpl> markers;
	public ArrayList<Long> cinematicTime;
	public int duration;
	public ArrayList<CameraLocation> cameraLocations;

	public CinematicTimeline() {
		super();
	}
}
