package org.fukkit.event.theme;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;

public class ThemeChangedEvent extends ThemeEvent {
	
	private FleXPlayer player;
	
	private Theme from;
	
	public ThemeChangedEvent(FleXPlayer player, Theme theme, Theme from, boolean async) {
		
		super(theme, async);
		
		this.player = player;
		
		this.from = from;
		
	}
	
	public Theme getFrom() {
		return this.from;
	}
	
	public FleXPlayer getPlayer() {
		return this.player;
	}

}
