package org.fukkit.scoreboard.entry;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;
import org.fukkit.utils.FormatUtils;

import io.flex.commons.cache.cell.Cell;
import io.flex.commons.utils.StringUtils;

public class TeamEntry implements CharSequence, Cloneable {
	
	private Team team;
	
	private String prefix = "", suffix = "", lastPrefix = "", lastSuffix = "";
	
	private Cell<?> impute, attribute;
	
	public TeamEntry(String line) {
		this.line(line, null, null, null);
	}
	
	public TeamEntry(String prefix, String suffix) {
		this.line(prefix, suffix, null, null);
	}
	
	public TeamEntry(String prefix, Cell<?> attribute) {
		this.line(prefix, null, null, attribute);
	}
	
	public TeamEntry(Cell<?> prefix, Cell<?> suffix) {
		this.line(null, null, prefix, suffix);
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public String getSuffix() {
		return this.suffix;
	}
	
	public Cell<?> getImpute() {
		return this.impute;
	}
	
	public Cell<?> getAttribute() {
		return this.attribute;
	}
	
	public Team getTeam() {
		return this.team;
	}
	
	public void setTeam(Team team) {
		this.team = team;
		this.update();
	}
	
	public void setLine(String line) {
		this.line(line, null, null, null);
	}
	
	public void setLine(String prefix, String suffix) {
		this.line(prefix, suffix, null, null);
	}
	
	public void setLine(String prefix, Cell<?> attribute) {
		this.line(prefix, null, null, attribute);
	}
	
	public boolean isUnregistered() {
		try {
			
			if (this.team != null)
				this.team.getDisplayName();
			
			return false;
			
		} catch (IllegalStateException e) {
			return true;
		}
	}
	
	private void line(String prefix, String suffix, Cell<?> impute, Cell<?> attribute) {
		
		String[] line = suffix != null ? new String[] { StringUtils.shorten(prefix, 0, 16), StringUtils.shorten(suffix, 0, 16) } : this.generate(prefix);
		
		this.lastPrefix = FormatUtils.format(line[0]);
		this.lastSuffix = FormatUtils.format(line[1]);
		
		this.impute = impute;
		this.attribute = attribute;
		
		this.update();
		
	}
	
	public void update() throws IllegalStateException {
		
	    this.prefix = this.lastPrefix + (this.impute != null ? this.impute.a() : "");
	    this.suffix = this.lastSuffix + (this.attribute != null ? this.attribute.a() : "");
	    
	    if (this.team != null) {
	        this.team.setPrefix(FormatUtils.format(StringUtils.shorten(this.prefix, 0, 16)));
	        this.team.setSuffix(FormatUtils.format(StringUtils.shorten(this.suffix, 0, 16)));
	    }
	    
	}
	
	private String[] generate(String line) {
		
		String prefix = FormatUtils.format(StringUtils.shorten(line, 0, 16));
		String suffix = FormatUtils.format(StringUtils.shorten(line, 16, 32));
		
		if (prefix.endsWith("&")) {
			
			prefix = StringUtils.shorten(prefix, 0, prefix.length() - 1);
			suffix = ChatColor.COLOR_CHAR + suffix;
			
		} else if (prefix.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
			
			prefix = StringUtils.shorten(prefix, 0, prefix.length() - 1);
			suffix = '&' + suffix;
			
		} else {
			
			suffix = ChatColor.getLastColors(prefix).length() > 0 ? ChatColor.getLastColors(prefix) + suffix : ChatColor.RESET + suffix;
			suffix = StringUtils.shorten(suffix, 0, 16);
			
		}
		
		return new String[]{ prefix, suffix };
		
	}

	@Override
	public int length() {
		return this.toString().length();
	}

	@Override
	public char charAt(int index) {
		return this.toString().charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return this.toString().subSequence(start, end);
	}
	
	@Override
	public String toString() {

		String prefix = this.lastPrefix;
		
		if (this.impute != null)
			prefix = prefix + this.impute.a();
		
		String method = FormatUtils.format(String.valueOf((this.attribute != null ? this.attribute.a() : "")));
		
		String suffixColor = ChatColor.getLastColors(this.lastSuffix);
		String methodColor = method.length() > 1 && method.charAt(0) == ChatColor.COLOR_CHAR ? ChatColor.getByChar(method.charAt(1)).toString() : null;
		
		return StringUtils.shorten(prefix + (suffixColor.equals(methodColor) || suffixColor.equals(this.lastSuffix) ? "" : this.lastSuffix) + method, 0, 32);
		
	}
	
	@Override
	public TeamEntry clone() {
		try {
			return (TeamEntry) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
