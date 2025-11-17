package org.fukkit;

import org.fukkit.cache.BadgeCache;
import org.fukkit.cache.ButtonCache;
import org.fukkit.cache.CommandCache;
import org.fukkit.cache.GuiCache;
import org.fukkit.cache.SkinCache;
import org.fukkit.cache.PlayerCache;
import org.fukkit.cache.RankCache;
import org.fukkit.cache.RecordingCache;
import org.fukkit.cache.ThemeCache;
import org.fukkit.cache.WorldCache;
import org.fukkit.consequence.Ban.BanCache;

public interface Memory {

	BanCache BAN_CACHE = new BanCache();
	GuiCache GUI_CACHE = new GuiCache();
	RankCache RANK_CACHE = new RankCache();
	SkinCache SKIN_CACHE = new SkinCache();
	BadgeCache BADGE_CACHE = new BadgeCache();
	WorldCache WORLD_CACHE = new WorldCache();
	ThemeCache THEME_CACHE = new ThemeCache();
	ButtonCache BUTTON_CACHE = new ButtonCache();
	PlayerCache PLAYER_CACHE = new PlayerCache();
	CommandCache COMMAND_CACHE = new CommandCache();
	RecordingCache RECORDING_CACHE = new RecordingCache();
	
	public static void load() {
		
		RECORDING_CACHE.load();
		COMMAND_CACHE.load();
		WORLD_CACHE.load();
		THEME_CACHE.load();
		BADGE_CACHE.load();
		RANK_CACHE.load();
		SKIN_CACHE.load();
		BAN_CACHE.load();
		
		/* After everything has loaded. */
		PLAYER_CACHE.load();
		
	}
	
	public enum Setting {
		
		ARROW_FIX(false),
		
		DEBUG_ENABLED(true);
		
        private Object value;
        
        private Setting(Object value) {
        	this.value = value;
		}
        
        public boolean asBoolean() {
            return (Boolean) this.value;
        }
		
	}
	
}
