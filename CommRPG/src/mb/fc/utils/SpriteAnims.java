package mb.fc.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.ResourceLoader;

public class SpriteAnims implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Hashtable<String, Animation> animations;
	private String spriteSheet;
	public ArrayList<Rectangle> imageLocs;
	public transient ArrayList<Image> images;
	
	public SpriteAnims(String spriteSheet, ArrayList<Rectangle> imageLocs)
	{
		animations = new Hashtable<String, Animation>();
		this.imageLocs = imageLocs;
		this.spriteSheet = spriteSheet;
	}
	
	public void addAnimation(String name, Animation anim)
	{
		animations.put(name, anim);
	}
	
	public void initialize(Image image, float magnify)
	{
		images = new ArrayList<Image>();
		
		for (int i = 0; i < imageLocs.size(); i++)
		{
			Rectangle r = imageLocs.get(i);
			Image subImage = image.getSubImage((int) r.getX(), (int) r.getY(), (int) r.getWidth(), 
					(int) r.getHeight());
			subImage.setFilter(Image.FILTER_NEAREST);
			images.add(subImage.getScaledCopy(magnify));
		}
	}
	
	private HashSet<Integer> getUnmagnifiedIndexes()
	{
		HashSet<Integer> unmagnified = new HashSet<Integer>();
		addUnmagnifiedForAnimation(animations.get("UnLeft"), unmagnified);
		addUnmagnifiedForAnimation(animations.get("UnRight"), unmagnified);
		addUnmagnifiedForAnimation(animations.get("UnDown"), unmagnified);
		addUnmagnifiedForAnimation(animations.get("UnUp"), unmagnified);
		return unmagnified;
	}
	
	private void addUnmagnifiedForAnimation(Animation a, HashSet<Integer> unmagnified)
	{
		if (a == null)
			return;
		
		for (AnimFrame af : a.frames)
		{
			for (AnimSprite as : af.sprites)
			{
				unmagnified.add(as.imageIndex);
			}
		}
	}
	
	public Animation getAnimation(String name)
	{
		return animations.get(name);
	}
	
	public Image getImageAtIndex(int idx)
	{
		return images.get(idx);
	}
	
	public String getSpriteSheet() {
		return spriteSheet;
	}
	
	public void printAnimations()
	{
		System.out.println("-- Print Animations --");
		for (String a : animations.keySet())
			System.out.println(a);
	}
	
	public Set<String> getAnimationKeys()
	{
		return animations.keySet();
	}

	public void serializeToFile(String fileName)
	{
		try 
		{
			OutputStream file = new FileOutputStream(fileName);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(this);
			output.flush();
			file.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static SpriteAnims deserializeFromFile(String fileName)
	{
	    try
	    {
	    	InputStream file = ResourceLoader.getResourceAsStream(fileName);
	    	InputStream buffer = new BufferedInputStream(file);
	    	ObjectInput input = new ObjectInputStream (buffer);
	    	SpriteAnims sa = (SpriteAnims) input.readObject();
	    	file.close();
	    	return sa;
	    }
	    catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	    return null;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
