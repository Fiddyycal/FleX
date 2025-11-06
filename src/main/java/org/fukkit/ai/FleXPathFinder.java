package org.fukkit.ai;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.fukkit.ai.task.FleXAITaskBuilder;
import org.fukkit.entity.FleXBot;

public abstract class FleXPathFinder {

	private FleXBotAI ai;
	
	private static final int DEFAULT_MAX_NODES = 2000;
	
	public FleXPathFinder(FleXBotAI ai) {
		this.ai = ai;
	}
	
	public FleXBot getBot() {
		return this.ai.getBot();
	}
	
    public boolean canNavigateTo(Location target) {
        return this.canNavigateTo(target, DEFAULT_MAX_NODES);
    }
    
    /**
     * Returns true if a simple walkable path likely exists from start -> target.
     * This uses BFS on block coordinates and allows stepping up or down by 1 block.
     *
     * @param start starting location (bot's current location)
     * @param target desired target location
     * @param maxNodes maximum number of visited nodes to explore (safety limit)
     */
    public boolean canNavigateTo(Location target, int maxNodes) {
    	
    	Location start = this.ai.getBot().getLocation();
    	
        if (start == null || target == null)
        	return false;
        
        World world = start.getWorld();
        
        if (world == null || world != target.getWorld())
        	return false;
        
        int sx = start.getBlockX();
        int sy = start.getBlockY();
        int sz = start.getBlockZ();
        
        int tx = target.getBlockX();
        int ty = target.getBlockY();
        int tz = target.getBlockZ();
        
        if (!canStandAt(world, tx, ty, tz))
            return false;
        
        Deque<int[]> queue = new ArrayDeque<int[]>();
        Set<String> visited = new HashSet<String>();
        
        queue.add(new int[] { sx, sy, sz });
        visited.add(key(sx, sy, sz));

        int nodes = 0;
        
        int[][] directions = new int[][] {
            { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }
        };
        
        while (!queue.isEmpty() && nodes++ < maxNodes) {
        	
            int[] cur = queue.poll();
            int cx = cur[0], cy = cur[1], cz = cur[2];
            
            if (cx == tx && cy == ty && cz == tz)
            	return true;
            
            for (int[] dir : directions) {
            	
                int nxBase = cx + dir[0];
                int nzBase = cz + dir[1];
                
                int[] candidates = new int[] {
                    cy - 1, cy, cy + 1
                };
                
                for (int ny : candidates) {
                	
                    if (ny <= 0 || ny > world.getMaxHeight() - 2)
                    	continue;

                    String candidateKey = key(nxBase, ny, nzBase);
                    
                    if (visited.contains(candidateKey))
                    	continue;
                    
                    if (!world.isChunkLoaded(nxBase >> 4, nzBase >> 4))
                    	continue;
                    
                    if (!canStandAt(world, nxBase, ny, nzBase))
                    	continue;
                    
                    visited.add(candidateKey);
                    queue.add(new int[] { nxBase, ny, nzBase });
                    
                }
            }
        }
        
        return false;
        
    }

    /**
     * True if the block coordinate (x,y,z) is a valid place for the bot's feet:
     * - block at (x, y - 1, z) must be solid (ground)
     * - block at (x, y, z) must be non-solid (feet space)
     * - block at (x, y + 1, z) must be non-solid (head space)
     */
    public boolean canStandAt(World world, int x, int y, int z) {
    	
        if (!world.isChunkLoaded(x >> 4, z >> 4))
        	return false;
        
        Block ground = world.getBlockAt(x, y - 1, z);
        Block feet  = world.getBlockAt(x, y, z);
        Block head  = world.getBlockAt(x, y + 1, z);
        
        if (!ground.getType().isSolid())
        	return false;
        
        if (feet.getType().isSolid() || head.getType().isSolid())
        	return false;
        
        return true;
        
    }

    private static String key(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }
	
	public abstract FleXAITaskBuilder navigate(Location location);
	
	public abstract void roam(Location location, int radius);
	
}
