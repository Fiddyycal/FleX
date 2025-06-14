package org.fukkit.api.helper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.flex.FleX.Task;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;
import io.flex.commons.utils.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.UUID;

import org.fukkit.Fukkit;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.PlayerFactory;

@SuppressWarnings("deprecation")
public class MojangHelper {
	
	public static final String DEFAULT_VALUE = "eyJ0aW1lc3RhbXAiOjE1Njk4MjEwNDkzNjksInByb2ZpbGVJZCI6ImZmNmU0ZjcxOWM3YjQwMWQ5MGUwYjEzNzY1ZjZjOGYzIiwicHJvZmlsZU5hbWUiOiI1T2NhbCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU5NGQ4Mjc2YjI3ZWRhYWVkODY4ZWM1NWU3ZGFkMGRhNjJlMmIwNDRmOWUyOWU2MzlkMTU0MzY4YTUwOTA5In19fQ==";
	public static final String DEFAULT_SIGNATURE = "hKYlbfeX792HgNd032JemHdr6zddb1RPfVt+tTsJ/qw5nlHjbNiElR8X/LPr96Z7iEA7tPBGr3arY8Jzwgr6GaE8pBPbZzGg0OxnZ7H7dbDN0tfdZw9CjWevHEpl7OtWEXwXTHPEpwW/OO7HnGcuPPHusf5PYNpQpcP5T3cNmaiYqegdkQuQKNIlYfy8WThCPNyDSYFX4yZIJ5euWx1W+su+eMCRwnThFCeuYS6hViz8tk0ahpvXPoJeOfTHuFdnhcw9+vCezDr1/yP5BmuM2fAmIMP4yy4TXcH8IwdZoz3UJUS58JQd9PHXp5nFo2chqAtGta6Gc8c9JH/C1ZjC8sD1+OPUNqfyoHDco4cSdJB0cCtrdIvatbRqgDMEETACZJNOs+noNZLaWtz+x+UNAzSxU0oIqEVIySsx/Ke7CS4Zxt1FjHGbmx04gjaQou3cSyPOTLve4elnFywdxuM/EYcHewYONsGKptOUa6fLNa+JwR3uUIBXbXK3ci1uagvW86nVR1U11GRi/+Ds66xOGHKk5z1+ofkfWjQReOdadUG01T2S+YwqA4x+zVVkG39qTgDjms9b10OvP752OLw1CdIRp29WJYeE2qp/Je5LJrqwzgvjKzkBN82huo14cQDEWw8RZE193+liP8jMbXIZOEIHmep/7JlM1MqMQbQWav8=";
	
	public static final String EMPTY_VALUE = "eyJ0aW1lc3RhbXAiOjE1NzgwMDczNTc0OTEsInByb2ZpbGVJZCI6ImIzYjE4MzQ1MzViZjRiNzU4ZTBjZGJmMGY4MjA2NTZlIiwicHJvZmlsZU5hbWUiOiIxMDExMTEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I0NTVjMTRhZjU4MjFhNDVhNGMzOTc0MWNiMTY2ZGE2ZDlmMzQwYWI5YzBjNTgyMWE5M2I2NzYxZTFiNGQwODcifX19";
	public static final String EMPTY_SIGNATURE = "P82RrzaxK3ivIs79lWuKP6tVvcSwgHS+kyWW1CN/GQh1mcBR1YqtVjpmIJmNzG3qD1TaYMV+qVFfe291EmtRFa2/jCF0neWG79Nvz1HVcDXalqrKgZFHqF1nOfI5YPUgScTl4TNwUlGPQLRlpGmW55ka8vmIKEiLxg3LQXaGLjq370+Bqj45ZjjyUCODS3D2Fg8wX4Ku0hZlZ1ro2H6IIs0BekSsKqGEzw/yeupmDArbU6B6HT5RVvv/23hZYVWwbI5QVF+2gfoj1LfQupF12fa+VtGlS5KfIB1/D6ZcQnIQtCyX8ULr1LMa55eIlwvOBFHkosTQ/ioR7hpXxvz2nc3Lnptwt1gIjjdb1oUr/yuYksLsRpDGGcH9y1tkLhxCvlLawoo/RiH69+cY+m9a1tM9HJeNgj29YWHZLTydxfkt6QZaWWmRGvEywRVfBwxA65yubcod0qeoUzMhpI7Rr5wrPt6RH8iRgaIIqE2sST8F5SFRyrscxgbaPsKlJFjbccAilWiC3o6aIiZftnALdo4Rv8rO72Jq/ATp1PzbCxR12StP1aiRB8Z3ipis7yDa/EKBQcag1ZPni+halgOME7ih2+JLm9h3jV3e5DN667fn1m/nYT+R1C4Kbru4DRb0PLBVBwv//QyI/FVD2lK1/8ppMzqK312hp8P/J7mnyh0=";
	
	public static UUID getUid(String name) {
		
		try {
			
			URL api = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
	        
	        InputStreamReader apiReader = new InputStreamReader(api.openStream());
	        JsonObject apiObject = new JsonParser().parse(apiReader).getAsJsonObject();
	        
	        return StringUtils.toUUID(apiObject.get("id").getAsString());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
			
		}
		
	}
	
    public static FleXSkin getSkin(String name) {
    	
    	URLConnection apiServer = null;
    	
    	String uuid = null;
    	
    	FleXSkin skin = null;
    	
        try {
        	
            URL api = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            apiServer = api.openConnection();
            
            InputStreamReader apiReader = new InputStreamReader(api.openStream());
            JsonObject apiObject = new JsonParser().parse(apiReader).getAsJsonObject();
            
            uuid = apiObject.get("id").getAsString();
			skin = getSkin(StringUtils.toUUID(uuid));
            
        } catch (IOException e) {
        	
        	skin = fetchDefault(uuid != null ? UUID.fromString(uuid) : null, apiServer, null);
        	
        } catch (IllegalStateException e) {}
        
        return skin;
        
    }
    
	public static FleXSkin getSkin(UUID uuid) {
    	
    	Objects.requireNonNull(uuid, "uuid must not null");
    	
    	URLConnection sessionServer = null;
    	
    	FleXSkin skin = null;
    	
        try {
        	
            URL session = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false");
            sessionServer = session.openConnection();
        	
            InputStreamReader sessionReader = new InputStreamReader(session.openStream());
            JsonObject sessionObj = new JsonParser().parse(sessionReader).getAsJsonObject();
            JsonObject property = sessionObj.get("properties").getAsJsonArray().get(0).getAsJsonObject();
        	
            String name = sessionObj.get("name").getAsString();
            String value = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
        	
            skin = Fukkit.getImplementation().createSkin(uuid.toString(), value, signature);
        	
            PlayerFactory factory = Fukkit.getPlayerFactory();
        	
            try {
            	
            	if (Fukkit.getPlayer(uuid) == null)
                	factory.createFukkitSafe(uuid, name, skin);
            	
			} catch (Exception e) {
	        	Task.error("Mojang API", "Failed to update player skin for " + skin.getName() + ".");
	        	Console.log("Mojang API", Severity.ERROR, e);
			}
        	
        } catch (IOException e) {
            
        	skin = fetchDefault(uuid, null, sessionServer);
        	
        } catch (IllegalStateException e) {}
        
        return skin;
        
    }
    
    private static FleXSkin fetchDefault(UUID uuid, URLConnection apiServer, URLConnection sessionServer) {
    	
    	HttpURLConnection connection = (HttpURLConnection) (apiServer != null ? apiServer : sessionServer != null ? sessionServer : null);
    	
    	int code = -1;
    	
    	String error = "Could not get skin from mojang";
    	String from = apiServer != null ? "API server" : "Session servers";
    	String offline = apiServer != null ? "Is it" : "Are they";
    	
    	if (connection != null)
    		
			try {
				code = connection.getResponseCode();
			} catch (IOException e) {
				
		    	String cause = "Failed to extract HTTP response code from mojang " + from + ": " + offline + " offline?";
		    	
	        	Task.error("Mojang API", cause);
	        	Console.log("Mojang API", Severity.ERROR, new IOException(cause));
	        	
			}
    	
    	String cause = error + ": " + (code != -1 ? from + " returned HTTP response code " + code + "." : "no further information:");
    	
    	Task.error("Mojang API", cause);
    	Console.log("Mojang API", Severity.WARNING, new IOException(cause));
    	
    	Task.print("Mojang API", "Searching FleX for logged skin...");
    	
    	try {
        	
        	FleXPlayer player = uuid != null ? PlayerHelper.getPlayerSafe(uuid) : null;
			
        	return player.getSkin();
        	
		} catch (NoClassDefFoundError | NullPointerException e) {
			
			Task.error("Mojang API", "Skin not found, returning default skin signature.");
			
        	return Fukkit.getImplementation().createSkin(String.valueOf(new char[]{ String.valueOf(3 + 2).charAt(0), 79, 99, 97, 108 }), DEFAULT_VALUE, DEFAULT_SIGNATURE);
        	
		}
    	
    }

}
