package com.velocity.event;

import io.flex.commons.socket.Data;

public class AsyncDataEvent {

	private Data data;
	
	public AsyncDataEvent(Data data) {
		
		super();
		
		this.data = data;
		
	}
	
	public Data getData() {
		return this.data;
	}

}
