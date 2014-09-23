package mb.fc.utils.planner;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PlannerTab extends JPanel implements ActionListener, ListSelectionListener
{
	protected static final long serialVersionUID = 1L;

	protected Hashtable<String, PlannerContainerDef> containersByName;
	protected String[] containers;
	protected JList<String> list;
	protected DefaultListModel<String> listModel;
	protected ArrayList<PlannerContainer> listPC;
	protected PlannerContainer currentPC;
	protected int selectedPC;
	protected JScrollPane currentPCScroll;
	protected JComboBox<String> typeComboBox;
	protected int refersTo;
	protected PlannerFrame plannerFrame;

	public PlannerTab(Hashtable<String, PlannerContainerDef> containersByName,
			String[] containers, int refersTo, PlannerFrame plannerFrame)
	{
		this(containersByName, containers, refersTo, plannerFrame, true);
	}

	public PlannerTab(Hashtable<String, PlannerContainerDef> containersByName,
			String[] containers, int refersTo, PlannerFrame plannerFrame, boolean displayList)
	{
		super(new BorderLayout());

		JPanel listPanel = new JPanel(new BorderLayout());
		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		listPC = new ArrayList<PlannerContainer>();

		this.containersByName = containersByName;
		this.containers = containers;

		this.add(list, BorderLayout.LINE_START);
		listPanel.add(new JLabel("Entries"), BorderLayout.PAGE_START);
		listPanel.add(new JScrollPane(list), BorderLayout.CENTER);

		typeComboBox = new JComboBox<String>(containers);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(typeComboBox);
		buttonPanel.add(PlannerFrame.createActionButton("Add", "add", this));
		buttonPanel.add(PlannerFrame.createActionButton("Remove", "remove", this));
		listPanel.add(buttonPanel, BorderLayout.PAGE_END);

		if (displayList)
			this.add(listPanel, BorderLayout.LINE_START);
		this.refersTo = refersTo;
		this.plannerFrame = plannerFrame;
	}

	@Override
	public void actionPerformed(ActionEvent al)
	{
		String command = al.getActionCommand();
		if (command.equalsIgnoreCase("add"))
		{
			String type = containers[typeComboBox.getSelectedIndex()];
			listModel.addElement(type);
			listPC.add(new PlannerContainer(containersByName.get(type)));

			PlannerContainerDef pcd = containersByName.get(type);
			pcd.getDataLines().add("");

			list.setSelectedIndex(listModel.size() - 1);
		}
		else if (command.equalsIgnoreCase("remove"))
		{
			int selected = list.getSelectedIndex();
			plannerFrame.removeReferences(refersTo, selected);
			currentPC.getPcdef().getDataLines().remove(selected);
			currentPC = null;
			listModel.remove(selected);
			listPC.remove(selected);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting())
		{
			setNewValues();
		}
	}

	public void setNewValues()
	{
		if (currentPCScroll != null)
		{
			this.remove(currentPCScroll);
		}

		System.out.println("SET NEW VALUES");

		if (currentPC != null && currentPC.getDefLine() != null)
		{
			System.out.println("REMOVE DEF LINE");
			this.remove(currentPC.getDefLine().getDefiningPanel());
		}

		for (int i = 0; i < this.getComponentCount(); i++)
		{
			if (this.getComponent(i) instanceof PlannerTimeBarViewer)
			{
				this.remove(i);
				i--;
			}
		}
		if (currentPC != null)
		{
			currentPC.commitChanges();
			listModel.set(selectedPC, "(ID: " + (selectedPC) + ") " + currentPC.getDescription());
			currentPC.getPcdef().getDataLines().set(selectedPC, currentPC.getDescription());
		}

		if (list.getSelectedIndex() != -1)
		{
			currentPC = listPC.get(list.getSelectedIndex());
			selectedPC = list.getSelectedIndex();
			System.out.println("NEW VALUES");
			currentPC.setupUI();
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(currentPC);
			currentPCScroll = new JScrollPane(panel);
			currentPCScroll.getVerticalScrollBar().setUnitIncrement(75);
			this.add(currentPC.getDefLine().getDefiningPanel(), BorderLayout.PAGE_START);
			this.add(currentPCScroll, BorderLayout.CENTER);
			if (currentPC.getDefLine().getPlDef().getTag().equalsIgnoreCase("Cinematic") && currentPC.getPlannerGraph() != null &&
					PlannerFrame.SHOW_CIN)
			{
				this.add(currentPC.getPlannerGraph(), BorderLayout.PAGE_END);
			}
			this.revalidate();
		}
		else
		{
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			currentPCScroll = new JScrollPane(panel);
			this.add(currentPCScroll, BorderLayout.CENTER);
			this.revalidate();
		}
	}

	public void addPlannerContainer(PlannerContainer pc)
	{
		this.listPC.add(pc);
		listModel.addElement("(ID: " + (this.listPC.size() - 1) + ") " + pc.getDescription());
	}

	public ArrayList<PlannerContainer> getListPC() {
		return listPC;
	}

	public void clearValues(ArrayList<ArrayList<String>> listOfLists)
	{
		if (currentPCScroll != null)
		{
			this.remove(currentPCScroll);
		}

		System.out.println("SET NEW VALUES");

		if (currentPC != null && currentPC.getDefLine() != null)
		{
			System.out.println("REMOVE DEF LINE");
			this.remove(currentPC.getDefLine().getDefiningPanel());
		}

		for (int i = 0; i < this.getComponentCount(); i++)
		{
			if (this.getComponent(i) instanceof PlannerTimeBarViewer)
			{
				this.remove(i);
				i--;
			}
		}

		this.currentPC = null;
		this.listPC.clear();
		listModel.clear();
	}
}
