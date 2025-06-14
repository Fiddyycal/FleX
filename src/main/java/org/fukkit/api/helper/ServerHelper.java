package org.fukkit.api.helper;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.entity.Player;
import org.fukkit.Fukkit;
import org.fukkit.utils.BukkitUtils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;

public class ServerHelper {

	public static <T extends Player> void connect(T player, String name) throws Exception {
		
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		 
		try {
			
			out.writeUTF("Connect");
			out.writeUTF(name);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			BukkitUtils.runLater(() -> player.sendMessage(ChatColor.WHITE + "Error(s) printed to console."));
			
			throw e;
			
		}
		
		player.sendPluginMessage(Fukkit.getInstance(), "BungeeCord", b.toByteArray());
		
	}
	
	public static <T extends Player> void count(T player, String name) {
		
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        
        try {
			
			out.writeUTF("PlayerCount");
			out.writeUTF(name);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			BukkitUtils.runLater(() -> player.sendMessage(ChatColor.WHITE + "Error(s) printed to console."));
			
			throw e;
			
		}
        
        player.sendPluginMessage(Fukkit.getInstance(), "BungeeCord", out.toByteArray());
        
    }
	
}
