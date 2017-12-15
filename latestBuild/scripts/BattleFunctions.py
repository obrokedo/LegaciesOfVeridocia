from java.lang import Math
from mb.fc.engine.config import BattleFunctionConfiguration

class BattleFunctions(BattleFunctionConfiguration):
    
    # Gets the percent chance that the given target will dodge the attackers attack. This
    # number should be between 0-100
    def getDodgePercent(self, attacker, target):        
        # OLD VALUES
        # return Math.max(5, 5 + (target.getCurrentSpeed() - attacker.getCurrentSpeed()) / 5);
        return attacker.getModifiedEvade();
    
    # Gets the percent chance that the given attacker will get a critical hit against the target
    # this value should be between 0-100
    def getCritPercent(self, attacker, target):
        return attacker.getModifiedCrit();
    
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
        return target.getName() + " quickly dodges the attack!] "
    
    # Get the text that should be displayed when a target blocks an attack    
    def getBlockText(self, attacker, target):
        return target.getName() + " blocks the attack!] "
    
    # Get the text that should be displayed when an attacker gets a critical hit.
    # The damage variable must be used like `damage` to be put into a string
    def getCriticalAttackText(self, attacker, target, damage):
        return attacker.getName() + " strikes a deadly blow against " + target.getName() + ", dealing " + `damage` + " damage.] "
    
    # Get the text that should be displayed when an attacker gets a normal hit. 
    # The damage variable must be used like `damage` to be put into a string
    def getNormalAttackText(self, attacker, target, damage):
        return attacker.getName() + " deals " + `damage` + " damage to " + target.getName() + ".] "
        
    # The text that should be displayed when a combatant dies
    def getCombatantDeathText(self, attack, target):
        return target.getName() + " has been defeated!] "
    
    # Gets the percent chance that the given attacker will get a double attack against the target
    # this value should be between 0-100 
    def getDoublePercent(self, attacker, target):
        return attacker.getModifiedDouble();
    
    # Gets the percent chance that the given target will get a counter attack against the attacker
    # this value should be between 0-100 
    def getCounterPercent(self, attacker, target):
        return attacker.getModifiedCounter();
    
    # Get the amount that the damage dealt by an attack should be modified by when it's a counter attack
    def getCounterDamageModifier(self, attacker, target):
        return .75
    
    # Get the amount of experience that should be gained for hurting someone with a normal attack
    # The damage provided will always be NEGATIVE
    def getExperienceGainedByDamage(self, damage, attackerLevel, target):
        maxExp = Math.max(1, Math.min(49, (target.getLevel() - attackerLevel) * 7 + 35));
        # Check to see if we've killed the target, if so return the max experience possible
        if target.getCurrentHP() + damage <= 0:
            return maxExp;
        # Otherwise give experience based on damage dealt
        else:
            # Calculate the percent experience gained, this gives full "kill" experience any time you do 75% damage or more
            # and smaller amounts based on the percent of 75% health that you've done. This number can never exceed 1.
            percentExperienceGained = Math.min(Math.abs(1.0 * damage / target.getMaxHP()) / .75, 1);
            return int(Math.max(Math.max(1, 5 + target.getLevel() - attackerLevel), maxExp * percentExperienceGained));