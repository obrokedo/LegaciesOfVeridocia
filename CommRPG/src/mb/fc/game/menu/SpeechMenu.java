package mb.fc.game.menu;

import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.CinematicState;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Timer;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

public class SpeechMenu extends Menu
{
	public static final int NO_TRIGGER = -1;

	private int x;
	private int y = 60 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];
	private int width;
	private ArrayList<String> panelText;
	private int textIndex = 0;
	private int triggerId = -1;
	private Portrait portrait;
	protected boolean initialized = false;
	private boolean isAttackCinematic = false;

	private boolean textMoving = true;
	private int textMovingIndex = 0;
	private long waitUntil = -1;
	private String waitingOn = null;
	private Timer timer;
	private static final String CHAR_PAUSE = "{";
	private static final String CHAR_SOFT_STOP = "}";
	private static final String CHAR_HARD_STOP = "]";
	private static final String CHAR_LINE_BREAK = "[";
	private static final String CHAR_NEXT_CIN = "|";

	/**
	 * Constructor to create a SpeechMenu to be displayed in an Attack Cinematic
	 *
	 * @param text the text that should be displayed in the speech menu
	 * @param gc the graphics container that the menu will be displayed in
	 */
	public SpeechMenu(String text, FCGameContainer gc)
	{
		this(text, gc, NO_TRIGGER, null, null);
		y = 0;
		initialized = true;
		this.isAttackCinematic = true;
	}

	/**
	 * Constructor to create a SpeechMenu with no portrait or triggers, this should
	 * NOT be used in Attack Cinematics
	 *
	 * @param text the text that should be displayed in the speech menu
	 * @param stateInfo the stateinfo that resources should be retrieved from
	 */
	public SpeechMenu(String text, StateInfo stateInfo)
	{
		this(text, stateInfo.getGc(), NO_TRIGGER, null, stateInfo);
	}

	public SpeechMenu(String text, FCGameContainer gc, int triggerId,
			Portrait portrait, StateInfo stateInfo)
	{
		super(Panel.PANEL_SPEECH);
		width = gc.getWidth() - 100 - gc.getDisplayPaddingX() * 2;
		x = 50 + gc.getDisplayPaddingX();

		int maxTextWidth = width;
		int spaceWidth = SPEECH_FONT.getWidth("_");
		String[] splitText = text.split(" ");
		int currentLineWidth = 0;
		String currentLine = "";

		panelText = new ArrayList<String>();

		for (int i = 0; i < splitText.length; i++)
		{
			int wordWidth = SPEECH_FONT.getWidth(splitText[i]);

			if (wordWidth + currentLineWidth <= maxTextWidth)
			{
				boolean lineBreak = false;
				if (splitText[i].contains(CHAR_LINE_BREAK))
					lineBreak = true;

				currentLine += " " + splitText[i].replace(CHAR_LINE_BREAK, "");
				currentLineWidth += wordWidth + spaceWidth;

				if (lineBreak)
				{
					currentLineWidth = 0;
					panelText.add(currentLine);
					currentLine = "";
				}
			}
			else
			{
				i--;
				currentLineWidth = 0;
				panelText.add(currentLine);
				currentLine = "";
			}
		}

		if (currentLineWidth > 0)
			panelText.add(currentLine);

		this.triggerId = triggerId;

		if (portrait != null)
		{
			this.portrait = portrait;
			this.portrait.setTalking(true);
		}
		else
			this.portrait = null;

		timer = new Timer(16);
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics)
	{
		int posY = 2;
		if (isAttackCinematic)
			posY = 1;

		Panel.drawPanelBox(x, gc.getHeight() - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (posY + 1) * 20 + y, width , CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (posY + 1) * 20 - 5, graphics);

		if (!initialized)
			return;

		graphics.setFont(SPEECH_FONT);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		// graphics.setFont(ufont);

		for (int i = Math.max(0, textIndex - posY); i <= textIndex; i++)
		{
			graphics.drawString((i == textIndex ? panelText.get(i).substring(0, textMovingIndex) : panelText.get(i)), x + 15,
					gc.getHeight() - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (posY + 1) * 20 + 10 +  (i - textIndex + (textIndex >= posY ? posY : textIndex)) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 15 - (posY == 1 ? 5 : 0));
		}

		if (portrait != null)
		{
			portrait.render(x, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 12, graphics);
			// graphics.drawImage(portrait, x, 25);
		}
	}

	@Override
	public MenuUpdate update(long delta, StateInfo stateInfo) {
		super.update(delta, stateInfo);

		timer.update(delta);

		if (portrait != null)
			portrait.update(delta);

		while (timer.perform())
		{
			if (!initialized)
			{
				if (y <= 0)
				{
					initialized = true;
				}
				else
					y = Math.max(y - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 8, 0);
			}

			for (int i = 0; i < (CinematicState.cinematicSpeed > 1 ? CinematicState.cinematicSpeed : 1); i++)
			{
				if (textMoving)
				{
					if (textMovingIndex + 1 > panelText.get(textIndex).length())
					{
						if (textIndex + 1 < panelText.size())
						{
							textMovingIndex = panelText.get(textIndex).length();
							textMovingIndex = 0;
							textIndex++;
						}
						else
						{
							Log.debug("Speech Menu: Send Trigger " + triggerId);
							if (triggerId != NO_TRIGGER)
								stateInfo.getResourceManager().getTriggerEventById(triggerId).perform(stateInfo);
							return MenuUpdate.MENU_CLOSE;
						}
						// textMoving = false;
					}
					else
					{
						String nextLetter = panelText.get(textIndex).substring(textMovingIndex, textMovingIndex + 1);
						if (nextLetter.equalsIgnoreCase(CHAR_HARD_STOP))
						{
							textMoving = false;
							if (portrait != null)
								portrait.setTalking(false);
							waitingOn = CHAR_HARD_STOP;
						}
						else if (nextLetter.equalsIgnoreCase(CHAR_SOFT_STOP))
						{
							textMoving = false;
							if (portrait != null)
								portrait.setTalking(false);

							String[] softSplit = panelText.get(textIndex).substring(textMovingIndex).split(" ");

							if (softSplit[0].replaceFirst("[0-9]", "").length() != softSplit[0].length())
							{
								waitUntil = System.currentTimeMillis() + Integer.parseInt(softSplit[0].substring(1));
								waitingOn = softSplit[0];
							}
							else
							{
								waitUntil = System.currentTimeMillis() + 2500;
								waitingOn = CHAR_SOFT_STOP;
							}
						}
						else if (nextLetter.equalsIgnoreCase(CHAR_PAUSE))
						{
							textMoving = false;
							if (portrait != null)
								portrait.setTalking(false);
							waitUntil = System.currentTimeMillis() + 400;
							waitingOn = CHAR_PAUSE;
						}
						else if (nextLetter.equalsIgnoreCase(CHAR_NEXT_CIN))
						{
							panelText.set(textIndex, panelText.get(textIndex).replaceFirst("\\" + CHAR_NEXT_CIN, ""));
							return MenuUpdate.MENU_NEXT_ACTION;
						}

						if (textMoving)
							textMovingIndex += 1;
						else
							panelText.set(textIndex, panelText.get(textIndex).replaceFirst("\\" + waitingOn, ""));
						// This is a bit of a kludge, when we are in the attack cinematic we want the text to scroll but we don't
						// want the "talking" sound effect. Really this should probably be a different boolean rather then just
						// checking to see if the state info is not null
						if (textMovingIndex % 6 == 0 && !isAttackCinematic)
							stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "speechblip", .15f, false));
					}
				}
			}

			if (waitUntil != -1 && waitUntil <= System.currentTimeMillis())
			{
				textMoving = true;
				if (portrait != null)
					portrait.setTalking(true);
				waitUntil = -1;
			}
		}

		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo)
	{
		if (!initialized)
			return MenuUpdate.MENU_NO_ACTION;

		if (input.isKeyDown(KeyMapping.BUTTON_3))
		{
			if (waitingOn != null)
			{
				waitingOn = null;
				waitUntil = -1;
				textMoving = true;
				if (portrait != null)
					portrait.setTalking(true);
			}
		}

		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public boolean makeAddAndRemoveSounds()
	{
		return true;
	}
}
