package mb.fc.loading;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import mb.fc.map.Map;
import mb.fc.map.MapObject;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

public class MapParser
{
	public static void parseMap(String mapFile, Map map, TilesetParser tilesetParser,
			FCResourceManager frm) throws IOException, SlickException
	{
		HashSet<String> spriteToLoad = new HashSet<String>();
		new HashSet<String>();
		ArrayList<TagArea> tagAreas = XMLParser.process(mapFile);

		TagArea tagArea = tagAreas.get(0);

		int width = Integer.parseInt(tagArea.getAttribute("width"));
		int height = Integer.parseInt(tagArea.getAttribute("height"));
		//int tileWidth = Integer.parseInt(tagArea.getAttribute("tilewidth")) * map.getTileScale();
		//int tileHeight = Integer.parseInt(tagArea.getAttribute("tileheight")) * map.getTileScale();
		int tileWidth = Integer.parseInt(tagArea.getAttribute("tilewidth"));
		int tileHeight = Integer.parseInt(tagArea.getAttribute("tileheight"));


		float desiredTileSize = Map.DESIRED_TILE_WIDTH;
		float tileResize = 1;
		if (tileWidth > desiredTileSize)
		{
			tileResize = desiredTileSize / tileWidth;
			tileWidth *= tileResize;
			tileHeight *= tileResize;
			map.setOriginalTileWidth((int) desiredTileSize);
		}
		else
			map.setOriginalTileWidth(tileWidth);

		tileWidth *= map.getTileScale();
		tileHeight *= map.getTileScale();


		System.out.println("TILE " + tileWidth  + " " + tileHeight);

		String tileSet = null;

		for (TagArea childArea : tagArea.getChildren())
		{
			if (childArea.getTagType().equalsIgnoreCase("tileset"))
			{
				tileSet = childArea.getChildren().get(0).getAttribute("source");
				String trans = childArea.getChildren().get(0).getAttribute("trans");
				int startIndex = Integer.parseInt(childArea.getAttribute("firstgid"));
				String[] tsSplit = tileSet.split(",")[0].split("/");

				Hashtable<Integer, Integer> landEffectByTileId = new Hashtable<Integer, Integer>();

				for (TagArea tile : childArea.getChildren())
				{
					if (tile.getTagType().equalsIgnoreCase("tile"))
					{
						int id = Integer.parseInt(tile.getAttribute("id"));
						int landEffect = Integer.parseInt(tile.getChildren().get(0).getChildren().get(0).getAttribute("value"));
						landEffectByTileId.put(id, landEffect);
					}
				}

				tilesetParser.parseTileset("image/" + tsSplit[tsSplit.length - 1], new Color(	Integer.parseInt(trans.substring(0, 2), 16),
						Integer.parseInt(trans.substring(2, 4), 16),
						Integer.parseInt(trans.substring(4, 6), 16)),
						tileWidth, tileHeight, startIndex, map, landEffectByTileId, tileResize);
			}
			else if (childArea.getTagType().equalsIgnoreCase("layer"))
			{
				int[][] layer = new int[height][width];
				int index = 0;
				for (TagArea tileTag : childArea.getChildren().get(0).getChildren())
				{
					layer[index / width][index % width] = Integer.parseInt(tileTag.getAttribute("gid"));
					index++;
				}

				map.addLayer(layer);
			}
			else if (childArea.getTagType().equalsIgnoreCase("objectgroup"))
			{
				for (TagArea objectTag : childArea.getChildren())
				{
					MapObject mapObject = new MapObject();
					mapObject.setName(objectTag.getAttribute("name"));
					mapObject.setX((int) (map.getTileScale() * Integer.parseInt(objectTag.getAttribute("x")) * tileResize));
					mapObject.setY((int) (map.getTileScale() * Integer.parseInt(objectTag.getAttribute("y")) * tileResize));
					if (objectTag.getAttribute("width") != null)
						mapObject.setWidth((int) (map.getTileScale() *
								Integer.parseInt(objectTag.getAttribute("width")) * tileResize));
					if (objectTag.getAttribute("height") != null)
						mapObject.setHeight((int) (map.getTileScale() *
								Integer.parseInt(objectTag.getAttribute("height"))  * tileResize));
					for (TagArea propArea : objectTag.getChildren())
					{
						if (propArea.getTagType().equalsIgnoreCase("properties"))
						{
							mapObject.setKey(propArea.getChildren().get(0).getAttribute("name"));
							mapObject.setValue(propArea.getChildren().get(0).getAttribute("value"));

							if (mapObject.getKey().equalsIgnoreCase("sprite"))
							{
								String image = mapObject.getParam("image");
								if (image != null)
									spriteToLoad.add(image);
							}
							else if (mapObject.getKey().equalsIgnoreCase("enemy"))
							{
								Integer.parseInt(mapObject.getParam("enemyid"));
							}
						}
						else if (propArea.getTagType().equalsIgnoreCase("polyline"))
						{
							String[] points = propArea.getAttribute("points").split(" ");
							ArrayList<Point> pointList = new ArrayList<Point>();
							for (String point : points)
							{
								String[] p = point.split(",");
								pointList.add(new Point((int) (map.getTileScale() * Integer.parseInt(p[0])  * tileResize),
										(int) (map.getTileScale() * Integer.parseInt(p[1])  * tileResize)));
							}

							mapObject.setPolyPoints(pointList);
						}
					}
					mapObject.determineShape();
					map.addMapObject(mapObject);
				}

			}
		}

		/*
		for (String resource : spriteToLoad)
			frm.addSpriteResource(resource);
		for (String resource : animToLoad)
			frm.addAnimResource(resource);*/
	}
}
