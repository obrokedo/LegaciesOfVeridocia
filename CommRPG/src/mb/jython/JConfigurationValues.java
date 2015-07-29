package mb.jython;

import java.io.Serializable;

public abstract class JConfigurationValues implements Serializable {
	private static final long serialVersionUID = 1L;

	public abstract String[] getWeaponTypes();
	public abstract String[] getMovementTypes();
	public abstract String[] getTerrainTypes();
	public abstract int getTerrainEffectAmount(String terrainType);
	public abstract boolean isAffectedByTerrain(String movementType);
	public abstract int getMovementCosts(String movementType, String terrainType);
}
