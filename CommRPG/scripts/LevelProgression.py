from mb.jython import JLevelProgression
from org.python.modules import jarray

class LevelProgression(JLevelProgression):
    def getProgressArray(self, progressionType, promoted):
        # Unpromoted progressions
        if (not promoted):
            # Linear Progression
            if (progressionType == 0):
                return jarray.array([0.00, 10.00, 10.30, 9.70, 10.00, 10.30, 9.70, 10.00, 9.70, 10.30, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10], 'f')
            # Early Progression
            elif (progressionType == 1):
                return jarray.array([0.00, 14.00, 13.00, 12.00, 11.00, 10.00, 9.00, 8.00, 7.00, 6.00, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10], 'f')
            # Late Progression
            elif (progressionType == 2):
                return jarray.array([0.00, 6.00, 7.00, 8.00, 9.00, 10.00, 11.00, 12.00, 13.00, 14.00, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10], 'f')
            # Middle Progression
            elif (progressionType == 3):
                return jarray.array([0.00, 6.00, 8.00, 10.00, 13.00, 16.00, 13.00, 10.00, 8.00, 6.00, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10], 'f')
            # Early-Late Progression
            elif (progressionType == 4):
                return jarray.array([0.00, 15.00, 11.50, 9.00, 7.00, 5.00, 7.00, 9.00, 11.50, 15.00, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10, 0.90, 1.10], 'f')
        else:
            # Linear Progression
            if (progressionType == 0):
                return jarray.array([0.00, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 4.00, 3.50, 1.75, 2.25, 1.75, 2.25, 2.00], 'f')
            # Early Progression
            elif (progressionType == 1):
                return jarray.array([0.00, 8.30, 7.40, 6.50, 5.80, 5.20, 4.80, 4.40, 4.10, 3.80, 3.60, 3.40, 3.20, 3.00, 2.90, 2.80, 2.70, 2.60, 2.50, 2.40, 2.30, 2.20, 2.10, 2.00, 2.00, 3.00, 2.50, 2.00, 1.50, 1.00], 'f')
            # Late Progression
            elif (progressionType == 2):
                return jarray.array([0.00, 2.00, 2.00, 2.10, 2.20, 2.30, 2.40, 2.50, 2.60, 2.70, 2.80, 2.90, 3.00, 3.20, 3.40, 3.60, 3.80, 4.10, 4.40, 4.80, 5.20, 5.80, 6.50, 7.40, 8.30, 1.00, 1.50, 2.00, 2.50, 3.00], 'f')
            # Middle Progression
            elif (progressionType == 3):
                return jarray.array([0.00, 2.00, 2.00, 2.00, 2.25, 2.50, 2.75, 3.00, 3.50, 4.00, 5.00, 7.00, 9.00, 9.00, 7.00, 5.00, 4.00, 3.50, 3.00, 2.75, 2.50, 2.25, 2.00, 2.00, 2.00, 1.00, 2.00, 4.00, 2.00, 1.00], 'f')
            # Early-Late Progression
            elif (progressionType == 4):
                return jarray.array([0.00, 9.00, 7.00, 5.00, 4.00, 3.50, 3.00, 2.75, 2.50, 2.25, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.25, 2.50, 2.75, 3.00, 3.50, 4.00, 5.00, 7.00, 9.00, 2.50, 2.00, 1.00, 2.00, 2.50], 'f')
            # Stop & Start Progression
            elif (progressionType == 5):
                return jarray.array([0.00, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 6.00, 1.50, 2.33, 1.50, 2.33, 1.50, 2.33], 'f')
            # Step-Up Progression
            elif (progressionType == 6):
                return jarray.array([0.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 5.00, 5.00, 5.00, 5.00, 5.00, 5.00, 7.00, 7.00, 7.00, 7.00, 7.00, 1.00, 1.00, 2.50, 2.00, 3.50], 'f')
            # Step-Down Progression
            elif (progressionType == 7):
                return jarray.array([0.00, 7.00, 7.00, 7.00, 7.00, 7.00, 5.00, 5.00, 5.00, 5.00, 5.00, 5.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 3.50, 2.50, 2.00, 1.00, 1.00], 'f')
            # 2-Wave Progression
            elif (progressionType == 8):
                return jarray.array([0.00, 1.00, 1.50, 2.00, 3.50, 5.00, 6.00, 8.50, 6.00, 5.00, 3.50, 2.00, 1.50, 1.00, 1.50, 2.00, 3.50, 5.00, 6.00, 8.50, 6.00, 5.00, 3.50, 1.50, 1.00, 1.00, 3.50, 1.00, 3.50, 1.00], 'f')
            # 3-Wave Progression
            elif (progressionType == 9):
                return jarray.array([0.00, 6.00, 5.00, 3.50, 2.00, 1.50, 1.00, 1.50, 2.00, 3.50, 5.00, 6.00, 8.00, 6.00, 5.00, 3.50, 2.00, 1.50, 1.00, 1.50, 2.00, 3.50, 5.00, 6.00, 8.00, 2.67, 1.00, 2.67, 1.00, 2.67], 'f')             