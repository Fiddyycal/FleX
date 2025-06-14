package org.fukkit.event.consequence;

import org.fukkit.consequence.Report;

public class FleXReportEvent extends FleXConvictEvent {
	
	public FleXReportEvent(Report report, boolean async) {
		
		super(report, async);
		
		this.conviction = report;
		
	}
	
	@Override
	public Report getConviction() {
		return (Report) this.conviction;
	}

}
