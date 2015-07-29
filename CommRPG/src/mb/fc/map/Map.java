package mb.fc.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import mb.fc.engine.CommRPG;
import mb.jython.GlobalPythonFactory;
import mb.jython.JConfigurationValues;

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
	private static final int[][] movementCostsByType2 = {
														//Water   		Road   			Forest     		Sand    	H Sky
														//	  	Grass  			Th Gras   	 	Hills  			L Sky	Impassable
														// Normal
														{1000, 	10, 	10, 	15, 	20, 	15, 	20, 	1000, 1000, 1000},
														// Slow
														{1000, 	10, 	10, 	15, 	25, 	25, 	25, 	1000, 1000, 1000},
														// Fast
														{1000, 	10, 	10, 	10, 	15, 	10, 	20, 	1000, 1000, 1000},
														// Beast
														{1000, 	10, 	10, 	10, 	10, 	15, 	15, 	1000, 1000, 1000},
														// Tank
														{1000, 	10, 	10, 	10, 	15, 	10, 	15, 	1000, 1000, 1000},


														// Hovering
														{10,   10, 		10, 	10, 	10, 	10, 	10, 	10,   1000, 1000},
														// Flight
														{10,	10, 	10, 	10, 	10, 	10, 	10, 	10,	  10,	1000},
														// Free
														{10, 	10, 	10, 	10, 	10, 	10, 	10, 	10,   10,	10},
													};

	/**
	 * Describes the land effect of each terrain type for units that are not flying
	 */
													//Water   		Road   			Forest     		Sand    	H Sky
													//	  	Grass  			Th Gras   	 	Hills  			L Sky	Impassable
	private static final int[] terrainEffectByType2 = {0,  	10,  	0,  	20, 	40, 	30, 	0,  	0,    0, 0};



	private Hashtable<String, Integer> terrainEffectByType = new Hashtable<String, Integer>();
	private Hashtable<String, MovementCost> movementCostsByType = new Hashtable<String, Map.MovementCost>();

	/**
	 * A list of 2 dimensional int arrays, where each entry contains the tile indexs for each tile on that layer.
	 * A value of 0 in any given layer means that no tile was selected at this location.
	 */
	private ArrayList<int[][]> mapLayer = new ArrayList<int[][]>();
	protected int tileWidth, tileHeight;
	protected ArrayList<TileSet> tileSets = new ArrayList<TileSet>();
	private ArrayList<MapObject> mapObjects = new ArrayList<MapObject>();
	protected Hashtable<Integer, Integer> landEffectByTileId = new Hashtable<Integer, Integer>();
	private Hashtable<TerrainTypeIndicator, String> overriddenTerrain = new Hashtable<TerrainTypeIndicator, String>();
	private Hashtable<Integer, Roof> roofsById = new Hashtable<Integer, Roof>();
	private Shape battleRegion = null;
	private int roofCount = -1;
	private int backgroundImageIndex;
	private JConfigurationValues jConfigValues;

	private float tileRatio = 2f;
	public static final float DESIRED_TILE_WIDTH = 24f;

	public Map() {
		super();
		jConfigValues = GlobalPythonFactory.createConfigurationValues();
		for (String movementType : jConfigValues.getMovementTypes())
			movementCostsByType.put(movementType, new MovementCost(movementType, jConfigValues));
		for (String terrainType : jConfigValues.getTerrainTypes())
			terrainEffectByType.put(terrainType, jConfigValues.getTerrainEffectAmount(terrainType));
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
		roofCount = -1;
		backgroundImageIndex = 0;
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
						overriddenTerrain.put(new TerrainTypeIndicator(x / getTileEffectiveWidth(), y / getTileEffectiveHeight()), mo.getParam("type"));
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

			int roofId = 0;

			if (mo.getParam("roofid") != null)
			{
				roofId = Integer.parseInt(mo.getParam("roofid"));
			}
			else
				roofId = roofCount--;

			roofsById.put(roofId, new Roof(new Rectangle(startX, startY, roofWidth, roofHeight)));

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

	public int getMovementCostByType(String moverType, int tileX, int tileY)
	{
		String tile;

		TerrainTypeIndicator tti = new TerrainTypeIndicator(tileX, tileY);
		if (overriddenTerrain.containsKey(tti))
		{
			tile = overriddenTerrain.get(tti);
			return movementCostsByType.get(moverType).getMovementCost(tile);
		}
		else
		{
			/*
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
			*/
			return 10000;
		}
	}

	public int getLandEffectByTile(String moverType, int tileX, int tileY)
	{
		// Check to see if given the movement type is effected by land effect
		// if not then just return 0
		if (!jConfigValues.isAffectedByTerrain(moverType))
			return 0;

		String tileTerrainType;

		TerrainTypeIndicator tti = new TerrainTypeIndicator(tileX, tileY);
		if (overriddenTerrain.containsKey(tti))
		{
			tileTerrainType = overriddenTerrain.get(tti);
			return terrainEffectByType.get(tileTerrainType);
		}
		else
		{
			/*
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
			*/
			return 0;
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
		if ((mapLayer.get(6).length > (tileY * tileRatio)) && ((mapLayer.get(6)[0].length > tileX * tileRatio)))
			return mapLayer.get(6)[(int) (tileY * tileRatio)][(int) (tileX * tileRatio)] != 0;
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

	public void checkRoofs(int mapX, int mapY)
	{
		mapX = mapX / this.getTileRenderWidth();
		mapY = mapY / this.getTileRenderHeight();
		for (Roof r : getRoofIterator())
		{
			if (r.getRectangle().contains(mapX, mapY))
				r.setVisible(false);
			else
				r.setVisible(true);
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
	}

	public int getBackgroundImageIndex() {
		return backgroundImageIndex;
	}

	public void setBackgroundImageIndex(int index) {
		this.backgroundImageIndex = index;
	}

	private class MovementCost
	{
		private Hashtable<String, Integer> movementCostByTerrain = new Hashtable<>();

		public MovementCost(String moveType, JConfigurationValues configValues)
		{
			for (String terrainType : configValues.getTerrainTypes())
				movementCostByTerrain.put(terrainType, configValues.getMovementCosts(moveType, terrainType));
		}

		public int getMovementCost(String terrain)
		{
			return movementCostByTerrain.get(terrain);
		}
	}
}
