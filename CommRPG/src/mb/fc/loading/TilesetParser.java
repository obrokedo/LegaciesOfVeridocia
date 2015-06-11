package mb.fc.loading;

import java.util.Hashtable;

import mb.fc.engine.CommRPG;
import mb.fc.map.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

public class TilesetParser
{
	public void parseTileset(String image, Color trans, int tileWidth, int tileHeight,
			int startIndex, Map map, Hashtable<Integer, Integer> landEffectByTileId, float tileResize) throws SlickException
	{
		Image tileSheetImage = new Image(image, trans);
		Log.debug("LOAD TILESET " + tileSheetImage);
		tileSheetImage.setFilter(Image.FILTER_NEAREST);
		SpriteSheet ss = new SpriteSheet(tileSheetImage.getScaledCopy(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * tileResize), tileWidth, tileHeight);
		map.addTileset(ss, startIndex, tileWidth, tileHeight, landEffectByTileId);
	}
}
