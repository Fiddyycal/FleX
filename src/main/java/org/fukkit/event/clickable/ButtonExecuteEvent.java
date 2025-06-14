package org.fukkit.event.clickable;

import org.bukkit.event.Cancellable;
import org.fukkit.clickable.button.ExecutableButton;

import io.flex.commons.Nullable;

public class ButtonExecuteEvent extends ButtonEvent implements Cancellable {

	private boolean cancel = false;
	
	public ButtonExecuteEvent(@Nullable ExecutableButton button, boolean async) {
		
		super(button, async);
		
		if (this.cancel == true)
			return;
		
		this.button = button;
		
	}
	
	public final ExecutableButton getButton() {
		return (ExecutableButton) this.button;
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
