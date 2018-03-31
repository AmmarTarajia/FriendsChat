package friendschat;

public class Packet {
	public Packet(PacketType type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	public Packet(PacketType type) {
		this.type = type;
	}
	
	public Packet() {/*Satisfies kryonet*/}
	
	private PacketType type;
	private Object data;
	
	public PacketType getType() {
		return this.type;
	}
	
	public Object getData() {
		return this.data;
	}
	
	public void setType(PacketType type) {
		this.type = type;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
}