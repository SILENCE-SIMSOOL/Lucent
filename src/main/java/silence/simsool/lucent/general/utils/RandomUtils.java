package silence.simsool.lucent.general.utils;

import java.util.List;
import java.util.Random;

public class RandomUtils {

	private static final Random RANDOM = new Random();

	/**
	 * Returns a random integer between the specified min and max values (inclusive).
	 *
	 * @param min the minimum bound (inclusive)
	 * @param max the maximum bound (inclusive)
	 * @return a random integer between min and max
	 * @throws IllegalArgumentException if min is greater than max
	 */
	public static int range(int min, int max) {
		if (min > max) throw new IllegalArgumentException("min cannot be greater than max.");
		return RANDOM.nextInt((max - min) + 1) + min;
	}

	/**
	 * Returns a boolean value based on the given percentage probability (0 to 100).
	 *
	 * @param percent the probability percentage between 0 and 100
	 * @return true with the specified percentage probability, false otherwise
	 * @throws IllegalArgumentException if percent is not between 0 and 100
	 */
	public static boolean percentage(int percent) {
		if (percent < 0 || percent > 100) throw new IllegalArgumentException("Percentage must be between 0 and 100.");
		if (percent == 0) return false;
		if (percent == 100) return true;
		return RANDOM.nextInt(100) < percent;
	}

	/**
	 * Returns a boolean value with a 50:50 chance (coin flip).
	 *
	 * @return true or false randomly with equal probability
	 */
	public static boolean coinFlip() {
		return RANDOM.nextBoolean();
	}

	/**
	 * Returns a randomly selected element from the given list.
	 *
	 * @param <T>  the type of elements in the list
	 * @param list the list to select an element from
	 * @return a randomly selected element
	 * @throws IllegalArgumentException if the list is null or empty
	 */
	public static <T> T randomElement(List<T> list) {
		if (list == null || list.isEmpty()) throw new IllegalArgumentException("List cannot be null or empty.");
		return list.get(RANDOM.nextInt(list.size()));
	}

	/**
	 * Returns a randomly selected element from the given array.
	 *
	 * @param <T>   the type of elements in the array
	 * @param array the array to select an element from
	 * @return a randomly selected element
	 * @throws IllegalArgumentException if the array is null or empty
	 */
	public static <T> T randomElement(T[] array) {
		if (array == null || array.length == 0) throw new IllegalArgumentException("Array cannot be null or empty.");
		return array[RANDOM.nextInt(array.length)];
	}

}