from mb.fc.game.battle import BattleEffect
from org.python.modules import jarray
from java.lang import Math
from java.lang import String
from java.util import ArrayList;
from mb.fc.game.sprite import CombatSprite
import random

class Heal(BattleEffect):
    def __init__(self, level = 0):
        return None
        
    def getName(self):
        return "Heal"
        
    # This doesn't need to do anything because it's instantaneous
    # This action is performed on each subsequent turn after the effect is applied
    # This should return a string that will be displayed to the user each time the
    # effect "ticks"
    # The string returned from here should NOT have a line end special character    
    def performEffectImpl(self, target, currentTurn):
        effectLevel = self.getEffectLevel()
        return None;
    
    def performEffectText(self, target, currentTurn):
        return None
    
    # This doesn't need to return anything because it's instantaneous
        
    # This is called as soon as the effect is applied (on the casters turn NOT THE TARGETS)
    # This should return a string indicating what occurred when the effect started or None if
    # no effect occurred
    def effectStarted(self, attacker, target):
        
        battleEffects = target.getBattleEffects();
        for i in range(battleEffects.size()):
            if "Bleed" == battleEffects.get(i).getName():
                target.removeBattleEffect(battleEffects.get(i))
                i = i - 1
                break;
            
        return None;
    
    # This is the text indicating what effect has been applied
    def effectStartedText(self, attacker, target):
        battleEffects = target.getBattleEffects();
        for i in range(battleEffects.size()):
            if "Bleed" == battleEffects.get(i).getName():
                return battleEffects.get(i).effectEndedText(target);
        
        return None;
        
    # Returns what should be displayed when the effects ends. Additonally
    # this should reset any stats that have been changed as a result of this effect
    def effectEnded(self, target):
        return None;
    
    # target is the target of the effect
    # chance is the base percent chance 0-100 that this
    #     effect is successful
    # level is the level of the effect being applied
    def isEffected(self, target):
        return True;
            
    def getAnimationFile(self):
        return None
    
    
    def isNegativeEffect(self):
        return False
    
    def isDone(self):
        return True
        
    def preventsMovement(self):
        return False
        
    def preventsAttack(self):
        return False;
        
    def preventsSpells(self):
        return False;
        
    def preventsItems(self):
        return False;
        
    def preventsTurn(self):
        return False;