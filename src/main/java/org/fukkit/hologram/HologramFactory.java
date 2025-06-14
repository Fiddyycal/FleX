package org.fukkit.hologram;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.fukkit.item.Head;
import org.fukkit.item.Item;

import net.md_5.fungee.ProtocolVersion;

public interface HologramFactory {
	
	public Hologram createHologram(ProtocolVersion version, Location location, String... lines);
	
	public Hologram createHologram(Location location, String... lines);
	
	public FloatingItem createFloatingItem(Location location, Head item, boolean small);
	
	public FloatingItem createFloatingItem(Location location, Item item);
	
	public Hologram getHologram(UUID uuid);
	
	public Hologram getHologram(Location location);
	
	public Hologram getHologram(Entity entity);
	
	public FloatingItem getFloatingItem(UUID uuid);
	
	public FloatingItem getFloatingItem(Location location);
	
	public FloatingItem getFloatingItem(Entity entity);
	
}
