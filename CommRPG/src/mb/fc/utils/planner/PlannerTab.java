package mb.fc.utils.planner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PlannerTab extends JPanel implements ActionListener, ItemListener, ListSelectionListener
{
	protected static final long serialVersionUID = 1L;

	protected Hashtable<String, PlannerContainerDef> containersByName;
	protected String[] containers;
	protected JComboBox<String> list;
	protected DefaultComboBoxModel<String> listModel;
	protected ArrayList<PlannerContainer> listPC;
	protected PlannerContainer currentPC;
	protected int selectedPC;
	protected JScrollPane currentPCScroll;
	protected JComboBox<String> typeComboBox;
	protected int refersTo;
	protected PlannerFrame plannerFrame;
	protected JList<String> attributeList;
	protected DefaultListModel<String> attributeListModel;
	protected JScrollPane attributeScrollPane;
	private JPanel listPanel;

	public PlannerTab(Hashtable<String, PlannerContainerDef> containersByName,
			String[] containers, int refersTo, PlannerFrame plannerFrame)
	{
		super(new BorderLayout());

		listPanel = new JPanel();
		listModel = new DefaultComboBoxModel<String>();
		list = new JComboBox<String>(listModel);
		list.addItemListener(this);

		listPC = new ArrayList<PlannerContainer>();

		this.containersByName = containersByName;
		this.containers = containers;

		listPanel.add(list);

		listPanel.setBackground(Color.DARK_GRAY);
		listPanel.add(PlannerFrame.createActionButton("Add", "add", this));
		listPanel.add(PlannerFrame.createActionButton("Remove", "remove", this));

		// this.add(listPanel, BorderLayout.LINE_START);
		this.add(listPanel, BorderLayout.PAGE_START);

		typeComboBox = new JComboBox<String>(containers);

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
			listPC.add(new PlannerContainer(containersByName.get(type), this));
			PlannerContainerDef pcd = containersByName.get(type);
			pcd.getDataLines().add("");

			System.out.println("ADD ELEMENT");
			listModel.addElement("(ID: " + listModel.getSize() + ") " + type);
			System.out.println("AFTER ADD ELEMENT");
			list.setSelectedIndex(listModel.getSize() - 1);
		}
		else if (command.equalsIgnoreCase("remove"))
		{
			int selected = list.getSelectedIndex();
			plannerFrame.removeReferences(refersTo, selected);
			currentPC.getPcdef().getDataLines().remove(selected);
			currentPC = null;
			listModel.removeElementAt(selected);
			listPC.remove(selected);
		}
	}

	public void commitChanges()
	{
		if (currentPC != null)
		{
			currentPC.commitChanges();
			updateAttributeList(-1);
		}
	}

	public void setNewValues()
	{
		System.out.println("SET NEW VALUES");

		list.removeItemListener(this);
		if (currentPCScroll != null)
		{
			this.remove(currentPCScroll);
		}

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
			listModel.removeElementAt(selectedPC);
			listModel.insertElementAt("(ID: " + (selectedPC) + ") " + currentPC.getDescription(), selectedPC);
			currentPC.getPcdef().getDataLines().set(selectedPC, currentPC.getDescription());
			if (list.getSelectedIndex() == -1)
				list.setSelectedIndex(0);
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

			this.add(currentPC.getDefLine().getDefiningPanel(), BorderLayout.PAGE_END);
			this.add(currentPCScroll, BorderLayout.CENTER);

			if (currentPC.getDefLine().getPlDef().getTag().equalsIgnoreCase("Cinematic") && currentPC.getPlannerGraph() != null &&
					PlannerFrame.SHOW_CIN)
			{
				this.add(currentPC.getPlannerGraph(), BorderLayout.PAGE_END);
			}

			updateAttributeList(0);

			currentPCScroll.revalidate();
			this.repaint();
		}
		else
		{
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			currentPCScroll = new JScrollPane(panel);
			this.add(currentPCScroll, BorderLayout.CENTER);
			this.revalidate();
		}

		list.addItemListener(this);
	}

	public void updateAttributeList(int index)
	{
		if (attributeScrollPane != null)
		{
			this.remove(attributeScrollPane);
			attributeList.removeListSelectionListener(this);
		}

		attributeListModel = new DefaultListModel<String>();
		for (PlannerLine pl : currentPC.getLines())
		{
			attributeListModel.addElement(pl.getPlDef().getName());
		}
		attributeList = new JList<String>(attributeListModel);
		attributeScrollPane = new JScrollPane(attributeList);
		this.add(attributeScrollPane, BorderLayout.LINE_START);
		attributeList.setPreferredSize(new Dimension(150, attributeList.getPreferredSize().height));
		attributeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		attributeList.addListSelectionListener(this);
		attributeList.setTransferHandler(new AttributeTransferHandler());
		attributeList.setDropMode(DropMode.INSERT);
		attributeList.setDragEnabled(true);

		if (attributeListModel.size() > index && index >= 0)
			attributeList.setSelectedIndex(index);

		this.validate();
	}

	public void addPlannerContainer(PlannerContainer pc)
	{
		this.listPC.add(pc);
		listModel.addElement("(ID: " + (this.listPC.size() - 1) + ") " + pc.getDescription());
	}

	public ArrayList<PlannerContainer> getListPC() {
		return listPC;
	}

	public void clearValues()
	{
		list.removeItemListener(this);
		listModel.removeAllElements();
		this.currentPC = null;
		this.listPC.clear();
		System.out.println("CLEAR VALUES");
		this.removeAll();
		this.add(listPanel, BorderLayout.PAGE_START);
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
		this.revalidate();
		this.repaint();
		list.addItemListener(this);
		System.out.println("CLEARED CURRENT PC " + currentPC);
	}

	public boolean setSelectedListItem(int index)
	{
		System.out.println("SET SELECTED LIST ITEM " + index);
		if (index < list.getModel().getSize())
		{
			this.list.setSelectedIndex(index);
			System.out.println("NOW SELECTED " + this.list.getSelectedIndex());
			return true;
		}
		return false;


	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		System.out.println("COMBO BOX SELECTED CHANGED");
		if (e.getStateChange() == ItemEvent.SELECTED)
			setNewValues();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting())
		{
			if (currentPC != null)
			{
				currentPC.setupUI(attributeList.getSelectedIndex());
				currentPCScroll.revalidate();
			}
		}
	}

	public class AttributeTransferHandler extends TransferHandler
	{
		private static final long serialVersionUID = 1L;
		private final DataFlavor localObjectFlavor;
		private String transferedObjects = null;
		private int index;

		public AttributeTransferHandler() {
			super();
			localObjectFlavor = new ActivationDataFlavor(
				      String.class, DataFlavor.javaJVMLocalObjectMimeType, "Item");
		}

		@Override
		public boolean canImport(TransferSupport arg0) {
			return true;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			JList<?> list = (JList<?>) c;
		    index = list.getSelectedIndex();
		    transferedObjects = (String) list.getSelectedValue();
		    return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
		}

		@Override
		protected void exportDone(JComponent arg0, Transferable arg1, int arg2) {
			super.exportDone(arg0, arg1, arg2);
		}

		@Override
		public int getSourceActions(JComponent arg0) {
			return TransferHandler.MOVE;
		}

		@Override
		public boolean importData(TransferSupport ts) {

			JList.DropLocation dl = (JList.DropLocation)ts.getDropLocation();
			if (index != dl.getIndex())
			{
				if (currentPC == null)
					return false;

				ArrayList<PlannerLine> pls = currentPC.getLines();
				int newIndex = dl.getIndex();
				if (dl.getIndex() < index)
				{
					pls.add(dl.getIndex(), pls.remove(index));
				}
				else
				{
					pls.add(--newIndex, pls.remove(index));
				}

				updateAttributeList(newIndex);
				return true;
			}
			System.out.println(dl.getIndex() + " " + index);

			return false;
		}
	}

	public PlannerContainerDef getPlannerContainerDef()
	{
		return containersByName.get(containers[typeComboBox.getSelectedIndex()]);
	}

	public PlannerContainer getCurrentPC() {
		return currentPC;
	}

	public JComboBox<String> getItemList() {
		return list;
	}
}
