package friendschat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JOptionPane;

public class FriendsChatServer extends FriendsChatClient {
	public FriendsChatServer(String ip) {
		super("SERVER (IP - " + ip + ")", "127.0.0.1");
		this.server = new LocalServer(client.getStartPort());
	}

	private LocalServer server;

	@Override
	public void run() throws IOException {
		server.start(text);
		super.run();
	}

	public static void main(String[] args) {
		try {
			String ip = getPublicIP();
			JOptionPane.showMessageDialog(null, "Your IP: " + ip, "FriendsChat - IP", JOptionPane.INFORMATION_MESSAGE);
			FriendsChatServer server = new FriendsChatServer(ip);
			server.run();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Server loading error!", "FriendsChat - Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static String getPublicIP() throws IOException {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			in.close();
			return ip;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}