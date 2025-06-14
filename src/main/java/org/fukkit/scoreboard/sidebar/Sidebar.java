package org.fukkit.scoreboard.sidebar;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.FleXScoreboard;
import org.fukkit.scoreboard.entry.ScoredTeamEntry;
import org.fukkit.scoreboard.entry.TeamEntry;
import org.fukkit.utils.FormatUtils;

import io.flex.FleX.Task;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.StringUtils;

public class Sidebar implements FleXScoreboard {

	private FleXPlayer player;
	
	private String id;
	
	private Objective objective;
	
	private String title;
	
	private List<ScoredTeamEntry> entries;
	
	private ChatColor[] used;
	
	private Scoreboard scoreboard;
	
	private BukkitRunnable task;
	
	@SuppressWarnings("deprecation")
	public Sidebar(FleXPlayer player, String title, ScoredTeamEntry... entries) {
		
		this.player = player;
		
		if (player instanceof FleXBot)
			return;
		
		this.scoreboard = player.getPlayer().getScoreboard();
		
		this.id = StringUtils.generate(11, false).toLowerCase() + "_side";
		
		this.title = FormatUtils.format(title);
		
		if (entries.length > 15)
			entries = Arrays.copyOfRange(entries, 0, 15);
		
		this.objective = this.scoreboard.getObjective(this.id);
		
		if (this.objective == null)
			this.objective = this.scoreboard.registerNewObjective(this.id, "dummy");
		
		if (entries != null)
			try {
				this.setTeamEntries(entries);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		
		this.objective.setDisplayName(this.title);
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		this.setIntervals(20L);
		
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public List<ScoredTeamEntry> getTeamEntries() {
		return this.entries;
	}
	
	public TeamEntry getTeamEntry(Team team) {
		for (TeamEntry entry : this.entries) {
			if (entry.getTeam() == team) {
				return entry;
			}
		}
		return null;
	}
	
	public ScoredTeamEntry getTeamEntry(int score) {
		for (ScoredTeamEntry entry : this.entries) {
			if (entry.getScore() == score) {
				return entry;
			}
		}
		return null;
	}
	
	@Override
	public Objective getObjective() {
		return this.objective;
	}
	
	@Override
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}
	
	public void setTitle(String title) {
		
		this.title = FormatUtils.format(title);
		
		if (this.objective != null)
			this.objective.setDisplayName(this.title);
		
	}
	
	/**
	 * @deprecated {@link Method} objects set in entries can thow {@link InvocationTargetException} when {@link TeamEntry#toString()} is called.
	 * @throws InvocationTargetException
	 */
	@Deprecated
	public void setTeamEntries(ScoredTeamEntry... entries) throws InvocationTargetException {
		
		if (this.player instanceof FleXBot)
			return;
		
		this.resetEntries();
		
		this.entries = Arrays.stream(entries).filter(e -> e != null).collect(Collectors.toList());
		
		for (ScoredTeamEntry entry : this.entries) {
			
			ChatColor holder = this.unique(entry);
			String unique = StringUtils.generate(14, false);
			
			Team team = this.getScoreboard().getTeam(unique);
			
			if (team == null)
				team = this.getScoreboard().registerNewTeam(unique);
			
			if (!team.getEntries().contains(holder.toString()))
				team.addEntry(holder.toString());
			
			entry.setTeam(team);
			entry.update();
			
			this.objective.getScore(holder.toString()).setScore(entry.getScore());
			this.used = ArrayUtils.add(ChatColor.class, this.used, holder);
			
		}
		
	}
	
	private ChatColor unique(TeamEntry entry) {
		
		if (entry.length() > 1 && entry.charAt(0) == ChatColor.COLOR_CHAR && !this.isUsed(ChatColor.getByChar(entry.charAt(1))))
			return ChatColor.getByChar(entry.charAt(1));
			
		else if (!this.isUsed(ChatColor.RESET))
			return ChatColor.RESET;
			
		else if (!this.isUsed(ChatColor.WHITE))
			return ChatColor.WHITE;
		
		return IntStream.range(0, ChatColor.values().length).mapToObj(i -> ChatColor.values()[i]).filter(c -> !this.isUsed(c)).findAny().orElse(null);
		
	}
	
	private boolean isUsed(ChatColor color) {
		return IntStream.range(0, this.used.length).anyMatch(i -> this.used[i] == color);
	}

	@Override
	public void setIntervals(long ticks) {
		
		this.onTick();
		
		if (this.task != null)
			this.task.cancel();
		
		this.task = new BukkitRunnable() {
			
			@Override
			public void run() {
				Sidebar.this.onTick();
			}
			
		};
		
		if (ticks < 0)
			return;
		
		this.task.runTaskTimer(Fukkit.getInstance(), 0L, ticks);
		
	}
	
	@Override
	public void onTick() {
		
		if (this.player instanceof FleXBot || this.player == null || this.player.getPlayer() == null || !this.player.isOnline()) {
			
			this.clear();
			return;
			
		}
		
		this.entries.stream().forEach(e -> {
			
        	if (e.isUnregistered()) {
        		
        		this.clear();
        		return;
        		
        	}
        	
			if (e.getImpute() != null || e.getAttribute() != null)
				e.update();
			
		});
		
	}
	
	@Override
	public void clear() {
		
		if (this.objective != null) {
			this.objective.unregister();
			this.objective = null;
		}
		
		this.resetEntries();
		
		try {
			
			if (this.task != null)
				this.task.cancel();
			
		} catch (IllegalStateException e) {}
		
	}
	
	private void resetEntries() {
		
		if (this.used != null && this.objective != null)
			Arrays.stream(this.used).forEach(h -> {
				this.objective.getScoreboard().resetScores(h.toString());
			});
		
		this.used = new ChatColor[] { ChatColor.MAGIC, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.ITALIC };
		
		if (this.entries != null)
			this.entries.removeIf(e -> {
				
				if (e.isUnregistered())
					return true;
				
				Team team = e.getTeam();
				
				if (team != null && !e.isUnregistered()) {
					
					Iterator<String> it = team.getEntries().iterator();
					
					try {
						
						while(it.hasNext())
							team.removeEntry(it.next());
						
					} catch (IllegalStateException n) {
						n.printStackTrace();
					}
					
					team.unregister();
					
				}
				
				try {
					
					e.setTeam(null);
					
				} catch (NullPointerException ex) {
					Task.debug("NPE", "Silently handling exception caused buy external logic.");
				}
				
				return true;
				
			});
		
	}
	
}
