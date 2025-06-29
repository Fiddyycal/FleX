package io.flex.commons.socket;

public enum DataCommand {
	
	REQUEST_DATA(DataType.TCP), PUBLISH_DATA(DataType.TCP), SEND_DATA(DataType.TCP), RETURN_DATA(DataType.TCP),
	
	REGISTER(DataType.UDP), PING(DataType.UDP), PING_ALL(DataType.UDP), HEARTBEAT(DataType.UDP);
	
	private DataType type;
	
	private DataCommand(DataType type) {
		this.type = type;
	}
	
	public DataType getType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		return this.name();
	}
	
}
