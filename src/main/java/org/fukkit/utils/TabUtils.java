package org.fukkit.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.playerlist.tab.FleXImageSkin;
import org.fukkit.theme.Theme;

import io.flex.FleX;
import io.flex.commons.utils.StringUtils;

@SuppressWarnings("deprecation")
public class TabUtils {
	
	private static final String url_fetch_layer = "https://minotar.net/helm/%s/8.png";
	
	public static FleXImageSkin fetchSkin(FleXPlayer player) {
		
		FleXImageSkin skin = Memory.SKIN_CACHE.getByUniqueId(player.getUniqueId());
		
		if (skin == null) {
			
			skin = Fukkit.getImplementation().createImageSkin(player);
			
			Memory.SKIN_CACHE.add(skin);
			
		}
		
		return skin;
		
	}
	
	public static BufferedImage face(String name) {
		try {
			
			BufferedImage image;
			
			if (name.endsWith(".png"))
				image = ImageIO.read(FleX.getResourceAsStream(ConfigHelper.assets + "tab" + File.separator + name));
			
			else image = ImageIO.read(new URL(String.format(url_fetch_layer, name)));
			
			return image;
			
        } catch (IOException e) {
        	
        	e.printStackTrace();
        	
    		return null;
        	
        }
	}
	
	public static String[] threePartListName(FleXPlayer player, Theme theme) {
		
		String rank = player.getRank().getDisplay(theme, false);
		String colors = ChatColor.getLastColors(rank);
		String display = colors + player.getDisplayName();
		
		int max = 12 - colors.length();
		
		if (max < 0)
			max = 0;
		
		return new String[] { StringUtils.shorten(rank, 0, 16), StringUtils.shorten(display, 0, max), display.length() > max ? StringUtils.shorten(display, max, max + 16) : "" };
		
	}
	
	public static String connection(int ping) {
		
		if (ping <= 1)
			return "&bHost";
		
		if (ping <= 5)
			return "&2Exceptional";
		
		else if (ping <= 10)
			return "&2Excellent";

		else if (ping <= 20)
			return "&2Great";

		else if (ping <= 30)
			return "&2Very Good";

		else if (ping <= 40)
			return "&aGood";

		else if (ping <= 50) 
			return "&aOptimal";

		else if (ping <= 60)
			return "&eAdequate";

		else if (ping <= 70)
			return "&eFair";

		else if (ping <= 80)
			return "&eSubstantial";

		else if (ping <= 90)
			return "&6Poor";

		else if (ping <= 100)
			return "&6Very Poor";

		else if (ping <= 200)
			return "&cBad";

		else if (ping <= 400)
			return "&cVery Bad";

		else if (ping <= 600)
			return "&cUnplayable";

		else if (ping <= 800)
			return "&4Broken";

		else if (ping <= 1000)
			return "&4Hello?";

		return "&4Timed out";
		
	}
	
}
