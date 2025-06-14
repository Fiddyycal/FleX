package io.flex;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

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
	
	public static final String[] PRODUCT_KEYS = { "EYPCZ-ZHFN1-FQ6OR-IXWK0-KEDFM" };
	
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
		
		public static <T> void debug(T output) {
			if (debug) System.out.println("[Debug] " + output);
		}
		
		@SafeVarargs
		public static <T> void debug(String prefix, T... output) {
			if (debug) Arrays.stream(output).forEach(op -> System.out.println("[Debug/" + prefix + "] " + op));
		}
		
		public static <T> void error(T output) {
			System.err.println(output);
		}

		@SafeVarargs
		public static <T> void error(String prefix, T... output) {
			Arrays.stream(output).forEach(op -> System.err.println("[" + prefix + "] " + op));
		}
		
		public static <T> void print(T output) {
			System.out.println(output);
		}

		@SafeVarargs
		public static <T> void print(String prefix, T... output) {
			Arrays.stream(output).forEach(op -> {
				System.out.println("[" + prefix + "] " + op);
			});
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
