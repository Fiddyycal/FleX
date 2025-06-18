package org.fukkit.cache;

import static java.io.File.separator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;
import org.fukkit.Fukkit;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.scoreboard.playerlist.tab.FleXImageSkin;

import io.flex.FleX.Task;
import io.flex.commons.cache.LinkedCache;
import io.flex.commons.utils.NumUtils;

public class SkinCache extends LinkedCache<FleXSkin, BufferedImage> {
	
	private static final long serialVersionUID = -8519315256465075287L;
	
	public static final FleXImageSkin
	
	ICON_LIGHTNING_AQUA = null,// = new FleXImageSkin(TabUtils.face("lightning_aqua.png")),
	ICON_LIGHTNING_GRAY = null,// = new FleXImageSkin(TabUtils.face("lightning_gray.png")),
	ICON_LIGHTNING_WHITE = null,// = new FleXImageSkin(TabUtils.face("lightning_white.png")),
	ICON_LIGHTNING_OFFRED = null;// = new FleXImageSkin(TabUtils.face("lightning_offred.png"));

	public SkinCache() {
		super((s, i) -> s instanceof FleXImageSkin && i == ((FleXImageSkin)s).getImage());
	}
	
	public FleXImageSkin getByUniqueId(UUID uuid) {
		return (FleXImageSkin) this.stream().filter(s -> s instanceof FleXImageSkin).filter(s -> s.getName().equals(uuid + "_IMAGE")).findFirst().orElse(null);
	}
	
	@Override
	public FleXSkin getRandom() {
		List<FleXSkin> list = this.stream().filter(s -> s instanceof FleXImageSkin == false).collect(Collectors.toList());
		return list.get(NumUtils.getRng().getInt(0, list.size()-1));
	}
	
	@Override
	public boolean load() {
		
		Task.print("Disguise", "Pre-loading skins for efficient disguising...");
		
		this.add(FleXSkin.DEFAULT);
		
		File skins = new File("flex" + separator + "data" + separator + "local" + separator + "disguises" + separator + "skins");
		
		if (!skins.exists())
			skins.mkdirs();
		
		if (skins.list().length > 0) {
			
			for (File data : skins.listFiles()) {
				
				UUID uid = null;
				
				try {
					uid = UUID.fromString(data.getName().replace(".yml", ""));
				} catch (IllegalArgumentException e) {
					continue;
				}
				
				YamlConfiguration conf = YamlConfiguration.loadConfiguration(data);
				
				Task.print("Disguise", "Pre-loading skin from player " + conf.getString("Name") + "...");
				
				String value = conf.getString("Skin.value");
				String signature = conf.getString("Skin.signature");
				
				this.add(Fukkit.getImplementation().createSkin(uid.toString(), value, signature));
				
			}
			
		}
		
		Task.print("Disguise", "Done!");
		return true;
		
	}

}
