package net.md_5.fungee.listeners;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.md_5.fungee.FungeeCord;
import net.md_5.fungee.utils.FormatUtils;

public class QueueListeners implements Listener {
	
	public static final List<UUID> pending_queue = new LinkedList<UUID>();
	public static final Map<UUID, ScheduledTask> pending_tasks = new HashMap<UUID, ScheduledTask>();
	
	public QueueListeners() {
		ProxyServer.getInstance().getPluginManager().registerListener(FungeeCord.getInstance(), this);
	}
	
	@EventHandler
	public void event(LoginEvent event) {
		
		UUID uuid = event.getConnection().getUniqueId();
		
		if (!pending_queue.contains(uuid)) {
			
			pending_queue.add(uuid);
			pending_tasks.put(uuid, ProxyServer.getInstance().getScheduler().schedule(FungeeCord.getInstance(), () -> {
				
				if (!pending_queue.contains(uuid))
					return;
				
				pending_queue.remove(uuid);
				pending_tasks.remove(uuid);
				
			}, 30L, TimeUnit.SECONDS));
			
		}
		
		int index = pending_queue.indexOf(uuid) + 1;
		
		if (index == 1) {
			ProxyServer.getInstance().getScheduler().schedule(FungeeCord.getInstance(), () -> {
				
				if (!pending_queue.contains(uuid))
					return;
				
				pending_queue.remove(uuid);
				pending_tasks.remove(uuid);
				
			}, 3L, TimeUnit.SECONDS);
			return;
		}
		
		event.setCancelReason(FormatUtils.format("&8&l[&4&lFleX&8&l] &fYou are number &b" + index + "&r &fin the queue&8.\n\n&7Please re-connect within &e30&r &7seconds\n&7to keep your place in the queue&8."));
		event.setCancelled(true);
		return;
		
	}

}
