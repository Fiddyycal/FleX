package org.fukkit.listeners;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerCommandEvent;
import org.fukkit.Fukkit;
import org.fukkit.api.helper.BotHelper;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.bot.BotListenEvent;

import io.flex.FleX.Task;
import io.flex.commons.console.Console;
import io.flex.commons.utils.NumUtils;
import io.flex.commons.utils.StringUtils;

public class FleXBotListeners extends FleXEventListener {
	
	private static final String[] unknown_response = {
			"FleX Bot: Sorry, I can't help you with that.",
			"FleX Bot: ...",
		    "FleX Bot: I don't understand...",
			"FleX Bot: My programming limits me, please elaborate.",
			"FleX Bot: I'm sorry, I don't quite understand.",
			"FleX Bot: Need help? Just ask me and I'll list all console gestures!",
	};
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void event(ServerCommandEvent event) {

		if (event.getSender() instanceof ConsoleCommandSender == false)
			return;
			
		if (StringUtils.equalsIgnoreCaseAny(event.getCommand(), Console.IGNORE_COMMANDS))
			return;
		
		if (StringUtils.equalsIgnoreCaseAny(event.getCommand(), Console.BLOCKED_COMMANDS)) {
			Task.print("-O_O-", "FleX Bot: Sorry, I can't allow you to use that gesture.");
			event.setCancelled(true);
			return;
		}
		
		if (Fukkit.getCommandFactory().isKnown(event.getCommand().split(" ")[0]))
			return;
		
		BotListenEvent listen = new BotListenEvent(event.getSender(), event.getCommand(), false);
		
		Fukkit.getEventFactory().call(listen);
		
		if (listen.isCancelled())
			return;
		
		event.setCancelled(true);
		
		if (listen.hasResponse())
			Task.print("-O_O-", listen.getResponse());
			
		else Task.print("-O_O-", unknown_response[NumUtils.getRng().getInt(0, unknown_response.length - 1)]);
		
	}
	
	@EventHandler
	public void event(BotListenEvent event) {
		
		String LoCaseCompGesture = event.getCompleteGesture().toLowerCase();
		String gesture = event.getGesture();
		
		boolean meta = BotHelper.META_DATA.containsKey(event.getSender());
		boolean option1 = false;
		
		if ((option1 = LoCaseCompGesture.contains("confirm") || LoCaseCompGesture.contains("yes") || gesture.equalsIgnoreCase("y") || gesture.equalsIgnoreCase("ok")) ||
			!(LoCaseCompGesture.contains("deny") || LoCaseCompGesture.contains("no") || gesture.equalsIgnoreCase("n"))) {
			
			if (!meta) {
				event.setResponse(LoCaseCompGesture.contains("confirm") || LoCaseCompGesture.contains("deny") ? BotHelper.CONFIRM_FAIL : BotHelper.GREAT);
				return;
			}
			
			System.out.println("........... " + option1);
			
			/* leaving this here for reference.
			if (BotHelper.META_DATA.get(event.getSender()).equalsIgnoreCase("clear_backups")) {
				
				File backup = new File(world_backups_path);
				File[] files = backup.listFiles();
				
				IntStream.range(0, files.length).mapToObj(i -> files[i]).forEach(f -> {
					FileUtils.delete(f);
				});
				
				BotHelper.META_DATA.remove(event.getSender());
				
			}
			
			event.setResponse(String.format(BotHelper.CONFIRM_SUCCESS, option1 ? "I have cleared your backups folder." : "I won't touch it.")); */
			return;
			
		}
		
	}
	
}
