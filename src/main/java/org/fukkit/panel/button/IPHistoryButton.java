package org.fukkit.panel.button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.theme.Theme;

import io.flex.commons.file.Language;

public class IPHistoryButton extends ExecutableButton {
	
	private static final long serialVersionUID = 2984971389617295534L;
	
	public IPHistoryButton(Theme theme, Language language, FleXPlayer other) {
		super(Material.REDSTONE, theme.format("<title>History<sp>:\\s<pc>IPv4"), lore(theme, language, other));
	}
	
	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action, Inventory inventory) {
		return false;
	}
	
	private static String[] lore(Theme theme, Language language, FleXPlayer other) {
		
		List<String> lore = new ArrayList<String>();
		
		lore.add(theme.format("<lore>View <sc>" + other.getDisplayName(theme, true).replace(ChatColor.RESET.toString(), ChatColor.WHITE.toString()) + "<lore>'s IPv4 history<pp>."));
		
		lore.add("");
		lore.add(theme.format("<tc>IPv4 logged and provided by <spc>FloW<pp>."));
		
		lore.add("");
		lore.add(theme.format("<spc>IPv4 history<pp>:"));
		
		List<String> ips = history(theme, other);
		
		if (!ips.isEmpty())
			lore.addAll(ips);
			
		else lore.add(theme.format("<failure>No IP history found<pp>."));
		
		lore.add("");
		lore.add(theme.format("<sp>&oMiddle Click<pp>:\\s<sc>Show all<pp>."));
		lore.add(theme.format("<sp>&oLeft Click<pp>:\\s<sc>Show chat messages<pp>."));
		lore.add(theme.format("<sp>&oRight Click<pp>:\\s<sc>Show connections<pp>."));
		
		return lore.toArray(new String[lore.size()]);
		
	}
	
	private static List<String> history(Theme theme, FleXPlayer other) {
		
		List<String> history = new ArrayList<String>();
		
		Map<Long, String> ips = null;
		
		try {
			ips = other.getHistory().getIps().asMap();
		} catch (FleXPlayerNotLoadedException e) {
			
			e.printStackTrace();
			
			return new ArrayList<String>();
			
		}
		
		List<Long> times = ips.keySet().stream().sorted().collect(Collectors.toList());
		
		SimpleDateFormat format = new SimpleDateFormat("(dd/MM/yy) [hh:mm:ss]");
		
		for (int i = 0; i < ips.size(); i++) {
			
			long time = times.get(i);
			
			String ip = ips.get(time);
			
			String dateTime = format.format(new Date(time))
					
			        .replace("(", "<pp>(<tc>")
					.replace(")", "<pp>)" + Theme.reset)
					
					.replace("[", "<pp>[<spc>")
					.replace("]", "<pp>]" + Theme.reset);
			
			history.add(theme.format(dateTime + " <sc>" + ip));
			
		}
		
		return history;
		
	}

}
