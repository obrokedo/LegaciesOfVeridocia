from mb.jython import JMenuConfiguration

class MenuConfiguration(JMenuConfiguration):
    # Resurrect methods
    def getPriestNoOneToResurrectText(self):
        return "Hmm it doesn't look like anyone is dead...<hardstop>"
    
    def getPriestNotEnoughGoldToResurrectText(self):
        return "It seems you do not have enough gold to cover the materials for the rite of resurrection...<hardstop>"
    
    def getPriestResurrectCost(self, level, promoted):
        amount = level * 10
        if (promoted):
            amount = amount + 100
        return amount
    
    def getPriestSelectSomeoneToResurrectText(self, name, cost):
        return "Oh my! " + name + " has passed to the other side, but I can revive him for " + `cost` + " gold.<hardstop>"
    
    def getPriestTargetHasBeenResurrectedText(self, name):
        return name + " has returned to the land of the living!<hardstop>"
    
    # Promote methods
    def getPriestNoOneToPromoteText(self):
        return "No one has attained the status to be promoted at this time.<hardstop>"
    
    def getPriestSelectSomeoneToPromoteText(self, name, newClassName, specialItemName):
        if specialItemName == None:
            return name + " will be promoted to the rank of " + newClassName
        else:
            return name + " will be promoted to the rank of " + newClassName + " by using the " + specialItemName + ".<hardstop>"
    def getPriestTargetHasBeenPromotedText(self, name, newClassName, specialItemName):
        return name + " has been promoted to the rank of " + newClassName + "!<hardstop>"
    
    
    # Cure methods
    def getPriestNoOneToCureText(self):
        return "None of the members of the party are under the effect of any maladies.<hardstop>"
    
    def getPriestCureCost(self, battleEffectNames, batteEffectLevels):
        amount = 0
        
        for index in range(0, len(battleEffectNames)):
            amount = amount + (battleEffectLevels[index] * 10)
        
        return amount
    
    def getPriestSelectSomeoneToCureText(self, name, battleEffectNames, cost):
        return "Oh my! " + name + "'s body is riddled with maladies, but I can restore him to perfect health for " + `cost` + " gold<hardstop>"
    def getPriestNotEnoughGoldToCureText():
        return "I'm sorry, you do not have enough gold...<hardstop>"
    def getPriestTargetHasBeenCuredText(self, name):
        return name + "'s ailments have been cured!<hardstop>"