package org.fukkit.theme;

import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.fukkit.Fukkit;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.config.YamlConfig;
import org.fukkit.item.UniqueItem;
import org.fukkit.utils.ConfigUtils;
import org.fukkit.utils.FormatUtils;
import org.fukkit.utils.VersionUtils;

import io.flex.FleX.Task;
import io.flex.commons.cache.Cacheable;
import io.flex.commons.cache.cell.BiCell;
import io.flex.commons.emoji.Emoji;
import io.flex.commons.file.Language;

public class Theme extends YamlConfig implements Cacheable {
	
	private static final long serialVersionUID = -8408139312509647891L;
	
	public static final ChatColor success = ChatColor.GREEN;
	public static final ChatColor severe = ChatColor.DARK_RED;
	public static final ChatColor failure = ChatColor.RED;
	public static final ChatColor reset = ChatColor.RESET;
	
	private UniqueItem icon;
	
	private short data, subdata;
	
	private String name, category;

	private Map<BiCell<String, String>, String> tags = new LinkedHashMap<BiCell<String, String>, String>();

	private Map<BiCell<Language, ThemeMessage>, String> messages = new LinkedHashMap<BiCell<Language, ThemeMessage>, String>();
	
	private boolean enabled;
	
	public Theme(String name) {
		
		super(Fukkit.getInstance(), "themes" + separator + name, "theme", ConfigHelper.assets + "themes" + separator + "theme.yml");
		
		this.name = name;
		
		FileConfiguration themConf = this.getConfig();
		
		this.category = themConf.getString("Display.Category", "FleX_Engine").replace("_", " ");
		
		String parse = themConf.getString("Display.Icon.Material", "BOOK");
		
		Material iconMat = null;
		
		/**
		 * Just so we don't get pointless errors printed to Console#log post 1.13.
		 */
		if (parse.equals("BOOK_AND_QUILL"))
			iconMat = VersionUtils.material("BOOK_AND_QUILL", "WRITABLE_BOOK");
		
		if (parse.equals("INK_SACK"))
			iconMat = VersionUtils.material("INK_SACK", "INK_SAC");
		
		if (parse.equals("RED_ROSE"))
			iconMat = VersionUtils.material("RED_ROSE", "RED_TULIP");
		
		if (iconMat == null)
			iconMat = VersionUtils.material(parse, Material.BOOK.name());
		
	    this.icon = new UniqueItem(iconMat, "&f", 1, (short) themConf.getInt("Display.Icon.Data", 0));
	    
		this.data = (short) themConf.getInt("Display.Blocks.Primary.Data", 0);
		this.subdata = (short) themConf.getInt("Display.Blocks.Secondary.Data", 0);
		
		this.enabled = themConf.getBoolean("Enabled", true);
		
		this.loadTags();
		this.loadMessages();
		
	}

	public String getName() {
		return this.name;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public ItemStack getIcon() {
		return this.icon;
	}
	
	public short getDecorationData(boolean primary) {
		return primary ? this.data : this.subdata;
	}
	
	public String getMessage(ThemeMessage message) {
		return this.getMessage(Language.ENGLISH, message);
	}
	
	public String getMessage(Language language, ThemeMessage message) {
		
		Entry<BiCell<Language, ThemeMessage>, String> entry = this.messages.entrySet().stream().filter(e -> {
			return e.getKey().a() == language && e.getKey().b() == message;
		}).findFirst().orElse(null);
		
		return entry != null ? entry.getValue() : null;
		
	}
	
	public Map<BiCell<Language, ThemeMessage>, String> getMessages() {
		return this.messages;
	}
	
	public Map<BiCell<String, String>, String> getTags() {
		return this.tags;
	}
	
	public void setEnabled(boolean enabled) {
		
		this.getConfig().set("Enabled", enabled);
		this.save();
		
		this.enabled = enabled;
		
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void loadTags() {
		
		FileConfiguration conf = this.getConfig();
		ConfigurationSection section = conf.getConfigurationSection("Tags");
		
		if (section == null)
			return;
		
		section.getKeys(false).stream().forEach(t -> {
			
			String tag = conf.getString("Tags." + t + ".Tag");
			
			if (tag == null)
				return;
			
			String formatted = ConfigUtils.sequence(conf, "Tags." + t);
			
			this.tags.put(new BiCell<String, String>() {
				
				private static final long serialVersionUID = 5443606980733742295L;

				@Override
				public String a() {
					return t;
				}

				@Override
				public String b() {
					return tag;
				}
				
			}, formatted.contains("&g") || formatted.contains(ChatColor.COLOR_CHAR + "g") || formatted.contains("&h") || formatted.contains(ChatColor.COLOR_CHAR + "h") ?
					formatted : FormatUtils.format(formatted));
			
		});
		
	}
	
	public void loadMessages() {
		
		if (!this.messages.isEmpty())
			this.messages.clear();
		
		Map<BiCell<String, String>, String> tags = this.tags;
		
		if (tags.isEmpty())
			Task.debug("" + this.name, "No tags were found.");
		
		for (Language language : Language.values()) {
			
			String writeToPath = "themes" + separator + this.name + separator + "lang";
			String defaultFromPath = ConfigHelper.assets + "themes" + separator + "lang" + separator;
			
			YamlConfig langYaml = new YamlConfig(Fukkit.getInstance(), writeToPath, language.toString(), defaultFromPath + language + ".yml");
			FileConfiguration langConf = langYaml.getConfig();
			
			langConf.getKeys(true).stream().forEach(k -> {
				
				ThemeMessage message =
						ThemeMessage.valueOfKey(k);
				
				if (message == null)
					return;
				
				this.messages.put(new BiCell<Language, ThemeMessage>() {
					
					private static final long serialVersionUID = 4392795395138617642L;

					@Override
					public Language a() {
						return language;
					}

					@Override
					public ThemeMessage b() {
						return message;
					}
					
				}, langConf.getString(k));
				
			});
			
		}
		
	}
	
	public String format(String s) {
		
		if (s == null)
			return s;
		
		String format = s.replace("\\s", Theme.reset + " ");
		
		for (Entry<BiCell<String, String>, String> tag : this.getTags().entrySet())
			if (format.contains(tag.getKey().b())) format = format.replace(tag.getKey().b(), tag.getValue() != null ? tag.getValue() : "");
		
		for (Emoji emoji : Emoji.values())
			format = format.replace("[" + emoji.name() + "]", emoji.toString());
		
		return FormatUtils.format(format);
		
	}

}
