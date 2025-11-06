package org.fukkit.combat.gui.button;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.fukkit.Fukkit;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;

import io.flex.commons.utils.NumUtils;

public class HitRegistrationButton extends ExecutableButton {

	private static final long serialVersionUID = 7448844050179429714L;
	
	public HitRegistrationButton(Theme theme) {
		
		super(
				
				Material.FISHING_ROD,
				theme.format("<title>Hit Registration<pp>:" + Theme.reset + " <success>" + getDelay(false) + Theme.reset + " <tc>(" + (int) getDelay(true) + "ms)"),
				(int) getDelay(true) > 64 ? 1 : (int) getDelay(true),
				"",
				theme.format("<lore>This option allows you to change"),
				theme.format("<lore>the server-side hit detection delay<pp>."),
				"",
				theme.format("<tc>NOTE<pp>:" + Theme.reset + " <lore>Registration may differentiate in"),
				theme.format("<lore>practice depending on the tps of the host<pp>."),
				"",
				theme.format("<sp>&oLeft Click<pp>:" + Theme.reset + " <success>+0.10ms"),
				theme.format("<sp>&o[Shift] + Left Click<pp>:" + Theme.reset + " <success>+0.01ms"),
				"",
				theme.format("<sp>&oRight Click<pp>:" + Theme.reset + " <failure>-0.10ms"),
				theme.format("<sp>&o[Shift] + Right Click<pp>:" + Theme.reset + " <failure>-0.01ms"));
		
	}

	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action, Inventory inventory) {
		
		if (!action.isClick())
			return false;
		
		boolean negative = action.isRightClick();
		
		double num = action.isShiftClick() ? .01 : .1;
		double delay = Fukkit.getCombatFactory().getDamageDelay();
		double update = delay + (negative ? -num : num);
		
		if (update <= 0 && !action.isLeftClick())
			update = 0;
		
		if (update >= 1.0 && !action.isRightClick())
			update = 1.0;
		
		Fukkit.getCombatFactory().setDamageDelay(NumUtils.roundToDecimal(update, 2));
		
		this.setName(player.getTheme().format("<title>Hit Registration<pp>:" + Theme.reset + " <success>" + getDelay(false) + Theme.reset + " <tc>(" + (int) getDelay(true) + "ms)"));
		this.setAmount((int) getDelay(true) > 64 ? 1 : (int) getDelay(true));
		return true;
		
	}
	
	private static double getDelay(boolean ms) {
		double delay = NumUtils.roundToDecimal(Fukkit.getCombatFactory().getDamageDelay(), 2);
		return ms ? delay * 100 : delay;
	}

}
