from tactical.game.battle.spell import SpellDefinition
from org.python.modules import jarray
from java.lang import Math
from tactical.game import Range
from org.newdawn.slick import Color
from java.lang import String
from BattleEffect import BattleEffect

# NOTE:
# This spell uses some wonky stuff and should NOT be used as an example for 
# other spells
class Egress(SpellDefinition):

    # This is where you set up all of the parameters for each of the spells    
    def __init__(self):
        self.setName("Egress")
        self.setCosts(jarray.array([7], 'i'))
        self.setRange(jarray.array([Range.SELF_ONLY], Range))
        self.setArea(jarray.array([1], 'i'))            
        self.setTargetsEnemy(0)
        self.setMaxLevel(1)     
        
        # Describes which BattleEffects should be applied for each level of the self. (Reference
        # the BattleEffect script for options). 
        # No effects = []
        # 1 effect = ["Effect"]
        # Many effects = ["Effect1", "Effect2"...]
        self.setEffects(jarray.array([], String), 1) # Level 1 battle effects
        
        # Set the effect level for each of the effects specified
        # For effects that don't have levels a value of 1 should be specified
        # No effects = []
        # 1 effect = [1]
        # Many effects = [1, 2...]
        self.setEffectLevel(jarray.array([], 'i'), 1) # Level 1 battle effects
                           
        # A value of 0 means it does not loop. 1 means it does loop
        self.setLoops(0)   
        # The number specified here is an index into the SpellIcons image file starting from 0
        self.setSpellIconIndex(0)
    
    def getEffectiveDamage(self, attacker, target, spellLevel):
        return 0
    
    def getBattleText(self, target, damage, mpDamage, attackerHPDamage, attackerMPDamage):
        return ""
        
    # By default all spells will show combat animations, override that here
    def showCombatAnimation(self):
    	return False;
    	
    # Because combat animations are disabled for this spell, the actual effects are
    # performed here
    def performSkippedSpellAction(self, stateInfo):
    	# Load the map that we last saved at
        stateInfo.getPersistentStateInfo().loadMap(
                    stateInfo.getPersistentStateInfo().getClientProgress().getLastEgressLocation().getLastSaveMapData(),
                    stateInfo.getPersistentStateInfo().getClientProgress().getLastEgressLocation().getInTownLocation())
        
    def getExpGained(self, level, attacker, target):
        return 10
            
    # This is at 30% opacity    
    def getSpellOverlayColor(self, level):
        return Color(0, 0, 0);
    
    def getSpellAnimationFile(self, level):
        return "Aura"
    
    def getSpellRainAnimationFile(self, level):
        return None
    
    def getSpellRainAnimationName(self, level):
        return None

    def getSpellRainFrequency(self, level):
        return 0
        
    def getEffectChance(self, caster, level):
        return 0