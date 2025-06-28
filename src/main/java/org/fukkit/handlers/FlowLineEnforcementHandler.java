package org.fukkit.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.fukkit.Fukkit;
import org.fukkit.ai.AIDriver;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.config.Configuration;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.fle.CriticalHitListeners;
import org.fukkit.fle.flow.CommandLogListeners;

import io.flex.FleX;

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
		
		//DataHelper.set("flow.suspects.recording", new HashSet<UUID>());
		
		registered = true;
		
	}
	
	public static boolean isRegistered() {
		return registered;
	}
	
	public AIDriver getAIDriver() {
		return this.driver;
	}
	
	public Set<UUID> getPendingUnsafe() {
		
		List<String> pending = new ArrayList<String>();//DataHelper.getList("flow.suspects.pending");
		
		if (pending == null || pending.isEmpty())
			return new HashSet<UUID>();
		
		return pending.stream().filter(s -> {
			
			try {
				
				UUID.fromString(s);
				
				return true;
				
			} catch (IllegalArgumentException e) {
				return false;
			}
			
		}).map(s -> UUID.fromString(s)).collect(Collectors.toSet());
		
	}
	
	public Set<UUID> getRecordingUnsafe() {
		
		List<String> recording = new ArrayList<String>();//DataHelper.getList("flow.suspects.recording");
		
		if (recording == null || recording.isEmpty())
			return new HashSet<UUID>();
		
		return recording.stream().filter(s -> {
			
			try {
				
				UUID.fromString(s);
				
				return true;
				
			} catch (IllegalArgumentException e) {
				return false;
			}
			
		}).map(s -> UUID.fromString(s)).collect(Collectors.toSet());
		
	}
	
	public static String flowPath() {
		
		String path = Fukkit.getResourceHandler().getYaml(Configuration.DATA).getConfig().getString("FloW-Path", FleX.EXE_PATH + "/flex/data/flow/");
		
		path = path.replace("${server_absolute_path}", FleX.EXE_PATH);
		path = path.replace("${volumes_absolute_path}", new File(FleX.EXE_PATH).getParentFile().getAbsolutePath());
		
		return path;
		
	}
	
	public void setPending(FleXPlayer player, boolean pending) {
		
		if (player == null)
			return;
		
		Set<UUID> list = this.getPendingUnsafe();
		
		if (pending)
			list.add(player.getUniqueId());
			
		else list.remove(player.getUniqueId());
		
		//DataHelper.set("flow.suspects.pending", list);
		
	}
	
	public void setRecording(FleXPlayer player, boolean recording) {
		
		if (player == null)
			return;
		
		this.setPending(player, false);
		
		Set<UUID> list = this.getRecordingUnsafe();
		
		if (recording)
			list.add(player.getUniqueId());
			
		else list.remove(player.getUniqueId());
		
		//DataHelper.set("flow.suspects.recording", list);
		
	}
	
	public boolean isPending(FleXPlayer player) {
		return this.getPendingUnsafe().contains(player.getUniqueId());
	}
	
	public boolean isRecording(FleXPlayer player) {
		return this.getRecordingUnsafe().contains(player.getUniqueId());
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
