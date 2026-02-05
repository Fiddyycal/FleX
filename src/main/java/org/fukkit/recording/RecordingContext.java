package org.fukkit.recording;

public class RecordingContext {
	
	public static final int REPORT = 0, GAME = 1, OTHER = 2;
	
	private String metadata;
	
	private RecordingContext(String metadata) {
		this.metadata = metadata;
	}
	
	@Override
	public String toString() {
		return this.metadata;
	}
	
	public static RecordingContext of(int type, String metadata) {
		return new RecordingContext(type + ":" + metadata);
	}
	
}
