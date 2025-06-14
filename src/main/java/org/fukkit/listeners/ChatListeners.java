package org.fukkit.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.fukkit.Fukkit;
import org.fukkit.PlayerState;
import org.fukkit.api.helper.PlayerHelper;
import org.fukkit.config.Configuration;
import org.fukkit.config.YamlConfig;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.ChatUtils;

import io.flex.commons.file.Variable;
import net.md_5.fungee.Memory;
import net.md_5.fungee.channel.Channel;
import net.md_5.fungee.utils.NetworkUtils;

public class ChatListeners extends FleXEventListener {

	private double delay;
	private long weight;
	
	public static final List<UUID> chat_delay = new ArrayList<UUID>();
	
	public ChatListeners() {
		
		YamlConfig yaml = Fukkit.getResourceHandler().getYaml(Configuration.ENGINE);
		FileConfiguration conf = yaml.getConfig();
		
		this.delay = conf.getDouble("Chat.Delay.Seconds", 1.5);
		this.weight = conf.getLong("Chat.Delay.Bypass", 10);
		
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
    public void event(AsyncPlayerChatEvent event) {
		
		if (event.isCancelled())
			return;
		
		UUID uuid;
		
		FleXPlayer player = PlayerHelper.getPlayerSafe((uuid = event.getPlayer().getUniqueId()));
		
		String name = player.getName();
		String message = event.getMessage();
		
		if (player.getState() == PlayerState.CONNECTING || player.getState() == PlayerState.DISCONNECTING || player.getState() == PlayerState.UNKNOWN) {
			event.setCancelled(true);
			return;
		}
		
		if (chat_delay.contains(uuid)) {
			
			player.sendMessage(ThemeMessage.CHAT_DENIED_DELAY.format(player.getTheme(), player.getLanguage(), new Variable<Double>("%delay%", this.delay)));
			
			event.setCancelled(true);
			return;
			
		}
		
		if (player.getRank().getWeight() < this.weight) {
			
			chat_delay.add(uuid);
			
			BukkitUtils.runLater(() -> chat_delay.remove(uuid), 30L, false);
			
		}
		
		Set<FleXPlayer> mentions = new HashSet<FleXPlayer>();
		
		for (FleXPlayer p : Fukkit.getServerHandler().getOnlinePlayersUnsafe()) {
			
			if (message.toLowerCase().contains("@" + p.getName().toLowerCase())) {
				message = message.replaceAll("@(?i)" + p.getName(), "@" + p.getName());
				mentions.add(p);
			}
			
		}
		
		/**
		 * For console logging, as changing names when disguising
		 * will actually change the Minecraft username.
		 */
		event.setFormat((player.isDisguised() ? "(" + name + ") " + player.getDisplayName() : name) + ":" + ChatColor.RESET + " %2$s");
		
		Set<Player> recipients = event.getRecipients();
		Set<FleXPlayer> recipientsFp = new HashSet<FleXPlayer>();
		
		recipients.forEach(p -> {
			
			FleXPlayer fp = Fukkit.getPlayerExact(p);
			
			if (fp == null)
				return;
			
			recipientsFp.add(fp);
			
		});
		
		recipients.clear();
		
		boolean sent = ChatUtils.sendChat(player, player.isMasked() ? player.getMask() : player.getRank(), message, null, mentions, recipientsFp, event.isAsynchronous());
		
		if (!sent) {
			event.setCancelled(true);
			return;
		}
		
		Map<String, String> entries = new HashMap<String, String>();
		
		entries.put("UUID", uuid.toString());
		entries.put("Message", message);
		entries.put("Mentioned", mentions.stream().map(p -> p.getUniqueId().toString()).collect(Collectors.toList()).toString());
		
		/**
		 * TODO: Make this optional.
		 * Send chat to the proxy, so it can send it to all the OTHER servers.
		 */
		Channel channel = Memory.CHANNEL_CACHE.get("PlayerChat");
		
		if (channel != null)
			channel.send(player, entries, NetworkUtils.ALL_OTHER);
		
    }

}
