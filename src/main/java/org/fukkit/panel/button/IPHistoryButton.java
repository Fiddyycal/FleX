package org.fukkit.panel.button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerHistoryNotLoadedException;
import org.fukkit.history.variance.IPHistory;
import org.fukkit.theme.Theme;

import io.flex.commons.file.Language;

public class IPHistoryButton extends ExecutableButton {
	
	public IPHistoryButton(Theme theme, Language language, FleXPlayer other) {
		super(Material.REDSTONE, theme.format("<title>History<sp>:\\s<pc>IPv4"), lore(theme, language, other));
	}
	
	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action, Inventory inventory) {
		
		// TODO
		return false;
		
	}
	
	private static String[] lore(Theme theme, Language language, FleXPlayer other) {
		
		List<String> lore = new ArrayList<String>();
		
		lore.add(theme.format("<lore>View <sc>" + other.getDisplayName(theme, true).replace(ChatColor.RESET.toString(), ChatColor.WHITE.toString()) + "<lore>'s IPv4 history<pp>."));
		
		lore.add("");
		lore.add(theme.format("<tc>IPv4 logged and provided by <spc>FloW<pp>."));
		
		lore.add("");
		lore.add(theme.format("<spc>Recent IPv4 history<pp>:"));
		
		List<String> ips = last5(theme, language, other);
		
		if (!ips.isEmpty())
			lore.addAll(ips);
			
		else lore.add(theme.format("<failure>No IP history found<pp>."));
		
		lore.add("");
		lore.add(theme.format("<sp>&oLeft Click<pp>:\\s<sc>Show all<pp>."));
		
		return lore.toArray(new String[lore.size()]);
		
	}
	
	private static List<String> last5(Theme theme, Language language, FleXPlayer other) {
		
		Map<Long, String> ips = null;
		
		try {
			
			IPHistory history = other.getHistory(IPHistory.class);
			
			if (history != null)
				ips = history.asMap();
			
			else return new ArrayList<String>();
			
		} catch (FleXPlayerHistoryNotLoadedException e) {
			
			e.printStackTrace();
			
			return new ArrayList<String>();
			
		}
		
		List<Long> times = ips.keySet().stream().sorted().collect(Collectors.toList());
		
		int size = times.size() - 5;
		
		if (size < 0)
			size = 0;
		
		List<Long> latest = times.subList(size, times.size());
		
		List<String> last5 = new LinkedList<String>();
		
		if (latest.isEmpty())
			return last5;
		
		SimpleDateFormat format = new SimpleDateFormat("(dd/MM/yy) [hh:mm:ss]");
		
		for (int i = 0; i < latest.size(); i++) {
			
			long time = latest.get(i);
			
			String dateTime = format.format(new Date(time))
					
			        .replace("(", "<pp>(<tc>")
					.replace(")", "<pp>)" + Theme.reset)
					
					.replace("[", "<pp>[<spc>")
					.replace("]", "<pp>]" + Theme.reset);
			
			last5.add(theme.format(dateTime + " <sc>" + ips.get(time)));
			
		}
		
		return last5;
		
	}

}
