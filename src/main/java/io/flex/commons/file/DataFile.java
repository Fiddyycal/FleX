package io.flex.commons.file;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.flex.FleX.Task;
import io.flex.commons.Nullable;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;
import io.flex.commons.utils.FileUtils;

@SuppressWarnings("unchecked")
public class DataFile<T extends Serializable> extends File implements Serializable {
	
	private static final long serialVersionUID = 2051346415549572835L;
	
	private static final String unique_identifier = "t";
	
	private Map<String, Serializable> write = new HashMap<String, Serializable>();
	
	private boolean fresh;
	
	public DataFile(String path, T write, boolean overwrite) {
		this(path, null, write, overwrite);
	}
	
	public DataFile(String path) {
		this(path, null);
	}
	
	public DataFile(String path, @Nullable String name, T write, boolean overwrite) {
		
		this(path, name);
		
		if (this.fresh || overwrite)
			this.write(write);
		
	}
	
	public DataFile(String path, @Nullable String name) {
		
		super(fileAbsolutePath(path, name));
		
		boolean exists = (name != null ? new File(path, name) : new File(path)).exists();
		
		FileUtils.getFile(path, name, this.fresh = !exists);
		
		if (!this.fresh && exists) {
			try {
	        	this.update();
			} catch (IOException | ClassNotFoundException e) {}
		}
		
	}
	
	private static String fileAbsolutePath(String path, String name) {
		
		boolean dir = path.endsWith(File.separator) || path.endsWith("/") || path.endsWith("\\");
		
		if (dir && name == null)
			throw new UnsupportedOperationException("DataFile failed, path is present but no file name was provided, please revise.");
			
		return dir ? path + name : (path + (name != null ?  File.separator + name : ""));
			
	}
	
	public <V extends Serializable> V getTag(String tag, V def) {
		
		if (tag.equalsIgnoreCase(unique_identifier))
			throw new UnsupportedOperationException("Cannot use the tag \"" + tag + "\", please use #read() instead.");
		
		return this.write.containsKey(tag) ? (V) this.write.get(tag) : def;
		
	}
	
	public <V extends Serializable> V getTag(String tag) {
		return (V) this.write.get(tag);
	}
	
	public <V extends Serializable> void setTag(String tag, V value) {
		
		if (tag.equalsIgnoreCase(unique_identifier))
			throw new UnsupportedOperationException("Cannot use the tag \"" + tag + "\", please use #write() instead.");
		
		this.write.put(tag, value);
		
		this.save();
		
	}
	
	public T read() {
		
		if (!this.write.containsKey(unique_identifier)) {
			Console.log("Cabinet", Severity.ERROR, new NullPointerException("No object was found written to file"));
			return null;
		}
		
        try {
        	
        	this.update();
        	
            return (T) this.write.get(unique_identifier);
            
		} catch (ClassNotFoundException | IOException e) {
			
	    	Task.error("Cabinet (" + Severity.CRITICAL.name() + ")", "Object could not be read in file " + this.getAbsolutePath() + ".");
	    	Console.log("Cabinet", Severity.CRITICAL, e);
	    	
		}
        
		return null;
		
	}
	
	private void update() throws ClassNotFoundException, IOException {
		
		try (
		        FileInputStream fileIn = new FileInputStream(this);
		        ObjectInputStream objectIn = new ObjectInputStream(fileIn)
		    ) {
			
			Object obj = objectIn.readObject();
			
			if (obj instanceof Map)
				this.write = (HashMap<String, Serializable>) obj;
			
		    else {
		    	
		    	Task.error("Cabinet", "Unexpected root object type: " + obj.getClass().getName() + " in file (" + this.getAbsolutePath() + "). Resetting to new Map.");
		    	Task.error("Cabinet", "This file may have lost crucial tags, please review.");
		        
		    	Console.log("Cabinet", Severity.WARNING, new Throwable("Corrupt file may have lost crucial tags, please review: " + this.getAbsolutePath()));
		    	
		        this.write = new HashMap<String, Serializable>();
		        this.write.put(unique_identifier, (T) obj);
		        
		    }
			
		}
        
	}
	
	public void write(T write) {
		
		requireNonNull(write, "write must not be null");
		
		this.write.put(unique_identifier, write);
		
		this.save();
		
	}
	
	public boolean isFresh() {
		return this.fresh;
	}
	
	public void save() {
		
		try (
				
				FileOutputStream fileOut = new FileOutputStream(this);
				ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
				
			) {
    		
		    	objectOut.writeObject(this.write);
		    
		    	objectOut.flush();
		    	objectOut.close();
		    	fileOut.close();
    	    
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
		
	}
	
	public File zip() {
		
        File zipped = new File(this.getParentFile(), this.getName() + ".zip");

        try (
            FileOutputStream fos = new FileOutputStream(zipped);
            ZipOutputStream zipOut = new ZipOutputStream(fos)
        ) {
            if (this.isDirectory())
                this.zipDir(this, this.getName(), zipOut);
                
            else this.zipFile(this, "", zipOut);
            
        } catch (IOException e) {
        	
	    	Task.error("Cabinet (" + Severity.CRITICAL.name() + ")", "Failed to zip: " + this.getAbsolutePath());
	    	Console.log("Cabinet", Severity.CRITICAL, e);
            
        }
        
        return zipped;
        
    }

    private void zipFile(File fileToZip, String parentPath, ZipOutputStream zipOut) throws IOException {
    	
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
        	
            String zipEntryName = parentPath.isEmpty() ? fileToZip.getName() : parentPath + "/" + fileToZip.getName();
            
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            
            zipOut.putNextEntry(zipEntry);
            
            byte[] buffer = new byte[1024];
            
            int len;
            
            while ((len = fis.read(buffer)) >= 0)
                zipOut.write(buffer, 0, len);
            
        }
        
    }

    private void zipDir(File folder, String parentPath, ZipOutputStream zipOut) throws IOException {
    	
        File[] files = folder.listFiles();
        
        if (files == null || files.length == 0) {
        	
            zipOut.putNextEntry(new ZipEntry(parentPath + "/"));
            zipOut.closeEntry();
            return;
            
        }

        for (File file : files) {
        	
            if (file.isDirectory())
                this.zipDir(file, parentPath + "/" + file.getName(), zipOut);
                
            else this.zipFile(file, parentPath, zipOut);
            
        }
        
    }
	
	public void setContents(String recource) {
		FileUtils.copyResource(recource, this.getAbsolutePath());
	}
	
	public Map<String, Serializable> asTags() {
		return this.write.entrySet().stream().filter(e -> !e.getKey().equalsIgnoreCase(unique_identifier)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}
	
}
