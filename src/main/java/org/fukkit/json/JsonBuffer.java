package org.fukkit.json;

import java.io.Serializable;

public class JsonBuffer implements CharSequence, Serializable {
	
	private static final long serialVersionUID = 6094766172447703133L;
	
	private String raw = "";
	
	private String json = "";
	
	public JsonBuffer append(JsonComponent component) {
		
		this.raw = this.raw + component.getText();
		
		this.json = this.json + (this.json.equals("") ? "" : ",") + component;
		
		return this;
		
	}
	
	public JsonBuffer replace(String replace, JsonComponent component) {
		
		if (!this.raw.contains(replace))
			return this;
		
		this.raw = this.raw.replaceAll(replace, component.getText());
		
		this.json = this.json.replaceAll(replace, "\"}," + component + ",{\"text\":\"");
		
		return this;
		
	}
	
	public String getRaw() {
		return this.raw;
	}
	
	@Override
	public int length() {
		return this.raw.length();
	}

	@Override
	public char charAt(int index) {
		return this.raw.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return this.raw.subSequence(start, end);
	}
	
	@Override
	public String toString() {
		return "[\"\"," + this.json + "]";
	}
	
}
