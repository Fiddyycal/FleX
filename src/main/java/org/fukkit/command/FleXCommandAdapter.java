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
import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.ClassUtils;
import io.flex.commons.utils.NumUtils;
import io.flex.commons.utils.StringUtils;

public abstract class FleXCommandAdapter extends BukkitCommand implements FleXCommand {
	
	protected String command;
	
	private String[] flags, usage, complete;
	
	private long delay;
	
	private boolean unknown, console, global, tab;
	
	private CommandSender sender;
	
	private FleXPlayer fleXPlayer;
	
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
	
	public boolean execute(CommandSender sender, String command, String[] args) {
		
		String fukkit = Fukkit.getInstance().getName().toLowerCase() + ":";
		String plugin = Fukkit.getPlugin().getName().toLowerCase() + ":";

		boolean fukkitPre = command.toLowerCase().startsWith(fukkit);
		boolean pluginPre = command.toLowerCase().startsWith(plugin);

		this.command = fukkitPre ? command.substring(fukkit.length()) : pluginPre ? command.substring(plugin.length()) : command;
		
		String[] flags = new String[0];
		
		this.sender = sender;
		
		this.fleXPlayer = sender instanceof Player ? Fukkit.getPlayerExact((Player)sender) : null;
		
		boolean exists = this.global; // Add conditions here.
		
		if (!this.console && (!exists || !command.equals(this.command))) {
			
			if (this.global && !this.unknown)
				return true;
			
			this.unknownCommand();
			return true;
			
		}

		SoonCommand soonAnn = ClassUtils.getSuperAnnotation(this.getClass(), SoonCommand.class);
		
		if (soonAnn != null) {
			
			sender.sendMessage(soonAnn.todo() != null && !soonAnn.todo().equals("") ?
					
					ThemeMessage.ERROR_COMING_SOON.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(), new Variable<String>("%coming_soon%", soonAnn.todo())) :
					ThemeMessage.COMMAND_DENIED_COMING_SOON.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(), new Variable<String>("%command%", this.getName()))
					
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
						
						this.incompatible(arg);
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
				this.perform(args, flags);
				return true;
			}
		}
		
		Player player = (Player) sender;
		
		this.fleXPlayer = Fukkit.getPlayerExact(player);
		
		if (this.fleXPlayer == null)
			throw new FleXPlayerNotFoundException("There was a problem loading your FleX player wrapper. You cannot use the FleXCommandAdapter until this is addressed.");
		
		if (this.getPermission() != null && !this.fleXPlayer.hasPermission(this.getPermission())) {
			this.noPermission();
			return true;
		}
		
		RestrictCommand restrictAnn = ClassUtils.getSuperAnnotation(this.getClass(), RestrictCommand.class);
		if (restrictAnn != null && restrictAnn.disallow() != null && restrictAnn.disallow().length > 0) {
			
			PlayerState state = this.fleXPlayer.getState();
			
			for (PlayerState disallow : restrictAnn.disallow()) {
				
				if (state == disallow) {
					
					this.cantUse(disallow);
					return true;
					
				}
				
				if (state == PlayerState.CONNECTING || state == PlayerState.DISCONNECTING)
					return true;
				
			}
			
		}
		
		long LastUsed = 0;
		long cdmillis = this.delay * 1000;
		
		UUID uuid = this.fleXPlayer.getUniqueId();
		
		if (CooldownCommand.cooldowns_cache.containsKey(uuid))
			LastUsed = CooldownCommand.cooldowns_cache.get(uuid);
		
		if (System.currentTimeMillis()-LastUsed>=cdmillis) {
			
			FleXCommandPerformEvent event = new FleXCommandPerformEvent(this, player, args, flags, false);
			
			EventHelper.callEvent(event);
			
			if (event.isCancelled())
				return true;
			
			event.setPerformed(this.perform(args, flags));
			
			if (!event.isPerformed())
				return true;
			
		    if (this.delay != 0)
		    	CooldownCommand.cooldowns_cache.put(uuid, System.currentTimeMillis());
		    
		    return true;
		
		} else {
			
			int timeLeft = (int) (this.delay-((System.currentTimeMillis()-LastUsed)/1000));
			this.cantUse(timeLeft);
			return true;
			
		}
		
	}
	
	@Override
	public String[] getFlags() {
		return this.flags;
	}
	
	@Override
	public String[] getUses() {
		return this.usage;
	}

	@Override
	public long getDelay() {
		return this.delay;
	}

	@Override
	public FleXPlayer getPlayer() {
		return this.fleXPlayer;
	}

	@Override
	public CommandSender getSender() {
		return this.sender;
	}
	
	@Override
	public void playerNotFound(String name) {
		
		Variable<String> variable = new Variable<String>("%player%", name);
		
		if (this.getSender() instanceof ConsoleCommandSender)
			this.getSender().sendMessage(ThemeMessage.stripTags(ThemeMessage.COMMAND_PLAYER_NOT_FOUND.format(variable)));
			
		else this.fleXPlayer.sendMessage(ThemeMessage.COMMAND_PLAYER_NOT_FOUND.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(), variable));
		
	}

	@Override
	public void playerNotOnline(FleXPlayer player) {
		
		Theme theme = this.getSender() instanceof ConsoleCommandSender ? Memory.THEME_CACHE.stream().findFirst().get() : this.getPlayer().getTheme();
		Variable<?>[] variable = ThemeUtils.getNameVariables(player, theme);
		
		if (this.getSender() instanceof ConsoleCommandSender)
			this.getSender().sendMessage(ThemeMessage.stripTags(ThemeMessage.COMMAND_PLAYER_NOT_ONLINE.format(variable)));
			
		else this.fleXPlayer.sendMessage(ThemeMessage.COMMAND_PLAYER_NOT_ONLINE.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(), variable));
		
	}

	@Override
	public void playerNotAccessible(FleXPlayer player) {
		
		// TODO: COMMAND_PLAYER_NOT_ACCESSIBLE ("is not in your server")
		Theme theme = this.getSender() instanceof ConsoleCommandSender ? Memory.THEME_CACHE.stream().findFirst().get() : this.getPlayer().getTheme();
		Variable<?>[] variable = ThemeUtils.getNameVariables(player, theme);
		
		if (this.getSender() instanceof ConsoleCommandSender)
			this.getSender().sendMessage(ThemeMessage.stripTags(ThemeMessage.COMMAND_PLAYER_NOT_ONLINE.format(variable)));
			
		else this.fleXPlayer.sendMessage(ThemeMessage.COMMAND_PLAYER_NOT_ONLINE.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(), variable));
		
	}

	@Override
	public void noPermission() {
		
		Variable<String> variable = new Variable<String>("%command%", this.getName());
		
		if (this.getSender() instanceof ConsoleCommandSender)
			this.getSender().sendMessage(ThemeMessage.stripTags(ThemeMessage.COMMAND_DENIED_PERMISSION.format(variable)));
			
		else this.fleXPlayer.sendMessage(ThemeMessage.COMMAND_DENIED_PERMISSION.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(), variable));
		
	}

	@Override
	public void unknownCommand() {
		this.getSender().sendMessage("Unknown command. Type \"/help\" for help.");
	}

	@Override
	public void cantUse(double timeLeft) {
		
		boolean isMin = timeLeft > 60;
		double time = isMin ? timeLeft / 60 : timeLeft;
		if (isMin) time = NumUtils.roundToDecimal(time, 2);
		
		this.fleXPlayer.sendMessage(ThemeMessage.COMMAND_DENIED_COOLDOWN.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(),
				
				isMin ? new Variable<Double>("%cooldown%", (double) time) :
				
				new Variable<Integer>("%cooldown%", (int) time),
				new Variable<String>("%unit%", (isMin ? "minute" : "second" + ((int)time < 2 ? "" : "s")
				
		))));
		
	}

	@Override
	public void cantUse(PlayerState state) {
		
		String[] message = ThemeMessage.COMMAND_DENIED.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(),
				new Variable<String>("%command%", this.getName()));
		
		switch (state) {
		case INLOBBY:
			message = ThemeMessage.COMMAND_DENIED_STATE_LOBBY.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(),
					new Variable<String>("%command%", this.getName()));
			break;
		case INGAME_PVE_ONLY:
			message = ThemeMessage.COMMAND_DENIED_STATE_INGAME.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(),
					new Variable<String>("%command%", this.getName()));
			break;
		case INGAME:
			message = ThemeMessage.COMMAND_DENIED_STATE_INGAME.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(),
					new Variable<String>("%command%", this.getName()));
			break;
		case SPECTATING:
			message = ThemeMessage.COMMAND_DENIED_STATE_SPECTATOR.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(),
					new Variable<String>("%command%", this.getName()));
			break;
		default:
			message = ThemeMessage.COMMAND_DENIED_STATE_HUB_ONLY.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(),
					new Variable<String>("%command%", this.getName()));
			break;
		}
		
		this.fleXPlayer.sendMessage(message);
		
	}

	@Override
	public void usage(String... usage) {

		if (usage.length > 1) {

			String[] message = ThemeMessage.COMMAND_HELP_USAGE.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(),
					
					new Variable<String>("%command%", this.getName()),
					new Variable<String>("%description%", this.getDescription()),
					new Variable<String>("%usage%", "\n" + StringUtils.join(usage, "\n")),
					new Variable<String>("%permission%", this.getPermission() != null ? this.getPermission() : "n/a")
					
			);
			
			IntStream.range(0, message.length).forEach(i -> message[i] = message[i].replace("<command>", this.getName()));
			this.fleXPlayer.sendMessage(message);
			
		} else {
			
			String[] message = ThemeMessage.COMMAND_HELP_USAGE.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(),
					
					new Variable<String>("%command%", this.getName()),
					new Variable<String>("%description%", this.getDescription()),
					new Variable<String>("%usage%", usage[0]),
					new Variable<String>("%permission%", this.getPermission() != null ? this.getPermission() : "n/a")
					
			);
			
			IntStream.range(0, message.length).forEach(i -> message[i] = message[i].replace("<command>", this.getName()));
			this.fleXPlayer.sendMessage(message);
			
		}
		
	}

	@Override
	public void usage() {
		this.usage(this.usage);
	}

	@Override
	public void invalid(String... usage) {

		if (usage.length > 1) {

			String[] message = ThemeMessage.COMMAND_HELP_INVALID.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(),
					
					new Variable<String>("%command%", this.getName()),
					new Variable<String>("%description%", this.getDescription()),
					new Variable<String>("%usage%", "\n" + StringUtils.join(usage, "\n")),
					new Variable<String>("%permission%", this.getPermission() != null ? this.getPermission() : "n/a")
					
			);
			
			IntStream.range(0, message.length).forEach(i -> message[i] = message[i].replace("<command>", this.getName()));
			this.fleXPlayer.sendMessage(message);
			
		} else {
			
			String[] message = ThemeMessage.COMMAND_HELP_INVALID.format(this.fleXPlayer.getTheme(), this.fleXPlayer.getLanguage(),
					
					new Variable<String>("%command%", this.getName()),
					new Variable<String>("%description%", this.getDescription()),
					new Variable<String>("%usage%", usage[0]),
					new Variable<String>("%permission%", this.getPermission() != null ? this.getPermission() : "n/a")
					
			);
			
			IntStream.range(0, message.length).forEach(i -> message[i] = message[i].replace("<command>", this.getName()));
			this.fleXPlayer.sendMessage(message);
			
		}
		
	}
	
	@Override
	public void invalid() {
		this.invalid(this.usage);
	}
	
	@Override
	public void incompatible(String flag) {
		
		if (this.getSender() instanceof ConsoleCommandSender)
			this.getSender().sendMessage("The flag " + flag + " is not compatible with this command.");
		
		else {
			// TODO Incompatible flag theme message.
			this.fleXPlayer.sendMessage(this.fleXPlayer.getTheme().format("<engine><failure>The flag <sc>" + flag + "<failure> is not compatible with this command<pp>."));
		}
		
	}
	
	@Override
	public void unregister() {
        Fukkit.getCommandFactory().unregister(this);
	}
	
	@Override
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
	
	@Override
	public boolean isConsoleCommand() {
		return this.console;
	}
	
	public boolean isUnknown() {
		return this.unknown;
	}
	
	@Override
	public abstract boolean perform(String[] args, String[] flags);

}
