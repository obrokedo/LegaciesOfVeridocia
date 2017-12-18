from mb.fc.engine.config import LevelProgressionConfiguration
from org.python.modules import jarray
from java.lang import String
from mb.fc.game.constants import AttributeStrength
from mb.fc.game.sprite import CombatSprite

class LevelProgression(LevelProgressionConfiguration):
    # Gets a list of progression types that will be passed in for "progressionType"
    # on the getProgressArray method in this script
    def getStandardStatProgressionTypeList(self):        
        return jarray.array(["None", "Linear", "Early", "Middle", "Late", 
                             "Early-Late", "Start-Stop", "Step-Up", "Step-Down", "2-Wave", "3-Wave"], String)
        
    def getBodyMindProgressionTypeList(self):
        return jarray.array(["None", "Early", "Mid", "Late", "Linear", "Weak", "Strong", "Early-Late"], String)
    
    
    # Gets the base value for a battle stat (crit, dodge, counter, double) not including weapon modifiers
    # or values based on other stats (derived values) 
    #
    # @param attributeStrength -> mb.fc.game.constants.AttributeStrength -> The strength of the attribute to
    #                                                                        be set
    # @param heroSprite -> mb.fc.game.sprite.CombatSprite -> The sprite whose attribute will be set
    # @return an integer indicating the base statistic for this battle stat
    def getBaseBattleStat(self, attributeStrength, heroSprite):
        # For promoted we use 5/8/11
        if (heroSprite.isPromoted()):
            if (AttributeStrength.WEAK == attributeStrength):
                return 5
            elif (AttributeStrength.MEDIUM == attributeStrength):
                return 8
            elif (AttributeStrength.STRONG == attributeStrength):
                return 11
        # For unpromoted we use 3/5/7
        else:
            if (AttributeStrength.WEAK == attributeStrength):
                return 3
            elif (AttributeStrength.MEDIUM == attributeStrength):
                return 5
            elif (AttributeStrength.STRONG == attributeStrength):
                return 7
        
        return 0            
    
    # Gets the base value for either the body or mind stat, not including weapon modifiers
    # or values based on other stats (derived values) 
    #
    # @param attributeStrength -> mb.fc.game.constants.AttributeStrength -> The strength of the attribute to
    #                                                                        be set
    # @param heroSprite -> mb.fc.game.sprite.CombatSprite -> The sprite whose body or mind will be set
    # @return an integer indicating the base statistic for body or mind
    def getBaseBodyMindStat(self, attributeStrength, heroSprite):        
        baseStat = 0
        if (AttributeStrength.WEAK == attributeStrength):
            baseStat = baseStat + 5
        elif (AttributeStrength.MEDIUM == attributeStrength):
            baseStat = baseStat + 10
        elif (AttributeStrength.STRONG == attributeStrength):
            baseStat = baseStat + 15
            
        #TODO Need to add values based on body/mind progression types
        return baseStat
    
    # Gets the amount that a battle stat (crit, dodge, counter, double) should increase upon reaching 
    # a new level or being promoted. This method shall be called every time a hero is promoted or levels up. 
    #
    # @param attributeStrength -> mb.fc.game.constants.AttributeStrength -> The strength of the attribute to
    #                                                                        be modified
    # @param heroSprite -> mb.fc.game.sprite.CombatSprite -> The sprite that just leveled up or was promoted
    # @param newLevel -> integer -> The level that the sprite has just attained. If this value is 1 and 
    #                                "promoted" is true; then the sprite was just promoted
    # @param promoted -> integer -> False (0) or True (1) indicator of whether this sprite is currently promoted
    # @param currentValue -> integer -> The current value of this battle stat before they gained a level. This
    #                                    does NOT include weapon modifiers or values based on other stats
    # @return an integer that will be added to the current battle stat as a result of this level or promotion
    def getLevelUpBattleStat(self, attributeStrength, heroSprite, newLevel, promoted, currentValue):
        # Check to see if the hero was just promoted. This
        # is the only time that we'll increase the battle stat
        if (newLevel == 1 and promoted):
            # Our attributes are essentially hard-coded by attribute strength.
            # Get the difference between the pre-promotion value and the promotion value.
            # This will indicate the amount that needs to be returned to set the value
            # to our hard-coded amount 
            if (AttributeStrength.WEAK == attributeStrength):
                return 5 - currentValue
            elif (AttributeStrength.MEDIUM == attributeStrength):
                return 8 - currentValue
            elif (AttributeStrength.STRONG == attributeStrength):
                return 11 - currentValue
        return 0            
    
    # Gets the amount that body or mind should increase upon reaching 
    # a new level or being promoted. This method shall be called every time a hero is promoted or levels up. 
    #
    # @param progressionType -> String -> The name of the progression type that should be used to modify the given stat
    # @param heroSprite -> mb.fc.game.sprite.CombatSprite -> The sprite that just leveled up or was promoted
    # @param newLevel -> integer -> The level that the sprite has just attained. If this value is 1 and 
    #                                "promoted" is true; then the sprite was just promoted
    # @param promoted -> integer -> False (0) or True (1) indicator of whether this sprite is currently promoted
    # @return an integer that will be added to the current body/mind as a result of this level or promotion
    def getLevelUpBodyMindStat(self, progressionType, heroSprite, newLevel, promoted):
        if (promoted):        
            if (progressionType == "Early"):
                if (newLevel == 1):
                    return 15
                elif (newLevel == 10):
                    return 10
                elif (newLevel == 20):
                    return 5
            elif (progressionType == "Mid"):
                if (newLevel == 1):
                    return 10
                elif (newLevel == 10):
                    return 15
                elif (newLevel == 20):
                    return 10
            elif (progressionType == "Late"):
                if (newLevel == 1):
                    return 5
                elif (newLevel == 10):
                    return 10
                elif (newLevel == 20):
                    return 15
            elif (progressionType == "Linear"):
                if (newLevel == 1):
                    return 10
                elif (newLevel == 10):
                    return 10
                elif (newLevel == 20):
                    return 10
            elif (progressionType == "Weak"):
                if (newLevel == 1):
                    return 5
                elif (newLevel == 10):
                    return 5
                elif (newLevel == 20):
                    return 5
            elif (progressionType == "Strong"):
                if (newLevel == 1):
                    return 15
                elif (newLevel == 10):
                    return 15
                elif (newLevel == 20):
                    return 15
            elif (progressionType == "Early-Late"):
                if (newLevel == 1):
                    return 15
                elif (newLevel == 10):
                    return 5
                elif (newLevel == 20):
                    return 15
        return 0
    
    # Currently unused
    def levelUpHero(self, combatSprite):
        return None
    
    def getProgressArray(self, progressionType, promoted):
        # If the progression type is "None" then just return 0's
        if progressionType == "None":
            return jarray.array([0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00], 'f')
        
        # Unpromoted progressions
        if (not promoted):
            # Linear Progression
            if (progressionType == "Linear"):
                return jarray.array([0.00, 10.00, 10.30, 9.70, 10.00, 10.30, 9.70, 10.00, 9.70, 10.30, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10], 'f')
            # Early Progression
            elif (progressionType == "Early"):
                return jarray.array([0.00, 14.00, 13.00, 12.00, 11.00, 10.00, 9.00, 8.00, 7.00, 6.00, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10], 'f')
            # Late Progression
            elif (progressionType == "Late"):
                return jarray.array([0.00, 6.00, 7.00, 8.00, 9.00, 10.00, 11.00, 12.00, 13.00, 14.00, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10], 'f')
            # Middle Progression
            elif (progressionType == "Middle"):
                return jarray.array([0.00, 6.00, 8.00, 10.00, 13.00, 16.00, 13.00, 10.00, 8.00, 6.00, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10], 'f')
            # Early-Late Progression
            elif (progressionType == "Early-Late"):
                return jarray.array([0.00, 15.00, 11.50, 9.00, 7.00, 5.00, 7.00, 9.00, 11.50, 15.00, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10], 'f')
        else:
            # Linear Progression
            if (progressionType == "Linear"):
                return jarray.array([0.00, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 1.75, 2.25, 1.75, 2.25, 2.00], 'f')
            # Early Progression
            elif (progressionType == "Early"):
                return jarray.array([0.00, 8.30, 7.40, 6.50, 5.80, 5.20, 4.80, 4.40, 4.10, 3.80, 3.60, 3.40, 3.20, 3.00, 2.90, 2.80, 2.70, 2.60, 2.50, 2.40, 2.30, 2.20, 2.10, 2.00, 2.00, 3.00, 2.50, 2.00, 1.50, 1.00], 'f')
            # Late Progression
            elif (progressionType == "Late"):
                return jarray.array([0.00, 2.00, 2.00, 2.10, 2.20, 2.30, 2.40, 2.50, 2.60, 2.70, 2.80, 2.90, 3.00, 3.20, 3.40, 3.60, 3.80, 4.10, 4.40, 4.80, 5.20, 5.80, 6.50, 7.40, 8.30, 1.00, 1.50, 2.00, 2.50, 3.00], 'f')
            # Middle Progression
            elif (progressionType == "Middle"):
                return jarray.array([0.00, 2.00, 2.00, 2.00, 2.25, 2.50, 2.75, 3.00, 3.50, 4.00, 5.00, 7.00, 9.00, 9.00, 7.00, 5.00, 4.00, 3.50, 3.00, 2.75, 2.50, 2.25, 2.00, 2.00, 2.00, 1.00, 2.00, 4.00, 2.00, 1.00], 'f')
            # Early-Late Progression
            elif (progressionType == "Early-Late"):
                return jarray.array([0.00, 9.00, 7.00, 5.00, 4.00, 3.50, 3.00, 2.75, 2.50, 2.25, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.25, 2.50, 2.75, 3.00, 3.50, 4.00, 5.00, 7.00, 9.00, 2.50, 2.00, 1.00, 2.00, 2.50], 'f')
            # Stop & Start Progression
            elif (progressionType == "Start-Stop"):
                return jarray.array([0.00, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 2.33, 1.50, 2.33, 1.50, 2.33], 'f')
            # Step-Up Progression
            elif (progressionType == "Step-Up"):
                return jarray.array([0.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 5.00, 5.00, 5.00, 5.00, 5.00, 5.00, 7.00, 7.00, 7.00, 7.00, 7.00, 1.00, 1.00, 2.50, 2.00, 3.50], 'f')
            # Step-Down Progression
            elif (progressionType == "Step-Down"):
                return jarray.array([0.00, 7.00, 7.00, 7.00, 7.00, 7.00, 5.00, 5.00, 5.00, 5.00, 5.00, 5.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 3.50, 2.50, 2.00, 1.00, 1.00], 'f')
            # 2-Wave Progression
            elif (progressionType == "2-Wave"):
                return jarray.array([0.00, 1.00, 1.50, 2.00, 3.50, 5.00, 6.00, 8.50, 6.00, 5.00, 3.50, 2.00, 1.50, 1.00, 1.50, 2.00, 3.50, 5.00, 6.00, 8.50, 6.00, 5.00, 3.50, 1.50, 1.00, 1.00, 3.50, 1.00, 3.50, 1.00], 'f')
            # 3-Wave Progression
            elif (progressionType == "3-Wave"):
                return jarray.array([0.00, 6.00, 5.00, 3.50, 2.00, 1.50, 1.00, 1.50, 2.00, 3.50, 5.00, 6.00, 8.00, 6.00, 5.00, 3.50, 2.00, 1.50, 1.00, 1.50, 2.00, 3.50, 5.00, 6.00, 8.00, 2.67, 1.00, 2.67, 1.00, 2.67], 'f')             