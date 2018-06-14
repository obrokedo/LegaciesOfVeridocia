from tactical.game.battle.spell import SpellDefinition
from org.python.modules import jarray
from java.lang import Math
from tactical.game import Range
from org.newdawn.slick import Color
from java.lang import String
from BattleEffect import BattleEffect

class Heal(SpellDefinition):    
    # This is where you set up all of the parameters for each of the spells    
    def __init__(self):
        self.setName("Heal")              
        self.setCosts(jarray.array([3, 5, 9, 12], 'i'))
        self.setDamage(jarray.array([15, 22, 36, 120], 'i'))
        self.setRange(jarray.array([Range.ONE_ONLY, Range.TWO_AND_LESS, Range.THREE_AND_LESS, Range.ONE_ONLY], Range))
        self.setArea(jarray.array([1, 1, 1, 1], 'i'))            
        self.setTargetsEnemy(0)
        self.setMaxLevel(4)
        
        # Describes which BattleEffects should be applied for each level of the self. (Reference
        # the BattleEffect script for options). 
        # No effects = []
        # 1 effect = ["Effect"]
        # Many effects = ["Effect1", "Effect2"...]
        self.setEffects(jarray.array(["Heal"], String), 1) # Level 1 battle effects
        self.setEffects(jarray.array(["Heal"], String), 2) # Level 2 battle effects
        self.setEffects(jarray.array(["Heal"], String), 3) # Level 3 battle effects
        self.setEffects(jarray.array(["Heal"], String), 4) # Level 4 battle effects
        
        # Set the effect level for each of the effects specified
        # For effects that don't have levels a value of 1 should be specified
        # No effects = []
        # 1 effect = [1]
        # Many effects = [1, 2...]
        self.setEffectLevel(jarray.array([1], 'i'), 1) # Level 1 battle effects
        self.setEffectLevel(jarray.array([1], 'i'), 2) # Level 2 battle effects
        self.setEffectLevel(jarray.array([1], 'i'), 3) # Level 3 battle effects
        self.setEffectLevel(jarray.array([1], 'i'), 4) # Level 4 battle effects
        # A value of 0 means it does not loop. 1 means it does loop
        self.setLoops(1)
        # The number specified here is an index into the SpellIcons image file starting from 0   
        self.setSpellIconIndex(0)
    
    def getEffectiveDamage(self, attacker, target, spellLevel):
        bleeding = False
        baseDamage = self.getDamage()[spellLevel]
        # Check to see if the target is bleeding, if so then
        # this will just remove that condition, not actually heal
        battleEffects = target.getBattleEffects();
        for i in range(battleEffects.size()):
            if "Bleed" == battleEffects.get(i).getName():
                bleeding = True
                break;
    
        if bleeding:
            baseDamage = 0
        else :
            # Keep in mind that healing spells will have a positive base damage value. 
            baseDamage = Math.min(target.getMaxHP() - target.getCurrentHP(), # Maximum value is the amount of hp the target is missing 
                                  baseDamage + (baseDamage * attacker.getCurrentWaterAffin() / 100.0)) # Add the casters affinity (increasing healing)
        return int(baseDamage)
    
    def getBattleText(self, target, damage, mpDamage, attackerHPDamage, attackerMPDamage):
        # If the target is bleeding we won't actually heal any hp, just cure the bleeding
        # The text should reflect that. If the bleed effect is found, just return a blank
        # string as the heal effect will output the effec ended string. Otherwise
        # return the healing text
        battleEffects = target.getBattleEffects();
        for i in range(battleEffects.size()):
            if "Bleed" == battleEffects.get(i).getName():
                return ""
        # 
        return target.getName() + "'s wounds are healed. " + target.getName() + " recovers " + `damage` + " HP!"
        
    def getExpGained(self, level, attacker, target):
        if target.getCurrentHP() == target.getMaxHP():
            return int(0);
        else:
            return int(Math.max(10, Math.min(target.getMaxHP() - target.getCurrentHP(), self.getDamage()[level]) * 1.0/ target.getMaxHP() * 25));
            
    # This is at 30% opacity    
    def getSpellOverlayColor(self, level):
        return Color(0, 0, 0);
        
    def getSpellAnimationFile(self, level):
        return "Heal"
    
    def getSpellRainAnimationFile(self, level):
        return None
    
    def getSpellRainAnimationName(self, level):
        return None

    def getSpellRainFrequency(self, level):
        return 0
    
    def getEffectChance(self, caster, level):
        return 100
