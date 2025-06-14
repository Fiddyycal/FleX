package org.fukkit.utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.json.JsonBuffer;
import org.fukkit.json.JsonComponent;
import org.fukkit.reward.Rank;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.Nullable;
import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.fungee.event.FleXPlayerChatEvent;
import net.md_5.fungee.event.FleXPlayerMentionEvent;

public class ChatUtils {

	public static boolean sendChat(FleXPlayer player, String message) {
		
		Set<FleXPlayer> recipients = new HashSet<FleXPlayer>();
		
		Bukkit.getOnlinePlayers().forEach(p -> {
			
			FleXPlayer fp = Fukkit.getPlayerExact(p);
			
			if (fp == null || !fp.isOnline())
				return;
			
			recipients.add(fp);
			
		});
		
		return sendChat(player, player.isMasked() ? player.getMask() : player.getRank(), message, null, null, recipients, true);
	}

	@SuppressWarnings("deprecation")
	public static boolean sendChat(FleXPlayer player, Rank rank, String message, @Nullable String fromServer, @Nullable Set<FleXPlayer> mentions, Set<FleXPlayer> recipients, boolean async) {
		
		boolean mention = mentions != null && mentions.size() > 0;
		
		FleXPlayerChatEvent event = new FleXPlayerChatEvent(
				
				player,
				rank,
				message,
				fromServer != null ? fromServer : Fukkit.getServerHandler().getName(),
				recipients,
				mention ? mentions : new HashSet<FleXPlayer>(),
				async);
		
		Fukkit.getEventFactory().call(event);
		
		if (event.isCancelled())
			return false;
		
		message = event.getMessage();
		
		for (FleXPlayer p : recipients) {
			
			Theme theme = p.getTheme();
			Language lang = p.getLanguage();
			
			if (theme == null)
				continue;
			
			if (mention) {
				
				for (FleXPlayer pl : mentions) {
					
					message = message.replaceAll("@" + pl.getName(), (p.getName().equals(pl.getName()) ?
							
							ThemeMessage.CHAT_MENTION_PREFIX.format(theme, lang)[0] : "") +
							ThemeMessage.CHAT_MENTION_HIGHLIGHT.format(theme, lang,
									
									new Variable<String>("%name%", pl.getDisplayName() + ChatColor.RESET),
									new Variable<String>("%player%", pl.getName() + ChatColor.RESET),
									new Variable<String>("%display%", pl.getDisplayName(theme) + ChatColor.RESET),
									new Variable<String>("%role%", rank.getDisplay(theme, false) + ChatColor.RESET),
									new Variable<String>("%rank%", rank.getDisplay(theme, true) + ChatColor.RESET))[0]);
					
				}
				
			}
			
			Variable<?>[] variables = new Variable<?>[] {
				
					new Variable<String>("%name%", player.getDisplayName()),
					new Variable<String>("%player%", player.getName()),
					new Variable<String>("%display%", player.getDisplayName(theme)),
					new Variable<String>("%role%", rank.getDisplay(theme, false)),
					new Variable<String>("%rank%", rank.getDisplay(theme, true)),
					new Variable<String>("%message%", message.replace("\\", "\\\\").replace("\"", "\\\""))
					
			};
			
			String[] format = p.getRank().isStaff() ?
					
					ThemeMessage.CHAT_FORMAT_STAFF.format(theme, lang, variables) :
					ThemeMessage.CHAT_FORMAT.format(theme, lang, variables);
			
			for (String m : format) {
				
				JsonBuffer buffer = new JsonBuffer().append(new JsonComponent(m)).replace("%interactable%", new JsonComponent(theme.format("<clickable>Panel"))
						
						.onHover(p.getTheme().format("<interactable>Open the FleX panel<pp>."))
						.onClick(Action.RUN_COMMAND, "/flex " + player.getName()));
				
				p.sendJsonMessage(buffer);
				
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
