package org.fukkit.recording.loadout;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.flow.OverwatchReplay;
import org.fukkit.recording.Replay;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

public class ReplayPlayPauseButton extends ExecutableButton {

	private Replay replay;
	
	public ReplayPlayPauseButton(Theme theme, Replay replay) {
		
		super(material(replay.isPaused()), name(theme, replay), 1, damage(replay.isPaused()));
		
		Objects.requireNonNull(replay, "replay cannot be null");
		
		this.replay = replay;
		
	}
	
	private static String name(Theme theme, Replay replay) {
		return theme.format((replay instanceof OverwatchReplay ? "Overwatch" : "Replay") + "<pp>:<reset> " + (replay.isPaused() ? "<failure>Paused" : "<success>Playing"));
	}
	
	private static Material material(boolean paused) {
		return VersionUtils.material("INK_SACK", paused ? "GRAY_DYE" : "GREEN_DYE");
	}
	
	private static short damage(boolean paused) {
		return (short) (VersionUtils.material("INK_SACK", paused ? "GRAY_DYE" : "GREEN_DYE").name().equals("INK_SACK") ? (paused ? 8 : 10) : 0);
	}

	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action, Inventory inventory) {
		
		if (action.isClick()) {
			
			boolean paused = this.replay.isPaused();
			boolean toggle = !paused;
			
			if (paused)
				this.replay.play();
				
			else this.replay.pause();
			
			Theme theme = player.getTheme();
			
			player.sendMessage(theme.format("<flow><pc>You have<reset> <sc>" + (toggle ? "paused" : "unpaused") + "<reset> <pc>the " + (this.replay instanceof OverwatchReplay ? "overwatch replay" : "replay") + "<pp>."));
			
			this.setName(name(theme, this.replay));
			this.setType(material(toggle));
			this.setDurability(damage(toggle));
			this.update();
			return true;
			
		}
		
		return false;
		
	}

}
