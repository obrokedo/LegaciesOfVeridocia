from mb.jython import JMusicSelector

class MusicScript(JMusicSelector):
    # Get the name of the music that should be played during a BattleCinematic
    # The attacker is a CombatSprite and targetAllies is a boolean that indicates whether
    # this action is targeting the attackers allies
    def getAttackMusic(self, attacker, targetsAllies):
        if attacker.isHero():
            return "UnHero"
        else:
            return "Enemy"
        
    # WEAPON TYPE
    #-1 = No Weapon
    # 0 = Spear
    # 1 = Axe
    # 2 = Sword
    # 3 = Staff
    # 4 = Bow
    def getAttackHitSoundEffect(self, isHero, isCritical, isDodge, weaponType):
        if isDodge:
            return None
        elif weaponType==1 or weaponType==2:
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
