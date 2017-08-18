package mb.fc.game.menu;

import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.CinematicState;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Timer;
import mb.fc.game.constants.TextSpecialCharacters;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.listener.MenuListener;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.utils.StringUtils;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

public class SpeechMenu extends Menu
{
	public static final int NO_TRIGGER = -1;

	private int x;
	private int y = 60;
	private int width;
	protected ArrayList<String> panelText;
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


	/**
	 * Constructor to create a SpeechMenu to be displayed in an Attack Cinematic
	 *
	 * @param text the text that should be displayed in the speech menu
	 * @param gc the graphics container that the menu will be displayed in
	 */
	public SpeechMenu(String text, PaddedGameContainer gc)
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
		this(text, stateInfo.getFCGameContainer(), NO_TRIGGER, null, stateInfo);
	}

	public SpeechMenu(String text, PaddedGameContainer gc, int triggerId,
			Portrait portrait, StateInfo stateInfo)
	{
		this(text, gc, triggerId, portrait, stateInfo, null);
	}

	public SpeechMenu(String text, PaddedGameContainer gc, int triggerId,
			Portrait portrait, StateInfo stateInfo, MenuListener listener)
	{
		super(PanelType.PANEL_SPEECH);
		this.listener = listener;
		width = CommRPG.GAME_SCREEN_SIZE.width - 30;
		x = 15;
		
		text = TextSpecialCharacters.replaceControlTagsWithInternalValues(text);

		int maxTextWidth = width - 10;
		int spaceWidth = StringUtils.getStringWidth("_", SPEECH_FONT);
		String[] splitText = text.split(" ");
		int currentLineWidth = 0;
		String currentLine = "";

		panelText = new ArrayList<String>();

		for (int i = 0; i < splitText.length; i++)
		{
			int wordWidth = StringUtils.getStringWidth(splitText[i], SPEECH_FONT);

			if (wordWidth + currentLineWidth <= maxTextWidth)
			{
				boolean lineBreak = false;
				if (splitText[i].contains(TextSpecialCharacters.INTERNAL_LINE_BREAK))
					lineBreak = true;

				currentLine += " " + splitText[i].replace(TextSpecialCharacters.INTERNAL_LINE_BREAK, "");
				currentLineWidth += wordWidth + spaceWidth;

				if (lineBreak)
				{
					currentLineWidth = 0;
					panelText.add(currentLine.trim());
					currentLine = "";
				}
			}
			else
			{
				i--;
				currentLineWidth = 0;
				panelText.add(currentLine.trim());
				currentLine = "";
			}
		}

		if (currentLineWidth > 0)
			panelText.add(currentLine.trim());

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
	public void render(PaddedGameContainer gc, Graphics graphics)
	{
		int posY = 2;
		if (isAttackCinematic)
			posY = 1;

		Panel.drawPanelBox(x, CommRPG.GAME_SCREEN_SIZE.height - (posY + 1) * 20 + y, width, (posY + 1) * (20 + (posY == 1 ? 1 : 0)) - 5, graphics);

		if (!initialized)
			return;

		graphics.setFont(SPEECH_FONT);
		graphics.setColor(Panel.COLOR_FOREFRONT);

		for (int i = Math.max(0, textIndex - posY); i <= textIndex; i++)
		{
			StringUtils.drawString((i == textIndex ? panelText.get(i).substring(0, textMovingIndex) : panelText.get(i)), x + 2,
					CommRPG.GAME_SCREEN_SIZE.height - (posY + 1) * 24 + 15 + (i - textIndex + (textIndex >= posY ? posY : textIndex)) * 15 - (posY == 1 ? 5 : 0), graphics);
		}

		if (portrait != null)
		{
			portrait.render(x, y + 12, graphics);
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
					y = Math.max(y - 8, 0);
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
						if (nextLetter.equalsIgnoreCase(TextSpecialCharacters.INTERNAL_HARD_STOP))
						{
							textMoving = false;
							if (portrait != null)
								portrait.setTalking(false);
							waitingOn = TextSpecialCharacters.INTERNAL_HARD_STOP;
						}
						else if (nextLetter.equalsIgnoreCase(TextSpecialCharacters.INTERNAL_SOFT_STOP))
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
								waitingOn = TextSpecialCharacters.INTERNAL_SOFT_STOP;
							}
						}
						else if (nextLetter.equalsIgnoreCase(TextSpecialCharacters.INTERNAL_CHAR_PAUSE))
						{
							textMoving = false;
							if (portrait != null)
								portrait.setTalking(false);
							waitUntil = System.currentTimeMillis() + 400;
							waitingOn = TextSpecialCharacters.INTERNAL_CHAR_PAUSE;
						}
						else if (nextLetter.equalsIgnoreCase(TextSpecialCharacters.INTERNAL_NEXT_CIN))
						{
							panelText.set(textIndex, panelText.get(textIndex).replaceFirst("\\" + TextSpecialCharacters.INTERNAL_NEXT_CIN, ""));
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

		if (input.isKeyDown(KeyMapping.BUTTON_3) || CommRPG.TEST_MODE_ENABLED)
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
