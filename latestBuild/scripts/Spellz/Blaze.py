from tactical.game.battle.spell import SpellDefinition
from org.python.modules import jarray
from java.lang import Math
from tactical.game import Range
from org.newdawn.slick import Color
from java.lang import String
from BattleEffect import BattleEffect
from ParticleEmitter import RainParticleEmitter
from ParticleEmitter import RandomHorizontalParticleEmitter

import CommonFunctions

class Blaze(SpellDefinition):    
    # This is where you set up all of the parameters for each of the spells    
    def __init__(self):
        # The spell name as it should appear to players
        self.setName("Blaze")
        # The cost in MP per level of the spell, where the first index is in the array is for first level
        # second index is for second level, etc. The final attribute outside of the brackets describes the
        # type of values contained within the array. In this case 'i' = integer
        #
        # 'i' = integer
        # "TEXT" = String
        # Range = Range
        #    SELF_ONLY,
        #    ONE_ONLY,
        #    TWO_AND_LESS,
        #    THREE_AND_LESS,
        #    TWO_NO_ONE,
        #    THREE_NO_ONE,
        #    THREE_NO_ONE_OR_TWO;
        self.setCosts(jarray.array([2, 5, 8, 12], 'i'))
        # This is the amount of damage each level of the spell will do, negative values are DEALING damage,
        # positive values HEAL for this much
        self.setDamage(jarray.array([-6, -8, -15, -40], 'i'))
        # See above for range: Describes the range of the spell for each spell level.             
        self.setRange(jarray.array([Range.TWO_AND_LESS, Range.TWO_AND_LESS, Range.TWO_AND_LESS, Range.TWO_AND_LESS], Range))
        # Sets the area of the spell per spell level
        # 1 = X X X
        #     X O X
        #     X X X
        #
        # 2 = X O X
        #     O O O
        #     X O X
        # 3... etc
        # 0 = All targets on the battlefield 
        self.setArea(jarray.array([1, 2, 2, 1], 'i'))
        # Whether this spell targets the casters enemies or allies
        # 0 = Targets allies (this means the CASTERS allies)
        # 1 = Targets enemies (this means the CASTERS enemies)
        # 2 = Targets anyone       
        self.setTargetsEnemy(1)
        # The maximum level of spell that can be learned
        self.setMaxLevel(4)
        # Indicates whether the spells animation should loop     
        # A value of 0 means it does not loop. 1 means it does loop
        self.setLoops(1)
        # The number specified here is an index into the SpellIcons image file starting from 0            
        self.setSpellIconIndex(1)
        # Describes which BattleEffects should be applied for each level of the self. (Reference
        # the BattleEffect script for options). 
        # No effects = []
        # 1 effect = ["Effect"]
        # Many effects = ["Effect1", "Effect2"...]
        self.setEffects(jarray.array(["Burn"], String), 1) # Level 1 battle effects
        self.setEffects(jarray.array(["Burn"], String), 2) # Level 2 battle effects
        self.setEffects(jarray.array(["Burn"], String), 3) # Level 3 battle effects
        self.setEffects(jarray.array(["Burn"], String), 4) # Level 4 battle effects
        
        # Set the effect level for each of the effects specified
        # For effects that don't have levels a value of 1 should be specified
        # No effects = []
        # 1 effect = [1]
        # Many effects = [1, 2...]
        self.setEffectLevel(jarray.array([1], 'i'), 1) # Level 1 battle effects
        self.setEffectLevel(jarray.array([2], 'i'), 2) # Level 2 battle effects
        self.setEffectLevel(jarray.array([3], 'i'), 3) # Level 3 battle effects
        self.setEffectLevel(jarray.array([4], 'i'), 4) # Level 4 battle effects
    
    # Affin methods are
    #----------------------
    # getCurrentFireAffin()
    # getCurrentElecAffin()
    # getCurrentColdAffin()
    # getCurrentDarkAffin()
    # getCurrentWaterAffin()
    # getCurrentEarthAffin()
    # getCurrentWindAffin()
    # getCurrentLightAffin()
    def getEffectiveDamage(self, attacker, target, spellLevel):
        baseDamage = self.getDamage()[spellLevel]
        # Keep in mind that damaging spells will have a negative base damage value. 
        # The damage should never be above -1
        return int(Math.min(-1, baseDamage 
            + (baseDamage * attacker.getCurrentFireAffin() / 100.0) # Subtract the casters Affin (which adds damage)
            - (baseDamage * target.getCurrentFireAffin() / 100.0))) # Add the targets Affin (which reduces damage)
    
    def getBattleText(self, target, damage, mpDamage, attackerHPDamage, attackerMPDamage):
        return "Flame engulfs " + target.getName() + "'s body dealing " + `damage * -1` + " damage!"
        
    def getExpGained(self, level, attacker, target):
        # Call the common method
        return CommonFunctions.defaultSpellExpGained(self, level, attacker, target)
            
    # This is at 30% opacity    
    def getSpellOverlayColor(self, level):
        return Color(255, 0, 0)
    
    def getSpellAnimationFile(self, level):
        return "Blaze"
    
    # To turn OFF rain animation just return None here
    # return None
    def getSpellRainAnimationFile(self, level):
        # return "Flame"
        return "Blaze"
    
    def getSpellRainAnimationName(self, level):
        return str(level)
        
    def getSpellRainFrequency(self, level):
        return 100*level
    
    def getEmitter(self, level):
        # You could create a emitter with sounds here
        # For example, new particle plays "fall", particle end plays "blast"
        soundTime = 0
        if level == 1:
            soundTime = 240
        elif level == 2:
            soundTime = 300
        elif level == 3:
            soundTime = 375
        elif level == 4:
            soundTime = 375
            
        return RandomHorizontalParticleEmitter(None, "Explosion1", soundTime)
        #return RandomHorizontalParticleEmitter()
        #return RainParticleEmitter()
        
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
        return level * (6 - self.getArea()[level]) + caster.getCurrentMind() + caster.getCurrentFireAffin()
