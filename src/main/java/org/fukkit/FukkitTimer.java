package org.fukkit;

import io.flex.commons.utils.NumUtils;
import io.flex.commons.utils.StringUtils;

public abstract class FukkitTimer extends FukkitRunnable {
	
    private double time, elapsed;
    
    private boolean countdown, stopped;
    
    public FukkitTimer(double time, boolean countdown) {
    	
        this.time = time;
        this.elapsed = countdown ? time + 0.1 : -0.1;
    	
        this.countdown = countdown;
        this.stopped = false;
        
        this.runTimer(2L, 2L);
        
    }
    
    @Override
    public void run() {
    	
        this.elapsed = this.countdown ? this.elapsed - .1 : this.elapsed + .1;
        boolean unfinished = !this.isComplete();
        
        this.onTick();
        
        if (!unfinished) {
        	this.elapsed = this.countdown ? 0.0 : this.time;
        	this.stop();
        }
        
    }
    
    public double getTime() {
        return this.time;
    }
    
    public double getTimeLeft() {
        return this.time - this.elapsed;
    }
    
    public double getElapsedTime() {
        return this.elapsed;
    }
    
    public double getPercentage() {
    	double percent = this.elapsed * 100 / this.time;
        return this.countdown ? 100 - percent : percent;
    }
    
    public boolean isStopped() {
    	return this.stopped || this.isComplete();
    }
    
    public boolean isComplete() {
    	return this.countdown ? this.elapsed <= 0.0 : this.elapsed >= this.time;
    }
    
    public boolean isCountdown() {
    	return this.countdown;
    }
    
    public void stop() {
        this.cancel();
        this.stopped = true;
        this.onStop();
    }
    
    public String asBar() {
        return StringUtils.progressBar(this.getPercentage(), "[", "]", "=", "-", 10);
    }
    
    public String asBar(String loaded, String loading, int length) {
        return StringUtils.progressBar(this.getPercentage(), null, null, loaded, loading, length);
    }
    
    public String asBar(String prefix, String suffix, String loaded, String loading, int length) {
        return StringUtils.progressBar(this.getPercentage(), prefix, suffix, loaded, loading, length);
    }
    
    public String asClock(boolean showFractional, boolean showZeros) {
        return NumUtils.asClock(this.elapsed, showFractional, showZeros);
    }
    
    public abstract void onTick();
    
    public abstract void onStop();
    
}