package org.fukkit.reward;

import org.fukkit.theme.Theme;

public class RankDisplay {

	private Theme theme;
	
	private String prefix, suffix, display;
	
	public RankDisplay(Theme theme, String display, String prefix, String suffix) {
		this.theme = theme;
		this.display = display;
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	public Theme getTheme() {
		return this.theme;
	}
	
	public String getDisplay() {
		return this.display;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public String getSuffix() {
		return this.suffix;
	}
	
}
