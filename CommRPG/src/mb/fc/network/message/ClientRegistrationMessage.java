package mb.fc.network.message;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.MessageType;

public class ClientRegistrationMessage extends Message {
	private static final long serialVersionUID = 1L;

	private String name;
	private int clientId;

	public ClientRegistrationMessage(String name,
			int clientId) {
		super(MessageType.CLIENT_REGISTRATION);
		this.name = name;
		this.clientId = clientId;
	}

	public String getName() {
		return name;
	}

	public int getClientId() {
		return clientId;
	}
}
