from tactical.game.battle import BattleEffect
from org.python.modules import jarray
from java.lang import Math
from java.lang import String
from java.util import ArrayList;
from tactical.game.sprite import CombatSprite
import random

class BodyUp(BattleEffect):

    effectValue = 0

    def __init__(self, level = 0):
        return None
        
    def getName(self):
        return "BodyUp"
    
    # This action is performed on each subsequent turn after the effect is applied
    # This should return a string that will be displayed to the user each time the
    # effect "ticks"
    # The string returned from here should NOT have a line end special character    
    def performEffectImpl(self, target, currentTurn):
        return None;
        
    # This is called as soon as the effect is applied (on the casters turn NOT THE TARGETS)
    # This should return a string indicating what occurred when the effect started or None if
    # no effect occurred
    def effectStarted(self, attacker, target):    
        effectLevel = self.getEffectLevel()

        if effectLevel == 1:
            self.effectValue = int(target.getCurrentBody() * 0.25)
        elif effectLevel == 2:
            self.effectValue = int(target.getCurrentBody() * 0.30)
        elif effectLevel == 3:
            self.effectValue = int(target.getCurrentBody() * 0.35)
        elif effectLevel == 4:
            self.effectValue = int(target.getCurrentBody() * 0.40)

        #target.setCurrentBody(target.getCurrentBody() + self.effectValue) #setcurrentBody does not exist

        return None;
    
    # This is the text indicating what effect has been applied
    def effectStartedText(self, attacker, target):
        return target.getName() + " body was raised."
        
    # Returns what should be displayed when the effects ends. Additonally
    # this should reset any stats that have been changed as a result of this effect
    def effectEnded(self, target):
        #target.setCurrentBody(target.getCurrentBody() - self.effectValue) #setcurrentBody does not exist
        return target.getName() + " has returned back to normal."
    
    # target is the target of the effect
    # chance is the base percent chance 0-100 that this
    #     effect is successful
    # level is the level of the effect being applied
    def isEffected(self, target):
        #effectChance = self.getEffectChance()
        #effectLevel = self.getEffectLevel()
        
        #resistance = target.getCurrentBody()
            
        # Check to see using the given resistance whether the effect is successful
        #if (self.getEffectChance() - resistance) >= random.randint(0, 100):
        #    return True;
            
        return True;
            
    def getAnimationFile(self):
        effectLevel = self.getEffectLevel()
        return None;
    
    def isNegativeEffect(self):
        return False
    
    def isDone(self):
        # Burn lasts for 3 turns
        if self.getCurrentTurn() >= 3:
            return True
        return False 
        
    def preventsMovement(self):
        return False;
        
    # Helper method to determine if actions will be prevented this turn
    def doesPrevent(self):
        #effectLevel = self.getEffectLevel()
        # Prevents attacks 25, 30, 35, 40% based on level
        #if random.randint(0, 100) < 20 + effectLevel * 5: 
        #    return True
        #else:
        return False
        
    def preventsAttack(self):
        return self.doesPrevent()
        
    def preventsSpells(self):
        return self.doesPrevent()
        
    def preventsItems(self):
        return self.doesPrevent()
        
    def preventsTurn(self):
        return False;
