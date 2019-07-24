package lov.tools;

import java.io.File;

public class AnimatedWeaponExporter {
	public static void main(String args[]) {
		File f = new File("animations/weapongifs");
		for (File fs : f.listFiles())
			if (fs.getName().endsWith(".gif"))
			{
				GifDecoder gd = new GifDecoder();
				gd.read(fs.getPath());
				AnimationExporter.exportWeapon(gd, fs.getPath());
			}
	}
}
