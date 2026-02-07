package org.fukkit.recording.loadout;

import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.consequence.Reason;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.flow.OverwatchReplay;
import org.fukkit.recording.Replay;
import org.fukkit.theme.Theme;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.VersionUtils;

public class ReplayLeaveButton extends ExecutableButton {
	
	private Replay replay;
	
	private Reason verdict = null;
	
	public ReplayLeaveButton(Theme theme, Replay replay) {
		
		super(material(), name(theme, replay), 1, damage());
		
		Objects.requireNonNull(replay, "replay cannot be null");
		
		this.replay = replay;
		
	}
	
	private static String name(Theme theme, Replay replay) {
		return theme.format("<display>Leave " + (replay instanceof OverwatchReplay ? "Overwatch Replay" : "Replay"));
	}
	
	private static Material material() {
		return VersionUtils.material("INK_SACK", "RED_DYE");
	}
	
	private static short damage() {
		return (short) (material().name().equals("INK_SACK") ? 1 : 0);
	}
	
	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action, Inventory inventory) {
		
		if (action.isClick()) {
			
			Theme theme = player.getTheme();
			
			if (action.isLeftClick()) {
				
				player.sendMessage(theme.format("<error><failure>Fail safe<sp>;<reset> <failure>Accepting right click only<pp>."));
				return false;
				
			}
			
			player.sendMessage(theme.format("<flow><pc>Leaving replay<pp>..."));
			
			this.replay.onLeave(player);
			this.replay.destroy();
			
			if (player.isOnline()) {
				BukkitUtils.runLater(() -> {
					
					if (player.isOnline())
						player.kick(ChatColor.RED + "null send location (ReplayLeaveEvent), please reconnect.");
					
				}, 20L);
			}
			
			return true;
			
		}
		
		return false;
		
	}

}
