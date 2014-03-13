package mb.fc.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import mb.fc.game.sprite.CombatSprite;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

public class Map 
{
	private static final int[][] movementCostsByType = {
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
	
	private static final int[] terrainEffectByType = {0, 15, 0, 30, 30, 30, 0, 0, 0};
	
	private ArrayList<int[][]> mapLayer = new ArrayList<int[][]>();
	private int tileWidth, tileHeight;
	private ArrayList<TileSet> tileSets = new ArrayList<TileSet>();
	private ArrayList<MapObject> mapObjects = new ArrayList<MapObject>();
	private Hashtable<Integer, Integer> landEffectByTileId = new Hashtable<Integer, Integer>();
	
	public Map() {
		super();
	}
	
	public void reinitalize()
	{
		mapObjects.clear();
		mapLayer.clear();
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

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public ArrayList<MapObject> getMapObjects() {
		return mapObjects;
	}

	public void addMapObject(MapObject mo) {
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
		if (mapLayer.get(1)[tileY][tileX] != 0)
			tile = mapLayer.get(1)[tileY][tileX];
		else
			tile = mapLayer.get(0)[tileY][tileX];
		
		// Subtract one to account for the blank space at 0
		tile--;
		
		if (landEffectByTileId.containsKey(tile))
			return movementCostsByType[moverType][landEffectByTileId.get(tile)];
		else
			return 10;
	}

	public int getLandEffectByTile(int moverType, int tileX, int tileY)
	{
		int tile;
		if (mapLayer.get(1)[tileY][tileX] != 0)
			tile = mapLayer.get(1)[tileY][tileX];
		else
			tile = mapLayer.get(0)[tileY][tileX];
		
		// Subtract one to account for the blank space at 0
		tile--;	
		
		if (moverType == CombatSprite.MOVEMENT_FLYING)
			return 0;
		
		if (landEffectByTileId.containsKey(tile))
		{			
			return terrainEffectByType[landEffectByTileId.get(tile)];
		}		
		else
		{
			return 0;
		}
	}
	
	
	private class TileSet
	{
		private SpriteSheet spriteSheet;
		private int startIndex;
		private int ssWidth;		
		
		public TileSet(SpriteSheet spriteSheet, int startIndex) {
			super();
			this.spriteSheet = spriteSheet;
			this.startIndex = startIndex;
			this.ssWidth = spriteSheet.getHorizontalCount();
		}

		public Image getSprite(int index) {
			return spriteSheet.getSprite((index - startIndex) % ssWidth, (index - startIndex) / ssWidth);
		}

		public int getStartIndex() {
			return startIndex;
		}
	}
	
	private class TileSetComparator implements Comparator<TileSet>
	{
		@Override
		public int compare(TileSet ts0, TileSet ts1) 		
		{
			return ts1.startIndex - ts0.startIndex;
		}
	}
}
