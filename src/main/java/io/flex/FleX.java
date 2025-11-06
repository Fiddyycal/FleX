package io.flex;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;

import io.flex.commons.Nullable;
import io.flex.commons.utils.ArrayUtils;

import static java.io.File.separator;

public class FleX {
	
	public static final String[] FLEX = {

            ">                                                                                            ",
            "> FFFFFFFFFFFFFFFFFFFFFF LLLLLLLLLLL            EEEEEEEEEEEEEEEEEEEEEE XXXXXXX        XXXXXXX",
            "> F::::::::::::::::::::F L:::::::::L            E::::::::::::::::::::E X:::::X        X:::::X",
            "> FF::::::FFFFFFFFF::::F LL:::::::LL            EE::::::EEEEEEEE:::::E X:::::X        X:::::X",
            ">   F:::::F       FFFFFF   L:::::L                E:::::E       EEEEEE XXX::::X      X::::XXX",
            ">   F:::::F                L:::::L                E:::::E                X::::::X  X::::::X  ",
            ">   F::::::FFFFFFFFFF      L:::::L                E::::::EEEEEEEEEE        X:::::XX:::::X    ",
            ">   F:::::::::::::::F      L:::::L                E:::::::::::::::E          X::::::::X      ",
            ">   F::::::FFFFFFFFFF      L:::::L                E::::::EEEEEEEEEE        X:::::XX:::::X    ",
            ">   F:::::F                L:::::L                E:::::E                X::::::X  X::::::X  ",
            ">   F:::::F                L:::::L       LLLLLL   E:::::E       EEEEEE XXX::::X      X::::XXX",
            "> FF:::::::FF            LL::::::LLLLLLLL:::::L EE::::::EEEEEEEE:::::E X:::::X        X:::::X",
            "> F:::::::::F            L::::::::::::::::::::L E::::::::::::::::::::E X:::::X        X:::::X",
            "> FFFFFFFFFFF            LLLLLLLLLLLLLLLLLLLLLL EEEEEEEEEEEEEEEEEEEEEE XXXXXXX        XXXXXXX",
            ">                                                                                            "
            
	};
	
	public static final boolean COMPILED_USING_MAVEN = FleX.getResource("META-INF/maven/io.flex/flex-commons/pom.xml") != null;
	
	public static final String FLEX_JAR_PATH_ABSOLUTE = FleX.class.getProtectionDomain().getCodeSource().getLocation().toString();
	public static final String EXE_PATH = System.getProperty("user.dir").replace("/", separator);
	public static final String LOCALHOST_IP = retrieveIp();
	
	public static final String[] PRODUCT_KEYS = { "EYPCZ-ZHFN1-FQ6OR-IXWK0-KEDFM" };
	
	private static String retrieveIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new UnsupportedOperationException("An error occurred while attempting to retrieve local ip: " + e.getMessage());
		}
	}
	
	public static boolean isProductKey(String key, boolean validate) {
		
		boolean valid = key.charAt(5) == '-' && key.charAt(11) == '-' && key.charAt(17) == '-' && key.charAt(23) == '-';
		
		if (validate)
			valid = valid && ArrayUtils.contains(PRODUCT_KEYS, key);
		
		return valid && key.equals(key.toUpperCase());
		
	}
	
	public static InputStream getResourceAsStream(String name) {
		return FleX.class.getClassLoader().getResourceAsStream(name.replace(separator, "/"));
	}
	
	public static URL getResource(String name) {
		return FleX.class.getClassLoader().getResource(name.replace(separator, "/"));
	}
	
	public static class Task {
		
		private static boolean debug = false;
		
		public static void debug(@Nullable String prefix, @Nullable String... output) {
			if (debug) {
				
				if (output == null || output.length == 0)
					System.out.println("[Debug] " + prefix);
					
				Arrays.stream(output).forEach(op -> System.out.println((prefix != null ? "[" + prefix + "] " : "") + op));
				
			}
		}
		
		public static void error(@Nullable String prefix, @Nullable String... output) {
			
			if (output == null || output.length == 0)
				System.err.println(prefix);
			
			else Arrays.stream(output).forEach(op -> System.err.println((prefix != null ? "[" + prefix + "] " : "") + op));
			
		}
		
		public static void print(@Nullable String prefix, @Nullable String... output) {
			
			if (output == null || output.length == 0)
				System.out.println(prefix);
			
			else Arrays.stream(output).forEach(op -> System.out.println((prefix != null ? "[" + prefix + "] " : "") + op));
			
		}
		
		public static <T extends Throwable> T throw_(T exception) {
			try {
				throw exception.getClass().newInstance();
			} catch (Throwable e) {
				e.printStackTrace();
				return exception;
			}
		}
		
		public static <T extends Throwable> T throw_(T exception, String message) {
			try {
				throw exception.getClass().getConstructor(String.class).newInstance(message);
			} catch (Throwable e) {
				e.printStackTrace();
				return exception;
			}
		}
		
		public static Throwable try_(Runnable try_) {
			try {
				try_.run();
				return null;
			} catch (Throwable e) {
				return e;
			}
		}
		
		public static Throwable try_Catch(Runnable try_, Runnable catch_) {
			try {
				try_.run();
				return null;
			} catch (Throwable e) {
				catch_.run();
				return e;
			}
		}
		
		public static boolean enableDebugMode(boolean enable) {
			return Task.debug = enable;
		}
		
		public static boolean isDebugEnabled() {
			return Task.debug;
		}
		
	}

}
