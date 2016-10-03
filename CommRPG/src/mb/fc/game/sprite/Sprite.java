package mb.fc.game.sprite;

import java.io.Serializable;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class Sprite implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final byte TYPE_COMBAT = 1;
	public static final byte TYPE_NPC = 2;
	public static final byte TYPE_STATIC_SPRITE = 3;
	protected Rectangle spriteBounds;
	private int tileX, tileY;
	protected byte spriteType;
	protected String name;
	protected boolean visible = true;
	protected int id;

	protected static StateInfo stateInfo;

	public Sprite(int locX, int locY, int id)
	{
		spriteBounds = new Rectangle(locX, locY, 24, 24);
	}

	public void initializeSprite(StateInfo stateInfo)
	{
		// TODO I'm not sure that I really like this, but it's probably better then doing
		// division each time you need the sprites tile location or passing the state info
		// in each time you use the setter
		Sprite.stateInfo = stateInfo;
	}

	public void update()
	{

	}

	public void render(Camera camera, Graphics graphics, FCGameContainer cont)
	{

	}

	public void destroy(StateInfo stateInfo)
	{

	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public float getLocX() {
		return spriteBounds.getX();
	}

	public void setLocX(float locX) {
		spriteBounds.setX(locX);
		tileX = (int) (locX / stateInfo.getTileWidth());
	}

	public float getLocY() {
		return spriteBounds.getY();
	}

	public float getAbsLocX()
	{
		return spriteBounds.getX();
	}

	public float getAbsLocY()
	{
		return spriteBounds.getY();
	}

	public void setLocY(float locY) {
		spriteBounds.setY(locY);
		tileY = (int) (locY / stateInfo.getTileHeight());
	}

	public int getTileX() {
		return tileX;
	}

	public int getTileY() {
		return tileY;
	}

	public void setTileX(int tileX) {
		this.tileX = tileX;
		spriteBounds.setX(tileX * stateInfo.getTileWidth());
	}

	public void setTileY(int tileY) {
		this.tileY = tileY;
		spriteBounds.setY(tileY * stateInfo.getTileHeight());
	}

	public Rectangle getSpriteBounds() {
		return spriteBounds;
	}

	public byte getSpriteType() {
		return spriteType;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		System.out.println("Set hero ID " + id);
	}
}
