package org.fukkit.scoreboard.playerlist;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.FleXScoreboard;
import org.fukkit.scoreboard.entry.TeamEntry;

import io.flex.commons.utils.StringUtils;

import static org.fukkit.utils.FormatUtils.format;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class NameTag implements FleXScoreboard {
	
	private Map<UUID, TeamEntry> scoreboards = new HashMap<UUID, TeamEntry>();

	private String id, name;
	
	private TeamEntry entry;
	
	private BukkitRunnable task;
	
	private FleXPlayer player;
	
	public NameTag(String prefix) {
		this(prefix, null);
	}
	
	public NameTag(String prefix, String suffix) {
		this(new TeamEntry(prefix, suffix));
	}
	
	public NameTag(TeamEntry entry) {
		this.id = StringUtils.generate(12, false).toLowerCase() + "_tag";
		this.entry = entry;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getPrefix() {
		return format(this.entry.getPrefix());
	}
	
	public String getSuffix() {
		return format(this.entry.getSuffix());
	}
	
	public TeamEntry getTeamEntry() {
		return this.entry;
	}
	
	public Set<FleXPlayer> getRecipients() {
		return Collections.unmodifiableSet(this.scoreboards.keySet().stream().map(u -> Fukkit.getPlayer(u)).collect(Collectors.toSet()));
	}
	
	public void onAttach(FleXPlayer player) {
		this.player = player;
		this.name = player.isDisguised() && player.getDisguise() != null ? player.getDisguise().getName() : player.getName();
		this.update();
	}
	
	public void setTeamEntry(TeamEntry entry) {
		
		this.entry = entry;
		
		this.onTick();
		
		this.scoreboards.forEach((u, t) -> {
			
			Team team = t.getTeam();
			
			if (team != null)
				team.unregister();
			
			FleXPlayer player = Fukkit.getPlayer(u);
			
			Scoreboard scoreboard = player.getPlayer().getScoreboard();
			
			t.setTeam(scoreboard.registerNewTeam(this.id));
			
		});
		
	}
	
	public void addRecipients(FleXPlayer... recipients) {
		
		for (FleXPlayer p : recipients) {
			
			if (!p.isOnline())
				continue;
			
			Scoreboard scoreboard = p.getPlayer().getScoreboard();
			
			Team team = scoreboard.registerNewTeam(this.id);
			
			try {
				
				// TODO This is new and not in 1.8 add to CraftFukkit Implementation class
				//team.setColor(ChatColor.RED);
				
				Class.forName("org.bukkit.scoreboard.Team.Option");
				
				// TODO This is different in 1.8, add to CraftFukkit Implementation class
				team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
				
			} catch (ClassNotFoundException e) {
				team.setNameTagVisibility(NameTagVisibility.ALWAYS);
			}
			
			team.addEntry(this.name);
			
			TeamEntry entry = this.entry.clone();
			
			entry.setTeam(team);
			
			this.scoreboards.put(p.getUniqueId(), entry);
			
		}
		
	}
	
	public void removeRecipients(FleXPlayer... recipients) {
		
		for (FleXPlayer p : recipients)  {
			
			UUID uuid = p.getUniqueId();
			
			if (this.scoreboards.containsKey(uuid)) {
				
				TeamEntry entry = this.scoreboards.get(uuid);
				
				Team team = entry.getTeam();
				
				if (team != null)
					team.unregister();
				
				this.scoreboards.remove(uuid);
				
			}
			
		}
		
	}

	@Override
	public void setIntervals(long ticks) {
		
		this.onTick();
		
		if (this.task != null)
			this.task.cancel();
		
		this.task = new BukkitRunnable() {
			
			@Override
			public void run() {
				NameTag.this.onTick();
			}
			
		};
		
		if (ticks < 0)
			return;
		
		this.task.runTaskTimer(Fukkit.getInstance(), 0L, ticks);
		
	}
	
	@Override
	public void onTick() {
		
		if (this.player == null || !this.player.isOnline()) {
			
			this.clear();
			return;
			
		}
		
		String name = this.player.isDisguised() && this.player.getDisguise() != null ? this.player.getDisguise().getName() : this.player.getName();
		
		if (this.entry.getImpute() != null || this.entry.getAttribute() != null)
			this.entry.update();
		
		this.scoreboards.entrySet().removeIf(e -> {
			
			TeamEntry entry = e.getValue();
			
			Team team = entry.getTeam();
			
			if (team == null)
				return true;
			
			if (!team.getName().equals(this.id))
				return true;
			
			FleXPlayer recipient = Fukkit.getPlayer(e.getKey());
			
			boolean remove = recipient == null || !recipient.isOnline();
			
			if (remove) {
				
				if (team != null)
					team.unregister();
				
				entry.setTeam(null);
				
			} else {
				
				if (!name.equals(this.name)) {
					
					if (!team.hasEntry(name)) {
						
						for (String all : team.getEntries())
					        team.removeEntry(all);
						
					    team.addEntry(name);
					    
					}
					
				}
				
			}
			
			return remove;
			
		});
		
		this.name = name;
		
	}
	
	public boolean isRecipient(FleXPlayer player) {
		return this.scoreboards.containsKey(player.getUniqueId());
	}
	
	public void clear() {
		
		try {
			
			if (this.task != null)
				this.task.cancel();
			
		} catch (IllegalStateException e) {}
		
		this.scoreboards.values().removeIf(e -> {
			
			Team team = e.getTeam();

			if (team != null)
				team.unregister();
			
			return true;
			
		});
		
		this.scoreboards.clear();
		
	}

	@Override
	public Objective getObjective() {
		return null;
	}

	@Override
	public Scoreboard getScoreboard() {
		throw new UnsupportedOperationException("NameTag uses multiple recipients and cannot return a single Scoreboard.");
	}
	
	public void update() {
		this.onTick();
	}
	
}
