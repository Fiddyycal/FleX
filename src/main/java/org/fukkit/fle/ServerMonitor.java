package org.fukkit.fle;

import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;

public class ServerMonitor extends BukkitRunnable {
	
    private static final long spike_threshold_ms = 100;
    private static final long spike_pause_ms = 500;
	
    private static boolean started = false;

    private static long last = System.currentTimeMillis();
    private static long until = 0;
    
    public static boolean isLagging() {
    	
        long now = System.currentTimeMillis();
        
        if (now < until)
            return true;
        
        // TODO check if tps is lower than 18.5 and return true if so.
        return false;
        
    }

	@Override
	public void run() {
		
		long now = System.currentTimeMillis();
        long tickTime = now - last;
        
        last = now;
        
        if (tickTime > spike_threshold_ms)
        	until = now + spike_pause_ms;
        
	}
    
	public static void start() {
		
		if (started)
			throw new UnsupportedOperationException("ServerMonitor has already started.");
		
		new ServerMonitor().runTaskTimer(Fukkit.getInstance(), 1L, 1L);
		
		started = true;
		
	}
	
}
