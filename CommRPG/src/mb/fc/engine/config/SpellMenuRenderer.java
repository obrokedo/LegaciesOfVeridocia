package mb.fc.engine.config;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.loading.ResourceManager;

public interface SpellMenuRenderer {
	public void render(String spellName, CombatSprite spriteCastingSpell, ResourceManager fcrm, 
			boolean spellHasBeenSelected, int selectedLevel, 
			KnownSpell selectedSpell, StateInfo stateInfo, Graphics graphics, Color forefrontColor);
	
	public void spellLevelChanged(int spellLevel);
	
	public void update(long delta);
}
