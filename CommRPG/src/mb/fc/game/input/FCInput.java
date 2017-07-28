package mb.fc.game.input;

import java.util.ArrayList;
import java.util.HashSet;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

public class FCInput implements KeyListener
{
	private HashSet<Integer> keysPressed;
	private ArrayList<Integer> keysHeld;
	private int updateDelta = 0;
	private static final int UPDATE_TIME = 50;
	private static final int DIRECTION_KEYS[] = {Input.KEY_RIGHT, Input.KEY_LEFT, Input.KEY_UP, Input.KEY_DOWN};

	public FCInput() {
		super();
		keysPressed = new HashSet<Integer>();
		keysHeld = new ArrayList<Integer>();
	}

	public void update(int delta)
	{
		updateDelta += delta;
		if (updateDelta >= UPDATE_TIME)
		{
			updateDelta -= UPDATE_TIME;
			keysPressed.clear();
		}
	}

	public void clear()
	{
		keysPressed.clear();
		keysHeld.clear();
	}

	public boolean isKeyDown(int keyCode)
	{
		return keysHeld.contains(keyCode);
	}

	public int getMostRecentDirection()
	{
		int recent = -1;
		int recentIndex = -1;
		for (int dir : DIRECTION_KEYS)
		{
			if (keysHeld.indexOf(dir) > recentIndex)
			{
				recentIndex = keysHeld.indexOf(dir);
				recent = dir;
			}
		}

		return recent;
	}

	@Override
	public void setInput(Input input) {
	}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void inputEnded() {

	}

	@Override
	public void inputStarted() {

	}

	public HashSet<Integer> getKeysPressed() {
		return keysPressed;
	}

	@Override
	public void keyPressed(int key, char c) {
		keysPressed.add(key);
		keysHeld.add(key);
	}

	@Override
	public void keyReleased(int key, char c) {
		keysHeld.remove((Object) key);
	}
}
