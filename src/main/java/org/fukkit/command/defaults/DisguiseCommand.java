package org.fukkit.command.defaults;

import java.util.concurrent.TimeUnit;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.PlayerState;
import org.fukkit.api.helper.MojangHelper;
import org.fukkit.command.Command;
import org.fukkit.command.CooldownCommand;
import org.fukkit.command.FlaggedCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.disguise.Disguise;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.event.player.FleXPlayerDisguiseEvent;
import org.fukkit.event.player.FleXPlayerDisguisedEvent;
import org.fukkit.event.player.FleXPlayerDisguisedEvent.Result;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.ThemeUtils;

import io.flex.FleX.Task;
import io.flex.FleXMissingResourceException;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;
import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;

@GlobalCommand
@FlaggedCommand(flags = { "-f", "-s" })
@CooldownCommand(delay = 20, timeUnit = TimeUnit.SECONDS)
@RestrictCommand(permission = "flex.command.disguise", disallow = { PlayerState.INGAME_PVE_ONLY, PlayerState.INGAME, PlayerState.SPECTATING, PlayerState.UNKNOWN })
@Command(name = "disguise", aliases = { "d", "dis" }, usage = "/<command>")
public class DisguiseCommand extends FleXCommandAdapter {
	
	public boolean perform(String[] args, String[] flags) {
		
		boolean flip = this.getPlayer().hasPermission("flex.command.disguise.flip");
		boolean keep = this.getPlayer().hasPermission("flex.command.disguise.keepskin");
		
		if (args.length != 0 && args.length != 1) {
			
			this.usage((this.getPlayer().hasPermission("flex.command.disguise.custom") ? "/<command> <skin>" : this.getUsage())
					+ (flip || keep ? " [" + (flip ? "-f" : "") + (flip && keep ? ", " : "") + (keep ? "-s" : "") + "]" : ""));
			
			return false;
		}
		
		Theme theme = this.getPlayer().getTheme();
		Language lang = this.getPlayer().getLanguage();
		
		boolean flipped = flip && ArrayUtils.contains(flags, "-f");
		boolean original = keep && ArrayUtils.contains(flags, "-s");
		boolean generate = args.length == 0;
		
		if ((!this.getPlayer().hasPermission("flex.command.disguise.custom") && !generate) || (!flip && flipped) || (!keep && original)) {
			this.noPermission();
			return false;
		}
		
		FleXPlayerDisguiseEvent load = new FleXPlayerDisguiseEvent(this.getPlayer(), null);
		
		Fukkit.getEventFactory().call(load);
		
		if (load.isCancelled())
			return false;
		
		Variable<?>[] refresh = ThemeUtils.getNameVariables(this.getPlayer(), theme);
		Variable<?>[] variables = ArrayUtils.add(refresh,
				
				new Variable<Boolean>("%flipped%", flipped),
				new Variable<Boolean>("%keep_skin%", original));
		
		if (generate)
			this.getPlayer().sendMessage(ThemeMessage.DISGUISE_PREDISGUISE.format(theme, lang, variables));
		
		String name = flipped ? "Grumm" : generate ? Memory.NAME_CACHE.getRandom() : args[0];
		
		FleXSkin skin = null;
		FleXSkin random = null;
		
		FleXPlayerDisguisedEvent loaded = null;
		
		try {
			
			random = generate ? Memory.SKIN_CACHE.getRandom() : null;
			skin = original ? this.getPlayer().getSkin() : generate ? random : MojangHelper.getSkin(args[0]);
			
			if (skin == null)
				throw new NullPointerException("Skin failed to load.");
			
			loaded = new FleXPlayerDisguisedEvent(this.getPlayer(), new Disguise(name, skin), Result.SUCCESS);
			
		} catch (NullPointerException | IllegalArgumentException e) {
			
			e.printStackTrace();
			
			String generated = random != null ? random.getName() : "null";
			
			this.getPlayer().sendMessage(ThemeMessage.DISGUISE_FAILURE_ERROR.format(theme, lang, ArrayUtils.add(variables, new Variable<String>("%skin%", generate ? generated : args[0]))));
			
			FleXMissingResourceException exception = new FleXMissingResourceException("An error occurred loading skin " + (generate ? generated : args[0]) + ".");
			
			Task.debug(exception.getMessage());
			
			Console.log("Disguise", Severity.NOTICE, exception);
			
		}
		
		if (loaded == null)
			loaded = new FleXPlayerDisguisedEvent(this.getPlayer(), null, Result.FAILURE);
		
		Fukkit.getEventFactory().call(loaded);
		
		Disguise disguise = loaded.getDisguise();
		
		if (loaded.getDisguise() == null || loaded.isCancelled())
			return false;
		
		this.getPlayer().setDisguise(disguise);
		
		variables = ArrayUtils.remove(variables, refresh);
		variables = ArrayUtils.add(variables, ThemeUtils.getNameVariables(this.getPlayer(), theme));
		
		this.getPlayer().sendMessage(ThemeMessage.DISGUISE_SUCCESS.format(theme, lang, ArrayUtils.add(variables, new Variable<String>("%skin%", skin.getName()))));
		return true;
		
	}
	
}
