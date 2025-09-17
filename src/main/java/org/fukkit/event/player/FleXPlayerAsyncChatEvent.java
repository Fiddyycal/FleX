package org.fukkit.event.player;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.fukkit.entity.FleXPlayer;

public class FleXPlayerAsyncChatEvent extends FleXPlayerEvent implements Cancellable {
	
	private String message, server, prefix, suffix;
	
	private Set<FleXPlayer> recipients, mentions;
	
	private boolean cancel = false;
	
	public FleXPlayerAsyncChatEvent(final FleXPlayer player, String message, String originServer, Set<FleXPlayer> recipients, Set<FleXPlayer> mentioned) {
		
		super(player, true);
		
		this.message = message;
		this.server = originServer != null ? originServer : Bukkit.getServer().getName();
		
		this.recipients = recipients;
		this.mentions = mentioned;
		
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public String getSuffix() {
		return this.suffix;
	}
	
	public Set<FleXPlayer> getRecipients() {
		return this.recipients;
	}
	
	public Set<FleXPlayer> getMentions() {
		return this.mentions;
	}
	
	public String getOriginServer() {
		return this.server;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
