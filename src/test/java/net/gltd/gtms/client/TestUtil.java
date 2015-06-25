package net.gltd.gtms.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jivesoftware.util.ClassUtils;

public class TestUtil {

	public static final String DEFAULT_LOG_LEVEL = "INFO";
	public static final String DEFAULT_DEBUG_CONVERSION_PATTERN = "%d{ISO8601} %-5p %-7c{1} (%C:%M:%L) - %m%n";

	private TestUtil() {
	}

	public static Properties getProperties(Class clazz, String filename) throws FileNotFoundException, IOException {
		Properties properties = new Properties();

		InputStream inputStream = clazz.getClassLoader().getResourceAsStream(filename);
		if (inputStream != null) {
			properties.load(inputStream);
		} else {
			throw new FileNotFoundException("Property file " + filename + " not found in classpath");
		}
		return properties;
	}

	/**
	 * Reads a file and returns it as a string.
	 * 
	 * @param filename
	 *            the name of the file to open.
	 */
	public static String readFileAsString(String filename) throws IOException, FileNotFoundException {
		InputStream is = ClassUtils.getResourceAsStream(filename);
		StringBuilder stringBuilder = new StringBuilder();
		Scanner scanner = new Scanner(is);
		try {
			while (scanner.hasNextLine()) {
				stringBuilder.append(scanner.nextLine());
			}
		} finally {
			scanner.close();
		}
		return stringBuilder.toString();
	}

	public static Logger initializeConsoleLogger(String loggerName, String pattern, String logLevel) {
		Logger logger = Logger.getLogger(loggerName);
		return initializeConsoleLogger(logger, pattern, logLevel);
	}

	public static Logger initializeConsoleLogger(Class clazz, String pattern, String logLevel) {
		Logger logger = Logger.getLogger(clazz);
		return initializeConsoleLogger(logger, pattern, logLevel);
	}

	public static Logger initializeConsoleLogger(Logger logger, String pattern, String logLevel) {
		try {
			if (!TestUtil.validString(logLevel)) {
				logLevel = TestUtil.DEFAULT_LOG_LEVEL;
			}
			if (!TestUtil.validString(pattern) && ("ALL".equals(logLevel) || "DEBUG".equals(logLevel))) {
				pattern = TestUtil.DEFAULT_DEBUG_CONVERSION_PATTERN;
			}
			ConsoleAppender appender = new ConsoleAppender(new PatternLayout(pattern));
			logger.setLevel(Level.toLevel(logLevel, Level.toLevel(TestUtil.DEFAULT_LOG_LEVEL)));
			logger.addAppender(appender);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logger;
	}

	public static boolean validString(String str) {
		boolean result = false;
		if (str != null && !str.equals("") && !str.equals("null")) {
			result = true;
		}
		return result;
	}

}
