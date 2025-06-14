package net.md_5.fungee.event.data;

import io.flex.commons.socket.Data;
import net.md_5.bungee.api.plugin.Event;

public class DataEvent extends Event {

	private Data data;
	
	public DataEvent(Data data) {
		
		super();
		
		this.data = data;
		
	}
	
	public Data getData() {
		return this.data;
	}

}
