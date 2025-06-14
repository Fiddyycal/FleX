package org.fukkit.event.bot;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BotListenEvent extends Event implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	
	private String gesture, response = null;
	
	private CommandSender sender;
	
	private boolean cancel = false;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public BotListenEvent(CommandSender sender, final String gesture, boolean async) {
		super(async);
		
		this.gesture = gesture;
		this.sender = sender;
		
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;   
	}
	
	public CommandSender getSender() {
		return this.sender;
	}
	
	public String getCompleteGesture() {
		return this.gesture;
	}
	
	public String getGesture() {
		return this.gesture.split(" ")[0];
	}

	public String getResponse() {
		return this.response;
	}
	
	public void setResponse(String response) {
		this.response = response;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}
	
	public boolean hasResponse() {
		return this.response != null;
	}

}
