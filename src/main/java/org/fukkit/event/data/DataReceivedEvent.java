package org.fukkit.event.data;

import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataCommand;

public class DataReceivedEvent extends DataEvent {

	private DataCommand command;
	
	public DataReceivedEvent(Data data, DataCommand command) {
		
		super(data);
		
		this.command = command;
		
	}
	
	public DataCommand getCommand() {
		return this.command;
	}
	
	public int getSender() {
		return this.getData().getSender();
	}

}
