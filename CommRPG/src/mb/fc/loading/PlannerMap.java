package mb.fc.loading;

import java.awt.Image;
import java.util.Collections;
import java.util.Hashtable;

import mb.fc.map.Map;

public class PlannerMap extends Map {
	public void addTileset(Image[] sprites, int tileStartIndex,
			int tileWidth, int tileHeight,
			Hashtable<Integer, Integer> landEffectByTileId) {

		this.landEffectByTileId.putAll(landEffectByTileId);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.tileSets.add(new PlannerTileSet(sprites, tileStartIndex));
		Collections.sort(tileSets, new TileSetComparator());
	}

	public class PlannerTileSet extends TileSet
	{
		private Image[] sprites;

		public PlannerTileSet(Image[] sprites, int startIndex) {
			super(null, startIndex);
			this.sprites = sprites;
		}

		public Image getPlannerSprite(int index) {
			return sprites[index - startIndex];
		}
	}

	public Image getPlannerSprite(int index)
	{
		for (TileSet ts : tileSets)
			if (index >= ts.getStartIndex())
				return ((PlannerTileSet) ts).getPlannerSprite(index);
		return null;
	}

	@Override
	public int getTileScale() {
		// TODO Auto-generated method stub
		return 1;
	}
}
