package mb.fc.game.hudmenu;

import java.util.ArrayList;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.listener.MouseListener;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.map.Map;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class MapMoveMenu extends Panel
{
	private static final int CAMERA_MOVE = 32;
	private ArrayList<MapListener> listeners;
	private int selected;
	private Camera camera;
	private int maxX;
	private int maxY;	
	
	public MapMoveMenu(StateInfo stateInfo) {
		super(Panel.PANEL_MAPMOVE);
		
		listeners = new ArrayList<MapListener>();
		listeners.add(new MapListener(new Rectangle(20, 0, stateInfo.getGc().getWidth() - 40, 20), 1, 0, -CAMERA_MOVE));
		listeners.add(new MapListener(new Rectangle(20, stateInfo.getGc().getHeight() - 20, stateInfo.getGc().getWidth() - 40, 20), 2, 0, CAMERA_MOVE));
		listeners.add(new MapListener(new Rectangle(stateInfo.getGc().getWidth() - 20, 20, 20, stateInfo.getGc().getHeight() - 40), 3, CAMERA_MOVE, 0));
		listeners.add(new MapListener(new Rectangle(0, 20, 20, stateInfo.getGc().getHeight() - 40), 4, -CAMERA_MOVE, 0));
		
		for (MouseListener ml : listeners)
			stateInfo.registerMouseListener(ml);
		Map map = stateInfo.getResourceManager().getMap();
		maxX = map.getMapWidth() * map.getTileWidth() - stateInfo.getCamera().getViewportWidth();
		maxY = map.getMapHeight() * map.getTileHeight() - stateInfo.getCamera().getViewportHeight();
		
		camera = stateInfo.getCamera();;
		
		selected = 0;
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) 
	{

	}	
	
	private class MapListener implements MouseListener
	{
		private Rectangle triggerArea;
		private int selectedId;
		private int moveX;
		private int moveY;
		
		
		public MapListener(Rectangle triggerArea, int selectedId, int moveX, int moveY) {
			this.triggerArea = triggerArea;
			this.selectedId = selectedId;
			this.moveX = moveX;
			this.moveY = moveY;
		}

		@Override
		public boolean mouseUpdate(int frameMX, int frameMY, int mapMX,
				int mapMY, boolean leftClicked, boolean rightClicked,
				StateInfo stateInfo) 
		{
			if (triggerArea.contains(frameMX, frameMY))
			{
				selected = this.selectedId;
				// TODO Should the camera be directly influenced here?
				int mX = camera.getLocationX() + moveX;
				int mY = camera.getLocationY() + moveY;
				
				if (mX < 0)
					mX = 0;
				else if (mX > maxX)
					mX = maxX;
				
				if (mY < 0)
					mY = 0;
				else if (mY > maxY)
					mY = maxY;
				
				camera.setLocation(mX, mY);
			}
			else if (selected == selectedId)
				selected = 0;
			return false;
		}

		@Override
		public int getZOrder() {
			return MouseListener.ORDER_MAP_MOVE;
		}
	}
}
