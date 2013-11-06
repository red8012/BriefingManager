package PriceNormalizer;
public class Pair<type1, type2> {
	type1 key;
	type2 value;

	public Pair(type1 key, type2 value) {
		this.key = key;
		this.value = value;
	}

	type1 getKey() {
		return key;
	}

	type2 getValue() {
		return value;
	}
}
