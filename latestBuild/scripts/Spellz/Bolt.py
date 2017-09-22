from mb.jython import JSpell
from mb.fc.game.battle.spell import KnownSpell
from org.python.modules import jarray
from java.lang import Math
from mb.fc.game import Range
from org.newdawn.slick import Color
from java.lang import String
from BattleEffect import BattleEffect
from ParticleEmitter import OrientedFlashParticleEmitter

import CommonFunctions

class Bolt(JSpell):    
    # This is where you set up all of the parameters for each of the spells    
    def init(self, id):
        self.setName("Bolt")       
        self.setCosts(jarray.array([4, 7, 13, 20], 'i'))
        self.setDamage(jarray.array([-13, -20, -40, -60], 'i'))
        self.setRange(jarray.array([Range.TWO_AND_LESS, Range.THREE_AND_LESS, Range.THREE_AND_LESS, Range.THREE_AND_LESS], Range))
        self.setArea(jarray.array([1, 1, 1, 1], 'i'))            
        self.setTargetsEnemy(1)
        self.setMaxLevel(4)     
        # A value of 0 means it does not loop. 1 means it does loop
        self.setLoops(1)   
        # The number specified here is an index into the SpellIcons image file starting from 0
        self.setSpellIconIndex(8)
        # Describes which BattleEffects should be applied for each level of the self. (Reference
        # the BattleEffect script for options). 
        # No effects = []
        # 1 effect = ["Effect"]
        # Many effects = ["Effect1", "Effect2"...]
        self.setEffects(jarray.array(["Shock"], String), 1) # Level 1 battle effects
        self.setEffects(jarray.array(["Shock"], String), 2) # Level 2 battle effects
        self.setEffects(jarray.array(["Shock"], String), 3) # Level 3 battle effects
        self.setEffects(jarray.array(["Shock"], String), 4) # Level 4 battle effects
        
        # This sets the BASELINE chance (potency)
        # No effects = []
        # 1 effect = [15]
        # Many effects = [15, 30...]
        self.setEffectChance(jarray.array([65], 'i'), 1) # Level 1 battle effects
        self.setEffectChance(jarray.array([75], 'i'), 2) # Level 2 battle effects
        self.setEffectChance(jarray.array([85], 'i'), 3) # Level 3 battle effects
        self.setEffectChance(jarray.array([95], 'i'), 4) # Level 4 battle effects
        
        # Set the effect level for each of the effects specified
        # For effects that don't have levels a value of 1 should be specified
        # No effects = []
        # 1 effect = [1]
        # Many effects = [1, 2...]
        self.setEffectLevel(jarray.array([1], 'i'), 1) # Level 1 battle effects
        self.setEffectLevel(jarray.array([2], 'i'), 2) # Level 2 battle effects
        self.setEffectLevel(jarray.array([3], 'i'), 3) # Level 3 battle effects
        self.setEffectLevel(jarray.array([4], 'i'), 4) # Level 4 battle effects
    
    def getEffectiveDamage(self, attacker, target, spellLevel):
        baseDamage = self.getDamage()[spellLevel]
        # Keep in mind that damaging spells will have a negative base damage value. 
        # The damage should never be above -1
        return int(Math.min(-1, baseDamage 
            + (baseDamage * attacker.getCurrentElecAffin() / 100.0) # Subtract the casters affinity (which adds damage)
            - (baseDamage * target.getCurrentElecAffin() / 100.0))) # Add the targets affinity (which reduces damage)
    
    def getBattleText(self, target, damage, mpDamage, attackerHPDamage, attackerMPDamage):
        return "Sparks fly at " + target.getName() + ", dealing " + `damage * -1` + " damage! "
        
    # Probably should move this algorithm to a central location
    def getExpGained(self, level, attacker, target):
        return CommonFunctions.defaultSpellExpGained(self, level, attacker, target)
            
    # This is at 30% opacity    
    def getSpellOverlayColor(self, level):
        return Color(150, 220, 255);
        
    def getSpellAnimationFile(self, level):
        return "BOLT"
    
    def getSpellRainAnimationFile(self, level):
        # return "lightning"
        return "BOLT"
    
    def getSpellRainAnimationName(self, level):
        return "level"+str(level)
    
    def getEmitter(self, level):
        return OrientedFlashParticleEmitter()
        
    def getSpellRainFrequency(self, level):
        return 10
