package org.fukkit.recording;

public class RecordingContext {
	
	public static final int NONE = 0, REPORT = 1, GAME = 2, OTHER = 3;

	public static final RecordingContext EMPTY = new RecordingContext(NONE, null);
	
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
