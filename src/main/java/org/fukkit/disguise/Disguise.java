package org.fukkit.disguise;

public class Disguise {
	
	private String name;
	
	private FleXSkin skin;
	
	public Disguise(String name, FleXSkin skin) {
		this.name = name;
		this.skin = skin;
	}

	public String getName() {
		return this.name;
	}

	public FleXSkin getSkin() {
		return this.skin;
	}

}
