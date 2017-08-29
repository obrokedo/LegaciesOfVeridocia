package mb.jython;

import java.io.Serializable;

public abstract class JConfigurationValues implements Serializable {
	private static final long serialVersionUID = 1L;

	public abstract String[] getStartingHeroIds();
	public abstract String getStartingMapData();
	public abstract String getStartingState();
	public abstract String getStartingLocation();
	public abstract int getMaxPartySize();
	public abstract String[] getWeaponTypes();
	public abstract String[] getMovementTypes();
	public abstract String[] getTerrainTypes();
	public abstract String[] getAffinities();
	public abstract int getTerrainEffectAmount(String terrainType);
	public abstract boolean isAffectedByTerrain(String movementType);
	public abstract int getMovementCosts(String movementType, String terrainType);
	public abstract int getBattleBackgroundImageIndexByTerrainType(String terrainType);
	public abstract int getHeroPromotionLevel();
	public abstract void clearPythonModules();
}
