package mb.fc.loading;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

import mb.fc.map.Map;
import mb.fc.utils.ImageUtility;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

public class PlannerTilesetParser extends TilesetParser {
	@Override
	public void parseTileset(String image, Color trans, int tileWidth,
			int tileHeight, int startIndex, Map map,
			Hashtable<Integer, Integer> landEffectByTileId)
			throws SlickException {
		PlannerMap pm = (PlannerMap) map;
		BufferedImage bim = ImageUtility.loadBufferedImage(image);
		ImageUtility.makeColorTransparent(bim, new java.awt.Color(trans.getRed(), trans.getGreen(), trans.getBlue()));
		BufferedImage[] bims = ImageUtility.splitImage(bim, bim.getWidth() / tileWidth, bim.getHeight() / tileHeight);
		pm.addTileset(bims, startIndex, tileWidth, tileHeight, landEffectByTileId);
	}
}
