package silence.simsool.lucent.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.enums.LogLevel;

public class ULog {
	private static String NAME = Lucent.NAME;
	private static Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static Logger getLogger() {
		return LOGGER;
	}

	public static void setLogger(String name) {
		NAME = name;
		LOGGER = LoggerFactory.getLogger(NAME);
	}

	public static void custom(String message, LogLevel level) {
		print(getPrefix(level) + message);
	}

	public static void print(String message) {
		System.out.println(message);
	}

	public static void printc(String message) {
		print(getPrefix() + message);
	}

	public static void log(String message, LogLevel level) {
		switch (level) {
			case INFO -> info(message);
			case WARN -> warn(message);
			case ERROR -> error(message);
			case DEBUG -> debug(message);
			case TRACE -> trace(message);
		}
	}

	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void warn(String message) {
		LOGGER.warn(message);
	}

	public static void error(String message) {
		LOGGER.error(message);
	}

	public static void debug(String message) {
		LOGGER.debug(message);
	}

	public static void trace(String message) {
		LOGGER.trace(message);
	}

	public static void info(String message, Throwable t) {
		LOGGER.info(message, t);
	}

	public static void warn(String message, Throwable t) {
		LOGGER.warn(message, t);
	}

	public static void error(String message, Throwable t) {
		LOGGER.error(message, t);
	}

	public static void debug(String message, Throwable t) {
		LOGGER.debug(message, t);
	}

	public static void trace(String message, Throwable t) {
		LOGGER.trace(message, t);
	}

	public static void info(String format, Object... args) {
		LOGGER.info(format, args);
	}

	public static void warn(String format, Object... args) {
		LOGGER.warn(format, args);
	}

	public static void error(String format, Object... args) {
		LOGGER.error(format, args);
	}

	public static void debug(String format, Object... args) {
		LOGGER.debug(format, args);
	}

	public static void trace(String format, Object... args) {
		LOGGER.trace(format, args);
	}

	public static void info(String format, Throwable t, Object... args) {
		LOGGER.info(format, args, t);
	}

	public static void warn(String format, Throwable t, Object... args) {
		LOGGER.warn(format, args, t);
	}

	public static void error(String format, Throwable t, Object... args) {
		LOGGER.error(format, args, t);
	}

	public static void debug(String format, Throwable t, Object... args) {
		LOGGER.debug(format, args, t);
	}

	public static void trace(String format, Throwable t, Object... args) {
		LOGGER.trace(format, args, t);
	}

	private static String getPrefix() {
		return "[" + NAME + "] ";
	}

	private static String getPrefix(LogLevel level) {
		return "[" + NAME + "] [" + level.name() + "] ";
	}
}