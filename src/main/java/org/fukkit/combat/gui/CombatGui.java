package org.fukkit.combat.gui;

import org.bukkit.event.inventory.InventoryType;
import org.fukkit.clickable.Menu;
import org.fukkit.clickable.button.FacelessButton;
import org.fukkit.combat.gui.button.AntiLockupButton;
import org.fukkit.combat.gui.button.HitRegistrationButton;
import org.fukkit.combat.gui.button.KnockbackHeightButton;
import org.fukkit.combat.gui.button.KnockbackVelocityButton;
import org.fukkit.combat.gui.button.LegacyModeButton;
import org.fukkit.combat.gui.button.RealisticButton;
import org.fukkit.combat.gui.button.ResetButton;
import org.fukkit.combat.gui.button.ToggleModifierButton;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

public class CombatGui extends Menu {

	public CombatGui(Theme theme) {
		
		super(theme.format("<title>Knockback"), InventoryType.DISPENSER);
		
		this.setButton(0, new ToggleModifierButton(theme));
		this.setButton(1, new HitRegistrationButton(theme));
		this.setButton(2, new ResetButton(theme));
		this.setButton(3, new RealisticButton(theme));
		this.setButton(4, new KnockbackVelocityButton(theme));
		this.setButton(5, new LegacyModeButton(theme));
		this.setButton(6, new AntiLockupButton(theme));
		this.setButton(7, new KnockbackHeightButton(theme));
		this.setButton(8, new FacelessButton(VersionUtils.material("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS_PANE"), (short) 8));
		
	}

}
