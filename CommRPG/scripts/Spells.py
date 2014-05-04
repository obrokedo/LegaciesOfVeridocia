from mb.fc.game.battle.spell import Spell
from mb.fc.game.battle.spell import KnownSpell
from org.python.modules import jarray
from java.lang import Math

class Spells(Spell):
    def init(self, id):
        self.setId(id)
        if KnownSpell.ID_BLAZE == id:
            self.setName("Blaze")       
            self.setCosts(jarray.array([2, 5, 8, 12], 'i'))
            self.setDamage(jarray.array([-6, -8, -15, -40], 'i'))
            self.setRange(jarray.array([2, 2, 2, 2], 'i'))
            self.setArea(jarray.array([1, 2, 2, 1], 'i'))            
            self.setTargetsEnemy(1)
            self.setMaxLevel(4)     
        elif KnownSpell.ID_HEAL == id:      
            self.setName("Heal")              
            self.setCosts(jarray.array([3, 5, 9, 12], 'i'))
            self.setDamage(jarray.array([15, 22, 36, 120], 'i'))
            self.setRange(jarray.array([1, 2, 3, 1], 'i'))
            self.setArea(jarray.array([1, 1, 1, 1], 'i'))            
            self.setTargetsEnemy(0)
            self.setMaxLevel(4)
        elif KnownSpell.ID_AURA == id:
            self.setName("Aura")
            self.setCosts(jarray.array([7, 11, 15, 20], 'i'))
            self.setDamage(jarray.array([15, 15, 30, 120], 'i'))
            self.setRange(jarray.array([3, 3, 3, 3], 'i'))
            self.setArea(jarray.array([2, 3, 3, 3], 'i'))            
            self.setTargetsEnemy(0)
            self.setMaxLevel(4)                        
        return None
        
    def getBattleText(self, target, spellLevel):
        if self.name == "blaze":
            return "Flame engulfs " + target.getName() + "'s body dealing " + `self.getDamage()[spellLevel]` + " damage!"
        elif self.name == "Heal":
            return target.getName() + "'s wounds close and scars fade. " + target.getName() + " is healed by " + `int(Math.min(target.getMaxHP() - target.getCurrentHP(), self.getDamage()[spellLevel]))` + "!"
        elif self.name == "Aura":
            return "A healing aura washes over " + target.getName() + ". " + target.getName() + " is healed by " + `int(Math.min(target.getMaxHP() - target.getCurrentHP(), self.getDamage()[spellLevel]))` + "!"

    def getExpGained(self, level, attacker, target):
        if self.name == "Heal":
            return int(12 * Math.max(0, (target.getMaxHP() - target.getCurrentHP()) * 1.0 / self.getDamage()[level]))
        elif self.name == "Aura":
            return int(12 * Math.max(0, (target.getMaxHP() - target.getCurrentHP()) * 1.0 / self.getDamage()[level]))
        else:
            return -1
        