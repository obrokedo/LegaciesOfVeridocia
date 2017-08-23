package mb.fc.game.resource;

import java.util.ArrayList;

import mb.fc.game.sprite.NPCSprite;
import mb.fc.game.text.Speech;

public class NPCResource
{
	public static int NPC_ID_COUNTER = -1;

	public static NPCSprite getNPC(String animation, ArrayList<Speech> speeches)
	{
		return new NPCSprite(animation, speeches, NPC_ID_COUNTER--);
	}

	public static void resetNPCIds()
	{
		NPC_ID_COUNTER = -1;
	}
}
