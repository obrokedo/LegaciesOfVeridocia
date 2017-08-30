package mb.fc.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.exception.BadResourceException;
import mb.fc.game.sprite.Door;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.trigger.Trigger;
import mb.fc.game.trigger.TriggerCondition;
import mb.fc.game.trigger.TriggerCondition.HeroEntersLocation;
import mb.jython.GlobalPythonFactory;
import mb.jython.JConfigurationValues;

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
	private Hashtable<String, Integer> terrainEffectByType = new Hashtable<String, Integer>();
	private Hashtable<String, MovementCost> movementCostsByType = new Hashtable<String, Map.MovementCost>();

	/**
	 * A list of 2 dimensional int arrays, where each entry contains the tile indexs for each tile on that layer.
	 * A value of 0 in any given layer means that no tile was selected at this location.
	 */
	private String name;
	private ArrayList<MapLayer> mapLayer = new ArrayList<>();
	private MapLayer moveableLayer;
	private MapLayer roofLayer;
	protected int tileWidth, tileHeight;
	protected ArrayList<TileSet> tileSets = new ArrayList<TileSet>();
	protected ArrayList<MapObject> mapObjects = new ArrayList<MapObject>();
	protected Hashtable<Integer, Integer> landEffectByTileId = new Hashtable<Integer, Integer>();
	private Hashtable<TerrainTypeIndicator, String> overriddenTerrain = new Hashtable<TerrainTypeIndicator, String>();
	private Hashtable<Integer, Roof> roofsById = new Hashtable<Integer, Roof>();
	private Hashtable<Integer, ArrayList<MapLayer>> flashingLayersByPosition = new Hashtable<>();
	private Shape battleRegion = null;
	private int roofCount = -1;
	private int backgroundImageIndex;
	private JConfigurationValues jConfigValues;

	private float tileRatio = 2f;

	private TileSet inUseTileset;

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
		backgroundImageIndex = -1;
	}
	
	public void initializeObjects(boolean isCombat, StateInfo stateInfo)
	{
		int doorId = 500;
		for (MapObject mo : getMapObjects())
		{
			if (mo.getKey().equalsIgnoreCase("sprite"))
			{
				stateInfo.addSprite(mo.getSprite(stateInfo.getResourceManager()));
			}
			else if (mo.getKey().equalsIgnoreCase("door"))
			{
				Door door = (Door) mo.getDoor(stateInfo.getResourceManager(), doorId++);
				stateInfo.addSprite(door);
				// stateInfo.addMapTrigger(new TriggerLocation(stateInfo, mo, door));
				System.out.println(door.getId() + " DOOR");
				Trigger event = new Trigger(-100, false, false, true, true, null, null);
				event.addTriggerable(event.new TriggerRemoveSprite(door.getName()));
				stateInfo.getResourceManager().addTriggerEvent(door.getId(), event);
				
				TriggerCondition condition = new TriggerCondition(door.getId(), "Door");
				condition.addCondition(new HeroEntersLocation(mo.getName(), true));
				stateInfo.getResourceManager().addTriggerCondition(condition);
			}
			else if (mo.getKey().equalsIgnoreCase("searcharea"))
			{
				stateInfo.addSprite(mo.getSearchArea(stateInfo.getResourceManager()));
			}
			/*
			else if (mo.getKey().equalsIgnoreCase("roof"))
			{
				addSprite(mo.getSprite(this));
			}
			*/
		}
	}
	
	public void addMapObject(MapObject mo) {
		if (mo.getKey() == null && mo.getName() == null)
			return;

		if (mo.getKey() != null)
		{
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
		}
		else
			mo.setKey("location");
		
		this.mapObjects.add(mo);
	}

	public void addLayer(MapLayer layer)
	{
		mapLayer.add(layer);
	}

	public int getMapWidth() {
		return mapLayer.get(0).getTiles()[0].length;
	}

	public int getMapHeight() {
		return mapLayer.get(0).getTiles().length;
	}

	public int getMapWidthInPixels() {
		return mapLayer.get(0).getTiles()[0].length * tileWidth;
	}

	public int getMapHeightInPixels() {
		return mapLayer.get(0).getTiles().length * tileHeight;
	}

	public MapLayer getMapLayer(int layer) {
		return mapLayer.get(layer);
	}

	public int getMapEffectiveWidth()
	{
		return (int) (mapLayer.get(0).getTiles()[0].length / tileRatio);
	}

	public int getMapEffectiveHeight()
	{
		return (int) (mapLayer.get(0).getTiles().length / tileRatio);
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

	public void addTileset(SpriteSheet spriteSheet, int tileStartIndex, int tileWidth, int tileHeight, Hashtable<Integer, Integer> landEffectByTileId)
	{
		this.landEffectByTileId.putAll(landEffectByTileId);
		this.tileSets.add(new TileSet(spriteSheet, tileStartIndex));
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;

		Collections.sort(tileSets, new TileSetComparator());
	}

	public void renderSprite(int index, float xLoc, float yLoc)
	{
		for (TileSet ts : tileSets)
			if (index >= ts.getStartIndex()) {
				ts.renderSprite(xLoc, yLoc, index);
				return;
			}
	}

	public int getMovementCostByType(String moverType, int tileX, int tileY)
	{
		String tile;

		TerrainTypeIndicator tti = new TerrainTypeIndicator(tileX, tileY);
		if (overriddenTerrain.containsKey(tti))
		{
			try
			{
				tile = overriddenTerrain.get(tti);
				return movementCostsByType.get(moverType).getMovementCost(tile);
			}
			catch (NullPointerException e)
			{
				tile = overriddenTerrain.get(tti);
				throw new BadResourceException("The specified map has incorrect terrain cost types or the enemy/hero "
						+ "has an invalid movement type: Tile X "
						+ tileX + " Tile Y " + tileY + " Mover Type " + moverType);
			}
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
	
	public String getTerrainTypeByTile(int tileX, int tileY) {
		TerrainTypeIndicator tti = new TerrainTypeIndicator(tileX, tileY);
		if (overriddenTerrain.containsKey(tti))
		{
			return overriddenTerrain.get(tti);
		} else {
			return null;
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
		if ((moveableLayer.getTiles().length > (tileY * tileRatio)) && ((moveableLayer.getTiles()[0].length > tileX * tileRatio)))
			return moveableLayer.getTiles()[(int) (tileY * tileRatio)][(int) (tileX * tileRatio)] != 0;
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

		public void renderSprite(float x, float y, int index)
		{
			if (inUseTileset == null)
			{
				spriteSheet.startUse();
				inUseTileset = this;
			}
			else if (inUseTileset != this)
			{
				inUseTileset.endUse();
				spriteSheet.startUse();
				inUseTileset = this;
			}

			// System.out.println((index - startIndex) % ssWidth + " " + (index - startIndex) / ssWidth);
			// System.out.println("Bounds: " + spriteSheet.getHorizontalCount() + " " + spriteSheet.getVerticalCount());
			spriteSheet.renderInUse(Math.round(x), Math.round(y), (index - startIndex) % ssWidth, (index - startIndex) / ssWidth);
			// spriteSheet.getSubImage((index - startIndex) % ssWidth, (index - startIndex) / ssWidth).drawEmbedded(x, y, tileWidth + .01f, tileHeight + .01f);
		}

		public Image getSprite(int index) {
			// return spriteSheet.getSprite((index - startIndex) % ssWidth, (index - startIndex) / ssWidth);
			return spriteSheet.getSubImage((index - startIndex) % ssWidth, (index - startIndex) / ssWidth);
		}

		public void endUse() {
			spriteSheet.endUse();
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
		float mapXF = mapX / this.getTileRenderWidth() + .1f;
		float mapYF = mapY / this.getTileRenderHeight() + .1f;
		for (Roof r : getRoofIterator())
		{
			if (r.getRectangle().contains(mapXF, mapYF))
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

	public void setOriginalTileWidth(int origTileWidth)
	{
		tileRatio = DESIRED_TILE_WIDTH / origTileWidth;
	}
	
	public boolean isCustomBackground() {
		return -1 != backgroundImageIndex;
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

	public void setMoveableLayer(MapLayer moveableLayer) {
		this.moveableLayer = moveableLayer;
	}
	
	public void setRoofLayer(MapLayer roofLayer) {
		this.roofLayer = roofLayer;
	}

	public void endUse()
	{
		if (inUseTileset != null) {
			inUseTileset.endUse();
		}
		inUseTileset = null;
	}
	
	private class MapPathFinder implements TileBasedMap
	{
		private StateInfo stateInfo;
		
		public MapPathFinder(StateInfo stateInfo) {
			super();
			this.stateInfo = stateInfo;
		}

		@Override
		public int getWidthInTiles() {
			return getMapEffectiveWidth();
		}

		@Override
		public int getHeightInTiles() {
			// TODO Auto-generated method stub
			return getMapEffectiveHeight();
		}

		@Override
		public void pathFinderVisited(int x, int y) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean blocked(PathFindingContext context, int tx, int ty) {
			for (Sprite s : stateInfo.getSprites())
			{
				if (tx == s.getTileX() && 
						ty == s.getTileY())
				{
					return true;
				}
			}
			
			if (getHeightInTiles() > ty && getWidthInTiles() > tx)
			{
				return !isMarkedMoveable(tx, ty);
			}
			return true;
		}

		@Override
		public float getCost(PathFindingContext context, int tx, int ty) {
			return 1;
		}
	}
	
	public Path findTilePathWithPixels(int sx, int sy, int tx, int ty, StateInfo stateInfo)
	{
		AStarPathFinder asf = new AStarPathFinder(new MapPathFinder(stateInfo), 1000, false);
		return asf.findPath(null, sx / getTileEffectiveWidth(), sy / getTileEffectiveHeight(), 
				tx / getTileEffectiveWidth(), ty / getTileEffectiveHeight());
	}
	
	public Path findPixelPathWithPixels(int sx, int sy, int tx, int ty, StateInfo stateInfo)
	{
		Path tilePath = findTilePathWithPixels(sx, sy, tx, ty, stateInfo);
		Path pixelPath = new Path();
		for (int i = 0; i < tilePath.getLength(); i++)
		{
			pixelPath.appendStep(tilePath.getX(i) * getTileEffectiveWidth(), tilePath.getY(i) * getTileEffectiveHeight());
		}
		return pixelPath;
	}
	
	public void addFlashingLayer(MapLayer mapLayer) {
		ArrayList<MapLayer> layers = this.flashingLayersByPosition.get(this.mapLayer.size());
		if (layers == null)
			layers = new ArrayList<MapLayer>();
		layers.add(mapLayer);
		this.flashingLayersByPosition.put(this.mapLayer.size(), layers);
	}
	
	public List<MapLayer> getFlashingLayersByPosition(int layerPosition) {
		if (this.flashingLayersByPosition.containsKey(layerPosition))
			return this.flashingLayersByPosition.get(layerPosition);
		else
			return Collections.emptyList();
	}
	
	public void update(int delta)
	{
		for (ArrayList<MapLayer> layers : flashingLayersByPosition.values()) {
			for (MapLayer layer : layers) {
				layer.update(delta);
			}
		}
	}
	
	public MapLayer getRoofLayer() {
		return roofLayer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMapLayerAmount() {
		return mapLayer.size();
	}
}
