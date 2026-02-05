package org.fukkit.event.flow;

import org.fukkit.consequence.Report;
import org.fukkit.entity.FleXPlayer;

public class AsyncFleXPlayerOverwatchPreDownloadEvent extends AsyncFleXPlayerReplayPreDownloadEvent {
	
	private Report report;
	
	public AsyncFleXPlayerOverwatchPreDownloadEvent(FleXPlayer player, Report report) {
		
		super(player, "report-" + report.getReference() + ":" + player.getUniqueId().toString());
		
		this.report = report;
		
	}
	
	public Report getReport() {
		return this.report;
	}

}
