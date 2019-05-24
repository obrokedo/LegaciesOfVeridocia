from tactical.game.battle.spell import SpellDefinition
from org.python.modules import jarray
from java.lang import Math
from tactical.game import Range
from org.newdawn.slick import Color
from java.lang import String
from BattleEffect import BattleEffect

import CommonFunctions

class Lightning(SpellDefinition):    
    # This is where you set up all of the parameters for each of the spells    
    def __init__(self):
        self.setName("Lightning")       
        self.setCosts(jarray.array([17, 26, 32, 32], 'i'))
        self.setDamage(jarray.array([-25, -30, -37, -47], 'i'))
        self.setRange(jarray.array([Range.THREE_AND_LESS, Range.THREE_AND_LESS, Range.THREE_AND_LESS, Range.THREE_AND_LESS], Range))
        self.setArea(jarray.array([2, 3, 3, 2], 'i'))            
        self.setTargetsEnemy(1)
        self.setMaxLevel(4)     
        # A value of 0 means it does not loop. 1 means it does loop
        self.setLoops(1)   
        # The number specified here is an index into the SpellIcons image file starting from 0
        self.setSpellIconIndex(4)

        self.setEffects(jarray.array(["Shock"], String), 1) # Level 1 battle effects
        self.setEffects(jarray.array(["Shock"], String), 2) # Level 2 battle effects
        self.setEffects(jarray.array(["Shock"], String), 3) # Level 3 battle effects
        self.setEffects(jarray.array(["Shock"], String), 4) # Level 4 battle effects
        self.setEffectLevel(jarray.array([1], 'i'), 1) # Level 1 battle effects
        self.setEffectLevel(jarray.array([2], 'i'), 2) # Level 2 battle effects
        self.setEffectLevel(jarray.array([3], 'i'), 3) # Level 3 battle effects
        self.setEffectLevel(jarray.array([4], 'i'), 4) # Level 4 battle effects
    
    def getEffectiveDamage(self, attacker, target, spellLevel):
        baseDamage = self.getDamage()[spellLevel]
        return int(baseDamage)
    
    def getBattleText(self, target, damage, mpDamage, attackerHPDamage, attackerMPDamage):
        return "Lightning thigns happened"
        
    def getExpGained(self, level, attacker, target):
        return CommonFunctions.defaultSpellExpGained(self, level, attacker, target)
            
    # This is at 30% opacity    
    def getSpellOverlayColor(self, level):
        return Color(0, 0, 0);

    def getSpellAnimationFile(self, level):
        return "Blaze"
