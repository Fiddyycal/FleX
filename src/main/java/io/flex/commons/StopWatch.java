package io.flex.commons;

import java.util.concurrent.TimeUnit;

public class StopWatch {
	
	private long nano = System.nanoTime();
	
	public long getStarted() {
		return this.nano;
	}
	
	public long getTime() {
		return this.getTime(TimeUnit.MILLISECONDS);
	}
	
	public long getTime(TimeUnit unit) {
		switch (unit) {
		
		case DAYS:
			return this.getTime(TimeUnit.HOURS)/24;
		
		case HOURS:
			return this.getTime(TimeUnit.MINUTES)/60;
			
		case MINUTES:
			return this.getTime(TimeUnit.SECONDS)/60;
				
		case SECONDS:
			return this.getTime(TimeUnit.MILLISECONDS)/1000;
		
		case MILLISECONDS:
			return this.getTime(TimeUnit.MICROSECONDS)/1000;
		
		case MICROSECONDS:
			return this.getTime(TimeUnit.NANOSECONDS)/1000;
		
		case NANOSECONDS:
			return System.nanoTime() - this.nano;
		
		default:
			return this.nano;
		
		}
	}

}
