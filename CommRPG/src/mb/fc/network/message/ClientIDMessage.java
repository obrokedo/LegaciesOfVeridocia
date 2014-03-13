package mb.fc.network.message;

import mb.fc.engine.message.Message;

public class ClientIDMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private int clientId;
	
	public ClientIDMessage(int clientId) {
		super(Message.MESSAGE_CLIENT_ID);
		this.clientId = clientId;
	}

	public int getClientId() {
		return clientId;
	}
}
