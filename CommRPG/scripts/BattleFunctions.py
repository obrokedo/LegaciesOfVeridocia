from java.lang import Math
from mb.jython import JBattleFunctions

class BattleFunctions(JBattleFunctions):
    
    # Gets the percent change that the given target will dodge the attackers attack. This
    # number should be between 0-100
    def getDodgePercent(self, attacker, target):        
        return Math.max(5, 5 + (target.getCurrentSpeed() - attacker.getCurrentSpeed()) / 5);
    
    # Gets the percent change that the given attacker will get a critical hit against the target
    # this value should be between 0-100
    def getCritPercent(self, attacker, target):
        return 3;
    
    # Get the amount that the damage dealt by an attack should be modified by when it's a critical hit
    def getCritDamageModifier(self, attacker, target):
        return 1.25
    
    # Get the damage dealt by an attacker against a target
    def getDamageDealt(self, attacker, target, landEffect, random):
        return -1 * Math.max(1, (int)(((
                            # A random number between .8 - 1.2
                            random.nextInt(40) + 80) / 100.0 *
                            # Attack
                            attacker.getCurrentAttack()) - 
                            # A random number between .8 - 1.2
                            ((random.nextInt(40) + 80) / 100.0 * 
                                    # Defense modified by land effect
                                    + landEffect * target.getCurrentDefense())));
        
    # Get the text that should be displayed when a target dodges an attack
    def getDodgeText(self, attacker, target):
        return target.getName() + " quickly dodged the attack!}"
    
    # Get the text that should be displayed when a target blocks an attack    
    def getBlockText(self, attacker, target):
        return target.getName() + " blocked the attack!}"
    
    # Get the text that should be displayed when an attacker gets a critical hit.
    # The damage variable must be used like `damage` to be put into a string
    def getCriticalAttackText(self, attacker, target, damage):
        return attacker.getName() + " inflicted a vicious blow dealing " + `damage` + " damage.}"
    
    # Get the text that should be displayed when an attacker gets a normal hit. 
    # The damage variable must be used like `damage` to be put into a string
    def getNormalAttackText(self, attacker, target, damage):
        return attacker.getName() + " dealt " + `damage` + " damage.}"
        
    # The text that should be displayed when a combatant dies
    def getCombatantDeathText(self, attack, target):
        return target.getName() + " has been defeated...}"