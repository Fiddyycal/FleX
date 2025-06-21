package io.flex.commons.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.flex.FleX.Task;
import net.md_5.fungee.utils.NetworkUtils;

public abstract class DataServer extends Thread {

	public static final int DEFAULT_DATA_RECEIVING_PORT = 15565;
	
	private int port;
	
	private ServerSocket server;
    
	private static final Map<String, Data> memory = new ConcurrentHashMap<String, Data>();
	
	public DataServer(int port) {
		
    	this.port = port;
    	
    	if (this.port == -1)
    		return;
    	
		try {
			this.server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
	}
	
	public int getPort() {
		return this.port;
	}
	
	@Override
	public void run() {
		
	    if (this.port == -1)
	        return;

	    while (true) {
	        try {
	        	
	            Socket client = this.server.accept();
	            
	            // Now handling each client in its own thread.
	            new Thread(() -> handleClient(client)).start();
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	}

	private void handleClient(Socket client) {
		
	    int port = client.getLocalPort();
	    
	    PrintWriter out = null;
        BufferedReader in = null;
	    
        try {
					
			out = new PrintWriter(client.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	        
	        debug("Socket: " + port, "Socket connected.");

	        String command = in.readLine();
	        if (command == null) {
	            Task.error("Socket: " + port, "Connection closed: Unable to resolve command: Command cannot be null.");
	            return;
	        }

	        debug("Socket: " + port, "Deciphering command from string " + command + ".");

	        DataCommand cmd;
	        try {
	            cmd = DataCommand.valueOf(command);
	        } catch (IllegalArgumentException e) {
	            Task.error("Socket: " + port, "Connection closed: Unable to resolve command " + command + ".");
	            return;
	        }

	        debug("Socket: " + port, "Requesting key...");
	        String key = in.readLine();
	        if (key == null) {
	            Task.error("Socket: " + port, "Connection closed: Key cannot be null.");
	            return;
	        }

	        if (cmd == DataCommand.SEND_DATA || cmd == DataCommand.PUBLISH_DATA) {
	            String value = in.readLine();
	            if (value == null && cmd == DataCommand.PUBLISH_DATA) {
	                memory.remove(key);
	            } else if (value != null) {
	                memory.put(key, new Data(key, value, port));
	            }

	            onDataReceive(new Data(key, value, port), cmd);
	            out.println(true);
	            debug("Socket: " + port, "Data receipt sent.");
	        }

	        if (cmd == DataCommand.REQUEST_DATA) {
	            Data data = memory.getOrDefault(key, new Data(key, null, port));
	            out.println(DataCommand.RETURN_DATA.name());
	            out.println(data.getValue());
	            debug("Socket: " + port, "Data returned (" + data.getValue() + ").");
	        }

	        if (cmd == DataCommand.RETURN_DATA) {
	            throw new UnsupportedOperationException("Data command \"RETURN_DATA\" cannot be used here, please revise.");
	        }
	        
	    } catch (IOException e) {
	    	
	        Task.error("Socket (IOException): " + port, e.getMessage());
	        
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
    	
    	if (this.port == -1)
    		return;
    	
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
    
    private static String ip;
	
	public String getData(String key, int port) {
		
		Socket client = establishClient(port);
		
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
			
            debug("Socket: " + port, "Deciphering command from string " + command + ".");
			
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
	
	public void setData(Data data, int port) {
		
        Socket client = establishClient(port);
        
		if (client == null)
			return;

	    PrintWriter out = null;
	    BufferedReader in = null;
	    
        try {
        	
        	out = new PrintWriter(client.getOutputStream());
        	
            out.println(DataCommand.PUBLISH_DATA.name());
            out.println(data.getKey());
            out.println(data.getValue());
            
            debug("Socket: " + port, "Setting data: " + DataCommand.PUBLISH_DATA + "::" + data.getKey() + "::" + data.getValue());
            
            out.flush();
            
            debug("Socket: " + port, "Done.");
            
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            debug("Socket: " + port, "Reading response...");
            
            String response = in.readLine();
            
            debug("Socket: " + port, "Read response.");
            
            if (response == null || !response.equalsIgnoreCase("true"))
            	Task.error("Socket: " + port, "Unable to publish data " + data.getKey() + "::" + data.getValue() + ": no futher information.");
            
        } catch (IOException e) {
            e.printStackTrace();
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
	
	public void sendData(Data data, int port) {
		
        Socket client = establishClient(port);
        
		if (client == null)
			return;

	    PrintWriter out = null;
	    BufferedReader in = null;
	    
        try {
        	
        	out = new PrintWriter(client.getOutputStream());
        	
            out.println(DataCommand.SEND_DATA.name());
            out.println(data.getKey());
            out.println(data.getValue());
            
            debug("Socket: " + port, "Sending data: " + DataCommand.SEND_DATA + "::" + data.getKey() + "::" + data.getValue());
            
            out.flush();
            
            debug("Socket: " + port, "Done.");
            
        } catch (IOException e) {
            e.printStackTrace();
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
    
	private final static String try_first = "172.18.0.3";
	
	private static Socket establishClient(int port) {
		
		Socket client;
		
		if (ip == null) {
			
			Task.print("Sockets", 
					
					"Attempting to connect sockets for efficient heartbeat communication, " +
					"if their is a freeze here turn on debug mode, find out which attempt is successful " +
					"and put that attempt into the \"try_first\" field.");
			
			client = attemptConnection("localhost", port);
			
			if (client == null && try_first != null)
				client = attemptConnection(try_first, port);
			
			if (client == null)
				client = attemptConnection("127.0.0.1", port);
			
			if (client == null)
				client = attemptConnection("127.0.0.11", port);
			
			if (client == null)
				client = attemptConnection("172.17.0.1", port);
			
			if (client == null)
				client = attemptConnection("172.18.0.1", port);
			
			if (client == null)
				client = attemptConnection("172.18.0.2", port);
			
			if (client == null)
				client = attemptConnection("172.18.0.3", port);
			
			if (client == null)
				client = attemptConnection("172.18.0.4", port);
			
			if (client == null)
				client = attemptConnection("172.18.0.5", port);
			
			if (client == null)
				client = attemptConnection("172.18.0.6", port);
			
			if (client == null)
				client = attemptConnection("172.18.0.7", port);
			
			if (client == null)
				client = attemptConnection("172.18.0.8", port);
			
			if (client == null) {
				Task.error("Sockets", "Connection attempts for local data communication has failed. Turn on debugging to see attempts.");
				return null;
			}
			
		} else client = attemptConnection(ip, port);
		
		return client;
		
	}
	
	private static Socket attemptConnection(String ip, int port) {
		
		debug("Sockets", "Attempting to connect to " + ip + ":" + port + "...");
		
		try {
			
            Socket client = new Socket(ip, port);
            
    		debug("Sockets", "Connection successful.");
            
            if (client != null) {
            	DataServer.ip = ip;
            	return client;
            }
            
        } catch (IOException e) {
        	
    		debug("Sockets", "Connection refused: " + e.getMessage());
        	
        }
		
    	return null;
		
	}
    
    private static void debug(String prefix, String message) {

		if (NetworkUtils.isProxy())
			Task.print(prefix, message);
		
		else Task.debug(prefix, message);
    	
    }
	
}
