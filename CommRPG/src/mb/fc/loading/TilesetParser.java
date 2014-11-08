package mb.fc.loading;

import java.util.Hashtable;

import mb.fc.engine.CommRPG;
import mb.fc.map.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class TilesetParser
{
	public void parseTileset(String image, Color trans, int tileWidth, int tileHeight,
			int startIndex, Map map, Hashtable<Integer, Integer> landEffectByTileId) throws SlickException
	{
		Image tileSheetImage = new Image(image, trans);
		System.out.println("LOAD TILESET " + tileSheetImage);
		tileSheetImage.setFilter(Image.FILTER_NEAREST);
		SpriteSheet ss = new SpriteSheet(tileSheetImage.getScaledCopy(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]), tileWidth, tileHeight);
		map.addTileset(ss, startIndex, tileWidth, tileHeight, landEffectByTileId);
	}
}