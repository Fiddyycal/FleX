package org.fukkit.flow;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.World;
import org.fukkit.Fukkit;
import org.fukkit.consequence.EvidenceType;
import org.fukkit.consequence.Report;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.handlers.FlowLineEnforcementHandler;
import org.fukkit.recording.Recording;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.ThemeUtils;

import io.flex.commons.Nullable;
import io.flex.commons.file.DataFile;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;

public class Overwatch extends Recording {

	private Report report;
	
	public Overwatch(Report report, @Nullable FleXPlayer... record) throws FileAlreadyExistsException {
		
		super(FlowLineEnforcementHandler.flowPath() + report.getPlayer().getUniqueId().toString() + File.separator, 
				"flow",
				report.getPlayer().getPlayer().getWorld(), 
				400/*20 seconds*/,
				record_players(report, record));
		
		this.uuid = report.getPlayer().getUniqueId();
		
		DataFile<HashMap<UUID, String[]>> data = this.getData();
		
		data.setTag("UniqueId", this.uuid.toString());
		data.setTag("Report", (this.report = report).getReference());
		
	}
	
	protected Overwatch(String path) {
		super(path);
	}
	
	public static Overwatch download(Report report) {
		
		if (!ArrayUtils.contains(report.getReason().getRequiredEvidence(), EvidenceType.RECORDING_REFERENCE))
			throw new UnsupportedOperationException("report type " + report.getReason() +  " does not contain recording reference as a required evidence type.");
		
		return new Overwatch(report.getEvidence()[0]);
		
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

	@Override
	public void onPlayerDisconnect(FleXPlayer player) {
		
		// Not our suspect...
		if (!player.getUniqueId().equals(this.report.getPlayer().getUniqueId()))
			return;
		
		FleXPlayer by = this.report.getBy();
		
		Theme theme = by.getTheme();
		
		Variable<?>[] variables = ThemeUtils.getNameVariables(player, theme);
		
		String display = player.getDisplayName(theme);
		
		this.end("Suspect disconnected.");
		
		BukkitUtils.runLater(() -> {
			
			boolean banned = player.isBanned();
			
			String[] ban = { theme.format("<flow><sc>" + display + "<reset> <failure>has been banned, thank you for your report<pp>.") };
			
			if (by.isOnline())
				by.sendMessage(banned ? ban : ThemeMessage.FLOW_RECORDING_INTERRUPTED.format(theme, by.getLanguage(), variables));
			
			// TODO re-stage if banned == false
			
		});
		
	}
	
	public Report getReport() {
		return this.report;
	}

}
