from mb.jython import JBattleEffect
from org.python.modules import jarray
from java.lang import Math
from java.lang import String
from java.util import ArrayList;
from mb.fc.game.sprite import CombatSprite
import random
from Effects.Petrify import Petrify

class BattleEffect(JBattleEffect):
	def getBattleEffectList(self):
		return jarray.array(["Poison", "Bleed", "Shock", "Burn", "Confusion",
							 "Frozen", "Sleep", "Petrify", "HP Drain", "Daze", 
							 "Dispel", "Blind", "Addled", "Detox", "Heal"], String)
	# Intialize the BattleEffects, generally this includes
	# setting duration
	def init(self, id, level):
		# Initialize statistics, nothing should ever need
		# to be changed in this first section
		battleEffect = BattleEffect();
		battleEffect.setEffectLevel(level);
		battleEffect.setBattleEffectId(id);
		
		# Setup the actual effect, this is where effects should be modified
		# Poisoned
		if "Poison" == id:
			# Poison is an asshole and lasts until it is cured 
			self.setDuration(1000)
			# Set this a negative effect, positive effects won't be resisted
			self.setNegativeEffect(true)
		elif "Burn" == id:
			self.setDuration(3);
			# Set this a negative effect, positive effects won't be resisted
			self.setNegativeEffect(true);
		elif "Shock" == id:
			self.setDuration(3);
			# Set this a negative effect, positive effects won't be resisted
			self.setNegativeEffect(true);
		elif "Bleed" == id:
			self.setDuration(1000);
			# Set this a negative effect, positive effects won't be resisted
			self.setNegativeEffect(true);
		elif "Heal" == id:
			# Set the duration to 0 as this is an instantaneous effect
			self.setDuration(0);
			self.setNegativeEffect(false);
		elif "Detox" == id:
			# Set the duration to 0 as this is an instantaneous effect
			self.setDuration(0);
			self.setNegativeEffect(false);
		elif "Petrify" == id:
			print "RETURNING A PETRIFY"
			return Petrify()

		return battleEffect;
		
	# This action is performed on each subsequent turn after the effect is applied
	# This should return a string that will be displayed to the user each time the
	# effect "ticks"
	# The string returned from here should NOT have a line end special character	
	def performEffectImpl(self, target, currentTurn):
		effectLevel = self.getEffectLevel()
		
		# Poisoned
		if "Poison" == self.getBattleEffectId():
			target.modifyCurrentHP(-2);
			return target.getName() + " takes 2 damage from the poison.";
		elif "Burn" == self.getBattleEffectId():
			damage = 0
			if (effectLevel == 1):
				damage = -2
			elif (effectLevel == 2):
				damage = -4
			elif (effectLevel == 3):
				damage = -6
			elif (effectLevel == 4):
				damage = -8
			target.modifyCurrentHP(damage);
			return target.getName() + " suffers " + damage + " damage from the flames.";
		elif "Shock" == self.getBattleEffectId():
			target.setCurrentMove(target.getCurrentMove() - self.getTemporaryStat())
			self.setTemporaryStat(random.randint(-3, 0))
			target.getCurrentMove(target.getCurrentMove + self.getTemporaryStat())
		return None;
		
	# This is called as soon as the effect is applied (on the casters turn NOT THE TARGETS)
	# This should return a string indicating what occurred when the effect started or None if
	# no effect occurred
	def effectStarted(self, attacker, target):
		if "Shock" == self.getBattleEffectId():
			self.setTemporaryStat(0);
		elif "Heal" == self.getBattleEffectId():
			battleEffects = target.getBattleEffects();
			for i in range(battleEffects.size()):
				if "Bleed" == battleEffects.get(i):
					battleEffects.remove(i);
					break;
			
		return None;
	
	# This is the text indicating what effect has been applied
	def effectStartedText(self, attacker, target):
		if "Poison" == self.getBattleEffectId():
			return target.getName() + " is poisoned!"
		elif "Burn" == self.getBattleEffectId():
			return target.getName() + " is set ablaze!"
		elif "Shock" == self.getBattleEffectId():
			return target.getName() + "'s body seizes in shock!"
	   	elif "Heal" == self.getBattleEffectId():
			battleEffects = target.getBattleEffects();
			for i in range(battleEffects.size()):
				if "Bleed" == battleEffects.get(i):
					return battleEffects.get(i).effectEnded(target);
		
		return None;
		
	# Returns what should be displayed when the effects ends. Additonally
	# this should reset any stats that have been changed as a result of this effect
	def effectEnded(self, target):
		if "Burn" == self.getBattleEffectId():
			return target.getName() + " is no longer burning!"
		elif "Shock" == self.getBattleEffectId():
			target.setCurrentMove(target.getCurrentMove() - self.getTemporaryStat())
			return target.getName() + " recovered from the shock!"
		return None;
	
	# target is the target of the effect
	# chance is the base percent chance 0-100 that this
	#	 effect is successful
	# level is the level of the effect being applied
	def isEffected(self, target):
		effectChance = self.getEffectChance()
		effectLevel = self.getEffectLevel()
		
		resistance = 0
		# For effects that are "negative, apply the resistance
		if self.isNegativeEffect():
			if "Poison" == self.getBattleEffectId():
				resistance = target.getCurrentBody()
			
		# Check to see using the given resistance whether the effect is successful
		if (self.getEffectChance() - resistance) >= random.randint(0, 100):
			return True;
			
		return False;
			
	def getAnimationFile(self):
		effectLevel = self.getEffectLevel()
		# Poisoned
		if "Poison" == self.getBattleEffectId():
			return "Blaze";
		return "No animation file";
		
	def preventsMovement(self):
		return False;
		
	def preventsAttack(self):
		return False;
		
	def preventsSpells(self):
		return False;
		
	def preventsItems(self):
		return False;
		
	def preventsTurn(self):
		return False;