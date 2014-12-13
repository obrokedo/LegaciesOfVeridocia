package mb.fc.game.resource;

import java.util.ArrayList;

import mb.fc.game.sprite.NPCSprite;
import mb.fc.game.text.Speech;

public class NPCResource
{
	public static int NPC_ID_COUNTER = -1;

	public static NPCSprite getNPC(int imageId, ArrayList<Speech> speeches)
	{
		switch (imageId)
		{
			case 0:
				return new NPCSprite("eriumguard", speeches, NPC_ID_COUNTER--);
			case 1:
				return new NPCSprite("darkmerchant", speeches, NPC_ID_COUNTER--);
			case 2:
				return new NPCSprite("youngwomen", speeches, NPC_ID_COUNTER--);
		}
		return null;
	}

	public static void resetNPCIds()
	{
		NPC_ID_COUNTER = -1;
	}
}
