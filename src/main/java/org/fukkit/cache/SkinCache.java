package org.fukkit.cache;

import java.awt.image.BufferedImage;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.fukkit.Fukkit;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.scoreboard.playerlist.tab.FleXImageSkin;

import io.flex.FleX.Task;
import io.flex.commons.cache.LinkedCache;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.NumUtils;

public class SkinCache extends LinkedCache<FleXSkin, BufferedImage> {
	
	private static final long serialVersionUID = -8519315256465075287L;
	
	public static final FleXImageSkin
	
	ICON_LIGHTNING_AQUA = null,// = new FleXImageSkin(TabUtils.face("lightning_aqua.png")),
	ICON_LIGHTNING_GRAY = null,// = new FleXImageSkin(TabUtils.face("lightning_gray.png")),
	ICON_LIGHTNING_WHITE = null,// = new FleXImageSkin(TabUtils.face("lightning_white.png")),
	ICON_LIGHTNING_OFFRED = null;// = new FleXImageSkin(TabUtils.face("lightning_offred.png"));
	
	private static final List<String> names = new ArrayList<String>();
	
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
	
	public String getRandomName() {
		return names.get(NumUtils.getRng().getInt(0, names.size()-1));
	}
	
	@Override
	public boolean load() {
		
		// TODO load names and skins from files if database table is empty.
		//Task.print("Disguise", "Uploading skins and names on file...");
		
		Task.print("Disguise", "Pre-loading skins and names for efficient disguising...");
		
		this.add(FleXSkin.DEFAULT);
		
		try {
			
			int skip = NumUtils.getRng().getInt(10000, 30000);
			int limit = 5000;
			
			Set<SQLRowWrapper> rows = Fukkit.getConnectionHandler().getDatabase().result(
					
					"WITH ranked AS (SELECT *, ROW_NUMBER() OVER (PARTITION BY signed ORDER BY name ASC) AS rn FROM flex_disguises) " +
					"SELECT name, signature, value, signed FROM ranked WHERE (signed = TRUE) OR (signed = FALSE AND rn > " + skip + ") ORDER BY signed DESC, name ASC LIMIT " + limit);
			
			for (SQLRowWrapper row : rows) {
				
				String name = row.getString("name");
				String value = row.getString("value");
				String signature = row.getString("signature");
				
				if (name == null)
					continue;
				
				if (!names.contains(name))
					names.add(name);
				
				if (value != null && signature != null)
					this.add(Fukkit.getImplementation().createSkin(name, value, signature));
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Task.print("Disguise", "Done!");
		return true;
		
	}
	
}
