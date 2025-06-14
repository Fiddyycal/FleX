package org.fukkit.api.helper;

import org.fukkit.Fukkit;

import io.flex.commons.sql.SQLDriverType;
import net.md_5.fungee.ProtocolVersion;
import net.md_5.fungee.server.ServerRegion;
import net.md_5.fungee.server.ServerVersion;

public class NetworkHelper {
	
	public static SQLDriverType getDataDriver() {
		return Fukkit.getServerHandler().getDataDriver();
	}
	
	public static ServerVersion getVersion() {
		return Fukkit.getServerHandler().getServerVersion();
	}
	
	public static ServerRegion getRegion() {
		return Fukkit.getServerHandler().getServerRegion();
	}
	
	public static ProtocolVersion[] getBlockedProtocols() {
		return Fukkit.getServerHandler().getBlockedProtocols();
	}
	
	public static boolean isOffline() {
		return Fukkit.getServerHandler().isLocalHost();
	}

}
