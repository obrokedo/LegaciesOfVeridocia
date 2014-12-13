package mb.fc.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.MessageType;
import mb.fc.network.message.ClientIDMessage;
import mb.fc.network.message.ClientRegistrationMessage;
import mb.fc.network.message.PlayerListMessage;
import mb.tcp.network.Server;

public class TCPServer extends Server
{
	// TODO Base this on names or something
	private int id = 0;
	private Integer waitCount = 0;
	private Hashtable<Integer, ClientInfo> clientInfoById;
	private int startHeroId = 0;

	public TCPServer() {
		super(5000);
		this.clientInfoById = new Hashtable<Integer, ClientInfo>();
	}

	@Override
	public void clientClosed(int clientNumber) {

	}

	@Override
	public void serverStarted() {
		System.out.println("STARTED");

	}

	@Override
	public void serverClosing() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean messageRecieved(int clientNumber, Object message)
	{
		Message mess = (Message) message;
		switch (mess.getMessageType())
		{
			case PUBLIC_SPEECH:
				this.tellEveryone(message);
				break;
			case WAIT:
				synchronized (waitCount)
				{
					waitCount++;
					if (waitCount == this.clientOutputStreams.size())
					{
						waitCount = 0;
						tellEveryone(new Message(MessageType.CONTINUE));
					}
				}
				break;
			case CLIENT_REGISTRATION:
				ClientRegistrationMessage crm = (ClientRegistrationMessage) mess;
				synchronized (clientInfoById)
				{
					clientInfoById.put(crm.getClientId(), new ClientInfo(crm.getName(), crm.getClientId()));
					try {
						tellSomeone(new IntMessage(MessageType.CLIENT_HERO_START, startHeroId), this.clientOutputStreams.get(crm.getClientId()));
						startHeroId += crm.getHeroAmount();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				tellEveryone(new PlayerListMessage(getConnectedClientInfo()));
				break;
			case CLIENT_BROADCAST_HERO:
				tellEveryoneElse(message, clientOutputStreams.get(clientNumber));
				break;
			default:
				this.tellEveryone(message);
				break;
		}

		return true;
	}

	@Override
	public void clientJoined(int clientNumber, ObjectOutputStream writer)
			throws IOException {
		int newId;
		synchronized (clientInfoById)
		{
			newId = id++;
		}
		this.tellSomeone(new ClientIDMessage(newId), writer);
		System.out.println("JOINED");
	}

	public ArrayList<ClientInfo> getConnectedClientInfo()
	{
		return new ArrayList<ClientInfo>(clientInfoById.values());
	}
}
