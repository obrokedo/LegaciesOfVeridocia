package mb.fc.utils.planner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import mb.fc.cinematic.event.CinematicEvent;
import mb.fc.loading.TextParser;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;

public class PlannerContainer extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private PlannerContainerDef pcdef;
	private ArrayList<PlannerLine> lines;
	private PlannerLine defLine;
	private PlannerTimeBarViewer plannerGraph = null;
	private PlannerTab parentTab;

	public PlannerContainer(PlannerContainerDef pcdef, PlannerTab parentTab) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.pcdef = pcdef;

		this.defLine = new PlannerLine(pcdef.getDefiningLine(), true);
		this.lines = new ArrayList<PlannerLine>();
		this.parentTab = parentTab;
	}

	public void setupUI()
	{
		setupUI(0);
	}

	public void setupUI(int index)
	{
		this.removeAll();
		this.validate();

		defLine.setupUI(pcdef.getAllowableLines(), this, 0, pcdef.getListOfLists(), parentTab);
		this.add(defLine);

		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
		if (index != -1 && index < lines.size())
		{
			lines.get(index).setupUI(pcdef.getAllowableLines(), this, index + 1, pcdef.getListOfLists(), parentTab);
			listPanel.add(lines.get(index));
		}

		/*
		for (PlannerLine line : lines)
		{
			line.setupUI(pcdef.getAllowableLines(), this, i, pcdef.getListOfLists());
			listPanel.add(line);
			i++;
		} */

		this.add(listPanel);

		if (PlannerFrame.SHOW_CIN && pcdef.getDefiningLine().getTag().equalsIgnoreCase("Cinematic"))
		{
			try
			{
				ArrayList<PlannerContainer> pcs = new ArrayList<PlannerContainer>();
				pcs.add(this);
				ArrayList<String> results = PlannerFrame.export(pcs);

				ArrayList<TagArea> tas = XMLParser.process(results);
				if (tas.size() > 0)
				{
					ArrayList<CinematicEvent> initEvents = new ArrayList<CinematicEvent>();
					if (plannerGraph == null)
					{
						ArrayList<CinematicEvent> ces = TextParser.parseCinematicEvents(tas.get(0), initEvents,
								new HashSet<String>(), new HashSet<String>(), new HashSet<String>());
						ces.addAll(0, initEvents);
						plannerGraph = new PlannerTimeBarViewer(ces, new CinematicTimeline(), Integer.parseInt(tas.get(0).getAttribute("camerax")), Integer.parseInt(tas.get(0).getAttribute("cameray")));
					}
					else
					{
						ArrayList<CinematicEvent> ces = TextParser.parseCinematicEvents(tas.get(0), initEvents,
								new HashSet<String>(), new HashSet<String>(), new HashSet<String>());
						ces.addAll(0, initEvents);

						plannerGraph.generateGraph(ces, new CinematicTimeline(), Integer.parseInt(tas.get(0).getAttribute("camerax")), Integer.parseInt(tas.get(0).getAttribute("cameray")));
					// this.add(ptbv);
					}
				}
			}
			catch (Exception ex) {ex.printStackTrace();}

		}

		this.validate();
		this.repaint();
		// this.add(new JScrollPane(listPanel));
	}

	public void addLine(PlannerLine line)
	{
		this.lines.add(line);
	}

	public void addLine(PlannerLine line, int addIndex)
	{
		this.lines.add(addIndex, line);
	}

	public void removeLine(int index)
	{
		lines.remove(index);
		parentTab.updateAttributeList(Math.max(0, index - 1));
		this.revalidate();
		this.repaint();
	}

	public void duplicateLine(int index)
	{
		PlannerLine pl = lines.get(index);
		lines.add(index + 1, new PlannerLine(pl));
		parentTab.updateAttributeList(index + 1);
		this.revalidate();
		this.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		String action = a.getActionCommand();
		System.out.println("ACTION PERFORMED " + action + " " + (this == parentTab.getCurrentPC()));
		if (action.equalsIgnoreCase("addline"))
		{
			int index = defLine.getSelectedItem();
			this.lines.add(new PlannerLine(pcdef.getAllowableLines().get(index), false));
			parentTab.updateAttributeList(lines.size() - 1);
			// setupUI();
			this.revalidate();
			this.repaint();
		}
		else if (action.startsWith("refresh"))
		{
			setupUI();
			this.revalidate();
			this.repaint();
		}
		else if (action.startsWith("remove"))
		{
			int index = Integer.parseInt(action.split(" ")[1]) - 1;
			lines.remove(index);
			parentTab.updateAttributeList(Math.max(0, index - 1));
			// setupUI();
			this.revalidate();
			this.repaint();
		}
		else if (action.startsWith("moveup"))
		{
			int index = Integer.parseInt(action.split(" ")[1]) - 1;
			System.out.println("MY INDEX " + index);
			if (index != 0)
			{
				PlannerLine pl = lines.remove(index);
				lines.add(index - 1, pl);
				parentTab.updateAttributeList(index - 1);
				// setupUI();
				this.revalidate();
				this.repaint();
			}
		}
		else if (action.startsWith("movedown"))
		{
			int index = Integer.parseInt(action.split(" ")[1]) - 1;
			if (index != lines.size() - 1)
			{
				PlannerLine pl = lines.remove(index);
				lines.add(index + 1, pl);
				parentTab.updateAttributeList(index + 1);
				// setupUI();
				this.revalidate();
				this.repaint();
			}
		}
		else if (action.startsWith("duplicate"))
		{
			int index = Integer.parseInt(action.split(" ")[1]) - 1;
			PlannerLine pl = lines.get(index);
			System.out.println("DUPLICATE");
			lines.add(index + 1, new PlannerLine(pl));
			parentTab.updateAttributeList(index + 1);
			// setupUI();
			this.revalidate();
			this.repaint();
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
}
