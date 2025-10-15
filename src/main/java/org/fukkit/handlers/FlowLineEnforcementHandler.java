package org.fukkit.handlers;

import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import org.fukkit.Fukkit;
import org.fukkit.ai.AIDriver;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.config.Configuration;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.fle.CriticalHitListeners;
import org.fukkit.recording.RecordingContext;
import org.fukkit.recording.RecordingState;
import org.fukkit.fle.CommandLogListeners;

import io.flex.FleX;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;

public class FlowLineEnforcementHandler {
	
	private boolean fle = false, flow = false;
	
	private AIDriver driver;
	
	private static boolean registered = false;
	
	public FlowLineEnforcementHandler() {
		
		if (registered)
			return;
		
		this.driver = Fukkit.getBridgeHandler().isCitizensEnabled() ? AIDriver.CITIZENS : AIDriver.FLEX;
		
		new CriticalHitListeners();
		new CommandLogListeners();
		
		registered = true;
		
	}
	
	public static boolean isRegistered() {
		return registered;
	}
	
	public AIDriver getAIDriver() {
		return this.driver;
	}
	
	public static String flowPath() {
		
		String path = Fukkit.getResourceHandler().getYaml(Configuration.DATA).getConfig().getString("FloW-Path", FleX.EXE_PATH + "/flex/data/flow/");
		
		path = path.replace("${server_absolute_path}", FleX.EXE_PATH);
		path = path.replace("${volumes_absolute_path}", new File(FleX.EXE_PATH).getParentFile().getAbsolutePath());
		
		return path;
		
	}
	
	public void setPending(FleXPlayer player) throws SQLException {
		
		SQLRowWrapper row = null;
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		RecordingContext context = RecordingContext.of(RecordingContext.REPORT, player.getUniqueId().toString());
		
		Set<SQLRowWrapper> rows = base.getRows("flex_overwatch", SQLCondition.where("context").is(context.toString()));
		
		for (SQLRowWrapper r : rows) {
			
			String state = r.getString("state");
			
			if (state.equals(RecordingState.STAGED.name()) || state.equals(RecordingState.RECORDING.name()))
				row = r;
			
		}
		
		long now = System.currentTimeMillis();
		
		// Prevents duplicates.
		if (row != null) {
			
			if (row.getString("state").equals(RecordingState.RECORDING.name()))
				row.set("data", Collections.emptyMap().toString());
			
			row.set("state", RecordingState.STAGED.name());
			row.set("last_updated", now);
			row.update();
			return;
			
		}
		
		add(context, RecordingState.STAGED);
		
	}
	
	public boolean setRecording(FleXPlayer player) throws SQLException {
		
		SQLRowWrapper row = null;
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		RecordingContext context = RecordingContext.of(RecordingContext.REPORT, player.getUniqueId().toString());
		
		Set<SQLRowWrapper> rows = base.getRows("flex_overwatch", SQLCondition.where("context").is(context.toString()));
		
		for (SQLRowWrapper r : rows) {
			
			String state = r.getString("state");
			
			if (state.equals(RecordingState.COMPLETE.name()))
				continue;
			
			if (state.equals(RecordingState.RECORDING.name()))
				return false;
			
			if (r.getString("state").equals(RecordingState.STAGED.name()))
				row = r;
			
		}
		
		long now = System.currentTimeMillis();
		
		// Prevents duplicates.
		if (row != null) {
			row.set("state", RecordingState.RECORDING.name());
			row.set("last_updated", now);
			row.update();
			return true;
		}
		
		add(context, RecordingState.RECORDING);
		return true;
		
	}
	
	public void clear(FleXPlayer player) throws SQLException {
		
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		String context = RecordingContext.REPORT + ":" + player.getUniqueId().toString();
		
		base.execute("DELETE FROM flex_overwatch WHERE context = '" + context + "' AND state = '" + RecordingState.STAGED.toString() + "'");
		base.execute("DELETE FROM flex_overwatch WHERE context = '" + context + "' AND state = '" + RecordingState.RECORDING.toString() + "'");
		
	}
	
	private static SQLRowWrapper add(RecordingContext context, RecordingState state) throws SQLException {
		
		long now = System.currentTimeMillis();

		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		LinkedHashMap<String, Object> entries = new LinkedHashMap<String, Object>();
		
		entries.put("context", context.toString());
		entries.put("state", state.name());
		entries.put("last_updated", now);
		entries.put("world_path", null);
		entries.put("data", Collections.emptyMap().toString());
		
		return base.addRow("flex_overwatch", entries);
		
	}
	
	public boolean isPending(FleXPlayer player) throws SQLException {
		
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		RecordingContext context = RecordingContext.of(RecordingContext.REPORT, player.getUniqueId().toString());
		
		Set<SQLRowWrapper> rows = base.getRows("flex_overwatch", SQLCondition.where("context").is(context.toString()));
		
		for (SQLRowWrapper r : rows) {
			
			if (r.getString("state").equals(RecordingState.STAGED.name()))
				return true;
			
		}
		
		return false;
		
	}
	
	public boolean isRecording(FleXPlayer player) throws SQLException {
		
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		RecordingContext context = RecordingContext.of(RecordingContext.REPORT, player.getUniqueId().toString());
		
		Set<SQLRowWrapper> rows = base.getRows("flex_overwatch", SQLCondition.where("context").is(context.toString()));
		
		for (SQLRowWrapper r : rows) {
			
			if (r.getString("state").equals(RecordingState.RECORDING.name()))
				return true;
			
		}
		
		return false;
		
	}
	
	public boolean isFleEnabled() {
		
		if (!this.fle)
			this.fle = ConfigHelper.getConfig(Configuration.FLEX).getBoolean("Flow-line-Enforcement.Enabled", false);
		
		return this.fle;
		
	}
	
	public boolean isFlowEnabled() {
		
		if (!this.flow)
			this.flow = ConfigHelper.getConfig(Configuration.FLEX).getBoolean("Flow-line-Enforcement.FloW", false);
		
		return this.isFleEnabled() && this.flow;
		
	}

}
