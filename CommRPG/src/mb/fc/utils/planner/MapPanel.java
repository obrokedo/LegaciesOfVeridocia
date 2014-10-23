package mb.fc.utils.planner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mb.fc.map.MapObject;
import mb.fc.utils.planner.MapListRenderer.MapCell;
import mb.fc.utils.planner.PlannerTimeBarViewer.CameraLocation;

public class MapPanel extends JPanel implements ActionListener, ChangeListener, ItemListener, ListSelectionListener, MouseMotionListener
{
	private static final long serialVersionUID = 1L;

	private JTabbedPane parentTabbedPane;
	private MapDisplayPanel mdp = null;
	private JLabel locationLabel = new JLabel("");
	private JScrollPane mapScrollPane;
	private JSlider timeSlider = new JSlider();
	private JList<MapCell> cinematicList = new JList<>();
	private JScrollPane cinematicListScrollPane = new JScrollPane(cinematicList);
	private Vector<MapCell> cinematicListItems = new Vector<>();
	private JComboBox<String> cinematicIds = new JComboBox<String>();
	private JButton cinButton;
	private JLabel currentActionLabel = new JLabel("Action: NONE");
	private JLabel currentTimeLabel = new JLabel("Time: 0");
	private JButton nextActionButton = new JButton("Go to next action");
	private JButton prevActionButton = new JButton("Go to previous action");
	private JButton editActionButton = new JButton("Edit action");
	private JPanel currentActionPanel = new JPanel();
	private int selectedCinIndex = 0;
	private boolean movingToNextCin = false;

	public MapPanel(JTabbedPane parentTabbedPane)
	{
		super(new BorderLayout());
		this.parentTabbedPane = parentTabbedPane;

		// this.add(locationInfoPanel, BorderLayout.LINE_START);

		MapCell mc = new MapCell();
		mc.name = "Selected Location: NONE";
		cinematicListItems.add(mc);

		mc = new MapCell();
		mc.name = "System";
		cinematicListItems.add(mc);

		mc = new MapCell();
		mc.name = "Camera";
		cinematicListItems.add(mc);

		mc = new MapCell();
		mc.name = "Sound";
		cinematicListItems.add(mc);

		cinematicList.setCellRenderer(new MapListRenderer());
		cinematicList.setListData(cinematicListItems);
		cinematicList.addListSelectionListener(this);

		parentTabbedPane.getComponentAt(PlannerFrame.TAB_CIN);
		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.add(cinematicListScrollPane, BorderLayout.CENTER);
		PlannerTab pt = (PlannerTab) parentTabbedPane.getComponentAt(PlannerFrame.TAB_CIN);
		cinematicIds.setModel(pt.getItemList().getModel());
		listPanel.add(cinematicIds, BorderLayout.PAGE_START);
		listPanel.setPreferredSize(new Dimension(200, 50));
		this.add(listPanel, BorderLayout.LINE_START);

		this.add(locationLabel, BorderLayout.PAGE_END);

		JPanel timelinePanel = new JPanel(new BorderLayout());

		cinButton = new JButton("Go to nearest instruction");
		cinButton.addActionListener(this);
		cinButton.setActionCommand("cin");
		cinButton.setEnabled(false);
		listPanel.add(cinButton, BorderLayout.PAGE_END);
		// timelinePanel.add(cinButton, BorderLayout.LINE_START);

		timeSlider.setMinimum(0);
		timeSlider.setMaximum(100000);
		timeSlider.setMajorTickSpacing(5000);
		timeSlider.setMinorTickSpacing(1000);
		timeSlider.setPaintLabels(true);
		timeSlider.setPaintTicks(true);
		timeSlider.addChangeListener(this);
		timeSlider.setEnabled(false);
		timelinePanel.add(timeSlider, BorderLayout.CENTER);
		this.add(timelinePanel, BorderLayout.PAGE_START);

		JPanel actionPanel = new JPanel();
		actionPanel.setPreferredSize(new Dimension(200, 100));
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.PAGE_AXIS));
		actionPanel.add(currentActionLabel);
		actionPanel.add(currentTimeLabel);
		actionPanel.add(editActionButton);
		actionPanel.add(prevActionButton);
		actionPanel.add(nextActionButton);
		currentActionPanel.setLayout(new BoxLayout(currentActionPanel, BoxLayout.PAGE_AXIS));
		actionPanel.add(currentActionPanel);
		editActionButton.addActionListener(this);
		nextActionButton.addActionListener(this);
		nextActionButton.setActionCommand("nextaction");
		editActionButton.setActionCommand("editaction");
		prevActionButton.addActionListener(this);
		prevActionButton.setActionCommand("prevaction");
		this.prevActionButton.setEnabled(false);
		this.nextActionButton.setEnabled(false);
		this.editActionButton.setEnabled(false);
		this.add(actionPanel, BorderLayout.LINE_END);
	}

	public void loadMap(String map)
	{
		if (mapScrollPane != null)
		{
			this.remove(mapScrollPane);
			this.removeMouseMotionListener(this);
		}
		mdp = new MapDisplayPanel(map, this);
		mapScrollPane = new JScrollPane(mdp);
		this.add(mapScrollPane, BorderLayout.CENTER);
		cinematicIds.addItemListener(this);
		itemStateChanged(null);
		this.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		locationLabel.setText("Abs X: " + e.getX() + " Abs Y: " + e.getY() +
				" Tile X: " + (e.getX() / mdp.getPlannerMap().getTileRenderWidth()) / 2 +
				" Tile Y: " + (e.getY() / mdp.getPlannerMap().getTileRenderHeight()) / 2 +
				" Tiles Pixel X: " + (e.getX() / (mdp.getPlannerMap().getTileRenderWidth() * 2) * (mdp.getPlannerMap().getTileRenderWidth() * 2)) +
				" Tiles Pixel Y: " + (e.getY() / (mdp.getPlannerMap().getTileRenderHeight() * 2) * (mdp.getPlannerMap().getTileRenderHeight() * 2)) +
				" Walkable: " + mdp.getPlannerMap().isMarkedMoveable((e.getX() / mdp.getPlannerMap().getTileRenderWidth()) / 2,
						(e.getY() / mdp.getPlannerMap().getTileRenderHeight()) / 2));


	}

	public void locationClicked(MapObject mo)
	{
		cinematicListItems.get(0).name = "Selected Location: " + mo.getKey();
		cinematicListItems.get(0).values.clear();

		for (Entry<String, String> param : mo.getParams().entrySet())
		{
			cinematicListItems.get(0).values.add(param.getKey() + ": " + param.getValue());
		}

		if (mo.getKey().equalsIgnoreCase("trigger") || mo.getKey().equalsIgnoreCase("battletrigger"))
		{
			JButton button = new JButton("Go to trigger");
			button.setActionCommand("trigger");
			button.addActionListener(this);
			cinematicListItems.get(0).button = button;
		}
		else if (mo.getKey().equalsIgnoreCase("enemy"))
		{
			JButton button = new JButton("Go to enemy");
			button.setActionCommand("enemy");
			button.addActionListener(this);
			cinematicListItems.get(0).button = button;
		}

		cinematicList.setListData(cinematicListItems);

		this.revalidate();
		this.cinematicList.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		MapObject mo = this.mdp.getSelectedMO();

		if (arg0.getActionCommand().equalsIgnoreCase("trigger"))
		{
			PlannerTab pt = (PlannerTab) parentTabbedPane.getComponentAt(PlannerFrame.TAB_TRIGGER);
			if (pt.setSelectedListItem(Integer.parseInt(mo.getParam("triggerid"))))
			{
				this.parentTabbedPane.setSelectedIndex(PlannerFrame.TAB_TRIGGER);
				pt.setSelectedListItem(Integer.parseInt(mo.getParam("triggerid")));
				System.out.println("TRIGGER ID " + Integer.parseInt(mo.getParam("triggerid")));
			}
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("enemy"))
		{
			PlannerTab pt = (PlannerTab) parentTabbedPane.getComponentAt(PlannerFrame.TAB_ENEMY);
			if (pt.setSelectedListItem(Integer.parseInt(mo.getParam("enemyid"))))
			{
				this.parentTabbedPane.setSelectedIndex(PlannerFrame.TAB_ENEMY);
				System.out.println("TRIGGER ID " + Integer.parseInt(mo.getParam("enemyid")));
			}
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("cin"))
		{
			CinematicTimeline ct = mdp.getTimeline();
			for (int i = 0; i < ct.cinematicTime.size(); i++)
			{
				if (ct.cinematicTime.get(i) >= timeSlider.getValue())
				{
					PlannerTab pt = (PlannerTab) parentTabbedPane.getComponentAt(PlannerFrame.TAB_CIN);
					this.parentTabbedPane.setSelectedIndex(PlannerFrame.TAB_CIN);
					pt.updateAttributeList(i);
					break;
				}
			}
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("prevaction"))
		{
			CinematicTimeline ct = mdp.getTimeline();
			System.out.println("NEXT ACTION " + selectedCinIndex);
			if (selectedCinIndex > 0)
			{
				movingToNextCin = true;
				if (ct.cinematicTime.get(selectedCinIndex).intValue() != ct.cinematicTime.get(selectedCinIndex - 1).intValue())
					timeSlider.setValue(ct.cinematicTime.get(--selectedCinIndex).intValue());
				else
				{
					--selectedCinIndex;
					stateChanged(null);
				}
				System.out.println("AFTER SELECTED " + selectedCinIndex);
			}
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("nextaction"))
		{
			CinematicTimeline ct = mdp.getTimeline();
			System.out.println("NEXT ACTION " + selectedCinIndex);
			if (selectedCinIndex + 1 < ct.cinematicTime.size())
			{
				movingToNextCin = true;
				if (ct.cinematicTime.get(selectedCinIndex).intValue() != ct.cinematicTime.get(selectedCinIndex + 1).intValue())
					timeSlider.setValue(ct.cinematicTime.get(++selectedCinIndex).intValue());
				else
				{
					++selectedCinIndex;
					stateChanged(null);
				}
				System.out.println("AFTER SELECTED " + selectedCinIndex);
			}
		}
		else if (arg0.getActionCommand().equalsIgnoreCase("editaction"))
		{
			editCinematicLine();
		}

		/*
		PlannerTab pt = (PlannerTab) parentTabbedPane.getComponentAt(PlannerFrame.TAB_CIN);
		PlannerContainerDef pcdef = pt.getPlannerContainerDef();
		PlannerLine pl = new PlannerLine(pcdef.getAllowableLines().get(0), false);
		pl.setupUI(pcdef.getAllowableLines(), this, 1, pcdef.getListOfLists());
		int ret = JOptionPane.showConfirmDialog(this, pl, "Add cinematic action", JOptionPane.OK_CANCEL_OPTION);

		if (ret == JOptionPane.YES_OPTION)
		{
			pt.getCurrentPC().addLine(pl);
		}
		*/
	}

	private void editCinematicLine()
	{
		if (!timeSlider.isEnabled())
			return;

		PlannerTab pt = (PlannerTab) parentTabbedPane.getComponentAt(PlannerFrame.TAB_CIN);
		PlannerLine pl = pt.getCurrentPC().getLines().get(selectedCinIndex);
		PlannerContainerDef pcdef = pt.getPlannerContainerDef();
		pl.setupUI(pcdef.getAllowableLines(), this, 1, pcdef.getListOfLists(), false, true, null);

		JOptionPane.showConfirmDialog(this, pl, "Edit cinematic action", JOptionPane.OK_OPTION);

		pl.commitChanges();

		long maxTime = mdp.loadCinematicItem(cinematicIds.getSelectedIndex());
		timeSlider.setMaximum((int) maxTime);
		timeSlider.revalidate();
		timeSlider.repaint();
	}

	public void addCinematicLineByName(String name, int actorIndex, int xLoc, int yLoc)
	{
		if (!timeSlider.isEnabled())
			return;

		PlannerTab pt = (PlannerTab) parentTabbedPane.getComponentAt(PlannerFrame.TAB_CIN);
		PlannerContainerDef pcdef = pt.getPlannerContainerDef();
		PlannerLine pl = new PlannerLine(getCinematicLineDefByName(name), false);
		if (actorIndex != -1)
			pl.getValues().add(cinematicListItems.get(actorIndex + 4).name);
		if (xLoc != -1 && yLoc != -1)
		{
			pl.getValues().add(xLoc);
			pl.getValues().add(yLoc);
		}
		pl.setupUI(pcdef.getAllowableLines(), this, 1, pcdef.getListOfLists(), false, true, null);
		int ret = JOptionPane.showConfirmDialog(this, pl, "Add cinematic action", JOptionPane.OK_CANCEL_OPTION);

		if (ret == JOptionPane.YES_OPTION)
		{
			CinematicTimeline ct = mdp.getTimeline();

			pl.commitChanges();
			if (ct.cinematicTime.size() == 0)
			{
				pt.getCurrentPC().addLine(pl);
			}
			else
			{
				boolean found = false;
				for (int i = 0; i < ct.cinematicTime.size(); i++)
				{
					if (ct.cinematicTime.get(i) > timeSlider.getValue())
					{
						pt.getCurrentPC().addLine(pl, i + 1);
						found = true;
						break;
					}
				}

				if (!found)
					pt.getCurrentPC().addLine(pl);
			}

			long maxTime = mdp.loadCinematicItem(cinematicIds.getSelectedIndex());
			timeSlider.setMaximum((int) maxTime);
			timeSlider.revalidate();
			timeSlider.repaint();
		}
	}

	private PlannerLineDef getCinematicLineDefByName(String name)
	{
		PlannerTab pt = (PlannerTab) parentTabbedPane.getComponentAt(PlannerFrame.TAB_CIN);
		PlannerContainerDef pcdef = pt.getPlannerContainerDef();

		for (PlannerLineDef pld : pcdef.getAllowableLines())
		{
			if (pld.getName().equalsIgnoreCase(name))
				return pld;
		}
		return null;
	}

	public JTabbedPane getParentTabbedPane() {
		return parentTabbedPane;
	}

	private class ActorAction implements Comparable<ActorAction>
	{
		public String actor;
		public ArrayList<String> actions;

		public ActorAction(String actor, ArrayList<String> actions) {
			super();
			this.actor = actor;
			this.actions = actions;
		}

		@Override
		public int compareTo(ActorAction o) {
			return actor.compareTo(o.actor);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		int selected = cinematicList.getSelectedIndex();
		cinematicListItems.get(1).values = mdp.getSystemValuesAtTime(timeSlider.getValue());
		cinematicListItems.get(2).values = mdp.getCameraValuesAtTime(timeSlider.getValue());
		cinematicListItems.get(3).values = mdp.getSoundValuesAtTime(timeSlider.getValue());

		System.out.println("Before remove " + cinematicListItems.size());
		while (cinematicListItems.size() > 4)
			cinematicListItems.remove(4);

		System.out.println("After remove " + cinematicListItems.size());

		ArrayList<String> names = new ArrayList<String>();
		ArrayList<ArrayList<String>> lol = mdp.getActorValuesAtTime(timeSlider.getValue(), names);
		System.out.println(names.size());

		ArrayList<ActorAction> aas = new ArrayList<ActorAction>();
		for (int i = 0; i < names.size(); i++)
		{
			aas.add(new ActorAction(names.get(i), lol.get(i)));
		}
		Collections.sort(aas);

		for (ActorAction aa : aas)
		{
			MapCell mc = new MapCell();
			mc.name = aa.actor;
			mc.values = aa.actions;
			cinematicListItems.add(mc);
		}

		cinematicList.setListData(cinematicListItems);

		if (selected < cinematicListItems.size())
			cinematicList.setSelectedIndex(selected);
		else
			cinematicList.setSelectedIndex(1);

		System.out.println("STATE CHANGED " + movingToNextCin);
		if (!movingToNextCin)
		{
			if (timeSlider.getValue() == 0)
				selectedCinIndex = 0;
			else
			{
				CinematicTimeline ct = mdp.getTimeline();
				if (ct.cinematicTime.size() <= 1)
					selectedCinIndex = 0;
				else
				{
					for (int i = 0; i < ct.cinematicTime.size(); i++)
					{
						if (ct.cinematicTime.get(i) < timeSlider.getValue())
							selectedCinIndex = i;
						else
							break;
					}

					System.out.println("SELECTED");
				}
			}
		}
		movingToNextCin = false;

		CinematicTimeline ct = mdp.getTimeline();
		CameraLocation cameraLocations = null;
		for (CameraLocation cl : ct.cameraLocations)
		{
			if (cl.time > timeSlider.getValue())
				break;
			else
				cameraLocations = cl;
		}

		if (cameraLocations.following != null)
		{
			Point actorPoint = mdp.getActorLocationAtTime(timeSlider.getValue(), cameraLocations.following);

			if (actorPoint.x < 160)
				actorPoint.x = 0;
			else
				actorPoint.x = (Math.max(0, actorPoint.x - 160));

			if (actorPoint.y < 120)
				actorPoint.y = 0;
			else
				actorPoint.y = (Math.max(0, actorPoint.y - 120));
			mdp.setCameraLocation(actorPoint);
		}
		else if (cameraLocations.endLocX != -1)
		{
			mdp.setCameraLocation(new Point((int) (cameraLocations.locX + ((cameraLocations.endLocX - cameraLocations.locX) * 1.0f * (timeSlider.getValue() - cameraLocations.time) / cameraLocations.duration)),
					(int) (cameraLocations.locY + ((cameraLocations.endLocY - cameraLocations.locY) * 1.0f * (timeSlider.getValue() - cameraLocations.time) / cameraLocations.duration))));
		}
		else
		{
			mdp.setCameraLocation(new Point(cameraLocations.locX, cameraLocations.locY));
		}

		setCurrentActionLabel();

		this.revalidate();
		this.cinematicList.repaint();
		mdp.repaint();

		/*
		StringBuffer sb = mdp.getValuesAtTime(timeSlider.getValue());
		String[] lines = sb.toString().split("\n");
		locationInfoPanel.removeAll();
		JButton cinButton = new JButton("Go to nearest instruction");
		cinButton.addActionListener(this);
		cinButton.setActionCommand("cin");
		locationInfoPanel.add(cinButton);
		for (String s : lines)
			locationInfoPanel.add(new JLabel(s));
		locationInfoPanel.revalidate();
		locationInfoPanel.repaint();
		mdp.repaint();
		*/
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		loadCinematicItem(-1);
	}

	public void reloadCinematicItem()
	{
		loadCinematicItem(timeSlider.getValue());
	}

	private void loadCinematicItem(int selectedTime)
	{
		if (cinematicIds.getSelectedIndex() == -1)
		{
			this.timeSlider.setEnabled(false);
			this.cinButton.setEnabled(false);
			mdp.setSelectedActor(-1);
			this.nextActionButton.setEnabled(false);
			this.editActionButton.setEnabled(false);
			this.prevActionButton.setEnabled(false);
			this.currentActionLabel.setText("Action: NONE");
			mdp.setCameraLocation(null);
			currentActionPanel.removeAll();
		}
		else
		{
			currentActionPanel.removeAll();
			currentTimeLabel.setText("Time: 0");
			this.nextActionButton.setEnabled(true);
			this.editActionButton.setEnabled(true);
			this.prevActionButton.setEnabled(true);
			long maxTime = mdp.loadCinematicItem(cinematicIds.getSelectedIndex());
			System.out.println(maxTime);
			timeSlider.setMaximum((int) maxTime);
			if (selectedTime == -1)
				this.timeSlider.setValue(0);
			else
			{
				if (selectedTime > maxTime)
					timeSlider.setValue((int) maxTime);
				else
					timeSlider.setValue(selectedTime);
			}
			this.timeSlider.setEnabled(true);
			this.cinButton.setEnabled(true);
			selectedCinIndex = 0;
			this.setCurrentActionLabel();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting())
		{
			System.out.println("SELECTED INDEX " + cinematicList.getSelectedIndex());
			if (cinematicList.getSelectedIndex() > 3)
			{
				mdp.setSelectedActor(cinematicList.getSelectedIndex() - 4);
			}
			else
				mdp.setSelectedActor(-1);
		}
	}

	public void setActorSelected(int index)
	{
		if (index != -1)
			cinematicList.setSelectedIndex(index + 4);
		else
			cinematicList.setSelectedIndex(1);
	}

	private void setCurrentActionLabel()
	{
		PlannerTab pt = (PlannerTab) parentTabbedPane.getComponentAt(PlannerFrame.TAB_CIN);
		PlannerContainer pc = pt.getListPC().get(cinematicIds.getSelectedIndex());
		if (selectedCinIndex < pc.getLines().size())
		{
			PlannerLine pl = pc.getLines().get(selectedCinIndex);
			currentActionLabel.setText("Action: " + pl.getPlDef().getName());
			currentActionPanel.removeAll();
			for (int i = 0; i < pl.getValues().size(); i++)
			{
				currentActionPanel.add(new JLabel(pl.getPlDef().getPlannerValues().get(i).getDisplayTag()));
				JLabel valueLabel = new JLabel(pl.getValues().get(i).toString());
				valueLabel.setForeground(Color.BLUE);
				currentActionPanel.add(valueLabel);
			}

			currentActionPanel.repaint();
		}
		currentActionLabel.repaint();

		currentTimeLabel.setText("Time: " + timeSlider.getValue());
	}
}
