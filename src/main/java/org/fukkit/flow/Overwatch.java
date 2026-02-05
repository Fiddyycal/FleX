package org.fukkit.flow;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.fukkit.consequence.Report;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.handlers.FlowLineEnforcementHandler;
import org.fukkit.recording.Recording;
import org.fukkit.recording.RecordingContext;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.ThemeUtils;

import io.flex.commons.file.DataFile;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.StringUtils;

public class Overwatch extends Recording {

	private Report report;
	
	public Overwatch(Report report) throws SQLException {
		
		super(new File(FlowLineEnforcementHandler.flowPath() + "flow-" + StringUtils.generate(8, false)), RecordingContext.of(RecordingContext.REPORT, report.getPlayer().getUniqueId().toString()));
		
		DataFile<HashMap<UUID, String[]>> data = this.getData();
		
		data.setTag("Report", (this.report = report).getReference());
		
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

	@Override
	public void onComplete() {
		
		try {
			
			System.out.println("test 2");
			
			// TODO set evidence for all reports that match context critiria
			this.report.setEvidence(this.name + "/" + this.getData().getName());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
