from tactical.game.battle import BattleEffect
from org.python.modules import jarray
from java.lang import Math
from java.lang import String
from java.util import ArrayList;
from tactical.game.sprite import CombatSprite
import random

class Burn(BattleEffect):
    def __init__(self, level = 0):
        return None
        
    def getName(self):
        return "Burn"
        
    # This action is performed on each subsequent turn after the effect is applied
    # This should return a string that will be displayed to the user each time the
    # effect "ticks"
    # The string returned from here should NOT have a line end special character    
    def performEffectImpl(self, target, currentTurn):
        effectLevel = self.getEffectLevel()
        damage = effectLevel * -2;
        target.modifyCurrentHP(damage);
    
    def performEffectText(self, target, currentTurn):
        effectLevel = self.getEffectLevel()
        damage = effectLevel * -2;
        return target.getName() + " suffers " + `damage * -1` + " damage from the flames.";
        
    # This is called as soon as the effect is applied (on the casters turn NOT THE TARGETS)
    # This should return a string indicating what occurred when the effect started or None if
    # no effect occurred
    def effectStarted(self, attacker, target):
        return None;
    
    # This is the text indicating what effect has been applied
    def effectStartedText(self, attacker, target):
        return "[" + target.getName() + " is set ablaze!"
        
    # This should reset any stats that have been changed as a result of this effect
    def effectEnded(self, target):
        return None
    
    # Returns what should be displayed when the effects ends. 
    def effectEndedText(self, target):
        return target.getName() + " is no longer burning!"
    
    # target is the target of the effect
    # chance is the base percent chance 0-100 that this
    #     effect is successful
    # level is the level of the effect being applied
    def isEffected(self, target):
        effectChance = self.getEffectChance()
        effectLevel = self.getEffectLevel()
        
        # Potentially usable values
        # target.getCurrentMind()
        # target.getCurrentBody()
        resistance = target.getCurrentBody()
            
        # Check to see using the given resistance whether the effect is successful
        if (self.getEffectChance() - resistance) >= random.randint(0, 100):
            return True;
            
        return False;
            
    def getAnimationFile(self):
        return "Burning";
    
    def getIconName(self):
        return "Burned"
    
    def isNegativeEffect(self):
        return True
    
    def getRemainingTurns(self):
        return 3 - self.getCurrentTurn()
    
    def isDone(self):
        # Burn lasts for 3 turns
        if self.getCurrentTurn() >= 3:
            return True
        return False 
        
    def preventsAttack(self):
        return False;
        
    def preventsSpells(self):
        return False;
        
    def preventsItems(self):
        return False;
        
    def preventsTurn(self):
        return False;
