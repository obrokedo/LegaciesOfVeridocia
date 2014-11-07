package mb.fc.game.sprite;

import java.util.ArrayList;

import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.text.Speech;

public class NPCSprite extends AnimatedSprite
{
	private static final long serialVersionUID = 1L;

	private ArrayList<Speech> speeches;
	private int uniqueNPCId;

	public NPCSprite(String imageName,
			ArrayList<Speech> speeches)
	{
		super(0, 0, imageName);
		this.speeches = speeches;
		this.spriteType = Sprite.TYPE_NPC;
		this.uniqueNPCId = 0;
	}

	public void triggerButton1Event(StateInfo stateInfo)
	{
		SPEECHLOOP: for (Speech s : speeches)
		{
			// Check to see if this mesage meets all required quests
			if (s.getRequires() != null && s.getRequires().length > 0)
			{
				for (int i : s.getRequires())
				{
					if (i != -1 && !stateInfo.isQuestComplete(i))
						continue SPEECHLOOP;
				}
			}

			// Check to see if the excludes quests have been completed, if so
			// then we can't use this message
			if (s.getExcludes() != null && s.getExcludes().length > 0)
			{
				for (int i : s.getExcludes())
				{
					if (i != -1 && stateInfo.isQuestComplete(i))
						continue SPEECHLOOP;
				}
			}

			if (stateInfo.getCurrentSprite().getLocX() > this.getLocX())
				this.setFacing(Direction.RIGHT);
			else if (stateInfo.getCurrentSprite().getLocX() < this.getLocX())
				this.setFacing(Direction.LEFT);
			else if (stateInfo.getCurrentSprite().getLocY() > this.getLocY())
				this.setFacing(Direction.DOWN);
			else if (stateInfo.getCurrentSprite().getLocY() < this.getLocY())
				this.setFacing(Direction.UP);

			stateInfo.sendMessage(new SpeechMessage(s.getMessage(), s.getTriggerId(), s.getPortraitId()));
			break;
		}
	}

	public int getUniqueNPCId() {
		return uniqueNPCId;
	}

	public void setUniqueNPCId(int uniqueNPCId) {
		this.uniqueNPCId = uniqueNPCId;
	}
}
