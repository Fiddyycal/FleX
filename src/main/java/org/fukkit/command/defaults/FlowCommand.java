package org.fukkit.command.defaults;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.fukkit.Fukkit;
import org.fukkit.command.Command;
import org.fukkit.command.FlaggedCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.consequence.Report;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.flow.AsyncFleXPlayerOverwatchReplayPreDownloadEvent;
import org.fukkit.flow.OverwatchReplay;
import org.fukkit.handlers.FlowLineEnforcementHandler;
import org.fukkit.json.JsonBuffer;
import org.fukkit.json.JsonComponent;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.ThemeUtils;

import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.NumUtils;

import net.md_5.bungee.api.chat.ClickEvent.Action;

@GlobalCommand
@FlaggedCommand(flags = { "-v", "-a", "-c", "-r" })
@RestrictCommand(permission = "flex.command.flow", disallow = {})
@Command(name = "flow", usage = "/<command> [<#>] [<-v, -a, -c>]", aliases = { "flexoverwatch", "reports" })
public class FlowCommand extends FleXCommandAdapter {
	
	@SuppressWarnings("deprecation")
	public boolean perform(CommandSender sender, String[] args, String[] flags) {

		if (args.length != 0 && args.length != 1) {
			this.usage(sender);
			return false;
		}
		
		boolean flow = Fukkit.getFlowLineEnforcementHandler().isFlowEnabled();
		
		boolean view = ArrayUtils.contains(flags, "-v");
		boolean random = ArrayUtils.contains(flags, "-r");
		boolean clear = ArrayUtils.contains(flags, "-c");

		Theme theme = ((FleXPlayer)sender).getTheme();
		Language lang = ((FleXPlayer)sender).getLanguage();
		
		if (args.length == 1) {
			
			if (!NumUtils.canParseAsInt(args[0])) {
				this.invalid(sender);
				return false;
			}
			
			BukkitUtils.asyncThread(() -> {
				
				try {
					
					// TODO use local report cache instead of this resource taxing way...
					Set<Report> downloaded = Report.download(SQLCondition.where("id").is(Long.parseLong(args[0])));
					
					if (downloaded.isEmpty()) {
						// TODO: REPORT_VIEW_FAILURE_NOT_FOUND
						((FleXPlayer)sender).sendMessage(theme.format("<engine><failure>Report <sc>%id%<reset> <failure>doesn't exist<pp>.").replace("%id%", args[0]));
						return;
					}
					
					Report report = downloaded.stream().findFirst().orElse(null);
					
					BukkitUtils.mainThread(() -> {
						
						if (view) {
							
							view((FleXPlayer)sender, report);
							return;
							
						}
						
						if (clear) {
							
							if (report.isPardoned()) {
								
								// TODO: REPORT_CLEAR_FAILURE_ARCHIVED
								((FleXPlayer)sender).sendMessage(theme.format("<engine><pc>That report is already archived<pp>."));
								return;
								
							}
							
							// TODO: REPORT_CLEARING
							((FleXPlayer)sender).sendMessage(theme.format("<engine><pc>Clearing report<pp>,\\s<pc>please wait<pp>..."));
							
							BukkitUtils.asyncThread(() -> {

								try {
									
									report.pardon();
									
									// TODO: REPORT_CLEARED
									if (((FleXPlayer)sender).isOnline())
										((FleXPlayer)sender).sendMessage(theme.format("<engine><success>Report archived and cleared<pp>."));
									
								} catch (SQLException e) {
									
									e.printStackTrace();
									
									// TODO: ERROR_GENERIC
									if (((FleXPlayer)sender).isOnline())
										((FleXPlayer)sender).sendMessage(theme.format("<engine><failure>An error occured<pp>:\\s<failure>" +  e.getMessage() + "<pp>."));
									
								}
								
							});
							return;
						}
						
						// TODO: Rename: REPORT_LOADING -> REPORT_VIEW_LOADING
						((FleXPlayer)sender).sendMessage(ThemeMessage.REPORT_LOADING.format(theme, lang, ThemeUtils.getNameVariables(report.getPlayer(), theme)));
						
						// TODO: REPORT_INFORMATION
						((FleXPlayer)sender).sendMessage(new String[] { 
								
								theme.format("<engine><pc>Report details for report <sc>#%id%<pp>:").replace("%id%", args[0]),
								theme.format("<engine><reset>%display_reporter%<reset> <qc>reported <reset>%display%<pp>.").replace("%display%", report.getPlayer().getDisplayName(theme, true)).replace("%display_reporter%", report.getBy().getDisplayName(theme, true)),
								theme.format("<engine><qc>Category<pp>:<reset> <lore>%category%").replace("%category%", report.getReason().getCategory()),
								theme.format("<engine><qc>Reason<pp>:<reset> <lore>%reason%").replace("%reason%", report.getReason().toString()),
								theme.format("<engine><qc>Description<pp>:<reset> <lore>%description%").replace("%description%", report.getReason().getDescription())
								
						});
						
						if (report.getReason().getRequiredEvidence() != null && report.getReason().getRequiredEvidence().length > 0) {

							// TODO: REPORT_INFORMATION_REQUIRED
							((FleXPlayer)sender).sendMessage(theme.format("<engine><qc>Conviction evidence required<pp>:"));
							
							Arrays.asList(report.getReason().getRequiredEvidence()).forEach(e -> {
								// TODO: REPORT_INFORMATION_EVIDENCE
								((FleXPlayer)sender).sendMessage(theme.format("<engine><pp>*<reset> <lore>" + e));
							});
							
						}
						
					});
					
					return;
					
				} catch (SQLException e) {

					// TODO: REPORT_FAILURE_ERROR
					((FleXPlayer)sender).sendMessage("[ThemeMessage=REPORT_FAILURE_ERROR]");
					
				}
				
			});
			
			return true;
			
		}
		
		if (random) {
			
			BukkitUtils.asyncThread(() -> {

				Set<Report> reports = new LinkedHashSet<Report>();
				
				try {
					reports = Report.download(SQLCondition.where("pardoned").is(false));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				Set<Report> parse = reports;
				
				BukkitUtils.mainThread(() -> {
					
					if (!((FleXPlayer)sender).isOnline())
						return;
					
					if (parse == null || parse.isEmpty()) {
						// TODO: FLOW_NONE
						((FleXPlayer)sender).sendMessage("[ThemeMessage=FLOW_NONE]");
						return;
					}
					
					view((FleXPlayer)sender, parse.stream().collect(Collectors.toList()).get(NumUtils.getRng().getInt(0, parse.size()-1)));
					
				});
				
			});
			
			return true;
			
		}
		
		((FleXPlayer)sender).sendMessage(flow ? ThemeMessage.FLOW_REPORTS_LOADING.format(theme, lang) : ThemeMessage.REPORTS_LOADING.format(theme, lang));
		
		String[] reportView = flow ? ThemeMessage.FLOW_REPORTS_VIEW.format(theme, lang) : ThemeMessage.REPORTS_VIEW.format(theme, lang);
		
		BukkitUtils.asyncThread(() -> {

			Set<Report> reports = new LinkedHashSet<Report>();
			
			try {
				reports = Report.download(SQLCondition.where("pardoned").is(false));
			} catch (SQLException e) {
				e.printStackTrace();
			}

			Set<Report> parse = reports;
			
			BukkitUtils.mainThread(() -> {

				if (!((FleXPlayer)sender).isOnline())
					return;
				
				if (parse == null || parse.isEmpty()) {
					
					Arrays.stream((flow ? ThemeMessage.FLOW_REPORTS_NONE : ThemeMessage.REPORTS_NONE).format(lang, new Variable<Integer>("%amount%", parse.size()))).forEach(s -> {
						((FleXPlayer)sender).sendMessage(theme.format(s));
					});
					
					return;
					
				}
				
				for (Report report : parse) {
					for (String message : reportView) {
						
						JsonBuffer buffer = new JsonBuffer();
						
						if (report.getPlayer() == null || report.getBy() == null)
							continue;
						
						message = message.replace("%id%", String.valueOf(report.getReference()));
						message = message.replace("%name%", report.getPlayer().getDisplayName());
						message = message.replace("%player%", report.getPlayer().getName());
						message = message.replace("%display%", report.getPlayer().getDisplayName(((FleXPlayer)sender).getTheme(), true));
						message = message.replace("%reason%", report.getReason().toString());
						
						buffer = buffer.append(new JsonComponent(message)).replace("%interactable_review%", new JsonComponent(((FleXPlayer)sender).getTheme().format("<clickable>Review"))
										
										.onHover(((FleXPlayer)sender).getTheme().format("<interactable>Review evidence<pp>."))
										.onClick(Action.SUGGEST_COMMAND, "/flow " + report.getReference() + " -v"));
						
						buffer = buffer.replace("%interactable_clear%", new JsonComponent(((FleXPlayer)sender).getTheme().format("<clickable>Clear"))
								
								.onHover(((FleXPlayer)sender).getTheme().format("<interactable>Clear report<pp>."))
								.onClick(Action.SUGGEST_COMMAND, "/flow " + report.getReference() + " -c"));
						
						((FleXPlayer)sender).sendJsonMessage(buffer);
						
					}
				}
				
			});
			
		});
		
		return true;
		
	}
	
	private static void view(FleXPlayer player, Report report) {
		
		if (!player.isOnline())
			return;
		
		boolean flow = Fukkit.getFlowLineEnforcementHandler().isFlowEnabled();
		
		Theme theme = player.getTheme();
		
		if (report.isPardoned() && !player.hasPermission("flex.command.flow.archive")) {
			// TODO: REPORT_VIEW_FAILURE_ARCHIVED, REPORT_VIEW_FAILURE_ARCHIVED
			player.sendMessage(theme.format(flow ? "<flow><pc>You cannot view archived recording <sc>%id%<pp>." : "<engine><pc>You cannot view archived report <sc>%id%<pp>.").replace("%id%", String.valueOf(report.getReference())));
			return;
		}
		
		if (flow) {
			
			player.sendMessage(theme.format("<flow><pc>Loading evidence<pp>,\\s<pc>please wait<pp>..."));
			
			BukkitUtils.asyncThread(() -> {
				try {
					
					AsyncFleXPlayerOverwatchReplayPreDownloadEvent event = new AsyncFleXPlayerOverwatchReplayPreDownloadEvent(player, report);
					
					Fukkit.getEventFactory().call(event);
					
					if (event.isCancelled())
						return;
					
					OverwatchReplay replay = OverwatchReplay.download(report);
					
					if (replay != null)
						BukkitUtils.mainThread(() -> {
							try {
								
								FlowLineEnforcementHandler.watchReplay(replay, player, Integer.MAX_VALUE);
								
							} catch (UnsupportedOperationException | IllegalStateException | IOException e) {
								
								e.printStackTrace();
								
								BukkitUtils.mainThread(() -> {
									
									String error = e.getMessage();
									
									if (error.endsWith("."))
										error = error.substring(0, error.length()-1);
									
									if (player.isOnline())
										player.sendMessage(theme.format("<flow><failure>" + error + "<pp>."));
									
								});
								
							}
						});
					
					else throw new FileNotFoundException("Replay could not be found.");
					
				} catch (UnsupportedOperationException | IllegalStateException | SQLException | IOException e) {
					
					e.printStackTrace();
					
					String error = e.getMessage().toLowerCase();
					
					if (error.endsWith("."))
						error = error.substring(0, error.length()-1);
					
					String parse = error;
					
					BukkitUtils.mainThread(() -> {

						if (player.isOnline())
							player.sendMessage(theme.format("<flow><failure>Overwatch failed to load, " + parse + "<pp>."));
						
					});
					
				}
			});
			
			return;
			
		} else {
			// TODO: FLOW_REPORT_FAILURE_FLAG
			player.sendMessage(theme.format("<engine><failure>FloW is not enabled on this server<pp>.<reset> <failure>Cannot use flag %flag%<pp>.").replace("%flag%", "-v"));
			return;
		}
		
	}
	
}
