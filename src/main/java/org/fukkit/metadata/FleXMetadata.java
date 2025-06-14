package org.fukkit.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;

public class FleXMetadata extends MetadataValueAdapter implements MetadataValue {

	public FleXMetadata() {
		super(Fukkit.getInstance());
	}
	
	public final <F extends FleXMetadata> F as(Class<F> cls) {
		return cls.isInstance(this) ? cls.cast(this) : null;
	}
	
	public final <F extends FleXMetadata> boolean is(Class<F> cls) {
		return cls.isInstance(this);
	}
	
	/**
	 * Default implementation does nothing.
	 */
	@Override
	public void invalidate() {
		//
	}

	/**
	 * Returns this instance for the sake of returning something useful.
	 */
	@Override
	public Object value() {
		return this;
	}
	
	public static <M extends FleXMetadata> boolean hasMetaData(Class<M> metadata, Player player, String key) {
		return getMetaData(metadata, player, key) != null;
	}
	
	public static <M extends FleXMetadata> boolean hasMetaData(Class<M> metadata, FleXPlayer fleXPlayer, String key) {
		return getMetaData(metadata, fleXPlayer == null ? null : fleXPlayer.getPlayer(), key) != null;
	}
	
	public static <M extends FleXMetadata> M getMetaData(Class<M> metadata, Player player, String key) {
		Objects.requireNonNull(metadata, "The given Class<? extends FleXMetadata> was null!");
		Objects.requireNonNull(player, "The given Player was null!");
		
		List<MetadataValue> list = player.getMetadata(key);
		List<M> found = new ArrayList<>();
		
		// First allow all assignable FleXMetadata to be found.
		list.forEach(data -> {
			if (metadata.isInstance(data)) {
				found.add(metadata.cast(data));
			}
		});
		
		if (found.size() >= 1) {
			return found.get(0);
		}
		
		return null;
	}

}
