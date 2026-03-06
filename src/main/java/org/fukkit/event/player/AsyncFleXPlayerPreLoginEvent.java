package org.fukkit.event.player;

import java.net.InetAddress;
import java.util.UUID;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.fukkit.entity.FleXPlayer;

/**
 * I was advised that async event priorities
 * can be called from different threads, so EventPriority.NORMAL
 * isn't always after EventPriority.LOW, to ensure that FleXPlayer
 * exists during Bukkits only async task, I've made this event
 * for api users to calling my own event.
 */
public class AsyncFleXPlayerPreLoginEvent extends FleXPlayerEvent {
	
    private AsyncPlayerPreLoginEvent event;
    
	public AsyncFleXPlayerPreLoginEvent(FleXPlayer player, AsyncPlayerPreLoginEvent event) {
		
		super(player, true);
		
        this.event = event;
		
	}
	
    public UUID getUniqueId() {
        return this.getPlayer().getUniqueId();
    }
    
    public String getName() {
        return this.getPlayer().getName();
    }
    
	public AsyncPlayerPreLoginEvent.Result getLoginResult() {
		return this.event.getLoginResult();
	}
	
    public void setLoginResult(AsyncPlayerPreLoginEvent.Result result) {
        this.event.setLoginResult(result);
    }
    
    public String getKickMessage() {
        return this.event.getKickMessage();
    }
    
    public void setKickMessage(String message) {
    	this.event.setKickMessage(message);
    }
    
    public void allow() {
        this.event.allow();
    }
    
    public void disallow(AsyncPlayerPreLoginEvent.Result result, String message) {
        this.event.disallow(result, message);
    }
    
    public InetAddress getAddress() {
        return this.event.getAddress();
    }

}
