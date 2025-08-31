package org.fukkit.command.defaults.admin;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.metadata.MetadataValue;
import org.fukkit.Fukkit;
import org.fukkit.api.helper.PlayerHelper;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.player.FleXPlayerBroadcastEvent;
import org.fukkit.metadata.FleXFixedMetadataValue;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Variable;
import io.flex.commons.utils.StringUtils;

public class BroadcastSubCommand extends AbstractAdminSubCommand {
	
	public BroadcastSubCommand(AdminCommand command) {
		super(command, "say", "broadcast", "bc");
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (args.length > 0) {
			
			FleXPlayer player = (FleXPlayer) sender;
			
			if (args.length == 1) {
				
				if (args[0].equalsIgnoreCase("confirm")) {
					
					if (!player.hasMetadata("admin.say.message")) {
						player.sendMessage(ThemeMessage.BROADCAST_CONFIRM_DENIED.format(player.getTheme(), player.getLanguage()));
						return false;
					}
					
					List<MetadataValue> bc = player.getMetadata("admin.say.message");
					
					String message = bc.get(0).asString();
					
					try {
						
						FleXPlayerBroadcastEvent event = new FleXPlayerBroadcastEvent(player, message);
						
						Fukkit.getEventFactory().call(event);
						
						if (event.isCancelled())
							return false;
						
						player.sendMessage(ThemeMessage.BROADCAST_SUCCESS.format(player.getTheme(), player.getLanguage()));
						
						Bukkit.getOnlinePlayers().stream().forEach(p -> {
							
							FleXPlayer fp = PlayerHelper.getPlayerSafe(p.getUniqueId());
							
							fp.sendMessage(ThemeMessage.BROADCAST_MESSAGE.format(player.getTheme(), player.getLanguage(),
									
									new Variable<String>("%name%", player.getName()),
									new Variable<String>("%player%", player.getDisplayName()),
									new Variable<String>("%display%", player.getDisplayName(fp.getTheme(), true)),
									new Variable<String>("%rank%", fp.getRank().getDisplay(fp.getTheme(), false)),
									new Variable<String>("%viewers%", "GLOBAL"),
									new Variable<String>("%message%", message)
									
							));
							
						});
						
						return true;
						
					} catch (Exception e) {
						
						e.printStackTrace();
						
						player.sendMessage(ThemeMessage.BROADCAST_FAILURE.format(player.getTheme(), player.getLanguage()));
						return false;
						
					} finally {
						
						player.removeMetadata("admin.say.message", Fukkit.getInstance());
						player.removeMetadata("admin.say.message.global", Fukkit.getInstance());
						
					}
					
				}
				
				if (args[0].equalsIgnoreCase("dismiss")) {
					
					if (!player.hasMetadata("admin.say.message")) {
						player.sendMessage(ThemeMessage.BROADCAST_DISMISS_DENIED.format(player.getTheme(), player.getLanguage()));
						return false;
					}
					
					player.sendMessage(ThemeMessage.BROADCAST_DISMISS_SUCCESS.format(player.getTheme(), player.getLanguage()));
					return true;
					
				}
				
			}
			
			String message = StringUtils.join(args, " ");
			
			player.setMetadata("admin.say.message", new FleXFixedMetadataValue(message));
			
			player.sendMessage(ThemeMessage.BROADCAST_CONFIRM_VIEW.format(player.getTheme(), player.getLanguage(),
					
					new Variable<String>("%name%", player.getName()),
					new Variable<String>("%player%", player.getDisplayName()),
					new Variable<String>("%display%", player.getDisplayName(player.getTheme(), true)),
					new Variable<String>("%rank%", player.getRank().getDisplay(player.getTheme(), false)),
					new Variable<String>("%viewers%", "Global"),
					new Variable<String>("%flags%", "(default) Global"),
					new Variable<String>("%message%", message)
					
			));
			
			return true;
			
		}
		
		this.command.usage(sender, "/<command> say <confirm/dismiss>", "/<command> say <message> [-g]");
		return false;
		
	}

}
