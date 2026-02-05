package org.fukkit.command.defaults.admin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.command.CommandSender;
import org.fukkit.Fukkit;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.flow.AsyncFleXPlayerReplayPreDownloadEvent;
import org.fukkit.recording.Replay;
import org.fukkit.theme.Theme;
import org.fukkit.utils.BukkitUtils;

public class ReplaySubCommand extends AbstractAdminSubCommand {
	
	public ReplaySubCommand(AdminCommand command) {
		super(command, "replay", "playback", "play");
	}

	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (args.length != 1) {
			this.command.usage(sender, "/<command> replay/playback/play <uid>");
			return false;
		}
		
		FleXPlayer player = (FleXPlayer) sender;
		
		Theme theme = player.getTheme();
		String uid = args[0];
		
		BukkitUtils.asyncThread(() -> {
			
			if (player.isOnline())
				player.sendMessage(theme.format("<engine><pc>Finding replay<pp>..."));
			
			AsyncFleXPlayerReplayPreDownloadEvent event = new AsyncFleXPlayerReplayPreDownloadEvent(player, uid);
			
			Fukkit.getEventFactory().call(event);
			
			if (event.isCancelled())
				return;
			
			try {
				
				File recordings = new File(ConfigHelper.flow_path + File.separator + "recordings" + File.separator + uid);
				
				Replay replay = Replay.download(recordings, null);
				
				if (replay == null) {
					player.sendMessage(theme.format("<engine><failure>That replay could not be found<pp>."));
					return;
				}
				
				player.sendMessage(theme.format("<engine><success>Loading replay, please wait<pp>..."));
				
			} catch (SQLException | IOException e) {
				
				e.printStackTrace();
				
				player.sendMessage(theme.format("<engine><failure>An error occurred<pp>:<reset> <failure>" + e.getMessage()));
				return;
				
			}
			
		});
		
		return true;
		
	}

}
