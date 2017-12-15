from mb.fc.engine.config import MusicConfiguration
from mb.fc.game.item import EquippableItem

class MusicScript(MusicConfiguration):
    # Get the name of the music that should be played during a BattleCinematic
    # The attacker is a CombatSprite and targetAllies is a boolean that indicates whether
    # this action is targeting the attackers allies
    def getAttackMusic(self, attacker, targetsAllies):
        if attacker.isHero() and attacker.isPromoted():
            return "PrHero"
        elif attacker.isHero():
            return "UnHero"
        elif attacker.isLeader():
            return "Boss"
        elif attacker.getName() == "Erium Captain":
            return "Boss"
        else:
            return "Enemy"
        
    # WEAPON TYPE. You can either use the number
    # or the EquippableItem.STYLE_????? listed below
    #-1 = No Weapon
    # 0 = Spear
    # 1 = Axe
    # 2 = Sword
    # 3 = Staff
    # 4 = Bow
    # 5 = Blade
    # 6 = Spellshot
    # 7 = Claw
    # 8 = Wand
    # 9 = Glove
    #10 = Rapier
    #11 = Lance
    #12 = Wing
    #13 = Mace
    #14 = Katana
    #15 = Shuriken
    # EquippableItem.STYLE_SPEAR = 0;
    # EquippableItem.STYLE_AXE = 1;
    # EquippableItem.STYLE_SWORD = 2;
    # EquippableItem.STYLE_STAFF = 3;
    # EquippableItem.STYLE_BOW = 4;    
    def getAttackHitSoundEffect(self, isHero, isCritical, isDodge, weaponType):
        if isDodge:
            return None
        elif weaponType==1 or weaponType==2 or weaponType==5 or weaponType==7 or weaponType==10 or weaponType==11 or weaponType==12 or weaponType==14 or weaponType==15:
            return "hit1"
        else:
            return "hit2"
        
    def getSpellHitSoundEffect(self, isHero, isTargetAlly, spellName):
        if spellName=="Heal":
            return "heal"
    
    def getCastSpellSoundEffect(self, isHero, spellName):
        return "casting"
    
    def getUseItemSoundEffect(self, isHero, isTargetAlly, itemName):
        return "jump"
    
    def getLevelUpSoundEffect(self, heroSprite):
        return "lvl"
    
    def getInvalidActionSoundEffect(self):
        return "invalid"
    
    def getMenuAddedSoundEffect(self):
        return "menushow"

    def getMenuRemovedSoundEffect(self):
        return "menushow"

    def getAfterSpellFlashSoundEffect(self, isHero, spellName):
        return None
    
    def getSpriteDeathOnMapSoundEffect(self, deadSpriteName):
        return None
