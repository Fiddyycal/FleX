package org.fukkit.combat.gui.button;

import org.bukkit.inventory.Inventory;
import org.fukkit.Fukkit;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ToggleableButton;
import org.fukkit.combat.CombatFactory;
import org.fukkit.combat.gui.CombatGui;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

public class ResetButton extends ToggleableButton {

	private static final long serialVersionUID = 7448844050179429714L;
	
	public ResetButton(Theme theme) {
		
		super(

				VersionUtils.material("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS_PANE"),
				theme.format("<title>Reset<pp>:" + Theme.reset + " <sc>Select"),
				1,
				(short) 7,
				"",
				theme.format("<lore>This option resets all combat"),
				theme.format("<lore>settings to the selected preset<pp>."),
				"",
				theme.format("<sp>&oLeft Click<pp>:\\s<sc>Select<pp>."),
				theme.format("<sp>&oRight Click<pp>:\\s<sc>Change<pp>."));
		
	}

	@Override
	public boolean onToggle(FleXPlayer player, ButtonAction action, Inventory inventory) {
		
		if (action.isLeftClick() && this.getName().contains("Select"))
			return false;
		
		boolean vanilla = this.isToggled();
		
		if (action.isRightClick()) {
			
			this.setName(player.getTheme().format("<title>Reset<pp>:" + Theme.reset + (vanilla ? " &dVanilla" : " &3FleX")));
			this.setDurability((short)(vanilla ? 10 : 11));
			
		}
		
		if (action.isLeftClick()) {
			
			CombatFactory factory = Fukkit.getCombatFactory();
			
			factory.setKnockbackRealistic(vanilla);
			factory.setAntiLockupKnockback(!vanilla);
			
			factory.setKnockbackVelocity(vanilla ? CombatFactory.VANILLA_KNOCKBACK_VELOCITY_RAW : CombatFactory.KNOCKBACK_VELOCITY_RAW);
			factory.setKnockbackHeight(vanilla ? CombatFactory.VANILLA_KNOCKBACK_HEIGHT_RAW : CombatFactory.KNOCKBACK_HEIGHT_RAW);
			factory.setDamageDelay(vanilla ? CombatFactory.VANILLA_DAMAGE_DELAY : CombatFactory.DAMAGE_DELAY);
			
			player.closeMenu();
			player.openMenu(new CombatGui(player.getTheme()), true);
			
		}
		
		return true;
		
	}

}
