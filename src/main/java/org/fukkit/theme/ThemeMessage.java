package org.fukkit.theme;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.fukkit.Memory;

import io.flex.FleX.Task;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;
import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.StringUtils;

public enum ThemeMessage {
	
	// TODO: This is a concept, idea hasn't been fully thought out.
	/*
	 * 
	 * ThemeMessage rehaul:
	 * 
	 * ThemeMessage(String key, String... variables)
	 * 
	 * Example:
	 * 
	 * CHAT_FORMAT("Message.Chat.Format.Default", "%name%", "%player%", "%display%", "%rank%", "%message%")
	 * 
	 * Maybe an AbstractThemeMessage ?
	 * 
	 * public static final ChatFormatThemeMessage CHAT_FORMAT;
	 * 
	 * CHAT_FORMAT.format(FleXPlayer player, String message)
	 * 
	 */
	
	SERVER_CONNECTING("Message.Server.Connecting"),
	SERVER_CONNECTING_FALLBACK("Message.Server.Fallback.Connecting"),
	
	SERVER_NOT_FOUND("Message.Server.Not-Found"),
	SERVER_NOT_FALLBACK("Message.Server.Fallback.Not-Fallback"),
	
	ERROR_TRY_AGAIN("Message.Error.Try-Again"),
	ERROR_TRY_AGAIN_LATER("Message.Error.Try-Again-Later"),
	ERROR_COMING_SOON("Message.Error.Coming-Soon"),

	CHAT_FORMAT("Message.Chat.Format.Default"),
	CHAT_FORMAT_HOVER("Message.Chat.Format.Hover"),
	CHAT_FORMAT_STAFF("Message.Chat.Format.Staff.Default"),
	CHAT_FORMAT_STAFF_DISGUISED("Message.Chat.Format.Staff.Disguised"),
	
	CHAT_MENTION_PREFIX("Message.Chat.Mention.Prefix"),
	CHAT_MENTION_HIGHLIGHT("Message.Chat.Mention.Highlight"),
	
	CHAT_DENIED_DELAY("Message.Chat.Denied.Delay"),
	CHAT_DENIED_DISABLED("Message.Chat.Denied.Disabled"),

	COMMAND_PLAYER_NOT_FOUND("Message.Command.Player.Not-Found"),
	COMMAND_PLAYER_NOT_ONLINE("Message.Command.Player.Not-Online"),
	
	COMMAND_HELP_USAGE("Message.Command.Help.Usage"),
	COMMAND_HELP_INVALID("Message.Command.Help.Invalid"),
	COMMAND_HELP_NOT_FOUND("Message.Command.Help.Not-Found"),
	COMMAND_HELP_PAGE_TITLE("Message.Command.Help.Page.Title"),
	COMMAND_HELP_PAGE_LINE("Message.Command.Help.Page.Line"),
    
    COMMAND_HELP("Message.Command.Help.Suggest"),

	COMMAND_DENIED("Message.Command.Denied.Other"),
	COMMAND_DENIED_COOLDOWN("Message.Command.Denied.Cooldown"),
	COMMAND_DENIED_PERMISSION("Message.Command.Denied.Permission"),
	COMMAND_DENIED_COMING_SOON("Message.Command.Denied.Coming-Soon"),
	
	COMMAND_DENIED_STATE_LOBBY("Message.Command.Denied.Lobby"),
	COMMAND_DENIED_STATE_INGAME("Message.Command.Denied.Ingame"),
	COMMAND_DENIED_STATE_SPECTATOR("Message.Command.Denied.Spectator"),
	COMMAND_DENIED_STATE_CONSOLE("Message.Command.Denied.Console"),
	COMMAND_DENIED_STATE_HUB_ONLY("Message.Command.Denied.Hub-Only"),

	PING_SHOW("Message.Command.Ping.Millisecond.Self"),
	PING_SHOW_OTHER("Message.Command.Ping.Millisecond.Other"),

	PING_CONNECTION("Message.Command.Ping.Connection.Self"),
	PING_CONNECTION_OTHER("Message.Command.Ping.Connection.Other"),

	REPORT_LOADING("Message.Command.Report.Loading"),
	REPORT_SUCCESS("Message.Command.Report.Success"),

	REPORT_FAILURE_SELF("Message.Command.Report.Failure.Self"),
	REPORT_FAILURE_DENIED("Message.Command.Report.Failure.Denied"),

	REPORTS_REMINDER("Message.Command.Report.Reports.Reminder"),
	REPORTS_LOADING("Message.Command.Report.Reports.Loading"),
	REPORTS_VIEW("Message.Command.Report.Reports.View"),
	REPORTS_NONE("Message.Command.Report.Reports.None"),
	
	FLOW_RECORDING_STARTED("Message.Command.Report.Flow.Success"),
	FLOW_RECORDING_INTERRUPTED("Message.Command.Report.Flow.Failure"),

	FLOW_REPORTS_REMINDER("Message.Command.Report.Flow.Reports.Reminder"),
	FLOW_REPORTS_LOADING("Message.Command.Report.Flow.Reports.Loading"),
	FLOW_REPORTS_VIEW("Message.Command.Report.Flow.Reports.View"),
	FLOW_REPORTS_NONE("Message.Command.Report.Flow.Reports.None"),

	DISGUISE_SUCCESS("Message.Command.Disguise.Success.Self"),
	DISGUISE_SUCCESS_OTHER("Message.Command.Disguise.Success.Other"),
	DISGUISE_PREDISGUISE("Message.Command.Disguise.Predisguise"),

	DISGUISE_FAILURE_ERROR("Message.Command.Disguise.Failure.Error"),
	DISGUISE_FAILURE_DISABLED("Message.Command.Disguise.Failure.Disabled"),

	UNDISGUISE_SUCCESS("Message.Command.Disguise.Undisguise.Success.Self"),
	UNDISGUISE_SUCCESS_OTHER("Message.Command.Disguise.Undisguise.Success.Other"),
	
	UNDISGUISE_FAILURE("Message.Command.Disguise.Undisguise.Failure.Self"),
	UNDISGUISE_FAILURE_OTHER("Message.Command.Disguise.Undisguise.Failure.Other"),
	UNDISGUISE_FAILURE_ERROR("Message.Command.Disguise.Undisguise.Failure.Error"),

	MASK_LIST("Message.Command.Mask.List"),
	MASK_SUCCESS("Message.Command.Mask.Success"),
	
	MASK_FAILURE_NOT_FOUND("Message.Command.Mask.Failure.Not-Found"),
	MASK_FAILURE_DENIED("Message.Command.Mask.Failure.Denied"),
	
	UNMASK_SUCCESS("Message.Command.Mask.Unmask.Success.Self"),
	UNMASK_SUCCESS_OTHER("Message.Command.Mask.Unmask.Success.Other"),
	
	UNMASK_FAILURE("Message.Command.Mask.Unmask.Failure.Self"),
	UNMASK_FAILURE_OTHER("Message.Command.Mask.Unmask.Failure.Other"),
	
	FLIGHT_SUCCESS("Message.Command.Flight.Success.Self"),
	FLIGHT_SUCCESS_OTHER("Message.Command.Flight.Success.Other"),
	
	FLIGHT_FAILURE("Message.Command.Flight.Failure.Self"),
	FLIGHT_FAILURE_OTHER("Message.Command.Flight.Failure.Other"),

	TELEPORT_SUCCESS("Message.Command.Teleport.Success.Self"),
	TELEPORT_SUCCESS_OTHER("Message.Command.Teleport.Success.Other"),

	TELEPORT_FAILURE("Message.Command.Teleport.Failure.Self"),
	TELEPORT_FAILURE_OTHER("Message.Command.Teleport.Failure.Other"),
	TELEPORT_FAILURE_DENIED("Message.Command.Teleport.Failure.Denied"),
	
	GAMEMODE_SUCCESS("Message.Command.Gamemode.Success.Self"),
	GAMEMODE_SUCCESS_OTHER("Message.Command.Gamemode.Success.Other"),

	GAMEMODE_FAILURE("Message.Command.Gamemode.Failure.Self"),
	GAMEMODE_FAILURE_OTHER("Message.Command.Gamemode.Failure.Other"),
	GAMEMODE_FAILURE_NOT_FOUND("Message.Command.Gamemode.Failure.Not-Found"),
	
	PUNISHMENT_SUCCESS("Message.Command.Punishment.Success"),
	PUNISHMENT_FAILURE_SELF("Message.Command.Punishment.Failure.Self"),
	PUNISHMENT_FAILURE_DENIED("Message.Command.Punishment.Failure.Denied"),

	BROADCAST_CONFIRM_VIEW("Message.Command.Broadcast.Confirm.View"),
	BROADCAST_CONFIRM_DENIED("Message.Command.Broadcast.Confirm.Denied"),

	BROADCAST_DISMISS_SUCCESS("Message.Command.Broadcast.Dismiss.Success"),
	BROADCAST_DISMISS_DENIED("Message.Command.Broadcast.Dismiss.Denied"),
	
	BROADCAST_MESSAGE("Message.Command.Broadcast.Message"),
	BROADCAST_SUCCESS("Message.Command.Broadcast.Success"),
	BROADCAST_FAILURE("Message.Command.Broadcast.Failure"),
	
	RANK_SUCCESS("Message.Command.Rank.Success.Self"),
	RANK_SUCCESS_OTHER("Message.Command.Rank.Success.Other"),
	
	RANK_FAILURE("Message.Command.Rank.Failure.Self"),
	RANK_FAILURE_OTHER("Message.Command.Rank.Failure.Other"),
	RANK_FAILURE_NOT_FOUND("Message.Command.Rank.Failure.Not-Found"),
	
	LANGUAGE_SELECT_SUCCESS("Message.Command.Language.Success"),
	LANGUAGE_FAILURE_NOT_FOUND("Message.Command.Language.Failure.Not-Found"),
	
	THEME_SELECT_SUCCESS("Message.Command.Theme.Success"),
	THEME_FAILURE_NOT_FOUND("Message.Command.Theme.Failure.Not-Found"),
	THEME_MESSAGE_NOT_FOUND("&4&l[THEME ERROR: &7&l(DEFAULT=en_US) &4&lMESSAGE NOT FOUND]");
	
	private String key;
	
	private ThemeMessage(String key) {
		this.key = key;
	}
	
	public String[] format(Variable<?>... variables) {
		return this.format(Memory.THEME_CACHE.stream().findFirst().orElse(null), variables);
	}
	
	@Deprecated
	public String[] format(Language language, Variable<?>... variables) {
		return this.format(Memory.THEME_CACHE.stream().findFirst().orElse(null), language, variables);
	}
	
	@Deprecated
	public String[] format(Object... objects) {
		return this.format(Memory.THEME_CACHE.stream().findFirst().orElse(null), objects);
	}
	
	public String[] format(Theme theme, Variable<?>... variables) {
		return this.format(theme, Language.ENGLISH, variables);
	}
	
	public String[] format(Theme theme, Language language, Variable<?>... variables) {
		
		String tagged = theme.format(theme.getMessage(language, this) != null ? theme.getMessage(language, this) : THEME_MESSAGE_NOT_FOUND.key);
		
		if (theme.getMessage(this) == null) {
			
			NullPointerException exception = new NullPointerException(
					"Theme message " + this + "(key: " + this.key + ") could not be found.");
			
			Task.debug("Theme", exception.getMessage());
			Console.log("Theme", Severity.ALERT, exception);
			
		}
		
		Date now = new Date();
		SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy");
		SimpleDateFormat time = new SimpleDateFormat("hh:mm a z");
		
		if (variables == null)
			variables = new Variable<?>[0];
		
		variables = ArrayUtils.add(variables, new Variable<String>("%time%", ChatColor.UNDERLINE + time.format(now) + ChatColor.getLastColors(tagged)));
		variables = ArrayUtils.add(variables, new Variable<String>("%date%", ChatColor.UNDERLINE + date.format(now) + ChatColor.getLastColors(tagged)));
		
		for (Variable<?> variable : variables) {
			if (variable.value() != null) {
				tagged = tagged.replace("!" + variable.variable(), StringUtils.capitalize(variable.value().toString()));
				tagged = tagged.replace(variable.variable(), variable.value().toString());
			}
		}
		
		return tagged.split("\\\\n");
		
	}

	@Deprecated
	public String[] format(Theme theme, Object... objects) {
		
		String tagged = theme.format(String.format(theme.getMessage(this) != null ?
				theme.getMessage(this) : THEME_MESSAGE_NOT_FOUND.key, objects));
		
		return tagged.split("\\\\n");
		
	}

	@Deprecated
	public static String[] getTaggedValues(String string, String tag) {
		
		List<String> values = new ArrayList<String>();
        Matcher matcher = Pattern.compile("<" + tag + ">(.+?)</" + tag + ">").matcher(string);
        
        while (matcher.find())
        	values.add(matcher.group(1));
        
        return values.toArray(new String[values.size()]);
        
    }
	
	@Deprecated
	public static String[] getVariables(String string) {
		
		List<String> values = new ArrayList<String>();
        Matcher matcher = Pattern.compile("%(.+?)%").matcher(string);
        
        while (matcher.find())
        	values.add(matcher.group(1));
        
        return values.toArray(new String[values.size()]);
        
    }
	
	public String getKey() {
		return this.key;
	}
	
	public String[] stripTags() {
		String[] message = this.format();
		IntStream.range(0, message.length).forEach(i -> message[i] = ChatColor.stripColor(Memory.THEME_CACHE.getDefaultTheme().format(message[i])));
		return message;
	}
	
	public static String[] stripTags(String[] message) {
		IntStream.range(0, message.length).forEach(i -> message[i] = ChatColor.stripColor(Memory.THEME_CACHE.getDefaultTheme().format(message[i])));
		return message;
	}
	
	public static ThemeMessage valueOfKey(String key) {
		return Arrays.stream(ThemeMessage.values()).filter(m -> m.key.equals(key)).findFirst().orElse(null);
	}
	
}