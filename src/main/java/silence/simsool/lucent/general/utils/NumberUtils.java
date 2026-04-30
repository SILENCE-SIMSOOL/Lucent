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

	public static String formatInt(double v) {
		return INT_FMT.format(Math.round(v));
	}

	public static String formatNumber(long number) {
		return String.format("%,d", number);
	}

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

	public static long unformat(String value) {
		String suffix = value.replaceAll("\\d", "").toLowerCase();
		long num = Long.parseLong(value.replaceAll("\\D", ""));
		for (Entry<Long, String> entry : suffixes.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(suffix)) return num * entry.getKey();
		}
		return num;
	}

	public static double roundToPrecision(double value, int precision) {
		double scale = Math.pow(10, precision);
		return Math.round(value * scale) / scale;
	}

	public static float roundToPrecision(float value, int precision) {
		float scale = (float) Math.pow(10, precision);
		return Math.round(value * scale) / scale;
	}

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

	public static String toRoman(int value) {
		if (value <= 0) throw new IllegalArgumentException(value + " must be positive!");
		int l = romanSymbols.floorKey(value);
		if (value == l) return romanSymbols.get(value);
		return romanSymbols.get(l) + toRoman(value - l);
	}

//	private static int processDecimal(int decimal, int lastNumber, int lastDecimal) {
//		return lastNumber > decimal ? lastDecimal - decimal : lastDecimal + decimal;
//	}

	public static String getServerTickTimerDefaultFormat(int timer) {
		double totalSeconds = timer / 20.0;
		int minutes = (int) (totalSeconds / 60);
		double seconds = totalSeconds - (minutes * 60);
		seconds = Math.round(seconds * 100.0) / 100.0;
		return minutes + "m " + seconds + "s";
	}

	public static String getServerTickTimerMiniFormat(int timer) {
		double seconds = timer / 20.0;
		seconds = Math.round(seconds * 100.0) / 100.0;
		return seconds + "s";
	}

	public static String formatWithComma(Number value) {
		return nf.format(value);
	}

	public static String formatWithComma(String value) {
		try {
			long num = Long.parseLong(value.replaceAll(",", ""));
			return nf.format(num);
		} catch (NumberFormatException e) {
			return value;
		}
	}

	public static boolean isValidRoman(String s) {
		try {
			int val = romanToInt(s);
			return toRoman(val).equals(s);
		} catch (Exception e) {
			return false;
		}
	}

}