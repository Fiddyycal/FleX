package org.fukkit.recording;

public class RecordingContext {

	public static final int REPORT = 0, GAME = 1, OTHER = 2;
	
	private int type;
	
	private String context;
	
	private RecordingContext(int type, String context) {
		this.type = type;
		this.context = context;
	}
	
	@Override
	public String toString() {
		return this.type + ":" + this.context;
	}
	
	public static RecordingContext of(int type, String context) {
		return new RecordingContext(type, context);
	}
	
}
