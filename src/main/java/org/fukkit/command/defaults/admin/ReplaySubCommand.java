package org.fukkit.command.defaults.admin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.fukkit.Fukkit;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.flow.AsyncFleXPlayerReplayPreDownloadEvent;
import org.fukkit.recording.Replay;
import org.fukkit.theme.Theme;
import org.fukkit.utils.BukkitUtils;

import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;

public class ReplaySubCommand extends AbstractAdminSubCommand {
	
	public ReplaySubCommand(AdminCommand command) {
		super(command, "replay", "playback", "play");
	}

	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (args.length != 1) {
			this.command.usage(sender, "/<command> replay/playback/play <uid>", "/<command> replay/playback/play list");
			return false;
		}
		
		FleXPlayer player = (FleXPlayer) sender;
		
		Theme theme = player.getTheme();
		String uid = args[0];
		
		BukkitUtils.asyncThread(() -> {
			
			if (!player.isOnline())
				return;
			
			if (uid.equals("list")) {
				
				try {
					
					player.sendMessage(theme.format("<engine><pc>Loading complete replays, please wait<pp>..."));
					
					SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
					
					Set<SQLRowWrapper> rows = base.getRows("flex_recording", Arrays.asList("uuid"), Arrays.asList(SQLCondition.where("state").is("COMPLETE")));
					
					if (rows.isEmpty()) {
						player.sendMessage(theme.format("<engine><success>No replays found<pp>."));
						return;
					}
					
					player.sendMessage(theme.format("<engine><display>Replays<pp>:"));
					
					// TODO Json message that makes them clickable
					for (SQLRowWrapper row : rows)
						player.sendMessage(theme.format("<engine><pc>" + row.getString("uuid")));
					
					return;
					
				} catch (SQLException e) {
					
					e.printStackTrace();
					
					player.sendMessage(theme.format("<engine><failure>An error occurred<pp>:<reset> <failure>" + e.getMessage()));
					
				}
				
				return;
				
			}
			
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
