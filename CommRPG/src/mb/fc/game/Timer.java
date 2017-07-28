package mb.fc.game;

/**
 * Keeps track of time that has passed in the game engine and indicates
 * when a specified amount of time has passed
 *
 * @author Broked
 *
 */
public class Timer
{
	private long timerUpdate;
	private long timerDelta;

	public Timer(long timerUpdate)
	{
		this.timerUpdate = timerUpdate;
	}

	public void update(long delta)
	{
		timerDelta += delta;
	}

	public boolean perform()
	{
		if (timerDelta >= timerUpdate)
		{
			timerDelta = 0;
			return true;
		}

		return false;
	}
}
