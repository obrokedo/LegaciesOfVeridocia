from mb.fc.engine.config import PanelRenderer
import sys

# Building object that subclasses a Java interface
class PanelRender(PanelRenderer):

    # Draws the background and border of all in game panels
    def render(self, menuBorder, x, y, width, height, graphics):
        graphics.fillRect(x, y, width, height);
        menuBorder.getSprite(4, 0).draw(x, y + height - 12, x + width, y + height, 4, 0, 5, 12);
        menuBorder.getSprite(5, 0).draw(x, y, x + width, y + 12, 4, 0, 5, 12);
        menuBorder.getSprite(6, 0).draw(x, y + 12, 12, height - 24);
        menuBorder.getSprite(7, 0).draw(x + width - 12, y + 12, 12, height - 24);
        
        menuBorder.getSprite(0, 0).draw(x, y + height - 12);
        menuBorder.getSprite(1, 0).draw(x, y);        
        menuBorder.getSprite(2, 0).draw(x + width - 12, y + height - 12);
        menuBorder.getSprite(3, 0).draw(x + width - 12, y);        
        
    def reload(self):
            sys.modules.clear()