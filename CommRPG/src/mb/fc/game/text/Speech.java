package mb.fc.game.text;

import java.util.ArrayList;

import mb.fc.engine.message.SpeechBundleMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.menu.Portrait;
import mb.fc.utils.StringUtils;

public class Speech
{
	private String message;
	private String[] requires;
	private String[] excludes;
	private int triggerId;
	protected int heroPortrait;
	protected int enemyPortrait;
	protected String spriteAnimsName;

	public Speech(String message, String[] requires, String[] excludes, int triggerId,
			int heroPortrait, int enemyPortrait, String spriteAnimsName) {
		super();
		this.message = message;
		this.requires = requires;
		this.excludes = excludes;
		this.triggerId = triggerId;
		this.heroPortrait = heroPortrait;
		this.enemyPortrait = enemyPortrait;
		this.spriteAnimsName = spriteAnimsName;
	}

	public String getMessage() {
		return message;
	}

	public String[] getRequires() {
		return requires;
	}

	public String[] getExcludes() {
		return excludes;
	}

	public int getTriggerId() {
		return triggerId;
	}

	public Portrait getPortrait(StateInfo stateInfo) {
		return Portrait.getPortrait(heroPortrait, enemyPortrait, spriteAnimsName, stateInfo);
	}
	
	public static boolean showFirstSpeechMeetsReqs(int textId, StateInfo stateInfo) {
		ArrayList<Speech> speeches = stateInfo.getResourceManager().getSpeechesById(textId);
		if (speeches != null) {
			SPEECHLOOP: for (int speechIndex = 0; speechIndex < speeches.size(); speechIndex++)
			{
				Speech s = speeches.get(speechIndex);
				
				// Check to see if this mesage meets all required quests
				if (s.getRequires() != null && s.getRequires().length > 0)
				{
					for (String quest : s.getRequires())
					{
						if (StringUtils.isNotEmpty(quest) && !stateInfo.isQuestComplete(quest))
							continue SPEECHLOOP;
					}
				}
	
				// Check to see if the excludes quests have been completed, if so
				// then we can't use this message
				if (s.getExcludes() != null && s.getExcludes().length > 0)
				{
					for (String quest : s.getExcludes())
					{
						if (StringUtils.isNotEmpty(quest) && stateInfo.isQuestComplete(quest))
							continue SPEECHLOOP;
					}
				}
				
				stateInfo.sendMessage(new SpeechBundleMessage(textId, speechIndex));
				return true;
			}
		}
		return false;
	}
	
	public boolean hasMoreSpeech() {
		return false;
	}
	
	public void initialize() {
		
	}
}
