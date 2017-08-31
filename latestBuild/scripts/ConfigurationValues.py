from java.lang import String
from mb.jython import JConfigurationValues
import sys

from org.python.modules import jarray

class ConfigurationValues(JConfigurationValues):
    
    def getStartingHeroIds(self):
        return jarray.array(["Noah", "Sharna", "Huun"], String)
    
    def getStartingMapData(self):
        return "erium"
    
    # TOWN
    # CINEMATIC
    #    If cinematic is selected then cinematic 0 will be ran and the start location will be ignored
    # BATTLE
    def getStartingState(self):
        return "CINEMATIC"
    
    def getStartingLocation(self):
        return None
    
    def getMaxPartySize(self):
        return 12
    
    # Return an array of elemental affinity types. Each spell
    # will have an associated affinity and some weapons may
    # deal damage related to an affinity
    def getAffinities(self):
        return jarray.array(["FIRE", "COLD", "ELECTRICITY", "DARKNESS", 
                             "WIND", "WATER", "EARTH", "LIGHT"], String)
    
    def getWeaponTypes(self):
        return jarray.array(["SPEAR", "AXE", "SWORD", "STAFF", "ARROW", "BLADE", "SPELLSHOT", 
                             "CLAW", "WAND", "GLOVE", "RAPIER", "LANCE", "WING", "MACE", 
                             "KATANA", "SHURIKEN"], String)        
        
    def getMovementTypes(self):
        return jarray.array(["NORMAL", "SLOW", "FAST", "BEAST", 
                             "TANK", "HOVERING", "FLIGHT", "FREE"], String)
        
    def getTerrainTypes(self):
        return jarray.array(["WATER", "GRASS", "ROAD", "THICK-GRASS", 
                             "FOREST", "HILLS", "SAND", "LOW-SKY", "HIGH-SKY", "IMPASSABLE"], String)
        
    def getTerrainEffectAmount(self, terrainType):
        if "WATER" == terrainType:
            return 0
        elif "GRASS" == terrainType:
            return 10
        elif "ROAD" == terrainType:
            return 0
        elif "THICK-GRASS" == terrainType:
            return 20
        elif "FOREST" == terrainType:
            return 40
        elif "HILLS" == terrainType:
            return 30
        elif "SAND" == terrainType:
            return 0
        elif "LOW-SKY" == terrainType:
            return 0
        elif "HIGH-SKY" == terrainType:
            return 0
        elif "IMPASSABLE" == terrainType:
            return 0
        
    def isAffectedByTerrain(self, movementType):
        if "FLIGHT" == movementType:
            return 0
        else:
            return 1
        
    def getMovementCosts(self, movementType, terrainType):
        if "NORMAL" == movementType:
            if "WATER" == terrainType:
                return 1000
            elif "GRASS" == terrainType:
                return 10
            elif "ROAD" == terrainType:
                return 10
            elif "THICK-GRASS" == terrainType:
                return 15
            elif "FOREST" == terrainType:
                return 20
            elif "HILLS" == terrainType:
                return 15
            elif "SAND" == terrainType:
                return 20
            elif "LOW-SKY" == terrainType:
                return 1000
            elif "HIGH-SKY" == terrainType:
                return 1000
            elif "IMPASSABLE" == terrainType:
                return 1000
        elif "SLOW" == movementType:
            if "WATER" == terrainType:
                return 1000
            elif "GRASS" == terrainType:
                return 10
            elif "ROAD" == terrainType:
                return 10
            elif "THICK-GRASS" == terrainType:
                return 15
            elif "FOREST" == terrainType:
                return 25
            elif "HILLS" == terrainType:
                return 25
            elif "SAND" == terrainType:
                return 25
            elif "LOW-SKY" == terrainType:
                return 1000
            elif "HIGH-SKY" == terrainType:
                return 1000
            elif "IMPASSABLE" == terrainType:
                return 1000
        elif "FAST" == movementType:
            if "WATER" == terrainType:
                return 1000
            elif "GRASS" == terrainType:
                return 10
            elif "ROAD" == terrainType:
                return 10
            elif "THICK-GRASS" == terrainType:
                return 10
            elif "FOREST" == terrainType:
                return 15
            elif "HILLS" == terrainType:
                return 10
            elif "SAND" == terrainType:
                return 20
            elif "LOW-SKY" == terrainType:
                return 1000
            elif "HIGH-SKY" == terrainType:
                return 1000
            elif "IMPASSABLE" == terrainType:
                return 1000
        elif "BEAST" == movementType:
            if "WATER" == terrainType:
                return 1000
            elif "GRASS" == terrainType:
                return 10
            elif "ROAD" == terrainType:
                return 10
            elif "THICK-GRASS" == terrainType:
                return 10
            elif "FOREST" == terrainType:
                return 10
            elif "HILLS" == terrainType:
                return 15
            elif "SAND" == terrainType:
                return 15
            elif "LOW-SKY" == terrainType:
                return 1000
            elif "HIGH-SKY" == terrainType:
                return 1000
            elif "IMPASSABLE" == terrainType:
                return 1000
        elif "TANK" == movementType:
            if "WATER" == terrainType:
                return 1000
            elif "GRASS" == terrainType:
                return 10
            elif "ROAD" == terrainType:
                return 10
            elif "THICK-GRASS" == terrainType:
                return 10
            elif "FOREST" == terrainType:
                return 15
            elif "HILLS" == terrainType:
                return 10
            elif "SAND" == terrainType:
                return 15
            elif "LOW-SKY" == terrainType:
                return 1000
            elif "HIGH-SKY" == terrainType:
                return 1000
            elif "IMPASSABLE" == terrainType:
                return 1000
        elif "HOVERING" == movementType:
            if "WATER" == terrainType:
                return 10
            elif "GRASS" == terrainType:
                return 10
            elif "ROAD" == terrainType:
                return 10
            elif "THICK-GRASS" == terrainType:
                return 10
            elif "FOREST" == terrainType:
                return 10
            elif "HILLS" == terrainType:
                return 10
            elif "SAND" == terrainType:
                return 10
            elif "LOW-SKY" == terrainType:
                return 10
            elif "HIGH-SKY" == terrainType:
                return 1000
            elif "IMPASSABLE" == terrainType:
                return 1000
        elif "FLIGHT" == movementType:
            if "WATER" == terrainType:
                return 10
            elif "GRASS" == terrainType:
                return 10
            elif "ROAD" == terrainType:
                return 10
            elif "THICK-GRASS" == terrainType:
                return 10
            elif "FOREST" == terrainType:
                return 10
            elif "HILLS" == terrainType:
                return 10
            elif "SAND" == terrainType:
                return 10
            elif "LOW-SKY" == terrainType:
                return 10
            elif "HIGH-SKY" == terrainType:
                return 10
            elif "IMPASSABLE" == terrainType:
                return 1000
        elif "FREE" == movementType:
            if "WATER" == terrainType:
                return 10
            elif "GRASS" == terrainType:
                return 10
            elif "ROAD" == terrainType:
                return 10
            elif "THICK-GRASS" == terrainType:
                return 10
            elif "FOREST" == terrainType:
                return 10
            elif "HILLS" == terrainType:
                return 10
            elif "SAND" == terrainType:
                return 10
            elif "LOW-SKY" == terrainType:
                return 10
            elif "HIGH-SKY" == terrainType:
                return 10
            elif "IMPASSABLE" == terrainType:
                return 10
        
    # Return the index in the battle backgrounds image that should be used
    # for the battle background image when someone attacks on the "overworld"
    # while standing on the specified terrainType
    def getBattleBackgroundImageIndexByTerrainType(self, terrainType):
        return 0
    
    # Return an integer that indicates the level that heroes can begin being
    # promoted at
    def getHeroPromotionLevel(self):
        return 10
    
    def clearPythonModules(self):
        sys.modules.clear()
