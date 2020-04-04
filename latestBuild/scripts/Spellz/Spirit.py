from tactical.game.battle.spell import SpellDefinition
from org.python.modules import jarray
from java.lang import Math
from tactical.game import Range
from org.newdawn.slick import Color
from java.lang import String
from BattleEffect import BattleEffect

import CommonFunctions

class Spirit(SpellDefinition):    
    # This is where you set up all of the parameters for each of the spells    
    def __init__(self):
        # This value should appear in the array returned from the spell list above
        self.setName("Spirit")       
        self.setCosts(jarray.array([3, 6, 20, 20], 'i'))
        self.setDamage(jarray.array([0, 0, 0, 0], 'i'))
        self.setRange(jarray.array([Range.TWO_AND_LESS, Range.THREE_AND_LESS, Range.THREE_AND_LESS, Range.SELF_ONLY], Range))
        self.setArea(jarray.array([1, 1, 2, 3], 'i'))            
        self.setTargetsEnemy(0)
        self.setMaxLevel(4)     
        # A value of 0 means it does not loop. 1 means it does loop
        self.setLoops(1)   
        # The number specified here is an index into the SpellIcons image file starting from 0
        self.setSpellIconIndex(22)
        self.setEffects(jarray.array(["ResistanceUp", "MindUp"], String), 1) # Level 1 battle effects
        self.setEffects(jarray.array(["ResistanceUp", "MindUp"], String), 2) # Level 2 battle effects
        self.setEffects(jarray.array(["ResistanceUp", "MindUp"], String), 3) # Level 3 battle effects
        self.setEffects(jarray.array(["ResistanceUp", "MindUp"], String), 4) # Level 4 battle effects
        
        # Set the effect level for each of the effects specified
        # For effects that don't have levels a value of 1 should be specified
        # No effects = []
        # 1 effect = [1]
        # Many effects = [1, 2...]
        self.setEffectLevel(jarray.array([1,1], 'i'), 1) # Level 1 battle effects
        self.setEffectLevel(jarray.array([2,2], 'i'), 2) # Level 2 battle effects
        self.setEffectLevel(jarray.array([3,3], 'i'), 3) # Level 3 battle effects
        self.setEffectLevel(jarray.array([4,4], 'i'), 4) # Level 4 battle effects
    
    def getEffectiveDamage(self, attacker, target, spellLevel):
        baseDamage = self.getDamage()[spellLevel]
        return int(baseDamage)
    
    def getBattleText(self, target, damage, mpDamage, attackerHPDamage, attackerMPDamage):
        return "Spirit washes all over "
        
    def getExpGained(self, level, attacker, target):
        #if self.name == "Heal":
        #    if target.getCurrentHP() == target.getMaxHP():
        #        return int (0);
        #    else:
        #        return int(Math.max(10, Math.min(target.getMaxHP() - target.getCurrentHP(), 
        #                                         self.getDamage()[level]) * 1.0/ target.getMaxHP() * 25));
        #elif self.name == "Aura":
        #    return int(12 * Math.max(0, (target.getMaxHP() - target.getCurrentHP()) * 1.0 / self.getDamage()[level]))
        #else:
        #    return -1
        return CommonFunctions.defaultSpellExpGained(self, level, attacker, target)
            
    # This is at 30% opacity    
    def getSpellOverlayColor(self, level):
        return Color(0, 0, 0);

    def getSpellAnimationFile(self, level):
        return "Spirit"
    
    def getEffectChance(self, caster, level):
        # Return the base chance for an effect to take place
        # Likely useful values...
        # caster.getCurrentMind()
        # caster.getCurrentBody()
        # caster.getCurrentFireAffin()
        # caster.getCurrentColdAffin()
        # caster.getCurrentElecAffin()
        # caster.getCurrentDarkAffin()
        # caster.getCurrentWaterAffin()
        # caster.getCurrentWindAffin()
        # caster.getCurrentEarthAffin()
        # caster.getCurrentLightAffin()
        return 100 #level * (6 - self.getArea()[level]) + caster.getCurrentMind()
