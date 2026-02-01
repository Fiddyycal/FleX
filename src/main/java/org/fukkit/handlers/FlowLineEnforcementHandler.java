package org.fukkit.handlers;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.fukkit.Fukkit;
import org.fukkit.ai.AIDriver;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.config.Configuration;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.fle.ServerMonitor;
import org.fukkit.fle.listeners.AutoClickerListeners;
import org.fukkit.fle.listeners.CriticalHitListeners;
import org.fukkit.fle.listeners.HistoryListeners;
import org.fukkit.recording.RecordingContext;
import org.fukkit.recording.RecordingState;
import org.fukkit.recording.Replay;
import org.fukkit.utils.WorldUtils;

import io.flex.FleX;
import io.flex.commons.file.DataFile;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLMap;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.StringUtils;

public class FlowLineEnforcementHandler {
	
	private boolean fle = false, flow = false;
	
	private AIDriver driver;
	
	private static boolean registered = false;
	
	public FlowLineEnforcementHandler() {
		
		if (registered)
			return;
		
		this.driver = Fukkit.getBridgeHandler().isCitizensEnabled() ? AIDriver.CITIZENS : AIDriver.FLEX;
		
		ServerMonitor.start();
		
		new HistoryListeners();
		new CriticalHitListeners();
		new AutoClickerListeners();
		
		registered = true;
		
	}
	
	public static boolean isRegistered() {
		return registered;
	}
	
	public AIDriver getAIDriver() {
		return this.driver;
	}
	
	public static void watchReplay(Replay replay, FleXPlayer player, int duration) throws IOException, UnsupportedOperationException, IllegalStateException {
		
		if (replay.isPlaying()) {
			
			replay.addWatcher(player);
			return;
			
		}
		
		if (!Bukkit.isPrimaryThread())
			throw new IllegalArgumentException("Replay must be watched on the main thread.");
		
		File data = replay.getData();
		File parent = data.getParentFile();
		String name = parent.getName() + "-" + StringUtils.generate(5, false);
		
		boolean worldContents = ArrayUtils.contains(parent.list(), "region");
		
		World world = Bukkit.getWorld(name);
		
		if (world != null)
			throw new UnsupportedOperationException("A world with that name already exists.");
		
		if (worldContents)
			world = WorldUtils.copyWorld(parent.getAbsolutePath(), Bukkit.getWorldContainer().getPath() + File.separator + name);
		
		else {
			
			String path = ((DataFile<?>)data).getTag("Path");
			
			if (path != null) {
				
				File copy = new File(path);
				
				if (copy.exists() && copy.isDirectory() && ArrayUtils.contains(copy.list(), "region"))
					world = WorldUtils.copyWorld(path, Bukkit.getWorldContainer().getPath() + File.separator + name);
				
			}
			
			path = ((DataFile<?>)data).getTag("Map Path");
			
			if (path != null) {
				
				File copy = new File(path);
				
				if (copy.exists() && copy.isDirectory() && ArrayUtils.contains(copy.list(), "region"))
					world = WorldUtils.copyWorld(path, Bukkit.getWorldContainer().getPath() + File.separator + name);
				
			}
			
		}

		if (world == null) {
			
			WorldCreator creator = new WorldCreator(name);
			
		    creator.type(WorldType.FLAT);
		    creator.generateStructures(false);
		    
		    world = Bukkit.createWorld(creator);
			
		}
		
		replay.start(world, duration, player);
		
	}
	
	public static String flowPath() {
		
		String path = Fukkit.getResourceHandler().getYaml(Configuration.DATA).getString("FloW-Path", FleX.EXE_PATH + "/flex/data/flow/");
		
		path = path.replace("${server_absolute_path}", FleX.EXE_PATH);
		path = path.replace("${volumes_absolute_path}", new File(FleX.EXE_PATH).getParentFile().getAbsolutePath());
		
		return path;
		
	}
	
	public void clear(FleXPlayer player) throws SQLException {
		
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		String context = RecordingContext.REPORT + ":" + player.getUniqueId().toString();
		
		base.execute("DELETE FROM flex_recording WHERE context = '" + context + "' AND state = '" + RecordingState.STAGED.toString() + "'");
		base.execute("DELETE FROM flex_recording WHERE context = '" + context + "' AND state = '" + RecordingState.RECORDING.toString() + "'");
		
	}

	public void setPending(FleXPlayer player) throws SQLException {
		
		SQLRowWrapper row = null;
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		RecordingContext context = RecordingContext.of(RecordingContext.REPORT, player.getUniqueId().toString());
		
		Set<SQLRowWrapper> rows = base.getRows("flex_recording", SQLCondition.where("context").is(context.toString()));
		
		for (SQLRowWrapper r : rows) {
			
			String state = r.getString("state");
			
			if (state.equals(RecordingState.STAGED.name()) || state.equals(RecordingState.RECORDING.name()))
				row = r;
			
		}
		
		long now = System.currentTimeMillis();
		
		// Prevents duplicates.
		if (row != null) {

			row.set("time", now);
			row.set("state", RecordingState.STAGED.name());
			
			if (row.getString("state").equals(RecordingState.RECORDING.name()))
				row.set("data", Collections.emptyMap().toString());
			
			row.update();
			return;
			
		}
		
		base.addRow("flex_recording", SQLMap.of(
				
				SQLMap.entry("context", context.toString()),
				SQLMap.entry("time", now),
				SQLMap.entry("state", RecordingState.STAGED.name()),
				SQLMap.entry("world", null),
				SQLMap.entry("players", Collections.emptyMap().toString()),
				SQLMap.entry("data", null)
				
			));
		
	}
	
	public boolean isPending(FleXPlayer player) throws SQLException {
		
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		RecordingContext context = RecordingContext.of(RecordingContext.REPORT, player.getUniqueId().toString());
		
		Set<SQLRowWrapper> rows = base.getRows("flex_recording", SQLCondition.where("context").is(context.toString()));
		
		boolean error = false;
	    boolean complete = false;

	    for (SQLRowWrapper r : rows) {
	    	
	        String state = r.getString("state");
	        
	        if (RecordingState.STAGED.name().equals(state))
	            return true;
	        
	        if (RecordingState.ERROR.name().equals(state))
	        	error = true;
	        
	        else if (RecordingState.COMPLETE.name().equals(state))
	        	complete = true;
	        
	    }
	    
	    return error && !complete;
		
	}
	
	public boolean isRecording(FleXPlayer player) throws SQLException {
		
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		RecordingContext context = RecordingContext.of(RecordingContext.REPORT, player.getUniqueId().toString());
		
		Set<SQLRowWrapper> rows = base.getRows("flex_recording", SQLCondition.where("context").is(context.toString()));
		
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