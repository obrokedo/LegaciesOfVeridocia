from mb.jython import JSpell
from mb.fc.game.battle.spell import KnownSpell
from org.python.modules import jarray
from java.lang import Math

class Spells(JSpell):
    def init(self, id):
        spell = Spells()
        spell.setId(id)
        if KnownSpell.ID_BLAZE == id:
            spell.setName("Blaze")       
            spell.setCosts(jarray.array([2, 5, 8, 12], 'i'))
            spell.setDamage(jarray.array([-6, -8, -15, -40], 'i'))
            spell.setRange(jarray.array([2, 2, 2, 2], 'i'))
            spell.setArea(jarray.array([1, 2, 2, 1], 'i'))            
            spell.setTargetsEnemy(1)
            spell.setMaxLevel(4)     
        elif KnownSpell.ID_HEAL == id:      
            spell.setName("Heal")              
            spell.setCosts(jarray.array([3, 5, 9, 12], 'i'))
            spell.setDamage(jarray.array([15, 22, 36, 120], 'i'))
            spell.setRange(jarray.array([1, 2, 3, 1], 'i'))
            spell.setArea(jarray.array([1, 1, 1, 1], 'i'))            
            spell.setTargetsEnemy(0)
            spell.setMaxLevel(4)
        elif KnownSpell.ID_AURA == id:
            spell.setName("Aura")
            spell.setCosts(jarray.array([7, 11, 15, 20], 'i'))
            spell.setDamage(jarray.array([15, 15, 30, 120], 'i'))
            spell.setRange(jarray.array([3, 3, 3, 3], 'i'))
            spell.setArea(jarray.array([2, 3, 3, 3], 'i'))            
            spell.setTargetsEnemy(0)
            spell.setMaxLevel(4)                        
        return spell
        
    def getBattleText(self, target, spellLevel):
        if self.name == "blaze":
            return "Flame engulfs " + target.getName() + "'s body dealing " + `self.getDamage()[spellLevel]` + " damage!"
        elif self.name == "Heal":
            return target.getName() + "'s wounds are healed. " + target.getName() + " recovers " + `int(Math.min(target.getMaxHP() - target.getCurrentHP(), self.getDamage()[spellLevel]))` + " HP!"
        elif self.name == "Aura":
            return "A healing aura washes over " + target.getName() + ". " + target.getName() + " is healed by " + `int(Math.min(target.getMaxHP() - target.getCurrentHP(), self.getDamage()[spellLevel]))` + "!"

    def getExpGained(self, level, attacker, target):
        if self.name == "Heal":
            if target.getCurrentHP() == target.getMaxHP():
                return int (0);
            else:
                return int(Math.max(10, Math.min(target.getMaxHP() - target.getCurrentHP(), self.getDamage()[level]) * 1.0/ target.getMaxHP() * 25));
        elif self.name == "Aura":
            return int(12 * Math.max(0, (target.getMaxHP() - target.getCurrentHP()) * 1.0 / self.getDamage()[level]))
        else:
            return -1
        
