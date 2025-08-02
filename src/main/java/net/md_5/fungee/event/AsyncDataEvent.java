package net.md_5.fungee.event;

import io.flex.commons.socket.Data;
import net.md_5.bungee.api.plugin.Event;

public class AsyncDataEvent extends Event {

	private Data data;
	
	private boolean async;
	
	public AsyncDataEvent(Data data) {
		
		super();
		
		this.data = data;
		
	}
	
	public Data getData() {
		return this.data;
	}
	
	public boolean isAsynchronous() {
		return this.async;
	}

}
