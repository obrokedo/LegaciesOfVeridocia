from tactical.game.battle import BattleEffect
from org.python.modules import jarray
from java.lang import Math
from java.lang import String
from java.util import ArrayList;
from tactical.game.sprite import CombatSprite
import random

class Addled(BattleEffect):
    def __init__(self, level = 0):
        return None
        
    def getName(self):
        return "Addled"
        
    # This action is performed on each subsequent turn after the effect is applied
    # This should return a string that will be displayed to the user each time the
    # effect "ticks"
    # The string returned from here should NOT have a line end special character    
    def performEffectImpl(self, target, currentTurn):
        effectLevel = self.getEffectLevel()
        damage = (effectLevel + 1) * -2;
        target.modifyCurrentMP(damage);
    
    def performEffectText(self, target, currentTurn):
        effectLevel = self.getEffectLevel()
        damage = (effectLevel + 1) * -2;
        return target.getName() + " feels weak " + `-1 * damage` + " mp has been lost.";
        
    # This is called as soon as the effect is applied (on the casters turn NOT THE TARGETS)
    # This should return a string indicating what occurred when the effect started or None if
    # no effect occurred
    def effectStarted(self, attacker, target):    
        return None;
    
    # This is the text indicating what effect has been applied
    def effectStartedText(self, attacker, target):
        return target.getName() + " has been addled!"
        
    # This should reset any stats that have been changed as a result of this effect
    def effectEnded(self, target):
        return None;
    
    # Returns what should be displayed when the effects ends.
    def effectEndedText(self, target):
        return target.getName() + " is no longer addled!"
    
    # target is the target of the effect
    # chance is the base percent chance 0-100 that this
    #     effect is successful
    # level is the level of the effect being applied
    def isEffected(self, target):
        effectChance = self.getEffectChance()
        effectLevel = self.getEffectLevel()
        
        resistance = 0
            
        # Check to see using the given resistance whether the effect is successful
        if (self.getEffectChance() - resistance) >= random.randint(0, 100):
            return True;
            
        return False;
            
    def getAnimationFile(self):
        effectLevel = self.getEffectLevel()
        return "Addled"
    
    def isNegativeEffect(self):
        return True
    
    def isDone(self):
        # Burn lasts for 3 turns
        if self.getCurrentTurn() >= 3:
            return True
        return False 
        
    def preventsMovement(self):
        return False;
        
    def preventsAttack(self):
        return False;
        
    def preventsSpells(self):
        return False;
        
    def preventsItems(self):
        return False;
        
    def preventsTurn(self):
        return False;
