package FeatureGenerator;
import java.util.ArrayList;
import java.util.HashMap;

public class DataSet {
	HashMap<String, ArrayList> map = new HashMap<String, ArrayList>();

	void addNewColumn(String columnName, ArrayList emptyList) {
		map.put(columnName, emptyList);
	}

	void set(String column, int index, String value) {
		map.get(column).set(index, value);
	}

	void set(String column, int index, Double value) {
		map.get(column).set(index, value);
	}

	void add(String column, Object value) {
		map.get(column).add(value);
	}

//	void add(String column, String value){
//		map.get(column).add(value);
//	}
//
//	void add(String column, Integer value){
//		map.get(column).add(value);
//	}
//
//	void add(String column, Float value){
//		map.get(column).add(value);
//	}

	String getStr(String column, int index) {
		return ((ArrayList<String>) map.get(column)).get(index);
	}

	Integer getInt(String column, int index) {
		return ((ArrayList<Integer>) map.get(column)).get(index);
	}

	Float getFlo(String column, int index) {
		return ((ArrayList<Float>) map.get(column)).get(index);
	}

	public double getDoubleChecked(String column, int index) throws NullPointerException {
		ArrayList a = map.get(column);
		if (a == null) throw new NullPointerException("ArrayList is null");
		if (a.isEmpty()) throw new NullPointerException("ArrayList is empty");
		ArrayList<Double> ad = a;
		Double d;
		try {
			d= ad.get(index);
		} catch (Exception e) {
			throw new NullPointerException("Array out of bound");
		}
		if (d == null) throw new NullPointerException("Item is null");
		if (d == Float.NaN) throw new NullPointerException("Item is NaN");
		return d;
	}

	ArrayList get(String column) {
		return map.get(column);
	}

	int getLength() {
		return map.get("date").size();
	}
}

