package net.md_5.fungee.server;

import java.util.LinkedList;
import java.util.List;

import org.fukkit.api.helper.DataHelper;
import org.fukkit.api.helper.ServerHelper;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;
import io.flex.commons.socket.DataServer;

import net.md_5.fungee.utils.NetworkUtils;

public class Server {
	
    public static final Server PROXY = new Server(25565);
	
	private static final List<Server> values = new LinkedList<Server>();
	
    private Server fallback;
    
    private int port, data;
    
    private Server(int port) {
        this(null, port);
    }

    private Server(@Nullable Server fallback, int port) {
    	
    	if (port < 1025)
            throw new UnsupportedOperationException("Cannot use port '" + port + "': Ports be greater than 1025 (pterodactyl default minimum).");
    	
    	if (port > 65535)
            throw new UnsupportedOperationException("Cannot use port '" + port + "': Ports must not exceed 65535.");
        
    	Server exists = values.stream().filter(s -> s.getPort() == port).findFirst().orElse(null);
    	
    	if (exists != null)
            throw new IllegalArgumentException("Cannot use port '" + port + "' as it is already bound: Port bound by server " + exists.getName() + ".");
        
        this.fallback = fallback;
        this.port = port;
        
        this.data = this == PROXY ? DataServer.DEFAULT_WRITABLE_PORT : this.port + 35535/*FleX server data port*/;
        
        values.add(this);
        
    }
	
	public String getName() {
		return this == PROXY ? "Proxy" : "S" + this.getId();
	}
	
	public Server getFallback() throws ServerConnectException {
		
		if (this.fallback == null)
			throw new ServerConnectException(ServerConnectException.FALLBACK_ERROR);
		
		return this.fallback;
		
	}
	
	public int getId() {
		return this.port < 25565 ? this.port : values.indexOf(this);
	}
	
	public String getIp() {
		return DataHelper.getString("server." + this.name() + ".ip");
	}
	
	public int getPort() {
		return this.port;
	}
	
	public int getDataPort() {
		return this == PROXY ? DataServer.DEFAULT_WRITABLE_PORT : this.data;
	}
	
	public int getOnline() {
		try {
			return Integer.parseInt(DataHelper.getString("server." + this.name() + ".online"));
		} catch (Exception e) {
			return -1;
		}
	}
	
	public void setOnline(int online) {
		DataHelper.set("server." + this.name() + ".online", online);
	}
	
	public void connect(FleXPlayer... players) {
		
		if (NetworkUtils.isProxy())
			throw new UnsupportedOperationException("Cannot transfer player with the Server object, use the proxy player object to handle connections.");
		
		for (FleXPlayer fp : players)
			ServerHelper.connect(fp.getPlayer(), this.getName());
		
	}
	
	public boolean isOffline() {
		return this.getIp() == null || this.getOnline() == -1;
	}
	
	public static Server getByName(String name) {
		
		for (Server server : values)
			if (server.getName().equalsIgnoreCase(name))
				return server;
		
		return null;
		
	}
	
	public static Server getByPort(int port) {
		
		for (Server server : values)
			if (server.getPort() == port || server.getDataPort() == port)
				return server;
		
		return null;
		
	}
	
	public static Server valueOf(String name) {
		
		for (Server server : values)
			if (server.name().equals(name))
				return server;
		
		return null;
		
	}
	
	public static List<Server> values() {
		return values;
	}
	
	public String name() {
		return this.toString();
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
}
