package mb.fc.game.menu;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.map.Map;

public class MiniMapPanel extends Menu {

	private Map currentMap;
	private int menuWidth, menuHeight, menuX, menuY;
	private StateInfo stateInfo;
	
	public MiniMapPanel(Map currentMap, StateInfo stateInfo) {
		super(Panel.PanelType.PANEL_MINI_MAP);
		this.currentMap = currentMap;
		
		float width = 0, height = 0;
		if (currentMap.getBattleRegion() != null) {
			width = currentMap.getBattleRegion().getWidth() / currentMap.getTileEffectiveWidth();
			height = currentMap.getBattleRegion().getHeight() / currentMap.getTileEffectiveHeight();
		} else {
			width = currentMap.getMapWidth() / currentMap.getTileRatio();
			height = currentMap.getMapHeight() / currentMap.getTileRatio();
		}
		
		menuWidth = (int) (width * 5);
		menuHeight = (int) (height * 5);
		menuX = (int) (CommRPG.GAME_SCREEN_SIZE.width - width * 5) / 2;
		menuY = (int) (CommRPG.GAME_SCREEN_SIZE.height - height * 5) / 3;
		
		this.stateInfo = stateInfo;
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		if (input.isKeyDown(KeyMapping.BUTTON_1) ||
			input.isKeyDown(KeyMapping.BUTTON_2) ||
			input.isKeyDown(KeyMapping.BUTTON_3))
				return MenuUpdate.MENU_CLOSE;
		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		float width = 0f;
		float height = 0f;
		int xMin = 0;
		int yMin = 0;
		
		if (currentMap.getBattleRegion() != null) {
			width = currentMap.getBattleRegion().getWidth() / currentMap.getTileEffectiveWidth();
			height = currentMap.getBattleRegion().getHeight() / currentMap.getTileEffectiveHeight();
			xMin = (int) (currentMap.getBattleRegion().getMinX() / currentMap.getTileEffectiveWidth());
			yMin = (int) (currentMap.getBattleRegion().getMinY() / currentMap.getTileEffectiveHeight());
		} else {
			width = currentMap.getMapWidth() / currentMap.getTileRatio();
			height = currentMap.getMapHeight() / currentMap.getTileRatio();
		}
		
		Panel.drawPanelBox((int) menuX, (int) menuY, 
				(int) menuWidth + 10, (int) menuHeight + 10, graphics, Color.black);
		
		for (int x = xMin; 
				x < xMin + width; x++) {
			
			for (int y = yMin; 
					y < yMin + height; y++) {
				Sprite s = stateInfo.getSpriteAtTile(x, y);
				if (currentMap.isMarkedMoveable(x, y) &&
						(s == null || s.getSpriteType() != Sprite.TYPE_STATIC_SPRITE)) {
					graphics.setColor(Color.lightGray);
				} else {
					graphics.setColor(Color.black);
				}
				
				graphics.fillRect(menuX + 5 + (x - xMin) * 5, menuY + 5 + (y - yMin) * 5, 5, 5);
				
				if (s != null && s.getSpriteType() == Sprite.TYPE_COMBAT) {
					if (((CombatSprite) s).isHero()) {
						graphics.setColor(Color.green);
					} else {
						graphics.setColor(Color.red);
					}
					graphics.fillOval(menuX + 5 + (x - xMin) * 5, menuY + 5 + (y - yMin) * 5, 5, 5);
				}
			}
		}
	}

}
