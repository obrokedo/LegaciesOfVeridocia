package mb.fc.network.message;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.MessageType;

public class ClientIDMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private int clientId;

	public ClientIDMessage(int clientId) {
		super(MessageType.CLIENT_ID);
		this.clientId = clientId;
	}

	public int getClientId() {
		return clientId;
	}
}
