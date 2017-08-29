package mb.jython;

public interface JMenuConfiguration {
	public String getPriestSavingText();
	
	public String getPriestNoOneToCureText();
	public int getPriestCureCost(String[] battleEffectNames, int[] battleEffectLevels);
	public String getPriestSelectSomeoneToCureText(String targetName, String[] ailments, int cost);
	public String getPriestNotEnoughGoldToCureText();
	public String getPriestTargetHasBeenCuredText(String targetName);
	
	public String getPriestNoOneToPromoteText();
	public String getPriestSelectSomeoneToPromoteText(String targetName, String targetClass, String itemUsed);
	public String getPriestTargetHasBeenPromotedText(String targetName, String targetClass, String itemUsed);
	
	public String getPriestNoOneToResurrectText();
	public int getPriestResurrectCost(int level, boolean promoted);
	public String getPriestSelectSomeoneToResurrectText(String targetName, int cost);
	public String getPriestNotEnoughGoldToResurrectText();
	public String getPriestTargetHasBeenResurrectedText(String targetName);
	
	public String getPriestMenuClosedText();
}
