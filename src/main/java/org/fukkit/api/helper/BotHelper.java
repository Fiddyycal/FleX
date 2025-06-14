package org.fukkit.api.helper;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

public class BotHelper {

	public static final String CONFIRM_SUCCESS = "Ok, no problem. %s";
	public static final String CONFIRM_FAIL = "I havn't asked you to confirm anything silly.";
	
	public static final String HELP_SUCCESS = "Happy to help!";
	
	public static final String RUDE = "Well, that was rude. ò_ó";
	public static final String APOLOGY = "I'm very sorry... ಠ_ಠ";
	public static final String AWKWARD = "Well, this is awkward...";
	
	public static final String BYE = "Bye %s!";
	public static final String CYA = "See you soon %s!";
	
	public static final String GREAT = "Great!";
	public static final String AWESOME = "Awesome!";
	public static final String GOOD_TALK = "Good talk.";
	
	public static final Map<CommandSender, String> META_DATA = new HashMap<CommandSender, String>();

}
