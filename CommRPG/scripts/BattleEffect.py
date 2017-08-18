from mb.jython import JBattleEffect
from org.python.modules import jarray
from java.lang import Math
from java.lang import String
from java.util import ArrayList;
from mb.fc.game.sprite import CombatSprite
import random
from Effects.Bleed import Bleed
from Effects.Burn import Burn
from Effects.Detox import Detox
from Effects.Heal import Heal
from Effects.Poison import Poison
from Effects.Shock import Shock

class BattleEffect(JBattleEffect):
	def getBattleEffectList(self):
		return jarray.array(["Poison", "Bleed", "Shock", "Burn", "Confusion",
							 "Frozen", "Sleep", "Petrify", "HP Drain", "Daze", 
							 "Dispel", "Blind", "Addled", "Detox", "Heal"], String)
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
		else:
			initChild = False
			battleEffect = BattleEffect()
			
		# Initialize statistics, nothing should ever need
		# to be changed in this first section
		if initChild:
			battleEffect.init(id, level)

		return battleEffect;