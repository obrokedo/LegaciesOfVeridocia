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
    def getPriestSaveText(self):
        return "Your progress will be saved.<hardstop>"
    def getPriestMenuClosedText(self):
        return "May the power of light be ever at your back.<hardstop>"
    
    def getShopMenuClosedText(self):
        return "I hope your gear serves you well!<hardstop>"
    def getShopNoDealsText(self):
        return "Sorry, I haven't got any deals for today...<hardstop>"
    
    def getShopPromptSellDealText(self, itemName, cost):
        return "Wow this is a rare find! How about " + cost + " gold for your " + itemName + "; is that ok?<hardstop>"
    def getShopPromptSellNormalText(self, itemName, cost):
        return "I can give you " + cost + " gold for your " + itemName + "; is that ok?<hardstop>"
    def getShopPromptRepairBrokenText(self, itemName, cost):
        return "Oh wow, this " + itemName + " is completely broken! I can repair it for " + cost + " gold. Is that alright?<hardstop>"
    def getShopPromptRepairDamagedText(self, itemName, cost):
        return "This " +  itemName + " looks a little dinged up. I can repair it for " + cost + " gold. Is that alright?<hardstop>"
    def getShopItemNotDamagedText(self, itemName):
        return "This " + itemName + " is in perfect shape!<hardstop>"
    def getShopTransactionCancelledText(self):
        return "I'm sorry we can't strike a deal...<hardstop>"
    def getShopRepairCancelledText(self):
        return "Alright... but you should take better care of your gear.<hardstop>"
    def getShopTransactionSuccessfulText(self):
        return "A pleasure doing business with ya'!<hardstop>"
    def getShopNotEnoughGoldText(self):
        return "Wait a minute, you don't have enough gold for this!<hardstop>"
    def getShopItemRepairedText(self):
        return "There you go, good as new!<hardstop>"
    def getShopLookAtNormalText(self):
        return "What would you like to buy?<hardstop>"
    def getShopLookAtDealsText(self):
        return "This is where I keep the good stuff. See anything you like?<hardstop>"
    def getShopPromptPurchaseCostText(self, itemName, cost):
        return "The " + itemName + " costs " + cost + " gold coins. Is that OK?]"
    def getShopPromptWhoGetsItemText(self):
        return "Who will use this?<hardstop>"
    def getShopPromptEquipItNowText(self):
        return "Would you like to equip it now?<hardstop>"
    def getShopCantCarryMoreText(self, personName):
        return personName + " can't carry anything else... Who should use this?<hardstop>"
    def getShopNoMoreDealsText(self):
        return "That's all I've got for now...<hardstop>"
    def getShopPromptSellOldText(self, itemName, cost):
        return "Would you like to sell your old " + itemName + " for " + cost + " gold?]"