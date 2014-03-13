package mb.fc.engine.message;

public class DisplayAreaMessage extends Message 
{
	private static final long serialVersionUID = 1L;
	
	public int topX, topY;
	public int[][] moveableTiles;
	public boolean includeMiddle;

	public DisplayAreaMessage(int topX, int topY,
			int[][] moveableTiles, int type, boolean includeMiddle) {
		super(type);
		this.topX = topX;
		this.topY = topY;
		this.moveableTiles = moveableTiles;
		this.includeMiddle = includeMiddle;
	}	
}
