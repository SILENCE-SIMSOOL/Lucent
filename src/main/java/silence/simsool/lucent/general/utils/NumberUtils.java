package silence.simsool.lucent.general.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.TreeMap;
import java.util.Map.Entry;

public class NumberUtils {

	public static final NumberFormat nf = NumberFormat.getInstance(Locale.US);
	private static final TreeMap<Long, String> suffixes = new TreeMap<>();
	private static final TreeMap<Integer, String> romanSymbols = new TreeMap<>();
	private static final NumberFormat INT_FMT = NumberFormat.getIntegerInstance(Locale.US);
	private static final DecimalFormat DEC_FMT = new DecimalFormat("#,###.##");

	static {
		suffixes.put(1000L, "k");
		suffixes.put(1000000L, "M");
		suffixes.put(1000000000L, "B");
		suffixes.put(1000000000000L, "T");
		suffixes.put(1000000000000000L, "P");
		suffixes.put(1000000000000000000L, "E");
		romanSymbols.put(1000, "M");
		romanSymbols.put(900, "CM");
		romanSymbols.put(500, "D");
		romanSymbols.put(400, "CD");
		romanSymbols.put(100, "C");
		romanSymbols.put(90, "XC");
		romanSymbols.put(50, "L");
		romanSymbols.put(40, "XL");
		romanSymbols.put(10, "X");
		romanSymbols.put(9, "IX");
		romanSymbols.put(5, "V");
		romanSymbols.put(4, "IV");
		romanSymbols.put(1, "I");
	}

	/**
	 * Formats a Number to a comma-separated string based on its type.
	 * If it has no significant decimals, it formats as an integer.
	 * Example: formatNumber(12345.67) -> "12,345.67"
	 * Example: formatNumber(12345.00) -> "12,345"
	 */
	public static String formatNumber(Number n) {
		if (n == null) return "0";
		if (n instanceof Double || n instanceof Float) {
			double v = n.doubleValue();
			if (Math.abs(v - Math.rint(v)) < 1e-9) {
				return INT_FMT.format(Math.round(v));
			}
			return DEC_FMT.format(v);
		}
		return INT_FMT.format(n.longValue());
	}

	/**
	 * Rounds a double value to the nearest integer and formats with commas.
	 * Example: formatInt(1234.56) -> "1,235"
	 */
	public static String formatInt(double v) {
		return INT_FMT.format(Math.round(v));
	}

	/**
	 * Formats a large number with metric suffixes (k, M, B, T, etc.).
	 * Example: format(1234567) -> "1.2M"
	 * Example: format(1000) -> "1k"
	 */
	public static String format(Number value) {
		long val = value.longValue();
		if (val == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
		if (val < 0) return "-" + format(-val);
		if (val < 1000) return String.valueOf(val);

		Entry<Long, String> entry = suffixes.floorEntry(val);
		long divideBy = entry.getKey();
		String suffix = entry.getValue();

		long truncated = val / (divideBy / 10);
		boolean hasDecimal = truncated < 100 && truncated / 10.0 != truncated / 10;
		return hasDecimal ? (truncated / 10.0) + suffix : (truncated / 10) + suffix;
	}

	/**
	 * Converts a suffix-formatted string back to a long value.
	 * Example: unformat("1.2M") -> 1200000
	 */
	public static long unformat(String value) {
		String suffix = value.replaceAll("\\d", "").toLowerCase();
		long num = Long.parseLong(value.replaceAll("\\D", ""));
		for (Entry<Long, String> entry : suffixes.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(suffix)) return num * entry.getKey();
		}
		return num;
	}

	/**
	 * Rounds a double value to the specified number of decimal places.
	 * Example: round(123.456, 2) -> 123.46
	 */
	public static double round(double v, int places) {
		if (places < 0) throw new IllegalArgumentException();
		double scale = Math.pow(10, places);
		return Math.round(v * scale) / scale;
	}

	/**
	 * Rounds a float value to the specified number of decimal places.
	 * Example: round(123.456f, 1) -> 123.5f
	 */
	public static float round(float v, int places) {
		if (places < 0) throw new IllegalArgumentException();
		float scale = (float) Math.pow(10, places);
		return Math.round(v * scale) / scale;
	}

	/**
	 * Adds an ordinal suffix (st, nd, rd, th) to a number.
	 * Example: addSuffix(1) -> "1st"
	 * Example: addSuffix(22) -> "22nd"
	 */
	public static String addSuffix(Number value) {
		long val = value.longValue();
		if (val >= 11 && val <= 13) return val + "th";
		switch ((int) (val % 10)) {
			case 1:
				return val + "st";
			case 2:
				return val + "nd";
			case 3:
				return val + "rd";
			default:
				return val + "th";
		}
	}

	/**
	 * Converts a Roman numeral string to an integer. Returns the parsed integer if the string is numeric.
	 * Example: romanToInt("XIV") -> 14
	 */
	public static int romanToInt(String value) {
		if (value.matches("^[0-9]+$")) {
			return Integer.parseInt(value);
		}

		value = value.toUpperCase();
		int result = 0;

		for (int i = 0; i < value.length() - 1; i++) {
			int current = romanValue(value.charAt(i));
			int next = romanValue(value.charAt(i + 1));
			result += (current < next) ? -current : current;
		}

		return result + romanValue(value.charAt(value.length() - 1));
	}

	/**
	 * Helper method to map individual Roman characters to their integer values.
	 */
	private static int romanValue(char c) {
		switch (c) {
			case 'I': return 1;
			case 'V': return 5;
			case 'X': return 10;
			case 'L': return 50;
			case 'C': return 100;
			case 'D': return 500;
			case 'M': return 1000;
			default: return 0;
		}
	}

	/**
	 * Converts an integer to a Roman numeral string.
	 * Example: toRoman(9) -> "IX"
	 */
	public static String toRoman(int value) {
		if (value <= 0) throw new IllegalArgumentException(value + " must be positive!");
		int l = romanSymbols.floorKey(value);
		if (value == l) return romanSymbols.get(value);
		return romanSymbols.get(l) + toRoman(value - l);
	}

	/**
	 * Converts Minecraft server ticks into a formatted minute and second string.
	 * Example: getServerTickTimerDefaultFormat(1200) -> "1m 0.0s"
	 */
	public static String getServerTickTimerDefaultFormat(int timer) {
		double totalSeconds = timer / 20.0;
		int minutes = (int) (totalSeconds / 60);
		double seconds = totalSeconds - (minutes * 60);
		seconds = Math.round(seconds * 100.0) / 100.0;
		return minutes + "m " + seconds + "s";
	}

	/**
	 * Converts Minecraft server ticks into a formatted seconds-only string.
	 * Example: getServerTickTimerMiniFormat(300) -> "15.0s"
	 */
	public static String getServerTickTimerMiniFormat(int timer) {
		double seconds = timer / 20.0;
		seconds = Math.round(seconds * 100.0) / 100.0;
		return seconds + "s";
	}

	/**
	 * Formats a Number with US-style commas.
	 * Example: formatWithComma(1000000) -> "1,000,000"
	 */
	public static String formatWithComma(Number value) {
		return nf.format(value);
	}

	/**
	 * Strips existing commas from a numeric string, parses it, and re-formats it with commas.
	 * Example: formatWithComma("1234,567") -> "1,234,567"
	 */
	public static String formatWithComma(String value) {
		try {
			long num = Long.parseLong(value.replaceAll(",", ""));
			return nf.format(num);
		} catch (NumberFormatException e) {
			return value;
		}
	}

	/**
	 * Validates whether a given string is a valid Roman numeral.
	 * Example: isValidRoman("IIII") -> false
	 * Example: isValidRoman("IV") -> true
	 */
	public static boolean isValidRoman(String s) {
		try {
			int val = romanToInt(s);
			return toRoman(val).equals(s);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Formats a double value to a specified number of decimal digits (with rounding).
	 * Example: fmtDouble(123.456, 1) -> "123.5"
	 */
	public static String fmtDouble(double v, int fractionDigits) {
		return String.format("%." + fractionDigits + "f", v);
	}

	/**
	 * Formats a double value to 1 decimal place (with rounding).
	 * Example: fmtDouble(123.456) -> "123.5"
	 */
	public static String fmtDouble(double v) {
		return String.format("%.1f", v);
	}

	/**
	 * Formats a double value to 2 decimal places (with rounding).
	 * Example: fmtDouble2(123.456) -> "123.46"
	 */
	public static String fmtDouble2(double v) {
		return String.format("%.2f", v);
	}

	/**
	 * Converts milliseconds into a minute and second string format (rounded seconds).
	 * Example: fmtMsToMinSec(65432) -> "1m 5s"
	 */
	public static String fmtMsToMinSec(double ms) {
		long s = Math.round(ms / 1000.0);
		long m = s / 60;
		long r = s % 60;
		return m + "m " + r + "s";
	}

	/**
	 * Converts milliseconds into a hours, minutes, and seconds format. Hides hours if it is 0.
	 * Example: fmtMsToTime(3665000) -> "1h 1m 5s"
	 * Example: fmtMsToTime(65000) -> "1m 5s"
	 */
	public static String fmtMsToTime(long ms) {
		long s = Math.round(ms / 1000.0);
		long h = s / 3600;
		long m = (s % 3600) / 60;
		long r = s % 60;

		if (h > 0) {
			return h + "h " + m + "m " + r + "s";
		}
		return m + "m " + r + "s";
	}

	/**
	 * Converts a ratio value into a percentage string formatted to 1 decimal place.
	 * Example: fmtPercent(0.1234) -> "12.3%"
	 */
	public static String fmtPercent(double v) {
		return String.format("%.1f%%", v * 100);
	}

	/**
	 * Converts a ratio value into a percentage string formatted to 2 decimal places.
	 * Example: fmtPercent2(0.1234) -> "12.34%"
	 */
	public static String fmtPercent2(double v) {
		return String.format("%.2f%%", v * 100);
	}

	/**
	 * Formats a long integer with 3-digit comma separators.
	 * Example: fmtComma(1234567) -> "1,234,567"
	 */
	public static String fmtComma(long v) {
		return String.format("%,d", v);
	}

	/**
	 * Formats a double value with 3-digit comma separators and 1 decimal place.
	 * Example: fmtComma(1234567.891) -> "1,234,567.9"
	 */
	public static String fmtComma(double v) {
		return String.format("%,.1f", v);
	}

	/**
	 * Formats a double value with 3-digit comma separators and 2 decimal places.
	 * Example: fmtComma2(1234567.891) -> "1,234,567.89"
	 */
	public static String fmtComma2(double v) {
		return String.format("%,.2f", v);
	}

	/**
	 * Formats a double value with metric suffixes (k, M, B) or commas if less than 1000.
	 * Example: fmtNum(1234567.8) -> "1.2M"
	 * Example: fmtNum(500.5) -> "500"
	 */
	public static String fmtNum(double v) {
		double a = Math.abs(v);
		if (a >= 1_000_000_000) return String.format("%.1fB", v/1_000_000_000d);
		if (a >= 1_000_000) return String.format("%.1fM", v/1_000_000d);
		if (a >= 1_000) return String.format("%.1fk", v/1_000d);
		return new DecimalFormat("#,###").format((long)v);
	}

}