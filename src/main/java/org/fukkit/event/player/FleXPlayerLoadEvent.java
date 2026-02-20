package org.fukkit.event.player;

import org.fukkit.entity.FleXPlayer;

/**
 * 
 * This event is for everything EXCEPT history stores.
 * Use {@link FleXPlayer#getHistoryAsync()} to load an offline players history.
 * Alternatively wait for {@link FleXPlayer#getHistory()} to finish loading when getPlayer is called.
 * {@link FleXPlayer#getHistory()} will throw an exception if it has not fully loaded.
 *
 */
public class FleXPlayerLoadEvent extends FleXPlayerEvent {
	
	private boolean offline;
	
	public FleXPlayerLoadEvent(FleXPlayer player, boolean offline) {
		
		super(player, false);
		
		this.offline = offline || !player.isOnline();
		
	}
	
	public boolean isOffline() {
		return this.offline;
	}
	
}
