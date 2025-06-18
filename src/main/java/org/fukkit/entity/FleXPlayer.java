package org.fukkit.entity;

import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.fukkit.clickable.Menu;
import org.fukkit.consequence.Ban;
import org.fukkit.consequence.Mute;
import org.fukkit.disguise.Disguise;
import org.fukkit.disguise.Visibility;
import org.fukkit.event.entity.EntityCleanEvent.CleanType;
import org.fukkit.history.HistoryStore;
import org.fukkit.json.JsonBuffer;
import org.fukkit.scoreboard.playerlist.ListScore;
import org.fukkit.scoreboard.playerlist.NameTag;
import org.fukkit.scoreboard.playerlist.NameBar;
import org.fukkit.scoreboard.playerlist.tab.Tablist;
import org.fukkit.scoreboard.sidebar.Sidebar;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.Nullable;
import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;

import net.md_5.fungee.ProtocolVersion;

public interface FleXPlayer extends FleXHumanEntity {
	
	/**
	 * @deprecated Please use {@link #getDisplayName(Theme)} instead.
	 */
	@Deprecated
	public String getDisplayName();
	
	public String getDisplayName(Theme theme);
	
	public String getDisplayName(Theme theme, boolean ignoreDisguise);
	
	public String getPlayerListName();
	
	public double getAbsorptionHearts();
	
	public int getPing();
	
	public long getCurrency();
	
	public Theme getTheme();
	
	public String getIp();
	
	public NameTag getNameTag(FleXPlayer viewer);
	
	public NameBar getSubNameTag();
	
	public ListScore getListbar();
	
	public Sidebar getSidebar();
	
	public Tablist getTablist();
	
	public Disguise getDisguise();
	
	public Visibility getVisibility();
	
	public Mute getMute();
	
	public Ban getBan();
	
	public Language getLanguage();
	
	public ProtocolVersion getVersion();
	
	public Map<String, Boolean> getPermissions();
	
	public HistoryStore getHistory() throws FleXPlayerNotLoadedException;
	
	public void getHistoryAsync(Consumer<HistoryStore> history);
	
	public void getHistoryAsync(Consumer<HistoryStore> history, Runnable timeout);
	
	public Player getPlayer();
	
	public void setCurrency(long currency);
	
	public void setTheme(Theme theme);
	
	public void setSubNameTag(NameBar namebar);
	
	public void setListbar(ListScore listbar);
	
	public void setSidebar(Sidebar sidebar);
	
	public void setPermission(String permission, boolean value);
	
	public void setPermission(Permission permission, boolean value);
	
	public void setTablist(Tablist tab);
	
	public void setDisguise(Disguise disguise);
	
	public void setLanguage(Language language);
	
	public void setVersion(ProtocolVersion version);
	
	public void setVisibility(Visibility visibility);
	
	public void setVelocity(Vector vector);
	
	@Deprecated
	public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException;
	
	public void sendMessage(ThemeMessage message);
	
	public void sendMessage(ThemeMessage message, Variable<?>... variables);
	
	public void sendJsonMessage(JsonBuffer[] json);
	
	public void sendJsonMessage(JsonBuffer json);
	
	public void sendJsonMessage(String[] json);
	
	public void sendJsonMessage(String json);
	
	public void sendActionbar(String display);
	
	public void sendTitle(String title);

    public void sendTitle(String title, String subtitle);

    public void sendTitle(String title, int fadeIn, int screenTime, int fadeOut);
    
    public void sendTitle(String title, String subtitle, int fadeIn, int screenTime, int fadeOut);
    
	public void sendParticle(Location location, Effect effect, int id, int data);
	
	public void sendParticle(Location location, Effect effect, int id, int data, float offsetX, float offsetY, float offsetZ, float speed, int particleCount, int radius);
	
	public void addNameTag(NameTag nametag);
	
	public void removeNameTag(NameTag nametag);
    
	public void removePermission(String permission);
	
	public void removePermission(Permission permission);
	
	public void openMenu(Menu menu, boolean keepOpen);
	
	public void updateMenu();
	
	public void closeMenu();
	
	public void unDisguise();
	
	public void unMute();
	
	public void unBan();
	
	public boolean isDisguised();
	
	public boolean isMuted();
	
	public boolean isBanned();
	
	public boolean isNew();
	
	public void disconnect(@Nullable String reason);
	
	public void kickPlayer(String message);
	
	public void kick(@Nullable String... message);
	
	public void reload() throws SQLException;
	
	/**
	 * 
	 * Every use of this method pushes SQL upstream, it is
	 * strongly recommended that this method be used asynchronously.
	 * 
	 * @throws SQLException When database error occurs.
	 * @throws FleXPlayerNotLoadedException when data row is not found.
	 * 
	 */
	public void push() throws SQLException, FleXPlayerNotLoadedException;
	
	/**
	 * 
	 * Every use of this method pulls SQL downstream, it is
	 * strongly recommended that this method be used asynchronously.
	 * 
	 * @throws SQLException When database error occurs.
	 * 
	 */
	public void pull() throws SQLException;
	
	public void update();
	
	@Deprecated
	public void clean(CleanType type, boolean async);
	
	public void clean(CleanType type);
	
	public void onConnect(Player player);
	
	public void onDisconnect(Player player);
	
}
