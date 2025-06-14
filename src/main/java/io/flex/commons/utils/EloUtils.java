package io.flex.commons.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.flex.commons.Nullable;

public class EloUtils {
	
	public static final int WIN = 1, DRAW = 0, LOSS = -1;
	
    public static int interpolate(int winner, int other) {
        return calculate(winner, other, WIN);
    }
	
	public static Map<Integer, Integer> calculate(int winnerId, @Nullable Map<Integer, Integer> elos) {
        
        Map<Integer, Integer> calc = new HashMap<Integer, Integer>();
		
        if (elos == null || elos.isEmpty())
            return calc;
        
        if (elos.size() == 2) {
        	
        	int opponentId = elos.keySet().stream().filter(k -> k != winnerId).findFirst().orElse(null);
        	int opponent = elos.get(opponentId);
        	
        	int winner = elos.get(winnerId);
        	int diff = interpolate(winner, opponent) - winner;
        	
        	calc.put(winnerId, winner + diff);
        	calc.put(opponentId, opponent - diff);
        
        	return calc;
        	
        }
        
        double q = 0.0;
        
        for (int userId : elos.keySet())
            q += Math.pow(10.0, ((double) elos.get(userId) / 400));
        
        for (Entry<Integer, Integer> el : calc.entrySet()) {
        	
        	int id = el.getKey();
        	int elo = el.getValue();
        	
            double expected = (double) Math.pow(10.0, ((double) elo / 400)) / q;
            
            calc.put(id, r(id, winnerId != id ? 0.0 : 1.0 / elos.size(), expected));
            
        }
        
        return calc;
        
    }
    
    public static int calculate(int challenger, int opponent, int outcome) {
        
    	if (outcome != WIN && outcome != LOSS && outcome != DRAW)
    		return challenger;
    	
        double exponent = (double) (opponent - challenger) / 400;
        double expected = (1 / (1 + (Math.pow(10, exponent))));
       
        return r(challenger, (outcome == WIN ? 1.0 : 0.5), expected);
        
    }
    
    private static int k(int elo) {
    	return elo < 2000 ? 32 : elo >= 2000 && elo < 2400 ? 24 : 16;
    }
    
    private static int r(int elo, double multiple, double expected) {
    	
    	/*
    	 * 
    	 * 1 - if player is winner
    	 * 0 - if player losses
    	 * (another option is to give fractions of 1/number-of-players instead of 0)
    	 * 
    	 */
    	
    	int k = k(elo);
    	
    	return (int) Math.round(elo + k * (multiple - expected)) + (k / 7);
    	
    }

}
