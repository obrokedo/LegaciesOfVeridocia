package mb.gl2.loading;

import java.io.File;

public class Preload
{
	public static void main(String args[])
	{
		File f = new File("animations/animationsheets");
		for (File fs : f.listFiles())
		{
			if (fs.getName().endsWith(".png"))
				System.out.println("animsheet," + fs.getName().replaceFirst(".png", "") + ",animations/animationsheets/" + fs.getName());
		}

		f = new File("animations/fsa");
		for (File fs : f.listFiles())
		{
			if (fs.getName().endsWith(".fsa"))
				System.out.println("fsa," + fs.getName().replaceFirst(".fsa", "") + ",animations/fsa/" + fs.getName());
		}

		f = new File("sprite");
		for (File fs : f.listFiles())
		{
			if (fs.getName().endsWith(".png"))
				System.out.println("image," + fs.getName().replaceFirst(".png", "") + ",sprite/" + fs.getName());
		}

		f = new File("sound");
		for (File fs : f.listFiles())
		{
			if (fs.getName().endsWith(".ogg") || fs.getName().endsWith(".wav"))
				System.out.println("sound," + fs.getName().replaceFirst(".ogg", "").replaceFirst(".wav", "") + ",sound/" + fs.getName());
		}

		f = new File("music");
		for (File fs : f.listFiles())
		{
			if (fs.getName().endsWith(".ogg") || fs.getName().endsWith(".wav"))
				System.out.println("music," + fs.getName().replaceFirst(".ogg", "").replaceFirst(".wav", "") + ",music/" + fs.getName());
		}
	}
}
