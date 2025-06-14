package net.md_5.fungee.listeners;

import java.util.HashMap;
import java.util.Map;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import io.flex.commons.cache.cell.BiCell;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.fungee.FungeeCord;

public class FlowListeners implements Listener {
	
	public static final Map<BiCell<String, String>, ByteArrayDataOutput> pending_data = new HashMap<BiCell<String, String>, ByteArrayDataOutput>();
	
	public FlowListeners() {
		
		ProxyServer.getInstance().getPluginManager().registerListener(FungeeCord.getInstance(), this);
		
		ProxyServer.getInstance().registerChannel("flex:flow");
		
	}
	
	@EventHandler
    public void event(PluginMessageEvent event) {
		
		String channel = event.getTag();
		
        if (channel.equalsIgnoreCase("flex:flow")) {
        	
        	ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            
            String action = in.readUTF();
            
        	if (action.equals("play")) {
                
                String uuid = in.readUTF();
                
                long time = in.readLong();
                
                boolean anon = in.readBoolean();
        		
        		ByteArrayDataOutput out = ByteStreams.newDataOutput();
    	        
    	        out.writeUTF(action);
    	        out.writeUTF(uuid);
    	        
    	        out.writeLong(time);
    	        
    	        out.writeBoolean(anon);
    	        
    	        pending_data.put(cell("Flow", channel), out);
        		
        	}
        	
        	return;
        	
        }
            
    }
	
	@EventHandler
	public void event(ServerConnectEvent event) {
		
		ServerInfo server = event.getTarget();
		
        /**
         * FOR FLOW
         */
		pending_data.entrySet().removeIf(e -> {
	        
			BiCell<String, String> cell = e.getKey();
			
			String name = cell.a();
			String tag = cell.b();
			
			if (server.getName().equals(name)) {
				ProxyServer.getInstance().getServerInfo(name).sendData(tag, e.getValue().toByteArray());
				return true;
			}
			
			return false;
			
		});
		
	}
	
	private static BiCell<String, String> cell(String server, String tag) {
		return new BiCell<String, String>() {
			
			private static final long serialVersionUID = 3530859202010775719L;

			@Override
			public String a() {
				return server;
			}
			
			@Override
			public String b() {
				return tag;
			}
			
		};
	}

}
