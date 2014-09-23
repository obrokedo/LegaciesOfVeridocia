package mb.fc.utils.planner;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MapTriggerPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private JTextArea generatedText;
	private PlannerContainer currentPC = null;
	private JTextField nameTextField;
	private JTextField valueTextField;

	public MapTriggerPanel(Hashtable<String, PlannerContainerDef> containersByName)
	{
		super(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		JButton resetButton = new JButton("Reset/New");
		JButton generateButton = new JButton("Generate");
		generateButton.setActionCommand("gen");
		generateButton.addActionListener(this);

		this.add(buttonPanel, BorderLayout.PAGE_START);
		generatedText = new JTextArea(100, 10);

		currentPC = new PlannerContainer(containersByName.get("map"));
		System.out.println(currentPC.getDefLine().getDefiningPanel());

		currentPC.setupUI();

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(currentPC);
		JScrollPane currentPCScroll = new JScrollPane(panel);
		currentPCScroll.getVerticalScrollBar().setUnitIncrement(75);
		currentPC.getDefLine().getDefiningPanel().add(resetButton);
		currentPC.getDefLine().getDefiningPanel().add(generateButton);
		this.add(currentPC.getDefLine().getDefiningPanel(), BorderLayout.PAGE_START);
		this.add(currentPCScroll, BorderLayout.CENTER);

		JPanel outputPanel = new JPanel(new BorderLayout());
		nameTextField = new JTextField(100);
		valueTextField = new JTextField(100);
		outputPanel.add(nameTextField, BorderLayout.PAGE_START);
		outputPanel.add(valueTextField, BorderLayout.PAGE_END);

		this.add(outputPanel, BorderLayout.PAGE_END);
	}

	@Override
	public void actionPerformed(ActionEvent al)
	{
		String command = al.getActionCommand();
		if (command.equalsIgnoreCase("gen"))
		{
			nameTextField.setText(currentPC.getDefLine().getPlDef().getTag());
			String output = "";
			for (PlannerLine pl : currentPC.getLines())
				for (PlannerValueDef pvd : pl.getPlDef().getPlannerValues())
				{
					output = output + pvd.getTag() + "=" + pl.getValues().get(0);
				}

			System.out.println(output);
		}
	}
}
