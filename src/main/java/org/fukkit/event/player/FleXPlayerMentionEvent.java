package org.fukkit.event.player;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.utils.VersionUtils;

public class FleXPlayerMentionEvent extends FleXPlayerEvent {
	
	private FleXPlayer mentioned;
	
	private Sound sound = null;
	
	public FleXPlayerMentionEvent(final FleXPlayer player, FleXPlayer mentioned) {
		
		super(player, !Bukkit.isPrimaryThread());
		
		this.mentioned = mentioned;
		
		// TODO Put this option into the configuration.
		if (this.sound == null)
			this.sound = VersionUtils.sound("NOTE_PLING", "BLOCK_NOTE_BLOCK_PLING");
		
	}
	
	public FleXPlayer getMentioned() {
		return this.mentioned;
	}
	
	public Sound getSound() {
		return this.sound;
	}
	
	public void setSound(Sound sound) {
		this.sound = sound;
	}

}
