package org.fukkit.theme;

import org.fukkit.Memory;
import org.fukkit.clickable.Menu;
import org.fukkit.entity.FleXPlayer;

public class ThemeGui extends Menu {
	
    public ThemeGui(FleXPlayer player) {
    	
    	super((player.getTheme() != null ? player.getTheme() : Memory.THEME_CACHE.stream().findFirst().orElse(null)).format("<title>Select Theme"), 1, player);
    	
    	Memory.THEME_CACHE.forEach(t -> {
    		this.addButton(new ThemeButton(t));
    	});
    	
    }
    
}
