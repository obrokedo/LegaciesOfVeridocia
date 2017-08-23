package mb.fc.utils.planner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import mb.fc.cinematic.event.CinematicEvent;
import mb.fc.engine.CommRPG;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;
import mb.fc.utils.planner.cinematic.CinematicTimeline;

public class PlannerContainer implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private PlannerContainerDef pcdef;
	private ArrayList<PlannerLine> lines;
	private PlannerLine defLine;
	private PlannerTimeBarViewer plannerGraph = null;
	private PlannerTab parentTab;
	private JPanel uiAspect = new JPanel();

	public PlannerContainer(PlannerContainerDef pcdef, PlannerTab parentTab) {
		super();
		uiAspect.setLayout(new BoxLayout(uiAspect, BoxLayout.PAGE_AXIS));
		this.pcdef = pcdef;

		this.defLine = new PlannerLine(pcdef.getDefiningLine(), true);
		this.lines = new ArrayList<PlannerLine>();
		this.parentTab = parentTab;
	}

	public PlannerContainer(String newName, PlannerContainer copyContainer)
	{
		super();
		uiAspect.setLayout(new BoxLayout(uiAspect, BoxLayout.PAGE_AXIS));
		this.pcdef = copyContainer.pcdef;

		this.defLine = new PlannerLine(copyContainer.defLine);
		this.defLine.getValues().set(0, newName);
		this.lines = new ArrayList<PlannerLine>();
		this.parentTab = copyContainer.parentTab;
		for (PlannerLine l : copyContainer.lines)
			this.lines.add(new PlannerLine(l));
	}

	public void setupUI()
	{
		setupUI(0);
	}

	public void setupUI(int index)
	{
		uiAspect.removeAll();
		uiAspect.validate();

		defLine.setupUI(pcdef.getAllowableLines(), this, 0, pcdef.getListOfLists(), parentTab);
		uiAspect.add(defLine.getUiAspect());

		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
		if (index != -1 && index < lines.size())
		{
			lines.get(index).setupUI(pcdef.getAllowableLines(), this, index + 1, pcdef.getListOfLists(), parentTab);
			listPanel.add(lines.get(index).getUiAspect());
		}

		/*
		for (PlannerLine line : lines)
		{
			line.setupUI(pcdef.getAllowableLines(), this, i, pcdef.getListOfLists());
			listPanel.add(line);
			i++;
		} */

		uiAspect.add(listPanel);

		if (PlannerFrame.SHOW_CIN && pcdef.getDefiningLine().getTag().equalsIgnoreCase("Cinematic"))
		{
			try
			{
				ArrayList<PlannerContainer> pcs = new ArrayList<PlannerContainer>();
				pcs.add(this);
				ArrayList<String> results = PlannerIO.export(pcs);

				ArrayList<TagArea> tas = XMLParser.process(results);
				if (tas.size() > 0)
				{
					ArrayList<CinematicEvent> initEvents = new ArrayList<CinematicEvent>();
					if (plannerGraph == null)
					{
						ArrayList<CinematicEvent> ces = CommRPG.TEXT_PARSER.parseCinematicEvents(tas.get(0), initEvents,
								new HashSet<String>(), new HashSet<String>(), new HashSet<String>());
						ces.addAll(0, initEvents);
						plannerGraph = new PlannerTimeBarViewer(ces, new CinematicTimeline(), Integer.parseInt(tas.get(0).getAttribute("camerax")), Integer.parseInt(tas.get(0).getAttribute("cameray")));
					}
					else
					{
						ArrayList<CinematicEvent> ces = CommRPG.TEXT_PARSER.parseCinematicEvents(tas.get(0), initEvents,
								new HashSet<String>(), new HashSet<String>(), new HashSet<String>());
						ces.addAll(0, initEvents);

						plannerGraph.generateGraph(ces, new CinematicTimeline(), Integer.parseInt(tas.get(0).getAttribute("camerax")), Integer.parseInt(tas.get(0).getAttribute("cameray")));
					// this.add(ptbv);
					}
				}
			}
			catch (Exception ex) {ex.printStackTrace();}

		}

		uiAspect.validate();
		uiAspect.repaint();
		// this.add(new JScrollPane(listPanel));
	}

	public void addLine(PlannerLine line)
	{
		this.lines.add(line);
		parentTab.refreshItem(this);
		uiAspect.revalidate();
		uiAspect.repaint();
	}

	public void addLine(PlannerLine line, int addIndex)
	{
		this.lines.add(addIndex, line);
		parentTab.refreshItem(this);
		uiAspect.revalidate();
		uiAspect.repaint();
	}

	public PlannerLine removeLine(int index)
	{
		PlannerLine pl = lines.remove(index);
		parentTab.refreshItem(this);
		uiAspect.revalidate();
		uiAspect.repaint();
		return pl;
	}

	public void duplicateLine(int index)
	{
		PlannerLine pl = lines.get(index);
		lines.add(index + 1, new PlannerLine(pl));
		parentTab.refreshItem(this);
		uiAspect.revalidate();
		uiAspect.repaint();
	}

	public PlannerContainer duplicateContainer(String newName)
	{
		return new PlannerContainer(newName, this);
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		String action = a.getActionCommand();
		if (action.equalsIgnoreCase("addline"))
		{
			int index = defLine.getSelectedItem();
			this.lines.add(new PlannerLine(pcdef.getAllowableLines().get(index), false));
			parentTab.addAttribute(pcdef.getAllowableLines().get(index).getName(), lines.size() - 1);
			// setupUI();
			uiAspect.revalidate();
			uiAspect.repaint();
		}
		else if (action.startsWith("refresh"))
		{
			setupUI();
			uiAspect.revalidate();
			uiAspect.repaint();
		}
		else if (action.startsWith("remove"))
		{
			int index = Integer.parseInt(action.split(" ")[1]) - 1;
			lines.remove(index);
			parentTab.refreshItem(this);
			// setupUI();
			uiAspect.revalidate();
			uiAspect.repaint();
		}
		else if (action.startsWith("moveup"))
		{
			int index = Integer.parseInt(action.split(" ")[1]) - 1;
			if (index != 0)
			{
				PlannerLine pl = lines.remove(index);
				lines.add(index - 1, pl);
				parentTab.refreshItem(this);
				// setupUI();
				uiAspect.revalidate();
				uiAspect.repaint();
			}
		}
		else if (action.startsWith("movedown"))
		{
			int index = Integer.parseInt(action.split(" ")[1]) - 1;
			if (index != lines.size() - 1)
			{
				PlannerLine pl = lines.remove(index);
				lines.add(index + 1, pl);
				parentTab.refreshItem(this);
				// setupUI();
				uiAspect.revalidate();
				uiAspect.repaint();
			}
		}
		else if (action.startsWith("duplicate"))
		{
			int index = Integer.parseInt(action.split(" ")[1]) - 1;
			PlannerLine pl = lines.get(index);
			lines.add(index + 1, new PlannerLine(pl));
			parentTab.refreshItem(this);
			// setupUI();
			uiAspect.revalidate();
			uiAspect.repaint();
		}
		else if (action.startsWith("save")) {
			int index = Integer.parseInt(action.split(" ")[1]) - 1;
			PlannerLine pl = lines.get(index);
			pl.commitChanges();
			parentTab.refreshItem(this);
			uiAspect.revalidate();
			uiAspect.repaint();
		}
	}

	public void commitChanges()
	{
		defLine.commitChanges();
		for (PlannerLine pl : lines)
			pl.commitChanges();
	}

	public PlannerContainerDef getPcdef() {
		return pcdef;
	}

	public String getDescription()
	{
		return (String) defLine.getValues().get(0);
	}

	public PlannerLine getDefLine() {
		return defLine;
	}

	public ArrayList<PlannerLine> getLines() {
		return lines;
	}

	public PlannerTimeBarViewer getPlannerGraph() {
		return plannerGraph;
	}

	public JPanel getUiAspect() {
		return uiAspect;
	}
}
