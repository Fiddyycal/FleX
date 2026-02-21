package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.disguise.Disguise;
import org.fukkit.entity.FleXPlayer;
import io.flex.commons.Nullable;

public class FleXPlayerPreDisguiseEvent extends FleXPlayerDisguiseEvent implements Cancellable {
	
	private boolean cancel = false;
	
	public FleXPlayerPreDisguiseEvent(FleXPlayer player, @Nullable Disguise disguise, Result result) {
		super(player, disguise, result);
	}
	
	public boolean isFlipped() {
		Disguise disguise = this.getDisguise();
		return disguise != null && (disguise.getName().equals("Grumm") || disguise.getName().equals("Dinnerbone"));
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
}
