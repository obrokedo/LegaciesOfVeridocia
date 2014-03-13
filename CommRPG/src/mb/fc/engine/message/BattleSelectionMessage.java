package mb.fc.engine.message;

public class BattleSelectionMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private int selectionIndex, level;
	
	public BattleSelectionMessage(int messageType, int selectionIndex) 
	{
		super(messageType);
		this.selectionIndex = selectionIndex;
		this.level = 0;
	}
	
	public BattleSelectionMessage(int messageType, int selectionIndex, int level) 
	{
		super(messageType);
		this.selectionIndex = selectionIndex;
		this.level = level;
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}

	public int getLevel() {
		return level;
	}
}
