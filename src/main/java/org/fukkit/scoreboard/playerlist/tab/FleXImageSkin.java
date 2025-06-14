package org.fukkit.scoreboard.playerlist.tab;

import java.awt.image.BufferedImage;

import org.fukkit.Fukkit;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.disguise.FleXSkinType;

public interface FleXImageSkin extends FleXSkin {
	
	FleXImageSkin BLANK = (FleXImageSkin) Fukkit.getImplementation().getSystemSkin(FleXSkinType.BLANK);
	
	public BufferedImage getImage();

}

