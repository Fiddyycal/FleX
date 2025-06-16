package org.fukkit.command.defaults;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.fukkit.Fukkit;
import org.fukkit.command.Command;
import org.fukkit.command.FlaggedCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.consequence.Report;
import org.fukkit.fle.flow.OverwatchReplay;
import org.fukkit.json.JsonBuffer;
import org.fukkit.json.JsonComponent;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
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
	public boolean perform(String[] args, String[] flags) {

		if (args.length != 0 && args.length != 1) {
			this.usage();
			return false;
		}
		
		boolean flow = Fukkit.getFlowLineEnforcementHandler().isFlowEnabled();
		
		boolean view = ArrayUtils.contains(flags, "-v");
		boolean random = ArrayUtils.contains(flags, "-r");
		boolean clear = ArrayUtils.contains(flags, "-c");

		Theme theme = this.getPlayer().getTheme();
		Language lang = this.getPlayer().getLanguage();
		
		Set<Report> reports = new LinkedHashSet<Report>();
		
		if (args.length == 1) {
			
			if (!NumUtils.canParseAsInt(args[0])) {
				this.invalid();
				return false;
			}
			
			try {
				
				// TODO use local report cache instead of this resource taxing way...
				Set<Report> downloaded = Report.download(SQLCondition.where("id").is(Long.parseLong(args[0])));
				
				if (downloaded.isEmpty()) {
					// TODO: REPORT_VIEW_FAILURE_NOT_FOUND
					this.getPlayer().sendMessage(theme.format("<engine><failure>Report <sc>%id%<reset> <failure>doesn't exist<pp>.").replace("%id%", args[0]));
					return false;
				}
				
				Report report = downloaded.stream().findFirst().orElse(null);
				
				if (report.isPardoned() && !this.getPlayer().hasPermission("flex.command.flow.archive")) {
					// TODO: REPORT_VIEW_FAILURE_ARCHIVED, REPORT_VIEW_FAILURE_ARCHIVED
					this.getPlayer().sendMessage(theme.format(flow ? "<flow><pc>You cannot view archived recording <sc>%id%<pp>." : "<engine><pc>You cannot view archived report <sc>%id%<pp>.").replace("%id%", args[0]));
					return false;
				}
				
				if (view) {
					
					if (flow) {

						// TODO: REPORT_VIEW_LOADING
						this.getPlayer().sendMessage(theme.format("<flow><pc>Evidence loading<pp>,\\s<pc>please wait<pp>..."));
						
						try {
							
							new OverwatchReplay(report, true, this.getPlayer());
							return true;
							
						} catch (Exception e) {
							
							e.printStackTrace();
							
							this.getPlayer().sendMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
							return false;
							
						}
						
					} else {
						// TODO: FLOW_REPORT_FAILURE_FLAG
						this.getPlayer().sendMessage(theme.format("<engine><failure>FloW is not enabled on this server<pp>.<reset> <failure>Cannot use flag %flag%<pp>.").replace("%flag%", "-v"));
						return false;
					}
					
				}
				
				if (clear) {
					// TODO: REPORT_CLEARING
					this.getPlayer().sendMessage(theme.format("<engine><pc>Clearing report<pp>,\\s<pc>please wait<pp>..."));
					// TODO Put new end logic here
					//BridgeHandler.end(report);
					report.pardon();
					// TODO: REPORT_CLEARED
					this.getPlayer().sendMessage(theme.format("<engine><success>Report archived and cleared<pp>."));
					return true;
				}
				
				// TODO: Rename: REPORT_LOADING -> REPORT_VIEW_LOADING
				this.getPlayer().sendMessage(ThemeMessage.REPORT_LOADING.format(theme, lang, ThemeUtils.getNameVariables(report.getPlayer(), theme)));
				
				// TODO: REPORT_INFORMATION
				this.getPlayer().sendMessage(new String[] { 
						
						theme.format("<engine><pc>Report details for report <sc>#%id%<pp>:").replace("%id%", args[0]),
						theme.format("<engine><reset>%display_reporter%<reset> <qc>reported <reset>%display%<pp>.").replace("%display%", report.getPlayer().getDisplayName(theme, true)).replace("%display_reporter%", report.getBy().getDisplayName(theme, true)),
						theme.format("<engine><qc>Category<pp>:<reset> <lore>%category%").replace("%category%", report.getReason().getCategory()),
						theme.format("<engine><qc>Reason<pp>:<reset> <lore>%reason%").replace("%reason%", report.getReason().toString()),
						theme.format("<engine><qc>Description<pp>:<reset> <lore>%description%").replace("%description%", report.getReason().getDescription())
						
				});
				
				if (report.getReason().getRequiredEvidence() != null && report.getReason().getRequiredEvidence().length > 0) {

					// TODO: REPORT_INFORMATION_REQUIRED
					this.getPlayer().sendMessage(theme.format("<engine><qc>Conviction evidence required<pp>:"));
					
					Arrays.asList(report.getReason().getRequiredEvidence()).forEach(e -> {
						// TODO: REPORT_INFORMATION_EVIDENCE
						this.getPlayer().sendMessage(theme.format("<engine><pp>*<reset> <lore>" + e));
					});
					
				}
				
				return true;
				
			} catch (SQLException e) {

				// TODO: REPORT_FAILURE_ERROR
				this.getPlayer().sendMessage("[ThemeMessage=REPORT_FAILURE_ERROR]");
				
			}
			
			return false;
			
		}
		
		if (random) {
			
			try {
				reports = Report.download(SQLCondition.where("pardoned").is(false));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if (reports == null || reports.isEmpty()) {
				// TODO: FLOW_NONE
				this.getPlayer().sendMessage("[ThemeMessage=FLOW_NONE]");
				return false;
			}

			try {
				
			} catch (Exception e) {
				
				this.getPlayer().sendMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
				return false;
				
			}
			
			return true;
			
		}
		
		this.getPlayer().sendMessage(flow ? ThemeMessage.FLOW_REPORTS_LOADING.format(theme, lang) : ThemeMessage.REPORTS_LOADING.format(theme, lang));
		
		String[] reportView = flow ? ThemeMessage.FLOW_REPORTS_VIEW.format(theme, lang) : ThemeMessage.REPORTS_VIEW.format(theme, lang);
		
		try {
			reports = Report.download(SQLCondition.where("pardoned").is(false));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (reports == null || reports.isEmpty()) {

			Arrays.stream((flow ? ThemeMessage.FLOW_REPORTS_NONE : ThemeMessage.REPORTS_NONE).format(lang, new Variable<Integer>("%amount%", reports.size()))).forEach(s -> {
				this.getPlayer().sendMessage(theme.format(s));
			});
			
			return false;
			
		}
		
		for (Report report : reports) {
			for (String message : reportView) {
				
				JsonBuffer buffer = new JsonBuffer();
				
				if (report.getPlayer() == null || report.getBy() == null)
					continue;
				
				message = message.replace("%id%", String.valueOf(report.getReference()));
				message = message.replace("%name%", report.getPlayer().getDisplayName());
				message = message.replace("%player%", report.getPlayer().getName());
				message = message.replace("%display%", report.getPlayer().getDisplayName(this.getPlayer().getTheme(), true));
				message = message.replace("%reason%", report.getReason().toString());
				
				buffer = buffer.append(new JsonComponent(message)).replace("%interactable_review%", new JsonComponent(this.getPlayer().getTheme().format("<clickable>Review"))
								
								.onHover(this.getPlayer().getTheme().format("<interactable>Review evidence<pp>."))
								.onClick(Action.SUGGEST_COMMAND, "/flow " + report.getReference() + " -v"));
				
				buffer = buffer.replace("%interactable_clear%", new JsonComponent(this.getPlayer().getTheme().format("<clickable>Clear"))
						
						.onHover(this.getPlayer().getTheme().format("<interactable>Clear report<pp>."))
						.onClick(Action.SUGGEST_COMMAND, "/flow " + report.getReference() + " -c"));
				
				this.getPlayer().sendJsonMessage(buffer);
				
			}
		}
		
		return true;
		
	}
	
}
