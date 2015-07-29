package mb.jython;

public interface JLevelProgression {
	public float[] getProgressArray(String progressionType, boolean promoted);

	public abstract String[] getProgressionTypeList();
}
