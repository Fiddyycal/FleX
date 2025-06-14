package org.fukkit.scoreboard.playerlist;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.FleXScoreboard;
import org.fukkit.scoreboard.entry.ScoredTeamEntry;
import org.fukkit.scoreboard.entry.TeamEntry;

import io.flex.commons.utils.StringUtils;

public class ListScore implements FleXScoreboard {

	private UUID uuid;
	
	private String id;
	
	private Objective objective;
	
	private ScoredTeamEntry entry;
	
	private Scoreboard scoreboard;
	
	private BukkitRunnable task;
	
	@SuppressWarnings("deprecation")
	public <T> ListScore(FleXPlayer player, ScoredTeamEntry entry) {
		
		this.uuid = player.getUniqueId();
		
		this.scoreboard = player.getPlayer().getScoreboard();
		
		this.id = StringUtils.generate(11, false).toLowerCase() + "_list";
		
		this.objective = this.getScoreboard().getObjective(this.id);
		
		if (this.objective == null)
			this.objective = this.getScoreboard().registerNewObjective(this.id, "dummy");
		
		this.entry = entry;
		
		this.objective.setDisplayName(this.entry.toString());
		this.objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		
		this.objective.getScore(ChatColor.RESET.toString()).setScore(this.entry.getScore());
		
		this.setIntervals(20L);
		
	}
	
	public TeamEntry getTeamEntry() {
		return this.entry;
	}
	
	public <T> void setTeamEntry(ScoredTeamEntry entry) {
		
		this.entry = entry;
		
		Team team = entry.getTeam();
		
		if (team != null)
			this.id = team.getName();
		
		this.entry.update();
		
	}

	@Override
	public void setIntervals(long ticks) {
		
		this.onTick();
		
		if (this.task != null)
			this.task.cancel();
		
		this.task = new BukkitRunnable() {
			
			@Override
			public void run() {
				ListScore.this.onTick();
			}
			
		};
		
		if (ticks < 0)
			return;
		
		this.task.runTaskTimer(Fukkit.getInstance(), 0L, ticks);
		
	}
	
	@Override
	public void clear() {
		
		if (this.objective != null) {
			this.objective.unregister();
			this.objective = null;
		}
		
		this.getScoreboard().clearSlot(DisplaySlot.PLAYER_LIST);
		
		if (this.entry != null) {
			Team team = this.entry.getTeam();
			
			if (team != null) {
				
				Iterator<String> it = team.getEntries().iterator();
				
				while(it.hasNext())
					team.removeEntry(it.next());
				
				team.unregister();
				
			}
			
			this.entry.setTeam(null);
		}
		
		try {
			
			if (this.task != null)
				this.task.cancel();
			
		} catch (IllegalStateException e) {}
		
	}

	@Override
	public void onTick() {
		
		if (Bukkit.getPlayer(this.uuid) == null) {
			
			this.clear();
			return;
			
		}
		
		this.objective.getScore(ChatColor.RESET.toString()).setScore(this.entry.getScore());
		this.objective.setDisplayName(this.entry.toString());
		
	}
	
	@Override
	public Objective getObjective() {
		return this.objective;
	}
	
	@Override
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

}
