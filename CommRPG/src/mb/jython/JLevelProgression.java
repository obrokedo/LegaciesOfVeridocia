package mb.jython;

import mb.fc.game.constants.AttributeStrength;
import mb.fc.game.sprite.CombatSprite;

public interface JLevelProgression {
	public float[] getProgressArray(String progressionType, boolean promoted);

	public abstract String[] getStandardStatProgressionTypeList();
	public abstract String[] getBodyMindProgressionTypeList();
	public abstract int getBaseBattleStat(AttributeStrength attributeStrength, CombatSprite combatSprite);
	public abstract int getBaseBodyMindStat(AttributeStrength attributeStrength, CombatSprite combatSprite);
	public abstract int getLevelUpBattleStat(AttributeStrength attributeStrength, CombatSprite heroSprite,
			int newLevel, boolean promoted, int currentValue);
	public abstract int getLevelUpBodyMindStat(String progressionType, CombatSprite heroSprite,
			int newLevel, boolean promoted);
	public abstract String levelUpHero(CombatSprite heroSprite);
}
