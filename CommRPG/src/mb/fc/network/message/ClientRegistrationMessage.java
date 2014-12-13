package mb.fc.network.message;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.MessageType;

public class ClientRegistrationMessage extends Message {
	private String name;
	private int clientId;
	private int heroAmount;

	public ClientRegistrationMessage(String name,
			int clientId, int heroAmount) {
		super(MessageType.CLIENT_REGISTRATION);
		this.name = name;
		this.clientId = clientId;
		this.heroAmount = heroAmount;
	}

	public String getName() {
		return name;
	}

	public int getClientId() {
		return clientId;
	}

	public int getHeroAmount() {
		return heroAmount;
	}
}
