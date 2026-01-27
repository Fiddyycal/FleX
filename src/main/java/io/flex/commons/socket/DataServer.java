package io.flex.commons.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.flex.FleX;
import io.flex.FleX.Task;

import net.md_5.fungee.utils.NetworkUtils;

public abstract class DataServer extends Thread {

	public static final int DEFAULT_WRITABLE_PORT = 15565;
	
	private static final Map<String, Data> memory = new ConcurrentHashMap<String, Data>();
	
	private int port;
	
	private ServerSocket server;
	
	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	public DataServer(int port) throws IOException {
		
    	this.port = port;
    	
    	if (this.port <= -1)
    		return;
    	
    	Task.print("Sockets", "Opening socket to listen" + (this instanceof RelayDataServer == false ? " and write " : " ") + "on... (" + FleX.LOCALHOST_IP + ":" + port + ")");
    	
		this.server = new ServerSocket();
		this.server.bind(new InetSocketAddress(FleX.LOCALHOST_IP, port));
		
    	Task.print("Sockets", "This socket is reachable via " + FleX.LOCALHOST_IP + ".");
    	Task.print("Sockets", "If you are not using docker or some kind of firewall, it is highly recommended that you use point-to-point authentication keys before transfers to provent data spoofing.");
    	
	}
	
	public int getPort() {
		return this.port;
	}
	
	public String getData(String key, String ip, int port) {
		
		Socket client = attemptConnection(ip, port);
		
		if (client == null)
			return null;
		
	    PrintWriter out = null;
	    BufferedReader in = null;
	    
		try {
			
            debug("Socket: " + port, "Sending " + DataCommand.REQUEST_DATA + "::" + key + ".");
			
            out = new PrintWriter(client.getOutputStream());
    		
            out.println(DataCommand.REQUEST_DATA.name());
            out.println(key);
            
            out.flush();
    		
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            String command = in.readLine();
			
			if (command == null) {
				Task.error("Socket: " + port, "Connection closed: Unable to resolve command: Command cannot be null.");
				return null;
			}
			
			DataCommand cmd;
			
			try {
				cmd = DataCommand.valueOf(command);
			} catch (IllegalArgumentException e) {
				Task.error("Socket: " + port, "Connection closed: Unable to resolve command " + command + ".");
				return null;
			}
				
			String value = in.readLine();
				
	        debug("Socket: " + port, "Found value (" + value + ").");
    		
            if (cmd != DataCommand.RETURN_DATA)
				throw new UnsupportedOperationException("Data command is not \"RETURN_DATA\" and cannot be used here, please revise.");
    		
            debug("Socket: " + port, "Completed request: " + cmd + "::" + key + "::" + value);
			
            if (value.equalsIgnoreCase("null"))
            	return null;
            
            return value;
            
        } catch (IOException e) {
        	
            e.printStackTrace();
            
            return null;
            
        } finally {
			
        	try {
        		
        		if (out != null)
            		out.close();
    			
            	if (in != null)
    				in.close();
    			
            	if (client != null)
            		client.close();
            	
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        	
		}
		
	}
	
	@Override
	public void run() {
		
	    if (this.port <= -1)
    		return;
	    
	    ExecutorService pool = Executors.newCachedThreadPool(r -> {
	    	
	        Thread t = new Thread(r, "DataServer-worker");
	        
	        t.setDaemon(true);
	        
	        return t;
	        
	    });

	    while (!this.server.isClosed()) {
	        try {
	        	
	            Socket client = this.server.accept();
	            
	            pool.execute(() -> handleClient(client));
	            
	        } catch (IOException e) {
	        	
	            if (this.server.isClosed())
	                break;
	            
	            Task.error("Socket", "Accept failed: " + e.getMessage());
	            
	        }
	    }
	    
	}

	private void handleClient(Socket client) {
		
	    int port = client.getLocalPort();
	    
	    PrintWriter out = null;
        BufferedReader in = null;
	    
        try {
        	
        	// Timeout after 3 seconds
        	client.setSoTimeout(3000);
					
			out = new PrintWriter(client.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	        
	        String command = in.readLine();
	        
	        debug("Socket: " + port, "Socket connected.");
	        
	        if (command == null) {
	        	
	            Task.error("Socket: " + port, "Closing connection: Command cannot be null.");
	            return;
	            
	        }
	        
	        DataCommand cmd;
	        
	        try {
	            cmd = DataCommand.valueOf(command);
	        } catch (IllegalArgumentException e) {
	            Task.error("Socket: " + port, "Closing connection: Unable to resolve command \"" + command + "\".");
	            return;
	        }
	        
	        String key = in.readLine();
	        
	        if (key == null) {
	        	
	            Task.error("Socket: " + port, "Closing connection: Key cannot be null.");
	            return;
	            
	        }
	        
	        if (cmd == DataCommand.SEND_DATA || cmd == DataCommand.PUBLISH_DATA) {
	        	
	            String value = in.readLine().replace("\\n", "\n");
	            
		        debug("Socket: " + port, (cmd == DataCommand.PUBLISH_DATA ? "Publishing data" : "Recieved data") + " key \"" + key + "\" with value \"" + value + "\".");
	            
	            Data data = new Data(key, value, port);
	            
	            if (cmd == DataCommand.PUBLISH_DATA) {
	            	
	            	long deleteMs = Long.parseLong(in.readLine());
		        	
		        	if (this instanceof RelayDataServer)
			            throw new UnsupportedOperationException("Data command \"" + cmd.name() + "\" cannot be used here, this socket is a listener only.");
	            	
	            	if (value == null)
		                memory.remove(key);
		            
		            else {
		            	
		            	memory.put(key, data);
		            	
		            	if (deleteMs > 0) {
		            		
		    		        debug("Socket: " + port, "Key '" + key + "' will be deleted from memory after " + deleteMs + " milliseconds.");
		    		        
		            		this.scheduler.schedule(() -> memory.remove(key), deleteMs, TimeUnit.MILLISECONDS);
		            		
		            	}
		            	
		            }
	            	
	            }
		        
	            this.onDataReceive(data, cmd);
	            
	            // Sending receipt.
	            out.println(true);
	            
	        }

	        if (cmd == DataCommand.REQUEST_DATA) {
	        	
	        	if (this instanceof RelayDataServer)
		            throw new UnsupportedOperationException("Data command \"" + cmd.name() + "\" cannot be used here, this socket is a listener only.");
	        	
	            Data data = memory.getOrDefault(key, new Data(key, null, port));
	        	
		        debug("Socket: " + port, "Resolved request key \"" + data.getKey() + "\" and returning value \"" + data.getValue() + "\" to sender.");
	            
	            out.println(DataCommand.RETURN_DATA.name());
	            out.println(data.getValue());
	            
	        }

	        if (cmd == DataCommand.RETURN_DATA)
	            throw new UnsupportedOperationException("Data command \"" + cmd.name() + "\" cannot be used here, please revise.");
	        
        } catch (SocketTimeoutException e) {
        	
        	debug("Socket: " + port, "Closing connection: Read time out.");
	        
	    } catch (IOException e) {
	    	
	        Task.error("Socket: " + port, e.getMessage());
	        
	    } catch (UnsupportedOperationException e) {
	    	
	        Task.error("Socket: " + port, e.getMessage());
	        
	    } finally {
			
	    	if (in != null)
				try {
					in.close();
				} catch (IOException ignore) {}
	    	
	    	if (out != null)
	    		out.close();
	    	
	    	if (client != null)
				try {
					client.close();
				} catch (IOException ignore) {}
	    	
		}
	}
	
	public void close() throws IOException {
    	
    	if (this.server != null)
    		this.server.close();
    	
    }
	
    public void kill() {
    	try {
			this.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public abstract void onDataReceive(Data data, DataCommand command);
	
	public void setData(Data data, String ip, int port) {
		this.setData(data, ip, port, -1);
	}
	
	public void setData(Data data, String ip, int port, long deleteAfter) {
		
        Socket client = attemptConnection(ip, port);
        
		if (client == null)
			return;

	    PrintWriter out = null;
	    BufferedReader in = null;
        
        String response = null;
	    
        try {
        	
        	out = new PrintWriter(client.getOutputStream());
        	
            out.println(DataCommand.PUBLISH_DATA.name());
            out.println(data.getKey());
            out.println(data.getValue());
            out.println(deleteAfter);
            
            debug("Socket: " + port, "Setting data: " + DataCommand.PUBLISH_DATA + "::" + data.getKey() + "::" + data.getValue());
            
            out.flush();
            
            debug("Socket: " + port, "Done.");
            
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            debug("Socket: " + port, "Reading response...");
            
            response = in.readLine();
            
            debug("Socket: " + port, "Read response.");
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            
            if (response == null || !response.equalsIgnoreCase("true"))
            	Task.error("Socket: " + port, "Unable to publish data " + data.getKey() + "::" + data.getValue() + ": no futher information.");
			
        	try {
        		
        		if (out != null)
            		out.close();
    			
            	if (in != null)
    				in.close();
    			
            	if (client != null)
            		client.close();
            	
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        	
		}
        
    }
	
	public void sendData(Data data, String ip, int port) {
		
        Socket client = attemptConnection(ip, port);
        
		if (client == null)
			return;
		
	    PrintWriter out = null;
	    BufferedReader in = null;
	    
	    String response = null;
	    
        try {
        	
        	out = new PrintWriter(client.getOutputStream());
        	
            out.println(DataCommand.SEND_DATA.name());
            out.println(data.getKey());
            out.println(data.getValue().replace("\n", "\\n"));
            
            debug("Socket: " + port, "Sending data: " + DataCommand.SEND_DATA + "::" + data.getKey() + "::" + data.getValue());
            
            out.flush();
            
            debug("Socket: " + port, "Done.");
            
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            debug("Socket: " + port, "Reading response...");
            
            response = in.readLine();
            
            debug("Socket: " + port, "Read response.");
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            
            if (response == null || !response.equalsIgnoreCase("true"))
            	Task.error("Socket: " + port, "Unable to send data " + data.getKey() + "::" + data.getValue() + ": " + (response != null ? "response returned " + response : "No further information") + ".");
			
        	try {
        		
        		if (out != null)
            		out.close();
    			
            	if (in != null)
    				in.close();
    			
            	if (client != null)
            		client.close();
            	
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        	
		}
        
    }
	
	protected static Socket attemptConnection(String ip, int port) {
		
		if (ip == null)
			throw new NullPointerException("ip cannot be null");
		
		debug("Sockets", "Attempting to connect to " + ip + ":" + port + "...");
		
		try {
			
			Socket socket = new Socket();
		    
		    socket.connect(new InetSocketAddress(ip, port), /*5 second timeout*/5000);
            
    		debug("Sockets", "Connection successful.");
            
            if (socket != null)
            	return socket;
            
        } catch (Exception e) {
			Task.error("Sockets", "Connection attempt to " + ip + ":" + port + " has failed: " + e.getMessage());
        }
		
    	return null;
		
	}
    
	protected static void debug(String prefix, String message) {

		if (NetworkUtils.isProxy())
			Task.print(prefix, message);
		
		else Task.debug(prefix, message);
    	
    }
	
}
