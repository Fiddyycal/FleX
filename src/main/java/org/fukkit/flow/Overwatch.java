package org.fukkit.flow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.UUID;

import org.fukkit.Fukkit;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.consequence.EvidenceType;
import org.fukkit.consequence.Report;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.flow.OverwatchCompleteEvent;
import org.fukkit.handlers.FlowLineEnforcementHandler;
import org.fukkit.recording.Recording;
import org.fukkit.recording.RecordingContext;
import org.fukkit.recording.RecordingState;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.ThemeUtils;

import io.flex.commons.Nullable;
import io.flex.commons.file.DataFile;
import io.flex.commons.file.Variable;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.ArrayUtils;

public class Overwatch extends Recording {

	private Report report;
	
	private static String name() {
		
		SimpleDateFormat format = new SimpleDateFormat("MM_dd_yyyy_HH_mm");
		
		return format.format(System.currentTimeMillis());
		
	}
	
	public Overwatch(Report report, @Nullable FleXPlayer... record) {
		
		super(FlowLineEnforcementHandler.flowPath() + report.getPlayer().getUniqueId().toString() + File.separator + "flow-" + name() + ".rec");
		
		this.uuid = report.getPlayer().getUniqueId();
		
		DataFile<HashMap<UUID, String[]>> data = this.getData();
		
		data.setTag("UniqueId", this.uuid.toString());
		data.setTag("Report", (this.report = report).getReference());
		
	}
	
	protected Overwatch(String path) {
		super(path);
	}
	
	public static Overwatch download(Report report) throws SQLException, IOException {
		
		if (!ArrayUtils.contains(report.getReason().getRequiredEvidence(), EvidenceType.RECORDING_REFERENCE))
			throw new UnsupportedOperationException("report type " + report.getReason() +  " does not contain recording reference as a required evidence type.");
		
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		RecordingContext context = RecordingContext.of(RecordingContext.REPORT, report.getPlayer().getUniqueId().toString());
		SQLRowWrapper row = base.getRow("flex_overwatch", SQLCondition.where("context").is(context.toString()), SQLCondition.where("state").is(RecordingState.COMPLETE.name()));
		
		if (row == null)
			return null;
		
		String path = ConfigHelper.flow_path + File.separator + report.getEvidence()[0].replace("/", File.separator);
	    File file = new File(path);
	    
	    if (file.getParentFile() != null)
	    	file.getParentFile().mkdirs();
	    
	    byte[] data = row.getByteArray("data");
	    
	    try (FileOutputStream fos = new FileOutputStream(file)) {
	        fos.write(data);
	    }
		
		return new Overwatch(file.getAbsolutePath());
		
	}
	
	/*private static FleXPlayer[] record_players(Report report, @Nullable FleXPlayer... extra) {
		
		World world = report.getPlayer().getPlayer().getWorld();
		
		Set<FleXPlayer> players = world.getPlayers().stream().map(p -> Fukkit.getPlayerExact(p)).collect(Collectors.toSet());
		
		for (FleXPlayer other : extra) {
			
			if (players.stream().anyMatch(p -> p != null && other != null && p.getUniqueId().equals(other.getUniqueId())))
				continue;
			
			players.add(other);
			
		}
		
		return players.toArray(new FleXPlayer[players.size()]);
		
	}*/

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

	@Override
	public void onComplete() {
		
		Fukkit.getEventFactory().call(new OverwatchCompleteEvent(this));
		
		// Must be cast before set to null in Recording.
		DataFile<?> file = this.getData();
		
		BukkitUtils.asyncThread(() -> {
			
			try {
				
				// TODO set evidence for all reports that match context critiria
				this.report.setEvidence(file.getParentFile().getName() + "/" + file.getName());
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			File zipped = file.zip();
			
			try {
				
				SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
				RecordingContext context = RecordingContext.of(RecordingContext.REPORT, this.getReport().getPlayer().getUniqueId().toString());
				SQLRowWrapper row = base.getRow("flex_overwatch", SQLCondition.where("context").is(context.toString()), SQLCondition.where("state").is(RecordingState.RECORDING.name()));
				
				if (row == null)
					return;
				
				row.set("state", RecordingState.COMPLETE.name());
				row.set("data", Files.readAllBytes(zipped.toPath()));
				row.update();
			
			} catch (SQLException | IOException e) {
				e.printStackTrace();
			}
			
		});
		
	}

}
