package org.fukkit.theme;

import java.util.Arrays;
import org.bukkit.Material;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;

public class LanguageButton extends ExecutableButton {
	
	private static final long serialVersionUID = -1963371882839892716L;
	
	private Language language;
	
	public LanguageButton(Language language, Theme theme) {
		
		super(Material.BOOK, theme.format("<title>" + language.toString()), 1, theme.format("<lore>" + language.name()));
		
		this.language = language;
		
	}
	
	public Language getLanguage() {
		return this.language;
	}

	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action) {
		
		if (action != ButtonAction.GUI_LEFT_CLICK)
			return false;
		
		player.setLanguage(this.language);
		player.closeMenu();
		
		Arrays.stream(ThemeMessage.LANGUAGE_SELECT_SUCCESS.format(player.getTheme(), this.language,
				
				new Variable<String>("%language%", this.language.toString()),
				new Variable<String>("%lang%", this.language.name()))).forEach(m -> {
				
			player.sendMessage(player.getTheme().format(m));
			
		});
		
		return true;
		
	}

}
