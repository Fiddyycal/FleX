package org.fukkit.cache;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import io.flex.FleX.Task;
import io.flex.commons.utils.NumUtils;
import io.flex.commons.utils.StringUtils;

public class NameCache extends ArrayList<String> {
	
	private static final int start_at = 200000, end_at = 500000;
	
	private static final long serialVersionUID = -5745132082672710556L;
	
	public String getRandom() {
		return this.isEmpty() ? StringUtils.generate(12, true) : this.get(NumUtils.getRng().getInt(0, this.size()-1));
	}
	
	public boolean load() {
	
		Task.print("Disguise", "Pre-loading names for efficient disguising...");
		
		File names = new File("flex" + separator + "data" + separator + "local" + separator + "disguises", "disguise_names.dat");
		
		if (!names.getParentFile().exists())
			names.getParentFile().mkdirs();
		
		if (!names.exists()) {
			try {
				names.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			
			Stream<String> stream = Files.lines(names.toPath(), StandardCharsets.UTF_8);
	        
	        Iterator<String> iterator = stream.iterator();
	        
	        boolean start = false;
	        
	        int i = 0;
            
	        while(iterator.hasNext()) {
	        	
	            String line = iterator.next();
	            
	            i++;
	            
	            if (i == start_at) {
	            	start = true;
	            	continue;
	            }
	            
	            if (i == end_at)
	            	break;
	            
	            if (start)
					this.add(line);
	            
	        }
	        
	        stream.close();
            
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Task.print("Disguise", "Done!");
		return true;
		
	}

}
