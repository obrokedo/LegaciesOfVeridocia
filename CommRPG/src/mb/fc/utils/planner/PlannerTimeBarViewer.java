package mb.fc.utils.planner;

import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mb.fc.cinematic.event.CinematicEvent;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultHeaderRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultMarkerRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultTimeBarRenderer;

public class PlannerTimeBarViewer extends TimeBarViewer implements AdjustmentListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<TimeBarMarkerImpl> markers;

	public PlannerTimeBarViewer(ArrayList<CinematicEvent> ces)
	{		
		this.markers = new ArrayList<TimeBarMarkerImpl>();
		generateGraph(ces);
		
		setDrawOverlapping(false);
		setRowHeight(50);
		
		// int duration = (int) (currentTime / 1000) + 1;
		this.setInitialDisplayRange(new JaretDate(0), 8);
		this.setTimeBarRenderer(new ZTimeBarRenderer());	
		this.setOptimizeScrolling(true);
		this._xScrollBar.addAdjustmentListener(this);
		this._xScrollBar.setUnitIncrement(50);
		this._xScrollBar.setBlockIncrement(50);
		this._xScrollBar.getModel().addChangeListener(new Listen());
	}
	
	@Override
	public void adjustmentValueChanged(AdjustmentEvent arg0) {
		System.out.println("ADJUST");
		this.validate();
		this.repaint();		
		System.out.println(this.getSize());
	}		

	public void generateGraph(ArrayList<CinematicEvent> ces)
	{
		DefaultTimeBarRowModel cameraRow = new DefaultTimeBarRowModel(new DefaultRowHeader("Camera"));
		ZIntervalImpl cameraInterval = null;
		DefaultTimeBarRowModel systemRow = new DefaultTimeBarRowModel(new DefaultRowHeader("System"));
		DefaultTimeBarRowModel soundRow = new DefaultTimeBarRowModel(new DefaultRowHeader("Sound"));
		ZIntervalImpl soundInterval = null;
		
		for (TimeBarMarkerImpl m : markers)			
			this.remMarker(m);
		
		markers.clear();
		
		Hashtable<String, ActorBar> rowsByName = new Hashtable<String, ActorBar>();
		
		long currentTime = 0;
		
		for (CinematicEvent ce : ces)
		{
			switch (ce.getType())
			{
				// Actor Stuff
				case ADD_ACTOR:
					DefaultTimeBarRowModel dt = new DefaultTimeBarRowModel(new DefaultRowHeader("Actor: " + (String) ce.getParam(2)));
					ActorBar ab = new ActorBar(dt, (int) ce.getParam(0), (int) ce.getParam(1));
					rowsByName.put((String) ce.getParam(2), ab);					
					
					// Check invisible
					if (!(boolean) ce.getParam(5))
					{
						ZIntervalImpl zi = new ZIntervalImpl("Invisible");
						zi.setBegin(new JaretDate(currentTime));
						zi.setEnd(new JaretDate(currentTime + 100));
						ab.indefiniteIntervals.put("invisible", zi);
						ab.dt.addInterval(zi);
					}
					
					if (currentTime != 0)
					{
						ZIntervalImpl zi = new ZIntervalImpl("Not in scene");
						zi.setBegin(new JaretDate(0));
						zi.setEnd(new JaretDate(currentTime));
						dt.addInterval(zi);
					}
					break;
				case MOVE:
					handleMove(currentTime, ce, rowsByName, "Move ");
					break;
				case MOVE_ENFORCE_FACING:
					handleMove(currentTime, ce, rowsByName, "Move Face Enforced ");
					break;
				case HALTING_MOVE:										
					currentTime += handleMove(currentTime, ce, rowsByName, "Halting Move to ");
					break;	
				case LOOP_MOVE:
					 ab = rowsByName.get(ce.getParam(0));
					
					ZIntervalImpl zi = new ZIntervalImpl("Loop Move " + (int) ce.getParam(1) + " " + (int) ce.getParam(2));
					zi.setBegin(new JaretDate(currentTime));
					zi.setEnd(new JaretDate(currentTime + 1));
					ab.indefiniteIntervals.put("loopmove", zi);
					ab.dt.addInterval(zi);
					break;
				case STOP_LOOP_MOVE:
					ab = rowsByName.get(ce.getParam(0));
											
					zi = ab.indefiniteIntervals.get("loopmove");
					zi.setEnd(new JaretDate(currentTime));
					ab.indefiniteIntervals.remove("loopmove");
					break;
					
				case SPIN:
					ab = rowsByName.get(ce.getParam(0));
						
					zi = new ZIntervalImpl("Spin");
					zi.setBegin(new JaretDate(currentTime));
					
					if ((int) ce.getParam(2) != -1)
						zi.setEnd(new JaretDate(currentTime + (int) ce.getParam(2)));
					else
					{
						zi.setEnd(new JaretDate(currentTime + 1));
						ab.indefiniteIntervals.put("spin", zi);
					}
					ab.dt.addInterval(zi);
					break;
				case STOP_SPIN:
					ab = rowsByName.get(ce.getParam(0));
					
					zi = ab.indefiniteIntervals.get("spin");
					zi.setEnd(new JaretDate(currentTime));
					ab.indefiniteIntervals.remove("spin");
					break;
				
				case SHRINK:
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Shrink");
					zi.setBegin(new JaretDate(currentTime));					
					zi.setEnd(new JaretDate(currentTime + (int) ce.getParam(1)));
					ab.dt.addInterval(zi);
					break;
				case GROW:
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Grow");
					zi.setBegin(new JaretDate(currentTime));					
					zi.setEnd(new JaretDate(currentTime + (int) ce.getParam(1)));
					ab.dt.addInterval(zi);
					break;
				case QUIVER:
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Quiver");
					zi.setBegin(new JaretDate(currentTime));		
					zi.setEnd(new JaretDate(currentTime + 1));
					ab.indefiniteIntervals.put("quiver", zi);
					ab.dt.addInterval(zi);
					break;
				case LAY_ON_SIDE:
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Lay on Side");
					zi.setBegin(new JaretDate(currentTime));	
					zi.setEnd(new JaretDate(currentTime + 1));
					ab.indefiniteIntervals.put("layonside", zi);
					ab.dt.addInterval(zi);
					break;
				case FALL_ON_FACE:
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Fall on Face");
					zi.setBegin(new JaretDate(currentTime));			
					zi.setEnd(new JaretDate(currentTime + 1));
					ab.indefiniteIntervals.put("fallonface", zi);
					ab.dt.addInterval(zi);
					break;
				case FLASH:
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Flash");
					zi.setBegin(new JaretDate(currentTime));
					
					if ((int) ce.getParam(2) != -1)
						zi.setEnd(new JaretDate(currentTime + (int) ce.getParam(2)));
					else
					{
						ab.indefiniteIntervals.put("flash", zi);
						zi.setEnd(new JaretDate(currentTime + 1));
					}
					ab.dt.addInterval(zi);
					break;
				case NOD:
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Nod");
					zi.setBegin(new JaretDate(currentTime));					
					zi.setEnd(new JaretDate(currentTime + 500));
					ab.dt.addInterval(zi);
					break;
				case HEAD_SHAKE:
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Head Shake");
					zi.setBegin(new JaretDate(currentTime));					
					zi.setEnd(new JaretDate(currentTime + 750));
					ab.dt.addInterval(zi);
					break;				
				case STOP_SE:
					ab = rowsByName.get(ce.getParam(0));
					stopEffects(currentTime, ab, "quiver");
					stopEffects(currentTime, ab, "flash");
					stopEffects(currentTime, ab, "fallonface");
					stopEffects(currentTime, ab, "layonside");
					break;
				
				case VISIBLE:
					ab = rowsByName.get(ce.getParam(0));
					if ((boolean) ce.getParam(1))
					{
						zi = ab.indefiniteIntervals.get("invisible");
						if (zi != null)
						{
							zi.setEnd(new JaretDate(currentTime));
							ab.indefiniteIntervals.remove("invisible");
						}
					}
					else
					{
						zi = new ZIntervalImpl("Invisible");
						zi.setBegin(new JaretDate(currentTime));
						zi.setEnd(new JaretDate(currentTime + 1));
						ab.indefiniteIntervals.put("invisible", zi);
						ab.dt.addInterval(zi);
					}					
					break;
				case REMOVE_ACTOR:
					ab = rowsByName.get(ce.getParam(0));
					zi = new ZIntervalImpl("Not in scene");
					zi.setBegin(new JaretDate(currentTime));
					zi.setEnd(new JaretDate(currentTime + 1));
					ab.indefiniteIntervals.put("notinscene", zi);
					ab.dt.addInterval(zi);
					break;
				case FACING:
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Set Facing");
					zi.setBegin(new JaretDate(currentTime));					
					zi.setEnd(new JaretDate(currentTime + 100));
					ab.dt.addInterval(zi);
					break;
				
				case STOP_ANIMATION:
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Stop Animating");
					zi.setBegin(new JaretDate(currentTime));					
					zi.setEnd(new JaretDate(currentTime + 100));
					ab.dt.addInterval(zi);
					break;
				case HALTING_ANIMATION:	
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Halting Animating " + (String) ce.getParam(1));
					zi.setBegin(new JaretDate(currentTime));					
					currentTime += (int) ce.getParam(2);
					zi.setEnd(new JaretDate(currentTime));					
					ab.dt.addInterval(zi);
					break;
				case ANIMATION:	
					ab = rowsByName.get(ce.getParam(0));
					
					zi = new ZIntervalImpl("Animation " + (String) ce.getParam(1));
					zi.setBegin(new JaretDate(currentTime));					
					zi.setEnd(new JaretDate(currentTime));
					ab.dt.addInterval(zi);
					break;	
				
				
				// Camera Stuff
				case CAMERA_MOVE:
					if (cameraInterval != null)
						cameraInterval.setEnd(new JaretDate(currentTime));
					
					zi = new ZIntervalImpl("Move Camera to  " + (int) ce.getParam(0) + " " + (int) ce.getParam(1));
					zi.setBegin(new JaretDate(currentTime));					
					zi.setEnd(new JaretDate(currentTime + (int) ce.getParam(2)));
					cameraInterval = zi;
					cameraRow.addInterval(zi);										
					break;
				
				case CAMERA_CENTER:
					if (cameraInterval != null)
						cameraInterval.setEnd(new JaretDate(currentTime));
					
					zi = new ZIntervalImpl("Center Camera  " + (int) ce.getParam(0) + " " + (int) ce.getParam(1));
					zi.setBegin(new JaretDate(currentTime));					
					zi.setEnd(new JaretDate(currentTime + 100));
					cameraInterval = zi;
					cameraRow.addInterval(zi);			
					break;
				
				case CAMERA_FOLLOW:
					if (cameraInterval != null)
						cameraInterval.setEnd(new JaretDate(currentTime));
					
					zi = new ZIntervalImpl("Camera Follow  " + (String) ce.getParam(0));
					zi.setBegin(new JaretDate(currentTime));					
					zi.setEnd(new JaretDate(currentTime + 100));
					cameraInterval = zi;
					cameraRow.addInterval(zi);							
					break;
				
				case CAMERA_SHAKE:
					zi = new ZIntervalImpl("Camera Shake");
					zi.setBegin(new JaretDate(currentTime));					
					zi.setEnd(new JaretDate(currentTime + (int) ce.getParam(0)));
					cameraInterval = zi;
					cameraRow.addInterval(zi);		
					
					break;	
				
				// System Stuff
				case SPEECH:
					zi = new ZIntervalImpl("Speech");
					zi.setBegin(new JaretDate(currentTime));
					TimeBarMarkerImpl tb = new TimeBarMarkerImpl(false, new JaretDate(currentTime));
					this.markers.add(tb);
					addMarker(tb);
					currentTime += 100;
					tb = new TimeBarMarkerImpl(false, new JaretDate(currentTime));
					this.markers.add(tb);
					addMarker(tb);
					zi.setEnd(new JaretDate(currentTime));					
					systemRow.addInterval(zi);		
					
					break;			
				case WAIT:
					zi = new ZIntervalImpl("Wait");
					zi.setBegin(new JaretDate(currentTime));
					currentTime += (int) ce.getParam(0);
					zi.setEnd(new JaretDate(currentTime));
					systemRow.addInterval(zi);
					
					break;
					/*
				case ASSOCIATE_NPC_AS_ACTOR:
					for (Sprite s : stateInfo.getSprites())
					{
						if (s.getSpriteType() == Sprite.TYPE_NPC && ((NPCSprite) s).getUniqueNPCId() == (int) ce.getParam(0))
						{
							actors.put((String) ce.getParam(1), new CinematicActor((AnimatedSprite) s));
							break;
						}
					}
					break;
					
				// Music stuff
				 */
				case PLAY_MUSIC:
					if (soundInterval != null)
						soundInterval.setEnd(new JaretDate(currentTime));
					
					zi = new ZIntervalImpl("Play Music " + (String) ce.getParam(0));
					zi.setBegin(new JaretDate(currentTime));
					zi.setEnd(new JaretDate(currentTime));
					soundInterval = zi;
					soundRow.addInterval(zi);
					break;
				case PAUSE_MUSIC:
					if (soundInterval != null)
						soundInterval.setEnd(new JaretDate(currentTime));
									
					soundInterval = null;
					
					break;
				case RESUME_MUSIC:					
					zi = new ZIntervalImpl("Resume Music");
					zi.setBegin(new JaretDate(currentTime));
					zi.setEnd(new JaretDate(currentTime));
					soundInterval = zi;
					soundRow.addInterval(zi);
					break;
				case FADE_MUSIC:
					
					zi = new ZIntervalImpl("Fade Music");
					zi.setBegin(new JaretDate(currentTime));
					zi.setEnd(new JaretDate(currentTime + (int) ce.getParam(0)));
					soundRow.addInterval(zi);					
					break;
				case PLAY_SOUND:
					zi = new ZIntervalImpl("Play Music");
					zi.setBegin(new JaretDate(currentTime));
					zi.setEnd(new JaretDate(currentTime + 100));
					soundRow.addInterval(zi);					
					break;	
			}	
		}
		
		DefaultTimeBarModel model = new DefaultTimeBarModel();
		model.addRow(systemRow);
		model.addRow(cameraRow);
		model.addRow(soundRow);
		
		
		for (ActorBar ab : rowsByName.values())
		{
			for (ZIntervalImpl zi : ab.indefiniteIntervals.values())
			{
				zi.setEnd(new JaretDate(currentTime));
			}
			
			model.addRow(ab.dt);
		}
		
		if (soundInterval != null)
			soundInterval.setEnd(new JaretDate(currentTime));
		
		if (cameraInterval != null)
			cameraInterval.setEnd(new JaretDate(currentTime));
		
		
		this.setModel(model);	
		setHeaderRenderer(new ZHeaderRenderer());
		this.setSecondsDisplayed(8, false);
		this.repaint();		
		
	}
	
	private void stopEffects(long currentTime, ActorBar ab, String command)
	{
		ZIntervalImpl zi = ab.indefiniteIntervals.get(command);
		if (zi != null)
		{
			zi.setEnd(new JaretDate(currentTime));
			ab.indefiniteIntervals.remove(command);
		}
	}
	
	private long handleMove(long currentTime, CinematicEvent ce, Hashtable<String, ActorBar> rowsByName, String title)
	{
		
		ActorBar ab = rowsByName.get(ce.getParam(3));
		
		int xDistance = Math.abs(ab.locX - (int) ce.getParam(0));
		int yDistance = Math.abs(ab.locY - (int) ce.getParam(1));
		long duration = ((int) (Math.ceil(xDistance / (float) ce.getParam(2))) + (int) (Math.ceil(yDistance / (float) ce.getParam(2)))) * 20;
		
		ab.locX = (int) ce.getParam(0);
		ab.locY = (int) ce.getParam(1);
		
		ZIntervalImpl zi = new ZIntervalImpl(title + (int) ce.getParam(0) + " " + (int) ce.getParam(1));
		zi.setBegin(new JaretDate(currentTime));
		
		zi.setEnd(new JaretDate(currentTime + duration));
		ab.dt.addInterval(zi);
		return duration;
	}
	
	public class ZHeaderRenderer extends DefaultHeaderRenderer
    {
		@Override
		public int getWidth() {
			return 200;
		}	    				
    }
    
    public class ZIntervalImpl extends IntervalImpl
    {
    	public String title;
    	
    	public ZIntervalImpl(String title)
    	{
    		this.title = title;
    	}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return title;
		}	    	
    }
    
    public class ActorBar
    {
    	public DefaultTimeBarRowModel dt;
    	public int locX;
    	public int locY;
    	public float moveSpeed;
    	public long startMove;
    	public Hashtable<String, ZIntervalImpl> indefiniteIntervals;
    	
		public ActorBar(DefaultTimeBarRowModel dt, int locX, int locY) {
			super();
			this.dt = dt;
			this.locX = locX;
			this.locY = locY;
			indefiniteIntervals = new Hashtable<String, ZIntervalImpl>();
		}
    }
    
    public class ZTimeBarMarker extends TimeBarMarkerImpl
    {

		public ZTimeBarMarker(JaretDate date) {
			super(false, date);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return "Can you see me";
		}
    	
    }
    
    public class ZMarkerRenderer extends DefaultMarkerRenderer
    {

		@Override
		public void renderMarker(TimeBarViewerDelegate delegate,
				Graphics graphics, TimeBarMarker marker, int x,
				boolean isDragged) {
			// TODO Auto-generated method stub 
			super.renderMarker(delegate, graphics, marker, x, isDragged);
		}
    	
    }
    
    public class ZTimeBarRenderer extends DefaultTimeBarRenderer
    {
    
    }
    
    public class Listen implements ChangeListener
    {
    	@Override
    	public void stateChanged(ChangeEvent e) {
    		// TODO Auto-generated method stub
    		System.out.println("FART");
    	}
    }
}
