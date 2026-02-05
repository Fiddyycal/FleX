package org.fukkit.recording;

import java.util.UUID;

import org.fukkit.disguise.FleXSkin;

public class PlaceholderFleXSkin implements FleXSkin {
	
	private UUID uuid;
	
	public PlaceholderFleXSkin(UUID uuid) {
		this.uuid = uuid;
	}
	
	public UUID getUniqueId() {
		return this.uuid;
	}
	
	@Override
	public String getValue() {
		return FleXSkin.DEFAULT.getValue();
	}
	
	@Override
	public String getSignature() {
		return FleXSkin.DEFAULT.getSignature();
	}
	
	@Override
	public String getPropertyName() {
		return FleXSkin.DEFAULT.getPropertyName();
	}
	
	@Override
	public String getName() {
		return this.uuid.toString();
	}
	
	@Override
	public SkinSource getSource() {
		return SkinSource.DATABASE;
	}
	
}
