package mb.fc.game.move;

import mb.fc.engine.message.OverlandMoveMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.listener.MouseListener;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;	

public class OverlandMove implements MouseListener, TileBasedMap
{
	private int[][] tiles;
	private AStarPathFinder pathFinder;
	
	public OverlandMove(StateInfo stateInfo) 
	{
		tiles = stateInfo.getResourceManager().getMap().getMapLayer(3);
		pathFinder = new AStarPathFinder(this, 200, false);
		stateInfo.registerMouseListener(this);
	}
	
	@Override
	public boolean mouseUpdate(int frameMX, int frameMY, int mapMX, int mapMY,
			boolean leftClicked, boolean rightClicked, StateInfo stateInfo) 
	{
		if (leftClicked)
		{
			System.out.println(mapMX + " " + mapMY);
			
			int mx = mapMX / stateInfo.getTileWidth();
			int my = mapMY / stateInfo.getTileHeight();
			if (tiles.length > my && tiles[0].length > mx && tiles[my][mx] != -1)
			{
				Path path = pathFinder.findPath(
						null, 
						stateInfo.getCurrentSprite().getTileX(), 
						stateInfo.getCurrentSprite().getTileY(), 
						mx, 
						my);
				
				
				if (path != null)
				{
					
					stateInfo.sendMessage(new OverlandMoveMessage(stateInfo.getCurrentSprite(), path));
				}
			
				return true;
			}
		}
		return false;
	}

	@Override
	public int getWidthInTiles() {
		return tiles[0].length;
	}

	@Override
	public int getHeightInTiles() {
		return tiles.length;
	}

	@Override
	public void pathFinderVisited(int x, int y) 
	{ }

	@Override
	public boolean blocked(PathFindingContext context, int tx, int ty) 
	{
		if (tiles.length > ty && tiles[0].length > tx)		
			return tiles[ty][tx] == 0;
		return true;
	}

	@Override
	public float getCost(PathFindingContext context, int tx, int ty) {
		return 0;
	}

	@Override
	public int getZOrder() {
		return MouseListener.ORDER_MAP_MOVE;
	}
}
