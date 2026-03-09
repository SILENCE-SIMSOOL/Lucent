package silence.simsool.lucent.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import silence.simsool.lucent.general.enums.LogLevel;

public class ULog {
	private final String name;
	private final Logger logger;

	public ULog(String name) {
		this.name = name;
		this.logger = LoggerFactory.getLogger(name);
	}

	public Logger getLogger() {
		return logger;
	}

	public static void print(String message) {
		System.out.println(message);
	}

	public void printc(String message) {
		print(getPrefix() + message);
	}

	public void custom(String message, LogLevel level) {
		print(getPrefix(level) + message);
	}

	public void log(String message, LogLevel level) {
		switch (level) {
			case INFO -> info(message);
			case WARN -> warn(message);
			case ERROR -> error(message);
			case DEBUG -> debug(message);
			case TRACE -> trace(message);
		}
	}

	public void info(String message) {
		logger.info(message);
	}

	public void warn(String message) {
		logger.warn(message);
	}

	public void error(String message) {
		logger.error(message);
	}

	public void debug(String message) {
		logger.debug(message);
	}

	public void trace(String message) {
		logger.trace(message);
	}

	public void info(String message, Throwable t) {
		logger.info(message, t);
	}

	public void warn(String message, Throwable t) {
		logger.warn(message, t);
	}

	public void error(String message, Throwable t) {
		logger.error(message, t);
	}

	public void debug(String message, Throwable t) {
		logger.debug(message, t);
	}

	public void trace(String message, Throwable t) {
		logger.trace(message, t);
	}

	public void info(String format, Object... args) {
		logger.info(format, args);
	}

	public void warn(String format, Object... args) {
		logger.warn(format, args);
	}

	public void error(String format, Object... args) {
		logger.error(format, args);
	}

	public void debug(String format, Object... args) {
		logger.debug(format, args);
	}

	public void trace(String format, Object... args) {
		logger.trace(format, args);
	}

	public void info(String format, Throwable t, Object... args) {
		logger.info(format, args, t);
	}

	public void warn(String format, Throwable t, Object... args) {
		logger.warn(format, args, t);
	}

	public void error(String format, Throwable t, Object... args) {
		logger.error(format, args, t);
	}

	public void debug(String format, Throwable t, Object... args) {
		logger.debug(format, args, t);
	}

	public void trace(String format, Throwable t, Object... args) {
		logger.trace(format, args, t);
	}

	private String getPrefix() {
		return "[" + name + "] ";
	}

	private String getPrefix(LogLevel level) {
		return "[" + name + "] [" + level.name() + "] ";
	}
}