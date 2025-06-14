package org.fukkit.api.helper;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;

public class PlayerHelper {

	@Deprecated
	/**
	 * @deprecated Phasing the PlayerHelper out completely, use {@link Fukkit#getPlayer(String)} instead.
	 * @param name
	 * @return
	 */
	public static FleXPlayer getPlayer(String name) {
		return Fukkit.getPlayer(name);
	}

	@Deprecated
	/**
	 * @deprecated Phasing the PlayerHelper out completely, use {@link Fukkit#getPlayer(UUID))} instead.
	 * @param uuid
	 * @return
	 */
	public static FleXPlayer getPlayerSafe(UUID uuid) {
		return Fukkit.getPlayer(uuid);
	}

	@Deprecated
	/**
	 * @deprecated Phasing the PlayerHelper out completely, use {@link Fukkit#getPlayerExact(org.bukkit.entity.HumanEntity))} instead.
	 * @param player
	 * @return
	 */
	public static FleXPlayer getPlayerExact(Player player) {
		return Fukkit.getPlayerExact(player);
	}

	@Deprecated
	/**
	 * @deprecated Phasing the PlayerHelper out completely, use {@link Fukkit#getPlayer(String)} instead.
	 * @param name
	 * @return
	 */
	public static FleXPlayer getPlayerOnline(String name) {
		return Fukkit.getPlayer(name);
	}
	
}
