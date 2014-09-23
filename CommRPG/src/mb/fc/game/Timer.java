package mb.fc.game;

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
			timerDelta -= timerUpdate;
			return true;
		}

		return false;
	}
}
