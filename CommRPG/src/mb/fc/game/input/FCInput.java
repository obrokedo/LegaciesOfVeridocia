package mb.fc.game.input;

import java.util.HashSet;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

public class FCInput implements KeyListener
{
	private HashSet<Integer> keysPressed;
	private HashSet<Integer> keysHeld;
	private int updateDelta = 0;
	private static final int UPDATE_TIME = 50;
	
	public FCInput() {
		super();
		keysPressed = new HashSet<Integer>();
		keysHeld = new HashSet<Integer>();
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
		return keysPressed.contains(keyCode) || keysHeld.contains(keyCode);
	}

	@Override
	public void setInput(Input input) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void keyPressed(int key, char c) {
		keysPressed.add(key);		
		keysHeld.add(key);
	}

	@Override
	public void keyReleased(int key, char c) {
		keysHeld.remove(key);		
	}
}
