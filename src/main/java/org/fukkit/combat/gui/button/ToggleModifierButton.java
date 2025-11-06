package org.fukkit.combat.gui.button;

import org.bukkit.inventory.Inventory;
import org.fukkit.Fukkit;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ToggleableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

public class ToggleModifierButton extends ToggleableButton {
	
	private static final long serialVersionUID = 6493962112217010064L;
	
	public ToggleModifierButton(Theme theme) {
		
		super(

				VersionUtils.material("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS_PANE"),
				theme.format("<title>Knockback Modifier<pp>:" + Theme.reset + " " + (isEnabled() ? "<success>Enabled" : "<failure>Disabled")),
				1,
				(short)(isEnabled() ? 5 : 7),
				"",
				theme.format("<lore>This option allows you to toggle"),
				theme.format("<lore>the server-side knockback modifications"),
				"",
				theme.format("<sp>&oClick<pp>:\\s<sc>Toggle<pp>."));
		
	}
	
	@Override
	public boolean onToggle(FleXPlayer player, ButtonAction action, Inventory inventory) {
		
		Fukkit.getCombatFactory().setEnabled(!isEnabled());
		
		this.setName(player.getTheme().format("<title>Knockback Modifier<pp>:" + Theme.reset + " " + (isEnabled() ? "<success>Enabled" : "<failure>Disabled")));
		this.setDurability((short)(isEnabled() ? 5 : 7));
		return true;
		
	}
	
	private static boolean isEnabled() {
		return Fukkit.getCombatFactory().isEnabled();
	}
	
}
