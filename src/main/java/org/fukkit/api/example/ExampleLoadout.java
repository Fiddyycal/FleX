package org.fukkit.api.example;

import org.bukkit.Material;
import org.fukkit.clickable.Loadout;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Variable;

public class ExampleLoadout extends Loadout {

	public ExampleLoadout() {
		
		ExecutableButton test = new ExecutableButton(Material.PAPER) {
			
			private static final long serialVersionUID = -6132974559526764938L;

			@Override
			@SuppressWarnings("deprecation")
			public boolean onExecute(FleXPlayer player, ButtonAction action) {
				
				if (!action.isClick())
					return false;
				
				Variable<?>[] variables = {
						
						new Variable<Integer>("%ping%", player.getPing()),
						new Variable<String>("%name%", player.getDisplayName()),
						new Variable<String>("%player%", player.getName()),
						new Variable<String>("%display%", player.getDisplayName(player.getTheme())),
								
				};
				
				player.sendMessage(ThemeMessage.PING_SHOW, variables);
				return true;
				
			}
			
		};
		
		test.setName("&4&lButton&8: &7&nClick&7 to show your ping&8.");
		this.addButton(test);
		
	}

}
