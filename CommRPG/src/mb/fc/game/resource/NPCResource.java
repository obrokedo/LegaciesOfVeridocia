package mb.fc.game.resource;

import mb.fc.game.sprite.NPCSprite;

public class NPCResource
{
	public static int NPC_ID_COUNTER = -1;

	public static NPCSprite getNPC(String animation, int textId, String name, boolean throughWall)
	{
		return new NPCSprite(animation, textId, NPC_ID_COUNTER--, name, throughWall);
	}

	public static void resetNPCIds()
	{
		NPC_ID_COUNTER = -1;
	}
}
