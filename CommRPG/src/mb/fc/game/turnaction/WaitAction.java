package mb.fc.game.turnaction;

public class WaitAction extends TurnAction
{
	private static final long serialVersionUID = 1L;
	
	public int waitAmt = 5;
	public WaitAction() {
		super(TurnAction.ACTION_WAIT);			
	}		
	
	public WaitAction(int amt) {
		super(TurnAction.ACTION_WAIT);
		waitAmt = amt;
	}		
}