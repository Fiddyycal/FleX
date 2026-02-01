package org.fukkit.panel;

import org.fukkit.clickable.Menu;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.panel.button.BanButton;
import org.fukkit.panel.button.IPHistoryButton;
import org.fukkit.panel.button.KickButton;
import org.fukkit.panel.button.MetricsButton;
import org.fukkit.panel.button.MuteButton;
import org.fukkit.panel.button.ReportHistoryButton;
import org.fukkit.panel.button.chat.ChatHistoryButton;
import org.fukkit.theme.Theme;

import io.flex.commons.file.Language;

public class FleXPanel extends Menu {

	public FleXPanel(FleXPlayer player, FleXPlayer other, boolean hideSensitive) {
	
		super(player.getTheme().format("<engine><pc>" + other.getName()), 3);
		
		Theme theme = player.getTheme();
		Language lang = player.getLanguage();
		
		other.getHistoryAsync(history -> {
			
			this.addButton(new ChatHistoryButton(theme, lang, other));
			this.addButton(new IPHistoryButton(theme, lang, other));
			this.addButton(new ReportHistoryButton(theme, lang, other));
			this.addButton(new BanButton(theme, lang, other));
			this.addButton(new MuteButton(theme, lang, other));
			this.addButton(new KickButton(theme, lang, other));
			this.addButton(new MetricsButton(theme, lang, other));
			
		}, () -> {
			
			other.sendMessage("Failed to load FleX Panel for player " + other.getName() + ".");
			
		});
		
		/* References */
		/* Ranks */
		/* Accounts */
		/* Names */
		/* Disguises */
		/* IPs - Admin only */
		/* Flow Reference names - Admin only */
		
	}

}
