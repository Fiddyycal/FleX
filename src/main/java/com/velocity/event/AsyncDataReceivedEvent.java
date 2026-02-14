package com.velocity.event;

import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataCommand;

public class AsyncDataReceivedEvent extends AsyncDataEvent {

	private DataCommand command;
	
	public AsyncDataReceivedEvent(Data data, DataCommand command) {
		
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
