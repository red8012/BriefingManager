package BriefingManager;
import com.mongodb.*;

import java.util.List;

public class M {
	static MongoClient mongoClient;
	static DB db;
	static DBCollection collection;

	public static void connect(boolean ensureIndex) throws Exception {
		mongoClient = new MongoClient();
		db = mongoClient.getDB("bm");
		collection = db.getCollection("tw");
		if (ensureIndex) {
			collection.ensureIndex("securityCode");
			collection.ensureIndex("row");
			collection.ensureIndex("date");
		}
	}

	public static void disconnect() throws Exception {
		mongoClient.close();
	}

	public static void insert(String securityCode, int row, String date) throws Exception {
		BasicDBObject insert = new BasicDBObject("securityCode", securityCode)
				.append("row", row).append("date", date);
		WriteResult result = collection.insert(insert);
		if (result.getError() != null) throw new Exception(result.getError());
	}

	public static void insert(String securityCode, int row, String date, Double volume, Double open, Double high, Double low, Double close) throws Exception {
		BasicDBObject insert = new BasicDBObject("securityCode", securityCode)
				.append("row", row).append("date", date).append(C.volume, volume)
				.append(C.open, open).append(C.normalizedOpen, open)
				.append(C.high, high).append(C.normalizedHigh, high)
				.append(C.low, low).append(C.normalizedLow, low)
				.append(C.close, close).append(C.normalizedClose, close);
		WriteResult result = collection.insert(insert);
		if (result.getError() != null) throw new Exception(result.getError());
	}

	public static void set(String securityCode, int row, String column, Double value) throws Exception {
		BasicDBObject query = new BasicDBObject("securityCode", securityCode)
				.append("row", row);
		BasicDBObject update = new BasicDBObject("$set", new BasicDBObject(column, value));
		WriteResult result = collection.update(query, update, true, false);
		if (result.getError() != null) throw new Exception(result.getError());
	}

	public static void set(String securityCode, String date, String column, Double value) throws Exception {
		BasicDBObject query = new BasicDBObject("securityCode", securityCode)
				.append("date", date);
		BasicDBObject update = new BasicDBObject("$set", new BasicDBObject(column, value));
		WriteResult result = collection.update(query, update, true, false);
		if (result.getError() != null) throw new Exception(result.getError());
	}

	public static Double get(String securityCode, int row, String column) throws Exception {
		BasicDBObject query = new BasicDBObject("securityCode", securityCode)
				.append("row", row);
		DBObject result = collection.findOne(query);
		if (result == null) return null;
		else return (Double) result.get(column);
	}

	public static String getDate(String securityCode, int row) throws Exception {
		BasicDBObject query = new BasicDBObject("securityCode", securityCode)
				.append("row", row);
		DBObject result = collection.findOne(query);
		if (result == null) return null;
		else return (String) result.get("date");
	}

	public static Double get(String securityCode, String date, String column) throws Exception {
		BasicDBObject query = new BasicDBObject("securityCode", securityCode)
				.append("date", date);
		DBObject result = collection.findOne(query);
		if (result == null) return null;
		else return (Double) result.get(column);
	}

	public static Integer getRow(String securityCode, String date) throws Exception {
		BasicDBObject query = new BasicDBObject("securityCode", securityCode)
				.append("date", date);
		DBObject result = collection.findOne(query);
		if (result == null) return null;
		else return (Integer) result.get("row");
	}

	public static void print(String securityCode, String date) throws Exception {
		BasicDBObject query = new BasicDBObject("securityCode", securityCode)
				.append("date", date);
		DBCursor cursor = collection.find(query);
		if (!cursor.hasNext()) System.out.println("Nothing found of " + securityCode + " @ " + date);
		while (cursor.hasNext()) System.out.println(cursor.next());
	}

	public static List<String> listSecurities() throws Exception {
		return collection.distinct("securityCode");
	}

	public static int getRowCount(String securityCode) {
		BasicDBObject find = new BasicDBObject("securityCode", securityCode);
		BasicDBObject sort = new BasicDBObject("row", -1);
		DBCursor cursor = collection.find(find).sort(sort).limit(1);
		if (cursor.hasNext()) return ((Integer) cursor.next().get("row")) + 1;
		else return 0;
	}

	public static void checkRowDateConsistency() {
		System.out.print("Checking consistency... ");

		// get most recent date
		BasicDBObject find = new BasicDBObject("securityCode", "2330");
		BasicDBObject sort = new BasicDBObject("date", -1);
		DBCursor cursor = collection.find(find).sort(sort).limit(1);
		System.out.println(cursor.next().get("date"));

		// find distinct code
		List<String> list = collection.distinct("securityCode");
		for (String s : list) {
			find = new BasicDBObject("securityCode", s);
			sort = new BasicDBObject("date", 1);
			cursor = collection.find(find).sort(sort);
			int i = 0;
			while (cursor.hasNext()) {
				DBObject next = cursor.next();
				if (((Integer) next.get("row")) != i++)
					System.out.println(s + ": " + next.get("row") + "\t" + next.get("date"));
			}
		}
		System.out.println("OK");
	}
}
