package friendschat;

import java.io.IOException;
import java.util.HashMap;

import javax.swing.JTextArea;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class LocalServer {
	public LocalServer(int startPort) {
		this.startPort = startPort;
		usernames = new HashMap<>();
		server = new Server();
	}
	
	private Server server;
	private int startPort;
	private HashMap<Integer, String> usernames;
	
	public void start(JTextArea area) throws IOException {
		server.start();
		server.bind(startPort, startPort + 1);
		server.getKryo().register(String.class);
		server.getKryo().register(int.class);
		server.getKryo().register(float.class);
		server.getKryo().register(boolean.class);
		server.getKryo().register(PacketType.class);
		server.getKryo().register(Packet.class);
		server.getKryo().register(String[].class);
		
		server.addListener(new Listener() {
			
			@Override
			public void received(Connection connection, Object received) {
				//System.out.println(connection.getID());
				if(received instanceof String) {
					server.sendToAllTCP("[" + usernames.get(connection.getID()) + "]: " + received.toString());
				} else if(received instanceof Packet) {
					Packet p = (Packet) received;
					
					if(p.getType() == PacketType.REGISTER) {
						//check username taken
						String name = p.getData().toString().trim();
						boolean found = false;
						if(name.length() > 32)
							name = name.substring(0, 32);
						for(String user : usernames.values()) {
							if(name.equals("SERVER") || user.equalsIgnoreCase(name)) {
								 connection.sendTCP(new Packet(PacketType.REGISTER_FAIL));
								 connection.close();
								 found = true;
								 break;
							}
						}
						
						if(!found) {
							usernames.put(connection.getID(), name);
							server.sendToAllTCP("User '" + name + "' has joined!\n");
							server.sendToAllTCP(new Packet(PacketType.USER_UPDATE, getNames()));
						}
					} else if(p.getType() == PacketType.DISCONNECT) {
						server.sendToAllTCP("User '" + usernames.get(connection.getID()) + "' has left!\n");
						usernames.remove(connection.getID());
						server.sendToAllTCP(new Packet(PacketType.USER_UPDATE, getNames()));
					}
				}
			}
		});
	}
	
	public String[] getNames() {
		String[] names = new String[usernames.values().size()];
		for(int i = 0; i < names.length; i++)
			names[i] = usernames.values().toArray()[i].toString();
		return names;
	}
	
	public void disconnect() {
		server.close();
	}
	
	public void stop() {
		disconnect();
		server.stop();
	}
	
	public Server getServer() {
		return this.server;
	}
	
	public int getStartPort() {
		return startPort;
	}
	
	public HashMap<Integer, String> getUsernames() {
		return this.usernames;
	}
}