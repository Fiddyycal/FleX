package org.fukkit.command.defaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.fukkit.Fukkit;
import org.fukkit.FukkitRunnable;
import org.fukkit.Memory;
import org.fukkit.command.Command;
import org.fukkit.command.ConsoleCommand;
import org.fukkit.command.FlaggedCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.handlers.ServerHandler;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.ItemUtils;
import org.fukkit.world.FleXWorld;

import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.CollectionUtils;
import io.flex.commons.utils.NumUtils;
import io.flex.commons.utils.StringUtils;

import net.md_5.fungee.server.ServerRegion;
import net.md_5.fungee.server.ServerVersion;

@SuppressWarnings("deprecation")
@GlobalCommand
@ConsoleCommand
@FlaggedCommand(flags = "-h")
@Command(name = "debug", usage = "/<command> [type] [-h]", aliases = { "bug", "deghost" }, description = "Prints debug information")
public class DebugCommand extends FleXCommandAdapter {
	
	private static final String NAME = "FungeeCord";
	private static final String VERSION = "1.0.1b-SNAPSHOT";
	private static final String COMMIT = "e274f23";
	private static final String TRAVERTINE = "86";

	private static final String AUTHOR = "md_5";
	private static final String ADDJUNCT = String.valueOf(new char[]{ '5', 'O', 'c', 'a', 'l' });
	
	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		/*
		 * 
		 * 
		 * 
		 * 
		 * String ed = Task.isDebugEnabled() ? "Disable" : "Enable";
		
		if (args.length != 0) {
			
			if (((FleXPlayer)sender) != null) {
				this.usage(sender);
				return false;
			}
			
			Task.print("-O_O-", "FleX Bot: " + ed + " debug messages are " + ed.toLowerCase() + "ed\". " + ed + " debug mode using \"/debug\".");
			return false;
			
		}
		
		Task.enableDebugMode(!Task.isDebugEnabled());
		Task.print("-O_O-", "FleX Bot: Debug messages have been " + ed + "ed.");
		return true;
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
		
		FleXPlayer player = (FleXPlayer) sender;
		
		boolean staff = sender instanceof ConsoleCommandSender || player.isStaff();
		
		if (args.length != 0 && args.length != 1 && args.length != 2) {
			
			if (staff) {
				
				this.usage(sender,
						
						"/<command> [-h]",
						"/<command> player/bot/item/world/data <player> [-h]",
						"/<command> server [-h]",
						"/<command> memory");
				
			} else this.usage(sender, "/<command> player/server/item/world/data [-h]");
			
		    return false;
		}
		
		if (args.length == 2 && !staff) {
			this.noPermission(sender);
			return false;
		}
		
		boolean flagged = flags != null && ArrayUtils.contains(flags, "-h");
		
		if (args.length == 0) {
			
			if (sender instanceof FleXPlayer == false) {
				sender.sendMessage(ThemeMessage.COMMAND_DENIED_STATE_CONSOLE.format(Memory.THEME_CACHE.stream().findFirst().get(), Language.ENGLISH, new Variable<String>("%command%", this.getName())));
				return false;
			}
			
			this.sendPlayerDebug(sender, flagged, true, player);
			return true;
			
		}
		
		FleXPlayer other = player;
		
		if (args.length == 2) {
			
			other = Fukkit.getPlayer(args[1]);
			
			if (args[0].equalsIgnoreCase("player")) {
				
				if (other == null) {
					this.playerNotFound(sender, args[1]);
					return false;
				}
				
				if (!other.isOnline()) {
					this.playerNotOnline(sender, other);
					return false;
				}
				
			} else if (args[0].equalsIgnoreCase("bot")) {
				
				if (other == null || other instanceof FleXBot == false) {
					
					boolean console = sender instanceof ConsoleCommandSender;
					
					Theme theme = console ? Memory.THEME_CACHE.getDefaultTheme() : ((FleXPlayer)sender).getTheme();
					
					Language lang = console ? Language.ENGLISH : ((FleXPlayer)sender).getLanguage();
					
					Variable<String> variable = new Variable<String>("%player%", args[1]);
					
					for (String message : ThemeMessage.COMMAND_PLAYER_NOT_FOUND.format(theme, lang, variable))
						((FleXPlayer)sender).sendMessage(message.replaceAll("Player", "Bot").replaceAll("player", "bot"));
					
					return false;
					
				}
				
			} else {
				
				if (staff) {
					
					this.usage(sender,
							
							"/<command> [-h]",
							"/<command> player/bot/item/world/data <player> [-h]",
							"/<command> server [-h]",
							"/<command> memory");
					
				} else this.usage(sender, "/<command> player/server/item/world/data [-h]");
				
				return false;
				
			}
			
		}
		
		if (args[0].equalsIgnoreCase("player")) {
			this.sendPlayerDebug(sender, flagged, false, other);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("bot")) {
			
			if (!staff) {
				this.noPermission(sender);
				return false;
			}
			
			this.sendBotDebug(sender, flagged, (FleXBot)other);
			return true;
			
		}
		
		if (args[0].equalsIgnoreCase("memory")) {
			
			if (!staff) {
				this.noPermission(sender);
				return false;
			}
			
			this.sendMemoryDebug(sender);
			return true;
			
		}
		
		if (args[0].equalsIgnoreCase("item")) {
			this.sendItemDebug(sender, flagged, other);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("world")) {
			this.sendWorldDebug(sender, flagged, other);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("server")) {
			
			if (args.length == 2) {
				
				if (staff) {
					
					this.usage(sender, 
							
							"/<command> [-h]",
							"/<command> player/bot/item/world/data <player> [-h]",
							"/<command> server [-h]",
							"/<command> memory");
					
				} else this.usage(sender, "/<command> player/server/item/world/data [-h]");
				
				return false;
				
			}
			
			this.sendNetworkDebug(sender, flagged);
			return true;
			
		}
		
		if (args[0].equalsIgnoreCase("data") || args[0].equalsIgnoreCase("sql") || args[0].equalsIgnoreCase("cloud")) {
			this.sendDataDebug(sender, flagged, player);
			return true;
		}
		
		if (staff) {
			
			this.usage(sender,
					
					"/<command> [-h]",
					"/<command> player/bot/item/world/data <player> [-h]",
					"/<command> server [-h]",
					"/<command> memory");
			
		} else this.usage(sender,  "/<command> player/server/item/world/data [-h]");
		
	    return false;
		
	}
	
	private void sendRef(CommandSender sender) {
		// TODO Log debug ref and debug log somewhere
		sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Reference: " + ChatColor.RESET + "D" + NumUtils.getRng().getInt(0, 9) + NumUtils.getRng().getInt(0, 9) + NumUtils.getRng().getInt(0, 9) + StringUtils.generate(4, false).toUpperCase());
	}
	
	private void sendNetworkDebug(CommandSender sender, boolean silent) {
		
		this.sendRef(sender);
		
		BukkitUtils.runLater(() -> {
			
			if (sender == null || (sender instanceof FleXPlayer && !((FleXPlayer)sender).isOnline()))
				return;

			sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Printing information about the network...");

			if (!silent)
				sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.RED + ChatColor.BOLD + "Hide sensitive information using the " + ChatColor.AQUA + ChatColor.BOLD + "-h" + ChatColor.RED + ChatColor.BOLD + " flag.");
			
			double tps = System.currentTimeMillis();
			
			ServerHandler handle = Fukkit.getServerHandler();
			
			ServerRegion region = handle.getServerRegion();
			
			Server server = Bukkit.getServer();
			
			sender.sendMessage(ChatColor.GRAY + "Network: " + ChatColor.RESET + "(#" + (region.ordinal() + 2) + ") Luminous");
			
			String domain = "flex.gg";
			
			if (sender instanceof FleXPlayer) {
				if (((FleXPlayer)sender).hasMetadata("domain")) {
					if (((FleXPlayer)sender).getMetadata("domain").get(0) != null) {
						domain = ((FleXPlayer)sender).getMetadata("domain").get(0).asString();
					}
				}
			}
			
			String
			serverName = "LuminousMC",
			game = "MINECRAFT_GAME",
			uid = "MCF-" + region + "(" + (region.ordinal()) + ")",
			state = (handle.isLocalHost() ? "LOCAL" : "ONLINE") + "--HOST",
			nativeVer = handle.getServerVersion() + "-" + handle.getServerVersion().getRevision(),
			ver = NAME + ":" + VERSION + ":" + COMMIT + ":" + TRAVERTINE,
			auth = CollectionUtils.toCollection("Dinnerbone,EvilSeph,Wolvereness,sk89q,grum,aikar," + AUTHOR + ",zachbr,electronicboy," + ADDJUNCT).toString(),
			stability = "STABLE,",
			ping = ((int)(System.currentTimeMillis() - tps) / 1000) + ",",
			ticksPerSec = "" + NumUtils.roundToDecimal(((System.currentTimeMillis() - tps) * 20 / 1000) > 20 ? 20 : ((System.currentTimeMillis() - tps) * 20 / 1000), 2),
			ticksAvg = "(" + 20.0 + ", " + 20.0 + ", " + 20.0 + "), " + 20.0,
			ip = (silent || ((FleXPlayer)sender) == null || ((FleXPlayer)sender).isStaff()) ? (server.getIp().length() > 0 ? server.getIp() : "localhost") : ChatColor.MAGIC + "Yea.hGo.odT.ry",
			port = ":" + server.getPort(),
			players = server.getOnlinePlayers().size() + "/" + server.getMaxPlayers();
			
			sender.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.RESET + serverName);
			sender.sendMessage(ChatColor.GRAY + "Game: " + ChatColor.RESET + game);
			sender.sendMessage(ChatColor.GRAY + "Uid: " + ChatColor.RESET + uid);
			sender.sendMessage(ChatColor.GRAY + "State: " + ChatColor.RESET + state);
			sender.sendMessage(ChatColor.GRAY + "Domain: " + ChatColor.RESET + domain);
			sender.sendMessage(ChatColor.GRAY + "Data Driver: " + ChatColor.RESET + handle.getDataDriver().name());
			sender.sendMessage(ChatColor.GRAY + "Default World: " + ChatColor.RESET + (handle.getDefaultWorld() != null ? handle.getDefaultWorld().getName() : null));
			
			try {
				sender.sendMessage(ChatColor.GRAY + "Native Version: " + ChatColor.RESET + nativeVer + " (" + Bukkit.getServer().getVersion() + ")");
			} catch (NoSuchMethodError | Exception e) {
				sender.sendMessage(ChatColor.GRAY + "Native Version: " + ChatColor.RESET + nativeVer);
			}
			
			sender.sendMessage(ChatColor.GRAY + "Proxy Version: " + ChatColor.RESET + ver);
			sender.sendMessage(ChatColor.GRAY + "Author(s): " + ChatColor.RESET + auth);
			sender.sendMessage(ChatColor.GRAY + "Stability, Ping, TPS: " + ChatColor.RESET + CollectionUtils.toCollection(stability + ping + ticksPerSec));
			sender.sendMessage(ChatColor.GRAY + "TPS (1m, 5m, 15m), Average TPS: " + ChatColor.RESET + ticksAvg);
			sender.sendMessage(ChatColor.GRAY + "Server IP: " + ChatColor.RESET + ip + port);
			sender.sendMessage(ChatColor.GRAY + "Players: " + ChatColor.RESET + players);
			
			if (sender instanceof FleXPlayer)
				sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Open chat " + ChatColor.AQUA + "[T]" + ChatColor.GRAY + " to show more information.");
		
		}, 20L, false);
		
	}
	
	private void sendMemoryDebug(CommandSender sender) {
		
		this.sendRef(sender);
		
		BukkitUtils.runLater(() -> {
			
			if (sender == null || (sender instanceof FleXPlayer && !((FleXPlayer)sender).isOnline()))
				return;
			
    		int o = 0;
    		
    		for (FleXPlayer player : Fukkit.getServerHandler().getOnlinePlayersUnsafe())
				o = o + player.getPlayer().getScoreboard().getObjectives().size();
    		
    		int t = 0;
    		
    		for (FleXPlayer player : Fukkit.getServerHandler().getOnlinePlayersUnsafe())
				t = t + player.getPlayer().getScoreboard().getTeams().size();
    		
    		sender.sendMessage(ChatColor.GRAY + "Tasks: " + ChatColor.RESET + FukkitRunnable.getTasks());
    		sender.sendMessage(ChatColor.GRAY + "Teams: " + ChatColor.RESET + t);
    		sender.sendMessage(ChatColor.GRAY + "Objectives: " + ChatColor.RESET + o);
    		sender.sendMessage(ChatColor.GRAY + "Loadouts: " + ChatColor.RESET + Fukkit.getServerHandler().getOnlinePlayersUnsafe().stream().filter(p -> p.getLoadout() != null).count());
    		sender.sendMessage(ChatColor.GRAY + "Channels: " + ChatColor.RESET + net.md_5.fungee.Memory.CHANNEL_CACHE.size());
    		sender.sendMessage(ChatColor.GRAY + "Commands: " + ChatColor.RESET + Memory.COMMAND_CACHE.size());
    		sender.sendMessage(ChatColor.GRAY + "Players/Bots: " + ChatColor.RESET + Memory.PLAYER_CACHE.size() + "/" + Memory.PLAYER_CACHE.stream().filter(p -> p instanceof FleXBot).count());
    		sender.sendMessage(ChatColor.GRAY + "Buttons: " + ChatColor.RESET + Memory.BUTTON_CACHE.size());
    		sender.sendMessage(ChatColor.GRAY + "Themes: " + ChatColor.RESET + Memory.THEME_CACHE.size());
    		sender.sendMessage(ChatColor.GRAY + "Skins: " + ChatColor.RESET + Memory.SKIN_CACHE.size());
    		sender.sendMessage(ChatColor.GRAY + "Badges: " + ChatColor.RESET + Memory.BADGE_CACHE.size());
    		sender.sendMessage(ChatColor.GRAY + "Ranks: " + ChatColor.RESET + Memory.RANK_CACHE.size());
    		sender.sendMessage(ChatColor.GRAY + "Menus: " + ChatColor.RESET + Memory.GUI_CACHE.size());
    		
		}, 20L, false);
		
	}
	
	private void sendWorldDebug(CommandSender sender, boolean silent, FleXPlayer player) {
		
		this.sendRef(sender);
		
		FleXPlayer fp = ((FleXPlayer)sender);
		
		boolean same = fp != null && fp.getUniqueId().equals(player.getUniqueId());
		
		sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Printing information about the world " + (same ? "you are" : player.getDisplayName(fp == null ? Memory.THEME_CACHE.getDefaultTheme() : fp.getTheme(), true) + ChatColor.GRAY + " is") + ChatColor.GRAY + " in...");

		if (!silent)
			sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.RED + ChatColor.BOLD + "Hide sensitive information using the " + ChatColor.AQUA + ChatColor.BOLD + "-h" + ChatColor.RED + ChatColor.BOLD + " flag.");
		
		World world = player.getPlayer().getWorld();
		FleXWorld fw = player.getWorld();
		
		boolean flex = fw != null;
		
		BukkitUtils.runLater(() -> {
			
			if (sender == null || (sender instanceof FleXPlayer && !((FleXPlayer)sender).isOnline()))
				return;
			
			String spawn = world.getSpawnLocation().getBlockX() + "," + world.getSpawnLocation().getBlockY() + "," + world.getSpawnLocation().getBlockZ();
			
			if (flex && fw.getBackupSpawnLocation() != null)
				spawn = spawn + " (Backup: " + fw.getBackupSpawnLocation().getBlockX() + "," + fw.getBackupSpawnLocation().getBlockY() + "," + fw.getBackupSpawnLocation().getBlockZ() + ")";
			
			sender.sendMessage(ChatColor.GRAY + "World: " + ChatColor.RESET + (flex ? "FleX" : "Bukkit"));
			sender.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.RESET + world.getName());
			sender.sendMessage(ChatColor.GRAY + "Uid: " + ChatColor.RESET + world.getUID());
			sender.sendMessage(ChatColor.GRAY + "Spawn: " + ChatColor.RESET + spawn);
			sender.sendMessage(ChatColor.GRAY + "Players: " + ChatColor.RESET + world.getPlayers().size());
			sender.sendMessage(ChatColor.GRAY + "Password: " + ChatColor.RESET + (flex && fw.hasPassword() ? (silent ? ChatColor.MAGIC + ((FleXWorld)player.getWorld()).getPassword() : "") : "n/a"));
			sender.sendMessage(ChatColor.GRAY + "Private, Whitelist, Joinable: " + ChatColor.RESET + CollectionUtils.toCollection((flex && fw.hasPassword()) + "," + (flex && fw.hasWhitelist()) + "," + (!flex || fw.isJoinable())));
			
		}, 20L, false);
		
	}
	
	private void sendPlayerDebug(CommandSender sender, boolean silent, boolean showUsage, FleXPlayer player) {
		
		this.sendRef(sender);
		
		FleXPlayer fp = (FleXPlayer) sender;
		
		boolean same = fp != null && fp.getUniqueId().equals(player.getUniqueId());
		
		/**
		 * 
		 *  DE-GHOST ATTEMPT
		 *  
		 *  This will update the server of the players location and trigger
		 *  the PlayerTeleportEvent in Fukkit that updates player visibility.
		 * 
		 */
		Location loc = player.getLocation().clone();
		
		loc.setY(loc.getY() + 1);
		
		player.teleport(loc);
		
		if (!same)
			sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.WHITE + "You have attempted to de-ghost " + (player.getDisplayName(fp == null ? Memory.THEME_CACHE.getDefaultTheme() : fp.getTheme(), true)) + ".");
			
		player.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.WHITE + "You have been de-ghosted" + (same ? "." : " by " + (fp != null ? fp.getDisplayName(player.getTheme()) : "the server")) + ".");
		
		if (same && showUsage) {

			boolean admin = sender instanceof FleXPlayer == false || player.isStaff();
			
			sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.RED + "NOTICE: " + ChatColor.GRAY + "The debug tool can provide other useful information.");
			sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.AQUA + "Usage: " + ("/<command> player" + (admin ? "/bot/" : "/") + "item/world/data" + (admin ? " <player> " : " ") + "[-h]" + (admin ? ", /<command> server [-h]" : "")));
			
		}
		
		if (!silent)
			sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.RED + ChatColor.BOLD + "Hide sensitive information using the " + ChatColor.AQUA + ChatColor.BOLD + "-h" + ChatColor.RED + ChatColor.BOLD + " flag.");
		
		sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Printing information about " + (same ? "you" : player.getDisplayName(fp == null ? Memory.THEME_CACHE.getDefaultTheme() : fp.getTheme(), true)) + "...");
		
		BukkitUtils.runLater(() -> {
			
			if (sender == null || (sender instanceof FleXPlayer && !((FleXPlayer)sender).isOnline()))
				return;
			
			int id = player.getEntity() != null && player.getEntity().isValid() ? player.getEntity().getEntityId() : -1;
			
			String name = player.getEntity() != null && player.getEntity().isValid() ? player.getPlayer().getName() : player.getName();
			
			String ip = "0.0.0.0";
			
			try {
				ip = player.getIp();
			} catch (FleXPlayerNotLoadedException ignore) {}
			
			sender.sendMessage(ChatColor.GRAY + "User: " + ChatColor.RESET + "(#" + (/*(CraftFleXPlayer)*/id)/*.id*/ + ") " + name);
			sender.sendMessage(ChatColor.GRAY + "Logged, Display: " + ChatColor.RESET + CollectionUtils.toCollection(player.getName() + "," + player.getDisplayName()));
			sender.sendMessage(ChatColor.GRAY + "Play time, Last seen: " + ChatColor.RESET + CollectionUtils.toCollection(player.getPlayTime() + "," + player.getLastSeen()));
			sender.sendMessage(ChatColor.GRAY + "Location: " + ChatColor.RESET + (player.getWorld() instanceof FleXWorld ? "(FleX) " + player.getWorld().getName() : "(Bukkit) " + player.getPlayer().getWorld().getName()) + ", x" + NumUtils.roundToDecimal(player.getLocation().getX(), 2) + ", y" + NumUtils.roundToDecimal(player.getLocation().getY(), 2) + ", z" + NumUtils.roundToDecimal(player.getLocation().getZ(), 2) + ", (p" + NumUtils.roundToDecimal(player.getLocation().getPitch(), 2) + ", y" + NumUtils.roundToDecimal(player.getLocation().getYaw(), 2) + ")");
			sender.sendMessage(ChatColor.GRAY + "Signature: " + ChatColor.RESET + (silent ? ChatColor.MAGIC : "") + player.getSkin().getSignature().substring(0, 8));
			sender.sendMessage(ChatColor.GRAY + "State, Ping: " + ChatColor.RESET + CollectionUtils.toCollection(player.getState() + "," + player.getPing()));
			sender.sendMessage(ChatColor.GRAY + "Visibility: " + ChatColor.RESET + player.getVisibility().name());
			sender.sendMessage(ChatColor.GRAY + "Currency: " + ChatColor.RESET + player.getCurrency());
			sender.sendMessage(ChatColor.GRAY + "Version: " + ChatColor.RESET + player.getVersion() + " (" + player.getVersion().toRecommendedProtocol() + ")");
			sender.sendMessage(ChatColor.GRAY + "Rank: " + ChatColor.RESET + player.getRank().getName());
			sender.sendMessage(ChatColor.GRAY + "Theme: " + ChatColor.RESET + player.getTheme().getName());
			sender.sendMessage(ChatColor.GRAY + "Skin: " + ChatColor.RESET + player.getSkin().getValue().substring(0, 6));
			sender.sendMessage(ChatColor.GRAY + "IP: " + ChatColor.RESET + (silent ? ChatColor.MAGIC : "") + ip);
			sender.sendMessage(ChatColor.GRAY + "Sidebar, Disguised, Airborne: " + ChatColor.RESET + CollectionUtils.toCollection((player.getSidebar() != null) + "," + player.isDisguised() + "," + !player.isOnGround()));

			if (sender instanceof FleXPlayer)
				sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Open chat " + ChatColor.AQUA + "[T]" + ChatColor.GRAY + " to show more information.");
			
		}, 20L, false);
		
	}
	
	private void sendBotDebug(CommandSender sender, boolean silent, FleXBot bot) {
		
		this.sendRef(sender);
		
		FleXPlayer fp = (FleXPlayer) sender;
		
		if (!silent)
			sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.RED + ChatColor.BOLD + "Hide sensitive information using the " + ChatColor.AQUA + ChatColor.BOLD + "-h" + ChatColor.RED + ChatColor.BOLD + " flag.");
		
		sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Printing information about " + bot.getDisplayName(fp == null ? Memory.THEME_CACHE.getDefaultTheme() : fp.getTheme(), true) + ChatColor.GRAY + "...");
		
		BukkitUtils.runLater(() -> {
			
			if (sender == null || (sender instanceof FleXPlayer && !((FleXPlayer)sender).isOnline()))
				return;
			
			int id = bot.getEntity() != null && bot.getEntity().isValid() ? bot.getEntity().getEntityId() : -1;
			
			String name = bot.getEntity() != null && bot.getEntity().isValid() ? bot.getPlayer().getName() : bot.getName();
			
			sender.sendMessage(ChatColor.GRAY + "User: " + ChatColor.RESET + "(#" + (/*(CraftFleXPlayer)*/id)/*.id*/ + ") " + name);
			sender.sendMessage(ChatColor.GRAY + "Logged, Display: " + ChatColor.RESET + CollectionUtils.toCollection(bot.getName() + "," + bot.getDisplayName()));
			sender.sendMessage(ChatColor.GRAY + "State, Behaviour: " + ChatColor.RESET + CollectionUtils.toCollection(bot.getState() + "," + bot.getBehaviour()));
			sender.sendMessage(ChatColor.GRAY + "Visibility: " + ChatColor.RESET + bot.getVisibility().name());
			sender.sendMessage(ChatColor.GRAY + "Task: " + ChatColor.RESET + (bot.getAI().getTask() != null ? bot.getAI().getTask().getClass().getSimpleName() : "None"));
			sender.sendMessage(ChatColor.GRAY + "Rank: " + ChatColor.RESET + bot.getRank().getName());
			sender.sendMessage(ChatColor.GRAY + "Skin: " + ChatColor.RESET + bot.getSkin().getValue().substring(0, 6));
			sender.sendMessage(ChatColor.GRAY + "Airborne: " + ChatColor.RESET + !bot.isOnGround());

			if (sender instanceof FleXPlayer || sender instanceof FleXPlayer)
				sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Open chat " + ChatColor.AQUA + "[T]" + ChatColor.GRAY + " to show more information.");
			
		}, 20L, false);
		
	}
	
	private void sendDataDebug(CommandSender sender, boolean silent, FleXPlayer player) {
		
		this.sendRef(sender);
		
		sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Printing information about your cloud data...");
		
		if (!silent)
			sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.RED + ChatColor.BOLD + "Hide sensitive information using the " + ChatColor.AQUA + ChatColor.BOLD + "-h" + ChatColor.RED + ChatColor.BOLD + " flag.");
		
		BukkitUtils.runLater(() -> {
			
			/**
			 * TODO:
			 * Add cloud rows to map async so no server lag then send to player if map isn't empty, if it is empty fail the next task.
			 */
			
		}, true);
		
		BukkitUtils.runLater(() -> {
			
			if (sender == null || (sender instanceof FleXPlayer && !((Player)sender).isOnline()))
				return;
			
			/**
			 * TODO READ ABOVE
			 */
			sender.sendMessage(ChatColor.GRAY + "Uploaded: " + ChatColor.RESET + "Yes");
			sender.sendMessage(ChatColor.RED + "This task is " + ChatColor.DARK_RED + ChatColor.STRIKETHROUGH + "depricated" + ChatColor.RED + ".");

			if (sender instanceof FleXPlayer || sender instanceof FleXPlayer)
				sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Open chat " + ChatColor.AQUA + "[T]" + ChatColor.GRAY + " to show more information.");
			
		}, 20L, false);
		
	}
	
	private void sendItemDebug(CommandSender sender, boolean silent, FleXPlayer player) {
		
		this.sendRef(sender);
		
		FleXPlayer fp = ((FleXPlayer)sender);
		
		boolean same = fp != null && fp.getUniqueId().equals(player.getUniqueId());
		boolean v1_9 = Fukkit.getServerHandler().getServerVersion().ordinal() > ServerVersion.v1_8_R3.ordinal();
		
		sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Printing information about the item" + (v1_9 ? "s " : " ") + (same ? "you are" : player.getDisplayName(fp == null ? Memory.THEME_CACHE.getDefaultTheme() : fp.getTheme(), true) + ChatColor.GRAY + " is") + ChatColor.GRAY + " holding...");

		if (!silent)
			sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.RED + ChatColor.BOLD + "Hide sensitive information using the " + ChatColor.AQUA + ChatColor.BOLD + "-h" + ChatColor.RED + ChatColor.BOLD + " flag.");
		
		int slot = player.getPlayer().getInventory().getHeldItemSlot() + 1;
		
		ItemStack item = v1_9 ? player.getPlayer().getInventory().getItemInMainHand() : player.getPlayer().getInventory().getItemInHand();
		
		String serialized = ItemUtils.serialize(item);
		
		BukkitUtils.runLater(() -> {

			if (sender == null || (sender instanceof FleXPlayer && !((FleXPlayer)sender).isOnline()))
				return;
			
			if (v1_9)
				sender.sendMessage(ChatColor.GRAY + "Main Hand:");
				
			sender.sendMessage(ChatColor.GRAY + "Slot: " + ChatColor.RESET + slot);
			sender.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.RESET + (item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "None" : "Item metadata not available."));
			sender.sendMessage(ChatColor.GRAY + "Lore: " + ChatColor.RESET + (item.hasItemMeta() ? item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : "None" : "Item metadata not available."));
			sender.sendMessage(ChatColor.GRAY + "Base64: " + ChatColor.RESET + serialized.substring(serialized.length() - 12, serialized.length()));
			sender.sendMessage(ChatColor.GRAY + "Button: " + ChatColor.RESET + (Memory.BUTTON_CACHE.getByItem(item) != null));
			
			if (v1_9) {

				ItemStack item2 = player.getPlayer().getInventory().getItemInOffHand();
				
				sender.sendMessage(ChatColor.GRAY + "Off Hand:");
				sender.sendMessage(ChatColor.GRAY + "Slot: " + ChatColor.RESET + slot);
				sender.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.RESET + (item2.hasItemMeta() ? item2.getItemMeta().hasDisplayName() ? item2.getItemMeta().getDisplayName() : "None" : "Item metadata not available."));
				sender.sendMessage(ChatColor.GRAY + "Lore: " + ChatColor.RESET + (item2.hasItemMeta() ? item2.getItemMeta().hasLore() ? item2.getItemMeta().getLore() : "None" : "Item metadata not available."));
				sender.sendMessage(ChatColor.GRAY + "Base64: " + ChatColor.RESET + serialized.substring(serialized.length() - 12, serialized.length()));
				sender.sendMessage(ChatColor.GRAY + "Button: " + ChatColor.RESET + (Memory.BUTTON_CACHE.getByItem(item2) != null));
				
			}

			if (sender instanceof FleXPlayer)
				sender.sendMessage(ChatColor.DARK_AQUA + "[Debug] " + ChatColor.GRAY + "Open chat " + ChatColor.AQUA + "[T]" + ChatColor.GRAY + " to show more information.");
			
		}, 20L, false);
		
	}

}
