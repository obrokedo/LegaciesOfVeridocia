package mb.fc.map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.ai.AI;
import mb.fc.game.ai.ClericAI;
import mb.fc.game.ai.WarriorAI;
import mb.fc.game.ai.WizardAI;
import mb.fc.game.constants.Direction;
import mb.fc.game.resource.EnemyResource;
import mb.fc.game.resource.NPCResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Door;
import mb.fc.game.sprite.NPCSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.sprite.StaticSprite;
import mb.fc.game.text.Speech;
import mb.fc.loading.FCResourceManager;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class MapObject
{
	private int width, height, x, y;
	private String name;
	private String key;
	private ArrayList<Point> polyPoints = null;
	private Hashtable<String, String> params;
	private Shape shape;

	public MapObject()
	{
		params = new Hashtable<String, String>();
	}

	public void determineShape()
	{
		if (polyPoints == null)
			shape = new Rectangle(x, y, width, height);
		else
		{
			float[] points = new float[polyPoints.size() * 2];
			for (int i = 0; i < polyPoints.size(); i++)
			{
				points[2 * i] = polyPoints.get(i).x + x;
				points[2 * i + 1] = polyPoints.get(i).y + y;
			}
			shape = new Polygon(points);
			x = (int) shape.getX();
			y = (int) shape.getY();
			width = (int) shape.getWidth();
			height = (int) shape.getHeight();
		}
	}

	public void setWidth(int width) {
		this.width = width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setValue(String value)
	{
		if (value != null && value.length() > 0)
		{
			String[] splitParams = value.split(" ");
			for (int i = 0; i < splitParams.length; i++)
			{
				String[] attributes = splitParams[i].split("=");
				params.put(attributes[0], attributes[1]);
			}
		}
	}

	public void setPolyPoints(ArrayList<Point> polyPoints) {
		this.polyPoints = polyPoints;
	}

	public Shape getShape()
	{
		return shape;
	}

	public String getParam(String param)
	{
		return params.get(param);
	}

	public Hashtable<String, String> getParams() {
		return params;
	}

	public void getStartLocation(StateInfo stateInfo)
	{
		int startX = 0;
		int startY = 0;

		Iterator<Sprite> spritesItr = stateInfo.getSpriteIterator();
		boolean getOnNext = true;
		Sprite sprite = null;

		while (spritesItr.hasNext())
		{
			if (getOnNext)
				sprite = spritesItr.next();
			getOnNext = true;

			if (sprite.getSpriteType() == Sprite.TYPE_COMBAT && ((CombatSprite) sprite).isHero() && sprite.getLocX() == -1)
			{
				if (shape.contains(x + startX + 1, y + startY + 1))
				{
					((CombatSprite) sprite).setLocation((x + startX), (y + startY), stateInfo.getTileWidth(), stateInfo.getTileHeight());
					((CombatSprite) sprite).setFacing(Direction.DOWN);
				}
				else
					getOnNext = false;

				startX += stateInfo.getTileWidth();
				if (startX == shape.getWidth())
				{
					startX = 0;
					startY += stateInfo.getTileHeight();
					if (startY == shape.getHeight())
						break;
				}
			}
		}
	}

	public NPCSprite getNPC(FCResourceManager fcrm)
	{
		int imageId = Integer.parseInt(params.get("imageid"));
		int wander = 0;
		if (params.containsKey("wander"))
			wander = Integer.parseInt(params.get("wander"));
		ArrayList<Speech> speeches = fcrm.getSpeechesById(Integer.parseInt(params.get("textid")));
		NPCSprite npc = NPCResource.getNPC(imageId, speeches);
		if (params.get("npcid") != null)
			npc.setUniqueNPCId(Integer.parseInt(params.get("npcid")));
		npc.initializeSprite(fcrm);
		npc.setInitialPosition(x, y, fcrm.getMap().getTileEffectiveWidth(), fcrm.getMap().getTileEffectiveHeight(), wander);
		return npc;
	}

	public CombatSprite getEnemy(FCResourceManager fcrm)
	{
		int enemyId = Integer.parseInt(params.get("enemyid"));
		CombatSprite enemy = EnemyResource.getEnemy(enemyId);
		if (params.containsKey("ai"))
		{
			String type = params.get("ai");
			String approach = params.get("aiapproach");

			int id = 0;
			if (params.containsKey("unit"))
				id = Integer.parseInt(params.get("unit"));

			int approachIndex = 0;
			if (approach.equalsIgnoreCase("fast"))
				approachIndex = AI.APPROACH_KAMIKAZEE;
			else if (approach.equalsIgnoreCase("slow"))
				approachIndex = AI.APPROACH_HESITANT;
			else if (approach.equalsIgnoreCase("wait"))
				approachIndex = AI.APPROACH_REACTIVE;

			if (type.equalsIgnoreCase("wizard"))
				enemy.setAi(new WizardAI(approachIndex));
			else if (type.equalsIgnoreCase("cleric"))
				enemy.setAi(new ClericAI(approachIndex));
			else if (type.equalsIgnoreCase("fighter"))
				enemy.setAi(new WarriorAI(approachIndex));

			if (id != -1)
				enemy.setUniqueEnemyId(id);
		}

		enemy.initializeSprite(fcrm);
		enemy.setLocX(x, fcrm.getMap().getTileEffectiveWidth());
		enemy.setLocY(y, fcrm.getMap().getTileEffectiveHeight());

		return enemy;
	}

	public Sprite getSprite(FCResourceManager fcrm)
	{
		String name = params.get("name");
		Image image = fcrm.getImage(params.get("image"));

		int[] trigger = null;
		if (params.containsKey("searchtrigger"))
		{
			String[] split = params.get("searchtrigger").split(",");
			trigger = new int[split.length];
			for (int i = 0; i < split.length; i++)
				trigger[i] = Integer.parseInt(split[i]);
		}

		Sprite s = new StaticSprite(x, y, name, image, trigger);
		s.initializeSprite(fcrm);
		s.setLocX(x, fcrm.getMap().getTileEffectiveWidth());
		s.setLocY(y, fcrm.getMap().getTileEffectiveHeight());
		return s;
	}

	public Sprite getSearchArea(FCResourceManager fcrm)
	{
		int[] trigger = null;
		if (params.containsKey("searchtrigger"))
		{
			String[] split = params.get("searchtrigger").split(",");
			trigger = new int[split.length];
			for (int i = 0; i < split.length; i++)
				trigger[i] = Integer.parseInt(split[i]);
		}

		Sprite s = new StaticSprite(x, y, trigger);
		s.initializeSprite(fcrm);
		s.setLocX(x, fcrm.getMap().getTileEffectiveWidth());
		s.setLocY(y, fcrm.getMap().getTileEffectiveHeight());
		return s;
	}

	public Sprite getDoor(FCResourceManager fcrm, int doorId)
	{
		Image image = fcrm.getImage(params.get("image"));

		Sprite s = new Door(doorId, x, y, image);
		s.initializeSprite(fcrm);
		s.setLocX(x, fcrm.getMap().getTileEffectiveWidth());
		s.setLocY(y, fcrm.getMap().getTileEffectiveHeight());
		return s;
	}
	
	public boolean contains(int mapX, int mapY)
	{
		return shape.contains(mapX + 1, mapY + 1);
	}
	
	public boolean contains(CombatSprite cs)
	{
		return shape.contains(cs.getLocX() + 1, cs.getLocY() + 1);
	}
}
