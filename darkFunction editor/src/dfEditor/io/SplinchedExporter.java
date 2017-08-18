package dfEditor.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JTree;

import dfEditor.animation.Animation;

public class SplinchedExporter {
	public static void exportSplinchedAnim(File aSaveFile, String aImageName, JTree aTree,
			ArrayList<Animation> aAnimList, BufferedImage hitboxImage)
	{

		/*
		ArrayList<String> spriteName = new ArrayList<String>();
		ArrayList<Rectangle> spriteRect = new ArrayList<Rectangle>();

		defineSprites(spriteName, spriteRect, (CustomNode)aTree.getModel().getRoot(), null);

		Animations sa = new Animations(aImageName, (int) spriteRect.get(0).getWidth(), (int) spriteRect.get(0).getHeight());

		for (Animation aAnimation : aAnimList) {
			mb.splinched.anim.Animation newAnim = new mb.splinched.anim.Animation();

			aAnimation.setCurrentCellIndex(0);
			AnimationCell cell = aAnimation.getCurrentCell();
			System.out.println("Animation -> " + aAnimation.getName());
			while (cell != null) {
				mb.splinched.anim.AnimationFrame newFrame = new mb.splinched.anim.AnimationFrame(cell.getDelay());

				ArrayList<GraphicObject> graphicList = cell.getGraphicList();
				for (int i = 0; i < graphicList.size(); ++i)
				{
					SpriteGraphic graphic = (SpriteGraphic) graphicList.get(i);
					CustomNode node = cell.nodeForGraphic(graphic);
					int imageIndex = -1;
					if (!((String) node.getUserObject()).equalsIgnoreCase("Weapon"))
						imageIndex = spriteName.indexOf(node.getFullPathName());
					newFrame.addSpriteIndexs(imageIndex);
					// af.sprites.add(new AnimSprite(graphic.getRect().x, graphic.getRect().y,
						//	imageIndex, (int) graphic.getAngle()));
				}

				cell = aAnimation.getNextCell();
				newAnim.frames.add(newFrame);
			}

			sa.addAnimation(aAnimation.getName(), newAnim);
		}

		try
		{
			sa.setHitboxes(getHitboxes(hitboxImage, (int) spriteRect.get(0).getWidth(), (int) spriteRect.get(0).getHeight()));
		}
		catch (Exception ex)
		{
			return;
		}
		sa.serializeToFile(aSaveFile.getAbsolutePath());
		*/
	}
/*
	private static void defineSprites(ArrayList<String> spriteName, ArrayList<Rectangle> spriteRect, CustomNode node, String directory)
	{
		
		if (node.isLeaf())
        {
			if (((String) node.getUserObject()).equalsIgnoreCase("Weapon"))
				return;
            java.awt.Rectangle r = ((GraphicObject)node.getCustomObject()).getRect();

            if (directory != null && !directory.equalsIgnoreCase("/"))
            	spriteName.add("/" + directory + "/" + node.getUserObject());
            else
            	spriteName.add("/" + node.getUserObject());
            spriteRect.add(new Rectangle(r.x,r. y, r.width, r.height));
        }
        else
        {
            for (int i=0; i<node.getChildCount(); ++i)
            {
            	defineSprites(spriteName, spriteRect, (CustomNode)node.getChildAt(i), (String) node.getUserObject());
            }
        }
        
	}

	private static ArrayList<Hitbox> getHitboxes(BufferedImage image, int chunkWidth, int chunkHeight) throws Exception
	{
	 	int rows = (image.getWidth() / chunkWidth); //You should decide the values for rows and cols variables
        int cols = (image.getHeight() / chunkHeight);
        int chunks = rows * cols;
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

                // draws the image chunk
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        final int black = Color.BLACK.getRGB();
        final int green = Color.GREEN.getRGB();
        final int blue = Color.BLUE.getRGB();
        final int red = Color.RED.getRGB();
        System.out.println("BLACK " + black);
        System.out.println("GREEN " + green);
        System.out.println("BLUE " + blue);
        System.out.println("RED " + red);

        ArrayList<Point> hit = new ArrayList<>();
        ArrayList<Point> damage = new ArrayList<>();
        Point groundPoint = null;
        ArrayList<Hitbox> hitboxes = new ArrayList<>();

        int frame = 0;

        for (BufferedImage b : imgs)
        {
        	damage = new ArrayList<>();
        	hit = new ArrayList<>();
        	Hitbox hitbox = new Hitbox();
        	for (int y = 0; y < b.getHeight(); y++)
        	{
	        	for (int x = 0; x < b.getWidth(); x++)
	        	{
	        		int rgb = b.getRGB(x, y);
	        		if (rgb == black)
	        		{
	        			hitbox.setCenter(new Point(x, y));
	        		}
	        		else if (rgb == blue)
	        		{
	        			groundPoint = new Point(x, y);
	        		}
	        		else if (rgb == red)
	        		{
	        			hit.add(new Point(x, y));
	        		}
	        		else if (rgb == green)
	        		{
	        			damage.add(new Point(x, y));
	        		}
	        	}
        	}

        	if (damage.size() == 4)
        	{
        		hitbox.setDamageBox(new Rectangle(damage.get(0).getX(), damage.get(0).getY(),
        				damage.get(3).getX() - damage.get(0).getX(), damage.get(3).getY() - damage.get(0).getY()));
        	}
        	else
        		System.out.println("No damage box found");

        	if (hit.size() == 4)
        	{
        		hitbox.setHitBox(new Rectangle(hit.get(0).getX(), hit.get(0).getY(),
        				hit.get(3).getX() - hit.get(0).getX(), hit.get(3).getY() - hit.get(0).getY()));
        	}
        	else
        	{
        		System.out.println("No hit box found");
        		int rc = JOptionPane.showConfirmDialog(null,
        				"A animation was found that does not have a hit box.\n Would you like to continue?");
        		if (rc != JOptionPane.OK_OPTION)
        			throw new Exception();
        	}

        	if (groundPoint != null)
        		hitbox.setGroundPoints(groundPoint);

        	hitboxes.add(hitbox);
        	frame++;
        }
        return hitboxes;
	}
	*/
}
