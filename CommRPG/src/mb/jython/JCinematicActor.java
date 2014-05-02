package mb.jython;

public interface JCinematicActor 
{
	public int getMoveUpdate();	// 20
	public int getNodHeadDuration(); // 500
	public int getQuiverUpdate(); // 25
	public int getTrembleUpdate(); // 13
	public int getAnimUpdateAfterSE(); // 500
	public float getAnimSpeedForMoveSpeed(float moveSpeed); // (469.875 / speed); 
}
