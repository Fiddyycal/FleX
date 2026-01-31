package org.fukkit.fle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.UUID;

import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.utils.BukkitUtils;

import io.flex.commons.cache.cell.BiCell;

public class AutoClickerDetector {
	
	private static final Set<UUID> notified_of = new HashSet<UUID>();
	
	private int highestCps = 0;
	
	private Deque<Long> clicks = new ArrayDeque<Long>();
	
	private Map<BiCell<Integer, Integer>, Integer> consistency = new HashMap<BiCell<Integer, Integer>, Integer>();
	
	private List<Integer> avarages = new ArrayList<Integer>();
	
	private FlowProfile profile;
	
	private int avarage = -1;
	
	public AutoClickerDetector(FlowProfile profile) {
		
		/**
		 * 
		 * cps, consistencyMax
		 * 
		 * consistency starts at 0, if a player hits higher than cps, that number goes up.
		 * If a player goes below cps, that number goes down, if the number goes above consistencyMax, staff is notified.
		 * 
		 */
		this.consistency.put(BiCell.of(10, 100), 0);
		this.consistency.put(BiCell.of(15, 10), 0);
		this.consistency.put(BiCell.of(20, 2), 0);
		
		this.profile = profile;
		
	}
	
	public int getCps() {
		return this.clicks.size();
	}
	
	public int getHighestCps() {
		return this.highestCps;
	}
	
	public int getAvarage() {
		return this.avarage;
	}
	
	private long last = System.currentTimeMillis();
	
	public void update() {
		
		long now = System.currentTimeMillis();
		
		if (now >= this.last + 10_000)
			this.consistency.replaceAll((k, v) -> 0);
		
		this.last = now;
		
		if (ServerMonitor.isLagging())
			return;
		
		this.clicks.addLast(now);
		
	    while (!this.clicks.isEmpty() && now - this.clicks.peekFirst() > 1000)
	    	this.clicks.removeFirst();
	    
	    int cps = this.clicks.size();
	    
	    if (cps > this.highestCps)
    		this.highestCps = cps;
	    
	    if (cps >= 8) {
	    	
	    	this.avarages.add(cps);
	    	
	    	if (this.avarages.size() > 10)
	    		this.avarages.remove(0);
	    	
	    	int avg = 0;
	    	
	    	for (int i = 0; i < this.avarages.size(); i++) {
				
	    		int c = this.avarages.get(i);
	    		
	    		avg = avg + c;
	    		
			}
	    	
	    	this.avarage = avg / this.avarages.size();
	    	
	    }
	    
	    for (Entry<BiCell<Integer, Integer>, Integer> consistencies : this.consistency.entrySet()) {
			
	    	BiCell<Integer, Integer> counter = consistencies.getKey();
	    	
	    	int max = counter.a();
	    	int consistency = consistencies.getValue();
			
	    	//Debug information
		    //Bukkit.getPlayer("Fiddycal").sendMessage(this.profile.getPlayer().getName() + ": " + cps + "/" + this.highestCps + " (" + consistency + ")");
	    	
	    	if (cps > max) {
	    		
	    		FleXPlayer player = this.profile.getPlayer();
	    		
	    		if (player.getPing() > 100)
	    			return;
		    	
	    		this.consistency.put(counter, ++consistency);
	    		
	    		int consistencyMax = counter.b();
	    		
	    		if (consistency > consistencyMax && this.avarage > 11) {
	    			
	    			if (notified_of.contains(player.getUniqueId()))
	    				return;
	    			
	    			for (FleXPlayer fp : Fukkit.getOnlinePlayers()) {
	    				
	    				Theme theme = fp.getTheme();
	    				
	    				if (fp.isStaff()) {
	    					
	    					notified_of.add(player.getUniqueId());
	    					
	    					BukkitUtils.runLater(() -> notified_of.remove(player.getUniqueId()), 300L);
	    					
	    					fp.sendMessage(theme.format("<flow>&4&lSuspicous avtivity detected for " + this.profile.getPlayer().getDisplayName(theme, true) + "<pp>."));
	    					fp.sendMessage(theme.format("<flow><lore>System Flag<sp>:\\s<pc>High Click/Swing Packet Count"));
	    					fp.sendMessage(theme.format("<engine><lore>Alert Reason<sp>:\\s<pc>Above " + cps + " CPS unnaturally consistent"));
	    					fp.sendMessage(theme.format("<engine><lore>Current/Highest" + (this.avarage > 0 ? "/Avarage" : "") + "<sp>:\\s<pc>" + max + "/" + this.highestCps + (this.avarage > 0 ? "/" + this.avarage : "")));
	    					fp.sendMessage(theme.format("<engine><lore>Severity<sp>:" + Theme.reset + " " + this.severity(consistency, consistencyMax)));
	    					fp.sendMessage(theme.format("<engine><success>Review this players cps in their user panel<pp>."));
	    					
	    				}
	    				
	    			}
	    			
	    		}
	    		
	    	} else if (consistency > 0)
	    		this.consistency.put(counter, --consistency);
	    	
		}
		
	}
	
	public void clear() {
		this.clicks = null;
		this.consistency.clear();
		this.consistency = null;
		this.profile = null;
	}
	
	private String severity(int consistency, int consistencyMax) {
		
		String severity = "<severe>&l|&8&l|||||||||";
		
		if (consistency > consistencyMax)
			severity = "<severe>&l||&8&l||||||||";
		
		if (consistency > consistencyMax * 2)
			severity = "<severe>&l|||&8&l|||||||";
		
		if (consistency > consistencyMax * 3)
			severity = "<severe>&l|||||&8&l|||||";
		
		if (consistency > consistencyMax * 4)
			severity = "<severe>&l|||||||&8&l|||";
		
		if (consistency > consistencyMax * 5)
			severity = "<severe>&l||||||||||";
		
		return severity;
		
	}
	
}
