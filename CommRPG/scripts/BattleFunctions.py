from java.lang import Math
from mb.jython import JBattleFunctions

class BattleFunctions(JBattleFunctions):
    
    # Gets the percent chance that the given target will dodge the attackers attack. This
    # number should be between 0-100
    def getDodgePercent(self, attacker, target):        
        return Math.max(5, 5 + (target.getCurrentSpeed() - attacker.getCurrentSpeed()) / 5);
    
    # Gets the percent chance that the given attacker will get a critical hit against the target
    # this value should be between 0-100
    def getCritPercent(self, attacker, target):
        return 3;
    
    # Get the amount that the damage dealt by an attack should be modified by when it's a critical hit
    def getCritDamageModifier(self, attacker, target):
        return 1.25
    
    # Get the damage dealt by an attacker against a target
    def getDamageDealt(self, attacker, target, landEffect, random):
        return -1 * Math.max(1, (int)(
                            # A random number between .85 - 1.0, multiplied by Land Effect
                            ((random.nextInt(15) + 85) / 100.0 * (1-(landEffect-1))) * 
                            # Attack
                            (attacker.getCurrentAttack() - 
                            # Minus Defense
                            target.getCurrentDefense())));
        
    # Get the text that should be displayed when a target dodges an attack
    def getDodgeText(self, attacker, target):
        return target.getName() + " quickly dodges the attack!"
    
    # Get the text that should be displayed when a target blocks an attack    
    def getBlockText(self, attacker, target):
        return target.getName() + " blocks the attack!"
    
    # Get the text that should be displayed when an attacker gets a critical hit.
    # The damage variable must be used like `damage` to be put into a string
    def getCriticalAttackText(self, attacker, target, damage):
        return attacker.getName() + " strikes a deadly blow against " + target.getName() + ", dealing " + `damage` + " damage. "
    
    # Get the text that should be displayed when an attacker gets a normal hit. 
    # The damage variable must be used like `damage` to be put into a string
    def getNormalAttackText(self, attacker, target, damage):
        return attacker.getName() + " deals " + `damage` + " damage to " + target.getName() + ". "
        
    # The text that should be displayed when a combatant dies
    def getCombatantDeathText(self, attack, target):
        return target.getName() + " has been defeated!"
    
    # Gets the percent chance that the given attacker will get a double attack against the target
    # this value should be between 0-100 
    def getDoublePercent(self, attacker, target):
        return 5;
    
    # Gets the percent chance that the given target will get a counter attack against the attacker
    # this value should be between 0-100 
    def getCounterPercent(self, attacker, target):
        return 5;
    
    # Get the amount that the damage dealt by an attack should be modified by when it's a counter attack
    def getCounterDamageModifier(self, attacker, target):
        return .75