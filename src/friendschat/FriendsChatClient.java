package friendschat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class FriendsChatClient {
	public FriendsChatClient(String username, String ip) {
		this.username = username;
		this.ip = ip;
		client = new LocalClient(username, ip, 49159);
	}
	
	protected String username, ip;
	protected JFrame frame = new JFrame();
	protected JPanel mainPanel = new JPanel(), southPanel = new JPanel(), onlinePanel = new JPanel();
	protected JTextArea text = new JTextArea();
	protected JTextField input = new JTextField();
	protected JList<String> list = new JList<String>();
	protected JLabel onlineLabel = new JLabel("Online Users:");
	protected JScrollPane textPane = new JScrollPane(text), users = new JScrollPane(list);
	protected JButton send = new JButton("Send");
	protected LocalClient client;
	
	public void run() throws IOException {
		((DefaultCaret) text.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setEditable(false);
		input.setEditable(true);
		frame.setTitle("FriendsChat - " + username + " (" + ip + ")");
		frame.setLayout(new BorderLayout());
		southPanel.setLayout(new BorderLayout());
		mainPanel.setLayout(new BorderLayout());
		onlinePanel.setLayout(new BorderLayout());
		mainPanel.add(textPane, BorderLayout.CENTER);
		southPanel.add(input, BorderLayout.CENTER);
		southPanel.add(send, BorderLayout.EAST);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		onlinePanel.add(users, BorderLayout.CENTER);
		onlinePanel.add(onlineLabel, BorderLayout.NORTH);
		frame.setMinimumSize(new Dimension(800, 480));
		frame.add(mainPanel, BorderLayout.CENTER);
		frame.add(onlinePanel, BorderLayout.EAST);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		list.setSelectionModel(new DefaultListSelectionModel() {
			private static final long serialVersionUID = 1L;
			public void setAnchorSelectionIndex(final int anchorIndex) {}
			public void setLeadAnchorNotificationEnabled(final boolean flag) {}
			public void setLeadSelectionIndex(final int leadIndex) {}
			public void setSelectionInterval(final int index0, final int index1) {}
		});
		
		frame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame, 
		            "Are you sure to close this window?", "FriendsChat - Confirmation", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
		        	client.getClient().sendTCP(new Packet(PacketType.DISCONNECT));
		        	client.stop();
		        	frame.dispose();
		            System.exit(0);
		        }
		    }
		});
		
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//send message
				if(sendMessage(input.getText()))
					input.setText("");
			}
		});
		
		input.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					//send message
					if(sendMessage(input.getText()))
						input.setText("");
				}
			}
		});
		
		frame.pack();
		frame.setVisible(true);
		frame.setSize(800, 480);
		client.start(text, list);
		client.getClient().sendTCP(new Packet(PacketType.REGISTER, username));
	}
	
	public boolean sendMessage(String message) {
		if(!message.trim().isEmpty()) {
			client.getClient().sendTCP(message + "\n");
			return true;
		}
		
		return false;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getIP() {
		return this.ip;
	}
	
	public LocalClient getClient() {
		return this.client;
	}
	
	public static void main(String[] args) {
		FriendsChatClient client = new FriendsChatClient(JOptionPane.showInputDialog(null, "Enter username: ", "FriendsChat - Login", JOptionPane.QUESTION_MESSAGE),
				JOptionPane.showInputDialog(null, "Enter IP of room: ", "FriendsChat - Login", JOptionPane.QUESTION_MESSAGE));
		
		try {
			client.run();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Server not found!", "FriendsChat - Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		}
	}
}