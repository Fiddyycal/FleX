package org.fukkit.command.defaults.admin;

import java.io.File;
import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.config.Configuration;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXQuickEventListener;
import org.fukkit.handlers.ResourceHandler;
import org.fukkit.metadata.FleXFixedMetadataValue;
import org.fukkit.utils.BukkitUtils;

import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.FileUtils;

public class TruncateSubCommand extends AbstractAdminSubCommand {
	
	private static final String password = "meshbedatabesh" + 2269;
	
	public TruncateSubCommand(AdminCommand command) {
		
		super(command, "truncate", "clear");
		
		FleXQuickEventListener.listen(AsyncPlayerChatEvent.class, event -> {
			
			BukkitUtils.runLater(() -> {
				
				FleXPlayer player = Fukkit.getPlayerExact(event.getPlayer());
				
				if (!player.hasMetadata("truncate"))
					return;
					
				event.setCancelled(true);
				
				if (event.getMessage().equalsIgnoreCase("cancel")) {
					
					// TODO: Add to message config.
					
					player.removeMetadata("truncate", Fukkit.getInstance());
					player.sendMessage(player.getTheme().format("<engine><failure>Interface cancelled<pp>."));
					return;
					
				}
				
				if (!event.getMessage().equalsIgnoreCase(password)) {
					
					// TODO: Add to message config.
					
					player.sendMessage(player.getTheme().format("<engine><failure>That is not the correct password<pp>."));
					return;
					
				}
				
				TruncateSubCommand.this.truncate(player.getPlayer(), (String[]) player.getMetadata("truncate").get(0).value());
				
			});
			
		});
		
	}

	@Override
	public boolean perform(String[] args, String[] flags) {
		
		if (args.length != 0) {
			this.command.usage("/<command> truncate/clear [-t, -m, -c, -d, -b, -f]");
			return false;
		}
		
		if (this.command.getSender() instanceof ConsoleCommandSender) {
			this.truncate(this.command.getSender(), flags);
			return false;
		}
		
		if (!this.command.getPlayer().hasMetadata("truncate"))
			this.command.getPlayer().setMetadata("truncate", new FleXFixedMetadataValue(flags));
		
		this.command.getPlayer().sendMessage(this.command.getPlayer().getTheme().format("<engine><pc>Please enter the password into chat<pp>."));
		this.command.getPlayer().sendMessage(this.command.getPlayer().getTheme().format("<engine><pc>Type <sp>\"<sc>cancel<sp>\"\\s<pc>to cancel<pp>."));
		
		return true;
		
	}
	
	@SuppressWarnings("deprecation")
	private void truncate(CommandSender sender, String[] flags) {
		
		boolean themeData = ArrayUtils.contains(flags, "-t");
		boolean messages = ArrayUtils.contains(flags, "-m");
		boolean configurations = ArrayUtils.contains(flags, "-c");
		boolean persistentData = ArrayUtils.contains(flags, "-d");
		boolean backups = ArrayUtils.contains(flags, "-b");
		boolean flow = ArrayUtils.contains(flags, "-f");
		
		if (!themeData)
			Memory.THEME_CACHE.forEach(t -> FileUtils.delete(t));
		
		if (!messages)
			Memory.THEME_CACHE.forEach(t -> {
				
				File file = FileUtils.getFile(t.getParentFile().getAbsolutePath(), "lang", false);
				
				if (file.exists())
					FileUtils.delete(file.listFiles());
				
			});
		
		if (!configurations) {
			
			ResourceHandler handler = Fukkit.getResourceHandler();
			
			FileUtils.delete(
					
					handler.getYaml(Configuration.ENGINE),
					handler.getYaml(Configuration.FLEX),
					handler.getYaml(Configuration.NETWORK),
					handler.getYaml(Configuration.RANKS),
					handler.getYaml(Configuration.SQL),
					handler.getYaml(Configuration.THEMES));
			
		}
		
		if (!persistentData) {
			FileUtils.delete(FileUtils.getFile(ConfigHelper.flex_path + File.separator + "local", false).listFiles());
			FileUtils.delete(FileUtils.getFile(ConfigHelper.flex_path + File.separator + "sqlite", false).listFiles());
			Arrays.stream(FileUtils.getFile(ConfigHelper.flow_path, false).listFiles()).filter(f -> f.isDirectory()).forEach(d -> FileUtils.delete(d));
		}
		
		if (!backups)
			FileUtils.delete(FileUtils.getFile(ConfigHelper.world_backups_path, false).listFiles());
		
		if (!flow)
			FileUtils.delete(FileUtils.getFile(ConfigHelper.flow_path, false).listFiles());
		
		FleXPlayer player = sender instanceof Player ? Fukkit.getPlayerExact((Player)sender) : null;
		
		if (player != null) {
			
			player.removeMetadata("truncate", Fukkit.getInstance());
			player.sendMessage("[ThemeMessage=TRUNCATE_SUCCESS]");
			// TODO
			
		} else {
			
			sender.sendMessage("[ThemeMessage=TRUNCATE_SUCCESS]"); 
			// TODO
			
		}
		
	}

}
