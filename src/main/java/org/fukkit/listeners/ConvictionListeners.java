package org.fukkit.listeners;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.PlayerState;
import org.fukkit.consequence.Ban;
import org.fukkit.consequence.Punishment;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.consequence.EvidenceType;
import org.fukkit.consequence.Kick;
import org.fukkit.consequence.Mute;
import org.fukkit.consequence.Consequence;
import org.fukkit.consequence.Report;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.consequence.FleXBanEvent;
import org.fukkit.event.consequence.FleXConvictEvent;
import org.fukkit.event.consequence.FleXKickEvent;
import org.fukkit.event.consequence.FleXMuteEvent;
import org.fukkit.event.consequence.FleXPreConsequenceEvent;
import org.fukkit.event.consequence.FleXReportEvent;
import org.fukkit.event.player.PlayerChangeStateEvent;
import org.fukkit.handlers.FlowLineEnforcementHandler;
import org.fukkit.metadata.FleXFixedMetadataValue;
import org.fukkit.recording.Recording;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.BukkitUtils;
import io.flex.commons.cache.cell.BiCell;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.FileUtils;
import io.flex.commons.utils.NumUtils;

import net.md_5.fungee.event.FleXFinalizeEvent;
import net.md_5.fungee.event.FleXPlayerAsyncChatEvent;

public class ConvictionListeners extends FleXEventListener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void event(FleXPreConsequenceEvent event) {
		
		if (event.isCancelled())
			return;
		
		Consequence consequence = event.getPreConsequence();
		
		FleXPlayer player = consequence.getPlayer();
		FleXPlayer by = consequence.getBy();
		
		PunishmentType type = consequence.getType();
		
		if ((type == PunishmentType.REPORT || type == PunishmentType.KICK) && !player.isOnline())
			return;
		
		if (type == PunishmentType.REPORT) {
			
			FleXReportEvent report = new FleXReportEvent(new Report(player, by, consequence.getReason()), false);
			
			Fukkit.getEventFactory().call(report);
			
			if (report.isCancelled())
				return;
			
		} else {
			
			String log;
			
			try {
				log = flowEvidence(player, consequence);
			} catch (FleXPlayerNotLoadedException e) {
				e.printStackTrace();
				return;
			}
			
			by.setMetadata("input_evidence", new FleXFixedMetadataValue(new BiCell<Consequence, String>() {
				
				private static final long serialVersionUID = -5368438625243730572L;

				@Override
				public Consequence a() {
					return consequence;
				}
				
				@Override
				public String b() {
					return log;
				}
				
			}));
			
			Theme theme = player.getTheme();
			
			by.sendMessage(theme.format("<engine><success>Insert the evidence for this punishment into chat<pp>."));
			
			if (log != null)
				by.sendMessage(theme.format("<flow>&7&oEvidence available<sp>," + Theme.reset + " &7&otype &8\"&dAuto&8\" &7&oto reference it<pp>."));
			
			by.sendMessage(theme.format("<engine><pc>Enter <sp>\"<sc>None<sp>\"" + Theme.reset + " <pc>if this is Administration approved<pp>."));
			by.sendMessage(theme.format("<engine><pc>Insert <sp>\"<sc>Temp<sp>\"" + Theme.reset + " <pc>if you need time to process the evidence<pp>."));
			by.sendMessage(theme.format("<engine><pc>Simply type <sp>\"<sc>Cancel<sp>\"" + Theme.reset + " <pc>if you have made a mistake<pp>."));
			
		}
		
    }

	@EventHandler
	@SuppressWarnings("unchecked")
    public void event(AsyncPlayerChatEvent event) {
		
		if (event.isCancelled())
			return;
		
		Player player = event.getPlayer();
		
		if (!player.hasMetadata("input_evidence"))
			return;
		
		FleXFixedMetadataValue metaData = (FleXFixedMetadataValue) player.getMetadata("input_evidence").get(0);
		BiCell<Consequence, String> cell = (BiCell<Consequence, String>) metaData.value();
		
		Consequence consequence = cell.a();
		
		FleXPlayer punished = consequence.getPlayer();
		FleXPlayer by = consequence.getBy();
		
		boolean auto = event.getMessage().equalsIgnoreCase("Auto");
		boolean none = event.getMessage().equalsIgnoreCase("None");
		boolean temp = event.getMessage().equalsIgnoreCase("Temp");
		boolean cancel = event.getMessage().equalsIgnoreCase("Cancel");
		
		event.setCancelled(true);
		
		Theme theme = by.getTheme();
		
		if (auto && cell.b() == null) {
			by.sendMessage(theme.format("<engine><failure>No evidence to reference, please enter it manually<pp>."));
			return;
		}
		
		String message = event.getMessage();
		
		if ((!auto && !none && !temp && !cancel && EvidenceType.parse(message) == null) || message.contains(" ")) {
			by.sendMessage(theme.format("<engine><failure>That is not a valid link, reference or alternative<pp>."));
			return;
		}
		
		FleXConvictEvent convict = null;
		
		by.removeMetadata("input_evidence", Fukkit.getInstance());
		
		if (consequence.getType() == PunishmentType.KICK && !punished.isOnline()) {
			by.sendMessage(theme.format("<engine><failure>Player went offline<sp>.\\s<failure>Punishment cancelled<pp>."));
			return;
		}
		
		if (cancel) {
			by.sendMessage(theme.format("<engine><success>Punishment cancelled<pp>."));
			return;
		}
		
		String processing = consequence.getType() == PunishmentType.KICK ? EvidenceType.PROCESSING : EvidenceType.REDUCED;
		String evidence = none ? EvidenceType.NOT_APPLICABLE : auto ? cell.b() + " (Automatic)" : temp ? processing : message;
		
		switch (consequence.getType()) {
		
		case BAN:
			
			convict = new FleXBanEvent(new Ban(punished, by, consequence.getReason(), consequence.isIp(), consequence.isSilent(), evidence), false);
			break;
			
		case KICK:
			
			convict = new FleXKickEvent(new Kick(punished, by, consequence.getReason(), consequence.isSilent(), evidence), false);
			break;
			
		case MUTE:
			
			convict = new FleXMuteEvent(new Mute(punished, by, consequence.getReason(), consequence.isIp(), consequence.isSilent(), evidence), false);
			break;
			
		default:
			
			by.sendMessage(ThemeMessage.ERROR_TRY_AGAIN_LATER.format(by.getTheme(), by.getLanguage()));
			break;
			
		}
		
		if (convict != null) {
			
			FleXConvictEvent parse = convict;
			
			BukkitUtils.runLater(() -> {
				
				Fukkit.getEventFactory().call(parse);
				
				if (parse.isCancelled())
					return;
				
				String success = auto ? "Passed with automated evidence" : "Punishment passed successfully";
				String reduced = parse.getConviction().getType() == PunishmentType.KICK ? success : "Passed with a reduced duration until evidence is finalized";
				String accepted = "Evidence accepted";
				
				by.sendMessage(theme.format("<engine><success>" + (none ? success : temp ? reduced : accepted) + "<pp>."));
				
			});
			
		}
		
    }
	
	/**
	 * Not sure whats going on here, look over and see what i was trying to achieve.
	 */
	@Deprecated
	private static String flowEvidence(FleXPlayer player, Consequence consequence) throws FleXPlayerNotLoadedException {
		
		EvidenceType[] required = consequence.getReason().getRequiredEvidence();
		
		if (required == null)
			return null;
		
		if (Arrays.stream(required).anyMatch(e -> e.isChatType())) {
			
			long logged = player.getHistory().getChatAndCommands().asMap().entrySet().stream().filter(e -> {
				return e.getValue().contains(":") && e.getKey() >= (System.currentTimeMillis() - (NumUtils.MINUTE_TO_MILLIS * 10));
				
			}).map(e -> e.getKey()).findFirst().orElse(-1L);
			
			if (logged != -1L)
				return "flow-" + FileUtils.getTimeStamp(logged) + ".chat";
			
		}
		/* TODO
		if (Arrays.stream(required).anyMatch(e -> e.isPhysicalType())) {
			
			long logged = player.getHistory().getFlowRecords().asMap().entrySet().stream().filter(e -> {
				return e.getKey() >= (System.currentTimeMillis() - (NumUtils.MINUTE_TO_MILLIS * 10));
				
			}).map(e -> e.getKey()).findFirst().orElse(-1L);
			
			if (logged != -1L)
				return "flow-" + FileUtils.getTimeStamp(logged) + ".rec";
			
		}
		*/
		return null;
				
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(FleXConvictEvent event) {
		
		if (event.isCancelled())
			return;
		
		Punishment conviction = event.getConviction();
		
		conviction.onConvict();
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void event(FleXPlayerAsyncChatEvent event) {
		
		if (event.isCancelled())
			return;
		
		if (!event.getPlayer().isMuted())
			return;
		
		event.setCancelled(true);
		
		event.getPlayer().getMute().onBypassAttempt();
		
	}
	
	private static boolean allow = false;
	
	@EventHandler
	public void event(FleXFinalizeEvent event)  {
		allow = true;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void event(AsyncPlayerPreLoginEvent event) {
		
		if (!allow) {
		
			event.setKickMessage(
					
					ChatColor.DARK_RED + "" + ChatColor.BOLD + "FleXPlayer signature not found" + ChatColor.DARK_GRAY + ChatColor.BOLD + ".\n" +
					ChatColor.WHITE + "FleX is still loading from a scheduled restart, Surefire is stopping all login attempts" + ChatColor.DARK_GRAY + ".\n"
					+ "\n" +
					ChatColor.GRAY + "If this persists please contact a staff member" + ChatColor.DARK_GRAY + ".");
			
			event.setLoginResult(Result.KICK_OTHER);
			
		} else {
			
			FleXPlayer player = Fukkit.getPlayer(event.getUniqueId());
			
			if (player == null)
				return;
			
			if (!player.isBanned())
				return;
			
			player.getBan().onPreBypassAttempt(player, event);
			
		}
		
		FleXHumanEntity fp = Memory.PLAYER_CACHE.getFromCache(event.getUniqueId());
		
		if (fp != null)
			Memory.PLAYER_CACHE.remove(fp);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerJoinEvent event) {
		
		FleXPlayer player = Fukkit.getPlayerExact(event.getPlayer());
		
		if (player == null)
			return;
		
		if (player.isBanned())
			player.getBan().onBypassAttempt();
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerChangeStateEvent event) {
		
		if (!event.isAsynchronous())
			return;
		
		FleXPlayer player = event.getPlayer();

		FlowLineEnforcementHandler fle = Fukkit.getFlowLineEnforcementHandler();
		
		if (event.getTo() == PlayerState.INGAME) {
			
			if (fle.isPending(player)) {
				
				// TODO local cache of reports so this doesn't contact database every time player state changes.
				Set<Report> reports;
				
				try {
					reports = Report.download(player);
				} catch (SQLException e) {
					
					e.printStackTrace();
					return;
					
				}
				
				if (!reports.isEmpty()) {
					
					boolean alreadyRecording = Fukkit.getFlowLineEnforcementHandler().isRecording(player);
					
					for (Report report : reports) {
						
						if (ArrayUtils.contains(report.getReason().getRequiredEvidence(), EvidenceType.RECORDING_REFERENCE)) {
							
							if (!alreadyRecording) {
								
								System.out.println("Starting recording............................ 1");
								
								Fukkit.getFlowLineEnforcementHandler().setRecording(player, true);
								
								new Recording() {
									
									@Override
									public void onPlayerDisconnect(FleXPlayer player) {
										// TODO Auto-generated method stub
										
									}
									
								};
								
								// This ensures that there are never multiple of the same recording for the same suspect.
								alreadyRecording = true;
								
							}
							
							// Still send recording started message.
							report.onBypassAttempt();
							
						}
						
					}
				}
				
			}
			
		}
		
	}

}
