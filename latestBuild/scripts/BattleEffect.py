from tactical.engine.config import BattleEffectFactory
from org.python.modules import jarray
from java.lang import Math
from java.lang import String
from java.util import ArrayList;
from tactical.game.sprite import CombatSprite
import random
from Effects.Bleed import Bleed
from Effects.Burn import Burn
from Effects.Detox import Detox
from Effects.Heal import Heal
from Effects.Poison import Poison
from Effects.Shock import Shock
from Effects.DefenceUp import DefenceUp
from Effects.BodyUp import BodyUp
from Effects.ResistanceUp import ResistanceUp
from Effects.MindUp import MindUp
from Effects.AttackUp import AttackUp
from Effects.StrikeUp import StrikeUp
from Effects.SpeedUp import SpeedUp
from Effects.MovementUp import MovementUp
from Effects.Addled import Addled
from Effects.Dispel import Dispel
from Effects.Confusion import Confusion

class BattleEffect(BattleEffectFactory):
    def getBattleEffectList(self):
        return jarray.array(["Poison", "Bleed", "Shock", "Burn", "Confusion",
                             "Frozen", "Sleep", "Petrify", "HP Drain", "Daze", 
                             "Dispel", "Blind", "Addled", "Detox", "Heal", 
                             "DefenceUp", "BodyUp", "ResistanceUp", "MindUp",
                             "AttackUp", "StrikeUp"], String)
    # Intialize the BattleEffects, generally this includes
    # setting duration
    def createEffect(self, id, level):
        
        initChild = True
        # Setup the actual effect, this is where effects should be modified
        # Poisoned
        if "Poison" == id:
            battleEffect = Poison()
        elif "Burn" == id:
            battleEffect = Burn()
        elif "Shock" == id:
            battleEffect = Shock()
        elif "Bleed" == id:
            battleEffect = Bleed()
        elif "Heal" == id:
            battleEffect = Heal()
        elif "Detox" == id:
            battleEffect = Detox()
        elif "DefenceUp" == id:
            battleEffect = DefenceUp()
        elif "BodyUp" == id:
            battleEffect = BodyUp()
        elif "ResistanceUp" == id:
            battleEffect = ResistanceUp()
        elif "MindUp" == id:
            battleEffect = MindUp()
        elif "AttackUp" == id:
            battleEffect = AttackUp()
        elif "StrikeUp" == id:
            battleEffect = StrikeUp()
        elif "MovementUp" == id:
            battleEffect = MovementUp()
        elif "SpeedUp" == id:
            battleEffect = SpeedUp()
        elif "Addled" == id:
            battleEffect = Addled()
        elif "Confusion" == id:
            battleEffect = Confusion()
        elif "Dispel" == id:
            battleEffect = Dispel()
        else:
            initChild = False
            battleEffect = BattleEffect()
            
        # Initialize statistics, nothing should ever need
        # to be changed in this first section
        if initChild:
            battleEffect.setEffectLevel(level)
            battleEffect.setBattleEffectId(id)

        return battleEffect;