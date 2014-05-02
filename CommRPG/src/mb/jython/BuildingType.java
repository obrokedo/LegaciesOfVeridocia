package mb.jython;

import org.newdawn.slick.Graphics;

/**
 * Java interface defining getters and setters
 */

public interface BuildingType {

 public String getBuildingName();
 public String getBuildingAddress();
 public int getBuildingId();
 public void setBuildingName(String name);
 public void setBuildingAddress(String address);
 public void setBuildingId(int id);
 public void render(Graphics g);

}