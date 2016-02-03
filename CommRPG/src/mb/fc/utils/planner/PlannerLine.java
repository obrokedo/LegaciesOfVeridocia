package mb.fc.utils.planner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

public class PlannerLine
{
	private static final long serialVersionUID = 1L;

	private PlannerLineDef plDef;
	private ArrayList<Component> components;
	private ArrayList<Object> values;
	private boolean isDefining;
	private JComboBox<String> box;
	private JPanel definingPanel;
	private JPanel uiAspect;

	public PlannerLine(PlannerLineDef plDef, boolean isDefining)
	{
		uiAspect = new JPanel();
		uiAspect.setLayout(new BoxLayout(uiAspect, BoxLayout.PAGE_AXIS));
		this.plDef = plDef;
		this.components = new ArrayList<Component>();
		this.values = new ArrayList<Object>();
		this.isDefining = isDefining;
	}

	public PlannerLine(PlannerLine plannerLine)
	{
		uiAspect = new JPanel();
		uiAspect.setLayout(new BoxLayout(uiAspect, BoxLayout.PAGE_AXIS));
		this.plDef = plannerLine.plDef;
		this.components = new ArrayList<Component>();
		this.values = new ArrayList<Object>();
		for (Object o : plannerLine.values)
			this.values.add(o);
		this.isDefining = plannerLine.isDefining;
	}

	public void setupUI(ArrayList<PlannerLineDef> allowableValues, ActionListener aListener,
			int index, ArrayList<ArrayList<String>> listOfLists, PlannerTab parentTab)
	{
		setupUI(allowableValues, aListener,
				index, listOfLists, true, true, parentTab);
	}

	public void setupUI(ArrayList<PlannerLineDef> allowableValues, ActionListener aListener,
			int index, ArrayList<ArrayList<String>> listOfLists, boolean displayButtons, boolean commitChanges, PlannerTab parentTab)
	{
		if (commitChanges)
			this.commitChanges();
		uiAspect.removeAll();
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
		JLabel headerLabel = new JLabel(plDef.getName().toUpperCase());
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
			if (displayButtons)
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
			}

			headDescPanel.add(headerPanel.add(new JLabel(this.plDef.getDescription())), BorderLayout.PAGE_END);
			headDescPanel.add(headerPanel, BorderLayout.CENTER);
			uiAspect.add(headDescPanel);
		}




		JPanel valuePanel = new JPanel();
		valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.PAGE_AXIS));

		for (int i = 0; i < plDef.getPlannerValues().size(); i++)
		{

			PlannerValueDef pv = plDef.getPlannerValues().get(i);
			// JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel panel = new JPanel(new BorderLayout());
			JLabel label = new JLabel(pv.getDisplayTag() + (pv.isOptional() ? "(Optional)" : ""));
			label.setPreferredSize(new Dimension(150, 25));
			JComponent c = null;
			panel.add(label, BorderLayout.LINE_START);
			switch (pv.getValueType())
			{
				case PlannerValueDef.TYPE_INT:
					if (pv.getRefersTo() == PlannerValueDef.REFERS_NONE)
					{
						SpinnerNumberModel snm = null;

						snm = new SpinnerNumberModel(0, -1, Integer.MAX_VALUE, 1);

						if (values.size() > i)
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
						if (values.size() > i)
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
					if (values.size() > i)
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
					if (values.size() > i)
						((JCheckBox) c).setSelected((boolean) values.get(i));
					break;
				case PlannerValueDef.TYPE_STRING:
					if (pv.getRefersTo() == PlannerValueDef.REFERS_NONE)
					{
						c = new JTextField(30);
						if (values.size() > i)
						{
							try
							{
								((JTextField) c).setText((String) values.get(i));
							}
							catch (Throwable t)
							{
								System.out.println(t);
							}
						}
					}
					else
					{
						Vector<String> items = new Vector<String>(listOfLists.get(pv.getRefersTo() - 1));
						c = new JComboBox<String>(items);
						if (values.size() > i)
							((JComboBox<?>) c).setSelectedItem(values.get(i));
					}
					break;
				case PlannerValueDef.TYPE_LONG_STRING:
					JTextArea ta = new JTextArea(5, 40);

					ta.setWrapStyleWord(true);
					ta.setLineWrap(true);
					if (values.size() > i)
					{
						try
						{
							ta.setText((String) values.get(i));
						}
						catch (Throwable t)
						{
							System.out.println(t);
						}
					}

					c = ta;
					break;

			}

			label.setToolTipText(pv.getDisplayDescription());
			c.setToolTipText(pv.getDisplayDescription());
			JLabel descriptionLabel = new JLabel(pv.getDisplayDescription());
			descriptionLabel.setOpaque(true);
			descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.BOLD));
			// descriptionLabel.setBackground(Color.DARK_GRAY);
			panel.add(descriptionLabel, BorderLayout.PAGE_START);
			panel.add(c);
			components.add(c);
			valuePanel.add(panel);
			valuePanel.add(Box.createRigidArea(new Dimension(5, 15)));
		}
		uiAspect.add(valuePanel);
	}

	public JPanel getDefiningPanel()
	{
		return definingPanel;
	}

	public void commitChanges()
	{
		if (components.size() > 0)
		{
			for (int i = 0; i < plDef.getPlannerValues().size(); i++)
			{
				PlannerValueDef pv = plDef.getPlannerValues().get(i);
				System.out.println(pv.getValueType());

				switch (pv.getValueType())
				{
					case PlannerValueDef.TYPE_BOOLEAN:
						if (i >= values.size())
							values.add(((JCheckBox) components.get(i)).isSelected());
						else
							values.set(i, ((JCheckBox) components.get(i)).isSelected());
						break;
					case PlannerValueDef.TYPE_STRING:
						if (pv.getRefersTo() == PlannerValueDef.REFERS_NONE)
						{
							if (((JTextField) components.get(i)).getText().trim().length() < 0 && !pv.isOptional())
							{
								throw new IllegalArgumentException();
							}

							if (i >= values.size())
								values.add(((JTextField) components.get(i)).getText());
							else
								values.set(i, ((JTextField) components.get(i)).getText());
						}
						else
						{
							if (i >= values.size())
								values.add(((JComboBox<?>) components.get(i)).getSelectedItem());
							else
								values.set(i, ((JComboBox<?>) components.get(i)).getSelectedItem());
						}
						break;
					case PlannerValueDef.TYPE_LONG_STRING:
						if (((JTextArea) components.get(i)).getText().trim().length() < 0 && !pv.isOptional())
						{
							throw new IllegalArgumentException();
						}

						if (i >= values.size())
							values.add(((JTextArea) components.get(i)).getText());
						else
							values.set(i, ((JTextArea) components.get(i)).getText());
						break;
					case PlannerValueDef.TYPE_INT:
						if (pv.getRefersTo() == PlannerValueDef.REFERS_NONE)
						{
							if (i >= values.size())
								values.add((int) ((JSpinner) components.get(i)).getValue());
							else
								values.set(i, (int) ((JSpinner) components.get(i)).getValue());
						}
						else
						{
							if (i >= values.size())
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

						if (i >= values.size())
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

	public JPanel getUiAspect() {
		return uiAspect;
	}
}
