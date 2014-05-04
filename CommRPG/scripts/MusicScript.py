from mb.jython import JMusicSelector

class MusicScript(JMusicSelector):
    # Get the name of the music that should be played during a BattleCinematic
    # The attacker is a CombatSprite and targetAllies is a boolean that indicates whether
    # this action is targeting the attackers allies
    def getAttackMusic(self, attacker, targetsAllies):
        if attacker.isHero():
            return "attack"
        else:
            return "attack"