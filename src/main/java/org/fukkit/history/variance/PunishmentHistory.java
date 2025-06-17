package org.fukkit.history.variance;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import org.fukkit.consequence.Ban;
import org.fukkit.consequence.Punishment;
import org.fukkit.consequence.Kick;
import org.fukkit.consequence.Mute;
import org.fukkit.consequence.Report;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;

import io.flex.FleX.Task;

public class PunishmentHistory extends History<Punishment> {

	public PunishmentHistory(FleXHumanEntity player) {
		super(player);
		try {
			
			this.log = Punishment.download(player).stream().collect(Collectors.toMap(Punishment::getTime, c -> c,
		             (conviction, compare) -> {
		            	 Task.debug("Conviction", "Duplicate key in punishment history has been overwritten: " + conviction.getTime() + "/" + compare.getTime());
		                 return conviction;
		             }
		          ));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Set<Ban> banSet() {
		return this.log.values().stream().filter(c -> c instanceof Ban).map(Ban.class::cast).collect(Collectors.toSet());
	}
	
	public Set<Mute> muteSet() {
		return this.log.values().stream().filter(c -> c instanceof Mute).map(Mute.class::cast).collect(Collectors.toSet());
	}
	
	public Set<Kick> kickSet() {
		return this.log.values().stream().filter(c -> c instanceof Kick).map(Kick.class::cast).collect(Collectors.toSet());
	}
	
	public Set<Report> reportSet() {
		return this.log.values().stream().filter(c -> c instanceof Report).map(Report.class::cast).collect(Collectors.toSet());
	}

}
