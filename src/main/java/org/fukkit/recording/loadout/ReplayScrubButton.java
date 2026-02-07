package org.fukkit.recording.loadout;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.fukkit.Fukkit;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.recording.Replay;
import org.fukkit.theme.Theme;

public class ReplayScrubButton extends ExecutableButton {
	
	private Replay replay;
	
	private boolean rewind;
	
	public ReplayScrubButton(Theme theme, Replay replay, boolean rewind) {
		
		super(Material.ARROW, name(theme, replay, rewind));
		
		Objects.requireNonNull(replay, "replay cannot be null");
		
		this.replay = replay;
		this.rewind = rewind;
		
	}
	
	private static String name(Theme theme, Replay replay, boolean rewind) {
		return theme.format("<display>" + (rewind ? "Rewind" : "Fast-Forward") + "<pp>:<reset> <sc>" + replay.getSpeed());
	}
	
	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action, Inventory inventory) {
		
		if (action.isClick()) {
			
			Theme theme = player.getTheme();
			
			if (this.rewind && !this.replay.isReversed())
				this.replay.play(true);
			
			else if (!this.rewind && this.replay.isReversed())
					this.replay.play(false);
				
			else {
				
				double speed = this.replay.getSpeed();
				
				if (action.isLeftClick()) {
					
					double newSpeed = speed + 0.5;
					
					if (newSpeed > 2.0)
						newSpeed = 1.0;
					
					this.replay.setSpeed(newSpeed);
					this.replay.play(this.rewind);
					
				}
				
				if (action.isRightClick()) {
					
					double newSpeed = speed - 0.5;
					
					if (newSpeed < 0.5)
						newSpeed = 1.0;
					
					this.replay.setSpeed(newSpeed);
					this.replay.play(this.rewind);
					
				}
				
			}
			
			this.replay.cancel();
			
			double current = this.replay.getSpeed();
			
			long ticks = Replay.TICK_RATE;
			
			if (current == 2.0)
				ticks = 1L;
			
			if (current == 0.50)
				ticks = 4L;
			
			this.replay.runTaskTimer(Fukkit.getInstance(), 120L, ticks);
			
			this.setName(name(theme, this.replay, this.rewind));
			
			player.sendMessage(theme.format("<flow><pc>Now playing" + (this.rewind ? " in reverse " : " ") + "at <sc>" + this.replay.getSpeed() + "<reset> <pc>speed<pp>."));
			return true;
			
		}
		
		return false;
		
	}

}
