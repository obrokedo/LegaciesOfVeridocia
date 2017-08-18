from mb.jython import JSpell
from mb.fc.game.battle.spell import KnownSpell
from org.python.modules import jarray
from java.lang import Math
from mb.fc.game import Range
from org.newdawn.slick import Color
from java.lang import String
from BattleEffect import BattleEffect

class Aura(JSpell):    
    someVal = 3    

    # This is where you set up all of the parameters for each of the spells    
    def init(self, id):
        print "INIT AURA"
        self.setName("Aura")
        self.setCosts(jarray.array([7, 11, 15, 20], 'i'))
        self.setDamage(jarray.array([15, 15, 30, 120], 'i'))
        self.setRange(jarray.array([Range.THREE_AND_LESS, Range.THREE_AND_LESS, Range.THREE_AND_LESS, Range.THREE_AND_LESS], Range))
        self.setArea(jarray.array([2, 3, 3, 3], 'i'))            
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
        
        # This sets the BASELINE chance (potency)
        # No effects = []
        # 1 effect = [15]
        # Many effects = [15, 30...]
        self.setEffectChance(jarray.array([100], 'i'), 1) # Level 1 battle effects
        self.setEffectChance(jarray.array([100], 'i'), 2) # Level 2 battle effects
        self.setEffectChance(jarray.array([100], 'i'), 3) # Level 3 battle effects
        self.setEffectChance(jarray.array([100], 'i'), 4) # Level 4 battle effects
        
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
        self.setSpellIconIndex(10)
    
    def getEffectiveDamage(self, attacker, target, spellLevel):
        bleeding = False
        # Check to see if the target is bleeding, if so then
        # this will just remove that condition, not actually heal
        battleEffects = target.getBattleEffects()
        for i in range(battleEffects.size()):
            if "Bleed" == battleEffects.get(i):
                bleeding = True
                break;
    
        if bleeding:
            baseDamage = 0
        else :
            # Keep in mind that healing spells will have a positive base damage value. 
            baseDamage = Math.min(target.getMaxHP() - target.getCurrentHP(), # Maximum value is the amount of hp the target is missing 
                                  baseDamage + (baseDamage * attacker.getCurrentWaterAffin() / 100.0)) # Add the casters affinity (increasing healing)
    
    def getBattleText(self, target, damage, mpDamage, attackerHPDamage, attackerMPDamage):
         return "A healing aura washes over " + target.getName() + ". " + target.getName() + " is healed by " + `damage` + "!"
        
    def getExpGained(self, level, attacker, target):
            return int(12 * Math.max(0, (target.getMaxHP() - target.getCurrentHP()) * 1.0 / self.getDamage()[level]))
            
    # This is at 30% opacity    
    def getSpellOverlayColor(self, level):
        return Color(0, 0, 0);
        
