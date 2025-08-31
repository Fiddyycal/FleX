package org.fukkit.event.command;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.fukkit.command.FleXCommandAdapter;

public class FleXCommandPerformEvent extends FleXCommandEvent implements Cancellable {
	
	private boolean cancel = false;
	private boolean performed = false;
	
	public Player player;
	
	private String[] args, flags;
	
	public FleXCommandPerformEvent(FleXCommandAdapter command, Player player, String[] args, String[] flags, boolean async) {
		
		super(command, async);
		
		this.player = player;
		
		this.args = args;
		this.flags = flags;
		
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public String[] getArguments() {
		return this.args;
	}
	
	public String[] getFlags() {
		return this.flags;
	}
	
	public boolean isPerformed() {
		return this.performed;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	public void setPerformed(boolean performed) {
		this.performed = performed;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
