from java.lang import Math

def defaultSpellExpGained(self, spellLevel, attacker, target):
        attackerLevel = attacker.getLevel()
        targetLevel = target.getLevel()
        # Add 10 to the level of promoted attackers
        if attacker.isPromoted():
            attackerLevel = attackerLevel + 10
        # Add 10 to the level of promoted targets
        if target.isPromoted():
            targetLevel = targetLevel + 10
        
        damage = self.getEffectiveDamage(attacker, target, spellLevel)
        maxExp = Math.max(1, Math.min(49, (targetLevel - attackerLevel) * 7 + 35))
         # Check to see if we've killed the target, if so return the max experience possible
        if target.getCurrentHP() + damage <= 0:
            return maxExp;
        # Otherwise give experience based on damage dealt
        else:
            # Calculate the percent experience gained, this gives full "kill" experience any time you do 75% damage or more
            # and smaller amounts based on the percent of 75% health that you've done. This number can never exceed 1.
            percentExperienceGained = Math.min(Math.abs(1.0 * damage / target.getMaxHP()) / .75, 1);
            return int(Math.max(Math.max(1, 5 + targetLevel - attackerLevel), maxExp * percentExperienceGained));