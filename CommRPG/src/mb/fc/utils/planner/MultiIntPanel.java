package mb.fc.utils.planner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class MultiIntPanel extends JPanel implements ActionListener 
{
	public ArrayList<String> mitems;
	
	public MultiIntPanel(ArrayList<String> mitems) {
		super();
		this.mitems = mitems;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("ADD"))
		{
			Vector<String> vs = new Vector<String>();
			vs.add("No value selected");
			vs.addAll(mitems);
			this.add(new JComboBox<String>(vs));			
		}
		else if (e.getActionCommand().equalsIgnoreCase("REMOVE") && this.getComponentCount() > 3)
		{
			for (int i = this.getComponentCount() - 1; i >= 0; i++)
			{
				if (this.getComponent(i) instanceof JComboBox<?>)
				{
					this.remove(i);
					break;
				}
			}
		}
		this.revalidate();
		this.repaint();
	}
}
