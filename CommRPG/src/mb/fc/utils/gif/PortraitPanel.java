package mb.fc.utils.gif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class PortraitPanel extends JPanel implements ActionListener {
	private GifDecoder portraitDecoder;
	private ImagePanel imagePanel;
	private JButton leftButton;
	private JButton rightButton;
	private int currentIndex = 0;
	private Hashtable<String, Integer> battleActions = new Hashtable<String, Integer>();
	private JButton actionLabel;
	private int yPortraitSplit = 0;

	public PortraitPanel()
	{
		this.setLayout(new BorderLayout());
		imagePanel = new ImagePanel();

		JPanel sidePanel = new JPanel();
		leftButton = createActionButton("< --", "left", sidePanel);
		rightButton = createActionButton("-- >", "right", sidePanel);
		createActionButton("Start Talk", "Talk", sidePanel);
		createActionButton("Start Blink", "Blink", sidePanel);
		createActionButton("Start Idle", "Idle", sidePanel);
		leftButton.setEnabled(false);
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
		actionLabel = new JButton("Unassigned");
		actionLabel.setEnabled(false);
		System.out.println("CONSTRUCTOR");
		sidePanel.add(actionLabel, BorderLayout.PAGE_START);
		this.add(sidePanel, BorderLayout.LINE_START);
		this.add(imagePanel, BorderLayout.CENTER);
	}

	public void setPortraitDecoder(GifDecoder portraitDecoder) {
		System.out.println("SET PORTRAIT");
		actionLabel.setText("Unassigned");
		this.battleActions.clear();
		this.portraitDecoder = portraitDecoder;
		currentIndex = 0;
		yPortraitSplit = portraitDecoder.getFrame(currentIndex).getHeight() / 2;
		imagePanel.setCurrentImage(portraitDecoder.getFrame(currentIndex));
	}

	class ImagePanel extends JPanel implements MouseListener
	{
		private static final long serialVersionUID = 1L;

		private BufferedImage currentImage;

		public ImagePanel()
		{
			this.setPreferredSize(new Dimension(200, 300));
			this.addMouseListener(this);
		}


		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());

			int x = this.getWidth() / 2 - currentImage.getWidth() / 2;
			int y = (this.getHeight() - currentImage.getHeight()) / 2;
			if (currentImage != null)
			{
				g.drawImage(currentImage, x, y, this);

				g.setColor(Color.blue);
				g.drawLine(x, y + yPortraitSplit, x + currentImage.getWidth(), y + yPortraitSplit);
			}

		}



		public void setCurrentImage(BufferedImage currentImage) {
			this.currentImage = currentImage;
			this.repaint();
		}


		@Override
		public void mouseClicked(MouseEvent arg0) {
			int y = (this.getHeight() - currentImage.getHeight()) / 2;
			int clickY = arg0.getY();
			if (clickY > y && clickY < y + currentImage.getHeight())
			{
				yPortraitSplit = clickY - y;
				this.repaint();
			}
		}


		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}


		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}


		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}


		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equalsIgnoreCase("left"))
		{
			imagePanel.setCurrentImage(portraitDecoder.getFrame(--currentIndex));
			if (currentIndex == 0)
				leftButton.setEnabled(false);
			rightButton.setEnabled(true);

			actionLabel.setText("Unassigned");
			for (Map.Entry<String, Integer> s : battleActions.entrySet())
			{
				if (s.getValue() == currentIndex)
				{
					actionLabel.setText(s.getKey());
					break;
				}
			}
		}
		else if (cmd.equalsIgnoreCase("right"))
		{
			imagePanel.setCurrentImage(portraitDecoder.getFrame(++currentIndex));
			if (currentIndex + 1 == portraitDecoder.getFrameCount())
				rightButton.setEnabled(false);
			leftButton.setEnabled(true);

			actionLabel.setText("Unassigned");
			for (Map.Entry<String, Integer> s : battleActions.entrySet())
			{
				if (s.getValue() == currentIndex)
				{
					actionLabel.setText(s.getKey());

					break;
				}
			}
		}
		else
		{
			battleActions.put(cmd, currentIndex);
			actionLabel.setText(cmd);
		}

		System.out.println(actionLabel + " " + actionLabel.getText());
		actionLabel.repaint();
		this.repaint();
	}

	private JButton createActionButton(String text, String command, JPanel container)
	{
		JButton b = new JButton(text);
		b.addActionListener(this);
		b.setActionCommand(command);
		container.add(b);
		return b;
	}

	public Hashtable<String, Integer> getBattleActions() {
		return battleActions;
	}

	public int getyPortraitSplit() {
		return yPortraitSplit;
	}
}
