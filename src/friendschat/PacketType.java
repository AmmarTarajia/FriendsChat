package friendschat;

public enum PacketType {
	REGISTER, REGISTER_SUCCESS, REGISTER_FAIL, DISCONNECT, KICK, USER_UPDATE;
	PacketType() {/*Satisfies kryonet*/}
}