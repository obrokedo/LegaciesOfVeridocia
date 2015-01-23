from mb.jython import JBattleEffect
from org.python.modules import jarray
from java.lang import Math

class BattleEffect(JBattleEffect):
    def init(self, id):
        battleEffect = BattleEffect()
        battleEffect.setBattleEffectId(id);
        # Poisoned
        if 0 == self.getBattleEffectId():
            # Poison is an asshole and lasts until it is cured 
            self.setDuration(1000);
        return battleEffect;
        
    # The string returned from here should NOT have a line end special character
    def performEffectImpl(self, target, currentTurn):
        # Poisoned
        if 0 == self.getBattleEffectId():
            target.modifyCurrentHP(-2);
            return target.getName() + " takes 2 damage from the poison.";
        return None;
        
    def effectStarted(self, attacker, target):
        return None;
    
    def effectStartedText(self, attacker, target):
        if 0 == self.getBattleEffectId():
            return target.getName() + " is poisoned!"
        return None;
        
    def effectEnded(self, target):
        return None;
            
    def getAnimationFile(self):
        # Poisoned
        if 0 == self.getBattleEffectId():
            return "Poison";
        return None;