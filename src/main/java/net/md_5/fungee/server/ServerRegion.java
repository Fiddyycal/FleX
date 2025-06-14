package net.md_5.fungee.server;

import java.util.Arrays;

public enum ServerRegion {

	AS("Asia"),
	EU("Europe"),
	NA("North America"),
	AU("Oceania"),
	SG("Singapore"),
	US("America"),
	
	UNSPECIFIED("Global");
	
	private String display;
	
	private ServerRegion(String display) {
		this.display = display;
	}
	
	public String toString() {
		return this.display;
	}
	
	public static ServerRegion fromString(String display) {
		ServerRegion region = Arrays.stream(values()).filter(v -> v.display.equals(display)).findFirst().orElse(null);
		return region != null ? region : valueOf(display);
	}
	
}
