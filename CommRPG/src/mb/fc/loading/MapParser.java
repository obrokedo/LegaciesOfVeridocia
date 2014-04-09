package mb.fc.loading;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import mb.fc.engine.CommRPG;
import mb.fc.map.Map;
import mb.fc.map.MapObject;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class MapParser 
{
	public static void parseMap(String mapFile, Map map) throws IOException, SlickException
	{
		ArrayList<TagArea> tagAreas = XMLParser.process(mapFile);
		
		TagArea tagArea = tagAreas.get(0);
		
		int width = Integer.parseInt(tagArea.getParams().get("width"));
		int height = Integer.parseInt(tagArea.getParams().get("height"));
		int tileWidth = Integer.parseInt(tagArea.getParams().get("tilewidth")) * CommRPG.GLOBAL_WORLD_SCALE;
		int tileHeight = Integer.parseInt(tagArea.getParams().get("tileheight")) * CommRPG.GLOBAL_WORLD_SCALE;
		String tileSet = null;
		
		for (TagArea childArea : tagArea.getChildren())
		{
			if (childArea.getTagType().equalsIgnoreCase("tileset"))
			{
				tileSet = childArea.getChildren().get(0).getParams().get("source");
				String trans = childArea.getChildren().get(0).getParams().get("trans");
				int startIndex = Integer.parseInt(childArea.getParams().get("firstgid"));
				String[] tsSplit = tileSet.split(",")[0].split("/");
				
				Image tileSheetImage = new Image("image/" + tsSplit[tsSplit.length - 1], new Color(	Integer.parseInt(trans.substring(0, 2), 16), 
						Integer.parseInt(trans.substring(2, 4), 16), 
						Integer.parseInt(trans.substring(4, 6), 16)));
				tileSheetImage.setFilter(Image.FILTER_NEAREST);
				SpriteSheet ss = new SpriteSheet(tileSheetImage.getScaledCopy(2), tileWidth, tileHeight);
				Hashtable<Integer, Integer> landEffectByTileId = new Hashtable<Integer, Integer>();
				
				for (TagArea tile : childArea.getChildren())
				{
					if (tile.getTagType().equalsIgnoreCase("tile"))
					{
						int id = Integer.parseInt(tile.getParams().get("id"));
						int landEffect = Integer.parseInt(tile.getChildren().get(0).getChildren().get(0).getParams().get("value"));
						landEffectByTileId.put(id, landEffect);
					}
				}
				
				map.addTileset(ss, startIndex, tileWidth, tileHeight, landEffectByTileId);
			}
			else if (childArea.getTagType().equalsIgnoreCase("layer"))
			{
				int[][] layer = new int[height][width];
				int index = 0;
				for (TagArea tileTag : childArea.getChildren().get(0).getChildren())
				{
					layer[index / width][index % width] = Integer.parseInt(tileTag.getParams().get("gid"));
					index++;					
				}
				
				map.addLayer(layer);
			}
			else if (childArea.getTagType().equalsIgnoreCase("objectgroup"))
			{				
				for (TagArea objectTag : childArea.getChildren())
				{
					MapObject mapObject = new MapObject();
					mapObject.setName(objectTag.getParams().get("name"));
					mapObject.setX(CommRPG.GLOBAL_WORLD_SCALE * Integer.parseInt(objectTag.getParams().get("x")));
					mapObject.setY(CommRPG.GLOBAL_WORLD_SCALE * Integer.parseInt(objectTag.getParams().get("y")));
					if (objectTag.getParams().containsKey("width"))
						mapObject.setWidth(CommRPG.GLOBAL_WORLD_SCALE * Integer.parseInt(objectTag.getParams().get("width")));
					if (objectTag.getParams().containsKey("height"))
						mapObject.setHeight(CommRPG.GLOBAL_WORLD_SCALE * Integer.parseInt(objectTag.getParams().get("height")));
					for (TagArea propArea : objectTag.getChildren())
					{
						if (propArea.getTagType().equalsIgnoreCase("properties"))
						{
							mapObject.setKey(propArea.getChildren().get(0).getParams().get("name"));
							mapObject.setValue(propArea.getChildren().get(0).getParams().get("value"));
						}
						else if (propArea.getTagType().equalsIgnoreCase("polyline"))
						{
							String[] points = propArea.getParams().get("points").split(" ");
							ArrayList<Point> pointList = new ArrayList<Point>();
							for (String point : points)
							{
								String[] p = point.split(",");
								pointList.add(new Point(CommRPG.GLOBAL_WORLD_SCALE * Integer.parseInt(p[0]), CommRPG.GLOBAL_WORLD_SCALE * Integer.parseInt(p[1])));								
							}
							
							mapObject.setPolyPoints(pointList);
						}
					}
					mapObject.determineShape();
					map.addMapObject(mapObject);
				}
				
			}
		}				
	}
}
