package org.fukkit.scoreboard.entry;

import org.bukkit.entity.Player;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.playerlist.tab.FleXImageSkin;

import io.flex.commons.cache.cell.Cell;
import io.flex.commons.utils.StringUtils;

public class NamedTeamEntry extends TeamEntry {
	
	private String name = "";
	
	private FleXSkin skin = FleXImageSkin.BLANK;
	
	private Player player;
	
	public NamedTeamEntry(String line) {
		
		super(StringUtils.shorten(line, 0, 16), StringUtils.shorten(line, 28, 44));
		
		this.name = StringUtils.shorten(line, 16, 28);
		
	}
	
	public NamedTeamEntry(String prefix, String name, String suffix) {
		
		super(prefix, suffix);
		
		this.name = StringUtils.shorten(name, 0, 12);
		
	}
	
	public NamedTeamEntry(String prefix, String name, Cell<?> attribute) {
		
		super(prefix, attribute);
		
		this.name = StringUtils.shorten(name, 0, 12);
		
	}
	
	public NamedTeamEntry(Cell<?> prefix, String name, Cell<?> suffix) {
		
		super(prefix, suffix);
		
		this.name = StringUtils.shorten(name, 0, 12);
		
	}
	
	public NamedTeamEntry(FleXSkin skin, String line) {
		
		super(StringUtils.shorten(line, 0, 16), StringUtils.shorten(line, 28, 44));
		
		this.name = StringUtils.shorten(line, 16, 28);
		
		this.skin = skin;
		
	}
	
	public NamedTeamEntry(FleXSkin skin, String prefix, String name, String suffix) {
		
		super(prefix, suffix);
		
		this.name = StringUtils.shorten(name, 0, 12);
		
		this.skin = skin;
		
	}
	
	public NamedTeamEntry(FleXSkin skin, String prefix, String name, Cell<?> attribute) {
		
		super(prefix, attribute);
		
		this.name = StringUtils.shorten(name, 0, 12);
		
		this.skin = skin;
		
	}
	
	public NamedTeamEntry(FleXSkin skin, Cell<?> prefix, String name, Cell<?> suffix) {
		
		super(prefix, suffix);
		
		this.name = StringUtils.shorten(name, 0, 12);
		
		this.skin = skin;
		
	}
	
	public NamedTeamEntry(Player player) {
		
		super("");
		
		this.setPlayer(player);
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public FleXSkin getSkin() {
		return this.skin;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSkin(FleXSkin skin) {
		this.skin = skin;
	}
	
	public void setPlayer(Player player) {
		
		this.player = player;
		this.name = player.getName();
		
		if (player instanceof FleXPlayer)
			this.skin = ((FleXPlayer)player).getSkin();
		
	}

}
