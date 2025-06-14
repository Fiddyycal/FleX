package io.flex.commons.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import io.flex.FleX.Task;
import net.md_5.fungee.utils.NetworkUtils;

public abstract class DataServer extends Thread {

	public static final int DEFAULT_DATA_RECEIVING_PORT = 15565;
	
	private int port;
    private Socket client;
	private ServerSocket server;
    private PrintWriter out;
    private BufferedReader in;
    
	private static final Map<String, Data> memory = new HashMap<String, Data>();
	
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
	@SuppressWarnings("deprecation")
	public void run() {
    	
    	if (this.port == -1) {
    		this.stop();
    		return;
    	}
    	
		while(true) {
			
			try {
				
				debug("Sockets", "Waiting for connection on port " + this.port + "...");
				
				try {
					this.client = this.server.accept();
				} catch (Exception e) {
					Task.error("Connection could not be established: " + e.getMessage() + ": no further information.");
					continue;
				}
				
				int port = this.client.getLocalPort();
				
				debug("Socket: " + port, "Socket connected.");
				
	            this.out = new PrintWriter(this.client.getOutputStream());
		        this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
				
				String command = this.in.readLine();
				
				if (command == null) {
					Task.error("Socket: " + port, "Connection closed: Unable to resolve command: Command cannot be null.");
					continue;
				}
				
				debug("Socket: " + port, "Deciphering command from string " + command + ".");
				
	            DataCommand cmd;
				
				try {
					cmd = DataCommand.valueOf(command);
				} catch (IllegalArgumentException e) {
					Task.error("Socket: " + port, "Connection closed: Unable to resolve command " + command + ".");
					continue;
				}
				
				debug("Socket: " + port, "Requesting key...");
				
				String key = this.in.readLine();
				
				if (key == null) {
					Task.error("Socket: " + port, "Connection closed: Key cannot be null.");
					continue;
				}
				
				if (cmd == DataCommand.SEND_DATA || cmd == DataCommand.PUBLISH_DATA) {
					
					String value = null;
					
					try {
						
						value = this.in.readLine();
						
						debug("Socket: " + port, "Key found. Raw UTF: " + command + "::" + key + "::" + value);
			            
					} catch (IndexOutOfBoundsException e) {
						
						debug("Socket: " + port, "Key found. Raw UTF: " + command + "::" + key + "::null");

						if (cmd == DataCommand.PUBLISH_DATA) {

				            debug("Socket: " + port, "Found value to be null, removing key from memory.");
				            
							memory.remove(key);
							
						}
						
					}
					
					Data data = new Data(key, value, port);
					
					if (cmd == DataCommand.PUBLISH_DATA && value != null) {
						
						debug("Socket: " + port, "Publishing value " + value + ".");
						
						memory.put(key, data);
						
					}
					
					this.onDataReceive(data, cmd);
					
					debug("Socket: " + port, "Sending reciept...");
					
					// Confirms the data was received.
					this.out.println(true);
					this.out.flush();
					
					debug("Socket: " + port, "Data reciept sent.");
					
				}
				
				if (cmd == DataCommand.REQUEST_DATA) {
					
		            debug("Socket: " + port, "Key found. Raw UTF: " + command + "::" + key);
					
					Data data = memory.get(key);
					
					if (data == null)
						data = new Data(key, null, this.port);
		    		
					this.out.println(DataCommand.RETURN_DATA.name());
					this.out.println(data.getValue());
					this.out.flush();
					
					debug("Socket: " + port, "Data returned (" + data.getValue() + ").");
					
				}
				
				if (cmd == DataCommand.RETURN_DATA)
					throw new UnsupportedOperationException("Data command \"RETURN_DATA\" cannot be used here, please revise.");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void close() throws IOException {
    	
    	if (this.port == -1)
    		return;
    	
    	if (this.in != null)
    		this.in.close();

    	if (this.out != null)
    		this.out.close();

    	if (this.client != null)
    		this.client.close();
    	
    	if (this.server != null)
    		this.server.close();
    	
    }
	
    @SuppressWarnings("deprecation")
    public void kill() throws IOException {
    	
    	this.close();
    	
    	/* 
    	 * As of JDK8, Thread#stop has been de-implemented.
    	 * It now just throws UnsupportedOperationException.
    	 */
    	if (null == null)
    		return;
    	
		this.stop();
		
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
