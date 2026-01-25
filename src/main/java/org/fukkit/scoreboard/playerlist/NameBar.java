package org.fukkit.scoreboard.playerlist;

import static org.fukkit.utils.FormatUtils.format;

import java.util.UUID;
import java.util.function.Function;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.FleXScoreboard;
import org.fukkit.scoreboard.entry.ScoredTeamEntry;
import org.fukkit.scoreboard.entry.TeamEntry;

import io.flex.commons.utils.StringUtils;

@SuppressWarnings("deprecation")
public class NameBar implements FleXScoreboard {

	private UUID uuid;
	
	private String id;
	
	private TeamEntry entry;
	
	private Objective objective;
	
	private Function<FleXPlayer, Integer> score;
	
	private BukkitRunnable task;
	
	/**
	 * This one's a bit confusing due to Minecraft limitations.
	 * Displays below player names can only show the same thing (with different scores), per viewer.
	 * <br><br>
	 * <b>Example: </b><i>
	 * <b>Player1</b> is given a Namebar that says "<u>Health</u>" and score that is their health.
	 * <b>Player2</b> is given a Namebar that says "<u>Hunger</u>"and score that is their hunger.
	 * <b>Player1</b> sees all {@code recipients} have "<u>Health</u>" with the score being their health.
	 * <b>Player2</b> sees all {@code recipients} have "<u>Hunger</u>" with the score being their hunger.</i>
	 * 
	 * @param entry What does the the peripheral tree see underneath all the recipients names.
	 * @param recipients Who does the peripheral tree see using this namebar.
	 */
	public NameBar(FleXPlayer player, String suffix, Function<FleXPlayer, Integer> score) {
		this(player, new TeamEntry(suffix), score);
	}

	/**
	 * This one's a bit confusing due to Minecraft limitations.
	 * Displays below player names can only show the same thing (with different scores), per viewer.
	 * <br><br>
	 * <b>Example: </b><i>
	 * <b>Player1</b> is given a Namebar that says "<u>Health</u>" and score that is their health.
	 * <b>Player2</b> is given a Namebar that says "<u>Hunger</u>"and score that is their hunger.
	 * <b>Player1</b> sees all {@code recipients} have "<u>Health</u>" with the score being their health.
	 * <b>Player2</b> sees all {@code recipients} have "<u>Hunger</u>" with the score being their hunger.</i>
	 * 
	 * @param entry What does the the peripheral tree see underneath all the recipients names.
	 * @param recipients Who does the peripheral tree see using this namebar.
	 */
	public NameBar(FleXPlayer player, TeamEntry entry, Function<FleXPlayer, Integer> score) {
		
		this.id = StringUtils.generate(11, false).toLowerCase() + "_name";
		
		this.uuid = player.getUniqueId();
		
		this.score = score;
		
		this.entry = entry;
		
		if (player == null || !player.isOnline())
			return;
		
		Scoreboard scoreboard = player.getPlayer().getScoreboard();
		
		this.objective = scoreboard.getObjective(this.id);
		
		try {
			
			Class.forName("org.bukkit.scoreboard.Criteria");
			
			// TODO This is different in 1.8, add to CraftFukkit Implementation class
			if (this.objective == null)
				this.objective = scoreboard.registerNewObjective(this.id, Criteria.DUMMY, entry.toString());
			
		} catch (ClassNotFoundException e) {
			this.objective = scoreboard.registerNewObjective(this.id, "dummy");
		}
		
		this.objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		
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
	
	public void setTeamEntry(ScoredTeamEntry entry) {
		
		this.entry = entry;
		
		this.onTick();
		
	}

	@Override
	public void setIntervals(long ticks) {
		
		this.onTick();
		
		if (this.task != null)
			this.task.cancel();
		
		this.task = new BukkitRunnable() {
			
			@Override
			public void run() {
				NameBar.this.onTick();
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
		
		Fukkit.getOnlinePlayers().forEach(fp -> {
			
			if (!fp.isOnline())
				return;
			
			this.objective.getScore(fp.getName()).setScore(this.score.apply(fp));
			this.objective.setDisplayName(this.entry.toString());
			
		});
		
	}
	
	public void clear() {
		
		try {
			
			if (this.task != null)
				this.task.cancel();
			
		} catch (IllegalStateException e) {}
		
		Objective obj = this.objective;
		
		if (obj != null)
			try {
				obj.unregister();
			} catch (IllegalStateException e2) {}
		
	}
	
	@Override
	public Objective getObjective() {
		return this.objective;
	}

	@Override
	public Scoreboard getScoreboard() {
		throw new UnsupportedOperationException("Namebar uses multiple recipients and cannot return a single Scoreboard.");
	}
	
}