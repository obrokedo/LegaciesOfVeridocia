package mb.fc.utils.planner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PlannerContainer extends JPanel implements ActionListener
{
	private PlannerContainerDef pcdef;
	private ArrayList<PlannerContainer> containers;
	private ArrayList<PlannerLine> lines;
	private PlannerLine defLine;
	
	public PlannerContainer(PlannerContainerDef pcdef) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.pcdef = pcdef;
				
		this.defLine = new PlannerLine(pcdef.getDefiningLine(), true);		
		this.containers = new ArrayList<PlannerContainer>();
		this.lines = new ArrayList<PlannerLine>();
	}
	
	public void setupUI()
	{
		this.removeAll();
		defLine.setupUI(pcdef.getAllowableLines(), this, 0, pcdef.getListOfLists());
		this.add(defLine);
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
		int i = 1;
		for (PlannerLine line : lines)
		{
			line.setupUI(pcdef.getAllowableLines(), this, i, pcdef.getListOfLists());
			listPanel.add(line);
			i++;
		}
		
		this.add(listPanel);
		// this.add(new JScrollPane(listPanel));
	}
	
	public void addLine(PlannerLine line)
	{
		this.lines.add(line);
	}
	
	public void addContainer(PlannerContainer container)
	{
		this.containers.add(container);
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		String action = a.getActionCommand();
		
		if (action.equalsIgnoreCase("addline"))
		{
			int index = defLine.getSelectedItem();
			this.lines.add(new PlannerLine(pcdef.getAllowableLines().get(index), false));			
			setupUI();
			this.revalidate();
			this.repaint();
		}
		else if (action.startsWith("remove"))
		{
			int index = Integer.parseInt(action.split(" ")[1]) - 1;
			lines.remove(index);
			setupUI();
			this.revalidate();
			this.repaint();
		}
		else if (action.startsWith("moveup"))
		{
			int index = Integer.parseInt(action.split(" ")[1]) - 1;
			if (index != 0)
			{
				PlannerLine pl = lines.remove(index);
				lines.add(index - 1, pl);
				setupUI();
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
				setupUI();
				this.revalidate();
				this.repaint();
			}
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
}
