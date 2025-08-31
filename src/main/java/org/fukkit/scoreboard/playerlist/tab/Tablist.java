package org.fukkit.scoreboard.playerlist.tab;

import static org.bukkit.ChatColor.COLOR_CHAR;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.FleXScoreboard;
import org.fukkit.scoreboard.entry.NamedTeamEntry;
import org.fukkit.scoreboard.entry.TeamEntry;

import io.flex.commons.reflect.Method;

public class Tablist implements FleXScoreboard {
	
	private UUID uuid;
	
	private Scoreboard scoreboard;
	
	private List<NamedTeamEntry> entries = new LinkedList<NamedTeamEntry>();
	
	private BukkitRunnable task;
	
	private String[] unique = {
			
			COLOR_CHAR + "1" + COLOR_CHAR + "0",
			COLOR_CHAR + "1" + COLOR_CHAR + "1",
			COLOR_CHAR + "1" + COLOR_CHAR + "2",
			COLOR_CHAR + "1" + COLOR_CHAR + "3",
			COLOR_CHAR + "1" + COLOR_CHAR + "4",
			COLOR_CHAR + "1" + COLOR_CHAR + "5",
			COLOR_CHAR + "1" + COLOR_CHAR + "6",
			COLOR_CHAR + "1" + COLOR_CHAR + "7",
			COLOR_CHAR + "1" + COLOR_CHAR + "8",
			COLOR_CHAR + "1" + COLOR_CHAR + "9",
			COLOR_CHAR + "1" + COLOR_CHAR + "a",
			COLOR_CHAR + "1" + COLOR_CHAR + "b",
			COLOR_CHAR + "1" + COLOR_CHAR + "c",
			COLOR_CHAR + "1" + COLOR_CHAR + "d",
			COLOR_CHAR + "1" + COLOR_CHAR + "e",
			COLOR_CHAR + "1" + COLOR_CHAR + "f",
			COLOR_CHAR + "1" + COLOR_CHAR + "k",
			COLOR_CHAR + "1" + COLOR_CHAR + "l",
			COLOR_CHAR + "1" + COLOR_CHAR + "m",
			COLOR_CHAR + "1" + COLOR_CHAR + "r",
			
			COLOR_CHAR + "2" + COLOR_CHAR + "0",
			COLOR_CHAR + "2" + COLOR_CHAR + "1",
			COLOR_CHAR + "2" + COLOR_CHAR + "2",
			COLOR_CHAR + "2" + COLOR_CHAR + "3",
			COLOR_CHAR + "2" + COLOR_CHAR + "4",
			COLOR_CHAR + "2" + COLOR_CHAR + "5",
			COLOR_CHAR + "2" + COLOR_CHAR + "6",
			COLOR_CHAR + "2" + COLOR_CHAR + "7",
			COLOR_CHAR + "2" + COLOR_CHAR + "8",
			COLOR_CHAR + "2" + COLOR_CHAR + "9",
			COLOR_CHAR + "2" + COLOR_CHAR + "a",
			COLOR_CHAR + "2" + COLOR_CHAR + "b",
			COLOR_CHAR + "2" + COLOR_CHAR + "c",
			COLOR_CHAR + "2" + COLOR_CHAR + "d",
			COLOR_CHAR + "2" + COLOR_CHAR + "e",
			COLOR_CHAR + "2" + COLOR_CHAR + "f",
			COLOR_CHAR + "2" + COLOR_CHAR + "k",
			COLOR_CHAR + "2" + COLOR_CHAR + "l",
			COLOR_CHAR + "2" + COLOR_CHAR + "m",
			COLOR_CHAR + "2" + COLOR_CHAR + "r",
			
			COLOR_CHAR + "3" + COLOR_CHAR + "0",
			COLOR_CHAR + "3" + COLOR_CHAR + "1",
			COLOR_CHAR + "3" + COLOR_CHAR + "2",
			COLOR_CHAR + "3" + COLOR_CHAR + "3",
			COLOR_CHAR + "3" + COLOR_CHAR + "4",
			COLOR_CHAR + "3" + COLOR_CHAR + "5",
			COLOR_CHAR + "3" + COLOR_CHAR + "6",
			COLOR_CHAR + "3" + COLOR_CHAR + "7",
			COLOR_CHAR + "3" + COLOR_CHAR + "8",
			COLOR_CHAR + "3" + COLOR_CHAR + "9",
			COLOR_CHAR + "3" + COLOR_CHAR + "a",
			COLOR_CHAR + "3" + COLOR_CHAR + "b",
			COLOR_CHAR + "3" + COLOR_CHAR + "c",
			COLOR_CHAR + "3" + COLOR_CHAR + "d",
			COLOR_CHAR + "3" + COLOR_CHAR + "e",
			COLOR_CHAR + "3" + COLOR_CHAR + "f",
			COLOR_CHAR + "3" + COLOR_CHAR + "k",
			COLOR_CHAR + "3" + COLOR_CHAR + "l",
			COLOR_CHAR + "3" + COLOR_CHAR + "m",
			COLOR_CHAR + "3" + COLOR_CHAR + "r",
			
			COLOR_CHAR + "4" + COLOR_CHAR + "0",
			COLOR_CHAR + "4" + COLOR_CHAR + "1",
			COLOR_CHAR + "4" + COLOR_CHAR + "2",
			COLOR_CHAR + "4" + COLOR_CHAR + "3",
			COLOR_CHAR + "4" + COLOR_CHAR + "4",
			COLOR_CHAR + "4" + COLOR_CHAR + "5",
			COLOR_CHAR + "4" + COLOR_CHAR + "6",
			COLOR_CHAR + "4" + COLOR_CHAR + "7",
			COLOR_CHAR + "4" + COLOR_CHAR + "8",
			COLOR_CHAR + "4" + COLOR_CHAR + "9",
			COLOR_CHAR + "4" + COLOR_CHAR + "a",
			COLOR_CHAR + "4" + COLOR_CHAR + "b",
			COLOR_CHAR + "4" + COLOR_CHAR + "c",
			COLOR_CHAR + "4" + COLOR_CHAR + "d",
			COLOR_CHAR + "4" + COLOR_CHAR + "e",
			COLOR_CHAR + "4" + COLOR_CHAR + "f",
			COLOR_CHAR + "4" + COLOR_CHAR + "k",
			COLOR_CHAR + "4" + COLOR_CHAR + "l",
			COLOR_CHAR + "4" + COLOR_CHAR + "m",
			COLOR_CHAR + "4" + COLOR_CHAR + "r",
			
			COLOR_CHAR + "5" + COLOR_CHAR + "r"
			
	};
	
	public Tablist(FleXPlayer player, NamedTeamEntry... entries) {
		
		this.uuid = player.getUniqueId();
		
		this.scoreboard = player.getPlayer().getScoreboard();
		
		if (entries.length > 80)
			entries = Arrays.copyOfRange(entries, 0, 80);
		
		if (entries != null)
			try {
				this.setTeamEntries(entries);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		
		this.setIntervals(60L);
		
	}
	
	public List<NamedTeamEntry> getTeamEntries() {
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
	
	public NamedTeamEntry getTeamEntry(Player player) {
		for (NamedTeamEntry entry : this.entries) {
			if (entry.getPlayer().equals(player)) {
				return entry;
			}
		}
		return null;
	}
	
	@Override
	public Objective getObjective() {
		return null;
	}
	
	@Override
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}
	
	/**
	 * @deprecated {@link Method} objects set in entries can thow {@link InvocationTargetException} when {@link TeamEntry#toString()} is called.
	 * @throws InvocationTargetException
	 */
	@Deprecated
	public void setTeamEntries(NamedTeamEntry... entries) throws InvocationTargetException {
		
		this.entries = new LinkedList<NamedTeamEntry>(Arrays.asList(entries));
		
		for (NamedTeamEntry entry : entries)
			this.dry(this.entries.indexOf(entry), entry);
		
	}

	@Override
	public void setIntervals(long ticks) {
		
		this.onTick();
		
		if (this.task != null)
			this.task.cancel();
		
		this.task = new BukkitRunnable() {
			
			@Override
			public void run() {
				Tablist.this.onTick();
			}
			
		};
		
		if (ticks < 0)
			return;
		
		this.task.runTaskTimer(Fukkit.getInstance(), 0L, ticks);
		
	}
	
	public void set(int index, NamedTeamEntry entry) {

		boolean set = index < this.entries.size();
		boolean add = index == this.entries.size();
		
		if (!set && !add)
			return;

		this.dry(index, entry);
		
		if (set)
			this.entries.set(index, entry);
		
		else if (add)
			this.entries.add(entry);
		
	}

	@Override
	public void onTick() {
		
		if (this.scoreboard == null) {
			this.clear();
			return;
		}
		
		FleXPlayer player = Fukkit.getPlayer(this.uuid);
		
		if (player == null || !player.isOnline()) {
			this.clear();
			return;
		}
		
        this.entries.stream().forEach(e -> {
			
			if (e.getImpute() != null || e.getAttribute() != null)
				e.update();
			
		});
		
	}
	
	@Override
	public void clear() {
		
		this.entries.stream().forEach(e -> {
			
			Fukkit.getTabFactory().remove(Fukkit.getPlayer(this.uuid), e.getPlayer());
			
			Team team = e.getTeam();
			
			if (team != null) {
				
				Iterator<String> it = team.getEntries().iterator();
				
				while(it.hasNext())
					team.removeEntry(it.next());
				
			}
			
			e.setTeam(null);
			
		});
		
		try {
			
			if (this.task != null)
				this.task.cancel();
			
		} catch (IllegalStateException e) {}
		
	}
	
	private void dry(int index, NamedTeamEntry entry) {
		
		Team team = this.getScoreboard().getTeam("!" + (index + 101) + "_tab");
		
		if (team == null)
			team = this.getScoreboard().registerNewTeam("!" + (index + 101) + "_tab");
		
		Iterator<String> it = team.getEntries().iterator();
		
		while(it.hasNext())
			team.removeEntry(it.next());
		
		entry.setTeam(team);
		entry.update();
		
		if (this.scoreboard == null)
			return;
		
		if (entry.getPlayer() == null) {
			
			String holder = this.unique[index > 80 ? 80 : index];
			
			entry.setName(holder + entry.getName());
			entry.setPlayer(Fukkit.getPlayerFactory().createBukkitFake(entry.getName(), entry.getSkin()));
			
		}
		
		Fukkit.getTabFactory().add(Fukkit.getPlayer(this.uuid), entry.getPlayer());
		
	}
	
}
