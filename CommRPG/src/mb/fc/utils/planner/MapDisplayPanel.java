package mb.fc.utils.planner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import mb.fc.cinematic.event.CinematicEvent;
import mb.fc.loading.MapParser;
import mb.fc.loading.PlannerMap;
import mb.fc.loading.PlannerTilesetParser;
import mb.fc.loading.TextParser;
import mb.fc.map.MapObject;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;
import mb.fc.utils.planner.PlannerTimeBarViewer.ActorBar;
import mb.fc.utils.planner.PlannerTimeBarViewer.StaticSprite;

import org.newdawn.slick.SlickException;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;

public class MapDisplayPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 1L;

	private PlannerMap plannerMap;
	private MapObject selectedMO;
	private MapPanel mapPanel;
	private CinematicTimeline timeline;
	private int mouseX, mouseY;
	private JPopupMenu systemPopup, actorPopup, actorMovePopup;
	private final Color UNSELECTED_MO_FILL_COLOR = new Color(0, 0, 255, 50);
	private final Color UNSELECTED_MO_LINE_COLOR = new Color(0, 0, 255);

	private final Color SELECTED_MO_FILL_COLOR = new Color(0, 255, 0, 50);
	private final Color SELECTED_MO_LINE_COLOR = new Color(0, 255, 0);

	private final Color SPRITE_FILL_COLOR = new Color(230, 230, 230, 50);
	private final Color SPRITE_LINE_COLOR = new Color(230, 230, 230);

	private ArrayList<Point> actorLocations = new ArrayList<Point>();
	private ArrayList<Point> spriteLocations = new ArrayList<Point>();
	private int selectedActor = -1;
	private int popupType = 0;
	private Point cameraLocation = null;

	private PlannerContainer currentPC;

	public MapDisplayPanel(String mapFile, MapPanel mapPanel)
	{
		systemPopup = new JPopupMenu();
		systemPopup.add(createMenuItem("Wait"));
		systemPopup.add(createMenuItem("Add Actor"));
		systemPopup.add(createMenuItem("Establish Sprite as Actor"));
		systemPopup.add(createMenuItem("Add Static Sprite"));
		systemPopup.add(createMenuItem("Remove Static Sprite"));
		systemPopup.add(createMenuItem("Play Music"));
		systemPopup.add(createMenuItem("Pause Music"));
		systemPopup.add(createMenuItem("Resume Music"));
		systemPopup.add(createMenuItem("Fade Out Music"));
		systemPopup.add(createMenuItem("Play Sound"));
		systemPopup.add(createMenuItem("Fade in from black"));
		systemPopup.add(createMenuItem("Fade to black"));
		systemPopup.add(createMenuItem("Flash Screen"));
		systemPopup.add(createMenuItem("Camera Pan"));
		systemPopup.add(createMenuItem("Shake Camera"));
		systemPopup.add(createMenuItem("Show Speech Box"));
		systemPopup.add(createMenuItem("Load Map"));
		systemPopup.add(createMenuItem("Start Battle"));
		systemPopup.add(createMenuItem("Exit Game"));



		actorPopup = new JPopupMenu();
		actorPopup.add(createMenuItem("Spin Actor"));
		actorPopup.add(createMenuItem("Stop Actor Spinning"));
		actorPopup.add(createMenuItem("Set Actor Facing"));
		actorPopup.add(createMenuItem("Shrink Actor"));
		actorPopup.add(createMenuItem("Grow Actor"));
		actorPopup.add(createMenuItem("Start Actor Trembling"));
		actorPopup.add(createMenuItem("Start Actor Agitate"));
		actorPopup.add(createMenuItem("Actor Fall on Face"));
		actorPopup.add(createMenuItem("Actor Lay on Side Right"));
		actorPopup.add(createMenuItem("Actor Lay on Side Left"));
		actorPopup.add(createMenuItem("Actor Lay on Back"));
		actorPopup.add(createMenuItem("Actor Flash"));
		actorPopup.add(createMenuItem("Actor Nod"));
		actorPopup.add(createMenuItem("Actor Shake Head"));
		actorPopup.add(createMenuItem("Stop Actor Special Effect"));
		actorPopup.add(createMenuItem("Set Actor Visiblity"));
		actorPopup.add(createMenuItem("Halting Animation"));
		actorPopup.add(createMenuItem("Animation"));
		actorPopup.add(createMenuItem("Stop Animation"));
		actorPopup.add(createMenuItem("Render on Top"));
		actorPopup.add(createMenuItem("Render on Normal"));
		actorPopup.add(createMenuItem("Set Camera follows actor"));
		actorPopup.add(createMenuItem("Stop Actor Looping Move"));
		actorPopup.add(createMenuItem("Remove Actor"));

		actorMovePopup = new JPopupMenu();
		actorMovePopup.add(createMenuItem("Halting Move"));
		actorMovePopup.add(createMenuItem("Move"));
		actorMovePopup.add(createMenuItem("Move Forced Facing"));
		actorMovePopup.add(createMenuItem("Move Actor in Loop"));

		plannerMap = new PlannerMap();
		this.mapPanel = mapPanel;

		try {
			MapParser.parseMap(mapFile, plannerMap, new PlannerTilesetParser(), null);
		} catch (IOException | SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setPreferredSize(new Dimension(plannerMap.getMapWidthInPixels(), plannerMap.getMapHeightInPixels()));
		this.addMouseListener(this);
		this.addMouseMotionListener(mapPanel);
		this.addMouseMotionListener(this);
	}

	private JMenuItem createMenuItem(String menuItem)
	{
		JMenuItem jmi = new JMenuItem(menuItem);
		jmi.addActionListener(this);
		jmi.setActionCommand(menuItem);
		return jmi;
	}

	public void setStaticSpritesAtTime(int time)
	{
		spriteLocations.clear();
		for (StaticSprite ss : timeline.staticSprites)
		{
			if (ss.timeAdded <= time && (ss.timeRemoved > time || ss.timeRemoved == 0))
				spriteLocations.add(new Point(ss.locX, ss.locY));
		}
	}

	public ArrayList<String> getSystemValuesAtTime(int time)
	{
		ArrayList<String> values = new ArrayList<String>();
		addIntervals(timeline.systemRow.getIntervals(new JaretDate(time)), values);
		return values;
	}

	public ArrayList<String> getCameraValuesAtTime(int time)
	{
		ArrayList<String> values = new ArrayList<String>();
		addIntervals(timeline.cameraRow.getIntervals(new JaretDate(time)), values);
		return values;
	}

	public ArrayList<String> getSoundValuesAtTime(int time)
	{
		ArrayList<String> values = new ArrayList<String>();
		addIntervals(timeline.soundRow.getIntervals(new JaretDate(time)), values);
		return values;
	}

	public ArrayList<ArrayList<String>> getActorValuesAtTime(int time, ArrayList<String> names)
	{
		actorLocations.clear();
		ArrayList<ArrayList<String>> lol = new ArrayList<ArrayList<String>>();

		ArrayList<Entry<String, ActorBar>> listOfActorEntrys = new ArrayList<>(timeline.rowsByName.entrySet());
		Collections.sort(listOfActorEntrys, new Comparator<Entry<String, ActorBar>>() {

			@Override
			public int compare(Entry<String, ActorBar> arg0,
					Entry<String, ActorBar> arg1) {
				return arg0.getKey().compareTo(arg1.getKey());
			}});

		for (Entry<String, ActorBar> ab : listOfActorEntrys)
		{
			ArrayList<String> values = new ArrayList<String>();

			if (ab.getValue().isActorInScene(time))
			{
				names.add(ab.getKey());

				boolean moving = false;

				Point actorPoint = ab.getValue().getActorLocationAtTime(time);

				values.add("Current Location: " + actorPoint.x + " " + actorPoint.y);
				values.add("Actor is Moving: " + moving);
				actorLocations.add(actorPoint);

				addIntervals(ab.getValue().dt.getIntervals(new JaretDate(time)), values);

				lol.add(values);
			}
		}

		return lol;
	}

	public Point getActorLocationAtTime(int time, String name)
	{
		if (!timeline.rowsByName.containsKey(name))
			return new Point(0, 0);

		ActorBar ab = timeline.rowsByName.get(name);

		if (ab.isActorInScene(time))
			return ab.getActorLocationAtTime(time);

		return new Point(0, 0);
	}

	private void addIntervals(List<Interval> intervals, ArrayList<String> values)
	{
		for (Interval i : intervals)
		{
			values.add(i.toString());
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		for (int i = 0; i < plannerMap.getMapWidth(); i++)
		{
			for (int j = 0; j < plannerMap.getMapHeight(); j++)
			{
				for (int k = 0; k < 3; k++)
				{
					if (k == 0 || plannerMap.getMapLayer(k)[j][i] != 0)
						g.drawImage(plannerMap.getPlannerSprite(plannerMap.getMapLayer(k)[j][i]), i * plannerMap.getTileRenderWidth(), j * plannerMap.getTileRenderHeight(), this);
				}
			}
		}

		if (PlannerFrame.SHOW_CIN_LOCATION)
		{
			for (MapObject mo : plannerMap.getMapObjects())
			{
				int[] xP, yP;
				xP = new int[mo.getShape().getPointCount()];
				yP = new int[mo.getShape().getPointCount()];
				for (int i = 0; i < xP.length; i++)
				{
					xP[i] = (int) mo.getShape().getPoint(i)[0];
					yP[i] = (int) mo.getShape().getPoint(i)[1];
				}

				if (mo != this.selectedMO)
					g.setColor(UNSELECTED_MO_FILL_COLOR);
				else
					g.setColor(SELECTED_MO_FILL_COLOR);
				g.fillPolygon(xP, yP, xP.length);

				if (mo != this.selectedMO)
					g.setColor(UNSELECTED_MO_LINE_COLOR);
				else
					g.setColor(SELECTED_MO_LINE_COLOR);
				g.drawPolygon(xP, yP, xP.length);
			}
		}

		for (Point sp : spriteLocations)
		{
			g.setColor(new Color(230, 230, 230, 50));
			g.fillRect(sp.x, sp.y, plannerMap.getTileEffectiveWidth(), plannerMap.getTileEffectiveHeight());
			g.setColor(new Color(230, 230, 230));
			g.drawRect(sp.x, sp.y, plannerMap.getTileEffectiveWidth(), plannerMap.getTileEffectiveHeight());
		}

		g.setColor(Color.red);
		int i = 0;
		for (Point ap : actorLocations)
		{
			g.setColor(Color.red);
			if (selectedActor != -1 && i == selectedActor)
				g.setColor(Color.yellow);
			g.fillRect(ap.x, ap.y, plannerMap.getTileEffectiveWidth(), plannerMap.getTileEffectiveHeight());
			g.setColor(Color.white);
			g.drawRect(ap.x, ap.y, plannerMap.getTileEffectiveWidth(), plannerMap.getTileEffectiveHeight());
			i++;
		}

		g.setColor(Color.white);
		g.drawRect(mouseX, mouseY, plannerMap.getTileEffectiveWidth(), plannerMap.getTileEffectiveHeight());

		if (cameraLocation != null)
		{
			g.drawRect(cameraLocation.x, cameraLocation.y, 320, 240);
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void setSelectedActor(int actor)
	{
		selectedActor = actor;
		this.repaint();
	}

	@Override
	public void mousePressed(MouseEvent m) {

		if (m.getButton() == MouseEvent.BUTTON1)
		{
			MapObject selected = null;
			int size = Integer.MAX_VALUE;
			boolean foundActor = false;

			int i = 0;
			for (Point al : actorLocations)
			{
				Rectangle r = new Rectangle(al, new Dimension(plannerMap.getTileEffectiveWidth(), plannerMap.getTileEffectiveHeight()));
				if (r.contains(m.getPoint()))
				{
					mapPanel.setActorSelected(i);
					foundActor = true;
					break;
				}
				i++;
			}

			if (!foundActor)
			{
				mapPanel.setActorSelected(-1);

				if (PlannerFrame.SHOW_CIN_LOCATION)
				{
					for (MapObject mo : plannerMap.getMapObjects())
					{
						if (mo.getShape().contains(m.getX(), m.getY()))
						{
							int newSize = mo.getWidth() * mo.getHeight();

							if (newSize < size)
							{
								selected = mo;
								size = newSize;
							}
						}
					}
				}
			}

			if (selected != null)
			{
				this.selectedMO = selected;
				mapPanel.locationClicked(selectedMO);
			}
			else
			{
				this.selectedMO = null;
			}
		}
		else if (m.getButton() == MouseEvent.BUTTON3)
		{
			if (selectedActor == -1)
				systemPopup.show(this, m.getX(), m.getY());
			else
			{
				if ((new Rectangle(actorLocations.get(selectedActor), new Dimension(plannerMap.getMapEffectiveWidth(), plannerMap.getMapEffectiveHeight()))).contains(m.getPoint()))
				{
					popupType = 1;
					actorPopup.show(this, m.getX(), m.getY());
				}
				else
				{
					popupType = 2;
					actorMovePopup.show(this, m.getX(), m.getY());
				}
			}
		}

		this.repaint();
	}

	public PlannerMap getPlannerMap() {
		return plannerMap;
	}

	public MapObject getSelectedMO() {
		return selectedMO;
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	public CinematicTimeline getTimeline() {
		return timeline;
	}

	public long loadCinematicItem(int index)
	{
		long maxTime = 0;
		this.actorLocations.clear();

		PlannerTab pt = (PlannerTab) mapPanel.getParentTabbedPane().getComponentAt(PlannerFrame.TAB_CIN);
		currentPC = pt.getListPC().get(index);

		try
		{
			ArrayList<PlannerContainer> pcs = new ArrayList<PlannerContainer>();
			pcs.add(currentPC);
			ArrayList<String> results = PlannerFrame.export(pcs);

			ArrayList<TagArea> tas = XMLParser.process(results);
			if (tas.size() > 0)
			{
				ArrayList<CinematicEvent> initEvents = new ArrayList<CinematicEvent>();
				ArrayList<CinematicEvent> ces = TextParser.parseCinematicEvents(tas.get(0), initEvents,
						new HashSet<String>(), new HashSet<String>(), new HashSet<String>());
				ces.addAll(0, initEvents);
				timeline = new CinematicTimeline();
				new PlannerTimeBarViewer(ces, timeline, Integer.parseInt(tas.get(0).getParams().get("camerax")), Integer.parseInt(tas.get(0).getParams().get("cameray")));
				maxTime = timeline.duration;
			}

			mapPanel.stateChanged(null);

		}
		catch (Exception ex) {ex.printStackTrace();}
		return maxTime;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = (e.getX() / (plannerMap.getTileRenderWidth() * 2) * (plannerMap.getTileRenderWidth() * 2));
		mouseY = (e.getY() / (plannerMap.getTileRenderHeight() * 2) * (plannerMap.getTileRenderHeight() * 2));
		this.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("Add Actor") ||
				e.getActionCommand().equalsIgnoreCase("Add Static Sprite") ||
				e.getActionCommand().equalsIgnoreCase("Camera Pan"))
			mapPanel.addCinematicLineByName(e.getActionCommand(), -1, mouseX, mouseY);
		else if (popupType != 2)
			mapPanel.addCinematicLineByName(e.getActionCommand(), selectedActor, -1, -1);
		else
			mapPanel.addCinematicLineByName(e.getActionCommand(), selectedActor, mouseX, mouseY);

		popupType = 0;
	}

	public void setCameraLocation(Point cameraLocation) {
		this.cameraLocation = cameraLocation;
	}

	public PlannerContainer getCurrentPC() {
		return currentPC;
	}
}
