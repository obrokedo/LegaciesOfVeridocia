from tactical.game.battle.spell import SpellDefinition
from org.python.modules import jarray
from java.lang import Math
from tactical.game import Range
from org.newdawn.slick import Color
from java.lang import String
from BattleEffect import BattleEffect
from ParticleEmitter import OrientedFlashParticleEmitter, RandomHorizontalParticleEmitter

import CommonFunctions

class Flare(SpellDefinition):    
    # This is where you set up all of the parameters for each of the spells    
    def __init__(self):
        self.setName("Flare")       
        self.setCosts(jarray.array([15, 25], 'i'))
        self.setDamage(jarray.array([-22, -30], 'i'))
        self.setRange(jarray.array([Range.TWO_AND_LESS, Range.THREE_AND_LESS], Range))
        self.setArea(jarray.array([3, 3], 'i'))            
        self.setTargetsEnemy(1)
        self.setMaxLevel(2)     
        # A value of 0 means it does not loop. 1 means it does loop
        self.setLoops(1)   
        # The number specified here is an index into the SpellIcons image file starting from 0
        self.setSpellIconIndex(18)
        # Describes which BattleEffects should be applied for each level of the self. (Reference
        # the BattleEffect script for options). 
        # No effects = []
        # 1 effect = ["Effect"]
        # Many effects = ["Effect1", "Effect2"...]
        self.setEffects(jarray.array(["Blind"], String), 1) # Level 1 battle effects
        self.setEffects(jarray.array(["Blind"], String), 2) # Level 2 battle effects
        
        # Set the effect level for each of the effects specified
        # For effects that don't have levels a value of 1 should be specified
        # No effects = []
        # 1 effect = [1]
        # Many effects = [1, 2...]
        self.setEffectLevel(jarray.array([1], 'i'), 1) # Level 1 battle effects
        self.setEffectLevel(jarray.array([2], 'i'), 2) # Level 2 battle effects
    
    def getEffectiveDamage(self, attacker, target, spellLevel):
        baseDamage = self.getDamage()[spellLevel]
        # Keep in mind that damaging spells will have a negative base damage value. 
        # The damage should never be above -1
        return int(Math.min(-1, baseDamage 
            + (baseDamage * attacker.getCurrentElecAffin() / 100.0) # Subtract the casters Affin (which adds damage)
            - (baseDamage * target.getCurrentElecAffin() / 100.0))) # Add the targets Affin (which reduces damage)
    
    def getBattleText(self, target, damage, mpDamage, attackerHPDamage, attackerMPDamage):
        return "Flare " + target.getName() + ", dealing " + `damage * -1` + " damage! "
        
    # Probably should move this algorithm to a central location
    def getExpGained(self, level, attacker, target):
        return CommonFunctions.defaultSpellExpGained(self, level, attacker, target)
            
    # This is at 30% opacity    
    def getSpellOverlayColor(self, level):
        #return Color(150, 220, 255);
        return Color(0, 0, 0);
        
    def getSpellAnimationFile(self, level):
        return "Flare"
    
    def getSpellRainAnimationFile(self, level):
        # return "lightning"
        return "Flare"
    
    def getSpellRainAnimationName(self, level):
        return str(level)
    
    def getEmitter(self, level):
        # You could create a emitter with sounds here
        # For example, new particle plays "fall", particle end plays "blast"
        delay = 0
        soundTime = 0
        if level == 1:
            soundTime = 499
        elif level == 2:
            soundTime = 499
        elif level == 3:
            soundTime = 499
        elif level == 4:
            soundTime = 499
            
        return RandomHorizontalParticleEmitter("Thunder", None , soundTime)
        
    def getSpellRainFrequency(self, level):
        return 10
    
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
        return level * (6 - self.getArea()[level]) + caster.getCurrentMind() + caster.getCurrentElecAffin()
