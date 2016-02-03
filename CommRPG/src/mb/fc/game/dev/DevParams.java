package mb.fc.game.dev;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mb.fc.game.persist.ClientProfile;

import org.newdawn.slick.util.Log;

public class DevParams {
	private static final String DEV_PARAMS_FILE = "DevParams";

	private ArrayList<Integer> heroesToAdd = new ArrayList<>();
	private int level = 1;

	private DevParams(ArrayList<Integer> heroesToAdd, int level) {
		super();
		this.heroesToAdd = heroesToAdd;
		this.level = level;
	}

	public static DevParams parseDevParams()
	{
		ArrayList<Integer> heroesToAdd = new ArrayList<>();
		int level = 1;

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(DEV_PARAMS_FILE))));
			String line;
			while ((line = br.readLine()) != null) {
	            if (line.startsWith("HERO "))
	            	heroesToAdd.add(Integer.parseInt(line.split(" ")[1]));
	            else if (line.startsWith("LEVEL "))
	            	level = Integer.parseInt(line.split(" ")[1]);
	        }

			return new DevParams(heroesToAdd, level);
		}
		catch (Exception ex) {
			Log.debug("An error occurred while trying to load the dev params from: " + DEV_PARAMS_FILE);
		}
		finally
		{
			if (br != null)
				try { br.close(); } catch (IOException e) {}
		}

		return null;
	}

	public void applyToClientProfile(ClientProfile clientProfile)
	{
		Log.debug("DevParams setting hero level to: " + level);
		for (Integer i : heroesToAdd)
			Log.debug("DevParams adding hero with id: " + i + " to the party");

		clientProfile.setDevelParams(heroesToAdd, level);
	}
}
