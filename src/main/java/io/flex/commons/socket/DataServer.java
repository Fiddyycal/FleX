package io.flex.commons.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.flex.FleX;
import io.flex.FleX.Task;

import net.md_5.fungee.utils.NetworkUtils;

public abstract class DataServer extends Thread {

	private static final Map<String, Data> memory = new ConcurrentHashMap<String, Data>();
	
	private int port;
	
	private ServerSocket server;
	
	private boolean readOnly = false;
	
	public DataServer(int port) throws IOException {
		
    	this.port = port;
    	
    	if (this.port <= -1)
    		return;
    	
    	Task.print("Sockets", "Opening socket to listen on... (" + FleX.LOCALHOST_IP + ":" + port + ")");
    	
		this.server = new ServerSocket(port);
    	
    	Task.print("Sockets", "Done.");
    	
	}
	
	public int getPort() {
		return this.port;
	}
	
	@Override
	public void run() {
		
	    if (this.port <= -1)
    		return;

	    while (true) {
	        try {
	        	
	            Socket client = this.server.accept();
	            
	            new Thread(() -> this.handleClient(client)).start();
	            
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
	        	
	            Task.error("Socket: " + port, "Closing connection: Unable to resolve command: Command cannot be null.");
	            return;
	            
	        }
	        
	        DataCommand cmd;
	        
	        try {
	            cmd = DataCommand.valueOf(command);
	        } catch (IllegalArgumentException e) {
	            Task.error("Socket: " + port, "Closing connection: Unable to resolve command " + command + ".");
	            return;
	        }
	        
	        String key = in.readLine();
	        
	        if (key == null) {
	        	
	            Task.error("Socket: " + port, "Closing connection: Key cannot be null.");
	            return;
	            
	        }

	        if (cmd == DataCommand.SEND_DATA || cmd == DataCommand.PUBLISH_DATA) {
	        	
	            String value = in.readLine();
	            
	            if (cmd == DataCommand.PUBLISH_DATA) {
		        	
		        	if (this.readOnly)
			            throw new UnsupportedOperationException("Data command \"" + cmd.name() + "\" cannot be used here, this socket is read only.");
	            	
	            	if (value == null)
		                memory.remove(key);
		            
		            else memory.put(key, new Data(key, value, port));
	            	
	            }
	            
	            this.onDataReceive(new Data(key, value, port), cmd);
	            
	            // Sending receipt.
	            out.println(true);
	            
	        }

	        if (cmd == DataCommand.REQUEST_DATA) {
	        	
	        	if (this.readOnly)
		            throw new UnsupportedOperationException("Data command \"" + cmd.name() + "\" cannot be used here, this socket is read only.");
	        	
	            Data data = memory.getOrDefault(key, new Data(key, null, port));
	            
	            out.println(DataCommand.RETURN_DATA.name());
	            out.println(data.getValue());
	            
	        }

	        if (cmd == DataCommand.RETURN_DATA)
	            throw new UnsupportedOperationException("Data command \"" + cmd.name() + "\" cannot be used here, please revise.");
	        
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
	
	public void setData(Data data, String ip, int port) {
		
        Socket client = attemptConnection(ip, port);
        
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
	
	public void sendData(Data data, String ip, int port) {
		
        Socket client = attemptConnection(ip, port);
        
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
	
	private static Socket attemptConnection(String ip, int port) {
		
		debug("Sockets", "Attempting to connect to " + ip + ":" + port + "...");
		
		try {
			
            Socket client = new Socket(ip, port);
            
    		debug("Sockets", "Connection successful.");
            
            if (client != null)
            	return client;
            
        } catch (UnknownHostException e) {

			Task.error("Sockets", "Connection attempt to " + ip + ":" + port + " has failed: " + e.getMessage());
        	
        } catch (IOException e) {
        	
    		debug("Sockets", "Connection refused: " + e.getMessage());
        	
        }
		
		Task.error("Sockets", "Connection attempt to " + ip + ":" + port + " has failed: No futher information.");
    	return null;
		
	}
    
    private static void debug(String prefix, String message) {

		if (NetworkUtils.isProxy())
			Task.print(prefix, message);
		
		else Task.debug(prefix, message);
    	
    }
	
}
