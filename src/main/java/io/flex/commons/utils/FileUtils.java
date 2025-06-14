package io.flex.commons.utils;

import static java.io.File.separator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.flex.FleX;
import io.flex.FleX.Task;
import io.flex.commons.Nullable;

public class FileUtils {
	
	public static final int BUFFER_SIZE = 4096;

	public static File getFile(String path, @Nullable String name, boolean createIfNull) {
		
		path = path != null ? path.endsWith(separator) ? path : path + separator : separator;
		path = path.replace("/", separator)
				   .replace(separator + separator + separator + separator, separator)
				   .replace(separator + separator + separator, separator)
				   .replace(separator + separator, separator);
		
		try {
			
			File file = name != null ? new File(path, name) : new File(path);
			
			if (!file.getParentFile().exists())
				Task.debug("Cabinet", "Directory " + path + " doesn't exist.");
			
			else if (!file.exists())
				Task.debug("Cabinet", "File \"" + file.getName() + "\" at " + path + " doesn't exist.");
			
			if (createIfNull && (!file.getParentFile().exists() || !file.exists())) {
				Task.debug("Cabinet", "Creating \"" + file.getName() + "\" at " + path);
				
	            if (!file.getParentFile().exists())
	            	file.getParentFile().mkdirs();
				
	    		if (!file.exists())
	    			file.createNewFile();
	    		
			}
			
			return file;
    		
        } catch(SecurityException | IOException e) {
        	e.printStackTrace();
        }
		
		return null;
		
	}
	
	public static File getFile(String absolutePath, boolean createIfNull) {
		return getFile(absolutePath, null, createIfNull);
	}
	
	public static void copyResource(String source, String destination) {
		try {
			
			source = source.replace("/", separator)
					   .replace(separator + separator + separator + separator, separator)
					   .replace(separator + separator + separator, separator)
					   .replace(separator + separator, separator);
			
			InputStream input = FleX.getResourceAsStream(source);
			
			if (input == null) {
				Task.error("Cabinet", "Resource " + source + " doesn't exist.");
				return;
			}
			
			File file = new File(destination);
			
			if (!file.getParentFile().exists())
	        	file.getParentFile().mkdirs();
			
			if (!file.exists())
				file.createNewFile();
			
			copy(input, file);
			
			input.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void copy(File source, File target, String... ignore) {
		try {
			
		    if (ArrayUtils.contains(ignore, source.getName()))
		    	return;
		    
		    if (source.isDirectory()) {
	        	
	            if (!target.exists())
	            	target.mkdirs();
	            
	            String[] files = source.list();
	            
	            for (String file : files) {
	            	
	            	File srcFile = new File(source, file);
	                File destFile = new File(target, file);
	                
	                copy(srcFile, destFile, ignore);
	                
	            }
	            
	        } else copy(new FileInputStream(source), target);
			
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	public static void copy(InputStream stream, File target) {
		try {
			
			OutputStream out = new FileOutputStream(target);
            
            byte[] buffer = new byte[FileUtils.BUFFER_SIZE/4];
            int length;
            
            while ((length = stream.read(buffer)) > 0)
            	out.write(buffer, 0, length);
            
            stream.close();
            
            out.flush();
            out.close();
			
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	public static void move(File source, File target, String... ignoreFiles) {
		copy(source, target, ignoreFiles);
		delete(source);
	}
	
    public static void unzip(File source, String destination) {
    	try {
    		
        	ZipFile zipFile = new ZipFile(source);
        	
    		Enumeration<? extends ZipEntry> entries = zipFile.entries();
    		
    		while (entries.hasMoreElements()) {
    			
    			ZipEntry entry = entries.nextElement();
    			File entryDestination = new File(destination, entry.getName());
    			
    	        if (entry.isDirectory()) {
    	            entryDestination.mkdirs();
    	        
    	        } else {
    	        	
    	            entryDestination.getParentFile().mkdirs();
    	            
    	            copy(zipFile.getInputStream(entry), entryDestination);
    	            
    	        }
    	    
    	    }
    		
    	    zipFile.close();
    	  
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	public static void unzipResource(String source, String destination) {
		try {
			
    		source = source.replace("/", separator)
					   .replace(separator + separator + separator + separator, separator)
					   .replace(separator + separator + separator, separator)
					   .replace(separator + separator, separator);
    		
			InputStream stream = FleX.getResourceAsStream(source);
		    File zip = new File(destination, "temp-" + StringUtils.generate(6, false) + ".zip");
		    
		    if (!zip.getParentFile().exists())
			    zip.getParentFile().mkdirs();
		    
		    if (!zip.exists())
			    zip.createNewFile();
		    
			copy(stream, zip);
			unzip(zip, destination);
			
			stream.close();
			zip.delete();
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean delete(File... files) {
		
		if (files == null || files.length == 0)
			return false;
		
		return Arrays.stream(files).allMatch(f -> {
			
	    	File[] list = f.listFiles();
			
			if (list != null && list.length > 0) {
		    	
		        for (File file : list)
		        	delete(file);
		        
		    }
			
			String parent = f.getParentFile().getName();
			
			Task.debug("Cabinet", "Deleting file " + f.getName() + " in " + (parent.equals(".") ? "<" + File.separator + "." + File.separator + ">" : parent) + "...");
        	
		    return (f.exists() && f.delete()) || !f.exists();
		    
		});
	    
	}
	
	public static long getMillis(String timeStamp) {
		
		try {
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
			Date date = null;
			
			try {
				
				date = format.parse(timeStamp);
				
			} catch (ParseException e) {
				
				format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
				date = format.parse(timeStamp);
				
			}
			
			return date.getTime();
			
		} catch (ParseException e) {
			
			e.printStackTrace();
			return -1;
			
		}
		
	}
	
	public static String getTimeStamp(long ms, boolean fileFriendly) {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH" + (fileFriendly ? "-" : ":") + "mm" + (fileFriendly ? "-" : ":") + "ss");
		Date date = new Date(ms);
		
		return format.format(date);
		
	}
	
	public static String getTimeStamp(long ms) {
		return getTimeStamp(ms, true);
	}
	
	public static String getTimeStamp() {
		return getTimeStamp(System.currentTimeMillis(), true);
	}
	
}
