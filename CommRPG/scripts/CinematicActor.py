from mb.jython import JCinematicActor

# Building object that subclasses a Java interface
class CinematicActor(JCinematicActor):
        
    # Get the amount of ms that will elapse between moving an actor. Decreasing this will increase overall move speed 
    def getMoveUpdate(self):
        return 20
    
    # The amount of time in ms that a head-nod special effect should last for
    def getNodHeadDuration(self):
        return 500
    
    # The speed that actors should "quiver" decreasing this will speed up the quiver rate
    def getQuiverUpdate(self):
        return 25
    
    # The speed that actors should "tremble" decreasing this will speed up the tremble rate
    def getTrembleUpdate(self):
        return 13
    
    # The speed in ms that animations should be set to change after completing a move or a special effect
    def getAnimUpdateAfterSE(self):
        return 500
    
    # The animation speed for a cinematic actor when they are moving
    def getAnimSpeedForMoveSpeed(self, moveSpeed):
        return 469.875 / moveSpeed 