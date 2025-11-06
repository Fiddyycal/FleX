package org.fukkit.combat.gui.button;

import org.bukkit.inventory.Inventory;
import org.fukkit.Fukkit;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

import io.flex.commons.utils.NumUtils;

public class KnockbackVelocityButton extends ExecutableButton {

	private static final long serialVersionUID = 7448844050179429714L;
	
	public KnockbackVelocityButton(Theme theme) {
		
		super(
				
				VersionUtils.material("WOOD_SWORD", "WOODEN_SWORD"),
				theme.format("<title>Knockback Velocity<pp>:" + Theme.reset + " <success>" + getVelocity(false) + "m" + Theme.reset + " <tc>(" + (int) getVelocity(true) + "cm)"),
				(int) getVelocity(true) > 64 ? 1 : (int) getVelocity(true),
				"",
				theme.format("<lore>This option allows you to change"),
				theme.format("<lore>the knockback velocity inflicted"),
				theme.format("<lore>when a player is damaged<pp>."),
				"",
				theme.format("<sp>&oLeft Click<pp>:" + Theme.reset + " <success>+0.10cm"),
				theme.format("<sp>&o[Shift] + Left Click<pp>:" + Theme.reset + " <success>+0.01cm"),
				"",
				theme.format("<sp>&oRight Click<pp>:" + Theme.reset + " <failure>-0.10"),
				theme.format("<sp>&o[Shift] + Right Click<pp>:" + Theme.reset + " <failure>-0.01cm"));
		
	}

	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action, Inventory inventory) {
		
		if (!action.isClick())
			return false;
		
		boolean negative = action.isRightClick();
		
		double num = action.isShiftClick() ? .01 : .1;
		double delay = Fukkit.getCombatFactory().getKnockbackVelocity();
		double update = delay + (negative ? -num : num);
		
		if (update <= 0 && !action.isLeftClick())
			update = 0;
		
		if (update >= 10.0 && !action.isRightClick())
			update = 10.0;
		
		Fukkit.getCombatFactory().setKnockbackVelocity((float)NumUtils.roundToDecimal(update, 2));
		
		this.setName(player.getTheme().format("<title>Knockback Velocity<pp>:" + Theme.reset + " <success>" + getVelocity(false) + "m" + Theme.reset + " <tc>(" + (int) getVelocity(true) + "cm)"));
		this.setAmount((int) getVelocity(true) > 64 ? 1 : (int) getVelocity(true));
		return true;
		
	}
	
	private static double getVelocity(boolean cm) {
		double velocity = NumUtils.roundToDecimal(Fukkit.getCombatFactory().getKnockbackVelocity(), 2);
		return cm ? velocity * 100 : velocity;
	}
	
}
