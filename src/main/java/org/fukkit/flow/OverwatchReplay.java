package org.fukkit.flow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.fukkit.Fukkit;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.consequence.EvidenceType;
import org.fukkit.consequence.Report;
import org.fukkit.recording.RecordingContext;
import org.fukkit.recording.RecordingState;
import org.fukkit.recording.Replay;

import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.FileUtils;

public class OverwatchReplay extends Replay {

	private boolean anonymous = false;
	
	protected OverwatchReplay(File container) throws SQLException {
		super(container);
	}
	
	public static OverwatchReplay download(Report report) throws SQLException, IOException {
		
		if (report.getEvidence() == null || report.getEvidence().length == 0)
			throw new UnsupportedOperationException("report does not have a recording linked as evidence");
		
		if (!ArrayUtils.contains(report.getReason().getRequiredEvidence(), EvidenceType.RECORDING_REFERENCE))
			throw new UnsupportedOperationException("report type " + report.getReason() +  " does not contain recording reference as a required evidence type");
		
		if (!report.getEvidence()[0].contains("/"))
			throw new UnsupportedOperationException("report evidence link invalid format");
		
		RecordingContext context = RecordingContext.of(RecordingContext.REPORT, report.getPlayer().getUniqueId().toString());
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		SQLRowWrapper row = base.getRow("flex_recording", SQLCondition.where("context").is(context.toString()));
		
		if (row == null)
			return null;
		
		if (!row.getString("state").equals(RecordingState.COMPLETE.name()))
			throw new IOException("recording is not complete");
		
		String path = ConfigHelper.flow_path + File.separator + report.getEvidence()[0].split("/")[0];
	    File file = new File(path + ".zip");
	    
	    if (file.getParentFile() != null)
	    	file.getParentFile().mkdirs();
	    
	    byte[] data = row.getByteArray("data");
	    
	    try (FileOutputStream fos = new FileOutputStream(file)) {
	        fos.write(data);
	    }
	    
	    FileUtils.unzip(file, path);
	    
	    System.out.println("UNZIPPINGGGGGGGGGGGGGGGGGGGGGGGGGG: " + file.getAbsolutePath() + " to " + path);
		
		return new OverwatchReplay(new File(path));
		
	}
	
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	
	public boolean isAnonymous() {
		return this.anonymous;
	}

}
