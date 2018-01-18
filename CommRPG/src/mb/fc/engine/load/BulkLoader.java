package mb.fc.engine.load;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.util.Log;

import mb.fc.engine.CommRPG;
import mb.fc.engine.config.EngineConfigurator;
import mb.fc.game.exception.BadResourceException;
import mb.fc.loading.FCResourceManager;

public class BulkLoader {
	private List<String> allLines = null;
	private boolean hasStarted = false;
	private String errorMessage = null;
	private FCResourceManager resourceManager;
	
	public BulkLoader(FCResourceManager resourceManager) {
		super();
		this.resourceManager = resourceManager;
		this.allLines = new ArrayList<>();
	}

	public void start() {
		hasStarted = true;
	}
	
	public void start(String resourceFile) throws IOException {
		allLines = FCResourceManager.readAllLines(resourceFile);
		expandLoadingDirs();
		hasStarted = true;
	}
	
	public void update() {
		if (hasStarted && allLines != null && allLines.size() > 0) {
			loadResourceLine(CommRPG.engineConfiguratior);
		}
	}
	
	public void expandLoadingDirs() {
		for (int i = 0; i < allLines.size(); i++) {
			String resource = allLines.get(i);
			String[] split = resource.split(",");
			
			if (split[0].equalsIgnoreCase("musicdir"))
			{
				allLines.remove(i); i--;
				loadSoundOrMusic(split, "music");
			}
			else if (split[0].equalsIgnoreCase("sounddir"))
			{
				allLines.remove(i); i--;
				loadSoundOrMusic(split, "sound");
			}
			else if (split[0].equalsIgnoreCase("animsheetdir"))
			{
				allLines.remove(i); i--;
				File dir = new File(split[1]);
				for (File file : dir.listFiles())
				{
					if (file.getName().endsWith(".png"))
					{
						allLines.add("image," + file.getName().replace(".png", "") + "," + file.getPath());
					}
				}
				
				for (File file : dir.listFiles())
				{
					if (file.getName().endsWith(".anim")) {
						allLines.add("anim," + file.getName().replace(".anim", "") + "," + file.getPath());
					}
				}
			}
			else if (split[0].equalsIgnoreCase("spritedir") || split[0].equalsIgnoreCase("imagedir"))
			{
				allLines.remove(i); i--;
				File dir = new File(split[1]);
				for (File file : dir.listFiles())
				{
					if (file.getName().endsWith(".png"))
						allLines.add("image," + file.getName().replace(".png", "") + "," + file.getPath());
				}
			}
		}
	}

	private void loadSoundOrMusic(String[] split, String type) {
		File dir = new File(split[1]);
		for (File file : dir.listFiles())
		{
			if (file.getName().endsWith(".ogg"))
				allLines.add(type + "," + file.getName().replace(".ogg", "") + "," + file.getPath());
			else if (file.getName().endsWith(".wav"))
				allLines.add(type + "," + file.getName().replace(".wav", "") + "," + file.getPath());
		}
	}
	
	long lastTime = 0;
	
	private void loadResourceLine(EngineConfigurator configurator) {
		String line = allLines.remove(0);
		System.out.println("Last load time: " + (System.currentTimeMillis() - lastTime));
		lastTime = System.currentTimeMillis();
		System.out.println("Loading " + line);
		if (!line.startsWith("//"))
		{
			try
			{
				resourceManager.addResource(line, configurator);
			}
			catch (BadResourceException e)
			{
				Log.debug("Error loading resource: " + line);
				errorMessage = "Error loading resource: " + line + ": " + e.getMessage();
				e.printStackTrace();
				throw e;
			}
			catch (Throwable e)
			{
				Log.debug("Error loading resource: " + line);
				errorMessage = "Error loading resource: " + line;
				e.printStackTrace();
				throw new BadResourceException("Error loading resource: " + line + ".\n" + e.getMessage());
			}
		}
	}
	
	public boolean hasStarted() {
		return hasStarted;
	}
	
	public boolean isDone() {
		return hasStarted && allLines.size() == 0;
	}
	
	public void addLine(String line) {
		allLines.add(line);
	}
	
	public int getResourceAmount() {
		return allLines.size();
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
