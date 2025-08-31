package org.fukkit.command;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.PlayerState;
import org.fukkit.api.helper.EventHelper;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotFoundException;
import org.fukkit.event.command.FleXCommandPerformEvent;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.ThemeUtils;

import com.google.common.collect.ImmutableList;

import io.flex.FleX.Task;
import io.flex.commons.cache.Cacheable;
import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.ClassUtils;
import io.flex.commons.utils.NumUtils;
import io.flex.commons.utils.StringUtils;

public abstract class FleXCommandAdapter extends BukkitCommand implements Cacheable {
	
	protected String command;
	
	private String[] flags, usage, complete;
	
	private long delay;
	
	private boolean unknown, console, global, tab;
	
	protected FleXCommandAdapter() {
		
		super(null);
		
		Command commandAnn = ClassUtils.getSuperAnnotation(this.getClass(), Command.class);
		
		if (commandAnn == null)
			Task.try_(() -> new FleXCommandNotFoundException());
		
		try {
			this.setName(commandAnn.name());
		} catch (NoSuchMethodError e) {
			
			try {
				
				Field field = org.bukkit.command.Command.class.getDeclaredField("name");
				
				field.setAccessible(true);
				
				field.set(this, commandAnn.name());
				
				field.setAccessible(false);
				
				
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
			
		}
		
		this.setLabel(commandAnn.name());
		
		if (ClassUtils.getSuperAnnotation(this.getClass(), UnknownCommand.class) != null)
			return;
		
		CooldownCommand cooldownAnn = ClassUtils.getSuperAnnotation(this.getClass(), CooldownCommand.class);
		
		if (cooldownAnn != null && cooldownAnn.delay() > 0)
			
			switch (cooldownAnn.timeUnit()) {

			case SECONDS:
				
				this.delay = cooldownAnn.delay();
				break;
				
			case MINUTES:
				
				this.delay = cooldownAnn.delay() * 60;
				break;

			case HOURS:
				
				this.delay = cooldownAnn.delay() * 3600;
				break;

			case DAYS:
				
				this.delay = cooldownAnn.delay() * 86400;
				break;
			
			default:
				
				this.delay = 0;
				break;
				
			}
		
		RestrictCommand restrictAnn = ClassUtils.getSuperAnnotation(this.getClass(), RestrictCommand.class);
		
		if (restrictAnn != null && !restrictAnn.permission().equals(""))
			this.setPermission(restrictAnn.permission().toLowerCase());
		
		this.unknown = ClassUtils.getSuperAnnotation(this.getClass(), UnknownCommand.class) != null;
        this.console = ClassUtils.getSuperAnnotation(this.getClass(), ConsoleCommand.class) != null;
        this.global = ClassUtils.getSuperAnnotation(this.getClass(), GlobalCommand.class) != null;
        
        TabComplete tabAnn = ClassUtils.getSuperAnnotation(this.getClass(), TabComplete.class);
        this.tab = tabAnn != null ? tabAnn.ignore() : false;
		
		FlaggedCommand flaggedAnn = ClassUtils.getSuperAnnotation(this.getClass(), FlaggedCommand.class);
        this.flags = flaggedAnn != null ? new String[flaggedAnn.flags().length] : null;
        
        IntStream.range(0, this.flags != null ? this.flags.length : 0).forEach(i -> {
        	this.flags[i] = flaggedAnn.flags()[i].startsWith("-") ? flaggedAnn.flags()[i] : "-" + flaggedAnn.flags()[i];
        });
        
        this.usage = commandAnn.usage() != null && commandAnn.usage().length != 0 ? commandAnn.usage() : new String[]{ "/<command>" };
        
        this.setUsage(this.usage[0]);
        
        this.complete = tabAnn != null ? tabAnn.complete() != null ? tabAnn.complete() : new String[0] : null;
        
        this.setDescription(commandAnn.description());
        
        List<String> aliasesList = this.getAliases();
        aliasesList.addAll(Arrays.asList(commandAnn.aliases()));
        
        if (!aliasesList.isEmpty())
        	this.setAliases(aliasesList);
        
        Fukkit.getCommandFactory().register(this);
        
        Memory.COMMAND_CACHE.add(this);
        
	}
	
	public boolean canUse(CommandSender sender) {
		
		if (sender instanceof ConsoleCommandSender) {
			
			if (!this.console)
				return false;
			
		} else if (sender instanceof Player == false && sender instanceof FleXPlayer == false)
			return false;
		
		SoonCommand soonAnn = ClassUtils.getSuperAnnotation(this.getClass(), SoonCommand.class);
		
		if (soonAnn != null)
			return false;
		
		FleXPlayer player = sender instanceof Player ? Fukkit.getPlayer(((Player)sender).getUniqueId()) : null;
		
		if (player != null) {
			
			RestrictCommand restrictAnn = ClassUtils.getSuperAnnotation(this.getClass(), RestrictCommand.class);
			
			if (restrictAnn != null) {
				
				if (restrictAnn.disallow() != null && restrictAnn.disallow().length > 0) {
					
					PlayerState state = player.getState();
					
					for (PlayerState disallow : restrictAnn.disallow()) {
						
						if (state == disallow)
							return false;
						
					}
					
				}
				
				if (restrictAnn.permission() != null && !restrictAnn.permission().equalsIgnoreCase("")) {
					
					if (!player.hasPermission(restrictAnn.permission()))
						return false;
					
				}
				
			}
			
		}
		
		if (!this.global) {
			
			// TODO check if command is able to be run on server/world, if it is not able to be run on world then run below
			// return false;
			
		}
		
		return true;
		
	}
	
	public boolean execute(CommandSender sender, String command, String[] args) {
		
		String fukkit = Fukkit.getInstance().getName().toLowerCase() + ":";
		String plugin = Fukkit.getPlugin().getName().toLowerCase() + ":";

		boolean fukkitPre = command.toLowerCase().startsWith(fukkit);
		boolean pluginPre = command.toLowerCase().startsWith(plugin);

		this.command = fukkitPre ? command.substring(fukkit.length()) : pluginPre ? command.substring(plugin.length()) : command;
		
		String[] flags = new String[0];
		
		if (this.unknown) {
			this.unknownCommand(sender);
			return true;
		}
		
		if (!this.global) {
			
			// TODO check if command is able to be run on server/world, if it is not able to be run on world then run below
			// this.unknownCommand();
			// return;
			
		}
		
		SoonCommand soonAnn = ClassUtils.getSuperAnnotation(this.getClass(), SoonCommand.class);
		
		if (soonAnn != null) {
			
			sender.sendMessage(soonAnn.todo() != null && !soonAnn.todo().equals("") ?
					
					ThemeMessage.ERROR_COMING_SOON.format(Memory.THEME_CACHE.getDefaultTheme(), Language.ENGLISH, new Variable<String>("%coming_soon%", soonAnn.todo())) :
					ThemeMessage.COMMAND_DENIED_COMING_SOON.format(Memory.THEME_CACHE.getDefaultTheme(), Language.ENGLISH, new Variable<String>("%command%", this.getName()))
					
			);
			
			return true;
			
		}
		
		if (this.flags != null) {
			for (String arg : args) {
				if (arg.startsWith("-")) {
					
					if (ArrayUtils.contains(this.flags, arg)) {
						
						flags = ArrayUtils.add(flags, arg);
						args = ArrayUtils.remove(args, arg);
						
					} else {
						
						this.incompatible(sender, arg);
						return true;
						
					}
					
				}
			}
		}
		
		if (sender instanceof ConsoleCommandSender) {
			if (!this.console) {
				sender.sendMessage(ThemeMessage.COMMAND_DENIED_STATE_CONSOLE.format(Memory.THEME_CACHE.stream().findFirst().get(), Language.ENGLISH, new Variable<String>("%command%", this.getName())));
				return true;
			} else {
				this.perform(sender, args, flags);
				return true;
			}
		}
		
		if (sender instanceof Player == false && sender instanceof FleXPlayer == false)
			return false;
		
		Player player = sender instanceof Player ? (Player) sender : null;
		FleXPlayer fp = sender instanceof FleXPlayer ? (FleXPlayer) sender : Fukkit.getPlayerExact(player);
		
		if (fp == null)
			throw new FleXPlayerNotFoundException("There was a problem loading the FleX player wrapper. Cannot use the FleXCommandAdapter until this is addressed.");
		
		if (this.getPermission() != null && !fp.hasPermission(this.getPermission())) {
			this.noPermission(fp);
			return true;
		}
		
		RestrictCommand restrictAnn = ClassUtils.getSuperAnnotation(this.getClass(), RestrictCommand.class);
		
		if (restrictAnn != null && restrictAnn.disallow() != null && restrictAnn.disallow().length > 0) {
			
			PlayerState state = fp.getState();
			
			for (PlayerState disallow : restrictAnn.disallow()) {
				
				if (state == disallow) {
					
					this.cantUse(fp, disallow);
					return true;
					
				}
				
				if (state == PlayerState.CONNECTING || state == PlayerState.DISCONNECTING)
					return true;
				
			}
			
		}
		
		long LastUsed = 0;
		long cdmillis = this.delay * 1000;
		
		UUID uuid = fp.getUniqueId();
		
		if (CooldownCommand.cooldowns_cache.containsKey(uuid))
			LastUsed = CooldownCommand.cooldowns_cache.get(uuid);
		
		if ((System.currentTimeMillis() - LastUsed) >= cdmillis) {
			
			FleXCommandPerformEvent event = new FleXCommandPerformEvent(this, fp.getPlayer(), args, flags, false);
			
			EventHelper.callEvent(event);
			
			if (event.isCancelled())
				return true;
			
			event.setPerformed(this.perform(fp, args, flags));
			
			if (!event.isPerformed())
				return true;
			
		    if (this.delay != 0)
		    	CooldownCommand.cooldowns_cache.put(uuid, System.currentTimeMillis());
		    
		    return true;
		
		} else {
			
			int timeLeft = (int) (this.delay - ((System.currentTimeMillis() - LastUsed) / 1000));
			
			this.cantUse(fp, timeLeft);
			return true;
			
		}
		
	}
	
	public String[] getFlags() {
		return this.flags;
	}
	
	public String[] getUses() {
		return this.usage;
	}
	
	public long getDelay() {
		return this.delay;
	}
	
	public void playerNotFound(CommandSender sender, String notFound) {
		
		FleXPlayer player = sender instanceof FleXPlayer ? (FleXPlayer)sender : null;
		
		if (player == null && sender instanceof FleXPlayer)
			player = Fukkit.getPlayer(((Player)sender).getUniqueId());
		
		Theme theme = player != null ? player.getTheme() : Memory.THEME_CACHE.getDefaultTheme();
		
		Language lang = player != null ? player.getLanguage() : Language.ENGLISH;
		
		Variable<String> variable = new Variable<String>("%player%", notFound);
		
		sender.sendMessage(ThemeMessage.COMMAND_PLAYER_NOT_FOUND.format(theme, lang, variable));
		
	}
	
	public void playerNotOnline(CommandSender sender, FleXPlayer offline) {
		
		FleXPlayer player = sender instanceof FleXPlayer ? (FleXPlayer)sender : null;
		
		if (player == null && sender instanceof FleXPlayer)
			player = Fukkit.getPlayer(((Player)sender).getUniqueId());
		
		Theme theme = player != null ? player.getTheme() : Memory.THEME_CACHE.getDefaultTheme();
		Language lang = player != null ? player.getLanguage() : Language.ENGLISH;
		
		Variable<?>[] variable = ThemeUtils.getNameVariables(player, theme);
		
		sender.sendMessage(ThemeMessage.COMMAND_PLAYER_NOT_ONLINE.format(theme, lang, variable));
		
	}
	
	public void playerNotAccessible(CommandSender sender, FleXPlayer inaccessible) {
		
		FleXPlayer player = sender instanceof FleXPlayer ? (FleXPlayer)sender : null;
		
		if (player == null && sender instanceof FleXPlayer)
			player = Fukkit.getPlayer(((Player)sender).getUniqueId());
		
		Theme theme = player != null ? player.getTheme() : Memory.THEME_CACHE.getDefaultTheme();
		
		Language lang = player != null ? player.getLanguage() : Language.ENGLISH;
		
		Variable<?>[] variable = ThemeUtils.getNameVariables(player, theme);

		// TODO: COMMAND_PLAYER_NOT_ACCESSIBLE ("is not in your server")
		sender.sendMessage(ThemeMessage.COMMAND_PLAYER_NOT_ONLINE.format(theme, lang, variable));
		
	}
	
	public void noPermission(CommandSender sender) {
		
		FleXPlayer player = sender instanceof FleXPlayer ? (FleXPlayer)sender : null;
		
		if (player == null && sender instanceof FleXPlayer)
			player = Fukkit.getPlayer(((Player)sender).getUniqueId());
		
		Theme theme = player != null ? player.getTheme() : Memory.THEME_CACHE.getDefaultTheme();
		
		Language lang = player != null ? player.getLanguage() : Language.ENGLISH;
		
		Variable<String> variable = new Variable<String>("%command%", this.getName());
		
		sender.sendMessage(ThemeMessage.COMMAND_DENIED_PERMISSION.format(theme, lang, variable));
		
	}
	
	public void unknownCommand(CommandSender sender) {
		sender.sendMessage("Unknown command. Type \"/help\" for help.");
	}
	
	public void cantUse(CommandSender sender, double timeLeft) {
		
		FleXPlayer player = sender instanceof FleXPlayer ? (FleXPlayer)sender : null;
		
		if (player == null && sender instanceof FleXPlayer)
			player = Fukkit.getPlayer(((Player)sender).getUniqueId());
		
		Theme theme = player != null ? player.getTheme() : Memory.THEME_CACHE.getDefaultTheme();
		
		Language lang = player != null ? player.getLanguage() : Language.ENGLISH;
		
		boolean isMin = timeLeft > 60;
		
		double time = isMin ? timeLeft / 60 : timeLeft;
		
		if (isMin)
			time = NumUtils.roundToDecimal(time, 2);
		
		sender.sendMessage(ThemeMessage.COMMAND_DENIED_COOLDOWN.format(theme, lang,
				
				isMin ? new Variable<Double>("%cooldown%", (double) time) :
				
				new Variable<Integer>("%cooldown%", (int) time),
				new Variable<String>("%unit%", (isMin ? "minute" : "second" + ((int)time < 2 ? "" : "s")
				
		))));
		
	}
	
	public void usage(CommandSender sender, String... usage) {
		
		FleXPlayer player = sender instanceof FleXPlayer ? (FleXPlayer)sender : null;
		
		if (player == null && sender instanceof FleXPlayer)
			player = Fukkit.getPlayer(((Player)sender).getUniqueId());
		
		Theme theme = player != null ? player.getTheme() : Memory.THEME_CACHE.getDefaultTheme();
		
		Language lang = player != null ? player.getLanguage() : Language.ENGLISH;
		
		if (usage.length > 1) {
			
			String[] message = ThemeMessage.COMMAND_HELP_USAGE.format(theme, lang,
					
					new Variable<String>("%command%", this.getName()),
					new Variable<String>("%description%", this.getDescription()),
					new Variable<String>("%usage%", "\n" + StringUtils.join(usage, "\n")),
					new Variable<String>("%permission%", this.getPermission() != null ? this.getPermission() : "n/a")
					
			);
			
			IntStream.range(0, message.length).forEach(i -> message[i] = message[i].replace("<command>", this.getName()));
			
			player.sendMessage(message);
			
		} else {
			
			String[] message = ThemeMessage.COMMAND_HELP_USAGE.format(theme, lang,
					
					new Variable<String>("%command%", this.getName()),
					new Variable<String>("%description%", this.getDescription()),
					new Variable<String>("%usage%", usage[0]),
					new Variable<String>("%permission%", this.getPermission() != null ? this.getPermission() : "n/a")
					
			);
			
			IntStream.range(0, message.length).forEach(i -> message[i] = message[i].replace("<command>", this.getName()));
			
			player.sendMessage(message);
			
		}
		
	}
	
	public void cantUse(FleXPlayer player, PlayerState state) {
		
		Theme theme = player.getTheme();
		
		Language lang = player.getLanguage();
		
		String[] message = ThemeMessage.COMMAND_DENIED.format(player.getTheme(), player.getLanguage(),
				new Variable<String>("%command%", this.getName()));
		
		switch (state) {
		case INLOBBY:
			message = ThemeMessage.COMMAND_DENIED_STATE_LOBBY.format(theme, lang,
					new Variable<String>("%command%", this.getName()));
			break;
		case INGAME_PVE_ONLY:
			message = ThemeMessage.COMMAND_DENIED_STATE_INGAME.format(theme, lang,
					new Variable<String>("%command%", this.getName()));
			break;
		case INGAME:
			message = ThemeMessage.COMMAND_DENIED_STATE_INGAME.format(theme, lang,
					new Variable<String>("%command%", this.getName()));
			break;
		case SPECTATING:
			message = ThemeMessage.COMMAND_DENIED_STATE_SPECTATOR.format(theme, lang,
					new Variable<String>("%command%", this.getName()));
			break;
		default:
			message = ThemeMessage.COMMAND_DENIED_STATE_HUB_ONLY.format(theme, lang,
					new Variable<String>("%command%", this.getName()));
			break;
		}
		
		player.sendMessage(message);
		
	}

	public void usage(CommandSender sender) {
		this.usage(sender, this.usage);
	}

	public void invalid(CommandSender sender, String... usage) {

		FleXPlayer player = sender instanceof FleXPlayer ? (FleXPlayer)sender : null;
		
		if (player == null && sender instanceof FleXPlayer)
			player = Fukkit.getPlayer(((Player)sender).getUniqueId());
		
		Theme theme = player != null ? player.getTheme() : Memory.THEME_CACHE.getDefaultTheme();
		
		Language lang = player != null ? player.getLanguage() : Language.ENGLISH;
		
		if (usage.length > 1) {

			String[] message = ThemeMessage.COMMAND_HELP_INVALID.format(theme, lang,
					
					new Variable<String>("%command%", this.getName()),
					new Variable<String>("%description%", this.getDescription()),
					new Variable<String>("%usage%", "\n" + StringUtils.join(usage, "\n")),
					new Variable<String>("%permission%", this.getPermission() != null ? this.getPermission() : "n/a")
					
			);
			
			IntStream.range(0, message.length).forEach(i -> message[i] = message[i].replace("<command>", this.getName()));
			
			sender.sendMessage(message);
			
		} else {
			
			String[] message = ThemeMessage.COMMAND_HELP_INVALID.format(theme, lang,
					
					new Variable<String>("%command%", this.getName()),
					new Variable<String>("%description%", this.getDescription()),
					new Variable<String>("%usage%", usage[0]),
					new Variable<String>("%permission%", this.getPermission() != null ? this.getPermission() : "n/a")
					
			);
			
			IntStream.range(0, message.length).forEach(i -> message[i] = message[i].replace("<command>", this.getName()));
			
			sender.sendMessage(message);
			
		}
		
	}
	
	public void invalid(CommandSender sender) {
		this.invalid(sender, this.usage);
	}
	
	public void incompatible(CommandSender sender, String flag) {

		FleXPlayer player = sender instanceof FleXPlayer ? (FleXPlayer)sender : null;
		
		if (player == null && sender instanceof FleXPlayer)
			player = Fukkit.getPlayer(((Player)sender).getUniqueId());
		
		Theme theme = player != null ? player.getTheme() : Memory.THEME_CACHE.getDefaultTheme();
		
		//Language lang = player != null ? player.getLanguage() : Language.ENGLISH;
		
		if (sender instanceof ConsoleCommandSender)
			sender.sendMessage("The flag " + flag + " is not compatible with this command.");
		
		else {
			// TODO Incompatible flag theme message.
			sender.sendMessage(theme.format("<engine><failure>The flag <sc>" + flag + "<failure> is not compatible with this command<pp>."));
		}
		
	}
	
	public void unregister() {
        Fukkit.getCommandFactory().unregister(this);
	}
	
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {

		if (this.unknown || this.tab)
			return ImmutableList.of();
		
		if (this.complete != null && this.complete.length == 0)
			return super.tabComplete(sender, alias, args);
		
		if (sender == null)
			throw new NullPointerException("Sender cannot be null");
		
		if (args == null)
			throw new NullPointerException("Arguments cannot be null");
		
		if (alias == null)
			throw new NullPointerException("Alias cannot be null");
        
        if (args.length == 1) {
        	
            String toComplete = args[0].toLowerCase();
            
            Stream<String> completions = this.complete != null ? Arrays.stream(this.complete) :
            	Fukkit.getServerHandler().getOnlinePlayersUnsafe().stream().map(p -> p.isDisguised() ? p.getDisguise().getName() : p.getName());
            
            return completions.filter(s -> s.toLowerCase().startsWith(toComplete)).collect(Collectors.toList());
            
        }
        
        return ImmutableList.of();
        
    }
	
	public boolean isConsoleCommand() {
		return this.console;
	}
	
	public boolean isUnknown() {
		return this.unknown;
	}
	
	public abstract boolean perform(CommandSender sender, String[] args, String[] flags);

}
