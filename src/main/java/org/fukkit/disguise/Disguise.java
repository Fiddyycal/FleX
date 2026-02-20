package org.fukkit.disguise;

public class Disguise {
	
	private String name;
	
	private FleXSkin skin;
	
	private boolean randomName, randomSkin;
	
	public Disguise(String name, FleXSkin skin, boolean randomName, boolean randomSkin) {
		this.name = name;
		this.skin = skin;
		this.randomName = randomName;
		this.randomSkin = randomSkin;
	}

	public String getName() {
		return this.name;
	}

	public FleXSkin getSkin() {
		return this.skin;
	}
	
	public boolean isRandomName() {
		return this.randomName;
	}
	
	public boolean isRandomSkin() {
		return this.randomSkin;
	}

}
