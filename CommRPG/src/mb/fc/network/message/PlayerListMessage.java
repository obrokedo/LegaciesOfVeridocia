package mb.fc.network.message;

import java.util.ArrayList;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.MessageType;
import mb.fc.network.ClientInfo;

public class PlayerListMessage extends Message{
	private static final long serialVersionUID = 1L;

	private ArrayList<ClientInfo> clientInfos;

	public PlayerListMessage(ArrayList<ClientInfo> clientInfos)
	{
		super(MessageType.PLAYER_LIST);
		this.clientInfos = clientInfos;
	}

	public ArrayList<ClientInfo> getClientInfos() {
		return clientInfos;
	}
}
