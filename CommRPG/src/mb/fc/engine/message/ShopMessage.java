package mb.fc.engine.message;

/**
 * A message that indicates that the shop menu should be displayed
 * with the given items and buy/sell ratios
 *
 * @author Broked
 *
 */
public class ShopMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private double buyPercent;
	private double sellPercent;
	private int[] itemIds;

	public ShopMessage(double buyPercent, double sellPercent,
			int[] itemIds) {
		super(MessageType.SHOW_SHOP);
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

	public void setMenuTypeShopOptions()
	{
		this.messageType = MessageType.SHOW_SHOP;
	}

	public void setMenuTypeShopBuy()
	{
		this.messageType = MessageType.SHOW_SHOP_BUY;
	}

	public void setMenuTypeShopDeals()
	{
		this.messageType = MessageType.SHOW_SHOP_DEALS;
	}
}
