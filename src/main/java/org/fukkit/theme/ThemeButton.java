package org.fukkit.theme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;
import org.fukkit.clickable.Menu;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.utils.ThemeUtils;

import io.flex.commons.cache.cell.BiCell;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.NumUtils;
import io.flex.commons.utils.StringUtils;

@SuppressWarnings("deprecation")
public class ThemeButton extends ExecutableButton {
	
	private static final long serialVersionUID = -8072076657498053925L;

	private Theme theme;
	
	public ThemeButton(Theme theme) {
		
		super(theme.getIcon().getType(), theme.format("<title>" + theme.getName()), 1, theme.getIcon().getDurability());
		
		this.theme = theme;
		
		List<String> lore = new ArrayList<String>();
		
		lore.add(theme.format("<subtitle>" + theme.getCategory()));
		lore.add("");
		
		Map<String, List<String>> animated = new HashMap<String, List<String>>();
		
		for (Entry<BiCell<String, String>, String> entry : theme.getTags().entrySet()) {
			
			String name = entry.getKey().a();
			String tag = entry.getKey().b();
			String sequence = entry.getValue();
			
			String lc = name.toLowerCase();
			
			List<String> tags = new LinkedList<String>();
			
			boolean result = lc.contains("success") || lc.contains("failure") || lc.contains("severe") || lc.contains("reset");
			boolean title = lc.contains("title") || lc.contains("subtitle") || lc.contains("display") || lc.contains("lore") || lc.contains("description") || lc.contains("divider");
			boolean interactable = lc.contains("interactable") || lc.contains("clickable") || lc.contains("hoverable");
			
			String key = name.split(" ").length > 1 ? name.split(" ")[1] : name.split("-").length > 1 ? name.split("-")[1] : name.split(" ")[0];
			String value = theme.format(tag + name);
			
			key = result ? "result" : title ? "title" : interactable ? "interactable" : key;
			
			if (animated.containsKey(key))
				tags = animated.get(key);
			
			if (sequence == null || sequence.equals(""))
				value = ChatColor.RESET + value + ChatColor.GRAY + " (None)";
			
			tags.add(value);
			
			animated.put(key, tags);
			
		}
		
		Map<String, List<String>> sorted = animated.entrySet().stream().sorted(ThemeUtils.compare()).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (mer, ge) -> mer, LinkedHashMap::new));
		
		this.setLore(lore.toArray(new String[lore.size()]));
		
		/**
		 * Do not use a FukkitRunnable here, this task must stay on a seperate thread.
		 */
		new BukkitRunnable() {
			
			private Map<Integer, String> fin = new HashMap<Integer, String>();
			private int i = -1, o = 2;
			
			@Override
			public void run() {
				
				String[] lines = new String[sorted.size()];
				
				Menu menu = (Menu) ThemeButton.this.getClickable();
				
				if (menu == null || !menu.isOpen()) {
					this.cancel();
					return;
				}
				
				if (this.o == 2) {
					
					for (int i = 0; i < lines.length; i++)
						lines[i] = this.fin.containsKey(i) ? this.fin.get(i) : ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + ChatColor.MAGIC + ChatColor.stripColor(StringUtils.generate(NumUtils.getRng().getInt(3, 7), false));
						
					this.fin.clear();
					
					this.o = -1;
					
					this.i++;
					
				} else {
					
					this.o++;
					
					for (int i = 0; i < sorted.size(); i++) {
						
						List<String> values = sorted.values().stream().skip(i).findFirst().orElse(null);
						
						String line = values.stream().skip(this.i).findFirst().orElse(null);
						
						if (line == null) {
							
							line = values.stream().findFirst().orElse(null);
							
							if (!this.fin.containsKey(i))
								this.fin.put(i, line);
							
						}
						
						lines[i] = line;
						
					}
					
				}
				
				String[] lore = new String[lines.length + 5];
				
				lore[0] = theme.format("<subtitle>" + theme.getCategory());
				lore[1] = "";
				
				for (int i = 0; i < lines.length; i++)
					lore[i + 2] = lines[i];
				
				lore[lore.length - 3] = "";
				lore[lore.length - 2] = theme.format("<lore>Actual appearance may differ due");
				lore[lore.length - 1] = theme.format("<lore>to manual<sp>/<lore>conditional input<pp>.");
				
				ThemeButton.this.setLore(lore);
				
				if (this.fin.size() == sorted.size())
					this.i = this.o == 2 ? -1 : 0;
				
			}
			
		}.runTaskTimerAsynchronously(Fukkit.getInstance(), 0L, 5L);
		
	}
	
	public Theme getTheme() {
		return this.theme;
	}

	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action) {
		
		if (action != ButtonAction.GUI_LEFT_CLICK)
			return false;
		
		player.setTheme(this.theme);
		
		player.closeMenu();
		
		Arrays.stream(ThemeMessage.THEME_SELECT_SUCCESS.format(this.theme, player.getLanguage(), new Variable<String>("%theme%", this.theme.getName()))).forEach(m -> {
			player.sendMessage(this.theme.format(m));
		});
		
		return true;
		
	}

}
