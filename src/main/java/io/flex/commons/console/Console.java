package io.flex.commons.console;

import java.util.ArrayList;
import java.util.List;

import io.flex.FleX.Task;
import io.flex.commons.NullObject;
import io.flex.commons.Severity;
import io.flex.commons.cache.cell.BiCell;

public class Console {
	
	public static final String[] BLOCKED_COMMANDS = {};
	public static final String[] IGNORE_COMMANDS = {
			
			"STOP", "RESTART",
			"RELOAD", "RL",
			
	};

	public static final List<BiCell<Throwable, String>> NOTICE_LOG = new ArrayList<BiCell<Throwable, String>>();
	public static final List<BiCell<Throwable, String>> ERROR_LOG = new ArrayList<BiCell<Throwable, String>>();
	
	public static BiCell<Throwable, String> log(String prefix, Severity severity, Throwable exception) {
		
		BiCell<Throwable, String> biCell = new BiCell<Throwable, String>() {

			private static final long serialVersionUID = -818345694612237399L;

			@Override
			public Throwable a() {
				return exception;
			}
			
			@Override
			public String b() {
				return "[Log] " + prefix + ": " + severity.name();
			}
			
		};
		
		if (severity == Severity.INFO || severity == Severity.NOTICE) {
			
			NOTICE_LOG.add(biCell);
			
			Task.print(biCell.b(), (severity == Severity.INFO ? "Info" : "Notice") + " logged. (Type \"notice\" here for more information)");
			
		} else {
			
			ERROR_LOG.add(biCell);
			
			Task.error(biCell.b(), "Stracktrace logged. (Type \"error\" here for more information)");
			
		}
		
    	return biCell;
    	
	}
	
	public static void print(BiCell<Throwable, String> log) {
		
		Throwable exception = log.a();
		String prefix = log.b();
		
		Task.error(prefix,

				"An error occurred (" + exception.getClass().getSimpleName() + "): " + (exception.getMessage() != null ? exception.getMessage() : "No further information."),
				"Refer to the following stacktrace" + (exception.getMessage() != null ? " for more information" : "") + ":");

		int here = -1;
		
		for (int i = 0; i < exception.getStackTrace().length; i++) {
			
			try {
				
				StackTraceElement element = exception.getStackTrace()[i];
				
				String file = element.getFileName().lastIndexOf('.') > 0 ?
						element.getFileName() : "(Class not found: " + element.getFileName() + ") " + NullObject.class.getName();
				
				String clazz = file.substring(0, file.lastIndexOf('.'));
				String method = (element.getMethodName().equals("<init>") ? clazz : "") + element.getMethodName() + "()";
				
				boolean fleXClass = element.getClassName().contains("io.flex") ||
						element.getClassName().contains("org.fukkit") || element.getClassName().contains("net.md_5.fungee");
				
				if (fleXClass && here == -1)
					here = i;
				
				Task.error(prefix, (i == 0 || here == i ? "HERE ->" : (i % 2 == 0 ? "|" : "^")) +
						" " + element.getClassName() + " | " + method + ":" + element.getLineNumber());
				
			} catch (Exception e) {
				
				Task.error(prefix, (i == 0 || here == i ? "HERE ->" : (i % 2 == 0 ? "|" : "^")) +
						" ERR_ELEMENT_LOST | METHOD_REQUIRE_ELEMENT: LINE_REQUIRE_ELEMENT");
				
			}
			
			
		}
		
		Task.error(prefix, "SUPPRESSED (" + exception.getSuppressed().length + ") <- ");
		
	}

}
