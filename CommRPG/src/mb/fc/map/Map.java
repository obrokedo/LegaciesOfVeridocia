package mb.fc.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import mb.fc.engine.CommRPG;
import mb.fc.game.sprite.CombatSprite;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

/**
 * @author Broked
 *
 * Holds values that describe a map created in Mappy, namely;
 * - Map dimensions
 * - Tileset
 * - Tile Size
 * - Polygon based map-objects that are created in "object" layers.
 * - Map layer data
 * In addition it provides methods for accessing this data in that allows the caller
 * to be unaware of multiple tilesets and tiles being handled differently then they are rendered
 */
public class Map
{
	/**
	 * Table that defines how each type of battle unit's movement is effected by each type of terrain.
	 * A value of 1000 means that the space is unmoveable in battle. A value of 10 is equivalent to 1 space
	 */
	private static final int[][] movementCostsByType = {
														//Sky      Path   Forest   Sand      Water
														//	  Even     Ovrgrth Mtn     High Mt.
														// Walking
														{1000, 10, 10, 15, 20, 15, 15, 1000, 1000},
														// Horses/Centaurs
														{1000, 10, 10, 15, 25, 25, 25, 1000, 1000},
														// Animals/Beastmen
														{1000, 10, 10, 10, 10, 10, 20, 1000, 1000},
														// Mechanical
														{1000, 10, 10, 10, 15, 15, 15, 1000, 1000},
														// Flying
														{10,   10, 10, 10, 10, 10, 10, 10, 10},
														// Hovering
														{10,   10, 10, 10, 10, 10, 10, 1000, 10},
														// Swimming
														{1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 10},
														// Elves
														{1000, 10, 10, 10, 10, 10, 20, 1000, 1000},
													};

	/**
	 * Describes the land effect of each terrain type for units that are not flying
	 */
													//Sky      Path   Forest   Sand      Water
													//	  Even     Ovrgrth Mtn     High Mt.
	private static final int[] terrainEffectByType = {0,  15,  0,  30, 30, 30, 0,  0,    0};

	/**
	 * A list of 2 dimensional int arrays, where each entry contains the tile indexs for each tile on that layer.
	 * A value of 0 in any given layer means that no tile was selected at this location.
	 */
	private ArrayList<int[][]> mapLayer = new ArrayList<int[][]>();
	protected int tileWidth, tileHeight;
	protected ArrayList<TileSet> tileSets = new ArrayList<TileSet>();
	private ArrayList<MapObject> mapObjects = new ArrayList<MapObject>();
	protected Hashtable<Integer, Integer> landEffectByTileId = new Hashtable<Integer, Integer>();
	private Hashtable<TerrainTypeIndicator, Integer> overriddenTerrain = new Hashtable<TerrainTypeIndicator, Integer>();
	private Hashtable<Integer, Roof> roofsById = new Hashtable<Integer, Roof>();
	private Shape battleRegion = null;

	private float tileRatio = 2f;
	public static final float DESIRED_TILE_WIDTH = 24f;

	public Map() {
		super();
	}

	public void reinitalize()
	{
		battleRegion = null;
		mapObjects.clear();
		mapLayer.clear();
		landEffectByTileId.clear();
		tileSets.clear();
		overriddenTerrain.clear();
		roofsById.clear();
	}

	public void addLayer(int[][] layer)
	{
		mapLayer.add(layer);
	}

	public int getMapWidth() {
		return mapLayer.get(0)[0].length;
	}

	public int getMapHeight() {
		return mapLayer.get(0).length;
	}

	public int getMapWidthInPixels() {
		return mapLayer.get(0)[0].length * tileWidth;
	}

	public int getMapHeightInPixels() {
		return mapLayer.get(0).length * tileHeight;
	}

	public int[][] getMapLayer(int layer) {
		return mapLayer.get(layer);
	}

	public int getMapEffectiveWidth()
	{
		return (int) (mapLayer.get(0)[0].length / tileRatio);
	}

	public int getMapEffectiveHeight()
	{
		return (int) (mapLayer.get(0).length / tileRatio);
	}

	public int getTileEffectiveWidth()
	{
		return (int) (tileWidth * tileRatio);
	}

	public int getTileEffectiveHeight()
	{
		return (int) (tileHeight * tileRatio);
	}

	public int getTileRenderWidth() {
		return tileWidth;
	}

	public int getTileRenderHeight() {
		return tileHeight;
	}

	public ArrayList<MapObject> getMapObjects() {
		return mapObjects;
	}

	public void addMapObject(MapObject mo) {
		if (mo.getKey().equalsIgnoreCase("terrain"))
		{
			for (int x = mo.getX(); x < mo.getX() + mo.getWidth(); x += getTileEffectiveWidth())
			{
				for (int y = mo.getY(); y < mo.getY() + mo.getHeight(); y += getTileEffectiveHeight())
				{
					if (mo.getShape().contains(x + 1, y + 1))
					{
						overriddenTerrain.put(new TerrainTypeIndicator(x / getTileEffectiveWidth(), y / getTileEffectiveHeight()), Integer.parseInt(mo.getParam("type")));
					}
				}
			}
		}
		else if (mo.getKey().equalsIgnoreCase("battleregion"))
		{
			battleRegion = mo.getShape();
		}
		else if (mo.getKey().equalsIgnoreCase("roof"))
		{
			int roofWidth = mo.getWidth() / getTileRenderWidth();
			int roofHeight = mo.getHeight() / getTileRenderHeight();

			int startX = mo.getX() / getTileRenderWidth();
			int startY = mo.getY() / getTileRenderHeight();

			roofsById.put(Integer.parseInt(mo.getParam("roofid")), new Roof(new Rectangle(startX, startY, roofWidth, roofHeight)));
		}
		else
			this.mapObjects.add(mo);


	}

	public void addTileset(SpriteSheet spriteSheet, int tileStartIndex, int tileWidth, int tileHeight, Hashtable<Integer, Integer> landEffectByTileId)
	{
		this.landEffectByTileId.putAll(landEffectByTileId);
		this.tileSets.add(new TileSet(spriteSheet, tileStartIndex));
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;

		Collections.sort(tileSets, new TileSetComparator());
	}

	public Image getSprite(int index)
	{
		for (TileSet ts : tileSets)
			if (index >= ts.getStartIndex())
				return ts.getSprite(index);
		return null;
	}

	public int getMovementCostByType(int moverType, int tileX, int tileY)
	{
		int tile;

		TerrainTypeIndicator tti = new TerrainTypeIndicator(tileX, tileY);
		if (overriddenTerrain.containsKey(tti))
		{
			tile = overriddenTerrain.get(tti);
			return movementCostsByType[moverType][tile];
		}
		else
		{
			if (mapLayer.get(1)[tileY][tileX] != 0)
				tile = mapLayer.get(1)[tileY][tileX];
			else
				tile = mapLayer.get(0)[tileY][tileX];

			// Subtract one to account for the blank space at 0
			tile--;

			if (landEffectByTileId.containsKey(tile))
			{
				return movementCostsByType[moverType][landEffectByTileId.get(tile)];
			}
			else
				return 10000;
		}
	}

	public int getLandEffectByTile(int moverType, int tileX, int tileY)
	{
		if (moverType == CombatSprite.MOVEMENT_FLYING)
			return 0;

		int tile;

		TerrainTypeIndicator tti = new TerrainTypeIndicator(tileX, tileY);
		if (overriddenTerrain.containsKey(tti))
		{
			tile = overriddenTerrain.get(tti);
			return terrainEffectByType[tile];
		}
		else
		{
			if (mapLayer.get(1)[tileY][tileX] != 0)
				tile = mapLayer.get(1)[tileY][tileX];
			else
				tile = mapLayer.get(0)[tileY][tileX];

			// Subtract one to account for the blank space at 0
			tile--;

			if (landEffectByTileId.containsKey(tile))
			{
				return terrainEffectByType[landEffectByTileId.get(tile)];
			}
			else
			{
				return 0;
			}
		}
	}

	public boolean isInBattleRegion(int mapX, int mapY)
	{
		if (battleRegion == null)
			return true;

		return battleRegion.contains(mapX + 1, mapY + 1);

	}

	public boolean isMarkedMoveable(int tileX, int tileY)
	{
		if ((mapLayer.get(3).length > (tileY * tileRatio)) && ((mapLayer.get(3)[0].length > tileX * tileRatio)))
			return mapLayer.get(3)[(int) (tileY * tileRatio)][(int) (tileX * tileRatio)] != 0;
		return false;
	}

	protected class TileSet
	{
		private SpriteSheet spriteSheet;
		protected int startIndex;
		private int ssWidth;

		public TileSet(SpriteSheet spriteSheet, int startIndex) {
			super();
			this.spriteSheet = spriteSheet;
			this.startIndex = startIndex;
			if (spriteSheet != null)
				this.ssWidth = spriteSheet.getHorizontalCount();
		}

		public Image getSprite(int index) {
			return spriteSheet.getSprite((index - startIndex) % ssWidth, (index - startIndex) / ssWidth);
		}

		public int getStartIndex() {
			return startIndex;
		}
	}

	public class TileSetComparator implements Comparator<TileSet>
	{
		@Override
		public int compare(TileSet ts0, TileSet ts1)
		{
			return ts1.startIndex - ts0.startIndex;
		}
	}

	private class TerrainTypeIndicator
	{
		private int tileX;
		private int tileY;

		public TerrainTypeIndicator(int tileX, int tileY) {
			super();
			this.tileX = tileX;
			this.tileY = tileY;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + tileX;
			result = prime * result + tileY;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TerrainTypeIndicator other = (TerrainTypeIndicator) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (tileX != other.tileX)
				return false;
			if (tileY != other.tileY)
				return false;
			return true;
		}
		private Map getOuterType() {
			return Map.this;
		}
	}

	public Iterable<Roof> getRoofIterator()
	{
		return roofsById.values();
	}

	public Roof getRoofById(int id)
	{
		return roofsById.get(id);
	}

	public int getTileScale()
	{
		return CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];
	}

	public void setOriginalTileWidth(int origTileWidth)
	{
		tileRatio = DESIRED_TILE_WIDTH / origTileWidth;
		System.out.println("TILE RATIO " + tileRatio);
	}
}
