package mb.fc.utils.planner;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class QuestList extends JTable
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<String> items = new ArrayList<String>();

	public QuestList()
	{
		this.setModel(new QuestListModel());
		this.setFillsViewportHeight(true);
	}
	
	public void addQuest(String quest)
	{
		items.add(quest);
		((QuestListModel) this.getModel()).fireTableDataChanged();
	}
	
	private class QuestListModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public int getRowCount() {
			return items.size();
		}

		@Override
		public Object getValueAt(int row, int column) {			
			return items.get(row);
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return true;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			items.set(rowIndex, (String) aValue);
			fireTableDataChanged();
		}
	}
}
