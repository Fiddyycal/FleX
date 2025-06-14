package org.fukkit.combat.gui.button;

import org.bukkit.Material;
import org.fukkit.Fukkit;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;

import io.flex.commons.utils.NumUtils;

public class KnockbackHeightButton extends ExecutableButton {

	private static final long serialVersionUID = 7448844050179429714L;
	
	public KnockbackHeightButton(Theme theme) {
		
		super(
				
				Material.ENDER_PEARL,
				theme.format("<title>Knockback Height<pp>:" + Theme.reset + " <success>" + getHeight(false) + "m" + Theme.reset + " <tc>(" + (int) getHeight(true) + "cm)"),
				(int) getHeight(true) > 64 ? 1 : (int) getHeight(true),
				"",
				theme.format("<lore>This option allows you to change"),
				theme.format("<lore>the knockback height inflicted"),
				theme.format("<lore>when a player is damaged<pp>."),
				"",
				theme.format("<sp>&oLeft Click<pp>:" + Theme.reset + " <success>+0.10cm"),
				theme.format("<sp>&o[Shift] + Left Click<pp>:" + Theme.reset + " <success>+0.01cm"),
				"",
				theme.format("<sp>&oRight Click<pp>:" + Theme.reset + " <failure>-0.10"),
				theme.format("<sp>&o[Shift] + Right Click<pp>:" + Theme.reset + " <failure>-0.01cm"));
		
	}

	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action) {
		
		if (!action.isClick())
			return false;
		
		boolean negative = action.isRightClick();
		
		double num = action.isShiftClick() ? .01 : .1;
		double delay = Fukkit.getCombatFactory().getKnockbackHeight();
		double update = delay + (negative ? -num : num);
		
		if (update <= 0 && !action.isLeftClick())
			update = 0;
		
		if (update >= 10.0 && !action.isRightClick())
			update = 10.0;
		
		Fukkit.getCombatFactory().setKnockbackHeight((float)NumUtils.roundToDecimal(update, 2));
		
		this.setName(player.getTheme().format("<title>Knockback Height<pp>:" + Theme.reset + " <success>" + getHeight(false) + "m" + Theme.reset + " <tc>(" + (int) getHeight(true) + "cm)"));
		this.setAmount((int) getHeight(true) > 64 ? 1 : (int) getHeight(true));
		return true;
		
	}
	
	private static double getHeight(boolean cm) {
		double height = NumUtils.roundToDecimal(Fukkit.getCombatFactory().getKnockbackHeight(), 2);
		return cm ? height * 100 : height;
	}
	
}
