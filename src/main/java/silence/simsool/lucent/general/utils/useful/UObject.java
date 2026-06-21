package silence.simsool.lucent.general.utils.useful;

import java.util.Optional;
import java.util.function.Supplier;

public class UObject {

	/**
	 * Checks if the value matches any of the provided options.
	 *
	 * @param value   The value to check
	 * @param options The options to compare against
	 * @return true if matches any; false otherwise
	 */
	public static boolean equalsOneOf(Object value, Object... options) {
		for (Object opt : options) {
			if (value == null ? opt == null : value.equals(opt)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the value is null.
	 *
	 * @param value The object to check
	 * @return true if null; false otherwise
	 */
	public static boolean isNull(Object value) {
		return value == null;
	}

	/**
	 * Checks if the value is not null.
	 *
	 * @param value The object to check
	 * @return true if not null; false otherwise
	 */
	public static boolean isNotNull(Object value) {
		return value != null;
	}

	/**
	 * Returns the default value if the provided value is null.
	 *
	 * @param value        The value to check
	 * @param defaultValue The value to return if null
	 * @param <T>          The type of the value
	 * @return value if not null; defaultValue otherwise
	 */
	public static <T> T defaultIfNull(T value, T defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
	 * Returns a value from the supplier if the provided value is null (lazy evaluation).
	 *
	 * @param value           The value to check
	 * @param defaultSupplier The supplier to provide a value if null
	 * @param <T>             The type of the value
	 * @return value if not null; supplied value otherwise
	 */
	public static <T> T defaultIfNull(T value, Supplier<T> defaultSupplier) {
		return value != null ? value : defaultSupplier.get();
	}

	/**
	 * Converts the value to an Optional.
	 *
	 * @param value The value to wrap
	 * @param <T>   The type of the value
	 * @return An Optional containing the value, or empty if null
	 */
	public static <T> Optional<T> toOptional(T value) {
		return Optional.ofNullable(value);
	}

	/**
	 * Safely casts the value to the specified type, returning null if the cast fails.
	 *
	 * @param value The object to cast
	 * @param type  The target class type
	 * @param <T>   The target type
	 * @return The casted object, or null if type mismatch
	 */
	@SuppressWarnings("unchecked")
	public static <T> T safeCast(Object value, Class<T> type) {
		return type.isInstance(value) ? (T) value : null;
	}

	/**
	 * Safely casts the value to the specified type, returning a default value if the cast fails.
	 *
	 * @param value        The object to cast
	 * @param type         The target class type
	 * @param defaultValue The value to return if cast fails
	 * @param <T>          The target type
	 * @return The casted object, or defaultValue if type mismatch
	 */
	@SuppressWarnings("unchecked")
	public static <T> T safeCast(Object value, Class<T> type, T defaultValue) {
		return type.isInstance(value) ? (T) value : defaultValue;
	}

	/**
	 * Null-safe equality check between two objects.
	 *
	 * @param a First object
	 * @param b Second object
	 * @return true if equal; false otherwise
	 */
	public static boolean equals(Object a, Object b) {
		return a == null ? b == null : a.equals(b);
	}

	/**
	 * Returns the first non-null value among the arguments (SQL COALESCE style).
	 *
	 * @param values The values to check
	 * @param <T>    The type of the values
	 * @return The first non-null value, or null if all are null
	 */
	@SafeVarargs
	public static <T> T coalesce(T... values) {
		for (T v : values) {
			if (v != null) return v;
		}
		return null;
	}

	/**
	 * Returns the string representation of the value, or a default string if null.
	 *
	 * @param value        The object to convert
	 * @param defaultValue The string to return if null
	 * @return value.toString() or defaultValue
	 */
	public static String toStringOrDefault(Object value, String defaultValue) {
		return value != null ? value.toString() : defaultValue;
	}

	/**
	 * Returns the string representation of the value, or an empty string if null.
	 *
	 * @param value The object to convert
	 * @return value.toString() or empty string
	 */
	public static String toStringSafe(Object value) {
		return value != null ? value.toString() : "";
	}

	/**
	 * Performs a shallow identity comparison (==).
	 *
	 * @param a First object
	 * @param b Second object
	 * @return true if the same instance; false otherwise
	 */
	public static boolean isSame(Object a, Object b) {
		return a == b;
	}

	/**
	 * Returns the hash code of the value, or 0 if null.
	 *
	 * @param value The object to get hash code from
	 * @return hashCode or 0
	 */
	public static int hashCodeSafe(Object value) {
		return value != null ? value.hashCode() : 0;
	}

}