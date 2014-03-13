package mb.fc.engine.message;

public class ShopMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private double buyPercent;
	private double sellPercent;
	private int[] itemIds;
	
	public ShopMessage(double buyPercent, double sellPercent,
			int[] itemIds) {
		super(Message.MESSAGE_SHOW_SHOP);
		this.buyPercent = buyPercent;
		this.sellPercent = sellPercent;
		this.itemIds = itemIds;
	}

	public double getBuyPercent() {
		return buyPercent;
	}

	public double getSellPercent() {
		return sellPercent;
	}

	public int[] getItemIds() {
		return itemIds;
	}
}
