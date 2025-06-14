package org.fukkit.command.defaults.admin;

import org.fukkit.entity.FleXPlayer;
import io.flex.commons.utils.StringUtils;

public class BotSubCommand extends AbstractAdminSubCommand {
	
	public BotSubCommand(AdminCommand command) {
		super(command, "bot", "flexbot");
	}

	@Override
	public boolean perform(String[] args, String[] flags) {
		
		if ((args.length != 1 && args.length != 2) || (!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("remove"))) {
			this.command.usage("/<command> bot/flexbot <create/remove> <name> [uuid]");
			return false;
		}
		
		if (args[0].equalsIgnoreCase("remove")) {
			this.command.getPlayer().sendMessage("Hope you didn't need this, it isn't finished yet... whoops. -5Ocal O_O");
			return false;
		}
		
		FleXPlayer fp = this.command.getPlayer();
		String name = StringUtils.shorten(args[1], 0, 16);
		
		//UUID uuid = UUID.randomUUID();
		
		if (args.length == 2) {
			try {
				StringUtils.toUUID(args[1]);
			} catch (IllegalArgumentException e) {
				fp.sendMessage(fp.getTheme().format("<engine><failure>UUID is invalid<pp>."));
				return false;
			}
		}
		/*
		Surfire bridge = Fukkit.getBridgeHandler().getSurfireBridge();
		
		if (!bridge.isAiFound()) {
			fp.sendMessage(theme.format("<engine><failure>Bots are not enabled on this server<pp>.", fp.getTheme()));
			return false;
		}
		
		FleXPlayer bot = bridge.createBot(fp.getLocation(), name, uuid);
		
		bot.setTheme(Memory.THEME_CACHE.stream().findAny().get());
		
		Collection<? extends FleXPlayer> players = Fukkit.getServerHandler().getOnlinePlayersUnsafe();
		
		players.forEach(p -> {
			//bot.setNameTag(new NameTag(bot.getPeripherals(), new TeamEntry(Memory.RANK_CACHE.get("Bot").getDisplay(p.getTheme(), false)), () -> Arrays.asList(p)));
		});
		*/
		fp.sendMessage(fp.getTheme().format("<engine><success>Created and spawned bot<reset> <sc>" + name + "<pp>."));
		return true;
		
	}

}
