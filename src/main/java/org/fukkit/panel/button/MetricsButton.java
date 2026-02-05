package org.fukkit.panel.button;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.Inventory;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.fle.AutoClickerDetector;
import org.fukkit.fle.FlowProfile;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

import io.flex.commons.file.Language;

public class MetricsButton extends ExecutableButton {
	
	private static final long serialVersionUID = -679027306270702473L;
	
	protected FleXPlayer other;
	
	public MetricsButton(Theme theme, Language language, FleXPlayer other) {
		
		super(
				
				VersionUtils.material("COMMAND_BLOCK", "COMMAND"),
				theme.format("<title>Flow<sp>:\\s<pc>Metrics"),
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
		
		FlowProfile profile = FlowProfile.getProfile(other.getUniqueId());
		
		AutoClickerDetector acd = profile.getAutoClickDetector();
		
		lore.add("");
		lore.add(theme.format("<tc>Anti-cheat data logged and provided by <spc>FloW<pp>."));
		
		lore.add("");
		lore.add(theme.format("<spc>Auto-clicker detector<pp>:"));
		lore.add(theme.format("<pc>Highest CPS<pp>:\\s<sc>" + (profile == null ? 0 : acd.getHighestCps())));
		lore.add(theme.format("<pc>Current CPS<pp>:\\s<sc>" + (profile == null ? 0 : acd.getCps())));
		lore.add(theme.format("<pc>Average CPS<pp>:\\s<sc>" + (profile == null ? 0 : acd.getAverage() < 0 ? "Not recorded yet" : acd.getAverage())));
		
		//lore.add(theme.format("<sp>&oLeft Click<pp>:\\s<sc>Show more reports<pp>."));
		//lore.add(theme.format("<sp>&oRight Click<pp>:\\s<sc>Show reported<pp>."));
		
		return lore.toArray(new String[lore.size()]);
		
	}

}
