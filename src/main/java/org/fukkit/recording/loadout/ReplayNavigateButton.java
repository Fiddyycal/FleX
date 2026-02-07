package org.fukkit.recording.loadout;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.fukkit.Fukkit;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.recording.Replay;
import org.fukkit.theme.Theme;

public class ReplayNavigateButton extends ExecutableButton {
	
	private Replay replay;
	
	public ReplayNavigateButton(FleXPlayer player, Replay replay) {
		
		super(Material.COMPASS, name(player, replay));
		
		Objects.requireNonNull(replay, "replay cannot be null");
		
		this.replay = replay;
		
	}
	
	private static String name(FleXPlayer player, Replay replay) {
		
		UUID uid = replay.getTranscript(player);
		
		FleXPlayer watching = Fukkit.getPlayer(uid);
		
		Theme theme = player.getTheme();
		
		return theme.format("<display>Watching<pp>:<reset> " + (watching != null ? "<sc>" + watching.getDisplayName(theme, true) : "<failure>No one"));
		
	}
	
	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action, Inventory inventory) {
		
		if (action.isClick()) {
			
			// TODO open nav menu
			//player.openMenu(new Menu, isUnbreakable());
			return true;
			
		}
		
		return false;
		
	}

}
