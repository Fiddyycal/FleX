package io.flex.commons.socket;

import java.util.Map.Entry;

public class Data implements Entry<String, String> {

	private int port;
	
	private String key, value;
	
	public Data(String key, String value, int sender) {
		this.key = key;
		this.value = value;
		this.port = sender;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public String setValue(String value) {
		return this.value = value;
	}
	
	public int getSender() {
		return this.port;
	}
	
}
