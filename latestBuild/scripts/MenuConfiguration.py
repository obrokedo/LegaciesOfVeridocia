# -*- coding: cp1252 -*-
from tactical.engine.config import MenuConfiguration

class MenuConfiguration(MenuConfiguration):
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
        return "Oh my! " + name + " has passed to the other side, but I can revive them for " + `cost` + " gold.<hardstop>"
    
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
    
    def getPriestCureCost(self, battleEffectNames, battleEffectLevels):
        amount = 0
        
        for index in range(0, len(battleEffectNames)):
            amount = amount + (battleEffectLevels[index] * 10)
        
        return amount
    
    def getPriestSelectSomeoneToCureText(self, name, battleEffectNames, cost):
        return "Oh my! " + name + "'s body is riddled with maladies, but I can restore them to perfect health for " + `cost` + " gold<hardstop>"
    def getPriestNotEnoughGoldToCureText(self):
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
    def getNoItemInChestText(self):
        return "It was dark inside...<hardstop>"
    def getItemInChestText(self, itemName):
        return "There was a " + itemName + " inside!<hardstop>"
    def getItemInChestTextNoRoom(self, itemName):
        return "There was a " + itemName + " inside,<softstop> but you have no room...<hardstop>"
    def getItemRecievedText(self, heroName, itemName):
        return heroName + " recieved the " + itemName + ".<hardstop>"
    
    # Town menus
    def getGiveToWhoText(self, heroName, itemName):
        return "Who will you give the " + itemName + " to?<hardstop>"
    def getGiveSuccessText(self, giverName, itemName, targetName):
        return "The " + itemName + " now belongs to " + targetName + "<hardstop>"
    def getDropConfirmText(self, itemName):
        return "Are you sure you want to drop the " + itemName + "?<hardstop>"
    def getDropSuccessText(self, itemName):
        return "The " + itemName + " has been discarded<hardstop>"
    def getUseTargetText(self, itemName):
        return "Who would you like to use the " + itemName + " on?<hardstop>"
    def getUseFailedText(self, heroName, itemName):
        return heroName + " uses the " + itemName + "!<pause> But it has no effect...<hardstop>"
    
    
    def getStorageDepositText(self):
        return "Which item would you like to deposit?<hardstop>"
    def getStorageDepositedText(self, itemName):
        return "The " + itemName + " has been deposited.<hardstop>"
    def getStorageWithdrawText(self):
        return "Which item would you like to withdraw?<hardstop>"
    def getStorageWithdrawnText(self):
        return "I hope it serves you well!<hardstop>"
    def getStorageEvaluateText(self):
        return "Which item would you like to evaluate?<hardstop>"
    def getStorageWithdrawNoItemsText(self):
        return "You don't have any items stored...<hardstop>"
    
    def getPartyNoOneToJoinText(self):
        return "Our garrison is empty!<hardstop>"
    def getPartyGroupIsFull(self):
        return "Our group is at full capacity, why don't you let someone rest?<hardstop>"
    def getPartyWhoToAdd(self):
        return "Who should be added?<hardstop>"
    def getPartyWhoToRemove(self):
        return "Who should take a break?<hardstop>"
    def getPartyNoOneToRemove(self):
        return "Taking a group that was any smaller would be suicide...<hardstop>"
    def getPartyWhoToInpsect(self):
        return "Who would you like to inspect?<hardstop>"
    def getPartyMemberRemoved(self, heroName):
        return  heroName + " why don't you take a break for a bit?<hardstop>"
    def getPartyMemberAdded(self, heroName):
         return heroName + " will now follow us into battle!<hardstop>"
     
    def getIntroMusicName(self):
        return "lovtheme"
    # Lower values makes the text scroll quicker
    def getIntroScrollSpeed(self):
        return 90
    # Gets the time that the screen will automatically transition to the menu 
    def getIntroExitTime(self):
        return 1600
    # Gets the amount of time that the text will wait before appearing on the screen
    def getIntroTextEnterDelayTime(self):
        return 80
    def getIntroTextMaxWidth(self):
        return 720
    def getIntroAnimationFileName(self):
        return "Intro"
    
    # For a new paragraph place <linebreak> on its own line
    def getIntroText(self):
        text = '''
Long ago, the powers of light and darkness fought for dominance. The world suffered. After eons, the forces of light claimed victory, but the cost was great.
<linebreak>
The king of the Gods of Light was slain; his divinity coursed from his lifeless body, his blood burning the land in holy fire.
<linebreak>
His Brothers and Sisters channelled his light to vanquish the forces of darkness, but his blood continued to burn.
<linebreak>
And so, they contained his life force within five holy seals, and scattered them far and wide across the Veridocian continent.
<linebreak>
The world was safe once more.
<linebreak>
With the war over, and Veridocia spared, the gods left the world to defend a distant world beyond the stars, departing one by one.
<linebreak>
But as the last God was about to leave, The Opportunist stepped out from the shadows.
<linebreak>
He sought to claim the God king's divinity for himself and tried to break the seals.
<linebreak>
The God of Light defeated The Opportunist, who retreated into hiding. Weary from the fighting, the last God of Light forged a divine key, and used it to mend the damaged seals.
<linebreak>
The God entrusted guardianship of the key and the seals to a noble family, who had defended Veridocia with The Pantheon against the tides of darkness.
<linebreak>
Once the God was satisfied that the world was in safe hands, he departed this world to join his kin among the stars.
<linebreak>
Peace has reigned for generations, and The Pantheon has been all but forgotten.
<linebreak>
But tales tell of a new darkness looming, just beyond the horizon...
        '''
        return text 
            
