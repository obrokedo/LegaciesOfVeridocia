from mb.fc.engine.config import SpellFactory
from org.python.modules import jarray
from java.lang import Math
from mb.fc.game import Range
from org.newdawn.slick import Color
from java.lang import String
from BattleEffect import BattleEffect
from Spellz.Aura import Aura
from Spellz.Blaze import Blaze
from Spellz.Bolt import Bolt
from Spellz.Heal import Heal
from Spellz.Lightning import Lightning
from Spellz.Spirit import Spirit

class Spells(SpellFactory):    
    # This defines the id for each spell in the game.
    def getSpellList(self):
        return jarray.array(["HEAL", "AURA", "DETOX", "BOOST", "SLOW", "STRENGTH", "DISPEL", "MUDDLE",
                             "DESOUL", "SLEEP", "EGRESS", "BLAZE", "FREEZE", "BOLT", "BLAST",
                             "POWER", "TORRENT", "GUARD", "BOULDER", "INFERNO", "SPIRIT", "MOMENTUM",
                             "BURST", "DELIRIUM", "GUST", "FLOOD", "CYCLONE", "FIRE BREATH", "HP&MP DRAIN", 
                             "LIGHTNING", "ION BLAST", "QUAKE", "FLARE"], String)
    # This is where you set up all of the parameters for each of the spells    
    def createSpell(self, id):
        # This value should appear in the array returned from the spell list above
        if "BLAZE" == id:            
            spell = Blaze()
        elif "HEAL" == id:      
            spell = Heal()
        elif "AURA" == id:
            spell = Aura()
        elif "BOLT" == id:
            spell = Bolt()
        elif "LIGHTNING" == id:
            spell = Lightning();
        elif "SPIRIT" == id:
            spell = Spirit()
        elif "DETOX" == id:
            spell = Heal()
        else:
            spell = None
            print "BAD SPELL " + id
        
        if spell != None:
            spell.setId(id)
        return spell