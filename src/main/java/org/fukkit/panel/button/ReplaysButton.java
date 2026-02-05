package org.fukkit.panel.button;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;

import io.flex.commons.file.Language;

public class ReplaysButton extends ExecutableButton {
	
	private static final long serialVersionUID = -679027306270702473L;
	
	protected FleXPlayer other;
	
	public ReplaysButton(Theme theme, Language language, FleXPlayer other) {
		
		super(
				
				Material.PAINTING,
				theme.format("<title>Flow<sp>:\\s<pc>Replays"),
				lore(theme, other));
		
		this.other = other;
		
	}
	
	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action, Inventory inventory) {
		
		if (!action.isClick())
			return false;
		
		
		return true;
		
	}
	
	private static String[] lore(Theme theme, FleXPlayer other) {
		
		List<String> lore = new ArrayList<String>();
		
		lore.add("");
		lore.add(theme.format("<tc>Replay data logged and provided by <spc>FloW<pp>."));
		
		lore.add("");
		lore.add(theme.format("<sp>&oLeft Click<pp>:\\s<sc>Show replays<pp>."));
		
		return lore.toArray(new String[lore.size()]);
		
	}

}
