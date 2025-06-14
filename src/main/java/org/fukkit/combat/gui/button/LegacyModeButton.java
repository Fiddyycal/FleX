package org.fukkit.combat.gui.button;

import org.fukkit.Fukkit;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ToggleableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

public class LegacyModeButton extends ToggleableButton {
	
	private static final long serialVersionUID = -5481384250398274775L;

	public LegacyModeButton(Theme theme) {
		
		super(

				VersionUtils.material("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS_PANE"),
				theme.format("<title>Legacy Mode<pp>:" + Theme.reset + " " + (isLegacy() ? "<success>Enabled" : "<failure>Disabled")),
				1,
				(short)(isLegacy() ? 5 : 7),
				"",
				theme.format("<lore>I'm gonna be straight with you- the"),
				theme.format("<lore>legacy modifier sucks, but I spent too"),
				theme.format("<lore>much time on it, so you can toggle it<pp>."),
				"",
				theme.format("<sp>&oClick<pp>:\\s<sc>Toggle<pp>."));
		
	}
	
	@Override
	public boolean onToggle(FleXPlayer player, ButtonAction action) {
		
		Fukkit.getCombatFactory().setLegacy(!isLegacy());
		
		this.setName(player.getTheme().format("<title>Legacy Mode<pp>:" + Theme.reset + " " + (isLegacy() ? "<success>Enabled" : "<failure>Disabled")));
		this.setDurability((short)(isLegacy() ? 5 : 7));
		return true;
		
	}
	
	private static boolean isLegacy() {
		return Fukkit.getCombatFactory().isLegacy();
	}
	
}
