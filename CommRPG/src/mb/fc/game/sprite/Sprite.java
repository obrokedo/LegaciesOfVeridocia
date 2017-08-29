package mb.fc.game.sprite;

import java.io.Serializable;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;

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

	// protected static StateInfo stateInfo;

	public Sprite(int locX, int locY, int id)
	{
		spriteBounds = new Rectangle(locX, locY, 24, 24);
	}

	public void initializeSprite(FCResourceManager fcrm)
	{ }

	public void update(StateInfo stateInfo)
	{

	}

	public void render(Camera camera, Graphics graphics, PaddedGameContainer cont, int tileHeight)
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

	public void setLocX(float locX, int tileWidth) {
		spriteBounds.setX(locX);
		tileX = (int) (locX / tileWidth);
	}

	public float getLocY() {
		return spriteBounds.getY();
	}
	
	public void setLocY(float locY, int tileHeight) {
		spriteBounds.setY(locY);
		tileY = (int) (locY / tileHeight);
	}

	public int getTileX() {
		return tileX;
	}

	public int getTileY() {
		return tileY;
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
