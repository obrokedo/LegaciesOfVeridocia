package lov.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
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
import javax.swing.JOptionPane;
import javax.swing.JSpinner;

public class AnimationExporter {
	private static void generateWeaponSpriteSheet(GifDecoder weaponGifDecoder, String imageName)
			throws IOException {
		Dimension fullImage = new Dimension(weaponGifDecoder.getFrameCount() * weaponGifDecoder.getFrameSize().width,
				(int) weaponGifDecoder.getFrameSize().getHeight());
		BufferedImage bim = new BufferedImage(
				fullImage.width, fullImage.height, BufferedImage.TYPE_INT_ARGB);

		ArrayList<String> spriteSheetContents = new ArrayList<String>();
		spriteSheetContents.add(
				"<img name=\"" + imageName.substring(imageName.lastIndexOf(File.separator) + 1).replace(".gif", ".png")
						+ "\" w=\"" + fullImage.width + "\" h=\"" + fullImage.height + "\">\n");
		spriteSheetContents.add("\t<definitions>\n");
		spriteSheetContents.add("\t\t<dir name=\"/\">\n");
		spriteSheetContents.add("\t\t\t<dir name=\"weapon\">\n");
		Graphics g = bim.createGraphics();
		for (int i = 0; i < weaponGifDecoder.getFrameCount(); i++) {
			spriteSheetContents
					.add("\t\t\t\t<spr name=\"Frame" + i + "\" x=\"" + i * weaponGifDecoder.getFrameSize().width
							+ "\" y=\"0\" w=\"" + weaponGifDecoder.getFrameSize().width + "\" h=\""
							+ weaponGifDecoder.getFrameSize().height + "\"/>\n");
			g.drawImage(weaponGifDecoder.getFrame(i), i * weaponGifDecoder.getFrameSize().width, 0, null);
		}
		
		spriteSheetContents.add("\t\t\t</dir>\n");
		spriteSheetContents.add("\t\t</dir>\n");
		spriteSheetContents.add("\t</definitions>\n");
		spriteSheetContents.add("</img>\n");

		g.dispose();

		Path path = Paths.get(imageName.replace(".gif", ".sprites").replaceAll("weapongifs", "weaponanim"));
		File outputfile = new File(imageName.replace(".gif", ".png").replaceAll("weapongifs", "weaponanim"));
		Files.write(path, spriteSheetContents, StandardCharsets.UTF_8);
		ImageIO.write(bim, "png", outputfile);
	}
	
	private static void generateWeaponAnimations(GifDecoder weaponGifDecoder, String imageName)
			throws IOException {
		ArrayList<String> animStrings = new ArrayList<String>();
		animStrings.add("<animations spriteSheet=\""
				+ imageName.substring(imageName.lastIndexOf(File.separator) + 1).replace(".gif", ".sprites")
				+ "\" ver=\"1.2\">");
		
		animStrings.add("<anim name=\"attack\" loops=\"1\">");
		for (int i = 0; i < weaponGifDecoder.getFrameCount(); i++) {
			animStrings.add("<cell index=\"0\" delay=\"" + weaponGifDecoder.getDelay(i) + "\">");
			animStrings.add("<spr name=\"/weapon/Frame" + i + "\" x=\"0\" y=\"0\" z=\"0\"/>");
			animStrings.add("</cell>");
		}
		animStrings.add("</anim>");
		

		animStrings.add("</animations>");

		Path animPath = Paths.get(imageName.replace(".gif", ".anim").replaceAll("weapongifs", "weaponanim"));
		Files.write(animPath, animStrings, StandardCharsets.UTF_8);
	}
	
	public static void exportWeapon(GifDecoder weaponGifDecoder, String imageName)
	{		 
		try {
			generateWeaponSpriteSheet(weaponGifDecoder, imageName);
			generateWeaponAnimations(weaponGifDecoder, imageName);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while trying to save the animation:"
					+ e.getMessage(), "Error saving animation", JOptionPane.ERROR_MESSAGE);
		}
	}
}
