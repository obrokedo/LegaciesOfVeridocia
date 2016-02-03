package mb.fc.utils.planner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class PlannerTab implements ActionListener, TreeSelectionListener
{
	protected static final long serialVersionUID = 1L;

	protected Hashtable<String, PlannerContainerDef> containersByName;
	protected String[] containers;
	protected ArrayList<PlannerContainer> listPC;
	protected PlannerContainer currentPC;
	protected int selectedPC;
	protected JScrollPane currentPCScroll;
	protected JComboBox<String> typeComboBox;
	protected int refersTo;
	protected PlannerFrame plannerFrame;
	protected PlannerTree plannerTree;
	private JPanel listPanel;
	private String name;
	private JPanel uiAspect;

	public PlannerTab(String name, Hashtable<String, PlannerContainerDef> containersByName,
			String[] containers, int refersTo, PlannerFrame plannerFrame)
	{
		uiAspect = new JPanel(new BorderLayout());

		this.name = name;
		listPanel = new JPanel();

		listPC = new ArrayList<PlannerContainer>();

		this.containersByName = containersByName;
		this.containers = containers;

		listPanel.setBackground(Color.DARK_GRAY);
		listPanel.add(PlannerFrame.createActionButton("Add", "add", this));
		listPanel.add(PlannerFrame.createActionButton("Remove", "remove", this));

		// this.add(listPanel, BorderLayout.LINE_START);
		// uiAspect.add(listPanel, BorderLayout.PAGE_START);

		typeComboBox = new JComboBox<String>(containers);

		this.refersTo = refersTo;
		this.plannerFrame = plannerFrame;

		plannerTree = new PlannerTree(name, listPC, this, new TabAttributeTransferHandler(listPC), this);
		uiAspect.add(plannerTree.getUiAspect(), BorderLayout.LINE_START);
	}

	@Override
	public void actionPerformed(ActionEvent al)
	{
		String command = al.getActionCommand();
		System.out.println("CONTAINER ACTION PERFORMED " + command);
		if (command.equalsIgnoreCase("add"))
		{
			addNewContainer();
		}
		else if (command.equalsIgnoreCase("remove"))
		{
			removeContainer(plannerTree.getSelectedIndex());
		}
	}

	public void addNewContainer()
	{
		String newName = JOptionPane.showInputDialog("Enter the new objects name");
		if (newName == null)
			return;

		String type = containers[typeComboBox.getSelectedIndex()];
		PlannerContainer newPC = new PlannerContainer(containersByName.get(type), this);
		listPC.add(newPC);
		PlannerContainerDef pcd = containersByName.get(type);
		pcd.getDataLines().add(newName);
		newPC.getDefLine().getValues().add(newName);
		System.out.println("ADD ELEMENT");
		plannerTree.addItem(newName, listPC.size() - 1);
		System.out.println("AFTER ADD ELEMENT");
	}

	public void removeContainer(int index)
	{
		int rc = JOptionPane.showConfirmDialog(uiAspect,
				"Are you sure you'd like to delete the entire " + typeComboBox.getSelectedItem(),
				"Confirm " + typeComboBox.getSelectedItem() + " deletion?", JOptionPane.YES_NO_OPTION);
		if (rc != JOptionPane.OK_OPTION)
			return;
		plannerTree.removeItem(index);
		plannerFrame.removeReferences(refersTo, index);
		currentPC.getPcdef().getDataLines().remove(index);
		uiAspect.remove(currentPC.getDefLine().getDefiningPanel());
		uiAspect.remove(currentPCScroll);
		uiAspect.repaint();

		currentPC = null;
		// TODO REMOVE HERE
		listPC.remove(index);
	}

	public void commitChanges()
	{
		for (PlannerContainer pcs : listPC)
			pcs.commitChanges();
	}

	public void setNewValues()
	{
		System.out.println("SET NEW VALUES");

		if (currentPCScroll != null)
		{
			uiAspect.remove(currentPCScroll);
		}

		if (currentPC != null && currentPC.getDefLine() != null)
		{
			System.out.println("REMOVE DEF LINE");
			uiAspect.remove(currentPC.getDefLine().getDefiningPanel());
		}

		for (int i = 0; i < uiAspect.getComponentCount(); i++)
		{
			if (uiAspect.getComponent(i) instanceof PlannerTimeBarViewer)
			{
				uiAspect.remove(i);
				i--;
			}
		}

		if (currentPC != null)
		{
			currentPC.commitChanges();
			currentPC.getPcdef().getDataLines().set(selectedPC, currentPC.getDescription());
		}

		if (plannerTree.getSelectedIndex() != -1)
		{
			currentPC = listPC.get(plannerTree.getSelectedIndex());
			selectedPC = plannerTree.getSelectedIndex();
			System.out.println("NEW VALUES " + selectedPC);
			if (plannerTree.getSelectedAttributeIndex() != -1)
				currentPC.setupUI(plannerTree.getSelectedAttributeIndex());
			else
				currentPC.setupUI();

			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(currentPC.getUiAspect());
			currentPCScroll = new JScrollPane(panel);
			currentPCScroll.getVerticalScrollBar().setUnitIncrement(75);

			uiAspect.add(currentPC.getDefLine().getDefiningPanel(), BorderLayout.PAGE_END);
			uiAspect.add(currentPCScroll, BorderLayout.CENTER);

			if (currentPC.getDefLine().getPlDef().getTag().equalsIgnoreCase("Cinematic") && currentPC.getPlannerGraph() != null &&
					PlannerFrame.SHOW_CIN)
			{
				uiAspect.add(currentPC.getPlannerGraph(), BorderLayout.PAGE_END);
			}

			currentPCScroll.revalidate();
			uiAspect.validate();
			uiAspect.repaint();
		}
		else
		{
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			currentPCScroll = new JScrollPane(panel);
			uiAspect.add(currentPCScroll, BorderLayout.CENTER);
			uiAspect.revalidate();
		}
	}

	public void updateAttributeList(int index)
	{
		plannerTree.updateTreeValues(name, listPC);
		uiAspect.validate();
	}

	public void addPlannerContainer(PlannerContainer pc)
	{
		this.listPC.add(pc);
	}

	public ArrayList<PlannerContainer> getListPC() {
		return listPC;
	}

	public void clearValues()
	{
		this.currentPC = null;
		this.listPC.clear();
		System.out.println("CLEAR VALUES");
		// plannerTree.updateTreeValues(name, listPC);
		/*
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
		*/
		uiAspect.revalidate();
		uiAspect.repaint();
		System.out.println("CLEARED CURRENT PC " + currentPC);
	}

	public boolean setSelectedListItem(int index)
	{
		System.out.println("SET SELECTED LIST ITEM " + index);
		if (index < listPC.size())
		{
			this.plannerTree.setSelectedIndex(index);
			return true;
		}
		return false;


	}

	public class TabAttributeTransferHandler extends TreeAttributeTransferHandler
	{
		private static final long serialVersionUID = 1L;

		public TabAttributeTransferHandler(ArrayList<PlannerContainer> plannerPCs) {
			super(plannerPCs);
		}

		@Override
		public boolean importData(TransferSupport ts) {
			boolean rc = super.importData(ts);
			if (rc)
				updateAttributeList(newIndex);
			return rc;
		}

	}

	public PlannerContainerDef getPlannerContainerDef()
	{
		return containersByName.get(containers[typeComboBox.getSelectedIndex()]);
	}

	public PlannerContainer getCurrentPC() {
		return currentPC;
	}

	public Vector<String> getItemList() {
		return plannerTree.getItemList();
	}

	public void refreshItem(PlannerContainer plannerContainer)
	{
		if (plannerTree != null && listPC != null)
			plannerTree.refreshItem(plannerContainer, listPC.indexOf(plannerContainer));
	}

	public void addAttribute(String name, int index)
	{
		plannerTree.addAttribute(name, selectedPC, index);
	}

	public void addLineToContainerAtIndex(PlannerLine lineToAdd, int indexOfLine, int indexOfContainer)
	{
		listPC.get(indexOfContainer).addLine(lineToAdd, indexOfLine);
	}

	@Override
	public void valueChanged(TreeSelectionEvent tse) {
		if (tse.getNewLeadSelectionPath() != null && plannerTree.getSelectedIndex() != -1)
		{
			setNewValues();
		}
	}

	public JPanel getUiAspect() {
		return uiAspect;
	}
}
