package mb.fc.engine.message;

public class IntMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private int value;
	
	public IntMessage(int type, int value) 
	{
		super(type);
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
