package mb.fc.game.sprite;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.SpriteMoveMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.text.Speech;

public class NPCSprite extends AnimatedSprite
{
	private static final long serialVersionUID = 1L;

	private int speechId;
	private int uniqueNPCId;
	private int moveCounter = 0;
	private int initialTileX = -1;
	private int initialTileY = -1;
	private int maxWander = 0;

	public NPCSprite(String imageName,
			int speechId, int id, String name)
	{
		super(0, 0, imageName, Integer.MAX_VALUE);
		this.name = name;
		this.speechId = speechId;
		this.spriteType = Sprite.TYPE_NPC;
		this.uniqueNPCId = 0;
		this.id = id;
	}

	public void setInitialPosition(int xLoc, int yLoc,
			int tileWidth, int tileHeight, int maxWander, Direction direction)
	{
		this.setLocation(xLoc, yLoc, tileWidth, tileHeight);
		if (direction != null)
			this.setFacing(direction);
		this.initialTileX = this.getTileX();
		this.initialTileY = this.getTileY();
		this.maxWander = maxWander;
	}

	public void triggerButton1Event(StateInfo stateInfo)
	{
		if (Speech.showFirstSpeechMeetsReqs(speechId, stateInfo)) {
			if (stateInfo.getCurrentSprite().getLocX() > this.getLocX())
				this.setFacing(Direction.RIGHT);
			else if (stateInfo.getCurrentSprite().getLocX() < this.getLocX())
				this.setFacing(Direction.LEFT);
			else if (stateInfo.getCurrentSprite().getLocY() > this.getLocY())
				this.setFacing(Direction.DOWN);
			else if (stateInfo.getCurrentSprite().getLocY() < this.getLocY())
				this.setFacing(Direction.UP);
		}
	}

	boolean moving = false;

	@Override
	public void update(StateInfo stateInfo) {
		super.update(stateInfo);
		
		if (stateInfo.getCamera().isVisible(this)) {
			if (maxWander > 0)
				wanderMove(stateInfo);
		}
	}

	private void wanderMove(StateInfo stateInfo)
	{
		if (!moving)
		{
			if (moveCounter == 30)
			{
				Direction nextDir = null;
				int count = 0;
				while (nextDir == null && count <= 4)
				{
					count++;
					nextDir = Direction.values()[CommRPG.RANDOM.nextInt(4)];
					switch (nextDir)
					{
						case UP:
							if (Math.abs(this.getTileX() - this.initialTileX) + Math.abs(this.getTileY() - 1 - this.initialTileY) > maxWander)
								nextDir = null;
							break;
						case DOWN:
							if (Math.abs(this.getTileX() - this.initialTileX) + Math.abs(this.getTileY() + 1 - this.initialTileY) > maxWander)
								nextDir = null;
							break;
						case LEFT:
							if (Math.abs(this.getTileX() - 1 - this.initialTileX) + Math.abs(this.getTileY() - this.initialTileY) > maxWander)
								nextDir = null;
							break;
						case RIGHT:
							if (Math.abs(this.getTileX() + 1 - this.initialTileX) + Math.abs(this.getTileY() - this.initialTileY) > maxWander)
								nextDir = null;
							break;
					}
				}

				if (nextDir != null)
				{
					moveCounter = 0;
					moving = true;
					stateInfo.sendMessage(new SpriteMoveMessage(this, nextDir), true);
				}
			}
			else
				moveCounter++;
		}
	}

	public int getUniqueNPCId() {
		return uniqueNPCId;
	}

	public void setUniqueNPCId(int uniqueNPCId) {
		this.uniqueNPCId = uniqueNPCId;
	}

	@Override
	public void doneMoving() {
		super.doneMoving();
		moving = false;
	}
}
