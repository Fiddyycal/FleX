package org.fukkit.combat.gui.button;

import org.fukkit.Fukkit;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ToggleableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

public class AntiLockupButton extends ToggleableButton {

	private static final long serialVersionUID = 7448844050179429714L;
	
	public AntiLockupButton(Theme theme) {
		
		super(
				
				VersionUtils.material("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS_PANE"),
				theme.format("<title>Anti-Lockup Knockback<pp>:" + Theme.reset + " " + (hasAntiLockup() ? "<success>Enabled" : "<failure>Disabled")),
				1,
				(short)(hasAntiLockup() ? 5 : 7),
				"",
				theme.format("<lore>This option stops knockback"),
				theme.format("<lore>lock-ups between the damager and"),
				theme.format("<lore>the freshly damaged player<pp>."),
				"",
				theme.format("<tc>NOTE<pp>:" + Theme.reset + " <lore>This uses damage ticks to"),
				theme.format("<lore>predict lock-ups<pp>;" + Theme.reset + " <lore>If each players damage"),
				theme.format("<lore>ticks are within 5 of each other, knockback"),
				theme.format("<lore>velocity is multiplied<pp>."),
				"",
				theme.format("<sp>&oClick<pp>:\\s<sc>Toggle<pp>."));
		
	}

	@Override
	public boolean onToggle(FleXPlayer player, ButtonAction action) {
		
		Fukkit.getCombatFactory().setAntiLockupKnockback(!hasAntiLockup());
		
		this.setName(player.getTheme().format("<title>Anti-Lockup<pp>:" + Theme.reset + " " + (hasAntiLockup() ? "<success>Enabled" : "<failure>Disabled")));
		this.setDurability((short)(hasAntiLockup() ? 5 : 7));
		
		return true;
		
	}
	
	private static boolean hasAntiLockup() {
		return Fukkit.getCombatFactory().isAntiLockupKnockbackEnabled();
	}

}
