package BriefingManager;
import Grab.Grab;
import PriceNormalizer.PriceNormalizer;

public class Main {
	public static void main(String[] args) throws Exception {
		M.connect(true);
		if (args[0].equals("rebuild")) rebuild("2013 07 01 2013 07 02".split(" "));
		else if (args[0].equals("update")) update();
//		else System.out.println("hello world");
//		System.out.println(M.get("0000", "2013-11-01", "return"));

		M.print("0000", "2013-07-01");
		M.print("0000", "2013-07-02");
		M.print("0000", "2013-07-03");
		M.print("0000", "2013-07-04");
		M.print("2330", "2013-07-01");
		M.print("2330", "2013-07-02");
		M.print("2330", "2013-07-03");
		M.print("2330", "2013-07-04");
		M.disconnect();
	}

	static void rebuild(String[] dates) throws Exception {
		System.out.print("Dropping table... ");
		M.collection.drop();
		M.collection = M.db.getCollection("tw");

		System.out.print("finished!\nDownloading data... ");
		Grab.determineGrabRange(dates);
		Grab.startWorking(false);
//		M.checkRowDateConsistency();

		M.collection.ensureIndex("securityCode");
		M.collection.ensureIndex("row");
		M.collection.ensureIndex("date");

		PriceNormalizer.start(false);

		System.out.print("All finished");
	}

	static void update() throws Exception {
		Grab.setUpdateRangeToday();
		Grab.startWorking(true);
//		M.checkRowDateConsistency();
		Grab.setUpdateRangeToday();
		PriceNormalizer.start(true);
	}
}
