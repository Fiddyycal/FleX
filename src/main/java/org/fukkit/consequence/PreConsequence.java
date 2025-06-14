package org.fukkit.consequence;

import java.util.UUID;

import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.cache.Cacheable;

public abstract class PreConsequence implements Cacheable {
	
	protected Reason reason;
	
	protected long duration, time, until;

	protected UUID uuid, by;
	
	protected boolean pardoned, ip, silent;
	
	public PreConsequence(FleXPlayer player, FleXPlayer by, Reason reason, boolean ip, boolean silent) {
		
		this.reason = reason;
		
		this.duration = this.reason.getDuration();

		this.time = System.currentTimeMillis();
		this.until = System.currentTimeMillis() + this.duration;

		this.uuid = player.getUniqueId();
		this.by = by.getUniqueId();
		
		this.ip = ip;
		this.silent = silent;
		this.pardoned = false;
		
	}
	
	protected PreConsequence() {
		//
	}
	
	public FleXPlayer getPlayer() {
		return Fukkit.getPlayer(this.uuid);
	}
	
	public FleXPlayer getBy() {
		return Fukkit.getPlayer(this.by);
	}
	
	public Reason getReason() {
		return this.reason;
	}
	
	public long getTime() {
		return this.time;
	}
	
	public long getUntil() {
		return this.until;
	}
	
	public long getDuration() {
		return this.duration;
	}
	
	public boolean isIp() {
		return this.ip;
	}
	
	public boolean isPardoned() {
		return this.pardoned;
	}
	
	public boolean isSilent() {
		return this.silent;
	}
	
	public abstract ConvictionType getType();
	
}
