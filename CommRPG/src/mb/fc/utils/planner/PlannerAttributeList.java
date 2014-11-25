package mb.fc.utils.planner;

import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

public class PlannerAttributeList extends JScrollPane
{
	private JList<String> attributeList;
	private DefaultListModel<String> attributeListModel;

	private static final long serialVersionUID = 1L;

	public PlannerAttributeList(ListSelectionListener lsl)
	{
		attributeListModel = new DefaultListModel<String>();
		attributeList = new JList<String>(attributeListModel);
		this.setViewportView(attributeList);
		attributeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		attributeList.addListSelectionListener(lsl);
		attributeList.setDropMode(DropMode.INSERT);
		attributeList.setDragEnabled(true);
	}

	public PlannerAttributeList(PlannerContainer currentPC,
			ListSelectionListener lsl, AttributeTransferHandler transferHandler)
	{
		attributeListModel = new DefaultListModel<String>();
		for (PlannerLine pl : currentPC.getLines())
		{
			attributeListModel.addElement(pl.getPlDef().getName());
		}
		attributeList = new JList<String>(attributeListModel);
		this.setViewportView(attributeList);
		attributeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		attributeList.addListSelectionListener(lsl);
		attributeList.setTransferHandler(transferHandler);
		attributeList.setDropMode(DropMode.INSERT);
		attributeList.setDragEnabled(true);
		this.setPreferredSize(new Dimension(200, 400));
	}

	public void updateAttributeList(PlannerContainer currentPC, int index, AttributeTransferHandler transferHandler)
	{
		attributeListModel.removeAllElements();
		if (currentPC != null)
		{
			for (PlannerLine pl : currentPC.getLines())
			{
				attributeListModel.addElement(pl.getPlDef().getName());
			}
		}

		attributeList.setTransferHandler(transferHandler);

		if (attributeListModel.size() > index && index >= 0)
			attributeList.setSelectedIndex(index);

		attributeList.revalidate();
		this.revalidate();
		attributeList.repaint();
		this.repaint();
	}

	public int getSelectedIndex()
	{
		return attributeList.getSelectedIndex();
	}

	public void setSelectedIndex(int index)
	{
		attributeList.setSelectedIndex(index);
	}

	public int getListLength()
	{
		return attributeListModel.getSize();
	}
}
