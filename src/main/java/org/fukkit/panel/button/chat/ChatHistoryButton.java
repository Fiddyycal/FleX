package org.fukkit.panel.button.chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.VersionUtils;

import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;

public class ChatHistoryButton extends ExecutableButton {
	
	private static final long serialVersionUID = 2984971389617295534L;
	
	private FleXPlayer other;
	
	public ChatHistoryButton(Theme theme, Language language, FleXPlayer other) {
		
		super(VersionUtils.material("BOOK_AND_QUILL", "WRITABLE_BOOK"), theme.format("<title>History<sp>:\\s<pc>Messages<sp>/<pc>Connections"), lore(theme, language, other));
		
		this.other = other;
		
	}
	
	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action) {
		
		if (!action.isClick())
			return false;
		
		if (action.isLeftClick()) {
			
			player.closeMenu();
			
			player.openMenu(new ChatHistoryShowcase(player, this.other), false);
			
		}
		
		return true;
		
	}
	
	private static String[] lore(Theme theme, Language language, FleXPlayer other) {
		
		List<String> lore = new ArrayList<String>();
		List<String> latest = last5(theme, language, other);
		
		lore.add(theme.format("<lore>View <sc>" + other.getDisplayName(theme, true).replace(ChatColor.RESET.toString(), ChatColor.WHITE.toString()) + "<lore>'s chat and connection history<pp>."));
		
		lore.add("");
		lore.add(theme.format("<tc>Chat<sp>/<tc>Connection history logged and provided by <spc>FloW<pp>."));
		
		lore.add("");
		lore.add(theme.format("<spc>Recent chat history<pp>:"));
		
		if (!latest.isEmpty())
			lore.addAll(latest);
			
		else lore.add(theme.format("<failure>No recent chat history found<pp>."));
		
		lore.add("");
		lore.add(theme.format("<sp>&oMiddle Click<pp>:\\s<sc>Show all<pp>."));
		lore.add(theme.format("<sp>&oLeft Click<pp>:\\s<sc>Show chat messages<pp>."));
		lore.add(theme.format("<sp>&oRight Click<pp>:\\s<sc>Show connections<pp>."));
		
		return lore.toArray(new String[lore.size()]);
		
	}
	
	private static List<String> last5(Theme theme, Language language, FleXPlayer other) {
		
		Map<Long, String> chats = null;
		
		try {
			chats = other.getHistory().getChatAndCommands().asMap();
		} catch (FleXPlayerNotLoadedException e) {
			
			e.printStackTrace();
			
			return new ArrayList<String>();
			
		}
		
		List<Long> times = chats.keySet().stream().sorted().collect(Collectors.toList());
		
		int size = times.size() - 5;
		
		if (size < 0)
			size = 0;
		
		List<Long> latest = times.subList(size, times.size());
		
		List<String> last5 = new LinkedList<String>();
		
		if (latest.isEmpty())
			return last5;
		
		SimpleDateFormat format = new SimpleDateFormat("(dd/MM/yy) [hh:mm:ss]");
		
		for (int i = 0; i < latest.size(); i++) {
			
			long time = latest.get(i);
			
			String dateTime = format.format(new Date(time))
					
			        .replace("(", "<pp>(<tc>")
					.replace(")", "<pp>)" + Theme.reset)
					
					.replace("[", "<pp>[<spc>")
					.replace("]", "<pp>]" + Theme.reset);
			
			Variable<?>[] variables = {

					new Variable<String>("%rank%", other.getRank().getDisplay(theme, true)),
					new Variable<String>("%role%", other.getRank().getDisplay(theme, false)),
					new Variable<String>("%player%", other.getDisplayName(theme, true)),
					new Variable<String>("%name%", other.getName())
					
			};
			
			String[] lines = ThemeMessage.CHAT_FORMAT.format(theme, language, ArrayUtils.add(variables, new Variable<String>("%message%", ChatColor.WHITE + chats.get(time))));

			Arrays.stream(lines).forEach(l -> last5.add(theme.format(dateTime + " " + l)));
			
		}
		
		return last5;
		
	}

}
