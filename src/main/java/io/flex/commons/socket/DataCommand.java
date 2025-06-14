package io.flex.commons.socket;

public enum DataCommand {
	
	REQUEST_DATA, PUBLISH_DATA, SEND_DATA, RETURN_DATA;
	
	@Override
	public String toString() {
		return this.name();
	}
	
}
