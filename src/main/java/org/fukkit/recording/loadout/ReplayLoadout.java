package org.fukkit.recording.loadout;

import org.fukkit.clickable.Loadout;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.recording.Replay;

public class ReplayLoadout extends Loadout {

	public ReplayLoadout(FleXPlayer player, Replay replay) {

		this.setButton(0, new ReplayNavigateButton(player, replay));
		
		this.setButton(3, new ReplayScrubButton(player.getTheme(), replay, true));
		this.setButton(4, new ReplayPlayPauseButton(player.getTheme(), replay));
		this.setButton(5, new ReplayScrubButton(player.getTheme(), replay, false));
		
		this.setButton(8, new ReplayLeaveButton(player.getTheme(), replay));
		
	}
	
}
