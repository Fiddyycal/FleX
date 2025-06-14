package io.flex.commons;

import java.util.Random;

public class RandomNumberGenerator {

	public static final Random RANDOM = new Random();
	
	public int getInt(int min, int max, int... neglect) {
		
    	if (min < Integer.MIN_VALUE)
    		min = Integer.MIN_VALUE;
    	
    	if (max > Integer.MAX_VALUE)
    		max = Integer.MAX_VALUE;
    	
    	if (min > max)
    		throw new UnsupportedOperationException("Bound error: Minimum cannot be more than maximum.");
    	
    	int ri = (int) (Math.random() * ((max-min) + 1)) + min;
    	
    	for (int i : neglect)
    		if (i == ri) return this.getInt(min, max, neglect);
    	
    	return ri;
    	
    }
    
	public double getDouble(double min, double max, double... neglect) {
		
    	if (min < Double.MIN_VALUE)
    		min = Double.MIN_VALUE;
    	
    	if (max > Double.MAX_VALUE)
    		max = Double.MAX_VALUE;
    	
    	if (min > max)
    		throw new UnsupportedOperationException("Bound error: Minimum cannot be more than maximum.");
    	
    	double rd = min + (max - min) * RANDOM.nextDouble();
    	
    	for (double d : neglect)
    		if (d == rd) return this.getDouble(min, max, neglect);
    	
    	return rd;
    	
    }
	
}
