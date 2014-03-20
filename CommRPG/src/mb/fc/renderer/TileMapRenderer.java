package mb.fc.renderer;

import mb.fc.engine.message.Message;
import mb.fc.game.Camera;
import mb.fc.game.manager.Manager;
import mb.fc.game.ui.FCGameContainer;
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

	public void render(Camera camera, Graphics g, FCGameContainer gc)
	{
		int xOffset = camera.getLocationX() % map.getTileWidth();
		int yOffset = camera.getLocationY() % map.getTileHeight();
		
		int maxScreenTilesX = camera.getViewportWidth() / map.getTileWidth() + 1;
		int maxScreenTilesY = camera.getViewportHeight() / map.getTileHeight() + 1;
		
		int lastTileX;
		int lastTileY;
		
		lastTileX = Math.min(map.getMapWidth(), 
				camera.getLocationX() / map.getTileWidth() + maxScreenTilesX + (xOffset == 0 ? 0 : 1));		
		
		lastTileY = Math.min(map.getMapHeight(), 
				camera.getLocationY() / map.getTileHeight() + maxScreenTilesY + (yOffset == 0 ? 0 : 1));
		
		for (int mapX = camera.getLocationX() / map.getTileWidth(), frameX = 0; mapX < lastTileX; mapX++, frameX++)
		{
			for (int mapY = camera.getLocationY() / map.getTileHeight(), frameY = 0; mapY < lastTileY; mapY++, frameY++)
			{	
				g.drawImage(map.getSprite(map.getMapLayer(0)[mapY][mapX]), 
					frameX * map.getTileWidth() - xOffset + gc.getDisplayPaddingX(), frameY * map.getTileHeight() - yOffset);
				
				if (map.getMapLayer(1)[mapY][mapX] != 0)
				{
					g.drawImage(map.getSprite(map.getMapLayer(1)[mapY][mapX]), 
						frameX * map.getTileWidth() - xOffset + gc.getDisplayPaddingX(), frameY * map.getTileHeight() - yOffset);
				}								
			}			
		}
	}
	
	public void renderForeground(Camera camera, Graphics g, FCGameContainer gc)
	{
		int xOffset = camera.getLocationX() % map.getTileWidth();
		int yOffset = camera.getLocationY() % map.getTileHeight();
		
		int maxScreenTilesX = camera.getViewportWidth() / map.getTileWidth() + 1;
		int maxScreenTilesY = camera.getViewportHeight() / map.getTileHeight() + 1;
		
		int lastTileX;
		int lastTileY;
		
		lastTileX = Math.min(map.getMapWidth(), 
				camera.getLocationX() / map.getTileWidth() + maxScreenTilesX + (xOffset == 0 ? 0 : 1));		
		
		lastTileY = Math.min(map.getMapHeight(), 
				camera.getLocationY() / map.getTileHeight() + maxScreenTilesY + (yOffset == 0 ? 0 : 1));
		
		for (int mapX = camera.getLocationX() / map.getTileWidth(), frameX = 0; mapX < lastTileX; mapX++, frameX++)
		{
			for (int mapY = camera.getLocationY() / map.getTileHeight(), frameY = 0; mapY < lastTileY; mapY++, frameY++)
			{	
				if (map.getMapLayer(2)[mapY][mapX] != 0)
					g.drawImage(map.getSprite(map.getMapLayer(2)[mapY][mapX]), 
						frameX * map.getTileWidth() - xOffset + gc.getDisplayPaddingX(), frameY * map.getTileHeight() - yOffset);
				
				// g.drawString(mapX +"\n" + mapY, frameX * map.getTileWidth() - xOffset + gc.getDisplayPaddingX(), frameY * map.getTileHeight() - yOffset);
			}			
		}
		
		g.setColor(Color.black);
		g.fillRect(0, 
				0, gc.getDisplayPaddingX(), gc.getScreenHeight());
		g.fillRect(gc.getDisplayPaddingX() + camera.getViewportWidth(),
				0, gc.getDisplayPaddingX(), gc.getScreenHeight());
	}
	
	public void setMap(Map map) {
		this.map = map;
	}

	@Override
	public void recieveMessage(Message message) {

	}	
}
