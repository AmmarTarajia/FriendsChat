package friendschat;

import java.io.IOException;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class LocalClient {
	public LocalClient(String username, String ip, int startPort) {
		this.username = username;
		this.ip = ip;
		this.startPort = startPort;
		client = new Client();
	}
	
	private Client client;
	private int startPort;
	private String ip, username;
	
	public void start(JTextArea area, JList<String> list) throws IOException {
		client.start();
		client.connect(5000, ip, startPort, startPort + 1);
		client.getKryo().register(String.class);
		client.getKryo().register(int.class);
		client.getKryo().register(float.class);
		client.getKryo().register(boolean.class);
		client.getKryo().register(PacketType.class);
		client.getKryo().register(Packet.class);
		client.getKryo().register(String[].class);
		
		client.addListener(new Listener() {
			
			@Override
			public void received(Connection connection, Object received) {
				if(received instanceof String) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							area.append(received.toString());
						}
					});
				} else if(received instanceof Packet) {
					Packet p = (Packet) received;
					
					if(p.getType() == PacketType.REGISTER_FAIL) {
						stop();
						JOptionPane.showMessageDialog(null, "Username '" + username + "' taken on this server!", "FriendsChat - Username Taken", JOptionPane.WARNING_MESSAGE);
						System.exit(0);
					} else if(p.getType() == PacketType.USER_UPDATE) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								list.setListData((String[]) p.getData());
							}
						});
					}
				}
			}
		});
	}
	
	public void disconnect() {
		client.close();
	}
	
	public void stop() {
		disconnect();
		client.stop();
	}
	
	public Client getClient() {
		return this.client;
	}
	
	public int getStartPort() {
		return startPort;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
}