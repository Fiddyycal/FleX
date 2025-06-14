package org.fukkit.combat.gui.button;

import org.fukkit.Fukkit;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ToggleableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

public class RealisticButton extends ToggleableButton {

	private static final long serialVersionUID = 7448844050179429714L;
	
	public RealisticButton(Theme theme) {
		
		super(

				VersionUtils.material("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS_PANE"),
				theme.format("<title>Realistic Knockback<pp>:" + Theme.reset + " " + (isRealistic() ? "<success>Enabled" : "<failure>Disabled")),
				1,
				(short)(isRealistic() ? 5 : 7),
				"",
				theme.format("<lore>This option helps knockback"),
				theme.format("<lore>to differentiate, making velocity"),
				theme.format("<lore>calculations more realistic<pp>."),
				"",
				theme.format("<sp>&oClick<pp>:\\s<sc>Toggle<pp>."));
		
	}

	@Override
	public boolean onToggle(FleXPlayer player, ButtonAction action) {
		
		Fukkit.getCombatFactory().setKnockbackRealistic(!isRealistic());
		
		this.setName(player.getTheme().format("<title>Realistic Knockback<pp>:" + Theme.reset + " " + (isRealistic() ? "<success>Enabled" : "<failure>Disabled")));
		this.setDurability((short)(isRealistic() ? 5 : 7));
		return true;
		
	}
	
	private static boolean isRealistic() {
		return Fukkit.getCombatFactory().isKnockbackRealistic();
	}

}
