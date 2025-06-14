package org.fukkit.world;

import org.bukkit.WorldCreator;

public class FleXWorldCreator extends WorldCreator {

	private static final String defaults = "flex\\:air,2*flex\\:air,flex\\:air;flex\\:plains;village;";
	
	private String settings = defaults;
	
	public FleXWorldCreator(String name) {
		super(name);
	}

	public void settings(String settings) {
		this.settings = settings;
	}
	
	public String settings() {
		return this.settings.toLowerCase();
	}

}
