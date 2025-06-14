package org.fukkit.theme;

import java.util.Arrays;

import org.fukkit.clickable.Menu;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.file.Language;

public class LanguageGui extends Menu {
	
    public LanguageGui(FleXPlayer player) {
    	
    	super(player.getTheme().format("<title>Select Language"), 1, player);
    	
    	Arrays.stream(Language.values()).forEach(l -> {
    		this.addButton(new LanguageButton(l, player.getTheme()));
    	});
    	
    }

}
