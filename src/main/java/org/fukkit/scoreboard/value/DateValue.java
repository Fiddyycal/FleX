package org.fukkit.scoreboard.value;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateValue {

	private String time;
	private String date;
	
	public DateValue() {
		
		Date now = new Date();
		
		SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy");
		SimpleDateFormat time = new SimpleDateFormat("hh:mm a z");
		
		this.time = time.format(now);
		this.date = date.format(now);
		
	}
	
	public String getTime() {
		return this.time;
	}

	public String getDate() {
		return this.date;
	}
	
}
