package mb.fc.utils.planner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

public class PlannerLine extends JPanel
{
	private static final long serialVersionUID = 1L;

	private PlannerLineDef plDef;
	private ArrayList<Component> components;
	private ArrayList<Object> values;
	private boolean isDefining;
	private JComboBox<String> box;
	private JPanel definingPanel;

	public PlannerLine(PlannerLineDef plDef, boolean isDefining)
	{
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.plDef = plDef;
		this.components = new ArrayList<Component>();
		this.values = new ArrayList<Object>();
		this.isDefining = isDefining;
	}

	public PlannerLine(PlannerLine plannerLine)
	{
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.plDef = plannerLine.plDef;
		this.components = new ArrayList<Component>();
		this.values = new ArrayList<Object>();
		for (Object o : plannerLine.values)
			this.values.add(o);
		this.isDefining = plannerLine.isDefining;
	}

	public void setupUI(ArrayList<PlannerLineDef> allowableValues, ActionListener aListener,
			int index, ArrayList<ArrayList<String>> listOfLists)
	{
		this.commitChanges();
		this.removeAll();
		JPanel headDescPanel = new JPanel(new BorderLayout());
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		if (isDefining)
		{
			headDescPanel.setBackground(Color.DARK_GRAY);
			headerPanel.setBackground(Color.DARK_GRAY);
		}
		else
		{
			headerPanel.setBackground(Color.LIGHT_GRAY);
			headDescPanel.setBackground(Color.LIGHT_GRAY);
		}
		JLabel headerLabel = new JLabel(plDef.getTag().toUpperCase());
		headerPanel.add(headerLabel);
		if (isDefining)
		{
			headerLabel.setForeground(Color.WHITE);

			if (definingPanel == null)
			{
				System.out.println("CREATE NEW DEFIINING PANEL");
				Vector<String> allowableStrings = new Vector<String>();
				for (PlannerLineDef pld : allowableValues)
					allowableStrings.add(pld.getName());
				box = new JComboBox<String>(allowableStrings);
				box.setMaximumRowCount(30);
				headerPanel.add(box);

				JButton addLineButton = new JButton("Add line");
				addLineButton.setActionCommand("addline");
				addLineButton.addActionListener(aListener);
				headerPanel.add(addLineButton);

				if (plDef.getTag().equalsIgnoreCase("Cinematic"))
				{
					JButton refreshButton = new JButton("Refresh");
					refreshButton.setActionCommand("refresh");
					refreshButton.addActionListener(aListener);
					headerPanel.add(refreshButton);
				}
				definingPanel = headerPanel;
			}
		}
		else
		{
			JButton removeLineButton = new JButton("Remove " + plDef.getName());
			removeLineButton.setActionCommand("remove " + index);
			removeLineButton.addActionListener(aListener);
			headerPanel.add(removeLineButton);
			JButton moveupButton = new JButton("Move Up");
			moveupButton.setActionCommand("moveup " + index);
			moveupButton.addActionListener(aListener);
			headerPanel.add(moveupButton);
			JButton movedownButton = new JButton("Move Down");
			movedownButton.setActionCommand("movedown " + index);
			movedownButton.addActionListener(aListener);
			headerPanel.add(movedownButton);
			JButton copyButton = new JButton("Duplicate");
			copyButton.setActionCommand("duplicate " + index);
			copyButton.addActionListener(aListener);
			headerPanel.add(copyButton);

			headDescPanel.add(headerPanel.add(new JLabel(this.plDef.getDescription())), BorderLayout.PAGE_END);
			headDescPanel.add(headerPanel, BorderLayout.CENTER);
			this.add(headDescPanel);
		}




		JPanel valuePanel = new JPanel();
		valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.PAGE_AXIS));

		for (int i = 0; i < plDef.getPlannerValues().size(); i++)
		{
			PlannerValueDef pv = plDef.getPlannerValues().get(i);
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel label = new JLabel(pv.getDisplayTag() + (pv.isOptional() ? "(Optional)" : ""));
			JComponent c = null;
			panel.add(label);
			switch (pv.getValueType())
			{
				case PlannerValueDef.TYPE_INT:
					if (pv.getRefersTo() == PlannerValueDef.REFERS_NONE)
					{
						SpinnerNumberModel snm = null;

						snm = new SpinnerNumberModel(0, -1, Integer.MAX_VALUE, 1);

						if (values.size() > 0)
							snm.setValue(values.get(i));
						c = new JSpinner(snm);

						((NumberFormatter) ((JSpinner.NumberEditor) ((JSpinner) c).getEditor()).getTextField().getFormatter()).setAllowsInvalid(false);
					}
					else
					{
						Vector<String> items = new Vector<String>();
						items.add("No value selected");
						items.addAll(listOfLists.get(pv.getRefersTo() - 1));
						c = new JComboBox<String>(items);
						if (values.size() > 0)
							((JComboBox<?>) c).setSelectedIndex(((int) values.get(i)));
					}
					break;
				case PlannerValueDef.TYPE_MULTI_INT:
					Vector<String> mitems = new Vector<String>();
					mitems.add("No value selected");
					mitems.addAll(listOfLists.get(pv.getRefersTo() - 1));

					c = new MultiIntPanel(listOfLists.get(pv.getRefersTo() - 1));
					JButton ab = new JButton("Add Item");
					ab.addActionListener((MultiIntPanel) c);
					ab.setActionCommand("ADD");
					c.add(ab);

					JButton rb = new JButton("Remove Last Item");
					rb.addActionListener((MultiIntPanel) c);
					rb.setActionCommand("REMOVE");
					c.add(rb);
					if (values.size() > 0)
					{
						String[] vals = ((String) values.get(i)).split(",");

						for (String s : vals)
						{
							JComboBox<String> jcb = new JComboBox<String>(mitems);
							if (s.length() > 0)
								jcb.setSelectedIndex(Integer.parseInt(s));
							c.add(jcb);
						}
					}
					else
						c.add(new JComboBox<String>(mitems));

					break;
				case PlannerValueDef.TYPE_BOOLEAN:
					c = new JCheckBox();
					if (values.size() > 0)
						((JCheckBox) c).setSelected((boolean) values.get(i));
					break;
				case PlannerValueDef.TYPE_STRING:
					if (pv.getRefersTo() == PlannerValueDef.REFERS_NONE)
					{
						c = new JTextField(30);
						if (values.size() > 0)
							((JTextField) c).setText((String) values.get(i));
					}
					else
					{
						Vector<String> items = new Vector<String>(listOfLists.get(pv.getRefersTo() - 1));
						c = new JComboBox<String>(items);
						if (values.size() > 0)
							((JComboBox<?>) c).setSelectedItem(values.get(i));
					}
					break;
			}

			label.setToolTipText(pv.getDisplayDescription());
			c.setToolTipText(pv.getDisplayDescription());
			panel.add(c);
			components.add(c);
			valuePanel.add(panel);
		}
		this.add(valuePanel);
	}

	public JPanel getDefiningPanel()
	{
		return definingPanel;
	}

	public void commitChanges()
	{
		if (components.size() > 0)
		{
			boolean noValues = false;
			if (values.size() == 0)
				noValues = true;

			for (int i = 0; i < plDef.getPlannerValues().size(); i++)
			{
				PlannerValueDef pv = plDef.getPlannerValues().get(i);

				switch (pv.getValueType())
				{
					case PlannerValueDef.TYPE_BOOLEAN:
						if (noValues)
							values.add(((JCheckBox) components.get(i)).isSelected());
						else
							values.set(i, ((JCheckBox) components.get(i)).isSelected());
						break;
					case PlannerValueDef.TYPE_STRING:
						if (pv.getRefersTo() == PlannerValueDef.REFERS_NONE)
						{
							if (noValues)
								values.add(((JTextField) components.get(i)).getText());
							else
								values.set(i, ((JTextField) components.get(i)).getText());
						}
						else
						{
							if (noValues)
								values.add(((JComboBox<?>) components.get(i)).getSelectedItem());
							else
								values.set(i, ((JComboBox<?>) components.get(i)).getSelectedItem());
						}
						break;
					case PlannerValueDef.TYPE_INT:
						if (pv.getRefersTo() == PlannerValueDef.REFERS_NONE)
						{
							if (noValues)
								values.add((int) ((JSpinner) components.get(i)).getValue());
							else
								values.set(i, (int) ((JSpinner) components.get(i)).getValue());
						}
						else
						{
							if (noValues)
								values.add(((JComboBox<?>) components.get(i)).getSelectedIndex());
							else
								values.set(i, ((JComboBox<?>) components.get(i)).getSelectedIndex());
						}
						break;
					case PlannerValueDef.TYPE_MULTI_INT:
						String multi = "";
						MultiIntPanel mip = (MultiIntPanel) components.get(i);
						for (int j = 2; j < mip.getComponentCount(); j++)
						{
							multi = multi + ((JComboBox<?>)mip.getComponent(j)).getSelectedIndex();
							if (j + 1 != mip.getComponentCount())
								multi = multi + ",";
						}
						System.out.println("Multi: " + multi);

						if (noValues)
							values.add(multi);
						else
							values.set(i, multi);

						break;
				}
			}

			components.clear();
		}
	}

	public ArrayList<Component> getPlannerLineComponents() {
		return components;
	}

	public void setComponents(ArrayList<Component> components) {
		this.components = components;
	}

	public int getSelectedItem()
	{
		System.out.println(box.getSelectedIndex());
		System.out.println(box);
		return box.getSelectedIndex();
	}

	public ArrayList<Object> getValues() {
		return values;
	}

	public boolean isDefining() {
		return isDefining;
	}

	public PlannerLineDef getPlDef() {
		return plDef;
	}
}
