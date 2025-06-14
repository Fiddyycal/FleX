package io.flex.commons;

public class Timer extends StopWatch {
	
	private long ms;
	
	private Timer(long length) {
		this.ms = length;
	}
	
	public long length() {
		return this.ms;
	}
	
	public void setLength(long length) {
		this.ms = length;
	}
	
	public boolean hasStopped() {
		return (this.getStarted() + this.ms) >= System.currentTimeMillis();
	}
	
	public void stop() {
		if (!this.hasStopped()) this.ms = System.currentTimeMillis();
	}

}
