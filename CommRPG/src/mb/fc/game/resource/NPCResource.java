package mb.fc.game.resource;

import java.util.ArrayList;

import mb.fc.game.sprite.NPCSprite;
import mb.fc.game.text.Speech;

public class NPCResource 
{
	public static NPCSprite getNPC(int imageId, ArrayList<Speech> speeches)
	{
		switch (imageId)
		{
			case 0:
				return new NPCSprite("priest", speeches);
			case 1:
				return new NPCSprite("darkmerchant", speeches);
			case 2:
				return new NPCSprite("youngwomen", speeches);
		}
		return null;
	}
}
