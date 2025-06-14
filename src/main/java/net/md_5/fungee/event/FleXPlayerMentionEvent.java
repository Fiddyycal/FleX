package net.md_5.fungee.event;

import org.bukkit.Sound;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.utils.VersionUtils;

public class FleXPlayerMentionEvent extends FleXPlayerEvent {
	
	private FleXPlayer mentioned;
	
	private Sound sound = null;
	
	public FleXPlayerMentionEvent(final FleXPlayer player, FleXPlayer mentioned, boolean async) {
		
		super(player, async);
		
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
