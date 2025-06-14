package org.fukkit.disguise;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;

public interface DisguiseFactory {

	/**
	 * Retrieves player skin as currently set in local game profile variable or the flex_user database.
	 * 
	 * @param uuid The unique id of the player you want to get the skin from.
	 * @param database Should the skin be retrieved from the flex_user database or directly from GameProfile.
	 */
	public FleXSkin getPlayerSkin(UUID uuid, boolean database);

	/**
	 * Retrieves player skin as currently set in local game profile variable.
	 * 
	 * @param player The player you want to get the skin from.
	 */
	public FleXSkin getPlayerSkin(Player player);
	
	/**
	 * Changes the players name to the specified name for <u>all players</u>, including the player.
	 * 
	 * @param player The flex player you want to mask.
	 * @param name The name the flex player will appear to have.
	 */
	public void setPlayerName(Player player, String name);

	/**
	 * Changes the players name to the specified name in the player list for <u>all players</u>, including the player.
	 * 
	 * @param player The flex player you want to mask.
	 * @param name The name the flex player will appear to have.
	 */
	public void setPlayerListName(Player player, String name);

	/**
	 * Changes the players name to the specified name in the player list for <u>the other player</u> specified.
	 * 
	 * @param player The flex player you want to mask.
	 * @param name The name the flex player will appear to have.
	 */
	public void setPlayerListName(Player player, Player other, String name);

	/**
	 * Changes the players skin to the specified skin for <u>all players</u>, including the player.
	 * @see {@link DisguiseFactory#updatePlayer(FleXPlayer)}.
	 * 
	 * @param player The flex player you want to mask.
	 * @param skin The skin the flex player will appear to have.
	 */
	public void setPlayerSkin(Player player, FleXSkin skin);

	/**
	 * Update the player for all players, <u>including the player</u>.
	 * 
	 * @param player The flex player that will be updated.
	 */
	public void updatePlayer(Player player);

	/**
	 * Update the player for <u>the other player</u> specified.
	 * 
	 * @param player The flex player that will be updated.
	 * @param other The flex player the player will be updated for.
	 */
	public void updatePlayer(Player player, @Nullable Player other);

	/**
	 * Verify whether the player is modified or not.
	 * 
	 * @param player The flex player that is being checked.
	 */
	public boolean isPlayerModified(Player player);

}
