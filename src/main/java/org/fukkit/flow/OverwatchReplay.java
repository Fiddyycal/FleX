package org.fukkit.flow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.World;
import org.fukkit.Fukkit;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.consequence.EvidenceType;
import org.fukkit.consequence.Report;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.recording.RecordingContext;
import org.fukkit.recording.RecordingState;
import org.fukkit.recording.Replay;

import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.FileUtils;

public class OverwatchReplay extends Replay {
	
	private FleXPlayer suspect;

	private boolean anonymous = false;
	
	protected OverwatchReplay(File container, FleXPlayer suspect) throws SQLException {
		
		super(container);
		
		this.suspect = suspect;
		
	}
	
	public static OverwatchReplay download(Report report) throws SQLException, IOException {
		
		if (report.getEvidence() == null || report.getEvidence().length == 0)
			throw new UnsupportedOperationException("report does not have a recording linked as evidence");
		
		if (!ArrayUtils.contains(report.getReason().getRequiredEvidence(), EvidenceType.RECORDING_REFERENCE))
			throw new UnsupportedOperationException("report type " + report.getReason() +  " does not contain recording reference as a required evidence type");
		
		if (!report.getEvidence()[0].contains("/"))
			throw new UnsupportedOperationException("report evidence link invalid format");
		
		String name = report.getEvidence()[0].split("/")[0];
		String path = ConfigHelper.flow_path + File.separator + name;
		
		File container = new File(path);
		
		if (container.exists())
			container.delete();
		
		RecordingContext context = RecordingContext.of(RecordingContext.REPORT, report.getPlayer().getUniqueId().toString());
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		SQLRowWrapper row = base.getRow("flex_recording", SQLCondition.where("context").is(context.toString()));
		
		if (row == null)
			return null;
		
		if (!row.getString("state").equals(RecordingState.COMPLETE.name()))
			throw new IOException("recording is not complete");
		
		File recordings = container.getParentFile();
		
		if (recordings != null)
			recordings.mkdirs();
		
	    File zip = new File(recordings.getAbsolutePath(), container.getName() + ".zip");
	    
	    byte[] data = row.getByteArray("data");
	    
	    try (FileOutputStream fos = new FileOutputStream(zip)) {
	        fos.write(data);
	    }
	    
	    FileUtils.unzip(zip, recordings.getAbsolutePath());
	     
		return new OverwatchReplay(container, report.getPlayer());
		
	}
	
	public FleXPlayer getSuspect() {
		return this.suspect;
	}
	
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	
	public boolean isAnonymous() {
		return this.anonymous;
	}
	
	@Override
	public void start(World world, int duration, FleXPlayer... watchers) {
		
		Location spawn = this.getRecorded().get(this.suspect.getUniqueId()).getFrames()
				
				.values()
				.stream()
				.filter(f -> f != null && f.getLocation() != null)
				.map(f -> {
					
					Location loc = f.getLocation();
					
					loc.setWorld(world);
					
					return loc;
					
				}).findFirst().orElse(null);
		
		if (spawn != null)
			this.spawn = spawn;
		
		this.setTranscript(this.suspect);
		
		super.start(world, duration, watchers);
		
	}

}
