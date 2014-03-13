package mb.fc.renderer;

import mb.fc.engine.message.Message;
import mb.fc.game.manager.Manager;
import mb.fc.map.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class TileMapRenderer extends Manager
{
	private Map map;		
	
	public TileMapRenderer() {}
	
	@Override
	public void initialize() {
		this.map = this.stateInfo.getResourceManager().getMap();
	}

	public void render(Graphics g)
	{
		int xOffset = stateInfo.getCamera().getLocationX() % map.getTileWidth();
		int yOffset = stateInfo.getCamera().getLocationY() % map.getTileHeight();
		
		int maxScreenTilesX = stateInfo.getCamera().getViewportWidth() / map.getTileWidth() + 1;
		int maxScreenTilesY = stateInfo.getCamera().getViewportHeight() / map.getTileHeight() + 1;
		
		int lastTileX;
		int lastTileY;
		
		lastTileX = Math.min(map.getMapWidth(), 
				stateInfo.getCamera().getLocationX() / map.getTileWidth() + maxScreenTilesX + (xOffset == 0 ? 0 : 1));		
		
		lastTileY = Math.min(map.getMapHeight(), 
				stateInfo.getCamera().getLocationY() / map.getTileHeight() + maxScreenTilesY + (yOffset == 0 ? 0 : 1));
		
		for (int mapX = stateInfo.getCamera().getLocationX() / map.getTileWidth(), frameX = 0; mapX < lastTileX; mapX++, frameX++)
		{
			for (int mapY = stateInfo.getCamera().getLocationY() / map.getTileHeight(), frameY = 0; mapY < lastTileY; mapY++, frameY++)
			{	
				g.drawImage(map.getSprite(map.getMapLayer(0)[mapY][mapX]), 
					frameX * map.getTileWidth() - xOffset + stateInfo.getGc().getDisplayPaddingX(), frameY * map.getTileHeight() - yOffset);
				
				if (map.getMapLayer(1)[mapY][mapX] != 0)
				{
					g.drawImage(map.getSprite(map.getMapLayer(1)[mapY][mapX]), 
						frameX * map.getTileWidth() - xOffset + stateInfo.getGc().getDisplayPaddingX(), frameY * map.getTileHeight() - yOffset);
				}								
			}			
		}
	}
	
	public void renderForeground(Graphics g)
	{
		int xOffset = stateInfo.getCamera().getLocationX() % map.getTileWidth();
		int yOffset = stateInfo.getCamera().getLocationY() % map.getTileHeight();
		
		int maxScreenTilesX = stateInfo.getCamera().getViewportWidth() / map.getTileWidth() + 1;
		int maxScreenTilesY = stateInfo.getCamera().getViewportHeight() / map.getTileHeight() + 1;
		
		int lastTileX;
		int lastTileY;
		
		lastTileX = Math.min(map.getMapWidth(), 
				stateInfo.getCamera().getLocationX() / map.getTileWidth() + maxScreenTilesX + (xOffset == 0 ? 0 : 1));		
		
		lastTileY = Math.min(map.getMapHeight(), 
				stateInfo.getCamera().getLocationY() / map.getTileHeight() + maxScreenTilesY + (yOffset == 0 ? 0 : 1));
		
		for (int mapX = stateInfo.getCamera().getLocationX() / map.getTileWidth(), frameX = 0; mapX < lastTileX; mapX++, frameX++)
		{
			for (int mapY = stateInfo.getCamera().getLocationY() / map.getTileHeight(), frameY = 0; mapY < lastTileY; mapY++, frameY++)
			{	
				if (map.getMapLayer(2)[mapY][mapX] != 0)
					g.drawImage(map.getSprite(map.getMapLayer(2)[mapY][mapX]), 
						frameX * map.getTileWidth() - xOffset + stateInfo.getGc().getDisplayPaddingX(), frameY * map.getTileHeight() - yOffset);
				
				// g.drawString(mapX +"\n" + mapY, frameX * map.getTileWidth() - xOffset + stateInfo.getGc().getDisplayPaddingX(), frameY * map.getTileHeight() - yOffset);
			}			
		}
		
		g.setColor(Color.black);
		g.fillRect(0, 
				0, stateInfo.getGc().getDisplayPaddingX(), stateInfo.getGc().getScreenHeight());
		g.fillRect(stateInfo.getGc().getDisplayPaddingX() + stateInfo.getCamera().getViewportWidth(),
				0, stateInfo.getGc().getDisplayPaddingX(), stateInfo.getGc().getScreenHeight());
	}
	
	@Override
	public void recieveMessage(Message message) {

	}	
}
