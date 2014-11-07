package mb.jython;

import mb.fc.game.sprite.CombatSprite;

/**
 * Interface to call the MusicScript python methods that will determine
 * which sounds and music should be played for given situations
 *
 * @see /scripts/MusicScript.py
 *
 * @author Broked
 *
 */
public interface JMusicSelector {
	public String getAttackMusic(CombatSprite attacker, boolean targetsAllies);

	public String getAttackHitSoundEffect(boolean isHero, boolean isCritical, boolean isDodge, int weaponType);

	public String getSpellHitSoundEffect(boolean isHero, boolean isTargetAlly, String spellName);

	public String getCastSpellSoundEffect(boolean isHero, String spellName);

	public String getUseItemSoundEffect(boolean isHero, boolean isTargetAlly, String itemName);

	public String getLevelUpSoundEffect(CombatSprite hero);

	public String getInvalidActionSoundEffect();

	public String getMenuAddedSoundEffect();

	public String getMenuRemovedSoundEffect();
}
