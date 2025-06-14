package org.fukkit.panel.button.chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.fukkit.clickable.Menu;
import org.fukkit.clickable.button.PointlessButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;

public class ChatHistoryShowcase extends Menu {
	
	public ChatHistoryShowcase(FleXPlayer viewer, FleXPlayer other) {
		
		super(viewer.getTheme().format("<title>Chat History"), 6);
		
		Theme theme = viewer.getTheme();
		
		Map<Long, String> history = other.getHistory().getMessages().asMap();
		
		String name = other.getDisplayName(theme, true);
		
		@SuppressWarnings("deprecation")
		Variable<?>[] variables = {
				
				new Variable<String>("%name%", other.getDisplayName()),
				new Variable<String>("%player%", name),
				new Variable<String>("%display%", name),
				new Variable<String>("%rank%", other.getRank().getDisplay(viewer.getTheme(), true)),
				new Variable<String>("%role%", other.getRank().getDisplay(viewer.getTheme(), false))
				
		};
		
		SimpleDateFormat format = new SimpleDateFormat("[hh:mm:ss z]");
		
		categorizeIntoDates(other).forEach((d, l) -> {
			
			List<String> lore = new ArrayList<String>();
			
			Collections.sort(l);
			
			l.forEach(t -> {
				
				String message = history.get(t);
				
				String dateTime = format.format(new Date(t))
				
						.replace("[", "<pp>[<spc>")
						.replace("]", "<pp>]" + Theme.reset);
				
				String[] arr = ThemeMessage.CHAT_FORMAT.format(viewer.getTheme(), viewer.getLanguage(), ArrayUtils.add(variables, new Variable<String>("%message%", message)));
				
				IntStream.range(0, arr.length).forEach(i -> {
					lore.add(theme.format(arr[i] = dateTime + " " + arr[i]));
				});
				
			});
			
			// TODO add a next page button if pages are too long
			
			this.addButton(new PointlessButton(Material.BOOK, theme.format("<display>" + d), lore.toArray(new String[lore.size()])));
			
		});
		
	}
	
	private static Map<String, List<Long>> categorizeIntoDates(FleXPlayer other) {
		
		Map<String, List<Long>> categorized = new HashMap<String, List<Long>>();
		
		Set<Long> all = other.getHistory().getMessages().asMap().keySet().stream().sorted().collect(Collectors.toSet());
		
		SimpleDateFormat date = new SimpleDateFormat("EEE, MMM d, yyyy");
		
		for (long time : all) {
			
			String key = date.format(time);
			
			List<Long> times = categorized.getOrDefault(date.format(time), new LinkedList<Long>());
			
			times.add(time);
			
			categorized.put(key, times);
			
		}
		
		return categorized;
		
	}

}
