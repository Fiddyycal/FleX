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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class NameTag implements FleXScoreboard {

	private UUID uuid;
	
	private String id;
	
	private TeamEntry entry;
	
	private Map<UUID, TeamEntry> scoreboards = new HashMap<UUID, TeamEntry>();
	
	private BukkitRunnable task;
	
	public NameTag(FleXPlayer player, String prefix, FleXPlayer... recipients) {
		this(player, prefix, null, recipients);
	}
	
	public NameTag(FleXPlayer player, String prefix, String suffix, FleXPlayer... recipients) {
		this(player, new TeamEntry(prefix, suffix), recipients);
	}
	
	public NameTag(FleXPlayer player, TeamEntry entry, FleXPlayer... recipients) {
		
		this.id = StringUtils.generate(11, false).toLowerCase() + "_tag";
		
		this.uuid = player.getUniqueId();
		
		this.entry = entry;
		
		if (recipients != null)
			this.addRecipients(recipients);
		
		this.setIntervals(20L);
		
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
	
	public Set<FleXPlayer> getRecipientsUnsafe() {
		return this.scoreboards.keySet().stream().map(u -> Fukkit.getPlayer(u)).collect(Collectors.toSet());
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
			
			FleXPlayer player = Fukkit.getPlayer(this.uuid);
			
			if (player == null || !player.isOnline()) {
				
				this.clear();
				return;
				
			}
			
			try {
				
				// TODO This is new and not in 1.8 add to CraftFukkit Implementation class
				//team.setColor(ChatColor.RED);
				
				Class.forName("org.bukkit.scoreboard.Team.Option");

				// TODO This is different in 1.8, add to CraftFukkit Implementation class
				team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
				
			} catch (ClassNotFoundException e) {
				team.setNameTagVisibility(NameTagVisibility.ALWAYS);
			}
			
			team.addEntry(player.getName());
			
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
		
		FleXPlayer player = Fukkit.getPlayer(this.uuid);
		
		if (player == null || !player.isOnline()) {
			
			this.clear();
			return;
			
		}
		
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
				
			}
			
			return remove;
			
		});
		
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
	
}
