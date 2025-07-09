package org.fukkit.fle.flow;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.fukkit.Fukkit;
import org.fukkit.consequence.Report;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.handlers.FlowLineEnforcementHandler;
import org.fukkit.recording.Recorded;
import org.fukkit.recording.Recording;

import io.flex.commons.Nullable;
import io.flex.commons.utils.FileUtils;
import io.flex.commons.utils.StringUtils;

public class Replay extends Recording {

	private boolean anonymous = false;
	
	public Replay(Report report, @Nullable FleXPlayer... record) {
		
		super(FlowLineEnforcementHandler.flowPath() + report.getPlayer().getUniqueId().toString() + File.separator + report.getReference(), 
				unique_file_name(report), 
				report.getPlayer().getPlayer().getWorld(), 
				record_players(report, record));
		
		this.getData().setTag("Report", report.getReference());
		
	}
	
	public Replay(Report report, boolean anonymous, FleXPlayer... watchers) {
		
		super(FlowLineEnforcementHandler.flowPath() + report.getPlayer().getUniqueId().toString() + File.separator + report.getReference(), 
				unique_file_name(report), 
				watchers);
		
		this.anonymous = anonymous;
		
		if (anonymous)
			this.getRecorded().forEach(r -> {
				
				if (r instanceof Recorded == false)
					return;
				
				Recorded recorded = (Recorded) r;
				
				if (recorded.getUniqueId().equals(report.getPlayer().getUniqueId()))
					recorded.setName(ChatColor.RED + "The Suspect");
					
				else recorded.setName(StringUtils.generate(16, true));
				
			});
		
	}
	
	public boolean isAnonymous() {
		return this.anonymous;
	}
	
	private static FleXPlayer[] record_players(Report report, @Nullable FleXPlayer... extra) {
		
		World world = report.getPlayer().getPlayer().getWorld();
		
		Set<FleXPlayer> players = world.getPlayers().stream().map(p -> Fukkit.getPlayerExact(p)).collect(Collectors.toSet());
		
		for (FleXPlayer other : extra) {
			
			if (players.stream().anyMatch(p -> p.getUniqueId().equals(other.getUniqueId())))
				continue;
			
			players.add(other);
			
		}
		
		return players.toArray(new FleXPlayer[players.size()]);
		
	}
	
	private static String unique_file_name(Report report) {
		
		if (report == null)
			throw new UnsupportedOperationException("report cannot be null");
		
		return "flow-" + FileUtils.getTimeStamp(report.getTime());
		
	}

}
