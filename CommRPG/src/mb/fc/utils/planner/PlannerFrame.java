package mb.fc.utils.planner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PlannerFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private QuestList questList;
	private JScrollPane questScroll;

	public static void main(String args[])
	{
		PlannerFrame pf = new PlannerFrame();
		pf.initUI();
	}
	
	private void initUI()
	{		
		JPanel backPanel = new JPanel(new BorderLayout());
		
		questList = new QuestList();
				
		JPanel questButtonPanel = new JPanel();
		questButtonPanel.add(createActionButton("+", "addquest", this));
		questButtonPanel.add(createActionButton("-", "removequest", this));
		questScroll = new JScrollPane(questList);
		questScroll.setPreferredSize(new Dimension(200, 600));
		
		JPanel questPanel = new JPanel(new BorderLayout());
		questPanel.add(new JLabel("Quest names"), BorderLayout.PAGE_START);
		questPanel.add(questScroll, BorderLayout.CENTER);
		questPanel.add(questButtonPanel, BorderLayout.PAGE_END);
		
		backPanel.add(questPanel, BorderLayout.LINE_START);

		this.setContentPane(backPanel);
		
		this.setPreferredSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
	private JButton createActionButton(String text, String action, ActionListener listener)
	{
		JButton button = new JButton(text);
		button.setActionCommand(action);
		button.addActionListener(listener);
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equalsIgnoreCase("addquest"))
		{
			questList.addQuest("New Quest");
		}
	}
}
