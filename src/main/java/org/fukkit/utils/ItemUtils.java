package org.fukkit.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.fukkit.Fukkit;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import io.flex.FleX.Task;
import io.flex.commons.utils.StringUtils;

public class ItemUtils {
	
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
    	try {
    		
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            dataOutput.writeInt(items.length);
            
            for (int i = 0; i < items.length; i++)
                dataOutput.writeObject(items[i]);
            
            dataOutput.close();
            
            return Base64Coder.encodeLines(outputStream.toByteArray());
            
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    
    public static String serialize(ItemStack item) {
    	
    	List<String> values = new ArrayList<String>();
		
		for (Entry<String, Object> serialized : item.serialize().entrySet())
			values.add(serialized.getKey() + "=" + (serialized.getKey().equals("type") ? serialized.getValue().toString().replace("LEGACY_", "") : serialized.getValue()));
		
		return StringUtils.join(values, "|");
        
    }
    
    public static String serializeB64(ItemStack item) throws IllegalStateException {
    	
    	try {
    		
    		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            dataOutput.writeObject(item);
            dataOutput.close();
            
            return Base64Coder.encodeLines(outputStream.toByteArray());
    		
    	} catch (IOException e) {
    		
    		if (e instanceof NotSerializableException) {
    			
    			Task.error("Serialize", "An error occurred attempting to serialize item: " + NotSerializableException.class.getName());
    			Task.error("Serialize", "Serializing into a readable string (Non-Base64).");
    			
    			return serialize(item);
    			
    		}
    			
    		throw new IllegalStateException("Unable to serialize item", e);
    		
		}
		
    }
    
    @Deprecated
    public static ItemStack deserialize(String data) {
        
    	/** TODO
    	 * v:3120type:LEGACY_GOLDEN_APPLEmeta:UNSPECIFIC_META:{meta-type=UNSPECIFIC, display-name={"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"aqua","text":"Luminous"}],"text":""}, lore=[{"extra":[{"bold":false,"italic":true,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_gray","text":"FleX Engine"}],"text":""}, {"text":""}, {"extra":[{"bold":true,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"aqua","text":"L"},{"bold":true,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"white","text":"MC "},{"bold":true,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"aqua","text":">>"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"text":"Server Prefix"}],"text":""}, {"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"aqua","text":"Quaternary Color"}],"text":""}, {"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"yellow","text":"Primary Valuing"}],"text":""}, {"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_gray","text":"Primary Punctuation"}],"text":""}, {"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"text":"Reset"}],"text":""}, {"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"Lore"}],"text":""}, {"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gold","text":"Clickable"}],"text":""}, {"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"aqua","text":"Clock"}],"text":""}, {"extra":[{"bold":true,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"aqua","text":"? "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"white","text":"Information Sidebar"}],"text":""}, {"text":""}, {"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"Actual appearance may differ due"}],"text":""}, {"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"to manual"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_gray","text":"/"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"conditional input"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"dark_gray","text":"."}],"text":""}]}
    	 */
        return null;
        
    }
    
    public static ItemStack deserializeB64(String data) {

		if (data == null)
			return null;
		
    	try {
    		
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            
            return item;
    		
    	} catch (IOException | ClassNotFoundException e) {
    		
    		if (e instanceof ClassNotFoundException)
                throw new IllegalStateException("Unable to decode class type.", e);
    		
    		if (e instanceof NotSerializableException) {
    			
    			Task.error("Serialize", "An error occurred attempting to serialize item: " + NotSerializableException.class.getName());
    			Task.error("Serialize", "Serializing into a readable string (Non-Base64).");
    			
    			return deserialize(data);
    			
    		}
			
		    throw new IllegalStateException("Unable to deserialize item", e);
    		
		}
    	
    }
    
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
    	try {
    		
    		if (data == null)
    			return null;
    		
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
            
            for (int i = 0; i < items.length; i++) {
            	
            	try {
            		
            		ItemStack item = (ItemStack) dataInput.readObject();
                	
                	items[i] = item != null && item.getType() != null ? item : null;
                	
				} catch (NullPointerException | IOException e) {
					
					System.out.println("null/IOException: " + e.getMessage() + ". (Returning null)");
					
					items[i] = null;
					
				}
            	
            	
            }
            
            dataInput.close();
            return items;
            
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
    
	public static ItemMeta makeUnbreakable(ItemMeta meta, boolean unbreakable) {
		return Fukkit.getImplementation().setItemUnbreakable(meta, unbreakable);
	}

	public static boolean unbreakable(ItemMeta meta) {
		return Fukkit.getImplementation().isItemUnbreakable(meta);
	}
    
}
