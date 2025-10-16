package org.fukkit.flow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.fukkit.Fukkit;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.consequence.EvidenceType;
import org.fukkit.consequence.Report;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.flow.AsyncOverwatchCompleteEvent;
import org.fukkit.handlers.FlowLineEnforcementHandler;
import org.fukkit.recording.Recording;
import org.fukkit.recording.RecordingContext;
import org.fukkit.recording.RecordingState;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.ThemeUtils;

import io.flex.commons.file.DataFile;
import io.flex.commons.file.Variable;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.FileUtils;

public class Overwatch extends Recording {

	private Report report;
	
	public Overwatch(Report report) {
		
		super(new File(FlowLineEnforcementHandler.flowPath() + report.getPlayer().getUniqueId().toString()), RecordingContext.of(RecordingContext.REPORT, report.getPlayer().getUniqueId().toString()));
		
		DataFile<HashMap<UUID, String[]>> data = this.getData();
		
		data.setTag("Report", (this.report = report).getReference());
		
	}
	
	public static Overwatch download(Report report) throws SQLException, IOException {
		
		if (!ArrayUtils.contains(report.getReason().getRequiredEvidence(), EvidenceType.RECORDING_REFERENCE))
			throw new UnsupportedOperationException("report type " + report.getReason() +  " does not contain recording reference as a required evidence type.");
		
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		RecordingContext context = RecordingContext.of(RecordingContext.REPORT, report.getPlayer().getUniqueId().toString());
		SQLRowWrapper row = base.getRow("flex_recording", SQLCondition.where("context").is(context.toString()), SQLCondition.where("state").is(RecordingState.COMPLETE.name()));
		
		if (row == null)
			return null;
		
		String rec = report.getEvidence()[0];
		String parent = ConfigHelper.flow_path + File.separator + rec.split("/")[0];
	    File file = new File(parent + ".zip");
	    
	    if (file.getParentFile() != null)
	    	file.getParentFile().mkdirs();
	    
	    byte[] data = row.getByteArray("data");
	    
	    try (FileOutputStream fos = new FileOutputStream(file)) {
	        fos.write(data);
	    }
	    
	    FileUtils.unzip(file, parent);
	    
	    System.out.println("UNZIPPINGGGGGGGGGGGGGGGGGGGGGGGGGG: " + file.getAbsolutePath() + " to " + parent);
		
		return new Overwatch(report);
		
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
		
		Fukkit.getEventFactory().call(new AsyncOverwatchCompleteEvent(this));
		
		try {
			
			// TODO set evidence for all reports that match context critiria
			this.report.setEvidence(this.uid + "/" + this.getData().getName());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
