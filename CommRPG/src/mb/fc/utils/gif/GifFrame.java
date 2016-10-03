package mb.fc.utils.gif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GifFrame extends JFrame implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = 1L;

	private GifDecoder battleGifDecoder = new GifDecoder();
	private GifDecoder walkGifDecoder = new GifDecoder();
	private GifDecoder portraitDecoder = new GifDecoder();
	private boolean hasPortrait = false;
	private ImagePanel imagePanel = new ImagePanel();
	private Hashtable<String, Integer> battleActions = new Hashtable<String, Integer>();
	private JButton leftButton;
	private JButton rightButton;
	private JButton playButton;
	private int currentIndex = 0;
	private JLabel topLabel;
	private static final Color TRANS = new Color(95, 134, 134);
	private JSpinner xOffsetSpinner;
	private JSpinner yOffsetSpinner;
	private JTabbedPane tabbedPane;
	private PortraitPanel portraitPanel;
	private boolean initialized = false;

	public GifFrame()
	{
		super("Super Ugly Animator DEV 1.24");
		this.setupUI();

		// loadImages();
	}

	private void loadImages()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select Battle Animation Gif");
		fc.showOpenDialog(this);
		File battleFile = fc.getSelectedFile();
		if (battleFile == null || !battleFile.getName().endsWith(".gif"))
		{
			JOptionPane.showMessageDialog(this, "ERROR: Selected file must be a .gif");
			return;
		}
		fc.setDialogTitle("Select Walking Animation Gif");
		fc.showOpenDialog(this);
		File walkFile = fc.getSelectedFile();
		if (walkFile == null || !walkFile.getName().endsWith(".gif"))
		{
			JOptionPane.showMessageDialog(this, "ERROR: Selected file must be a .gif");
			return;
		}

		fc.setDialogTitle("Select Portrait Animation Gif");
		int rc = fc.showOpenDialog(this);
		File portraitFile = fc.getSelectedFile();
		if (rc == JOptionPane.OK_OPTION && portraitFile != null && portraitFile.getName().endsWith(".gif"))
		{
			portraitDecoder.read(portraitFile.getPath());
			portraitPanel.setPortraitDecoder(portraitDecoder);
			tabbedPane.setEnabledAt(1, true);
			hasPortrait = true;
		}
		else
		{
			tabbedPane.setEnabledAt(1, false);
			hasPortrait = false;
		}

		loadBattleGif(battleFile.getPath());
		loadWalkGif(walkFile.getPath());
		this.initialized = true;
		this.repaint();
	}

	private void setupUI()
	{
		tabbedPane = new JTabbedPane();
		JPanel backPanel = new JPanel(new BorderLayout());
		JPanel sidePanel = new JPanel();
		sidePanel.setPreferredSize(new Dimension(150, 0));
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		createActionButton("Set Stand", "Stand", sidePanel);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		createActionButton("Set Attack", "Attack", sidePanel);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		createActionButton("Set Winddown", "Winddown", sidePanel);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		createActionButton("Set Dodge", "Dodge", sidePanel);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		createActionButton("Set Spell", "Spell", sidePanel);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		createActionButton("Set Item", "Item", sidePanel);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		createActionButton("Set Special", "Special", sidePanel);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		playButton = createActionButton("Play Action", "play", sidePanel);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		createActionButton("Export Hero", "Export Hero", sidePanel);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		createActionButton("Export Enemy", "Export Enemy", sidePanel);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		createActionButton("Load New Gifs", "Load", sidePanel);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));

		sidePanel.add(new JLabel("X coordinate offset"));
		xOffsetSpinner = new JSpinner();
		xOffsetSpinner.setPreferredSize(new Dimension(200, 30));
		xOffsetSpinner.setMaximumSize(new Dimension(150, 30));
		xOffsetSpinner.addChangeListener(this);
		xOffsetSpinner.setModel(new SpinnerNumberModel(0, -200, 200, 1));
		xOffsetSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
		sidePanel.add(xOffsetSpinner);
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));

		sidePanel.add(new JLabel("Y coordinate offset"));
		yOffsetSpinner = new JSpinner();
		yOffsetSpinner.setPreferredSize(new Dimension(200, 30));
		yOffsetSpinner.setMaximumSize(new Dimension(150, 30));
		yOffsetSpinner.addChangeListener(this);
		yOffsetSpinner.setModel(new SpinnerNumberModel(0, -200, 200, 1));
		yOffsetSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
		sidePanel.add(yOffsetSpinner);
		sidePanel.add(Box.createGlue());
		sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));

		topLabel = new JLabel(" Current Frame Animation: Unassigned");
		topLabel.setForeground(Color.white);
		topLabel.setPreferredSize(new Dimension(0, 30));
		topLabel.setBackground(Color.DARK_GRAY);
		topLabel.setOpaque(true);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(Color.DARK_GRAY);
		leftButton = createActionButton("< --", "left", bottomPanel);
		rightButton = createActionButton("-- >", "right", bottomPanel);
		leftButton.setEnabled(false);
		rightButton.setEnabled(false);
		playButton.setEnabled(false);


		backPanel.add(sidePanel, BorderLayout.LINE_START);
		backPanel.add(imagePanel, BorderLayout.CENTER);
		backPanel.add(bottomPanel, BorderLayout.PAGE_END);
		backPanel.add(topLabel, BorderLayout.PAGE_START);
		tabbedPane.add(backPanel);
		tabbedPane.setTitleAt(0, "Animation Definitions");

		portraitPanel = new PortraitPanel();
		tabbedPane.add(portraitPanel);
		tabbedPane.setTitleAt(1, "Portrait Definitions");
		tabbedPane.setEnabledAt(1, false);
		this.setContentPane(tabbedPane);

		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setMinimumSize(new Dimension(500, 600));
		this.pack();
		// this.setVisible(true);
	}

	public void loadBattleGif(String file)
	{
		currentIndex = 0;
		leftButton.setEnabled(false);
		if (currentIndex + 1 != battleGifDecoder.getFrameCount())
			rightButton.setEnabled(true);
		battleGifDecoder.read(file);
		imagePanel.setCurrentImage(battleGifDecoder.getFrame(0));
	}

	public void loadWalkGif(String file)
	{
		walkGifDecoder.read(file);
	}

	public static void main(String args[])
	{
		new GifFrame();
	}

	public void export(boolean hero)
	{
		 Dimension fullImage = new Dimension(battleGifDecoder.getFrameCount() * battleGifDecoder.getFrameSize().width, (int) battleGifDecoder.getFrameSize().getHeight());
		 BufferedImage bim = new BufferedImage(fullImage.width + walkGifDecoder.getFrameSize().width * 8 + (hasPortrait ? portraitDecoder.getFrameSize().width * portraitDecoder.getFrameCount(): 0),
				 fullImage.height, BufferedImage.TYPE_INT_ARGB);

		  ArrayList<String> spriteSheetContents = new ArrayList<String>();
		  spriteSheetContents.add("<img name=\"" + battleGifDecoder.getFileName().substring(
				  battleGifDecoder.getFileName().lastIndexOf("\\") + 1).replace(".gif", ".png") + "\" w=\"" + fullImage.width + "\" h=\"" + fullImage.height + "\">\n");
		  spriteSheetContents.add("\t<definitions>\n");
		  spriteSheetContents.add("\t\t<dir name=\"/\">\n");
		  spriteSheetContents.add("\t\t\t<dir name=\"Unpromoted\">\n");
		  Graphics g = bim.createGraphics();
		  for (int i = 0; i < battleGifDecoder.getFrameCount(); i++)
		  {
			  spriteSheetContents.add("\t\t\t\t<spr name=\"Frame" + i + "\" x=\"" + i * battleGifDecoder.getFrameSize().width + "\" y=\"0\" w=\""
					  + battleGifDecoder.getFrameSize().width + "\" h=\"" + battleGifDecoder.getFrameSize().height + "\"/>\n");
			  g.drawImage(battleGifDecoder.getFrame(i), i * battleGifDecoder.getFrameSize().width, 0, null);
		  }

		  for (int i = 0; i < 6; i++)
		  {
			  g.drawImage(transformColorToTransparency(walkGifDecoder.getFrame(i), TRANS), battleGifDecoder.getFrameCount() * battleGifDecoder.getFrameSize().width +
					  i * walkGifDecoder.getFrameSize().width, 0, this);

			  int width = battleGifDecoder.getFrameCount() * battleGifDecoder.getFrameSize().width +
					  i * walkGifDecoder.getFrameSize().width;

			  spriteSheetContents.add("\t\t\t\t<spr name=\"Walk" + i + "\" x=\"" + width + "\" y=\"0\" w=\""
					  + walkGifDecoder.getFrameSize().width + "\" h=\"" + walkGifDecoder.getFrameSize().height + "\"/>\n");
		  }

		  if (hasPortrait)
		  {
			  for (int i = 0; i < portraitDecoder.getFrameCount(); i++)
			  {
				  int placeX = battleGifDecoder.getFrameCount() * battleGifDecoder.getFrameSize().width +
						  8 * walkGifDecoder.getFrameSize().width + i * portraitDecoder.getFrameSize().width;
				  spriteSheetContents.add("\t\t\t\t<spr name=\"PortraitTop" + i + "\" x=\"" + placeX + "\" y=\"0\" w=\""
						  + portraitDecoder.getFrameSize().width + "\" h=\"" + portraitPanel.getyPortraitSplit() + "\"/>\n");
				  spriteSheetContents.add("\t\t\t\t<spr name=\"PortraitBottom" + i + "\" x=\"" + placeX + "\" y=\"" + portraitPanel.getyPortraitSplit() + "\" w=\""
						  + portraitDecoder.getFrameSize().width + "\" h=\"" + (portraitDecoder.getFrameSize().height - portraitPanel.getyPortraitSplit()) + "\"/>\n");
				  g.drawImage(portraitDecoder.getFrame(i), placeX, 0, null);
			  }
		  }

		  spriteSheetContents.add("\t\t\t</dir>\n");
		  spriteSheetContents.add("\t\t</dir>\n");
		  spriteSheetContents.add("\t</definitions>\n");
		  spriteSheetContents.add("</img>\n");

		  g.dispose();

		  ArrayList<String> animStrings = new ArrayList<String>();
		  animStrings.add("<animations spriteSheet=\"" + battleGifDecoder.getFileName().substring(
				  battleGifDecoder.getFileName().lastIndexOf("\\") + 1).replace(".gif", ".sprites") + "\" ver=\"1.2\">");
		  addWalkAnimations(animStrings);


		  HashSet<Integer> keyFrames = new HashSet<Integer>(battleActions.values());
		  for (Map.Entry<String,Integer> e : battleActions.entrySet())
		  {
			  animStrings.add("<anim name=\"Un" + e.getKey() + "\" loops=\"0\">");

			  int count = 0;
			  int index = e.getValue();

			  while (true)
			  {
				  animStrings.add("<cell index=\"" + count + "\" delay=\"" + battleGifDecoder.getDelay(index) + "\">");
				  if (!hero)
					  animStrings.add("<spr name=\"/Unpromoted/Frame" + index + "\" x=\"" + (65 + (int) xOffsetSpinner.getValue()) + "\" y=\"" + (-48 + (int) yOffsetSpinner.getValue()) + "\" z=\"0\"/>");
				  else
					  animStrings.add("<spr name=\"/Unpromoted/Frame" + index + "\" x=\"" + (241 + (int) xOffsetSpinner.getValue()) + "\" y=\"" + (-48 + (int) yOffsetSpinner.getValue()) + "\" z=\"0\"/>");
				  animStrings.add("</cell>");

				  count++;
				  index++;

				  if (keyFrames.contains(index) || index == battleGifDecoder.getFrameCount())
					  break;
			  }

			  animStrings.add("</anim>");
		  }

		  if (hasPortrait)
		  {
			  keyFrames = new HashSet<Integer>(portraitPanel.getBattleActions().values());
			  for (Map.Entry<String,Integer> e : portraitPanel.getBattleActions().entrySet())
			  {
				  animStrings.add("<anim name=\"UnPort" + e.getKey() + "\" loops=\"0\">");

				  int count = 0;
				  int index = e.getValue();

				  while (true)
				  {
					  animStrings.add("<cell index=\"" + count + "\" delay=\"" + portraitDecoder.getDelay(index) + "\">");
					  if (e.getKey().equalsIgnoreCase("Idle"))
					  {
						  animStrings.add("<spr name=\"/Unpromoted/PortraitTop" + index + "\" x=\"0\" y=\"0\" z=\"0\"/>");
						  animStrings.add("<spr name=\"/Unpromoted/PortraitBottom" + index + "\" x=\"0\" y=\"0\" z=\"0\"/>");
					  }
					  else if (e.getKey().equalsIgnoreCase("Blink"))
					  {
						  animStrings.add("<spr name=\"/Unpromoted/PortraitTop" + index + "\" x=\"0\" y=\"0\" z=\"0\"/>");
					  }
					  else if (e.getKey().equalsIgnoreCase("Talk"))
					  {
						  animStrings.add("<spr name=\"/Unpromoted/PortraitBottom" + index + "\" x=\"0\" y=\"0\" z=\"0\"/>");
					  }


					  animStrings.add("</cell>");

					  count++;
					  index++;

					  if (keyFrames.contains(index) || index == portraitDecoder.getFrameCount())
						  break;
				  }

				  animStrings.add("</anim>");
			  }
		  }

		  animStrings.add("</animations>");

		  Path path = Paths.get(battleGifDecoder.getFileName().replace(".gif", ".sprites"));
		  Path animPath = Paths.get(battleGifDecoder.getFileName().replace(".gif", ".anim"));

		  File outputfile = new File(battleGifDecoder.getFileName().replace(".gif", ".png"));
			try {
				Files.write(path, spriteSheetContents, StandardCharsets.UTF_8);
				Files.write(animPath, animStrings, StandardCharsets.UTF_8);
				ImageIO.write(bim, "png", outputfile);
				JOptionPane.showMessageDialog(this, "Animation successfully written to " + path.toString());
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "An error occurred while trying to save the animation:"
						+ e.getMessage(), "Error saving animation", JOptionPane.ERROR_MESSAGE);
			}
	}

	private void addWalkAnimations(ArrayList<String> anims)
	{
		anims.add("<anim name=\"UnUp\" loops=\"0\">");
		anims.add("<cell index=\"0\" delay=\"1\">");
		anims.add("<spr name=\"/Unpromoted/Walk4\" x=\"0\" y=\"0\" z=\"0\"/>");
		anims.add("</cell>");
		anims.add("<cell index=\"1\" delay=\"1\">");
		anims.add("<spr name=\"/Unpromoted/Walk5\" x=\"0\" y=\"0\" z=\"0\"/>");
		anims.add("</cell>");
		anims.add("</anim>");
		anims.add("<anim name=\"UnDown\" loops=\"0\">");
		anims.add("<cell index=\"0\" delay=\"1\">");
		anims.add("<spr name=\"/Unpromoted/Walk0\" x=\"0\" y=\"0\" z=\"0\"/>");
		anims.add("</cell>");
		anims.add("<cell index=\"1\" delay=\"1\">");
		anims.add("<spr name=\"/Unpromoted/Walk1\" x=\"0\" y=\"0\" z=\"0\"/>");
		anims.add("</cell>");
		anims.add("</anim>");
		anims.add("<anim name=\"UnLeft\" loops=\"0\">");
		anims.add("<cell index=\"0\" delay=\"1\">");
		anims.add("<spr name=\"/Unpromoted/Walk2\" x=\"0\" y=\"0\" z=\"0\" flipH=\"1\"/>");
		anims.add("</cell>");
		anims.add("<cell index=\"1\" delay=\"1\">");
		anims.add("<spr name=\"/Unpromoted/Walk3\" x=\"0\" y=\"0\" z=\"0\" flipH=\"1\"/>");
		anims.add("</cell>");
		anims.add("</anim>");
		anims.add("<anim name=\"UnRight\" loops=\"0\">");
		anims.add("<cell index=\"0\" delay=\"1\">");
		anims.add("<spr name=\"/Unpromoted/Walk2\" x=\"0\" y=\"0\" z=\"0\"/>");
		anims.add("</cell>");
		anims.add("<cell index=\"1\" delay=\"1\">");
		anims.add("<spr name=\"/Unpromoted/Walk3\" x=\"0\" y=\"0\" z=\"0\"/>");
		anims.add("</cell>");
		anims.add("</anim>");
	}

	public static Image transformColorToTransparency(final BufferedImage im, final Color color)
	   {
	      final ImageFilter filter = new RGBImageFilter()
	      {
	         // the color we are looking for (white)... Alpha bits are set to opaque
	         public int markerRGB = color.getRGB(); // | 0xFFFFFFFF;

	         @Override
			public final int filterRGB(final int x, final int y, final int rgb)
	         {
	            if ((rgb | 0xFF000000) == markerRGB)
	            {
	               // Mark the alpha bits as zero - transparent
	               return 0x00FFFFFF & rgb;
	            }
	            else
	            {
	               // nothing to do
	               return rgb;
	            }
	         }
	      };

	      final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
	      return Toolkit.getDefaultToolkit().createImage(ip);
	   }
	class ImagePanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		private BufferedImage currentImage;

		public ImagePanel()
		{
			this.setPreferredSize(new Dimension(200, 300));
		}


		@Override
		protected void paintComponent(Graphics g) {
			if (initialized)
			{
				g.setColor(Color.white);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());

				int drawX = (this.getWidth() - currentImage.getWidth()) / 2;

				if (currentImage != null)
				{
					g.drawImage(currentImage, drawX + (int) xOffsetSpinner.getValue(),
							(this.getHeight() - currentImage.getHeight()) / 2 + (int) yOffsetSpinner.getValue(), this);
				}

				g.setColor(Color.red);
				g.drawRect(drawX + 18, this.getHeight() / 2, 40, 40);

				g.setColor(Color.blue);

				g.drawLine(drawX + 18, this.getHeight() / 2 + 48, drawX + 58, this.getHeight() / 2 + 48);
				// g.drawRect(this.getWidth() / 2 - 25, this.getHeight() / 2, 50, 50);
			}
		}



		public void setCurrentImage(BufferedImage currentImage) {
			this.currentImage = currentImage;
			this.repaint();
		}
	}

	private JButton createActionButton(String text, String command, JPanel container)
	{
		JButton b = new JButton(text);
		b.addActionListener(this);
		b.setActionCommand(command);
		b.setAlignmentX(Component.LEFT_ALIGNMENT);
		b.setMaximumSize(new Dimension(150, 20));
		container.add(b);
		return b;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if (cmd.equalsIgnoreCase("left"))
		{
			imagePanel.setCurrentImage(battleGifDecoder.getFrame(--currentIndex));
			if (currentIndex == 0)
				leftButton.setEnabled(false);
			rightButton.setEnabled(true);

			topLabel.setText(" Current Frame Animation: Unassigned");
			playButton.setEnabled(false);
			for (Map.Entry<String, Integer> s : battleActions.entrySet())
			{
				if (s.getValue() == currentIndex)
				{
					playButton.setEnabled(true);
					topLabel.setText(" Current Frame Animation: " + s.getKey());
					break;
				}
			}
		}
		else if (cmd.equalsIgnoreCase("right"))
		{
			imagePanel.setCurrentImage(battleGifDecoder.getFrame(++currentIndex));
			if (currentIndex + 1 == battleGifDecoder.getFrameCount())
				rightButton.setEnabled(false);
			leftButton.setEnabled(true);

			topLabel.setText(" Current Frame Animation: Unassigned");
			playButton.setEnabled(false);
			for (Map.Entry<String, Integer> s : battleActions.entrySet())
			{
				if (s.getValue() == currentIndex)
				{
					topLabel.setText(" Current Frame Animation: " + s.getKey());
					playButton.setEnabled(true);
					break;
				}
			}
		}
		else if (cmd.equalsIgnoreCase("play"))
		{
			Thread t = new Thread(new PlayThread());
			t.start();
		}
		else if (cmd.startsWith("Export"))
		{
			this.export(cmd.contains("Hero"));
		}
		else if (cmd.startsWith("Load"))
		{
			this.loadImages();
		}
		else
		{
			battleActions.put(cmd, currentIndex);
			topLabel.setText(" Current Frame Animation: " + cmd);
			playButton.setEnabled(true);
		}
	}

	class PlayThread implements Runnable
	{
		@Override
		public void run() {
			outer: while (true)
			{
				try {
					Thread.sleep(battleGifDecoder.getDelay(currentIndex));
				} catch (InterruptedException e) {}

				if (currentIndex + 1 == battleGifDecoder.getFrameCount())
					break;

				for (Map.Entry<String, Integer> s : battleActions.entrySet())
				{
					if (s.getValue() == currentIndex + 1)
					{
						break outer;
					}
				}

				actionPerformed(new ActionEvent(this, 0, "right"));
			}
		}

	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		this.repaint();
	}
}
