from tactical.engine.config import SpellFactory
from org.python.modules import jarray
from java.lang import Math
from tactical.game import Range
from org.newdawn.slick import Color
from java.lang import String
from BattleEffect import BattleEffect
from Spellz.Aura import Aura
from Spellz.Blaze import Blaze
from Spellz.Bolt import Bolt
from Spellz.Heal import Heal
from Spellz.Lightning import Lightning
from Spellz.Spirit import Spirit
from Spellz.Bile import Bile
from Spellz.Freeze import Freeze
from Spellz.Guard import Guard
from Spellz.Power import Power
from Spellz.Momentum import Momentum
from Spellz.Delirium import Delirium
from Spellz.Inferno import Inferno
from Spellz.Blizzard import Blizzard
from Spellz.Desoul import Desoul
from Spellz.Plague import Plague
from Spellz.Boulder import Boulder
from Spellz.Quake import Quake
from Spellz.Torrent import Torrent
from Spellz.Flood import Flood
from Spellz.Gust import Gust
from Spellz.Cyclone import Cyclone
from Spellz.Burst import Burst
from Spellz.Flare import Flare
from Spellz.Detox import Detox
from Spellz.Egress import Egress

class Spells(SpellFactory):    
    # This defines the id for each spell in the game.
    def getSpellList(self):
        return jarray.array(["HEAL", "AURA", "DETOX", "BOOST", "SLOW", "STRENGTH", "DISPEL", "MUDDLE",
                             "DESOUL", "SLEEP", "EGRESS", "BLAZE", "FREEZE", "BOLT", "BLAST",
                             "POWER", "TORRENT", "GUARD", "BOULDER", "INFERNO", "SPIRIT", "MOMENTUM",
                             "BURST", "DELIRIUM", "GUST", "FLOOD", "CYCLONE", "FIRE BREATH", "HP&MP DRAIN", 
                             "LIGHTNING", "ION BLAST", "QUAKE", "FLARE", "BILE", "ERUPT", "BLIZZARD",
                             "PLAGUE"], String)
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
            spell = Detox()
        elif "BILE" == id:
        	spell = Bile()
        elif "FREEZE" == id:
            spell = Freeze()
        elif "GUARD" == id:
            spell = Guard()
        elif "POWER" == id:
            spell = Power()
        elif "MOMENTUM" == id:
            spell = Momentum();
        elif "DELIRIUM" == id:
            spell = Delirium();
        elif "INFERNO" == id:
            spell = Inferno();
        elif "BLIZZARD" == id:
            spell = Blizzard();
        elif "DESOUL" == id:
            spell = Desoul();
        elif "PLAGUE" == id:
            spell = Plague();
        elif "BOULDER" == id:
            spell = Boulder();
        elif "QUAKE" == id:
            spell = Quake();
        elif "TORRENT" == id:
            spell = Torrent();
        elif "FLOOD" == id:
            spell = Flood();
        elif "GUST" == id:
            spell = Gust();
        elif "CYCLONE" == id:
            spell = Cyclone();
        elif "BURST" == id:
            spell = Burst();
        elif "FLARE" == id:
            spell = Flare();
        elif "EGRESS" == id:
            spell = Egress();
        else:
            spell = None
            print "BAD SPELL " + id
        
        if spell != None:
            spell.setId(id)
        return spell
