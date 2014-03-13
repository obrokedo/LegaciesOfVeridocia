package mb.fc.engine.message;


public class LocationMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	public int locX;
	public int locY;

	public LocationMessage(int messageType, int locX, int locY) {
		super(messageType);
		this.locX = locX;
		this.locY = locY;
	}
}
