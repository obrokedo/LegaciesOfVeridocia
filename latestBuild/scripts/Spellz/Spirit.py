from mb.fc.game.battle.spell import SpellDefinition
from org.python.modules import jarray
from java.lang import Math
from mb.fc.game import Range
from org.newdawn.slick import Color
from java.lang import String
from BattleEffect import BattleEffect

class Spirit(SpellDefinition):    
    # This is where you set up all of the parameters for each of the spells    
    def __init__(self):
        # This value should appear in the array returned from the spell list above
        self.setName("Spirit")       
        self.setCosts(jarray.array([3, 6, 20, 20], 'i'))
        self.setDamage(jarray.array([0, 0, 0, 0], 'i'))
        self.setRange(jarray.array([Range.TWO_AND_LESS, Range.THREE_AND_LESS, Range.THREE_AND_LESS, Range.SELF_ONLY], Range))
        self.setArea(jarray.array([1, 1, 2, 3], 'i'))            
        self.setTargetsEnemy(0)
        self.setMaxLevel(4)     
        # A value of 0 means it does not loop. 1 means it does loop
        self.setLoops(1)   
        # The number specified here is an index into the SpellIcons image file starting from 0
        self.setSpellIconIndex(4)
    
    def getEffectiveDamage(self, attacker, target, spellLevel):
        baseDamage = self.getDamage()[spellLevel]
        return int(baseDamage)
    
    def getBattleText(self, target, damage, mpDamage, attackerHPDamage, attackerMPDamage):
        return None
        
    def getExpGained(self, level, attacker, target):
        if self.name == "Heal":
            if target.getCurrentHP() == target.getMaxHP():
                return int (0);
            else:
                return int(Math.max(10, Math.min(target.getMaxHP() - target.getCurrentHP(), 
                                                 self.getDamage()[level]) * 1.0/ target.getMaxHP() * 25));
        elif self.name == "Aura":
            return int(12 * Math.max(0, (target.getMaxHP() - target.getCurrentHP()) * 1.0 / self.getDamage()[level]))
        else:
            return -1
            
    # This is at 30% opacity    
    def getSpellOverlayColor(self, level):
        
        return Color(0, 0, 0);
        
