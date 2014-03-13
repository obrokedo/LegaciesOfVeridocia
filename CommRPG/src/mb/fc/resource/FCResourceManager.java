package mb.fc.resource;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import mb.fc.game.text.Speech;
import mb.fc.game.trigger.TriggerEvent;
import mb.fc.map.Map;
import mb.fc.utils.SpriteAnims;
import mb.gl2.loading.LoadingState;
import mb.gl2.loading.ResourceManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.util.ResourceLoader;

import com.artemis.Entity;

public class FCResourceManager extends ResourceManager {
	private Hashtable<String, Image> images = new Hashtable<String, Image>();
	private Hashtable<String, SpriteSheet> spriteSheets = new Hashtable<String, SpriteSheet>();
	private Hashtable<String, SpriteAnims> spriteAnimations = new Hashtable<String, SpriteAnims>();
	private Hashtable<String, Music> musicByTitle = new Hashtable<String, Music>();
	private Hashtable<String, UnicodeFont> unicodeFonts = new Hashtable<String, UnicodeFont>();
	
	// These values need to be initialized each time a map is loaded
	private Hashtable<Integer, ArrayList<Speech>> speechesById = new Hashtable<Integer, ArrayList<Speech>>();
	private Hashtable<Integer, TriggerEvent> triggerEventById = new Hashtable<Integer, TriggerEvent>();
	private Map map = new Map();
	
	private Color transparent = new Color(255, 0, 255);

	public static void main(String args[])
	{
		File f = new File("C:\\Users\\Broked\\Pictures\\ShiningForce\\Sprites\\sf1-characters");
		for (File s : f.listFiles())
			System.out.println("ss," + s.getName().replaceAll(".png", "") + "," + s.getAbsolutePath() + ",24,24");
	}
	
	public void reinitialize()
	{
		map.reinitalize();
		speechesById.clear();
		triggerEventById.clear();
	}
	
	public void getNewMap(String mapName) throws IOException, SlickException
	{
		MapParser.parseMap("/map/" + mapName + ".tmx", getMap(), getClass());
	}
	
	public void getNewText(String text) throws IOException
	{
		TextParser.parseText("/text/" + text, speechesById, triggerEventById, getClass());
	}
	
	
	@Override
	public void addResource(String resource, Entity entity, int currentIndex,
			int maxIndex) throws IOException, SlickException {
		String[] split = resource.split(",");
		if (split[0].equalsIgnoreCase("image"))
			images.put(split[1], new Image((LoadingState.inJar ? "" : "bin/") + split[2]).getScaledCopy(split.length == 4 ? Float.parseFloat(split[3]) : 1));
		else if (split[0].equalsIgnoreCase("ss"))
		{
			float scale = split.length == 7 ? Float.parseFloat(split[6]) : 1;
			System.out.println("SCALE FOR " + split[1] + " " + scale);
			Image ssIm = new Image((LoadingState.inJar ? "" : "bin/") + split[2], transparent).getScaledCopy(scale);			
			spriteSheets.put(split[1], new SpriteSheet(ssIm, 
					(int) (Integer.parseInt(split[3]) * scale), (int) (Integer.parseInt(split[4])  * scale), 
					(int) ((split.length >= 6 ? Integer.parseInt(split[5]) : 0)  * scale)));
		}
		else if (split[0].equalsIgnoreCase("map"))
		{
			
			System.out.println("Load map: " + split[2]);
			MapParser.parseMap(split[2], map, getClass());
		}
		else if (split[0].equalsIgnoreCase("anim"))
		{
			SpriteAnims sa = SpriteAnims.deserializeFromFile(split[2]);
			sa.initialize(images.get(sa.getSpriteSheet()), 1.88f);	
			spriteAnimations.put(split[1], sa);	
		}
		else if (split[0].equalsIgnoreCase("text"))
		{
			TextParser.parseText(split[1], speechesById, triggerEventById, getClass());
		}
		else if (split[0].equalsIgnoreCase("music"))
		{
			musicByTitle.put(split[1], new Music(split[2]));
		}
		else if (split[0].equalsIgnoreCase("font"))
		{
			InputStream inputStream	= ResourceLoader.getResourceAsStream(split[2]);			 
			try 
			{
				Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
				UnicodeFont ufont = new UnicodeFont(awtFont, Integer.parseInt(split[3]), false, false);
				ufont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
				ufont.addAsciiGlyphs();
				ufont.addGlyphs(400, 600);
				ufont.loadGlyphs();
				unicodeFonts.put(split[1], ufont);								
			} catch (FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if (split[0].equalsIgnoreCase("herodir") || split[0].equalsIgnoreCase("enemydir") || split[0].equalsIgnoreCase("npcdir"))
		{
			File dir = new File((LoadingState.inJar ? "" : "bin/") + split[1]);
			for (File file : dir.listFiles())
			{
				if (file.getName().endsWith(".png"))
					spriteSheets.put(file.getName().replace(".png", ""), new SpriteSheet(file.getPath(), 
						Integer.parseInt(split[3]), Integer.parseInt(split[3]), transparent));
			}
		}
		else if (split[0].equalsIgnoreCase("musicdir"))
		{
			File dir = new File((LoadingState.inJar ? "" : "bin/") + split[1]);
			for (File file : dir.listFiles())
			{
				if (file.getName().endsWith(".ogg"))
					musicByTitle.put(file.getName().replace(".ogg", ""), new Music(file.getPath()));				
			}
		}
		else if (split[0].equalsIgnoreCase("animsheetdir"))
		{
			File dir = new File((LoadingState.inJar ? "" : "bin/") + split[1]);
			for (File file : dir.listFiles())
			{
				if (file.getName().endsWith(".png"))
					images.put(file.getName().replace(".png", ""), new Image(file.getPath()));				
			}
		}
		else if (split[0].equalsIgnoreCase("animfsadir"))
		{
			File dir = new File((LoadingState.inJar ? "" : "bin/") + split[1]);
			for (File file : dir.listFiles())
			{
				if (file.getName().endsWith(".fsa"))
				{
					SpriteAnims sa = SpriteAnims.deserializeFromFile(file.getPath());
					sa.initialize(images.get(sa.getSpriteSheet()), 1.88f);			
					spriteAnimations.put(file.getName().replace(".fsa", ""), sa);
				}
			}
		}
		LoadingComp lc = entity.getComponent(LoadingComp.class);
		lc.currentIndex = currentIndex;
		lc.maxIndex = maxIndex;
	}		

	@Override
	public void initializeLoadingComp(Entity entity) {
		
	}

	public Hashtable<String, Image> getImages() {
		return images;
	}

	public Hashtable<String, SpriteSheet> getSpriteSheets() {
		return spriteSheets;
	}

	public Hashtable<String, SpriteAnims> getSpriteAnimations() {
		return spriteAnimations;
	}
	
	public UnicodeFont getFontByName(String name)
	{
		return unicodeFonts.get(name);
	}


	public Map getMap()
	{
		return map;
	}
	
	public ArrayList<Speech> getSpeechesById(int id)
	{
		return speechesById.get(id);
	}
	
	public TriggerEvent getTriggerEventById(int id)
	{
		return triggerEventById.get(id);
	}
	
	public void playMusicByName(String name)
	{
		musicByTitle.get(name).loop(1, .0f);
	}
	
	public static ArrayList<String> readAllLines(String file, Class<?> cl) throws IOException
	{
		System.out.println("Read all lines " + file);
		BufferedReader br = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(file)));
		ArrayList<String> allLines = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null) {
            allLines.add(line);
        }				
		br.close();
		return allLines;
	}
}
