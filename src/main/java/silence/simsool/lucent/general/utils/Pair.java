package silence.simsool.lucent.general.utils;

import java.util.Objects;

public class Pair<K, V> {

	private final K key;
	private final V value;

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public K getFirst() {
		return key;
	}

	public V getSecond() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Pair<?, ?> other)) return false;
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	@Override
	public String toString() {
		return "(" + key + ", " + value + ")";
	}

}