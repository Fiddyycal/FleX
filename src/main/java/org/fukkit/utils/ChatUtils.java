package org.fukkit.utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.fukkit.Fukkit;
import org.fukkit.PlayerState;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.player.FleXPlayerAsyncChatEvent;
import org.fukkit.event.player.FleXPlayerAsyncChatReceiveEvent;
import org.fukkit.event.player.FleXPlayerMentionEvent;
import org.fukkit.json.ChatJsonDisplayBuffer;
import org.fukkit.json.ChatJsonInteractableBuffer;
import org.fukkit.json.JsonBuffer;
import org.fukkit.json.JsonComponent;
import org.fukkit.reward.Rank;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.Nullable;
import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;

public class ChatUtils {

	public static boolean sendChat(FleXPlayer player, String message) {
		
		Set<FleXPlayer> recipients = new HashSet<FleXPlayer>();
		
		Bukkit.getOnlinePlayers().forEach(p -> {
			
			FleXPlayer fp = Fukkit.getPlayerExact(p);
			
			if (fp == null || !fp.isOnline())
				return;
			
			recipients.add(fp);
			
		});
		
		return sendChat(player, player.isMasked() ? player.getMask() : player.getRank(), message, null, null, recipients);
	}

	@SuppressWarnings("deprecation")
	public static boolean sendChat(FleXPlayer player, Rank rank, String message, @Nullable String fromServer, @Nullable Set<FleXPlayer> mentions, Set<FleXPlayer> recipients) {
		
		boolean mention = mentions != null && mentions.size() > 0;
		
		FleXPlayerAsyncChatEvent event = new FleXPlayerAsyncChatEvent(
				
				player,
				message,
				fromServer != null ? fromServer : Fukkit.getServerHandler().getName(),
				recipients,
				mention ? mentions : new HashSet<FleXPlayer>());
		
		Fukkit.getEventFactory().call(event);
		
		if (event.isCancelled())
			return false;
		
		message = event.getMessage();
		
		for (FleXPlayer p : recipients) {
			
			Theme theme = p.getTheme();
			Language lang = p.getLanguage();
			
			if (theme == null)
				continue;
			
			FleXPlayerAsyncChatReceiveEvent receiveEvent = new FleXPlayerAsyncChatReceiveEvent(
					
					player,
					p,
					message);
			
			Fukkit.getEventFactory().call(event);
			
			message = receiveEvent.getMessage();
			
			if (receiveEvent.isCancelled())
				continue;
			
			if (mention) {
				
				for (FleXPlayer pl : mentions) {
					
					message = message.replaceAll("@" + pl.getName(), (p.getName().equals(pl.getName()) ?
							
							ThemeMessage.CHAT_MENTION_PREFIX.format(theme, lang)[0] : "") +
							ThemeMessage.CHAT_MENTION_HIGHLIGHT.format(theme, lang,
									
									new Variable<String>("%name%", pl.getDisplayName() + ChatColor.RESET),
									new Variable<String>("%player%", pl.getName() + ChatColor.RESET),
									new Variable<String>("%display%", pl.getDisplayName(theme) + ChatColor.RESET),
									new Variable<String>("%role%", (player.getState() == PlayerState.SPECTATING ? "%dead%" : "") + rank.getDisplay(theme, false) + ChatColor.RESET),
									new Variable<String>("%rank%", rank.getDisplay(theme, true) + ChatColor.RESET))[0]);
					
				}
				
			}
			
			boolean mcgamer = theme.getName().equalsIgnoreCase("mcgamer");
			
			String mode = player.hasMetadata("mode.gamemaster") /*TODO mod mode*/ ? theme.format(mcgamer ? "&4GM&8|" : "<reset> <sp>&o(&4&oGamemaster<sp>&o)<reset> ") : "";
			String dead = player.getState() == PlayerState.SPECTATING ? theme.format(mcgamer ? "&4SPEC&8|" : "<reset> <sp>&o(&4&oDead<sp>&o)<reset> ") : "";
			
			Variable<?>[] variables = new Variable<?>[] {
				
					new Variable<String>("%name%", player.getDisplayName()),
					new Variable<String>("%player%", player.getName()),
					new Variable<String>("%role%", rank.getDisplay(theme, false) + ChatColor.RESET),
					new Variable<String>("%dead%", dead),
					new Variable<String>("%mode%", mode),
					new Variable<String>("%rank%", rank.getDisplay(theme, true)),
					new Variable<String>("%message%", message.replace("\\", "\\\\").replace("\"", "\\\""))
					
			};
			
			String[] format = p.getRank().isStaff() ?
					
					ThemeMessage.CHAT_FORMAT_STAFF.format(theme, lang, variables) :
					ThemeMessage.CHAT_FORMAT.format(theme, lang, variables);
			
			for (String m : format) {
				
				/* 
				 * Default json, less like mcgamer one.
				 * 
				JsonBuffer buffer = new JsonBuffer().append(new JsonComponent(m)).replace("%interactable%", new JsonComponent(theme.format("<clickable>Panel"))
						
						.onHover(p.getTheme().format("<interactable>Open the FleX panel<pp>."))
						.onClick(Action.RUN_COMMAND, "/flex " + player.getName()));
				*/
				
				JsonBuffer buff = new JsonBuffer().append(new JsonComponent(m))
						
						.replace("%interactable%", new ChatJsonInteractableBuffer(player, p))
						.replace("%display%", new ChatJsonDisplayBuffer(player, p, event.getPrefix(), event.getSuffix()));
				
				p.sendJsonMessage(buff);
				
			}
			
			if (mention) {
				
				if (mentions.stream().anyMatch(f -> f.getUniqueId().equals(p.getUniqueId()))) {
					
					FleXPlayerMentionEvent mentionEvent = new FleXPlayerMentionEvent(player, p, false);
					
					Fukkit.getEventFactory().call(mentionEvent);
					
					p.getPlayer().playSound(p.getLocation(), mentionEvent.getSound(), (float) 1, (float) 1);
					
				}
				
			}
			
		}
		
		return true;
		
	}
	
}
